package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;

import com.kakeibo.Query;

public class QueriesDBAdapter {
    private static final String TAG = QueriesDBAdapter.class.getSimpleName();
    public static final String TABLE_QUERIES = "queries";
    public static final String COL_ID = "_id";
    public static final String COL_QUERY_TYPE = "query_type";
    public static final String COL_QUERY = "sql";
    public static final String COL_CREATE_DATE = "create_date";
    public static final String COL_SEARCH_CRITERIA = "search_criteria";
    public static final String COL_VAL_Y = "val_y";
    public static final String COL_VAL_M = "val_m";
    public static final String COL_VAL_D = "val_d";
    public static final String COL_VAL_FROM_DATE = "val_from_date";
    public static final String COL_VAL_TO_DATE = "val_to_date";
    public static final String COL_VAL_MIN_AMOUNT = "val_min_amount";
    public static final String COL_VAL_MAX_AMOUNT = "val_max_amount";
    public static final String COL_VAL_CURRENCY_CODE = "val_currency_code";
    public static final String COL_VAL_CATEGORY_CODE = "val_category_code";
    public static final String COL_VAL_CATEGORY = "val_category";
    public static final String COL_VAL_MEMO = "val_memo";

    private final Context _context;
    private SQLiteDatabase _db;

    public QueriesDBAdapter(Context context) {
        _context = context;
    }

    public QueriesDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public void saveItem(Query query) {
        ContentValues values = new ContentValues();
//        values.put(COL_QUERY_TYPE, query.getType());
//        values.put(COL_CREATE_DATE, query.getCreateDate());
//        values.put(COL_VAL_FROM_DATE, query.getFromDBDate());
//        values.put(COL_VAL_TO_DATE, query.getToDBDate());
//        values.put(COL_VAL_MIN_AMOUNT, query.getMinAmount());
//        values.put(COL_VAL_MAX_AMOUNT, query.getMaxAmount());
//        values.put(COL_VAL_MEMO, query.getMemo());

        SQLiteDatabase db = DBAdapter.getInstance().openDatabase();
        db.insertOrThrow(TABLE_QUERIES, null, values);
        DBAdapter.getInstance().closeDatabase();

        Log.d(TAG, "saveItem() called");
    }
}
