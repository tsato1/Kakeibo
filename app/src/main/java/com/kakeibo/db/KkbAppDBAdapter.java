package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kakeibo.KkbApplication;

public class KkbAppDBAdapter extends DBAdapter {
    private static final String TAG = KkbAppDBAdapter.class.getSimpleName();
    public static final String TABLE_KKBAPP = "kkbapp";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE = "type";
    public static final String COL_UPDATE_DATE = "update_date";
    public static final String COL_VAL_INT_1 = "value_int_1";
    public static final String COL_VAL_INT_2 = "value_int_2";
    public static final String COL_VAL_INT_3 = "value_int_3";
    public static final String COL_VAL_STR_1 = "value_str_1";
    public static final String COL_VAL_STR_2 = "value_str_2";
    public static final String COL_VAL_STR_3 = "value_str_3";

    public static final String COL_VAL_INT = "int";
    public static final String COL_VAL_STR = "str";
    public static final String COL_VAL_DB_VERSION = "db_version";
    public static final String COL_VAL_ADS = "ads";

    private final Context _context;
    private SQLiteDatabase _db;

    public KkbAppDBAdapter(Context context) {
        _context = context;
    }

    public KkbAppDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public void saveItem(KkbApplication application) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, application.getVersion());
        values.put(COL_TYPE, application.getVersion());
        values.put(COL_UPDATE_DATE, application.getVersion());
        values.put(COL_VAL_STR_1, application.getVersion());
        values.put(COL_VAL_STR_2, application.getVersion());
        values.put(COL_VAL_STR_3, application.getVersion());
        values.put(COL_VAL_INT_1, application.getVersion());
        values.put(COL_VAL_INT_2, application.getVersion());

        SQLiteDatabase db = DBAdapter.getInstance().openDatabase();
        db.insertOrThrow(TABLE_KKBAPP, null, values);
        DBAdapter.getInstance().closeDatabase();

        Log.d(TAG, "saveItem() called");
    }
}
