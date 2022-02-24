package gapara.co.id.feature.login

import gapara.co.id.core.api.CoroutineDeclare
import gapara.co.id.core.api.GSONManager
import gapara.co.id.core.api.PostRequest
import gapara.co.id.core.api.response.LoginResponse
import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.BaseViewModel
import gapara.co.id.core.base.mutableLiveDataOf
import gapara.co.id.core.model.Urls
import gapara.co.id.core.model.UserModel
import kotlinx.coroutines.launch
import kotlin.random.Random

class LoginViewModel : BaseViewModel() , CoroutineDeclare {

    private var DEVICE_MODEL = "${android.os.Build.MODEL} ${android.os.Build.MANUFACTURER} ${android.os.Build.BRAND} ${android.os.Build.FINGERPRINT}"
    val loginApiResponse = mutableLiveDataOf<LoginResponse>()
    val username = mutableLiveDataOf<String>()
    val password = mutableLiveDataOf<String>()

    fun onLogin() {
        if (!isValidationPassed()) {
            showToast("Email and password not matches")
            return
        }
        showLoading()
        var fcmToken = BaseApplication.sessionManager?.fcmToken
        if (fcmToken.isNullOrEmpty()) {
            fcmToken = "${Random.nextLong()}"
        }
        val userModel = UserModel(username = username.value, password = password.value,
            fcmToken = fcmToken,
            imeiDevice = DEVICE_MODEL)

        BaseApplication.sessionManager?.loginParams = GSONManager.toJson(userModel)

        val url = Urls.POST_LOGIN

        launch {
            val apiResponse= PostRequest<UserModel>(url).post<LoginResponse>(userModel)
            if (apiResponse?.isSuccess() == true) {
                saveProfile(apiResponse)
                loginApiResponse.value = apiResponse
            } else {
                showToast(apiResponse?.message ?: "Email and password not matches")
            }
            showLoading(false)
        }
    }

    private fun isValidationPassed(): Boolean {
        return !password.value.isNullOrBlank() && !username.value.isNullOrBlank()
    }

    private fun saveProfile(apiResponse: LoginResponse) {
        BaseApplication.sessionManager?.accessToken = apiResponse.loginModel?.accessToken
        BaseApplication.sessionManager?.userId = apiResponse.loginModel?.userModel?.id
        BaseApplication.sessionManager?.fullName = apiResponse.loginModel?.userModel?.name
        BaseApplication.sessionManager?.email = apiResponse.loginModel?.userModel?.email
        BaseApplication.sessionManager?.avatar = apiResponse.loginModel?.userModel?.avatar
        BaseApplication.sessionManager?.role = apiResponse.loginModel?.userModel?.role?.name
        BaseApplication.sessionManager?.branchId = apiResponse.loginModel?.userModel?.branch?.id
    }

}