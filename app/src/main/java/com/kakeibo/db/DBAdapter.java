package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kakeibo.Item;
import com.kakeibo.R;
import com.kakeibo.Utilities;

/**
 * Created by T on 2015/09/16.
 */
public abstract class DBAdapter
{
    private static final String TAG = DBAdapter.class.getSimpleName();
    private static final String DATABASE_NAME = "kakeibo.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_SYSTEM = "system";
    private static final String DATABASE_CREATE_TABLE_SYSTEM =
            "CREATE TABLE " + SystemDBAdapter.TABLE_SYSTEM + " (" +
                    SystemDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    SystemDBAdapter.COL_VERSION + " INTEGER DEFAULT 0);";


    private static final String DATABASE_CREATE_TABLE_ITEM =
            "CREATE TABLE " + ItemsDBAdapter.TABLE_ITEM + " ("+
                    ItemsDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    ItemsDBAdapter.COL_AMOUNT + " TEXT NOT NULL," +
                    ItemsDBAdapter.COL_CATEGORY_CODE + " INTEGER DEFAULT 0," +
                    ItemsDBAdapter.COL_MEMO + " TEXT NOT NULL," +
                    ItemsDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL," +
                    ItemsDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);";
    private static final String DATABASE_UPDATE_1_TO_2 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
            " ADD COLUMN " + ItemsDBAdapter.COL_CATEGORY_CODE + " INTEGER DEFAULT 0;";
    private static final String DATABASE_UPDATE_2_TO_3_1 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
            //" ADD COLUMN " + COL_CURRENCY + " INTEGER DEFAULT 0" +
            //" ADD COLUMN " + COL_LOCALE + " TEXT NOT NULL DEFAULT ''" +
            " ADD COLUMN " + ItemsDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL DEFAULT '';";
    private static final String DATABASE_UPDATE_2_TO_3_2 =
            "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM + " RENAME TO " + ItemsDBAdapter.TABLE_ITEM + "_old;";
    private static final String DATABASE_UPDATE_2_TO_3_3 =
            "INSERT INTO " + ItemsDBAdapter.TABLE_ITEM + " (" +
                    ItemsDBAdapter.COL_ID+","+
                    ItemsDBAdapter.COL_AMOUNT+","+
                    ItemsDBAdapter.COL_CATEGORY_CODE+","+
                    ItemsDBAdapter.COL_MEMO+","+
                    ItemsDBAdapter.COL_EVENT_DATE+","+
                    ItemsDBAdapter.COL_UPDATE_DATE+") "+
                    " SELECT "+
                    ItemsDBAdapter.COL_ID+","+
                    ItemsDBAdapter.COL_AMOUNT+","+
                    ItemsDBAdapter.COL_CATEGORY_CODE+","+
                    ItemsDBAdapter.COL_MEMO+","+
                    ItemsDBAdapter.COL_EVENT_DATE+","+
                    ItemsDBAdapter.COL_UPDATE_DATE+" FROM "+ItemsDBAdapter.TABLE_ITEM+"_old;";
    private static final String DATABASE_UPDATE_2_TO_3_4 =
            "DROP TABLE "+ItemsDBAdapter.TABLE_ITEM+"_old;";
    private static final String DATABASE_UPDATE_3_TO_4_1 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
            " ADD COLUMN " + ItemsDBAdapter.COL_CURRENCY + " INTEGER DEFAULT 0;";
    private static final String DATABASE_UPDATE_3_TO_4_2 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
            " ADD COLUMN " + ItemsDBAdapter.COL_LOCALE + " INTEGER DEFAULT 0;";

    protected final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter (Context context)
    {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    protected static class DatabaseHelper extends SQLiteOpenHelper {
        private final Context _context;

        DatabaseHelper (Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this._context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE_TABLE_SYSTEM);
                db.execSQL(DATABASE_CREATE_TABLE_ITEM);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG,"oldVersion="+oldVersion+" : newVersion="+newVersion);

            // to do check if db is items if ()
            if (oldVersion < 2) {
                upgradeVersion2(db);
            }
            if (oldVersion < 3) {
                upgradeVersion3(db);
            }
            if (oldVersion < 4) {
                upgradeVersion4(db);
            }
        }

        private void upgradeVersion4(SQLiteDatabase db) {
            db.execSQL(DATABASE_UPDATE_3_TO_4_1);
            db.execSQL(DATABASE_UPDATE_3_TO_4_2);

            /*** changing UpdateDate format form M to MM ***/
            Cursor c = db.query(ItemsDBAdapter.TABLE_ITEM, new String[]{ItemsDBAdapter.COL_ID, ItemsDBAdapter.COL_UPDATE_DATE},
                    null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    String oriDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE));
                    String[] dates = oriDate.split("[ ]"); // [0]=date, [1]=time
                    String[] ymd = dates[0].split("[-]");
                    String d = Utilities.convertMtoMM(Integer.parseInt(ymd[2]));
                    String updDate = oriDate;
                    if (d.length() == 2) {
                        updDate = ymd[0]+"-"+ymd[1]+"-"+d+" "+dates[1];
                    }

                    int colId = c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_ID));
                    ContentValues values = new ContentValues();
                    values.put(ItemsDBAdapter.COL_UPDATE_DATE, updDate);

                    db.update(ItemsDBAdapter.TABLE_ITEM, values,
                            ItemsDBAdapter.COL_ID+"=?",
                            new String[] {String.valueOf(colId)});
                } while (c.moveToNext());
            }
            c.close();
        }

        private void upgradeVersion3(SQLiteDatabase db) {
            db.execSQL(DATABASE_UPDATE_2_TO_3_1);

            Cursor c = db.query(ItemsDBAdapter.TABLE_ITEM, new String[]{
                            ItemsDBAdapter.COL_ID,
                            ItemsDBAdapter.COL_EVENT_D,
                            ItemsDBAdapter.COL_EVENT_YM,
                            ItemsDBAdapter.COL_EVENT_D,
                            ItemsDBAdapter.COL_UPDATE_DATE},
                    null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    String eventD = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_D));
                    String eventYM = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_EVENT_YM));
                    String eventDate;
                    String updateDate = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_UPDATE_DATE));
                    int colId = c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_ID));
                    ContentValues values = new ContentValues();

                    /*** event_date ***/
                    eventDate = eventYM.replace('/','-') + "-" + eventD;
                    /*** update_date ***/
                    updateDate = updateDate.split("\\s+")[0].replace('/','-') + " 00:00:00";

                    values.put(ItemsDBAdapter.COL_EVENT_DATE, eventDate);
                    values.put(ItemsDBAdapter.COL_UPDATE_DATE, updateDate);
                    db.update(ItemsDBAdapter.TABLE_ITEM, values, ItemsDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
                } while (c.moveToNext());
            }
            c.close();

            db.execSQL(DATABASE_UPDATE_2_TO_3_2);
            db.execSQL(DATABASE_CREATE_TABLE_ITEM);
            db.execSQL(DATABASE_UPDATE_2_TO_3_3);
            db.execSQL(DATABASE_UPDATE_2_TO_3_4);
        }

        private void upgradeVersion2(SQLiteDatabase db) {
            db.execSQL(DATABASE_UPDATE_1_TO_2);

            Cursor c = db.query(ItemsDBAdapter.TABLE_ITEM,
                    new String[]{ItemsDBAdapter.COL_ID, ItemsDBAdapter.COL_CATEGORY} ,
                    null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    String catName = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_CATEGORY));
                    int colId = c.getInt(c.getColumnIndex(ItemsDBAdapter.COL_ID));
                    ContentValues values = new ContentValues();
                    String[] defaultCategory = _context.getResources().getStringArray(R.array.defaultCategory);
                    int catCode = 0;

                    for (int i=0; i<defaultCategory.length; ++i) {
                        if (catName.equals(defaultCategory[i])) {
                            catCode = i;
                        }
                    }

                    values.put(ItemsDBAdapter.COL_CATEGORY_CODE, catCode);
                    db.update(ItemsDBAdapter.TABLE_ITEM, values,
                            ItemsDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
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