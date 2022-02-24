package gapara.co.id.core.api

import gapara.co.id.core.base.getDetailReportDevice
import gapara.co.id.core.model.ApiReportModel
import gapara.co.id.core.model.Urls
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import kotlin.random.Random

object ReportRequest {

    private val request = RestClient.createStringService(UrlApi::class.java, Urls.API_REPORT)
    private const val maxLength = 4065

    private suspend fun post(text: String) {
        try {
            var realText = text
            if (realText.length > maxLength) {
                realText = realText.substring(0, maxLength)
            }

            val params = ApiReportModel(chatId = Urls.CHAT_ID, text = realText)
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                GSONManager.toJson(params)
            )
            val map = HashMap<String, String>()
            request.postRequestApi(Urls.API_REPORT + Urls.API_BOT, map, map, body)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    suspend fun postWithDeviceDetail(text: String) {
        val reportId = Random.nextLong()
        post("#$reportId ${getDetailReportDevice()}")
        post("#$reportId $text")
    }
}