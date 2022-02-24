package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class ReportModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("creator")
    var creator: UserModel? = null,
    @SerializedName("is_emergency")
    var isEmergency: Boolean? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("schedule_shift")
    var scheduleShift: ScheduleShiftReportModel? = null
) : Parcelable {

    fun isCheckPoint(): Boolean {
        return type?.equals(CHECK_POINT_TYPE, ignoreCase = true) == true
    }

    companion object {
        const val CHECK_POINT_TYPE = "check-point"
    }
}