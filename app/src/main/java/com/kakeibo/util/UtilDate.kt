package com.kakeibo.util

import android.util.Log
import com.kakeibo.R
import com.kakeibo.SubApp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tsato on 9/24/16.
 */
object UtilDate {

    private val TAG = UtilDate::class.java.simpleName

    const val DATE_FORMAT_YMD = "yyyy/MM/dd"
    const val DATE_FORMAT_MDY = "MM/dd/yyyy"
    const val DATE_FORMAT_DMY = "dd/MM/yyyy"
    const val DATE_FORMAT_DB = "yyyy-MM-dd"
    const val DATE_FORMAT_DB_HMS = "yyyy-MM-dd HH:mm:ss" // old UpdatedDate
    const val DATE_FORMAT_DB_KMS = "yyyy-MM-dd kk:mm:ss"

    val DATE_FORMATS = arrayOf(DATE_FORMAT_YMD, DATE_FORMAT_MDY, DATE_FORMAT_DMY, DATE_FORMAT_DB, DATE_FORMAT_DB_HMS)

    fun getTodaysDateWithDay(format: Int, weekName: Array<String>): String {
        val cal = Calendar.getInstance()
        val sdFormat = SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault())
        sdFormat.calendar = cal
        sdFormat.timeZone = cal.timeZone
        return sdFormat.format(cal.time) + " [" + weekName[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
    }

    fun getTodaysDate(format: String): String {
        val cal = Calendar.getInstance()
        val sdFormat = SimpleDateFormat(format, Locale.getDefault())
        sdFormat.calendar = cal
        sdFormat.timeZone = cal.timeZone
        return sdFormat.format(cal.time)
    }

    fun getTodaysDate(format: Int): String {
        val cal = Calendar.getInstance()
        val sdFormat = SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault())
        sdFormat.calendar = cal
        sdFormat.timeZone = cal.timeZone
        return sdFormat.format(cal.time)
    }

    fun getTodaysY(): String {
        return getTodaysDate(DATE_FORMAT_DB).substring(0, 4)
    }

    fun getTodaysYM(format: String): String {
        return when (format) {
            DATE_FORMAT_DB -> getTodaysDate(DATE_FORMAT_DB).substring(0, 7) /* '2021-02-11' -> '2021-02' */
            DATE_FORMAT_YMD -> getTodaysDate(DATE_FORMAT_YMD).substring(0, 7) /* '2021-02-11' -> '2021-02' */
            DATE_FORMAT_DMY, DATE_FORMAT_MDY -> getTodaysDate(DATE_FORMAT_DMY).substring(3) /* '11-02-2021' -> '02-2021' */
            else -> getTodaysDate(DATE_FORMAT_DB).substring(0, 7)
        }
    }

    fun getTodaysYM(format: Int): String {
        return getTodaysYM(DATE_FORMATS[format])
    }

    fun getDateWithDayFromDBDate(dbDate: String, weekName: Array<String>, dateFormat: String): String {
        val ymd = dbDate.split(" ")[0].split("-")
        val cal = GregorianCalendar(ymd[0].toInt(),
                ymd[1].toInt() - 1, ymd[2].toInt())
        val date = cal.time
        return (SimpleDateFormat(dateFormat, Locale.getDefault()).format(date)
                + " [" + weekName[cal[Calendar.DAY_OF_WEEK] - 1] + "]")
    }

    fun getDateWithDayFromDBDate(dbDate: String, weekName: Array<String>, dateFormat: Int) : String {
        return getDateWithDayFromDBDate(dbDate, weekName, DATE_FORMATS[dateFormat])
    }

    fun getDateFromDBDate(dbDate: String, dateFormat: Int): String {
        Log.d(TAG, "getDateWithDayFromDBDate() dbDate: $dbDate")
        val ymd = dbDate.split("-")
        val cal = GregorianCalendar(ymd[0].toInt(),
                ymd[1].toInt() - 1, ymd[2].toInt())
        val date = cal.time
        return SimpleDateFormat(DATE_FORMATS[dateFormat], Locale.getDefault()).format(date)
    }

    fun getDBDate(date: String, fromFormat: Int): String {
        Log.d(TAG, "date=$date")
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
    fun compareDates(in1: String, in2: String, format: Int = 3): Int { // format=3 is FORMAT_DB
        try {
            val formatter = SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault())
            val date1 = formatter.parse(in1)
            val date2 = formatter.parse(in2)
            date1?.let {
                return when {
                    date1.before(date2) -> 1
                    date1.after(date2) -> -1
                    date1 == date2 -> 0
                    else -> 0
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }




    /*
     used in import
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