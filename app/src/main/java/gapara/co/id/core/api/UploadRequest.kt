package gapara.co.id.core.api

import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.FirebaseApplication
import gapara.co.id.core.base.RemoteConfigHelper
import gapara.co.id.core.model.Urls
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadRequest(val url: String) {

    val request = RestClient.createStringService(UrlApi::class.java, RemoteConfigHelper.getApiUrl())
    var headers = HashMap<String, String>()
    var queries = HashMap<String, String>()

    init {
        BaseApplication.sessionManager?.accessToken.takeIf { !it.isNullOrEmpty() }?.apply {
            headers[Urls.AUTHORIZATION_KEY] = "Bearer $this"
        }
        headers["Accept"] = "application/json"
    }

    suspend inline fun <reified T> upload(files: HashMap<String, File>, names : HashMap<String, String>) : T? {
        try {
            val params = ArrayList<MultipartBody.Part>()

            files.toList().forEach {
                val typeFile = it.second.absolutePath.split(".").last()
                val requestBodyFile = RequestBody.create(typeFile.toMediaTypeOrNull(), it.second)
                val bodyFile = MultipartBody.Part.createFormData(it.first, it.second.name, requestBodyFile)
                params.add(bodyFile)
            }

            names.toList().forEach {
                val bodyName = MultipartBody.Part.createFormData(it.first, it.second)
                params.add(bodyName)
            }

            val response = request.uploadMedia(RemoteConfigHelper.getApiUrl() + url, headers, queries, params)
            checkUnauthenticated(response)
            return getResponse(response)
        } catch (throwable: Throwable) {
            FirebaseApplication.recordException(throwable, "$url $throwable $headers $queries $files $names")
            checkConnectionTimeOut(throwable, isJustShowToast = true)
            return null
        }
    }
}