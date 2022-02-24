package gapara.co.id.feature.checkpoint

import android.content.Intent
import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class CheckPointViewModel : BaseViewModel() , CoroutineDeclare {

    val reportResponse = mutableLiveDataOf<ArrayList<ReportModel>>()
    val reportDetail = mutableLiveDataOf<ReportModel>()
    val checkPointResponse = mutableLiveDataOf<CheckPointV2Entity>()
    val uploadLocationResponse = mutableLiveDataOf<BaseResponse>()
    val makeCheckPointResponse = mutableLiveDataOf<BaseResponse>()
    val createCheckPointApiResponse = mutableLiveDataOf<BaseResponse>()
    val mediaResponse = mutableLiveDataOf<Media2Response>()
    val scheduleShiftCheckPointId = mutableLiveDataOf<String>()
    val dateToday = mutableLiveDataOf<String>()
    val checkPointTitle = mutableLiveDataOf<String>()
    val checkPointDescription = mutableLiveDataOf<String>()
    var page = 1
    var imagesUploadedId = ArrayList<String>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        reportDetail.value = intent?.getParcelableExtra(EXTRA_CHECK_POINT_DETAIL)
    }

    fun getCheckPointList(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(getCheckPointUrl())
            request.queries["page"] = "$page"
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["start_date"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["end_date"] = this
            }

            val response= request.get<ReportDataResponse>()
            if (response?.isSuccess() == true) {
                reportResponse.value = response.data?.reports?.items
            }
            if (response?.data?.reports?.items.isNullOrEmpty()) {
                showToast("No data")
            } else {
                page += 1
            }
            showLoading(false)
        }
    }

    private fun getCheckPointUrl(): String {
        return if (isGuard()) { Urls.GET_PATROL_REPORT_CHECK_POINT } else { Urls.GET_SUPERVISOR_CHECK_POINT_LIST }
    }

    fun putMakeCheckPoint() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(checkPutMakeCheckPointUrl())
            apiRequest.queries["schedule_shift_check_point_id"] = scheduleShiftCheckPointId.value ?: ""
            apiRequest.queries["latitude"] = "${BaseApplication.sessionManager?.latitude}"
            apiRequest.queries["longitude"] = "${BaseApplication.sessionManager?.longitude}"
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PUT_METHOD)
            makeCheckPointResponse.value = response
            showLoading(false)
        }
    }

    private fun checkPutMakeCheckPointUrl(): String {
        return if (isGuard()) { Urls.PUT_PATROL_LOCATION_MAKE_CHECK } else { Urls.PUT_SUPERVISOR_MAKE_CHECK_POINT }
    }

    fun getLocationCheckPointList() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_CHECK_POINT_LIST)
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            request.queries["schedule_id"] = BaseApplication.sessionManager?.scheduleId ?: ""
            val response = request.get<CheckPointListV2Response>()
            checkPointResponse.value = response?.data?.checkpoints
            if (response?.data?.checkpoints?.items.isNullOrEmpty()) {
                showToast(response?.message ?: "No data")
            }
            showLoading(false)
        }
    }

    fun onCreateCheckPointReport() {
        if (checkPointTitle.value.isNullOrBlank()) {
            showToast("Title cannot empty")
            return
        }

        if (checkPointDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()
        launch {
            val model = PostCreateLostReport(schedule_id = BaseApplication.sessionManager?.scheduleId,
            title = checkPointTitle.value,
            content = checkPointDescription.value,
            images = imagesUploadedId)

            val response = PostRequest<PostCreateLostReport>(Urls.POST_CHECK_POINT_CREATE).post<BaseResponse>(model)
            createCheckPointApiResponse.value = response
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

            names["description"] = "Report from check point"

            val response = request.upload<Media2Response>(files, names)
            mediaResponse.value = response
            showLoading(false)
        }
    }

    companion object {
        const val EXTRA_CHECK_POINT_DETAIL = "extra_check_point_detail"
    }
}