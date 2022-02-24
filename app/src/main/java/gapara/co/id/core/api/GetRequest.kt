package gapara.co.id.core.api

import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.FirebaseApplication
import gapara.co.id.core.base.RemoteConfigHelper
import gapara.co.id.core.model.Urls

class GetRequest(val url: String) {

    val request = RestClient.createStringService(UrlApi::class.java, RemoteConfigHelper.getApiUrl())
    var headers = HashMap<String, String>()
    var queries = HashMap<String, String>()

    init {
        BaseApplication.sessionManager?.accessToken.takeIf { !it.isNullOrEmpty() }?.apply {
            headers[Urls.AUTHORIZATION_KEY] = "Bearer $this"
        }
        headers["Accept"] = "application/json"
    }

    suspend inline fun <reified T> get() : T? {
        return try {
            val response = request.getRequestApi(RemoteConfigHelper.getApiUrl() + url, headers, queries)
            checkUnauthenticated(response)
            getResponse(response)
        } catch (throwable : Throwable) {
            FirebaseApplication.recordException(throwable, "$url $throwable $headers $queries")
            checkConnectionTimeOut(throwable)
            null
        }
    }
}