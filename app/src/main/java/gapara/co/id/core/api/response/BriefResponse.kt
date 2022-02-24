package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.BriefsModel

data class BriefResponse(@SerializedName("data") val data: BriefsModel? = null) : BaseResponse()