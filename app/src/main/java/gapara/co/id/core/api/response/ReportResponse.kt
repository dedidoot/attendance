package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.IncidentItemModel
import gapara.co.id.core.model.ReportModel

data class ReportDataResponse(@SerializedName("data") val data: ReportDetailResponse? = null) : BaseResponse()

data class ReportDetailResponse(@SerializedName("reports") val reports: ReportResponse? = null)

data class ReportResponse(@SerializedName("items") val items: ArrayList<ReportModel>? = null)