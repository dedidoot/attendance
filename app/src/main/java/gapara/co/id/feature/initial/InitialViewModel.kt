package gapara.co.id.feature.initial

import android.content.Intent
import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.api.response.ReportDataResponse
import gapara.co.id.core.api.response.ScheduleAvailableResponse
import gapara.co.id.core.api.response.ScheduleResponse
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InitialViewModel : BaseViewModel() , CoroutineDeclare {

    val reportResponse = mutableLiveDataOf<ArrayList<ReportModel>>()
    val scheduleAvailableResponse = mutableLiveDataOf<ArrayList<UserModel>>()
    val reportModel = mutableLiveDataOf<ReportModel>()
    val dateToday = mutableLiveDataOf<String>()
    val initialTitle = mutableLiveDataOf<String>()
    val initialDescription = mutableLiveDataOf<String>()
    val createInitialApiResponse = mutableLiveDataOf<BaseResponse>()
    val presentApiResponse = mutableLiveDataOf<BaseResponse>()
    val absentApiResponse = mutableLiveDataOf<BaseResponse>()
    val putAsPatrolApiResponse = mutableLiveDataOf<BaseResponse>()
    private val extraFrom = mutableLiveDataOf<String>()
    val userIdPresentAndAbsent = mutableLiveDataOf<String>()
    val presentLocationId = mutableLiveDataOf<String>()
    val scheduleListShiftModel = mutableLiveDataOf<ScheduleListShiftModel>()
    var initialListPage = 1

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        reportModel.value = intent?.getParcelableExtra(EXTRA_INITIAL_MODEL)
        extraFrom.value = intent?.getStringExtra(EXTRA_FROM)
    }

    fun getInitialList(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(getInitialListUrl())
            if (isChief() && isFromIntel()) {
                request.queries["type"] = "intel"
            } else if (isChief() && isFromInitial()) {
                request.queries["type"] = "initial"
            }
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["start_date"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["end_date"] = this
            }
            request.queries["page"] = "$initialListPage"
            val response= request.get<ReportDataResponse>()
            if (response?.isSuccess() == true) {
                reportResponse.value = response.data?.reports?.items
            }
            if (response?.data?.reports?.items.isNullOrEmpty()) {
                showToast("No data")
            } else {
                initialListPage += 1
            }
            showLoading(false)
        }
    }

    fun isFromIntel() : Boolean {
        return extraFrom.value?.equals(EXTRA_INTEL, ignoreCase = true) == true
    }

    fun isFromInitial() : Boolean {
        return extraFrom.value?.equals(EXTRA_INITIAL, ignoreCase = true) == true
    }

    private fun getInitialListUrl(): String {
        return if (isSupervisor()) { Urls.GET_SUPERVISOR_INITIAL_LIST } else if (isChief()) { Urls.GET_CHIEF_REPORT_LIST } else { Urls.GET_CHIEF_REPORT_LIST }
    }

    fun onCreateInitial() {
        if (initialTitle.value.isNullOrBlank()) {
            showToast("Title cannot empty")
            return
        }

        if (initialDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()
        launch {
            val model = PostModel(initialTitle.value, initialDescription.value)
            val response = PostRequest<PostModel>(Urls.POST_SUPERVISOR_CREATE_INITIAL).post<BaseResponse>(model)
            createInitialApiResponse.value = response
            showLoading(false)
        }
    }

    fun getScheduleDate() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_SUPERVISOR_SCHEDULE_DATE)
            dateToday.value.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["date"] = this
            }
            val response= request.get<ScheduleResponse>()
            if (response?.data?.schedule?.shifts.isNullOrEmpty()) {
                message.value = response?.message
            }

            val todayTime = Calendar.getInstance().time.time / 1000

            response?.data?.schedule?.shifts?.forEach {
                val serverFormat = SimpleDateFormat(TimeHelper.FORMAT_DATE_FULL, TimeHelper.defaultLocale)

                val startDate = serverFormat.parse(it.start).time / 1000
                val endDate = serverFormat.parse(it.end).time / 1000


                val isScheduleTime = todayTime > startDate && todayTime < endDate
                log("todayTime $todayTime startDate $startDate endDate $endDate isScheduleTime $isScheduleTime")

                if ((it.supervisor?.id == BaseApplication.sessionManager?.userId) && isScheduleTime) {
                    scheduleListShiftModel.value = it
                }
            }

            showLoading(false)
        }
    }

    fun getScheduleListAvailableUser() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_SCHEDULE_LIST_AVAILABLE_USER)
            request.queries["schedule_shift_id"] = scheduleListShiftModel.value?.id ?: ""
            val response= request.get<ScheduleAvailableResponse>()
            if (response?.data?.isNullOrEmpty() == true) {
                message.value = response.message
            }
            scheduleAvailableResponse.value = response?.data
            showLoading(false)
        }
    }

    fun postPresent(file: File) {
        showLoading()
        launch {
            val request = UploadRequest(Urls.POST_SCHEDULE_PRESENT)
            val files = HashMap<String, File>()
            files["image"] = file
            val names = HashMap<String, String>()
            names["schedule_shift_user_id"] = userIdPresentAndAbsent.value ?: ""
            val response= request.upload<BaseResponse>(files, names)
            presentApiResponse.value = response
            showLoading(false)
        }
    }

    fun postAbsent(photoFile: File, attachmentFile: File, replacementUserId : String, description : String, reasonCategory : String) {
        showLoading()
        launch {
            val request = UploadRequest(Urls.POST_SCHEDULE_ABSENT)
            val files = HashMap<String, File>()
            files["image"] = photoFile
            files["attachment"] = attachmentFile
            val names = HashMap<String, String>()
            names["schedule_shift_user_id"] = userIdPresentAndAbsent.value ?: ""
            names["reason"] = description
            names["reason_category"] = reasonCategory
            names["replacement_id"] = replacementUserId

            log("names $names")

            val response= request.upload<BaseResponse>(files, names)
            absentApiResponse.value = response
            showLoading(false)
        }
    }

    fun putAsPatrol() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PUT_SUPERVISOR_SCHEDULE_AS_PATROL)
            apiRequest.queries["schedule_shift_user_id"] = userIdPresentAndAbsent.value ?: ""
            apiRequest.queries["location_id"] = presentLocationId.value ?: ""
            putAsPatrolApiResponse.value = apiRequest.requesting<BaseResponse>(ApiRequest.PUT_METHOD)
            showLoading(false)
        }
    }

    companion object {
        const val EXTRA_FROM = "extra_from"
        const val EXTRA_INTEL = "extra_intel"
        const val EXTRA_INITIAL = "extra_initial"
        const val EXTRA_INITIAL_MODEL = "extra_initial_model"
    }
}