package gapara.co.id.core.base

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

object TimeHelper {

    var defaultLocale = Locale("id")
    const val FORMAT_DATE_FULL = "yyyy-MM-dd HH:mm:ss"

    const val FORMAT_DATE_SERVER = "yyyy-MM-dd"
    const val FORMAT_DATE_TEXT= "dd MMMM yyyy"

    const val FORMAT_DATE_FULL_TEXT = "dd MMMM yyyy HH:mm:ss"

    const val FORMAT_DATE_FULL_DAY = "EEEE, dd MMM yyyy"

    fun timestampToText(time: String?, format : String = FORMAT_DATE_FULL): String {
        if (time.isNullOrBlank()) { return "" }

        val realFormatDate = SimpleDateFormat(format, defaultLocale)
        val timestamp : Long
        try {
            val date = realFormatDate.parse(time)
            timestamp = (date?.time ?: 0 ) / 1000
        } catch (e : Exception) {
            e.printStackTrace()
            FirebaseApplication.recordException(Throwable("$e"), "date error ${e.message}")
            return time
        }

        val currentTime = System.currentTimeMillis() / 1000
        val diff = currentTime - timestamp
        val text: String
        text = when {
            diff < 60 -> {
                "$diff detik yang lalu"
            }
            diff < 60 * 60 -> {
                (floor(diff / 60.toDouble()).toInt()).toString() + " menit yang lalu"
            }
            diff < 60 * 60 * 24 -> {
                floor(diff / (60 * 60).toDouble()).toInt().toString() + " jam yang lalu"
            }
            diff < 60 * 60 * 24 * 30 -> {
                floor(diff / (60 * 60 * 24).toDouble()).toInt().toString() + " hari yang lalu"
            }
            diff < 60 * 60 * 24 * 30 * 365 -> {
                floor(diff / (60 * 60 * 24 * 30).toDouble()).toInt().toString() + " bulan yang lalu"
            }
            else -> {
                (floor(diff / (60 * 60 * 24 * 30 * 365).toDouble()).toInt().toString() + " tahun yang lalu")
            }
        }
        return text
    }

    fun convertDateText(value: String?, formatTo: String, formatFrom: String = FORMAT_DATE_SERVER): String {
        if (value?.isNotEmpty() == true) {
            return try {
                val serverFormat = SimpleDateFormat(formatFrom, defaultLocale)
                val date = serverFormat.parse(value)

                val calendarFormat = SimpleDateFormat(formatTo, defaultLocale)
                calendarFormat.format(date)
            } catch (e : Exception) {
                e.printStackTrace()
                FirebaseApplication.recordException(Throwable("$e"), "date error ${e.message}")
                value
            }
        }
        return ""
    }

    fun getDate(day : Int = 0, month : Int = 0, year : Int = 0) : Date {
        val calendar = Calendar.getInstance()
        if (day > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
        if (month > 0) {
            calendar.set(Calendar.MONTH, month)
        }
        if (year > 0) {
            calendar.set(Calendar.YEAR, year)
        }
        return calendar.time
    }

    fun convertToFormatDateSever(date: Date, format : String = FORMAT_DATE_SERVER) : String {
        val serverFormat = SimpleDateFormat(format, defaultLocale)
        return serverFormat.format(date)
    }

    private fun getTime(normalDate: List<String>?): String {
        return normalDate?.lastOrNull()?.split(".")?.firstOrNull() ?: "${normalDate?.firstOrNull()}"
    }

    private fun splitTimeServer(date : String?) : List<String> {
        if (date?.contains("T", ignoreCase = false) == true) {
            return date.split("T", ignoreCase = true)
        } else {
            return date?.split(" ", ignoreCase = true) ?: arrayListOf()
        }
    }

    fun getHourServer(createdAt: String?): String? {
        return getTime(splitTimeServer(createdAt))
    }

    fun getDateServer(createdAt: String?): String {
        return splitTimeServer(createdAt).firstOrNull() ?: "-"
    }

    fun getDateByTimestamp(timestamp : Long) : Date {
        val calendar = Calendar.getInstance()
        if (timestamp > 0) {
            calendar.time = Date(timestamp)
        }
        return calendar.time
    }

    fun getToday(format: String) : String {
        val formatter = SimpleDateFormat(format, defaultLocale)
        return formatter.format(Calendar.getInstance().time)
    }
}