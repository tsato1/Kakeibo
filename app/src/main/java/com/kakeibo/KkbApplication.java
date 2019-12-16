package com.kakeibo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.kakeibo.db.DBAdapter;
import com.kakeibo.db.DBHelper;

public class KkbApplication extends Application {
    private int _version;
    private DBHelper dbHelper;

    public static KkbApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        MultiDex.install(this);

        dbHelper = new DBHelper(this.getApplicationContext());
        DBAdapter.initInstance(dbHelper);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static KkbApplication getInstance() {
        return sInstance;
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