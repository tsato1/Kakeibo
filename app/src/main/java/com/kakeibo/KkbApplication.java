package com.kakeibo;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.kakeibo.db.DBAdapter;
import com.kakeibo.db.DBHelper;

public class KkbApplication extends Application {

    private int _version;
    private DBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        dbHelper = new DBHelper(this.getApplicationContext());
        DBAdapter.initInstance(dbHelper);
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