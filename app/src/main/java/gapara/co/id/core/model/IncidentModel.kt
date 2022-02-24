package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IncidentItemModel(
    @SerializedName("items") var items: ArrayList<IncidentModel>? = null,
): Parcelable

@Parcelize
data class IncidentModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("branch_id") var branchId: String? = null,
    @SerializedName("emergency_id") var emergencyId: String? = null,
    @SerializedName("pic_id") var picId: String? = null,
    @SerializedName("client_id") var clientId: String? = null,
    @SerializedName("creator_id") var creatorId: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("due_date") var dueDate: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("pic") var pic: UserModel? = null,
    @SerializedName("emergency") var emergency: EmergencyModel? = null,
    @SerializedName("creator") var creator: UserModel? = null,
    @SerializedName("approvals") var approvals: ArrayList<ApprovalModel>? = null,
    @SerializedName("comments") var comments: ArrayList<CommentModel>? = null,
    @SerializedName("images") var images: ArrayList<MediaModel>? = null,
    @SerializedName("follow_ups") var followUps: ArrayList<FollowUpModel>? = null,
    @SerializedName("urgency") var urgency: String? = null,
    @SerializedName("deadline") var deadline: String? = null,
) : Parcelable {

    fun isPending() : Boolean {
        return status?.equals(PENDING, ignoreCase = true) == true
    }

    fun isComplete() : Boolean {
        return status?.equals(COMPLETE, ignoreCase = true) == true
    }

    fun isRequestComplete() : Boolean {
        return status?.equals(REQUEST_COMPLETE, ignoreCase = true) == true
    }

    fun isRed() : Boolean {
        return urgency?.equals(RED, ignoreCase = true) == true
    }

    companion object {
        const val REQUEST_COMPLETE = "request_complete"
        const val PENDING = "pending"
        const val COMPLETE = "complete"
        const val RED = "red"
    }
}

@Parcelize
data class EmergencyModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("branch_id") var branchId: String? = null,
    @SerializedName("creator_id") var creatorId: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("creator") var creator: UserModel? = null,
    @SerializedName("medias") var medias: ArrayList<MediaModel>? = null,
) : Parcelable

@Parcelize
data class ApprovalModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("incident_id") var incidentId: String? = null,
    @SerializedName("approval_id") var approvalId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("approval") var approval: UserModel? = null,
) : Parcelable {

    fun isPending() : Boolean {
        return status?.equals("pending", ignoreCase = true) == true
    }

    fun isApproved() : Boolean {
        return status?.equals("approved", ignoreCase = true) == true
    }
}

@Parcelize
data class FollowUpModel(
    @SerializedName("description") var description: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("action_at") var action_at: String? = null,
) : Parcelable