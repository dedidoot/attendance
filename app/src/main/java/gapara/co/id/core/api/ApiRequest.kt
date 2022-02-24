package gapara.co.id.core.api

import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.FirebaseApplication
import gapara.co.id.core.base.RemoteConfigHelper
import gapara.co.id.core.model.Urls

class ApiRequest(val url: String) {

    val request = RestClient.createStringService(UrlApi::class.java, RemoteConfigHelper.getApiUrl())
    var headers = HashMap<String, String>()
    var queries = HashMap<String, String>()

    init {
        BaseApplication.sessionManager?.accessToken.takeIf { !it.isNullOrEmpty() }?.apply {
            headers[Urls.AUTHORIZATION_KEY] = "Bearer $this"
        }
        headers["Accept"] = "application/json"
    }

    suspend inline fun <reified T> requesting(method : Int) : T? {
        return try {
            val response= if (method == DELETE_METHOD) {
                request.deleteRequestApi(RemoteConfigHelper.getApiUrl() + url, headers, queries)
            } else if (method == PUT_METHOD) {
                request.putRequestApi(RemoteConfigHelper.getApiUrl() + url, headers, queries)
            } else {
                request.patchRequestApi(RemoteConfigHelper.getApiUrl() + url, headers, queries)
            }
            checkUnauthenticated(response)
            getResponse(response)
        } catch (throwable: Throwable) {
            FirebaseApplication.recordException(throwable, "$url $throwable $headers $queries")
            checkConnectionTimeOut(throwable, isJustShowToast = true)
            null
        }
    }

    companion object {
        const val DELETE_METHOD = 1
        const val PUT_METHOD = 2
        const val PATCH_METHOD = 3
    }
}