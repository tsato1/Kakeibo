package com.kakeibo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by T on 2015/09/16.
 */
public class DBAdapter
{
    private static final String DATABASE_NAME = "kakeibo.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEM = "items";
    public static final String COL_ID = "_id";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_CATEGORY = "category";
    public static final String COL_MEMO = "memo";
    public static final String COL_EVENT_D = "event_d";
    public static final String COL_EVENT_YM = "event_ym";
    public static final String COL_UPDATE_DATE = "update_date";

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter (Context context)
    {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try{
                db.execSQL(
                        "CREATE TABLE " + TABLE_ITEM + " ("
                        + COL_ID + " INTEGER PRIMARY KEY,"
                        + COL_AMOUNT + " TEXT NOT NULL,"
                        + COL_CATEGORY + " TEXT NOT NULL,"
                        + COL_MEMO + " TEXT NOT NULL,"
                        + COL_EVENT_D + " TEXT NOT NULL,"
                        + COL_EVENT_YM + " TEXT NOT NULL,"
                        + COL_UPDATE_DATE + " TEXT NOT NULL);"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
            onCreate(db);
        }
    }

    public DBAdapter open()
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public boolean deleteItem(int id)
    {
        return db.delete(TABLE_ITEM, COL_ID + "=" + id, null) > 0;
    }

    public boolean deleteAllItems()
    {
        return db.delete(TABLE_ITEM, null, null) > 0;
    }

    public Cursor getAllItems()
    {
        return db.query(TABLE_ITEM, null, null, null, null, null, null);
    }

    public Cursor getAllItemsInMonth (String ym)
    {
        String query = "SELECT * FROM items WHERE event_ym = ? ORDER BY event_d DESC LIMIT 100";
        return db.rawQuery(query, new String[]{ym});
    }

    public Cursor getAllItemsInCategoryInMonth (String ym, String category) {
        String query = "SELECT * FROM items WHERE event_ym = ? AND category = ? ORDER BY event_d DESC LIMIT 200";
        return db.rawQuery(query, new String[]{ym, category});
    }

    public void saveItem(Item item)
    {
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, item.getAmount());
        values.put(COL_CATEGORY, item.getCategory());
        values.put(COL_MEMO, item.getMemo());
        values.put(COL_EVENT_D, item.getEventD());
        values.put(COL_EVENT_YM, item.getEventYM());
        values.put(COL_UPDATE_DATE, item.getUpdateDate());

        db.insertOrThrow(TABLE_ITEM, null, values);
    }
}
