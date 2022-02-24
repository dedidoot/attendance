package gapara.co.id.feature.incident

import gapara.co.id.core.api.*
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class Incident2ViewModel : BaseViewModel(), CoroutineDeclare {

    val postIncidentFollowUpResponse = mutableLiveDataOf<BaseResponse>()
    val postStatusUpdateResponse = mutableLiveDataOf<BaseResponse>()

    fun postIncidentFollowUp(file: File?, incidentId: String?, action: String) {
        launch {
            val request = UploadRequest(Urls.POST_FOLLOW_UP_INCIDENT+incidentId)
            val files = HashMap<String, File>()
            file?.apply { files["image"] = this }
            val names = HashMap<String, String>()
            names["section"] = "documentation"
            names["description"] = action
            names["action_at"] = TimeHelper.convertToFormatDateSever(Date(), TimeHelper.FORMAT_DATE_FULL)
            val response = request.upload<BaseResponse>(files, names)
            postIncidentFollowUpResponse.value = response
        }
    }

    fun postStatusUpdate(incidentId: String?, statusId: String) {
        launch {
            val model = PostStatusUpdateModel(incidentId, statusId)
            val response = PostRequest<PostStatusUpdateModel>(Urls.POST_INCIDENT_UPDATE_STATUS).post<BaseResponse>(model)
            showLoading(false)
            postStatusUpdateResponse.value = response
        }
    }
}