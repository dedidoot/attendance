package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaModel(
    @SerializedName("url") val url: String? = null,
    @SerializedName("section") val section: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("image") val image: String? = null,
): Parcelable

@Parcelize
data class Media2Model(
    @SerializedName("image") val image: MediaModel? = null,
): Parcelable