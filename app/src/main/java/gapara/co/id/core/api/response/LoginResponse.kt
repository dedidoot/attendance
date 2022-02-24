package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName
import gapara.co.id.core.model.LoginModel

data class LoginResponse(@SerializedName("data") val loginModel: LoginModel? = null) : BaseResponse()