package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SubCommentModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null
) : Parcelable