package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class SpecialReportModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("security_issue")
    var title: String? = null,
    @SerializedName("fact_content")
    var content: String? = null,
    @SerializedName("remarks")
    var remarks: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("reporter")
    var creator: UserModel? = null,
    @SerializedName("branch")
    var branch: UserModel? = null,
    @SerializedName("items")
    var items: ArrayList<MediaModel>? = null
) : Parcelable