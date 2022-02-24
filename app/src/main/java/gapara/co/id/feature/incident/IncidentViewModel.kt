package gapara.co.id.feature.incident

import android.content.Intent
import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.HashMap

class IncidentViewModel : BaseViewModel() , CoroutineDeclare {

    val incidentProgressResponse = mutableLiveDataOf<ArrayList<IncidentModel>>()
    val incidentCompleteResponse = mutableLiveDataOf<ArrayList<IncidentModel>>()
    val incidentCategoryResponse = mutableLiveDataOf<ArrayList<GeneralModel>>()
    val emergencyResponse = mutableLiveDataOf<ArrayList<EmergencyModel>>()
    val postCreateIncidentResponse = mutableLiveDataOf<BaseResponse>()
    val postCreateIncidentCommentResponse = mutableLiveDataOf<BaseResponse>()

    val incidentTitle = mutableLiveDataOf<String>()
    val incidentDescription = mutableLiveDataOf<String>()
    val commentText = mutableLiveDataOf<String>()
    private val emergencyId = mutableLiveDataOf<String>()
    private val clientId = mutableLiveDataOf<String>()
    private val picId = mutableLiveDataOf<String>()
    private val incidentCategoriesSelected = mutableLiveDataOf<ArrayList<String>>()
    private val dueDate = mutableLiveDataOf<String>()

    val incidentDetailModel = mutableLiveDataOf<IncidentModel>()
    val parentIdLocal = mutableLiveDataOf<String>()
    val urgencyType = mutableLiveDataOf<String>()
    val location = mutableLiveDataOf<String>()
    val scheduleShiftId = mutableLiveDataOf<String>()
    val currentScheduleResponse = mutableLiveDataOf<CurrentScheduleResponse>()

    private var pageProgress = 1
    private var pageComplete = 1

    val incidentRequestCompleteResponse = mutableLiveDataOf<BaseResponse>()
    val uploadSupervisorIncidentProofResponse = mutableLiveDataOf<BaseResponse>()
    val incidentApproveForChiefApiResponse = mutableLiveDataOf<BaseResponse>()
    val incidentApproveForClientApiResponse = mutableLiveDataOf<BaseResponse>()

    val mediaResponse = mutableLiveDataOf<Media2Response>()
    var imagesUploadedId = ArrayList<String>()
    var involvesUsersId = ArrayList<String>()

    fun processIntent(intent: Intent?) {
        incidentDetailModel.value = intent?.getParcelableExtra(EXTRA_INCIDENT_MODEL)
        incidentTitle.value = incidentDetailModel.value?.title
        incidentDescription.value = incidentDetailModel.value?.content
    }

    fun getIncidentProgress() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_INCIDENT_LIST)
            request.queries["page"] = "$pageProgress"
            request.queries["status"] = "pending"
            request.queries["branch_id"] = BaseApplication.sessionManager?.branchId ?: ""
            val response= request.get<IncidentDataResponse>()
            if (response?.isSuccess() == true) {
                if (!response.data?.reports?.items.isNullOrEmpty()) {
                    pageProgress += 1
                }
                if (pageProgress == 1 && response.data?.reports?.items.isNullOrEmpty()) {
                    showToast("No data")
                }
                incidentProgressResponse.value = response.data?.reports?.items
            }
            showLoading(false)
        }
    }

    fun getIncidentComplete() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_INCIDENT_LIST)
            request.queries["page"] = "$pageComplete"
            request.queries["status"] = "complete"
            val response= request.get<IncidentDataResponse>()
            if (response?.isSuccess() == true) {
                if (!response.data?.reports?.items.isNullOrEmpty()) {
                    pageComplete += 1
                }
                if (pageComplete == 1 && response.data?.reports?.items.isNullOrEmpty()) {
                    showToast("No data")
                }
                incidentCompleteResponse.value = response.data?.reports?.items
            }
            showLoading(false)
        }
    }

    fun getIncidentCategory() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_CATEGORY_LIST)
            request.queries["main_category_id"] = "1"
            val response= request.get<IncidentCategoryResponse>()
            if (response?.isSuccess() == true) {
                incidentCategoryResponse.value = response.data?.main_categories
            }
            showLoading(false)
        }
    }

    fun getEmergency() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_FINDING_LIST)
            val response= request.get<EmergencyResponse>()
            if (response?.isSuccess() == true) {
                emergencyResponse.value = response.data?.reports?.items
            }
            showLoading(false)
        }
    }

    fun postCreateIncident() {
        if (incidentTitle.value.isNullOrBlank()) {
            showToast("Incident title cannot empty")
            return
        }
        if (incidentDescription.value.isNullOrBlank()) {
            showToast("Incident detail cannot empty")
            return
        }

        showLoading()
        launch {
            val model = PostCreateIncident(incidentTitle.value, incidentDescription.value, picId.value,
                emergencyId.value, clientId.value, dueDate.value, incidentCategoriesSelected.value, urgencyType.value, location.value, scheduleShiftId.value, involvesUsersId, imagesUploadedId)

            val response = PostRequest<PostCreateIncident>(Urls.POST_INCIDENT_CREATE).post<BaseResponse>(model)
            showLoading(false)
            postCreateIncidentResponse.value = response

            if (response?.isSuccess() == false) {
                message.value = getErrorMessageServer(response.errors?.values?.toString())
            }
        }
    }

    fun updateEmergencyId(id: String?) {
        emergencyId.value = id
    }

    fun updateClientId(id: String?) {
        clientId.value = id
    }

    fun updatePicId(id: String?) {
        picId.value = id
    }

    fun updateIncidentCategoriesSelected(model: ArrayList<GeneralModel>) {
        incidentCategoriesSelected.value = arrayListOf()
        model.forEach {
            incidentCategoriesSelected.value?.add(it.id ?: "")
        }
    }

    fun updateDueDate(dueDate: String) {
        this.dueDate.value = dueDate
    }

    fun resetProgressData() {
        pageProgress = 1
        getIncidentProgress()
    }

    fun postCreateIncidentComment() {
        showLoading()
        launch {
            val model = PostCreateIncidentCommentModel(incidentDetailModel.value?.id, parentIdLocal.value, commentText.value)

            val response = PostRequest<PostCreateIncidentCommentModel>(Urls.POST_CREATE_INCIDENT_COMMENT+incidentDetailModel.value?.id).post<BaseResponse>(model)
            showLoading(false)
            postCreateIncidentCommentResponse.value = response

            if (response?.isSuccess() == true) {
                commentText.value = ""
            }
        }
    }

    fun updateParentCommentId(parentIdLocal: String?) {
        this.parentIdLocal.value = parentIdLocal
    }

    fun isStatusIdValid(statusId : String) : Boolean {
        return incidentDetailModel.value?.status?.equals(statusId, ignoreCase = true) == false
    }

    fun getIncidentRequestComplete() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_INCIDENT_REQUEST_COMPLETE+"/${incidentDetailModel.value?.id}")
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            incidentRequestCompleteResponse.value = response
            showLoading(false)
        }
    }

    fun uploadIncidentProof(file: File) {
        showLoading()
        launch {
            val request = UploadRequest(Urls.UPLOAD_SUPERVISOR_INCIDENT_PROOF)
            val files = HashMap<String, File>()
            files["image"] = file
            val names = HashMap<String, String>()
            names["incident_id"] = incidentDetailModel.value?.id ?: ""
            val response= request.upload<BaseResponse>(files, names)
            uploadSupervisorIncidentProofResponse.value = response
            showLoading(false)
        }
    }

    fun incidentApproveForChief() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PATCH_CHIEF_INCIDENT_APPROVE)
            apiRequest.queries["id"] = incidentDetailModel.value?.id ?: ""
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PATCH_METHOD)
            incidentApproveForChiefApiResponse.value = response
            showLoading(false)
        }
    }

    fun incidentApproveForClient() {
        showLoading()
        launch {
            val apiRequest = ApiRequest(Urls.PUT_CLIENT_INCIDENT_REPORT)
            apiRequest.queries["id"] = incidentDetailModel.value?.id ?: ""
            val response = apiRequest.requesting<BaseResponse>(ApiRequest.PUT_METHOD)
            incidentApproveForClientApiResponse.value = response
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

            names["section"] = "proof"

            names["description"] = "Report from incident"

            val response = request.upload<Media2Response>(files, names)
            mediaResponse.value = response
            showLoading(false)
        }
    }

    fun getCurrentSchedule() {
        launch {
            val request = GetRequest(Urls.GET_CURRENT_SCHEDULE)
            request.queries["actual"] = "1"
            val response = request.get<CurrentScheduleResponse>()
            currentScheduleResponse.value = response
        }
    }

    fun updateInvolves(selectedUserId: String) {
        involvesUsersId.add(selectedUserId)
    }

    fun clearInvolves() {
        involvesUsersId.clear()
    }

    companion object {
        const val EXTRA_INCIDENT_MODEL = "extra_incident_model"
        const val URGENCY_YELLOW = "medium"
        const val URGENCY_RED = "high"
        const val ROLE = "{role}"
    }

}