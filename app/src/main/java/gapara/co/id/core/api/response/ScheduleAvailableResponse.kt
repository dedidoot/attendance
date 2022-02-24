package gapara.co.id.core.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.UserModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduleAvailableResponse(@SerializedName("data") var data: ArrayList<UserModel>? = null) :
    BaseResponse(), Parcelable