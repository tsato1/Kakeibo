package com.kakeibo.data.disk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Helper class for creating the test database version 1 with SQLite.
 */
public class SqliteTestDbOpenHelper extends SQLiteOpenHelper {

    public SqliteTestDbOpenHelper(Context context, String databaseName, int version) {
        super(context, databaseName, null, version);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d("test", "onCreate called");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("test", "onUpgrade called");
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}