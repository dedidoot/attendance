package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.IncidentItemModel

data class IncidentDataResponse(@SerializedName("data") val data: IncidentResponse? = null) : BaseResponse()

data class IncidentResponse(@SerializedName("reports") val reports: IncidentItemModel? = null)