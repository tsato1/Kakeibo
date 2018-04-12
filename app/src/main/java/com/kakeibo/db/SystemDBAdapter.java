package com.kakeibo.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SystemDBAdapter extends DBAdapter {
    private static final String TAG = SystemDBAdapter.class.getSimpleName();
    public static final String TABLE_SYSTEM = "system";
    public static final String COL_ID = "_id";
    public static final String COL_VERSION = "version";

    private final Context _context;
    private SQLiteDatabase _db;
    private DatabaseHelper _dbHelper;

    public SystemDBAdapter(Context context) {
        super(context);
        _context = context;
    }

    public SystemDBAdapter open() throws SQLException {
        this._dbHelper = new DatabaseHelper(this._context);
        this._db = _dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this._dbHelper.close();
    }
}
