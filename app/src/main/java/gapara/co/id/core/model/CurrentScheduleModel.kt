package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentScheduleModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("branch_id") val branchId: String? = null,
    @SerializedName("schedule_id") val scheduleId: String? = null,
    @SerializedName("shift_id") val shiftId: String? = null,
    @SerializedName("supervisor_id") val supervisorId: String? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("schedule") val schedule: CurrentScheduleItemModel? = null,
    @SerializedName("shift") val shift: GeneralModel? = null,
    @SerializedName("supervisor") val supervisor: UserModel? = null,
    @SerializedName("all_user_schedules") val allUserSchedules: ArrayList<AllUserScheduleModel>? = null,
    @SerializedName("schedule_shift_check_point") val scheduleShiftCheckPoint: ArrayList<ScheduleShiftCheckPoint>? = null,
) : Parcelable

@Parcelize
data class CurrentScheduleItemModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("admin_id") val adminId: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) : Parcelable

@Parcelize
data class AllUserScheduleModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("schedule_shift_id") val scheduleShiftId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("has_device") val hasDevice: Boolean? = null,
    @SerializedName("location_id") val locationId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("present_photo") val presentPhoto: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("user") val user: UserModel? = null
) : Parcelable

@Parcelize
data class ScheduleShiftCheckPoint(
    @SerializedName("id") val id: String? = null,
    @SerializedName("check_point_id") val checkPointId: String? = null,
    @SerializedName("schedule_shift_id") val scheduleShiftId: String? = null,
    @SerializedName("checker_id") val checkerId: Boolean? = null,
    @SerializedName("latitude") val latitude: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("checked_at") val checkedAt: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) : Parcelable