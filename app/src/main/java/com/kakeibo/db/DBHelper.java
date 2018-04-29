package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kakeibo.R;
import com.kakeibo.Utilities;

import java.text.SimpleDateFormat;
import java.util.Currency;
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
                    ItemsDBAdapter.COL_AMOUNT + " TEXT NOT NULL," +
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
//    private static final String DATABASE_UPDATE_3_TO_4_1 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
//            " ADD COLUMN " + ItemsDBAdapter.COL_CURRENCY + " INTEGER DEFAULT 0;";
//    private static final String DATABASE_UPDATE_3_TO_4_2 = "ALTER TABLE " + ItemsDBAdapter.TABLE_ITEM +
//            " ADD COLUMN " + ItemsDBAdapter.COL_LOCALE + " INTEGER DEFAULT 0;";


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
            initKkbAppTable(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "oldVersion=" + oldVersion + " : newVersion=" + newVersion);

        if (oldVersion < 2) {
            upgradeVersion2(db);
        }
        if (oldVersion < 3) {
            upgradeVersion3(db);
        }
        if (oldVersion < 4) {
            upgradeVersion4(db);

            db.execSQL(DATABASE_CREATE_TABLE_KKBAPP);
            initKkbAppTable(db);
        }
    }

    private void upgradeVersion4(SQLiteDatabase db) {
        //db.execSQL(DATABASE_UPDATE_3_TO_4_1);
        //db.execSQL(DATABASE_UPDATE_3_TO_4_2);

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

    private void initKkbAppTable(SQLiteDatabase db) {
        SimpleDateFormat sdf = new SimpleDateFormat(Utilities.DATE_FORMAT_DB_HMS, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String strDate = sdf.format(new Date());

        ContentValues valuesAds = new ContentValues();
        valuesAds.put(KkbAppDBAdapter.COL_NAME, KkbAppDBAdapter.COL_VAL_ADS);
        valuesAds.put(KkbAppDBAdapter.COL_TYPE, KkbAppDBAdapter.COL_VAL_INT);
        valuesAds.put(KkbAppDBAdapter.COL_UPDATE_DATE, strDate);
        valuesAds.put(KkbAppDBAdapter.COL_VAL_INT_1, 0); // 0=original user from version before ads
        valuesAds.put(KkbAppDBAdapter.COL_VAL_INT_2, -1);
        valuesAds.put(KkbAppDBAdapter.COL_VAL_INT_3, -1);
        valuesAds.put(KkbAppDBAdapter.COL_VAL_STR_1, "");
        valuesAds.put(KkbAppDBAdapter.COL_VAL_STR_2, "");
        valuesAds.put(KkbAppDBAdapter.COL_VAL_STR_3, "");
        db.insertOrThrow(TABLE_KKBAPP, null, valuesAds);

        ContentValues valuesCurrency = new ContentValues();
        valuesCurrency.put(KkbAppDBAdapter.COL_NAME, KkbAppDBAdapter.COL_VAL_CURRENCY);
        valuesCurrency.put(KkbAppDBAdapter.COL_TYPE, KkbAppDBAdapter.COL_VAL_INT);
        valuesCurrency.put(KkbAppDBAdapter.COL_UPDATE_DATE, strDate);
        valuesCurrency.put(KkbAppDBAdapter.COL_VAL_INT_1, -1);
        valuesCurrency.put(KkbAppDBAdapter.COL_VAL_INT_2, -1);
        valuesCurrency.put(KkbAppDBAdapter.COL_VAL_INT_3, -1);
        valuesCurrency.put(KkbAppDBAdapter.COL_VAL_STR_1, Currency.getInstance("USD").getCurrencyCode());
        valuesCurrency.put(KkbAppDBAdapter.COL_VAL_STR_2, "");
        valuesCurrency.put(KkbAppDBAdapter.COL_VAL_STR_3, "");
        db.insertOrThrow(TABLE_KKBAPP, null, valuesCurrency);
    }
}
