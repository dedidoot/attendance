package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.SchedulesModel

data class IncidentCategoryResponse(@SerializedName("data") val data: IncidentCategoryEntity? = null) : BaseResponse()
data class IncidentCategoryEntity(@SerializedName("main_categories") val main_categories: ArrayList<GeneralModel>? = null) : BaseResponse()