package gapara.co.id.core.model

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("user") val userModel: UserModel? = null,
)