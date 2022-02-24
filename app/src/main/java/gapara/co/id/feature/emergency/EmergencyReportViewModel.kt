package gapara.co.id.feature.emergency

import android.content.Intent
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.UploadRequest
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.api.response.EmergencyResponse
import gapara.co.id.core.api.response.MediaResponse
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class EmergencyReportViewModel : BaseViewModel() , CoroutineDeclare {

    val emergencyResponse = mutableLiveDataOf<ArrayList<EmergencyModel>>()
    val emergencyModel = mutableLiveDataOf<EmergencyModel>()
    val dateToday = mutableLiveDataOf<String>()
    val emergencyTitle = mutableLiveDataOf<String>()
    val emergencyDescription = mutableLiveDataOf<String>()
    val createEmergencyApiResponse = mutableLiveDataOf<BaseResponse>()
    var emergencyListPage = 1
    var imagesUploadedId = ArrayList<String>()
    val mediaResponse = mutableLiveDataOf<MediaResponse>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        emergencyModel.value = intent?.getParcelableExtra(EXTRA_EMERGENCY_MODEL)
    }

    fun getEmergencyList(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(getEmergencyUrl())
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["start_date"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["end_date"] = this
            }
            request.queries["page"] = "$emergencyListPage"
            val response= request.get<EmergencyResponse>()
            if (response?.isSuccess() == true) {
                emergencyResponse.value = response.data?.reports?.items
            }
            if (response?.data?.reports?.items.isNullOrEmpty()) {
                showToast("No data")
            } else {
                emergencyListPage += 1
            }
            showLoading(false)
        }
    }

    private fun getEmergencyUrl(): String {
        if (isChief()) {
            return Urls.GET_CHIEF_EMERGENCY_LIST
        } else if (isSupervisor()) {
            return Urls.GET_SUPERVISOR_EMERGENCY_LIST
        } else if (isGuard()) {
            return Urls.GET_PATROL_EMERGENCY
        } else {
            return Urls.GET_CLIENT_EMERGENCY_LIST
        }
    }

    fun onCreateEmergency() {
        if (emergencyTitle.value.isNullOrBlank()) {
            showToast("Title cannot empty")
            return
        }

        if (emergencyDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()
        launch {
            val model = PostModel(emergencyTitle.value, emergencyDescription.value, imagesUploadedId)
            val response = PostRequest<PostModel>(checkCreateEmergencyUrl()).post<BaseResponse>(model)
            createEmergencyApiResponse.value = response
            showLoading(false)
        }
    }

    private fun checkCreateEmergencyUrl(): String {
        return if (isClient()) {
            Urls.POST_CLIENT_CREATE_EMERGENCY
        } else if(isGuard()) {
            Urls.POST_PATROL_CREATE_EMERGENCY
        } else if(isSupervisor()) {
            Urls.POST_SUPERVISOR_CREATE_EMERGENCY
        } else { "" }
    }

    fun uploadImage(file: File) {
        showLoading()
        launch {
            val request = UploadRequest(getUploadImageUrl())
            val files = HashMap<String, File>()
            files["image"] = file
            val names = HashMap<String, String>()
            val response= request.upload<MediaResponse>(files, names)
            mediaResponse.value = response
            showLoading(false)
        }
    }

    private fun getUploadImageUrl(): String {
        return if (isClient()) {
            Urls.UPLOAD_CLIENT_IMAGE_CREATE_EMERGENCY
        } else if (isGuard()) {
            Urls.UPLOAD_PATROL_IMAGE_CREATE_EMERGENCY
        } else if (isSupervisor()) {
            Urls.UPLOAD_SUPERVISOR_IMAGE_CREATE_EMERGENCY
        } else { "" }
    }

    companion object {
        const val EXTRA_EMERGENCY_MODEL = "extra_emergency_model"
    }
}