package com.kakeibo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDex;
import androidx.preference.PreferenceManager;

import com.kakeibo.db.DBAdapter;
import com.kakeibo.db.DBHelper;
import com.kakeibo.util.UtilDate;

import java.util.Currency;
import java.util.Locale;

public class KkbApplication extends Application {

    private static SharedPreferences _preferences;

    public static KkbApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        MultiDex.install(this);

        PreferenceManager.setDefaultValues(_instance, R.xml.preferences, false);
        _preferences = PreferenceManager.getDefaultSharedPreferences(_instance);

        DBHelper dbHelper = new DBHelper(this.getApplicationContext());
        DBAdapter.initInstance(dbHelper);
    }

    @Override
    public Context getApplicationContext() { return super.getApplicationContext(); }

    public static KkbApplication getInstance() { return _instance; }

    public static SharedPreferences getSharedPreferences() {
        if (_preferences == null) {
            PreferenceManager.setDefaultValues(_instance, R.xml.preferences, false);
            _preferences = PreferenceManager.getDefaultSharedPreferences(_instance);
        }
        return _preferences;
    }

    /*** dateFormat ***/
    public static int getDateFormat(int key) {
        String strKey = _instance.getString(key);
        String dateFormatIndex = getSharedPreferences().getString(strKey, UtilDate.DATE_FORMAT_YMD);
        return Integer.parseInt(dateFormatIndex);
    }

    public static int getFractionDigits(int key) {
        /*** fraction digits ***/
        String strKey = _instance.getString(key);

        Locale locale = Locale.getDefault();
        int defValue = 0;
        try {
            Currency currency = Currency.getInstance(locale);
            defValue = currency.getDefaultFractionDigits();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        String digitsIndex = getSharedPreferences().getString(strKey, ""+defValue);
        String[] fractionDigits = _instance.getResources().getStringArray(R.array.pref_list_fraction_digits);
        return Integer.parseInt(fractionDigits[Integer.parseInt(digitsIndex)]);
    }

    /*** num category icons per row ***/
    public static int getNumColumns(int key) {
        String strKey = _instance.getString(key);
        String numColumnsIndex = getSharedPreferences().getString(strKey, "1");
        String[] numColumns = _instance.getResources().getStringArray(R.array.pref_list_num_columns);
        return Integer.parseInt(numColumns[Integer.parseInt(numColumnsIndex)]);
    }
}

// set
// ((MyApplication) this.getApplication()).setSomeVariable("foo");
//
// get
// String s = ((MyApplication) this.getApplication()).getSomeVariable();