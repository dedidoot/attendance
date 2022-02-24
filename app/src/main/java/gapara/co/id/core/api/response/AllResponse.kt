package gapara.co.id.core.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.ReplacementModel
import gapara.co.id.core.model.UserModel
import kotlinx.android.parcel.Parcelize

data class CurrentScheduleResponse(@SerializedName("data") val data: CurrentSchedulesModel? = null) :
    BaseResponse()

@Parcelize
data class CurrentSchedulesModel(@SerializedName("schedules") val schedules: ArrayList<CurrentScheduleEntity>? = null) :
    Parcelable

@Parcelize
data class CurrentScheduleEntity(
    @SerializedName("id") val id: String? = null,
    @SerializedName("branch") val branch: GeneralModel? = null,
    @SerializedName("scheduleDate") val scheduleDate: ScheduleDateEntity? = null,
    @SerializedName("shift") val shift: GeneralModel? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("real_start") val real_start: String? = null,
    @SerializedName("real_end") val real_end: String? = null,
    @SerializedName("overtime_status") val overtime_status: String? = null,
    @SerializedName("users") var users: ArrayList<UserCurrentScheduleEntity>? = null,
    @SerializedName("announcement_read_at") val announcement_read_at: String? = null,
) : Parcelable

@Parcelize
data class ScheduleDateEntity(@SerializedName("date") val date: String? = null) :
    Parcelable

@Parcelize
data class UserCurrentScheduleEntity(
    @SerializedName("id") val id: String? = null,
    @SerializedName("user") val user: UserModel? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("created_at") val created_at: String? = null,
    @SerializedName("presence_photo") val presence_photo: String? = null,
    @SerializedName("present_at") val present_at: String? = null,
    @SerializedName("attachment") val attachment: String? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("replacement") val replacement: ReplacementModel? = null,
    @SerializedName("replaced") val replaced: ReplacementModel? = null
) : Parcelable

data class CurrentScheduleUsersResponse(@SerializedName("data") val data: CurrentScheduleEntity? = null) :
    BaseResponse()


data class CurrentItemScheduleResponse(@SerializedName("data") val data: CurrentItemSchedulesModel? = null) :
    BaseResponse()

@Parcelize
data class CurrentItemSchedulesModel(@SerializedName("schedules") val schedules: CurrentItemsSchedulesModel? = null) :
    Parcelable

@Parcelize
data class CurrentItemsSchedulesModel(@SerializedName("items") val items: ArrayList<CurrentScheduleEntity>? = null) :
    Parcelable


data class PermissionsResponse(@SerializedName("data") val data: PermissionsEntity? = null) :
    BaseResponse()

@Parcelize
data class PermissionsEntity(@SerializedName("permissions") val permissions: ArrayList<GeneralModel>? = null) :
    Parcelable


@Parcelize
data class FeedBackEntity(
    @SerializedName("name") val name: String? = null,
    @SerializedName("feed_back") val feed_back: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("images") val images: ArrayList<String>? = null,
    @SerializedName("branch_id") val branch_id: String? = null,
) :
    Parcelable

data class BranchResponse(@SerializedName("data") val data: BranchModel? = null) :
    BaseResponse()

@Parcelize
data class BranchModel(@SerializedName("branches") val branches: ArrayList<GeneralModel>? = null) : Parcelable
