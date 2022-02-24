package gapara.co.id.feature.special_report

import android.content.Intent
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

class SpecialReportViewModel : BaseViewModel() , CoroutineDeclare {

    val reportResponse = mutableLiveDataOf<ArrayList<SpecialReportModel>>()
    val reportModel = mutableLiveDataOf<SpecialReportModel>()
    val dateToday = mutableLiveDataOf<String>()
    val reportTitle = mutableLiveDataOf<String>()
    val reportDescription = mutableLiveDataOf<String>()
    val reportRemarks = mutableLiveDataOf<String>()
    val createReportApiResponse = mutableLiveDataOf<BaseResponse>()
    var reportListPage = 1

    val incidentCategoryResponse = mutableLiveDataOf<ArrayList<GeneralModel>>()
    private val incidentCategoriesSelected = mutableLiveDataOf<ArrayList<String>>()
    val mediaResponse = mutableLiveDataOf<MediaResponse>()
    var imagesUploadedId = ArrayList<String>()

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        reportModel.value = intent?.getParcelableExtra(EXTRA_REPORT_MODEL)
    }

    fun getSpecialReportList(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_INTEL_SPECIAL_REPORT_LIST)
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["start_date"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["end_date"] = this
            }
            request.queries["page"] = "$reportListPage"
            val response= request.get<SpecialReportDataResponse>()
            reportResponse.value = response?.data?.reports?.items

            if (response?.data?.reports?.items.isNullOrEmpty()) {
                showToast("No data")
            } else {
                reportListPage += 1
            }
            showLoading(false)
        }
    }

    fun onCreateSpecialReport() {
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
            val model = PostCreateSpecialReport(reportTitle.value, reportDescription.value, reportRemarks.value, incidentCategoriesSelected.value, imagesUploadedId)
            val response = PostRequest<PostCreateSpecialReport>(Urls.POST_INTEL_CREATE_SPECIAL_REPORT).post<BaseResponse>(model)
            createReportApiResponse.value = response
            showLoading(false)
        }
    }

    fun getIncidentCategory() {
        showLoading()
        launch {
            val request = GetRequest(Urls.GET_INTEL_INCIDENT_CATEGORY)
            val response= request.get<IncidentCategoryResponse>()
            if (response?.isSuccess() == true) {
                //incidentCategoryResponse.value = response.data
            }
            showLoading(false)
        }
    }

    fun updateIncidentCategoriesSelected(model: ArrayList<GeneralModel>) {
        incidentCategoriesSelected.value = arrayListOf()
        model.forEach {
            incidentCategoriesSelected.value?.add(it.id ?: "")
        }
    }

    fun uploadImage(file: File) {
        showLoading()
        launch {
            val request = UploadRequest(Urls.UPLOAD_INTEL_SPECIAL_REPORT)
            val files = HashMap<String, File>()
            files["image"] = file
            val names = HashMap<String, String>()
            val response= request.upload<MediaResponse>(files, names)
            mediaResponse.value = response
            showLoading(false)
        }
    }

    companion object {
        const val EXTRA_REPORT_MODEL = "extra_report_model"
    }
}