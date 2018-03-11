package com.kakeibo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by T on 2015/09/16.
 */
public class DBAdapter
{
    private static final String TAG = DBAdapter.class.getSimpleName();
    private static final String DATABASE_NAME = "kakeibo.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_ITEM = "items";
    public static final String COL_ID = "_id";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_CATEGORY = "category";
    public static final String COL_CATEGORY_CODE = "category_code";
    public static final String COL_MEMO = "memo";
    public static final String COL_EVENT_D = "event_d";
    public static final String COL_EVENT_YM = "event_ym";
    public static final String COL_UPDATE_DATE = "update_date";

    public static final String DATABASE_ALTER_STATEMENT_1 = "ALTER TABLE " + TABLE_ITEM + " ADD COLUMN " + COL_CATEGORY_CODE + " INTEGER DEFAULT 0;";

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter (Context context)
    {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private final Context _context;

        public DatabaseHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this._context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(
                        "CREATE TABLE " + TABLE_ITEM + " ("
                        + COL_ID + " INTEGER PRIMARY KEY,"
                        + COL_AMOUNT + " TEXT NOT NULL,"
                        + COL_CATEGORY_CODE + " INTEGER DEFAULT 0,"
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
            Log.d(TAG,"oldVersion="+oldVersion+" : newVersion="+newVersion);

            if (oldVersion < 2) {
                upgradeVersion2(db);
            }
        }

        private void upgradeVersion2(SQLiteDatabase db) {
            db.execSQL(DATABASE_ALTER_STATEMENT_1);

            Cursor c = db.query(TABLE_ITEM, new String[]{COL_ID, COL_CATEGORY} , null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    String catName = c.getString(c.getColumnIndex(DBAdapter.COL_CATEGORY));
                    int colId = c.getInt(c.getColumnIndex(COL_ID));
                    ContentValues values = new ContentValues();
                    Log.e("oioi", catName);
                    Log.e("heyhey", String.valueOf(colId));
                    String[] defaultCategory = _context.getResources().getStringArray(R.array.defaultCategory);
                    int catCode = 0;

                    for (int i=0; i<defaultCategory.length; ++i) {
                        if (catName.equals(defaultCategory[i])) {
                            catCode = i;
                        }
                    }

                    values.put(COL_CATEGORY_CODE, catCode);
                    db.update(TABLE_ITEM, values, COL_ID+"=?", new String[] {String.valueOf(colId)});
                } while (c.moveToNext());
            }
            c.close();
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
        String query = "SELECT * FROM items WHERE event_ym = ? ORDER BY event_d DESC";
        return db.rawQuery(query, new String[]{ym});
    }

    public Cursor getAllItemsInCategoryInMonth (String ym, int categoryCode) {
        String query = "SELECT * FROM items WHERE " + COL_EVENT_YM + " = ? AND " + COL_CATEGORY_CODE + " = ? ORDER BY event_d DESC";
        return db.rawQuery(query, new String[]{ym, String.valueOf(categoryCode)});
    }

    public void saveItem(Item item)
    {
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, item.getAmount());
        values.put(COL_CATEGORY_CODE, item.getCategoryCode());
        values.put(COL_MEMO, item.getMemo());
        values.put(COL_EVENT_D, item.getEventD());
        values.put(COL_EVENT_YM, item.getEventYM());
        values.put(COL_UPDATE_DATE, item.getUpdateDate());

        db.insertOrThrow(TABLE_ITEM, null, values);

        Log.d(TAG, "An item saved");
    }
}

//    SQLiteDatabase db = helper.getReadableDatabase();
//
//    String table = "table2";
//    String[] columns = {"column1", "column3"};
//    String selection = "column3 =?";
//    String[] selectionArgs = {"apple"};
//    String groupBy = null;
//    String having = null;
//    String orderBy = "column3 DESC";
//    String limit = "10";
//
//    Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
//
//    table: the name of the table you want to query
//    columns: the column names that you want returned. Don't return data that you don't need.
//    selection: the row data that you want returned from the columns (This is the WHERE clause.)
//    selectionArgs: This is substituted for the ? in the selection String above.
//    groupBy and having: This groups duplicate data in a column with data having certain conditions. Any unneeded parameters can be set to null.
//    orderBy: sort the data
//    limit: limit the number of results to return