package com.kakeibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kakeibo.Item;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;

import java.util.Calendar;

public class ItemsDBAdapter extends DBAdapter {
    private static final String TAG = ItemsDBAdapter.class.getSimpleName();
    public static final String TABLE_ITEM = "items";
    public static final String COL_ID = "_id";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_CURRENCY_CODE = "currency_code";
    public static final String COL_CATEGORY = "category"; // dropped on version 2
    public static final String COL_CATEGORY_CODE = "category_code";
    public static final String COL_MEMO = "memo";
    public static final String COL_EVENT_D = "event_d"; // dropped on version 3
    public static final String COL_EVENT_YM = "event_ym"; // dropped on version 3
    public static final String COL_EVENT_DATE = "event_date";
    public static final String COL_UPDATE_DATE = "update_date";

    private SQLiteDatabase _db;

    public ItemsDBAdapter () {
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
        return _db.delete(TABLE_ITEM, COL_ID + "=" + id, null) > 0;
    }

    public boolean deleteAllItems()
    {
        return _db.delete(TABLE_ITEM, null, null) > 0;
    }

    public Cursor getAllItemsInMonth (String y, String m)
    {
        String ym = "\'" + y + "-" + m + "\'";
        String query = "SELECT * FROM " + TABLE_ITEM +
                " WHERE strftime('%Y-%m', " + COL_EVENT_DATE + ") = " + ym +
                " ORDER BY " + COL_EVENT_DATE;
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getCountAllItemsInMonth (String y, String m)
    {
        String ym = "\'" + y + "-" + m + "\'";
        String query = "SELECT COUNT(*) FROM " + TABLE_ITEM +
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

    public Cursor getItemsByRawQuery (String query) {
        if (query==null) {
            Calendar cal = Calendar.getInstance();
            int m = cal.get(Calendar.MONTH) + 1;
            int y = cal.get(Calendar.YEAR);

            return getAllItemsInMonth(String.valueOf(y), UtilDate.convertMtoMM(m));
        }
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getCountItemsByRawQuery (String query) {
        if (query==null) {
            Calendar cal = Calendar.getInstance();
            int m = cal.get(Calendar.MONTH) + 1;
            int y = cal.get(Calendar.YEAR);

            return getCountAllItemsInMonth(String.valueOf(y), UtilDate.convertMtoMM(m));
        }
        return _db.rawQuery(query, new String[]{});
    }

    public void saveItem(Item item)
    {
        ContentValues values = new ContentValues();
        /*** when you save the amount, multiply the value by 1000 ***/
        values.put(COL_AMOUNT, UtilCurrency.getIntAmountFromBigDecimal(item.getAmount(), 3));
        values.put(COL_CURRENCY_CODE, UtilCurrency.CURRENCY_NONE);
        values.put(COL_CATEGORY_CODE, item.getCategoryCode());
        values.put(COL_MEMO, item.getMemo());
        values.put(COL_EVENT_DATE, item.getEventDate());
        values.put(COL_UPDATE_DATE, item.getUpdateDate());
        _db.insertOrThrow(TABLE_ITEM, null, values);
    }
}
