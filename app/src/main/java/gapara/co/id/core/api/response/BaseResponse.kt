package gapara.co.id.core.api.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("success") val isSuccess: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errors") val errors: HashMap<Any, Any>? = null
){
    fun isSuccess():Boolean{
        return isSuccess == true
    }
}