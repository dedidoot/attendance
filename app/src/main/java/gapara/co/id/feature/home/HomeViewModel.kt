package gapara.co.id.feature.home

import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel : BaseViewModel() , CoroutineDeclare {

    val announcementModel = mutableLiveDataOf<AnnouncementObjectModel>()
    val announcementUnread = mutableLiveDataOf<AnnouncementItemModel>()
    val briefItemModel = mutableLiveDataOf<ArrayList<BriefItemModel>>()
    val announcementAcceptResponse = mutableLiveDataOf<BaseResponse>()

    val findingListReport = mutableLiveDataOf<ArrayList<ReportModel>>()
    val incidentListReport = mutableLiveDataOf<IncidentItemModel>()
    val logoutModel = mutableLiveDataOf<BaseResponse>()
    val currentScheduleResponse = mutableLiveDataOf<CurrentScheduleResponse>()
    val permissionsResponse = mutableLiveDataOf<PermissionsResponse>()
    val intelReports = mutableLiveDataOf<IncidentDataResponse>()

    fun getFindingReport() {
        showLoading()
        launch {
            val reportResponse= GetRequest(Urls.GET_FINDING_LIST).get<EmergencyResponse>()
            if (reportResponse?.isSuccess() == true) {
                val items = ArrayList<ReportModel>()
                reportResponse.data?.reports?.items?.forEach {
                    items.add(ReportModel(id = it.id, title = it.title, content = it.content, creator = it.creator))
                }
                findingListReport.value = items
            }
            showLoading(false)
        }
    }

    fun getIncidentReport() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_INCIDENT_LIST)
            request.queries["page"] = "1"
            request.queries["status"] = "pending"
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            val incidentDataResponse= request.get<IncidentDataResponse>()
            if (incidentDataResponse?.isSuccess() == true) {
                incidentListReport.value = incidentDataResponse.data?.reports
            }
            showLoading(false)
        }
    }

    fun getBriefListToday() {
        showLoading()
        launch {
            val getRequest = GetRequest(Urls.GET_BRIEF_LIST)
            val todayDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
            getRequest.queries["startDate"] = todayDate
            getRequest.queries["endDate"] = todayDate
            val response= getRequest.get<BriefResponse>()
            briefItemModel.value = response?.data?.briefs?.briefs
            showLoading(false)
        }
    }

    fun postLogout() {
        showLoading()
        launch {
            val model = UserModel()
            val response = PostRequest<UserModel>(Urls.POST_LOGOUT).post<BaseResponse>(model)
            logoutModel.value = response
            showLoading(false)
        }
    }

    fun getCurrentSchedule(callback: () -> Unit) {
        launch {
            val request = GetRequest(Urls.GET_CURRENT_SCHEDULE)
            request.queries["actual"] = "1"
            request.queries["my-schedule"] = "1"
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            val response = request.get<CurrentScheduleResponse>()
            BaseApplication.sessionManager?.scheduleId = response?.data?.schedules?.firstOrNull()?.id
            currentScheduleResponse.value = response
            callback()
        }
    }

    fun getPermissions() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_GENERAL_PREMISSIONS)
            val response = request.get<PermissionsResponse>()
            showLoading(false)
            permissionsResponse.value = response
        }
    }

    fun getAnnouncement() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_ANNOUNCEMENT_LIST)
            val date = TimeHelper.convertToFormatDateSever(
                Calendar.getInstance().time,
                TimeHelper.FORMAT_DATE_SERVER
            )
            //request.queries["startDate"] = date
            //request.queries["endDate"] = date
            //request.queries["schedule_id"] = BaseApplication.sessionManager?.scheduleId ?: ""
            request.queries["unread"] = "true"
            val response= request.get<AnnouncementResponse>()
            if (response?.isSuccess() == true) {
                val tmp = AnnouncementItemModel()
                response.data?.announcement?.items?.forEach {
                    it.schedules?.forEach { schedule ->
                        schedule.users?.forEach { user ->
                            log("user.id ${user.user?.id} BaseApplication.sessionManager?.userId ${BaseApplication.sessionManager?.userId} schedule.announcement_read_at ${schedule.announcement_read_at}")
                            if (user.user?.id == BaseApplication.sessionManager?.userId
                                && schedule.announcement_read_at.isNullOrBlank()
                                && schedule.id == BaseApplication.sessionManager?.scheduleId) {

                                tmp.content = it.content
                                tmp.title = it.title
                                tmp.id = it.id
                                tmp.level = it.level
                                tmp.createdAt = it.createdAt
                                tmp.creator = it.creator
                                tmp.schedules = it.schedules
                            }
                        }
                    }
                }
                announcementUnread.value = tmp
            }
            showLoading(false)
        }
    }

    fun patchAnnouncementRead(id: String?) {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_ANNOUNCEMENT_READ)
            apiRequest.queries["id"] = id ?: ""
            apiRequest.queries["schedule_id"] = BaseApplication.sessionManager?.scheduleId ?: ""
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            announcementAcceptResponse.value = response
            showLoading(false)
        }
    }

    fun getListReportIntel() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_REPORT_INTEL_LIST)
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            val response= request.get<IncidentDataResponse>()
            if (response?.isSuccess() == true) {
                intelReports.value = response
            }
            showLoading(false)
        }
    }
}