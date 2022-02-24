package gapara.co.id.core.base

import android.app.DatePickerDialog
import android.content.Context
import gapara.co.id.R

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DialogCalender(private val context: Context?) {
    private var isStartYear1990 = false
    private var isStartYear1980 = false

    private var datePickerDialog: DatePickerDialog? = null

    fun setDisablePreviousDate() : DialogCalender {
        datePickerDialog?.datePicker?.minDate = System.currentTimeMillis()
        return this
    }

    fun setupDialog(onClicked: (String) -> Unit, format_date: String, themeId: Int? = null) : DialogCalender {
        val newCalendar = Calendar.getInstance()

        if (isStartYear1990) {
            newCalendar.set(1990, 1, 1)
        } else if (isStartYear1980) {
            newCalendar.set(1980, 1, 1)
        }

        if (context == null) {
            return this
        }

        var theme = themeId ?: 0
        if (theme == 0) {
            theme = R.style.DialogDateTheme
        }

        datePickerDialog = DatePickerDialog(
            context,
            theme,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, dayOfMonth)
                val formatAPI = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                if (format_date == ydm_human_like) {
                    val fmtHari = SimpleDateFormat("EEE")
                    val dt =
                        fmtHari.format(newDate.time) + ", " + newDate.get(Calendar.DAY_OF_MONTH) + " " +
                                "" + nameMonthYear(newDate.get(Calendar.MONTH)) + " " + newDate.get(
                            Calendar.YEAR
                        )
                    val DATE = newDate.get(Calendar.YEAR)
                        .toString() + "-" + (newDate.get(Calendar.MONTH) + 1) + "-" + newDate.get(
                        Calendar.DAY_OF_MONTH
                    )

                    onClicked("$dt|$DATE")
                } else if (format_date == human_like) {
                    val fmtHari = SimpleDateFormat("EEE")
                    val dt =
                        fmtHari.format(newDate.time) + ", " + newDate.get(Calendar.DAY_OF_MONTH) + " " +
                                "" + nameMonthYear(newDate.get(Calendar.MONTH)) + " " + newDate.get(
                            Calendar.YEAR
                        )
                    onClicked(dt)
                } else if (format_date == year_day_month) {
                    val DATE = newDate.get(Calendar.YEAR)
                        .toString() + "-" + (newDate.get(Calendar.MONTH) + 1) + "-" + newDate.get(
                        Calendar.DAY_OF_MONTH
                    )
                    onClicked(DATE)
                } else if (format_date == year_day_month2) {
                    onClicked(formatAPI.format(newDate.time))
                }
            },
            newCalendar.get(Calendar.YEAR),
            newCalendar.get(Calendar.MONTH),
            newCalendar.get(Calendar.DAY_OF_MONTH)
        )

        return this
    }

    fun showDialog() : DialogCalender {
        datePickerDialog?.show()
        return this
    }

    fun setIsStartYear1990() {
        isStartYear1990 = true
    }

    fun setIsStartYear1980() {
        isStartYear1980 = true
    }

    companion object {

        /**
         * format year_day_month : 1990-3-31 (YYYY-MM-DD)
         * format year_day_month2 : 1990-03-31 (YYYY-M-DD)
         * format human_like : Tue, Des 2017
         * format ydm_human_like :  Tue, Des 2017|1990-2-12
         */
        var year_day_month = "year_day_month"
        var year_day_month2 = "year_day_month2"
        var human_like = "human_like"
        var ydm_human_like = "ydm_human_like"
    }

    fun nameMonthYear(calendar: Int): String {
        return if (calendar == Calendar.JANUARY) {
            "Jan"
        } else if (calendar == Calendar.FEBRUARY) {
            "Feb"
        } else if (calendar == Calendar.MARCH) {
            "Mar"
        } else if (calendar == Calendar.APRIL) {
            "Apr"
        } else if (calendar == Calendar.MAY) {
            "Mei"
        } else if (calendar == Calendar.JUNE) {
            "Jun"
        } else if (calendar == Calendar.JULY) {
            "Jul"
        } else if (calendar == Calendar.AUGUST) {
            "Agt"
        } else if (calendar == Calendar.SEPTEMBER) {
            "Sep"
        } else if (calendar == Calendar.OCTOBER) {
            "Okt"
        } else if (calendar == Calendar.NOVEMBER) {
            "Nov"
        } else if (calendar == Calendar.DECEMBER) {
            "Des"
        } else {
            ""
        }
    }
}