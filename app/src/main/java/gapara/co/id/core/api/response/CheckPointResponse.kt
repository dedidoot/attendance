package gapara.co.id.core.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.*
import kotlinx.android.parcel.Parcelize

data class CheckPointResponse(@SerializedName("data") val data: ArrayList<CheckPointDetailResponse>? = null) :
    BaseResponse()

@Parcelize
data class CheckPointDetailResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("check_point") val checkPoint: ArrayList<CheckPointModel>? = null
) : Parcelable

@Parcelize
data class CheckPointModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("is_active") val isActive: Int? = null,
    @SerializedName("current_shift") val currentShift: CurrentShiftModel? = null
) : Parcelable

@Parcelize
data class CurrentShiftModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("latitude") val latitude: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("checker") val checker: UserModel? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("checked_at") val checkedAt: String? = null,
    @SerializedName("attachments") val attachments: ArrayList<MediaModel>? = null,
    @SerializedName("name") val name: String? = null,
) : Parcelable {

    fun isPending(): Boolean {
        return status?.equals(PENDING, ignoreCase = true) == true
    }

    fun isChecked(): Boolean {
        return status?.equals(CHECKED, ignoreCase = true) == true
    }

    companion object {
        const val PENDING = "pending"
        const val CHECKED = "checked"
    }
}

data class CheckPointListV2Response(@SerializedName("data") val data: CheckPointListV2Model? = null) :
    BaseResponse()

data class CheckPointListV2Model(@SerializedName("checkpoints") val checkpoints: CheckPointV2Entity? = null)

data class CheckPointV2Entity(@SerializedName("items") val items: ArrayList<CheckPointListV2Entity>? = null)

@Parcelize
data class CheckPointListV2Entity(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("location") val location: GeneralModel? = null,
) : Parcelable






data class CheckPointV2Response(@SerializedName("data") val data: TaskModel? = null) :
    BaseResponse()

@Parcelize
data class TaskModel(
    @SerializedName("tasks") val tasks: TasksModel? = null,
) : Parcelable

@Parcelize
data class TasksModel(
    @SerializedName("items") val items: ArrayList<TaskEntity>? = null,
) : Parcelable

@Parcelize
data class TaskEntity(
    @SerializedName("id") val id: String? = null,
    @SerializedName("check_point_id") val check_point_id: String? = null,
    @SerializedName("schedule_id") val schedule_id: String? = null,
    @SerializedName("checker_id") val checker_id: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("checked_at") val checked_at: String? = null,
    @SerializedName("check_point_photo") val check_point_photo: String? = null,
    @SerializedName("created_at") val created_at: String? = null,
    @SerializedName("updated_at") val updated_at: String? = null,
    @SerializedName("schedule") val schedule: TaskScheduleEntity? = null,
    @SerializedName("check_point") val check_point: TaskCheckPointEntity? = null,
    @SerializedName("checker") val checker: CheckerModel? = null,
) : Parcelable

@Parcelize
data class CheckerModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("phone") val phone: String? = null,
) : Parcelable

@Parcelize
data class TaskScheduleEntity(
    @SerializedName("id") val id: String? = null,
    @SerializedName("branch_id") val branch_id: String? = null,
    @SerializedName("schedule_date_id") val schedule_date_id: String? = null,
    @SerializedName("shift_id") val shift_id: String? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("real_start") val real_start: String? = null,
    @SerializedName("real_end") val real_end: String? = null,
    @SerializedName("overtime_status") val overtime_status: String? = null,
) : Parcelable

@Parcelize
data class TaskCheckPointEntity(
    @SerializedName("id") val id: String? = null,
    @SerializedName("location_id") val location_id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("location") val location: GeneralModel? = null,
) : Parcelable