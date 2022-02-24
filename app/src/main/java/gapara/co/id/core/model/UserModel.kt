package gapara.co.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatar") val avatar: String? = "",
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("fcm_token") var fcmToken: String? = null,
    @SerializedName("imei") val imeiDevice: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("branch_id") val branchId: GeneralModel? = null,
    @SerializedName("brief_read_at") val briefReadAt: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("role") val role: GeneralModel? = null,
    @SerializedName("branch") val branch: GeneralModel? = null,
) : Parcelable