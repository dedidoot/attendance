package gapara.co.id.feature.announcement

import android.content.Intent
import gapara.co.id.core.api.ApiRequest
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.BaseViewModel
import gapara.co.id.core.base.TimeHelper
import gapara.co.id.core.base.mutableLiveDataOf
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AnnouncementViewModel : BaseViewModel() , CoroutineDeclare {

    val announcementModel = mutableLiveDataOf<AnnouncementObjectModel>()
    val announcementItem = mutableLiveDataOf<AnnouncementItemModel>()
    val createAnnouncementApiResponse = mutableLiveDataOf<BaseResponse>()
    val acceptAnnouncementApiResponse = mutableLiveDataOf<BaseResponse>()
    val announcementTitle = mutableLiveDataOf<String>()
    val announcementDescription = mutableLiveDataOf<String>()
    val scheduleModel = mutableLiveDataOf<ScheduleShiftModel>()
    val dateToday = mutableLiveDataOf<String>()
    private val shiftsId = ArrayList<String>()

    var isCanAccept = mutableLiveDataOf<Boolean>()

    val announcementReadResponse = mutableLiveDataOf<BaseResponse>()

    private var schedules = ArrayList<String>()
    var currentScheduleResponse = mutableLiveDataOf<CurrentScheduleResponse>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        announcementItem.value = intent?.getParcelableExtra(EXTRA_ANNOUNCEMENT_MODEL)

        announcementItem.value?.schedules?.find { it.id == BaseApplication.sessionManager?.scheduleId }?.apply {
            isCanAccept.value = announcement_read_at.isNullOrBlank()
        }

    }

    fun getAnnouncement(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_ANNOUNCEMENT_LIST)
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["startDate"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["endDate"] = this
            }
            val response= request.get<AnnouncementResponse>()
            if (response?.isSuccess() == true) {
                announcementModel.value = response.data?.announcement
            }
            if (response?.data?.announcement?.items.isNullOrEmpty()) {
                showToast("No data")
            }
            showLoading(false)
        }
    }

    fun getScheduleDate(date : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_SCHEDULE_DATE)
            date.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["date"] = this
            }
            val response= request.get<ScheduleResponse>()
            scheduleModel.value = response?.data?.schedule
            showLoading(false)
        }
    }

    fun updateShiftId(id: String?) {
        shiftsId.clear()
        if (id?.equals(ALL_SHIFT_ID, ignoreCase = true) == true) {
            scheduleModel.value?.shifts?.forEach {
                shiftsId.add(it.id ?: "")
            }
        } else {
            shiftsId.add(id ?: "")
        }
    }

    fun onCreateAnnouncement() {
        if (announcementTitle.value.isNullOrBlank()) {
            showToast("Title cannot empty")
            return
        }

        if (announcementDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()
        launch {
            val model = PostAnnouncementModel(announcementTitle.value, announcementDescription.value, schedules, "high")
            val response = PostRequest<PostAnnouncementModel>(Urls.POST_ANNOUNCEMENT_CREATE).post<BaseResponse>(model)
            createAnnouncementApiResponse.value = response
            showLoading(false)
        }
    }

    fun getAnnouncementAccept() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.GET_SUPERVISOR_ANNOUNCEMENT_ACCEPT+"/${announcementItem.value?.id}")
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            acceptAnnouncementApiResponse.value = response
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

    fun patchAnnouncementRead() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_ANNOUNCEMENT_READ)
            apiRequest.queries["id"] = announcementItem.value?.id ?: ""
            announcementItem.value?.schedules?.find { it.id == BaseApplication.sessionManager?.scheduleId }?.apply {
                apiRequest.queries["schedule_id"] = this.id ?: ""
            }
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            announcementReadResponse.value = response
            showLoading(false)
        }
    }

    companion object {
        const val ALL_SHIFT_ID = "all_shift_id"
        const val EXTRA_ANNOUNCEMENT_MODEL = "extra_announcement_model"
    }
}