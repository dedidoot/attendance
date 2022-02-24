package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReplacementModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("location") val location: GeneralModel? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("user") val user: UserModel? = null,
    @SerializedName("present_photo") val presentPhoto: String? = null,
    @SerializedName("attachment") val attachment: String? = null
) : Parcelable {

    fun isPending() : Boolean {
        return status?.equals(PENDING, ignoreCase = true) == true
    }

    fun isPresent() : Boolean {
        return status?.equals(PRESENT, ignoreCase = true) == true
    }

    fun isAbsent() : Boolean {
        return status?.equals(ABSENT, ignoreCase = true) == true
    }

    companion object {
        const val PENDING = "pending"
        const val PRESENT = "present"
        const val ABSENT = "absent"
    }
}