package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class CommentModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("comment")
    var comment: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("commentator")
    var commentator: UserCommentator? = null,
    @SerializedName("sub_comments")
    var subComments: ArrayList<SubCommentModel>? = null,
) : Parcelable


@Parcelize
class UserCommentator(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("avatar")
    var avatar: String? = null
) : Parcelable