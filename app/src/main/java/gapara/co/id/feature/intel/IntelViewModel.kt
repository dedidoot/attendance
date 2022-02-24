package gapara.co.id.feature.intel

import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.UploadRequest
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

// intel di chief
// chief buat brief bisa pilih schedule kedepannya


class IntelViewModel : BaseViewModel(), CoroutineDeclare {

    val dateToday = mutableLiveDataOf<String>()
    val reportTitle = mutableLiveDataOf<String>()
    val reportDescription = mutableLiveDataOf<String>()
    val dateTime = mutableLiveDataOf<String>()
    val scheduleId = mutableLiveDataOf<String>()
    val createReportApiResponse = mutableLiveDataOf<BaseResponse>()
    val currentScheduleResponse = mutableLiveDataOf<CurrentScheduleResponse>()
    val intelReports = mutableLiveDataOf<IncidentDataResponse>()

    val mediaResponse = mutableLiveDataOf<Media2Response>()
    var imagesUploadedId = ArrayList<String>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun onCreateIntelReport() {
        if (reportTitle.value.isNullOrBlank()) {
            showToast("Title cannot empty")
            return
        }

        if (reportDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()
        launch {
            val model = PostCreateLostReport(
                schedule_id = scheduleId.value,
                title = reportTitle.value,
                content = reportDescription.value,
                images = imagesUploadedId,
                approximate_time = dateTime.value
            )
            val response = PostRequest<PostCreateLostReport>( Urls.POST_INTEL_REPORT_CREATE).post<BaseResponse>(model)
            createReportApiResponse.value = response
            showLoading(false)
        }
    }

    fun uploadImage(file: File) {
        showLoading()
        launch {
            val request = UploadRequest(Urls.UPLOAD_IMAGE_REPORT)
            val files = HashMap<String, File>()
            files["image"] = file
            val names = HashMap<String, String>()
            names["section"] = "documentation"
            names["description"] = "Report From intel"

            val response = request.upload<Media2Response>(files, names)
            mediaResponse.value = response
            showLoading(false)
        }
    }

    fun updateDateTime(date: String?) {
        dateTime.value = date ?: ""
    }

    fun getCurrentSchedule() {
        launch {
            val request = GetRequest(Urls.GET_CURRENT_SCHEDULE)
            request.queries["open"] = "1"
            val response = request.get<CurrentScheduleResponse>()
            currentScheduleResponse.value = response
        }
    }

    fun updateScheduleId(id: String?) {
        scheduleId.value = id
    }

    fun getListReportIntel(startDate : String? = null, endDate : String? = null, ) {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_REPORT_INTEL_LIST)
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["startDate"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["endDate"] = this
            }
            val response= request.get<IncidentDataResponse>()
            if (response?.isSuccess() == true) {
                intelReports.value = response
            }
            showLoading(false)
        }
    }
}