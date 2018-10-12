package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
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

    public static final String ASC = "asc";
    public static final String DESC = "desc";

    private final Context _context;
    private SQLiteDatabase _db;
    private QueriesDBAdapter _queriesDBAdapter;

    public ItemsDBAdapter (Context context) {
        _context = context;
        _queriesDBAdapter = new QueriesDBAdapter(context);
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

    public Cursor getItems(int fromY, int fromM, int fromD, int toY, int toM, int toD, String memo,
                           String order1, String direction1, String order2, String direction2)
    {
        String fromYMD = "\'" + fromY + "-" + fromM + "-" + fromD + "\'";
        String toYMD = "\'" + toY + "-" + toM + "-" + toD + "\'";
        String query;

        if ("".equals(memo)) {
            query = "SELECT * FROM " + TABLE_ITEM +
                    " WHERE " + COL_EVENT_DATE +
                    " between strftime('%Y-%m-%d', " + fromYMD + ") and strftime('%Y-%m-%d'," + toYMD + ")" +
                    " ORDER BY " + order1 + " " + direction1 + ", " + order2 + " " + direction2;
        } else {
            query = "SELECT * FROM " + TABLE_ITEM +
                    " WHERE " + COL_EVENT_DATE +
                    " between strftime('%Y-%m-%d', " + fromYMD + ") and strftime('%Y-%m-%d'," + toYMD + ")" +
                    " AND " + COL_MEMO + "=" + "\'" + memo + "\'" +
                    " ORDER BY " + order1 + " " + direction1 + ", " + order2 + " " + direction2;
        }

        //saveQuery(QueriesDBAdapter.QUERY_TYPE_AUTO, query);

        return _db.rawQuery(query, new String[]{});
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

    public Cursor getItemsByRawQuery (String query) {
        if (query==null) {
            Calendar cal = Calendar.getInstance();
            int m = cal.get(Calendar.MONTH) + 1;
            int y = cal.get(Calendar.YEAR);

            return getAllItemsInMonth(String.valueOf(y), UtilDate.convertMtoMM(m));
        }
        return _db.rawQuery(query, new String[]{});
    }

    public void saveItem(Item item)
    {
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, UtilCurrency.getIntAmount(item.getAmount(), item.getFractionDigits()));
        values.put(COL_CURRENCY_CODE, item.getCurrencyCode());
        values.put(COL_CATEGORY_CODE, item.getCategoryCode());
        values.put(COL_MEMO, item.getMemo());
        values.put(COL_EVENT_DATE, item.getEventDate());
        values.put(COL_UPDATE_DATE, item.getUpdateDate());
        _db.insertOrThrow(TABLE_ITEM, null, values);

//        Log.d(TAG, "saveItem(): amount="+item.getAmount()
//                +" intAmount="+item.getIntAmount()
//                +" currency_code="+item.getCurrencyCode()
//                +" category_code="+item.getCategoryCode()
//                +" memo="+item.getMemo()
//        );
    }
}
