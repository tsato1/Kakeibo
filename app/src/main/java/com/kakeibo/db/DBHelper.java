package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kakeibo.BuildConfig;
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
    private static final int DATABASE_VERSION = BuildConfig.versionDB;

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
            "CREATE TABLE " + ItemDBAdapter.TABLE_NAME + " ("+
                    ItemDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    ItemDBAdapter.COL_AMOUNT + " INTEGER DEFAULT 0," +
                    ItemDBAdapter.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '"+UtilCurrency.CURRENCY_OLD+"', " +
                    ItemDBAdapter.COL_CATEGORY_CODE + " INTEGER DEFAULT 0," +
                    ItemDBAdapter.COL_MEMO + " TEXT NOT NULL," +
                    ItemDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL," +
                    ItemDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);";

    /*** category table ***/
    private static final String DATABASE_CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
                    CategoryDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    CategoryDBAdapter.COL_CODE + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_NAME + " TEXT NOT NULL," +
                    CategoryDBAdapter.COL_COLOR + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_DRAWABLE + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_LOCATION + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_SUB_CATEGORIES + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_DESC + " TEXT NOT NULL," +
                    CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL);";
    private static final String DATABASE_CREATE_TABLE_CATEGORY_REVISED =
            "CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
                    CategoryDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    CategoryDBAdapter.COL_CODE + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_COLOR + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_SIGNIFICANCE + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_DRAWABLE + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_IMAGE + " BLOB DEFAULT NULL, " +
                    CategoryDBAdapter.COL_PARENT + " INTEGER DEFAULT -1," +
                    CategoryDBAdapter.COL_DESC + " TEXT NOT NULL," +
                    CategoryDBAdapter.COL_VAL1 + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_VAL2 + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_VAL3 + " INTEGER DEFAULT 0," +
                    CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL);";
    /*** category lan table ***/
    private static final String DATABASE_CREATE_TABLE_CATEGORY_LAN =
            "CREATE TABLE " + CategoryLanDBAdapter.TABLE_NAME + " (" +
                    CategoryLanDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    CategoryLanDBAdapter.COL_CODE + " INTEGER DEFAULT 0," +
                    CategoryLanDBAdapter.COL_NAME + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_EN + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_ES + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_FR + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_IT + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_JA + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL);";
    private static final String DATABASE_CREATE_TABLE_CATEGORY_LAN_REVISED =
            "CREATE TABLE " + CategoryLanDBAdapter.TABLE_NAME + " (" +
                    CategoryLanDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    CategoryLanDBAdapter.COL_CODE + " INTEGER DEFAULT 0," +
                    CategoryLanDBAdapter.COL_ARA + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_ENG + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_SPA + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_FRA + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_HIN + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_IND + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_ITA + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_JPN + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_KOR + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_POL + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_POR + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_RUS + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_TUR + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_VIE + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_Hans + " TEXT NOT NULL," +
                    CategoryLanDBAdapter.COL_Hant + " TEXT NOT NULL);";

    /*** category display table ***/
    private static final String DATABASE_CREATE_TABLE_CATEGORY_DSP =
            "CREATE TABLE " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                    CategoryDspDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                    CategoryDspDBAdapter.COL_LOCATION + " INTEGER DEFAULT 0," +
                    CategoryDspDBAdapter.COL_CODE + " INTEGER DEFAULT 0);";

    private static final String DATABASE_UPDATE_1_TO_2 = "ALTER TABLE " + ItemDBAdapter.TABLE_NAME +
            " ADD COLUMN " + ItemDBAdapter.COL_CATEGORY_CODE + " INTEGER DEFAULT 0;";
    private static final String DATABASE_UPDATE_2_TO_3_1 = "ALTER TABLE " + ItemDBAdapter.TABLE_NAME +
            " ADD COLUMN " + ItemDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL DEFAULT '';";
    private static final String DATABASE_UPDATE_2_TO_3_2 =
            "ALTER TABLE " + ItemDBAdapter.TABLE_NAME + " RENAME TO " + ItemDBAdapter.TABLE_NAME + "_old;";
    private static final String DATABASE_UPDATE_2_TO_3_3 =
            "INSERT INTO " + ItemDBAdapter.TABLE_NAME +
                    " (" +
                    ItemDBAdapter.COL_ID+","+
                    ItemDBAdapter.COL_AMOUNT+","+
                    ItemDBAdapter.COL_CATEGORY_CODE+","+
                    ItemDBAdapter.COL_MEMO+","+
                    ItemDBAdapter.COL_EVENT_DATE+","+
                    ItemDBAdapter.COL_UPDATE_DATE+") "+
                    " SELECT "+
                    ItemDBAdapter.COL_ID+","+
                    "CAST ("+ ItemDBAdapter.COL_AMOUNT+" AS INTEGER),"+
                    ItemDBAdapter.COL_CATEGORY_CODE+","+
                    ItemDBAdapter.COL_MEMO+","+
                    ItemDBAdapter.COL_EVENT_DATE+","+
                    ItemDBAdapter.COL_UPDATE_DATE+" FROM "+ ItemDBAdapter.TABLE_NAME +"_old;";
    private static final String DATABASE_UPDATE_2_TO_3_4 =
            "DROP TABLE "+ ItemDBAdapter.TABLE_NAME +"_old;";

    /*** category and category_lan table revision ***/
    private static final String DROP_TABLE_CATEGORY = "DROP TABLE "+ CategoryDBAdapter.TABLE_NAME;
    private static final String DROP_TABLE_CATEGORY_LAN = "DROP TABLE "+ CategoryLanDBAdapter.TABLE_NAME;


    private final Context _context;

    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "db version = " + DATABASE_VERSION);
        this._context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE_TABLE_ITEM);
            db.execSQL(DATABASE_CREATE_TABLE_KKBAPP);
            initKkbAppTable(db, DATABASE_VERSION);
            /*** added on 2.0.8 ***/
            db.execSQL(DATABASE_CREATE_TABLE_CATEGORY);
            db.execSQL(DATABASE_CREATE_TABLE_CATEGORY_LAN);
            PrepDB.initCategoriesTable(db);
            /*** added on 2.8.6 (-> 3.0.6) ***/
            upgradeVersion7(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "oldVersion=" + oldVersion + " : newVersion=" + newVersion);

        ItemDBAdapter itemDBAdapter = new ItemDBAdapter();
        itemDBAdapter.open();
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        CategoryLanDBAdapter categoryLanDBAdapter = new CategoryLanDBAdapter();
        categoryLanDBAdapter.open();

        if (oldVersion < 2) {
            upgradeVersion2(db);
        }
        if (oldVersion < 3) {
            upgradeVersion3(db);
        }
        if (oldVersion < 4) {
            db.execSQL(DATABASE_CREATE_TABLE_KKBAPP);
            initKkbAppTable(db, -1);
        }
        if (oldVersion < 5) {
            db.execSQL(DATABASE_CREATE_TABLE_CATEGORY);
            db.execSQL(DATABASE_CREATE_TABLE_CATEGORY_LAN);
            PrepDB.initCategoriesTable(db);
        }
        if (oldVersion < 6) {
            upgradeVersion7(db);
        }

        itemDBAdapter.close();
        categoryDBAdapter.close();
        categoryLanDBAdapter.close();
    }

    private void upgradeVersion7(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_CATEGORY_DSP);
        db.execSQL(DROP_TABLE_CATEGORY);
        db.execSQL(DROP_TABLE_CATEGORY_LAN);
        db.execSQL(DATABASE_CREATE_TABLE_CATEGORY_REVISED);
        db.execSQL(DATABASE_CREATE_TABLE_CATEGORY_LAN_REVISED);
        PrepDB.initCategoriesTableRevised(db, DATABASE_VERSION);
        /*** to add more categories ***/
        PrepDB.addMoreCategories(db);
    }

    private void upgradeVersion3(SQLiteDatabase db) {
        db.execSQL(DATABASE_UPDATE_2_TO_3_1);

        Cursor c = db.query(ItemDBAdapter.TABLE_NAME, new String[]{
                        ItemDBAdapter.COL_ID,
                        ItemDBAdapter.COL_AMOUNT,
                        ItemDBAdapter.COL_EVENT_D,
                        ItemDBAdapter.COL_EVENT_YM,
                        ItemDBAdapter.COL_UPDATE_DATE},
                null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                String eventD = c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_D));
                String eventYM = c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_YM));
                String eventDate;
                String updateDate = c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE));
                int colId = c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID));
                ContentValues values = new ContentValues();

                /*** event_date ***/
                eventDate = eventYM.replace('/','-') + "-" + eventD;
                values.put(ItemDBAdapter.COL_EVENT_DATE, eventDate);

                /*** update_date ***/
                updateDate = updateDate.split("\\s+")[0].replace('/','-') + " 00:00:00";
                values.put(ItemDBAdapter.COL_UPDATE_DATE, updateDate);

                /*** flipping negative to positive***/
                String amount = c.getString(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT));
                int newAmount = Math.abs(Integer.parseInt(amount));
                values.put(ItemDBAdapter.COL_AMOUNT, newAmount*1000);

                /*** reflecting the result to db ***/
                db.update(ItemDBAdapter.TABLE_NAME, values,
                        ItemDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
            } while (c.moveToNext());
        }
        c.close();

        db.execSQL(DATABASE_UPDATE_2_TO_3_2);
        db.execSQL(DATABASE_CREATE_TABLE_ITEM);
        db.execSQL(DATABASE_UPDATE_2_TO_3_3);
        db.execSQL(DATABASE_UPDATE_2_TO_3_4);
    }

    /*** when app was English only ***/
    private void upgradeVersion2(SQLiteDatabase db) {
        db.execSQL(DATABASE_UPDATE_1_TO_2);

        Cursor c = db.query(ItemDBAdapter.TABLE_NAME,
                new String[]{ItemDBAdapter.COL_ID, ItemDBAdapter.COL_CATEGORY} ,
                null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                String catName = c.getString(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY));
                int colId = c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID));
                ContentValues values = new ContentValues();
                String[] defaultCategory = _context.getResources().getStringArray(R.array.default_category);
                int catCode = 0;

                for (int i=0; i<defaultCategory.length; ++i) {
                    if (catName.equalsIgnoreCase(defaultCategory[i])) {
                        catCode = i;
                    } else if (catName.equalsIgnoreCase("Until")) {
                        catCode = 3;
                    }
                }

                values.put(ItemDBAdapter.COL_CATEGORY_CODE, catCode);
                db.update(ItemDBAdapter.TABLE_NAME, values,
                        ItemDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
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
