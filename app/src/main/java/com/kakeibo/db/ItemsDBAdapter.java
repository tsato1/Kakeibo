package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kakeibo.Item;

public class ItemsDBAdapter extends DBAdapter {
    private static final String TAG = ItemsDBAdapter.class.getSimpleName();
    public static final String TABLE_ITEM = "items";
    public static final String COL_ID = "_id";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_CURRENCY = "currency";
    public static final String COL_CATEGORY = "category"; // dropped on version 2
    public static final String COL_CATEGORY_CODE = "category_code";
    public static final String COL_MEMO = "memo";
    public static final String COL_LOCALE = "locale";
    public static final String COL_EVENT_D = "event_d"; // dropped on version 3
    public static final String COL_EVENT_YM = "event_ym"; // dropped on version 3
    public static final String COL_EVENT_DATE = "event_date";
    public static final String COL_UPDATE_DATE = "update_date";

    private final Context _context;
    private SQLiteDatabase _db;
    private DatabaseHelper _dbHelper;

    public ItemsDBAdapter (Context context) {
        super(context);
        _context = context;
    }

    public ItemsDBAdapter open() throws SQLException {
        this._dbHelper = new DatabaseHelper(this._context);
        this._db = _dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this._dbHelper.close();
    }

    public boolean deleteItem(int id)
    {
        return _db.delete(TABLE_ITEM, COL_ID + "=" + id, null) > 0;
    }

    public boolean deleteAllItems()
    {
        return _db.delete(TABLE_ITEM, null, null) > 0;
    }

    public Cursor getAllItems()
    {
        return _db.query(TABLE_ITEM, null, null, null, null, null, null);
    }

    public Cursor getAllItemsInMonth (String y, String m)
    {
        String ym = "\'" + y + "-" + m + "\'";
        String query = "SELECT * FROM " + TABLE_ITEM +
                " WHERE strftime('%Y-%m', " + COL_EVENT_DATE + ") = " + ym +
                " ORDER BY " + COL_EVENT_DATE;
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getAllItemsInCategoryInMonth (String y, String m, int categoryCode) {
        String ym = "'" + y + "-" + m + "'";
        String query = "SELECT * FROM " + TABLE_ITEM +
                " WHERE strftime('%Y-%m', " + COL_EVENT_DATE + ") = " + ym +
                " AND " + COL_CATEGORY_CODE + " = ? " +
                " ORDER BY " + COL_EVENT_DATE;
        return _db.rawQuery(query, new String[]{String.valueOf(categoryCode)});
    }

    public void saveItem(Item item)
    {
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, item.getAmount());
        values.put(COL_CATEGORY_CODE, item.getCategoryCode());
        values.put(COL_MEMO, item.getMemo());
        values.put(COL_EVENT_DATE, item.getEventDate());
        values.put(COL_UPDATE_DATE, item.getUpdateDate());

        _db.insertOrThrow(TABLE_ITEM, null, values);

        //Log.d(TAG, "saveItem() called");
    }
}
