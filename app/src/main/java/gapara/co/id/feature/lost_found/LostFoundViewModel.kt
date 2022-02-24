package gapara.co.id.feature.lost_found

import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.UploadRequest
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LostFoundViewModel : BaseViewModel(), CoroutineDeclare {

    val reportResponse = mutableLiveDataOf<ArrayList<SpecialReportModel>>()
    val reportModel = mutableLiveDataOf<SpecialReportModel>()
    val dateToday = mutableLiveDataOf<String>()
    val reportTitle = mutableLiveDataOf<String>()
    val reportDescription = mutableLiveDataOf<String>()
    val reportLocation = mutableLiveDataOf<String>()
    val typeName = mutableLiveDataOf<String>()
    val dateTime = mutableLiveDataOf<String>()
    val createReportApiResponse = mutableLiveDataOf<BaseResponse>()

    val mediaResponse = mutableLiveDataOf<Media2Response>()
    var imagesUploadedId = ArrayList<String>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun onCreateLostFoundReport() {
        if (reportTitle.value.isNullOrBlank()) {
            showToast("Title cannot empty")
            return
        }

        if (reportDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        val url = if (isTypeFound()) {
            Urls.POST_FINDING_CREATE
        } else {
            Urls.POST_LOST_AND_FOUND_CREATE
        }

        showLoading()
        launch {
            val model = PostCreateLostReport(
                schedule_id = BaseApplication.sessionManager?.scheduleId,
                title = reportTitle.value,
                content = reportDescription.value,
                images = imagesUploadedId,
                location = reportLocation.value,
                approximate_time = dateTime.value
            )
            val response = PostRequest<PostCreateLostReport>(url).post<BaseResponse>(model)
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

            names["section"] = if (isTypeFound()) {
                "proof"
            } else {
                "documentation"
            }

            names["description"] = typeName.value ?: ""

            val response = request.upload<Media2Response>(files, names)
            mediaResponse.value = response
            showLoading(false)
        }
    }

    fun updateType(name: String?) {
        typeName.value = name?.toLowerCase() ?: ""
    }

    fun updateDateTime(date: String?) {
        dateTime.value = date ?: ""
    }

    fun isTypeFound(): Boolean {
        return typeName.value?.equals("found", ignoreCase = true) == true
    }

    fun isTypeLost(): Boolean {
        return typeName.value?.equals("lost", ignoreCase = true) == true
    }
}