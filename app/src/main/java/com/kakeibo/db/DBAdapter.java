//package com.kakeibo.db;
//
//import android.database.sqlite.SQLiteDatabase;
//
///**
// * Created by T on 2015/09/16.
// */
//public class DBAdapter {
//    private static DBAdapter instance;
//    private static DBHelper _dbHelper;
//    private SQLiteDatabase _database;
//    private Integer _openCounter = 0;
//
//    public static synchronized void initInstance(DBHelper helper) {
//        if (instance == null) {
//            instance = new DBAdapter();
//            _dbHelper = helper;
//        }
//    }
//
//    public static synchronized DBAdapter getInstance() {
//        if (instance == null) {
//            throw new IllegalStateException(DBAdapter.class.getSimpleName() +
//                    " is not initialized, call initInstance(...) method first.");
//        }
//        return instance;
//    }
//
//    public synchronized SQLiteDatabase openDatabase() {
//        _openCounter += 1;
//        if (_openCounter == 1) {
//            _database = _dbHelper.getWritableDatabase();
//        }
//        return _database;
//    }
//
//    public synchronized void closeDatabase() {
//        _openCounter -= 1;
//        if (_openCounter == 0) {
//            _database.close();
//        }
//    }
//}
//
////    SQLiteDatabase db = helper.getReadableDatabase();
////
////    String table = "table2";
////    String[] columns = {"column1", "column3"};
////    String selection = "column3 =?";
////    String[] selectionArgs = {"apple"};
////    String groupBy = null;
////    String having = null;
////    String orderBy = "column3 DESC";
////    String limit = "10";
////
////    Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
////
////    table: the name of the table you want to query
////    columns: the column names that you want returned. Don't return data that you don't need.
////    selection: the row data that you want returned from the columns (This is the WHERE clause.)
////    selectionArgs: This is substituted for the ? in the selection String above.
////    groupBy and having: This groups duplicate data in a column with data having certain conditions. Any unneeded parameters can be set to null.
////    orderBy: sort the data
////    limit: limit the number of results to return