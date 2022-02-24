package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.SpecialReportModel

data class SpecialReportDataResponse(@SerializedName("data") val data: SpecialReportDetailResponse? = null) : BaseResponse()

data class SpecialReportDetailResponse(@SerializedName("reports") val reports: SpecialReportResponse? = null)

data class SpecialReportResponse(@SerializedName("items") val items: ArrayList<SpecialReportModel>? = null)