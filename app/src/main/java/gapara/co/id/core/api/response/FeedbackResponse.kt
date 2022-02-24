package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.*

data class FeedbackResponse(@SerializedName("data") val data: FeedbackItem? = null) : BaseResponse()

data class FeedbackItem(@SerializedName("feedbacks") val feedback: FeedbackDetailItem? = null)

data class FeedbackDetailItem(@SerializedName("items") val items: ArrayList<FeedbackModel>? = null)