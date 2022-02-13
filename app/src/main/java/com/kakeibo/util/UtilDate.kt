package com.kakeibo.util

import android.os.Build
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
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

    fun getTodaysLocalDate(): LocalDate {
        return Clock.System.todayAt(TimeZone.currentSystemDefault())
    }

    fun getCurrentMoment(format: String): String {
        val cal = Calendar.getInstance()
        cal.time = Date()
        return SimpleDateFormat(format, Locale.getDefault()).format(cal.time)
    }

    fun LocalDate.getYMDDateText(format: String): String = run {
        when (format) {
            DATE_FORMAT_YMD -> { this.toString() }
            DATE_FORMAT_MDY -> { "${this.monthNumber}/${this.dayOfMonth}/${this.year}}" }
            DATE_FORMAT_DMY -> { "${this.dayOfMonth}/${this.monthNumber}/${this.year}}" }
            else -> { this.toString() }
        }
    }

    fun LocalDate.getYMDateTextFromDBFormat(format: String): String {
        return when (format) {
            /* '2021-02-11' -> '2021-02' */
            DATE_FORMAT_DB -> this.toString().substring(0, 7)
            /* '2021-02-11' -> '2021-02' */
            DATE_FORMAT_YMD -> this.toString().substring(0, 7)
            /* '11-02-2021' -> '02-2021' */
            DATE_FORMAT_DMY, DATE_FORMAT_MDY -> this.toString().substring(3)
            /* else, use DB format */
            else -> this.toString().substring(0, 7)
        }
    }

    fun LocalDate.getYDateText(): String = run {
        this.toString().substring(0, 4)
    }

    fun getLastDayOfMonth(dateString: String) : String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dateFormat: DateTimeFormatter =
                DateTimeFormatter.ofPattern(DATE_FORMAT_DB, Locale.getDefault())
            val date = java.time.LocalDate.parse(dateString, dateFormat)
            val newDate: java.time.LocalDate =
                date.withDayOfMonth(date.month.length(date.isLeapYear))
            return newDate.month.toString()
        }
        else {
            val dateFormat = SimpleDateFormat(DATE_FORMAT_DB, Locale.getDefault())
            val convertedDate: Date? = dateFormat.parse(dateString)
            convertedDate?.let {
                val c = Calendar.getInstance()
                c.time = convertedDate
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
                return c.get(Calendar.DAY_OF_MONTH).toString()
            }
            return "28"
        }
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