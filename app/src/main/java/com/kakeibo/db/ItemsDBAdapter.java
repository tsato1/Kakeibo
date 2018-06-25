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

    public ItemsDBAdapter (Context context) {
        _context = context;
    }

    public ItemsDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public boolean deleteItem(int id)
    {
        boolean out = _db.delete(TABLE_ITEM, COL_ID + "=" + id, null) > 0;
        return out;
    }

    public boolean deleteAllItems()
    {
        boolean out = _db.delete(TABLE_ITEM, null, null) > 0;
        return out;
    }

    public Cursor getAllItems()
    {
        Cursor c = _db.query(TABLE_ITEM, null, null, null, null, null, null);
        return c;
    }

    public Cursor getAllItemsInMonth (String y, String m)
    {
        String ym = "\'" + y + "-" + m + "\'";
        String query = "SELECT * FROM " + TABLE_ITEM +
                " WHERE strftime('%Y-%m', " + COL_EVENT_DATE + ") = " + ym +
                " ORDER BY " + COL_EVENT_DATE;
        Cursor c = _db.rawQuery(query, new String[]{});
        return c;
    }

    public Cursor getAllItemsInCategoryInMonth (String y, String m, int categoryCode) {
        String ym = "'" + y + "-" + m + "'";
        String query = "SELECT * FROM " + TABLE_ITEM +
                " WHERE strftime('%Y-%m', " + COL_EVENT_DATE + ") = " + ym +
                " AND " + COL_CATEGORY_CODE + " = ? " +
                " ORDER BY " + COL_EVENT_DATE;
        Cursor c = _db.rawQuery(query, new String[]{String.valueOf(categoryCode)});

        return c;
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

        Log.d(TAG, "saveItem() called");
    }
}