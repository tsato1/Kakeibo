package com.kakeibo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDex;
import androidx.preference.PreferenceManager;

import com.kakeibo.db.DBAdapter;
import com.kakeibo.db.DBHelper;

public class KkbApplication extends Application {
    private int _version;
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

    /*** num category icons per row ***/
    public static int getNumColumns(String key) {
        String numColumnsIndex = getSharedPreferences().getString(key, "4");
        String[] numColumns = _instance.getResources().getStringArray(R.array.pref_list_num_columns);
        return Integer.parseInt(numColumns[Integer.parseInt(numColumnsIndex)]);
    }

    public void setVersion(int version) {
        this._version = version;
    }

    public int getVersion() {
        return _version;
    }
}

// set
// ((MyApplication) this.getApplication()).setSomeVariable("foo");
//
// get
// String s = ((MyApplication) this.getApplication()).getSomeVariable();