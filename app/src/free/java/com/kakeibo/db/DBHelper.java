package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kakeibo.R;
import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.kakeibo.db.KkbAppDBAdapter.TABLE_KKBAPP;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBAdapter.class.getSimpleName();
    private static final String DATABASE_NAME = "kakeibo.db";
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_CREATE_TABLE_KKBAPP =
            "CREATE TABLE " + TABLE_KKBAPP + " (" +
                    KkbAppDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    KkbAppDBAdapter.COL_NAME + " TEXT NOT NULL," +
                    KkbAppDBAdapter.COL_TYPE + " TEXT NOT NULL," +
                    KkbAppDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL," +
                    KkbAppDBAdapter.COL_VAL_INT_1 + " INTEGER DEFAULT 0," +
                    KkbAppDBAdapter.COL_VAL_INT_2 + " INTEGER DEFAULT 0," +
                    KkbAppDBAdapter.COL_VAL_INT_3 + " INTEGER DEFAULT 0," +
                    KkbAppDBAdapter.COL_VAL_STR_1 + " TEXT NOT NULL," +
                    KkbAppDBAdapter.COL_VAL_STR_2 + " TEXT NOT NULL," +
                    KkbAppDBAdapter.COL_VAL_STR_3 + " TEXT NOT NULL);";

    /*** items table ***/
    private static final String DATABASE_CREATE_TABLE_ITEM =
            "CREATE TABLE " + ItemsDBAdapter.TABLE_ITEM + " ("+
                    ItemsDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    ItemsDBAdapter.COL_AMOUNT + " INTEGER DEFAULT 0," +
                    ItemsDBAdapter.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '"+UtilCurrency.CURRENCY_OLD+"', " +
                    ItemsDBAdapter.COL_CATEGORY_CODE + " INTEGER DEFAULT 0," +
                    ItemsDBAdapter.COL_MEMO + " TEXT NOT NULL," +
                    ItemsDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL," +
                    ItemsDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);";

    private static final String DATABASE_UPDATE_1_TO_2 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
            " ADD COLUMN " + ItemsDBAdapter.COL_CATEGORY_CODE + " INTEGER DEFAULT 0;";
    private static final String DATABASE_UPDATE_2_TO_3_1 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
            " ADD COLUMN " + ItemsDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL DEFAULT '';";
    private static final String DATABASE_UPDATE_2_TO_3_2 =
            "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM + " RENAME TO " + ItemsDBAdapter.TABLE_ITEM + "_old;";
    private static final String DATABASE_UPDATE_2_TO_3_3 =
            "INSERT INTO " + ItemsDBAdapter.TABLE_ITEM +
                    " (" +
                    ItemsDBAdapter.COL_ID+","+
                    ItemsDBAdapter.COL_AMOUNT+","+
                    ItemsDBAdapter.COL_CATEGORY_CODE+","+
                    ItemsDBAdapter.COL_MEMO+","+
                    ItemsDBAdapter.COL_EVENT_DATE+","+
                    ItemsDBAdapter.COL_UPDATE_DATE+") "+
                    " SELECT "+
                    ItemsDBAdapter.COL_ID+","+
                    "CAST ("+ItemsDBAdapter.COL_AMOUNT+" AS INTEGER),"+
                    ItemsDBAdapter.COL_CATEGORY_CODE+","+
                    ItemsDBAdapter.COL_MEMO+","+
                    ItemsDBAdapter.COL_EVENT_DATE+","+
                    ItemsDBAdapter.COL_UPDATE_DATE+" FROM "+ItemsDBAdapter.TABLE_ITEM+"_old;";
    private static final String DATABASE_UPDATE_2_TO_3_4 =
            "DROP TABLE "+ItemsDBAdapter.TABLE_ITEM+"_old;";

    private final Context _context;

    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE_TABLE_ITEM);
            db.execSQL(DATABASE_CREATE_TABLE_KKBAPP);
            initKkbAppTable(db, DATABASE_VERSION);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "oldVersion=" + oldVersion + " : newVersion=" + newVersion);

        ItemsDBAdapter itemsDBAdapter = new ItemsDBAdapter();
        itemsDBAdapter.open();

        if (oldVersion < 2) {
            upgradeVersion2(db);
        }
        if (oldVersion < 3) {
            upgradeVersion3(db);
        }
        if (oldVersion < 4) {
            upgradeVersion4(db);

            db.execSQL(DATABASE_CREATE_TABLE_KKBAPP);
            initKkbAppTable(db, -1);
        }

        itemsDBAdapter.close();
    }

    private void upgradeVersion4(SQLiteDatabase db) {
        /*** changing UpdateDate format form M to MM ***/
//        Cursor c = db.query(ItemsDBAdapter.TABLE_ITEM,
//                new String[]{ItemsDBAdapter.COL_ID, ItemsDBAdapter.COL_UPDATE_DATE},
//                null, null, null, null, null, null);
//        if (c.moveToFirst()) {
//            do {
//            } while (c.moveToNext());
//        }
//        c.close();
    }

    private void upgradeVersion3(SQLiteDatabase db) {
        db.execSQL(DATABASE_UPDATE_2_TO_3_1);

        Cursor c = db.query(ItemsDBAdapter.TABLE_ITEM, new String[]{
                        ItemsDBAdapter.COL_ID,
                        ItemsDBAdapter.COL_AMOUNT,
                        ItemsDBAdapter.COL_EVENT_D,
                        ItemsDBAdapter.COL_EVENT_YM,
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
                values.put(ItemsDBAdapter.COL_EVENT_DATE, eventDate);

                /*** update_date ***/
                updateDate = updateDate.split("\\s+")[0].replace('/','-') + " 00:00:00";
                values.put(ItemsDBAdapter.COL_UPDATE_DATE, updateDate);

                /*** flipping negative to positive***/
                String amount = c.getString(c.getColumnIndex(ItemsDBAdapter.COL_AMOUNT));
                int newAmount = Math.abs(Integer.parseInt(amount));
                values.put(ItemsDBAdapter.COL_AMOUNT, newAmount*1000);

                /*** reflecting the result to db ***/
                db.update(ItemsDBAdapter.TABLE_ITEM, values,
                        ItemsDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
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
                String[] defaultCategory = _context.getResources().getStringArray(R.array.default_category);
                int catCode = 0;

                for (int i=0; i<defaultCategory.length; ++i) {
                    if (catName.equals(defaultCategory[i])) {
                        catCode = i;
                    } else if (catName.equals("Until")) {
                        catCode = 3;
                    }
                }

                values.put(ItemsDBAdapter.COL_CATEGORY_CODE, catCode);
                db.update(ItemsDBAdapter.TABLE_ITEM, values,
                        ItemsDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
            } while (c.moveToNext());
        }
        c.close();
    }

    private void initKkbAppTable(SQLiteDatabase db, int dbVersion) {
        SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.DATE_FORMAT_DB_HMS, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String strDate = sdf.format(new Date());

        ContentValues valuesDBVersion = new ContentValues();
        valuesDBVersion.put(KkbAppDBAdapter.COL_NAME, KkbAppDBAdapter.COL_VAL_DB_VERSION);
        valuesDBVersion.put(KkbAppDBAdapter.COL_TYPE, KkbAppDBAdapter.COL_VAL_INT);
        valuesDBVersion.put(KkbAppDBAdapter.COL_UPDATE_DATE, strDate);
        valuesDBVersion.put(KkbAppDBAdapter.COL_VAL_INT_1, dbVersion); // 0=original user from version before ads
        valuesDBVersion.put(KkbAppDBAdapter.COL_VAL_INT_2, -1);
        valuesDBVersion.put(KkbAppDBAdapter.COL_VAL_INT_3, -1);
        valuesDBVersion.put(KkbAppDBAdapter.COL_VAL_STR_1, "");
        valuesDBVersion.put(KkbAppDBAdapter.COL_VAL_STR_2, "");
        valuesDBVersion.put(KkbAppDBAdapter.COL_VAL_STR_3, "");
        db.insertOrThrow(TABLE_KKBAPP, null, valuesDBVersion);
    }
}
