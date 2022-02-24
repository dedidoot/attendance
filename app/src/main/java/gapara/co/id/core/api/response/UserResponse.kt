package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.GeneralModel
import gapara.co.id.core.model.SchedulesModel
import gapara.co.id.core.model.UserModel

data class UserResponse(@SerializedName("data") val data: UserItem? = null) : BaseResponse()

data class UserItem(@SerializedName("users") val users: UserDetailItem? = null)

data class UserDetailItem(@SerializedName("items") val items: ArrayList<UserModel>? = null)