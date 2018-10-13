package com.kakeibo.util;

import android.util.Log;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by tsato on 9/24/16.
 */

public class UtilDate {
    private static final String TAG = UtilDate.class.getSimpleName();

    public static final String DATE_FORMAT_YMD = "yyyy/MM/dd";
    public static final String DATE_FORMAT_MDY = "MM/dd/yyyy";
    public static final String DATE_FORMAT_DMY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_DB = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DB_HMS = "yyyy-MM-dd kk:mm:ss";
    public static final String[] DATE_FORMATS = {DATE_FORMAT_YMD, DATE_FORMAT_MDY, DATE_FORMAT_DMY, DATE_FORMAT_DB, DATE_FORMAT_DB_HMS};

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

    public static String getTodaysDate(String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat(format, Locale.getDefault());
        sdFormat.setCalendar(cal);
        sdFormat.setTimeZone(cal.getTimeZone());
        return sdFormat.format(cal.getTime());
    }

    public static String getTodaysDate(int format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault());
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

    public static String getDateFromDBDate(String dbDate, int dateFormat) {
        Log.d(TAG, "getDateFromDBDate() dbDate: " + dbDate);

        String[] ymd = dbDate.split("[ ]")[0].split("[-]");

        GregorianCalendar cal = new GregorianCalendar(
                Integer.parseInt(ymd[0]),
                Integer.parseInt(ymd[1])-1,
                Integer.parseInt(ymd[2]));
        Date date = cal.getTime();
        return new SimpleDateFormat(DATE_FORMATS[dateFormat], Locale.getDefault()).format(date);
    }

    /*** date1 < date2 returns -1
     *   date1 = date2 return 0
     *   date1 > date2 return 1 ***/
    public static int compareDate(String in1, String in2, int format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATS[format], Locale.getDefault());
            Date date1 = formatter.parse(in1);
            Date date2 = formatter.parse(in2);
            if (date1.before(date2)) return 1;
            else if (date1.after(date2)) return -1;
            else if (date1.equals(date2)) return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String convertDateFormat(String date, int fromFormat, int toFormat) {
        String out="";

        if ((fromFormat==3 || fromFormat==4) && (toFormat==0 || toFormat==1 || toFormat==2)) {
            String[] ymd = date.split("-");

//            if (DATE_FORMAT_YMD.equals(toFormat)) {
//                return ymd[0] + "/" + ymd[1] + "/" + ymd[2];
//            } else if (DATE_FORMAT_DMY.equals(toFormat)){
//                return ymd[2] + "/" + ymd[1] + "/" + ymd[0];
//            } else if (DATE_FORMAT_MDY.equals(toFormat)) {
//                return ymd[1] + "/" + ymd[2] + "/" + ymd[0];
//            }
        }
        else if ((fromFormat==0 || fromFormat==1 || fromFormat==2) && (toFormat==3 || toFormat==4)) {
            /*** toFormat==3 ***/
            String[] ymd = date.split("/");
            String y, m, d;

            switch (fromFormat) {
                case 1: // MDY
                    y = ymd[2];
                    m = ymd[0];
                    d = ymd[1];
                    break;
                case 2: // DMY
                    y = ymd[2];
                    m = ymd[1];
                    d = ymd[0];
                    break;
                default:  // YMD
                    y = ymd[0];
                    m = ymd[1];
                    d = ymd[2];
            }

            out = y + "-" + m + "-" + d;
        }

        return out;
    }
}