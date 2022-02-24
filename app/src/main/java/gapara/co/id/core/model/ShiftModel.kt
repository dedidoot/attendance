package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShiftModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("shift") val shift: UserModel? = null,
    @SerializedName("supervisor") val supervisor: UserModel? = null,
    @SerializedName("announcement_read_at") val announcementReadAt: String? = null,
    @SerializedName("schedule") val schedule: ScheduleShiftModel? = null,
    @SerializedName("created_at") val created_at: String? = null,
) : Parcelable {

    fun isRead() : Boolean {
        return announcementReadAt != null
    }
}