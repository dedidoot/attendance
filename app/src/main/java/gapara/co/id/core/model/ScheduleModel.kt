package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class SchedulesModel(@SerializedName("schedule") val schedule: ScheduleShiftModel? = null)

@Parcelize
data class ScheduleShiftModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("shifts") val shifts: ArrayList<ScheduleListShiftModel>? = null
) : Parcelable

@Parcelize
data class ScheduleListShiftModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("shift") val shift: UserModel? = null,
    @SerializedName("brief") val brief: BriefItemModel? = null,
    @SerializedName("supervisor") val supervisor: UserModel? = null,
    @SerializedName("guards") val guards: ArrayList<GuardsModel>? = null,
    @SerializedName("patrols") val patrols: ArrayList<GuardsModel>? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
) : Parcelable
