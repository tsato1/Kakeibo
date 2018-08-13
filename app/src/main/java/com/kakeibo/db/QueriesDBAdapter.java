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

    public static final int QUERY_TYPE_AUTO = 0; // automatically saved
    public static final int QUERY_TYPE_MANU = 1; // manually saved

    private final Context _context;
    private SQLiteDatabase _db;

    QueriesDBAdapter(Context context) {
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
        values.put(COL_QUERY_TYPE, query.getType());
        values.put(COL_QUERY, query.getQuery());
        values.put(COL_CREATE_DATE, query.getCreateDate());

        SQLiteDatabase db = DBAdapter.getInstance().openDatabase();
        db.insertOrThrow(TABLE_QUERIES, null, values);
        DBAdapter.getInstance().closeDatabase();

        Log.d(TAG, "saveItem() called");
    }
}
