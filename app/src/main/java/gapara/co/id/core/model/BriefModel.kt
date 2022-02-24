package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.api.response.CurrentScheduleEntity
import kotlinx.android.parcel.Parcelize

data class BriefsModel(@SerializedName("briefs") val briefs: BriefModel? = null)

@Parcelize
data class BriefModel(@SerializedName("items") var briefs: ArrayList<BriefItemModel>? = null) :
    Parcelable

@Parcelize
data class BriefItemModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("chief") val chief: UserModel? = null,
    @SerializedName("spvs") val spvs: ArrayList<UserModel>? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("schedule") val schedule: CurrentScheduleEntity? = null,
    @SerializedName("read_at") val readAt: String? = null,
    @SerializedName("creator") val creator: UserModel? = null,
) : Parcelable