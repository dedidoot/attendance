package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.AnnouncementItemModel

data class AnnouncementUnreadResponse(@SerializedName("data") val data: AnnouncementUnreadItem? = null) : BaseResponse()

data class AnnouncementUnreadItem(@SerializedName("unread_announcement") val unreadAnnouncement: AnnouncementItemModel? = null)