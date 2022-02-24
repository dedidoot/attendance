package gapara.co.id.feature.report

import android.content.Intent
import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GetRequest
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.api.response.ReportDataResponse
import gapara.co.id.core.base.*
import gapara.co.id.core.model.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ReportViewModel : BaseViewModel() , CoroutineDeclare {

    val reportResponse = mutableLiveDataOf<ArrayList<ReportModel>>()
    val reportModel = mutableLiveDataOf<ReportModel>()
    val dateToday = mutableLiveDataOf<String>()
    val reportTitle = mutableLiveDataOf<String>()
    val reportDescription = mutableLiveDataOf<String>()
    val createReportApiResponse = mutableLiveDataOf<BaseResponse>()
    var reportListPage = 1

    init {
        val realFromServerDate = TimeHelper.convertToFormatDateSever(Calendar.getInstance().time)
        dateToday.value = realFromServerDate
    }

    fun processIntent(intent: Intent?) {
        reportModel.value = intent?.getParcelableExtra(EXTRA_REPORT_MODEL)
    }

    fun getReportList(startDate : String? = null, endDate : String? = null) {
        showLoading()
        launch {
            val request = GetRequest(getReportListUrl())
            startDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["start_date"] = this
            }
            endDate.takeIf { !it.isNullOrBlank() }?.apply {
                request.queries["end_date"] = this
            }
            request.queries["page"] = "$reportListPage"
            val response= request.get<ReportDataResponse>()
            reportResponse.value = response?.data?.reports?.items

            if (response?.data?.reports?.items.isNullOrEmpty()) {
                showToast("No data")
            } else {
                reportListPage += 1
            }
            showLoading(false)
        }
    }

    private fun getReportListUrl(): String {
        if (isIntel()) { return Urls.GET_INTEL_REPORT_LIST } else return Urls.GET_DROP_OFF_REPORT_LIST
    }

    fun onCreateReport() {
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
            val model = PostModel(reportTitle.value, reportDescription.value)
            val response = PostRequest<PostModel>(getCreateReportUrl()).post<BaseResponse>(model)
            createReportApiResponse.value = response
            showLoading(false)
        }
    }

    private fun getCreateReportUrl(): String {
        return if (isIntel()) { return Urls.POST_INTEL_CREATE_REPORT } else Urls.POST_DROP_OFF_CREATE_REPORT
    }

    companion object {
        const val EXTRA_REPORT_MODEL = "extra_report_model"
    }
}