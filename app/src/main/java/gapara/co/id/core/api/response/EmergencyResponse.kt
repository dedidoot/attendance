package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.EmergencyModel
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.SchedulesModel
import gapara.co.id.core.model.UserModel

data class EmergencyResponse(@SerializedName("data") val data: EmergencyItem? = null) : BaseResponse()

data class EmergencyItem(@SerializedName("reports") val reports: EmergencyDetailItem? = null)

data class EmergencyDetailItem(@SerializedName("items") val items: ArrayList<EmergencyModel>? = null)