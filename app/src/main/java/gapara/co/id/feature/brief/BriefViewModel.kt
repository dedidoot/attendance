package gapara.co.id.feature.brief

import android.content.Intent
import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class BriefViewModel : BaseViewModel() , CoroutineDeclare {

    val briefModel = mutableLiveDataOf<BriefModel>()
    val scheduleModel = mutableLiveDataOf<ScheduleShiftModel>()
    val createBriefApiResponse = mutableLiveDataOf<BaseResponse>()
    val acceptBriefApiResponse = mutableLiveDataOf<BaseResponse>()
    val date = mutableLiveDataOf<String>()
    val briefItemModel = mutableLiveDataOf<BriefItemModel>()

    val briefText = mutableLiveDataOf<String>()
    val titleText = mutableLiveDataOf<String>()
    private var schedules = ArrayList<String>()
    var currentScheduleResponse = mutableLiveDataOf<CurrentScheduleResponse>()
    var currentItemScheduleResponse = mutableLiveDataOf<CurrentItemScheduleResponse>()
    var currentScheduleUsersResponse = mutableLiveDataOf<CurrentScheduleUsersResponse>()
    var schedule_id : String? =  ""

    fun processIntent(intent : Intent?) {
        briefItemModel.value = intent?.getParcelableExtra(EXTRA_BRIEF_ITEM_MODEL)

        if (isSupervisor()) {
        } else {
            date.value = intent?.getStringExtra(EXTRA_DATE)
        }

        patchReadBrief()
    }

    fun getBriefHistory(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(getBriefHistoryListUrl())
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["startDate"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["endDate"] = this
            }
            val response= request.get<BriefResponse>()
            briefModel.value = response?.data?.briefs
            if (response?.data?.briefs?.briefs?.isNullOrEmpty() == true) {
                showToast("No data")
            }

            showLoading(false)
        }
    }

    private fun getBriefHistoryListUrl(): String {
        return Urls.GET_BRIEF_LIST
    }

    var scheduleChooseDate = ""
    fun getScheduleDate(date : String? = null) {
        showLoading()
        scheduleChooseDate = date ?: ""
        launch {
            val request = GetRequest(Urls.GET_SCHEDULE_LIST)
            request.queries["my-schedule"] = "0" // all schedule
            date.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["startDate"] = this

                val timestampTomorrow =  System.currentTimeMillis() + (1000 * 60 * 60 * (24 * 1))
                val dateTomorrow = TimeHelper.getDateByTimestamp(timestampTomorrow)
                request.queries["endDate"] = this//TimeHelper.convertToFormatDateSever(dateTomorrow)
            }

            val response = request.get<CurrentItemScheduleResponse>()
            currentItemScheduleResponse.value = response
            showLoading(false)
        }
    }

    fun getScheduleUsers(schedule_id : String? = null) {
        this.schedule_id = schedule_id
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_SCHEDULE_USER)
            schedule_id.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["schedule_id"] = this
            }
            val response= request.get<CurrentScheduleUsersResponse>()

            currentItemScheduleResponse.value?.data?.schedules?.items?.find { it.id == schedule_id }?.apply {
                if (this.users.isNullOrEmpty()) {
                    this.users = arrayListOf()
                } else {
                    this.users?.clear()
                }
                this.users?.addAll(response?.data?.users ?: arrayListOf())
            }

            currentScheduleUsersResponse.value = response
            showLoading(false)
        }
    }

    fun onSaveBrief() {
        if (!isValidationPassed()) {
            return
        }
        showLoading()
        launch {
            val model = PostBriefModel(titleText.value, briefText.value, schedules)
            val response = PostRequest<PostBriefModel>(Urls.POST_CREATE_BRIEF).post<BaseResponse>(model)
            createBriefApiResponse.value = response
            showLoading(false)
        }
    }

    private fun isValidationPassed(): Boolean {
        if (briefText.value.isNullOrEmpty()) {
            showToast("Brief description cannot empty")
            return false
        }
        if (schedules.isNullOrEmpty()) {
            showToast("Shift cannot empty")
            return false
        }

        return true
    }

    fun getBriefAccept() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_ACCEPT_BRIEF+"/${briefItemModel.value?.id}")
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            acceptBriefApiResponse.value = response
            showLoading(false)
        }
    }

    fun getCurrentSchedule() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_CURRENT_SCHEDULE)
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            val response = request.get<CurrentScheduleResponse>()

            try {
                val tmpResponse = CurrentScheduleResponse(CurrentSchedulesModel(arrayListOf()))
                response?.data?.schedules?.forEach {
                    val serverFormat =
                        SimpleDateFormat(TimeHelper.FORMAT_DATE_SERVER, TimeHelper.defaultLocale)
                    val scheduleTimestamp = serverFormat.parse(it.scheduleDate?.date)?.time ?: 0
                    val nowTimestamp = System.currentTimeMillis()
                    val yesterdayTimestamp = (1000 * 60 * 60 * (24 * 1))
                    if (scheduleTimestamp >= (nowTimestamp - yesterdayTimestamp)) {
                        tmpResponse.data?.schedules?.add(it)
                    }
                }
                currentScheduleResponse.value = tmpResponse
            } catch (e: Exception) {
                currentScheduleResponse.value = response
            }

            showLoading(false)
        }
    }

    fun updateCurrentScheduleSelected(schedulesModel: ArrayList<GeneralModel>) {
        this.schedules.clear()
        schedulesModel.forEach {
            it.id?.apply {
                schedules.add(this)
            }
        }
    }

    fun patchReadBrief() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_BRIEF_READ)
            apiRequest.queries["id"] = briefItemModel.value?.id ?: ""
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            showLoading(false)
        }
    }

    fun getCurrentMySchedule() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_CURRENT_SCHEDULE)
            request.queries["my-schedule"] = "1"
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            currentScheduleResponse.value = request.get<CurrentScheduleResponse>()
            showLoading(false)
        }
    }

    companion object {
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_BRIEF_ITEM_MODEL = "extra_brief_item_model"
    }
}