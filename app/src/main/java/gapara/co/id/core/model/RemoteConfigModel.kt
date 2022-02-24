package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RemoteConfigModel(
    @SerializedName("data") val data: ArrayList<GeneralModel>? = null
) : Parcelable