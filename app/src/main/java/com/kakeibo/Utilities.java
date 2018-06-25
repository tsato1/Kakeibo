package com.kakeibo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by tsato on 9/24/16.
 */

public class Utilities {
    private static final String TAG = Utilities.class.getSimpleName();

    public static final String DATE_FORMAT_YMD = "yyyy/MM/dd";
    public static final String DATE_FORMAT_MDY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_DMY = "dd/MM/yyyy";
    public static final String[] DATE_FORMATS = {DATE_FORMAT_YMD, DATE_FORMAT_MDY, DATE_FORMAT_DMY};
    public static final String DATE_FORMAT_DB = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DB_HMS = "yyyy-MM-dd hh:mm:ss";

    public static String convertMtoMM(int calMonth) {
        String mon = String.valueOf(calMonth);
        if (String.valueOf(calMonth).length() == 1) {  // convert m to mm (ex. 5 -> 05)
            mon = "0" + String.valueOf(calMonth);
        }
        return mon;
    }

    public static String getTodaysDateWithDay(int format, String[] weekName) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault());
        sdFormat.setCalendar(cal);
        sdFormat.setTimeZone(cal.getTimeZone());
        return sdFormat.format(cal.getTime())+" [" + weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
    }

    public static String getTodaysDateWithHMS() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat(DATE_FORMAT_DB_HMS, Locale.getDefault());
        sdFormat.setCalendar(cal);
        sdFormat.setTimeZone(cal.getTimeZone());
        return sdFormat.format(cal.getTime());
    }

    public static String getDateFromDBDate(String dbDate, String[] weekName, int dateFormat) {
        String[] ymd = dbDate.split("[ ]")[0].split("[-]");

        GregorianCalendar cal = new GregorianCalendar(
                Integer.parseInt(ymd[0]),
                Integer.parseInt(ymd[1])-1,
                Integer.parseInt(ymd[2]));
        Date date = cal.getTime();
        return new SimpleDateFormat(DATE_FORMATS[dateFormat], Locale.getDefault()).format(date)
                + " [" + weekName[cal.get(Calendar.DAY_OF_WEEK)-1] + "]";
    }

    public static String convertDateFormat(String date, String fromFormat, String toFormat) {
        if (DATE_FORMAT_DB.equals(fromFormat)) {
            String[] ymd = date.split("-");

            if (DATE_FORMAT_YMD.equals(toFormat)) {
                return ymd[0] + "/" + ymd[1] + "/" + ymd[2];
            } else if (DATE_FORMAT_DMY.equals(toFormat)){
                return ymd[2] + "/" + ymd[1] + "/" + ymd[0];
            } else if (DATE_FORMAT_MDY.equals(toFormat)) {
                return ymd[1] + "/" + ymd[2] + "/" + ymd[0];
            }
        }
        if (DATE_FORMAT_DB.equals(toFormat)) {
            String[] ymd = date.split("/");

            //todo check format from sharedPreference
            String pref = DATE_FORMAT_YMD;
            if (DATE_FORMAT_YMD.equals(pref)) {
                return ymd[0] + "-" + ymd[1] + "-" + ymd[2];
            } else if (DATE_FORMAT_DMY.equals(pref)) {
                return ymd[2] + "-" + ymd[1] + "-" + ymd[0];
            } else if (DATE_FORMAT_MDY.equals(pref)) {
                return ymd[2] + "-" + ymd[0] + "-" + ymd[1];
            }
        }

        return "";
    }
}
