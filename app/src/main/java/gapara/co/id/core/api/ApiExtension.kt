package gapara.co.id.core.api

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.bind.DateTypeAdapter
import com.google.gson.reflect.TypeToken
import gapara.co.id.BuildConfig
import gapara.co.id.core.api.response.BaseResponse
import gapara.co.id.core.base.BaseApplication
import gapara.co.id.core.base.showLongRedToast
import org.json.JSONObject
import java.util.*

fun log(message: String) {
    if (BuildConfig.DEBUG) {
        val desc =
            "[" + Exception().stackTrace[1].className + "] " +
                    "[" + Exception().stackTrace[1].lineNumber + "] " +
                    "{" + Exception().stackTrace[1].methodName + "} "

        if (message.length > 500) {
            try {
                val maxLogSize = 500
                for (i in 0..message.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > message.length) message.length else end
                    Log.e(":", desc + " => " + message.substring(start, end))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("log Exception", desc + " => " + e.message)
            }
        } else {
            Log.e(":", "$desc => $message")
        }
    }
}

class GSONManager {
    companion object {
        private val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Date::class.java, DateTypeAdapter()).create()

        fun <T> fromJson(json: String, kelas: Class<T>): T {
            return gson.fromJson(json, kelas)
        }

        fun <T> fromJson(json: JSONObject, kelas: Class<T>): T {
            return fromJson(json.toString(), kelas)
        }

        fun <T> toJson(entity: T): String {
            return gson.toJson(entity)
        }
    }
}

inline fun <reified T> getResponse(response: String): T {
    val type = object : TypeToken<T>() {}.type
    return Gson().fromJson(response, type)
}

fun isUnauthenticated(message: String? = null) : Boolean {
    return message?.contains("Unauthenticated", ignoreCase = true) == true
}

fun checkUnauthenticated(response : String) {
    val message = getResponse<BaseResponse>(response)
    BaseApplication.baseApplication?.isShowTokenExpiredDialog?.value = isUnauthenticated(message.message)
}

fun checkConnectionTimeOut(throwable : Throwable, isJustShowToast : Boolean = false) {
    if ("$throwable".contains("HTTP 504", ignoreCase = true) ||
        "$throwable".contains("HTTP 502", ignoreCase = true) ||
        "$throwable".contains("HTTP 520", ignoreCase = true) ||
        "$throwable".contains("Connection timed out", ignoreCase = true)) {

        if (isJustShowToast) {
            BaseApplication.context?.showLongRedToast("Connection timed out")
            return
        }

        BaseApplication.baseApplication?.isTryAgainConnectionDialog?.value = true
    }
}