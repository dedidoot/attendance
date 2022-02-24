package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.api.response.CurrentScheduleEntity
import kotlinx.android.parcel.Parcelize

data class AnnouncementModel(@SerializedName("announcements") val announcement: AnnouncementObjectModel? = null)

data class AnnouncementObjectModel(@SerializedName("items") val items: ArrayList<AnnouncementItemModel>? = null)

@Parcelize
data class AnnouncementItemModel(@SerializedName("id") var id: String? = null,
                                 @SerializedName("title") var title: String? = null,
                                 @SerializedName("content") var content: String? = null,
                                 @SerializedName("chief") var chief: UserModel? = null,
                                 @SerializedName("schedules") var schedules: ArrayList<CurrentScheduleEntity>? = null,
                                 @SerializedName("created_at") var createdAt: String? = null,
                                 @SerializedName("creator") var creator: UserModel? = null,
                                 @SerializedName("level") var level: String? = null,) : Parcelable