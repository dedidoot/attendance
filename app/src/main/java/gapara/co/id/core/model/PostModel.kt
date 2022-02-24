package gapara.co.id.core.model

import com.google.gson.annotations.SerializedName

data class PostBriefModel(
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("schedules") val schedules: ArrayList<String>? = null
)

data class PostAnnouncementModel(
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("schedules") val schedules: ArrayList<String>? = null,
    @SerializedName("level") val level: String? = null,
)

data class PostCreateIncident(
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("pic_id") val picId: String? = null,
    @SerializedName("emergency_id") val emergencyId: String? = null,
    @SerializedName("client_id") val clientId: String? = null,
    @SerializedName("deadline") val deadline: String? = null,
    @SerializedName("categories") val categories: ArrayList<String>? = null,
    @SerializedName("level") val level: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("schedule_id") val schedule_id: String? = null,
    @SerializedName("involves") val involves: ArrayList<String>? = null,
    @SerializedName("images") val images: ArrayList<String>? = null,
)

data class PostModel(
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("media") val media: ArrayList<String>? = null,
)

data class PostCreateIncidentCommentModel(
    @SerializedName("incident_id") val incidentId: String? = null,
    @SerializedName("incident_comment_id") val incidentCommentId: String? = null,
    @SerializedName("comment") val comment: String? = null
)

data class PostCreateSpecialReport(
    @SerializedName("security_issue") val securityIssue: String? = null,
    @SerializedName("fact_content") val factContent: String? = null,
    @SerializedName("remarks") val remarks: String? = null,
    @SerializedName("incident_categories") val incidentCategories: ArrayList<String>? = null,
    @SerializedName("items") val items: ArrayList<String>? = null
)

data class PostCreateLostReport(
    @SerializedName("schedule_id") val schedule_id: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("images") val images: ArrayList<String>? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("approximate_time") val approximate_time: String? = null
)

data class PostStatusUpdateModel(
    @SerializedName("incident_id") val incidentId: String? = null,
    @SerializedName("status") val status: String? = null,
)



data class PostAddNewsModel(
    @SerializedName("description") val description: String? = null,
    @SerializedName("action_at") val action_at: String? = null,
    @SerializedName("image") val image: ArrayList<String>? = null,
)