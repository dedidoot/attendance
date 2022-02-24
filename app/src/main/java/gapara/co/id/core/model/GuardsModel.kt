package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GuardsModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("location") var location: GeneralModel? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("user") var user: UserModel? = null,
    @SerializedName("replacement") var replacement: ReplacementModel? = null,
    @SerializedName("is_from_replacement_local") var isFromReplacementLocal: Boolean? = null,
    @SerializedName("present_photo") var presentPhoto: String? = null,
    @SerializedName("attachment") var attachment: String? = null,
    @SerializedName("reason") var reason: String? = null,
    @SerializedName("present_at") var present_at: String? = null,
    @SerializedName("replaced") val replaced: ReplacementModel? = null
) : Parcelable {

    fun isPending() : Boolean {
        return status?.equals(PENDING, ignoreCase = true) == true
    }

    fun isPresent() : Boolean {
        return status?.equals(PRESENT, ignoreCase = true) == true
    }

    fun isAbsent() : Boolean {
        return status?.equals(ABSENT, ignoreCase = true) == true ||
                status?.equals(PAID_LEAVE, ignoreCase = true) == true||
                status?.equals(LEAVE_WITH_PERMISSION, ignoreCase = true) == true||
                status?.equals(TRAINING, ignoreCase = true) == true||
                status?.equals(SICK, ignoreCase = true) == true
    }

    companion object {
        const val PENDING = "pending"
        const val APPROVED = "approved"
        const val PRESENT = "present"
        const val ABSENT = "absent"
        const val PAID_LEAVE = "paid-leave"
        const val LEAVE_WITH_PERMISSION = "leave-with-permission"
        const val SICK = "sick"
        const val TRAINING = "training"
    }
}