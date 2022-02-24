package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.AnnouncementModel

data class AnnouncementResponse(@SerializedName("data") val data: AnnouncementModel? = null) : BaseResponse()