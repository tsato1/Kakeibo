package com.kakeibo.util

import android.content.Context
import android.os.Build
import com.kakeibo.R
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by tsato on 9/24/16.
 */
object UtilDate {

    const val DATE_FORMAT_YMD = "yyyy/MM/dd"
    const val DATE_FORMAT_MDY = "MM/dd/yyyy"
    const val DATE_FORMAT_DMY = "dd/MM/yyyy"
    const val DATE_FORMAT_DB = "yyyy-MM-dd"
    const val DATE_FORMAT_DB_HMS = "yyyy-MM-dd HH:mm:ss" // old UpdatedDate
    const val DATE_FORMAT_DB_KMS = "yyyy-MM-dd kk:mm:ss"

    val DATE_FORMATS = arrayOf(DATE_FORMAT_YMD, DATE_FORMAT_MDY, DATE_FORMAT_DMY)

    fun getCurrentMoment(format: String): String {
        val cal = Calendar.getInstance()
        cal.time = Date()
        return SimpleDateFormat(format, Locale.getDefault()).format(cal.time)
    }

    fun Calendar.toYMDWString(format: String, context: Context): String = run {
        val dayOfWeekIdx = this.get(Calendar.DAY_OF_WEEK) - 1
        val dayOfWeek = context.resources.getStringArray(R.array.week_name)[dayOfWeekIdx % 7]

        return when (format) {
            DATE_FORMAT_YMD -> { "${this.get(Calendar.YEAR)}/${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.DAY_OF_MONTH)} [${dayOfWeek}]" }
            DATE_FORMAT_MDY -> { "${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.DAY_OF_MONTH)}/${this.get(Calendar.YEAR)} [${dayOfWeek}]" }
            DATE_FORMAT_DMY -> { "${this.get(Calendar.DAY_OF_MONTH)}/${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.YEAR)} [${dayOfWeek}]" }
            DATE_FORMAT_DB -> {
                val m = if (this.get(Calendar.MONTH) + 1 < 10) "0${this.get(Calendar.MONTH) + 1}" else this.get(Calendar.MONTH) + 1
                val d = if (this.get(Calendar.DAY_OF_MONTH) < 10) "0${this.get(Calendar.DAY_OF_MONTH)}" else this.get(Calendar.DAY_OF_MONTH)
                "${this.get(Calendar.YEAR)}-$m-$d"
            }
            else -> { this.toString() }
        }
    }

    fun Calendar.toYMDString(format: String): String = run {
        when (format) {
            DATE_FORMAT_YMD -> { "${this.get(Calendar.YEAR)}/${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.DAY_OF_MONTH)}" }
            DATE_FORMAT_MDY -> { "${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.DAY_OF_MONTH)}/${this.get(Calendar.YEAR)}" }
            DATE_FORMAT_DMY -> { "${this.get(Calendar.DAY_OF_MONTH)}/${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.YEAR)}" }
            DATE_FORMAT_DB -> {
                val m = if (this.get(Calendar.MONTH) + 1 < 10) "0${this.get(Calendar.MONTH) + 1}" else this.get(Calendar.MONTH) + 1
                val d = if (this.get(Calendar.DAY_OF_MONTH) < 10) "0${this.get(Calendar.DAY_OF_MONTH)}" else this.get(Calendar.DAY_OF_MONTH)
                "${this.get(Calendar.YEAR)}-$m-$d"
            }
            else -> { this.toString() }
        }
    }

    fun Calendar.toYMString(format: String): String = run {
        when (format) {
            DATE_FORMAT_DB -> { "${this.get(Calendar.YEAR)}-${this.get(Calendar.MONTH) + 1}" } /* '2021-02-11' -> '2021-02' */
            DATE_FORMAT_YMD -> { "${this.get(Calendar.YEAR)}/${this.get(Calendar.MONTH) + 1}" } /* '2021-02-11' -> '2021/02' */
            DATE_FORMAT_DMY, DATE_FORMAT_MDY -> { "${this.get(Calendar.MONTH) + 1}/${this.get(Calendar.YEAR)}" } /* '11-02-2021' -> '02/2021' */
            else -> { this.toString() }
        }
    }

    fun Calendar.of(year: Int, month: Int, day: Int): Calendar = run {
        this.set(year, month, day)
        return this
    }

    fun Calendar.plus(unit: Int, value: Int): Calendar = run {
        this.add(unit, value)
        return this
    }

    fun Calendar.isSameDateAs(calendar: Calendar) = run {
        this.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                this.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                this.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
    }

    /*
     expects this string to be in UtilDate.DATE_FORMAT_DB
     */
    fun String.toCalendar(): Calendar = run {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(DATE_FORMAT_DB)
        dateFormat.parse(this)?.let { cal.time = it }
        cal
    }

    /*
     returns 0: Sunday, 6: Saturday, expects db format
     */
    fun getFirstDayOfMonth(dateString: String, format: String): Int {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val dateFormat: DateTimeFormatter =
//                DateTimeFormatter.ofPattern(format, Locale.getDefault())
//            val parsedDate = LocalDate.parse(dateString, dateFormat)
//            parsedDate.withDayOfMonth(1).dayOfWeek.value % 7 // originally 1: Monday, 7: Sunday hence %7
//        }
//        else {
        return run {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            val convertedDate: Date? = dateFormat.parse(dateString)
            convertedDate?.let {
                val c = Calendar.getInstance()
                c.time = it
                c.set(Calendar.DAY_OF_MONTH, 1)
                c.get(Calendar.DAY_OF_WEEK) - 1// originally 1: Sunday, 7: Saturday hence -1
            } ?: 1
        }
    }

    fun getLastDateOfMonth(dateString: String) : Int {
        val date = LocalDate.parse(dateString)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateFormat: DateTimeFormatter =
                DateTimeFormatter.ofPattern(DATE_FORMAT_DB, Locale.getDefault())
            val parsedDate = LocalDate.parse(dateString, dateFormat)
            parsedDate.withDayOfMonth(date.month.length(parsedDate.isLeapYear)).dayOfMonth
        }
        else {
            val dateFormat = SimpleDateFormat(DATE_FORMAT_DB, Locale.getDefault())
            val convertedDate: Date? = dateFormat.parse(dateString)
            convertedDate?.let {
                val c = Calendar.getInstance()
                c.time = it
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
                c.get(Calendar.DAY_OF_MONTH)
            } ?: 28
        }
    }

    /*
    returns the number of days from the last date of the searching month
    to fill in the calendar result
     */
    fun getRemainingDays(dateString: String): Int {
        return 13
//        val date = LocalDate.parse(dateString)
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val firstDayOfMonth = getFirstDayOfMonth(dateString)
//            val lastDateOfMotnh = getLastDateOfMonth(dateString)
//            if(lastDateOfMotnh / 4 - firstDayOfMonth >= 0) {
//                14 - LocalDate.of(date.year, date.monthValue, lastDateOfMotnh).dayOfWeek.value
//            }
//            else {
//                6
//            }
//        }
//        else {
//            13 //todo: decide whether to read one week or two weeks
//        }
    }

    fun getDBDate(date: String, fromFormat: Int): String {
        val ymd = date.split("/")
        val y: String
        val m: String
        val d: String
        when (fromFormat) {
            1 -> {
                y = ymd[2]
                m = ymd[0]
                d = ymd[1]
            }
            2 -> {
                y = ymd[2]
                m = ymd[1]
                d = ymd[0]
            }
            else -> {
                y = ymd[0]
                m = ymd[1]
                d = ymd[2]
            }
        }
        return "$y-$m-$d"
    }

    /*** date1 < date2 returns -1
     * date1 = date2 return 0
     * date1 > date2 return 1  */
//    fun compareDates(in1: String, in2: String, format: Int = 3): Int { // format=3 is FORMAT_DB
//        try {
//            val formatter = SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault())
//            val date1 = formatter.parse(in1)
//            val date2 = formatter.parse(in2)
//            date1?.let {
//                return when {
//                    date1.before(date2) -> 1
//                    date1.after(date2) -> -1
//                    date1 == date2 -> 0
//                    else -> 0
//                }
//            }
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return 0
//    }

    /*
    below code: used in import
     */
    fun isYMDDateValid(date: String): Boolean {
        if (date.length != 10) {
            return false
        }

        for ((i, c) in date.withIndex()) { // yyyy/MM/dd
            if (i == 4 || i == 7)
                if (c != '-')
                    return false
                else
                    if (!c.isDigit())
                        return false
        }

        val ymd = date.split("-") // yyyy/MM/dd
        if (ymd[0].toInt() < 1900 && 9999 < ymd[0].toInt())
            return false
        if (ymd[1].toInt() < 1 && 12 < ymd[1].toInt())
            return false
        if (ymd[2].toInt() < 1 && 31 < ymd[2].toInt())
            return false

        return true
    }

    fun isYMDHSDateValid(date: String): Boolean { //yyyy-MM-dd HH:mm:ss
        if (!date.contains(" "))
            return false

        val line = date.split(" ")
        if (!isYMDDateValid(line[0]))
            return false

        if (line[1].length != 7 && line[1].length != 8)
            return false

        for (i in 5 downTo 0) { // HH:mm:ss
            if (i == line[1].length - 3 || i == line[1].length - 6)
                if (line[1][i] != ':')
                    return false
                else
                    if (!line[1][i].isDigit())
                        return false
        }

        val hms = line[1].split(":")
        if (hms[0].toInt() < 0 || 23 < hms[0].toInt())
            return false
        if (hms[1].toInt() < 0 || 59 < hms[1].toInt())
            return false
        if (hms[2].toInt() < 0 || 59 < hms[2].toInt())
            return false

        return true
    }
}