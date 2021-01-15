//package com.kakeibo.db;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//
//public class KkbAppDBAdapter extends DBAdapter {
//    private static final String TAG = KkbAppDBAdapter.class.getSimpleName();
//    public static final String TABLE_KKBAPP = "kkbapp";
//    public static final String COL_ID = "_id";
//    public static final String COL_NAME = "name";
//    public static final String COL_TYPE = "type";
//    public static final String COL_UPDATE_DATE = "update_date";
//    public static final String COL_VAL_INT_1 = "value_int_1"; // db version at installation
//    public static final String COL_VAL_INT_2 = "value_int_2"; // -1: default, 0: banner ads display agreed
//    public static final String COL_VAL_INT_3 = "value_int_3";
//    public static final String COL_VAL_STR_1 = "value_str_1";
//    public static final String COL_VAL_STR_2 = "value_str_2";
//    public static final String COL_VAL_STR_3 = "value_str_3";
//
//    public static final String COL_VAL_INT = "int";
//    public static final String COL_VAL_STR = "str";
//    public static final String COL_VAL_DB_VERSION = "db_version";
//    public static final String COL_VAL_ADS = "ads";
//    public static final int COL_VAL_INT_2_DEFAULT = -1;
//    public static final int COL_VAL_INT_2_SHOWADS = 0; // show ads
//    public static final int COL_VAL_INT_3_DEFAULT = -1;
//
//    private SQLiteDatabase _db;
//
//    public KkbAppDBAdapter() {}
//
//    public KkbAppDBAdapter open() throws SQLException {
//        _db = DBAdapter.getInstance().openDatabase();
//        return this;
//    }
//
//    public void close() {
//        DBAdapter.getInstance().closeDatabase();
//    }
//
//    public void saveItem() {
////        ContentValues values = new ContentValues();
////        values.put(COL_NAME, application.getName());
////        values.put(COL_TYPE, application.getType());
////        values.put(COL_UPDATE_DATE, application.getUpdateDate());
////        values.put(COL_VAL_INT_1, application.getVersion());
////        values.put(COL_VAL_INT_2, application.getValInt2());
////        values.put(COL_VAL_INT_3, application.getValInt3());
////        values.put(COL_VAL_STR_1, application.getValStr1());
////        values.put(COL_VAL_STR_2, application.getValStr2());
////        values.put(COL_VAL_STR_3, application.getValStr3());
////
////        _db.insertOrThrow(TABLE_KKBAPP, null, values);
////
////        Log.d(TAG, "saveItem() called");
//    }
//
////    public Cursor getValueInt2() {
////        String query = "SELECT " + COL_VAL_INT_2 + " FROM " + TABLE_KKBAPP;
////        return _db.rawQuery(query, new String[]{});
////    }
////
////    /*** return number of rows affected ***/
////    public boolean setValueInt2(int valInt2) {
////        ContentValues values = new ContentValues();
////        values.put(COL_VAL_INT_2, valInt2);
////        return _db.update(TABLE_KKBAPP, values, COL_ID+"=1", null) > 0;
////        /*** there is only one entry in this table and its _id is 1 ***/
////    }
//}
