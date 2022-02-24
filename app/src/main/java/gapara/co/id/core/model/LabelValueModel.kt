package gapara.co.id.core.model

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.api.response.BaseResponse

data class LabelValueModel(@SerializedName("label") val label: String? = null,
                           @SerializedName("value") val value: String? = null) : BaseResponse()