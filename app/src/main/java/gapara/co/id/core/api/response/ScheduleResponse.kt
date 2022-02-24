package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.SchedulesModel

data class ScheduleResponse(@SerializedName("data") val data: SchedulesModel? = null) : BaseResponse()