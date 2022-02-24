package gapara.co.id.feature.incident

import android.content.Intent
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.UploadRequest
import gapara.co.id.core.api.response.*
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class AddNewsViewModel : BaseViewModel(), CoroutineDeclare {

    val dateToday = mutableLiveDataOf<String>()
    val incidentId = mutableLiveDataOf<String>()
    val reportDescription = mutableLiveDataOf<String>()
    val createReportApiResponse = mutableLiveDataOf<BaseResponse>()
    var attachments = ArrayList<File>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        incidentId.value = intent?.getStringExtra(EXTRA_INCIDENT_ID)
    }

    fun onCreateIntelReport() {
        if (reportDescription.value.isNullOrBlank()) {
            showToast("Description cannot empty")
            return
        }

        showLoading()

        launch {
            val request = UploadRequest(Urls.POST_CREATE_INCIDENT_NEWS + incidentId.value)
            val files = HashMap<String, File>()
            attachments.forEach { file ->
                file.takeIf { it.exists() }?.apply {
                    files["image"] = this
                }
            }

            val names = HashMap<String, String>()
            names["description"] = reportDescription.value ?: ""
            names["action_at"] = TimeHelper.convertToFormatDateSever(Date(), TimeHelper.FORMAT_DATE_FULL)

            val response= request.upload<Media2Response>(files, names)
            if (response?.isSuccess() == true) {
                createReportApiResponse.value = response
            } else {
                val message = getErrorMessageServer(response?.errors?.values?.toString()).takeIf { it.isNotBlank() }
                    ?: kotlin.run {
                        response?.message ?: "Failed uploaded"
                    }

                showToast(message)
            }
            showLoading(false)
        }
    }

    companion object {

        const val EXTRA_INCIDENT_ID = "EXTRA_INCIDENT_ID"
    }
}