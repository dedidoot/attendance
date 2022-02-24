package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class FeedbackModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("branch_id")
    var branchId: String? = null,
    @SerializedName("client_id")
    var clientId: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("client")
    var client: UserModel? = null
) : Parcelable