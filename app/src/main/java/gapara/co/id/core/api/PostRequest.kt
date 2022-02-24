package gapara.co.id.core.api

import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.FirebaseApplication
import gapara.co.id.core.base.RemoteConfigHelper
import gapara.co.id.core.model.Urls
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class PostRequest<Params>(val url: String) {

    val request = RestClient.createStringService(UrlApi::class.java, RemoteConfigHelper.getApiUrl())
    var headers = HashMap<String, String>()
    var queries = HashMap<String, String>()

    init {
        BaseApplication.sessionManager?.accessToken.takeIf { !it.isNullOrEmpty() }?.apply {
            headers[Urls.AUTHORIZATION_KEY] = "Bearer $this"
        }
        headers["Accept"] = "application/json"
    }

    suspend inline fun <reified T> post(params: Params) : T? {
        return try {
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                GSONManager.toJson(params)
            )

            val response = request.postRequestApi(RemoteConfigHelper.getApiUrl() + url, headers, queries, body)
            checkUnauthenticated(response)
            getResponse(response)
        } catch (throwable : Throwable) {
            FirebaseApplication.recordException(throwable, "$url $throwable $headers $queries ${GSONManager.toJson(params)}")
            checkConnectionTimeOut(throwable, isJustShowToast = true)
            null
        }
    }
}