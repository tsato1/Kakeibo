package com.kakeibo.util

import android.util.Log
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
    const val DATE_FORMAT_DB_HMS = "yyyy-MM-dd kk:mm:ss"

    val DATE_FORMATS = arrayOf(DATE_FORMAT_YMD, DATE_FORMAT_MDY, DATE_FORMAT_DMY, DATE_FORMAT_DB, DATE_FORMAT_DB_HMS)

    fun convertMtoMM(calMonth: Int): String {
        var mon = calMonth.toString()
        if (calMonth.toString().length == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0$calMonth"
        }
        return mon
    }

    fun getTodaysDateWithDay(format: Int, weekName: Array<String>): String {
        val cal = Calendar.getInstance()
        val sdFormat = SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault())
        sdFormat.calendar = cal
        sdFormat.timeZone = cal.timeZone
        return sdFormat.format(cal.time) + " [" + weekName[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
    }

    fun getTodaysDate(format: String?): String {
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

    fun getDateWithDayFromDBDate(dbDate: String, weekName: Array<String>, dateFormat: Int): String {
        val ymd = dbDate.split("[ ]").toTypedArray()[0].split("[-]").toTypedArray()
        val cal = GregorianCalendar(ymd[0].toInt(),
                ymd[1].toInt() - 1, ymd[2].toInt())
        val date = cal.time
        return (SimpleDateFormat(DATE_FORMATS[dateFormat], Locale.getDefault()).format(date)
                + " [" + weekName[cal[Calendar.DAY_OF_WEEK] - 1] + "]")
    }

    fun getDateFromDBDate(dbDate: String, dateFormat: Int): String {
        Log.d(TAG, "getDateWithDayFromDBDate() dbDate: $dbDate")
        val ymd = dbDate.split("[ ]").toTypedArray()[0].split("[-]").toTypedArray()
        val cal = GregorianCalendar(ymd[0].toInt(),
                ymd[1].toInt() - 1, ymd[2].toInt())
        val date = cal.time
        return SimpleDateFormat(DATE_FORMATS[dateFormat], Locale.getDefault()).format(date)
    }

    /*** date1 < date2 returns -1
     * date1 = date2 return 0
     * date1 > date2 return 1  */
    fun compareDate(in1: String, in2: String, format: Int): Int {
        try {
            val formatter = SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault())
            val date1 = formatter.parse(in1)
            val date2 = formatter.parse(in2)
            if (date1.before(date2)) return 1 else if (date1.after(date2)) return -1 else if (date1 == date2) return 0
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun convertDateFormat(date: String, fromFormat: Int, toFormat: Int): String {
        var out = ""
        if (fromFormat == toFormat) return date
        if ((fromFormat == 3 || fromFormat == 4) && (toFormat == 0 || toFormat == 1 || toFormat == 2)) {
            val ymd = date.split("-").toTypedArray()

//            if (DATE_FORMAT_YMD.equals(toFormat)) {
//                return ymd[0] + "/" + ymd[1] + "/" + ymd[2];
//            } else if (DATE_FORMAT_DMY.equals(toFormat)){
//                return ymd[2] + "/" + ymd[1] + "/" + ymd[0];
//            } else if (DATE_FORMAT_MDY.equals(toFormat)) {
//                return ymd[1] + "/" + ymd[2] + "/" + ymd[0];
//            }
        } else if ((fromFormat == 0 || fromFormat == 1 || fromFormat == 2) && (toFormat == 3 || toFormat == 4)) {
            /*** toFormat==3  */
            val ymd = date.split("/").toTypedArray()
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
            out = "$y-$m-$d"
        }
        return out
    }
}