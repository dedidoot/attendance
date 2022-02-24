package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.api.response.CurrentShiftModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleShiftReportModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("shift") val shift: UserModel? = null,
    @SerializedName("guards") val guards: ArrayList<GuardReportModel>? = null,
    @SerializedName("patrols") val patrols: ArrayList<GuardReportModel>? = null,
    @SerializedName("check_points") val checkPoints: ArrayList<CurrentShiftModel>? = null,
) : Parcelable

@Parcelize
data class GuardReportModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("user") val user: UserModel? = null,
    @SerializedName("location") val location: UserModel? = null,
    @SerializedName("status") val status: String? = null
) : Parcelable {

    fun isPending() : Boolean {
        return status?.equals(PENDING, ignoreCase = true) == true
    }

    companion object {
        const val PENDING = "pending"
    }

}