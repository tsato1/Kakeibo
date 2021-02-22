package com.kakeibo.data.disk;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kakeibo.data.CategoryDspStatus;
import com.kakeibo.db.CategoryDBAdapter;
import com.kakeibo.db.CategoryDspDBAdapter;
import com.kakeibo.db.CategoryLanDBAdapter;
import com.kakeibo.db.ItemDBAdapter;
import com.kakeibo.db.KkbAppDBAdapter;
import com.kakeibo.db.PrepDB;
import com.kakeibo.util.UtilCurrency;

import java.math.BigDecimal;

import static com.kakeibo.db.KkbAppDBAdapter.TABLE_KKBAPP;

/**
 * Helper class for working with the SQLiteDatabase.
 */
public class SqliteDatabaseTestHelper {
    /*** to insert item to db of versions v3 - v6 ***/
    public static void insertItemStatusToSqlite_v3_6(
            int id,
            BigDecimal amount,
            String currencyCode,
            int fractionDigits,
            int categoryCode,
            String memo,
            String eventDate,
            String updateDate,
            SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemDBAdapter.COL_ID, id);
        values.put(ItemDBAdapter.COL_AMOUNT, UtilCurrency.getLongAmountFromBigDecimal(amount, fractionDigits));
        values.put(ItemDBAdapter.COL_CURRENCY_CODE, currencyCode);
        values.put(ItemDBAdapter.COL_CATEGORY_CODE, categoryCode);
        values.put(ItemDBAdapter.COL_MEMO, memo);
        values.put(ItemDBAdapter.COL_EVENT_DATE, eventDate);
        values.put(ItemDBAdapter.COL_UPDATE_DATE, updateDate);
        db.insertWithOnConflict(ItemDBAdapter.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /*** to insert item to db of versions v1 and v2 ***/
    public static void insertItemStatusToSqlite_v1_2(
            int id,
            String amount,
            String category,
            String memo,
            String eventD,
            String eventYM,
            String updateDate,
            SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemDBAdapter.COL_ID, id);
        values.put(ItemDBAdapter.COL_AMOUNT, amount);
        values.put(ItemDBAdapter.COL_CATEGORY, category);
        values.put(ItemDBAdapter.COL_MEMO, memo);
        values.put(ItemDBAdapter.COL_EVENT_D, eventD);
        values.put(ItemDBAdapter.COL_EVENT_YM, eventYM);
        values.put(ItemDBAdapter.COL_UPDATE_DATE, updateDate);
        db.insertWithOnConflict(ItemDBAdapter.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public static void insertCategoryStatus_v5(
            int id,
            int code,
            String name,
            int color,
            int drawable,
            int location,
            int subCategories,
            String description,
            String savedDate,
            SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryDBAdapter.COL_ID, id);
        values.put(CategoryDBAdapter.COL_CODE, code);
        values.put(CategoryDBAdapter.COL_NAME, name);
        values.put(CategoryDBAdapter.COL_COLOR, color);
        values.put(CategoryDBAdapter.COL_DRAWABLE, drawable);
        values.put(CategoryDBAdapter.COL_LOCATION, location);
        values.put(CategoryDBAdapter.COL_SUB_CATEGORIES, subCategories);
        values.put(CategoryDBAdapter.COL_DESC, description);
        values.put(CategoryDBAdapter.COL_SAVED_DATE, savedDate);
        db.insertWithOnConflict(CategoryDBAdapter.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public static void insertCategoryStatus_v6(
            int id,
            int code,
            String name,
            int color,
            int significance,
            int drawable,
            byte[] image,
            int parent,
            String description,
            String savedDate,
            SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryDBAdapter.COL_ID, id);
        values.put(CategoryDBAdapter.COL_CODE, code);
        values.put(CategoryDBAdapter.COL_COLOR, color);
        values.put(CategoryDBAdapter.COL_SIGNIFICANCE, significance);
        values.put(CategoryDBAdapter.COL_DRAWABLE, drawable);
        values.put(CategoryDBAdapter.COL_IMAGE, image);
        values.put(CategoryDBAdapter.COL_PARENT, parent);
        values.put(CategoryDBAdapter.COL_DESC, description);
        values.put(CategoryDBAdapter.COL_SAVED_DATE, savedDate);
        db.insertWithOnConflict(CategoryDBAdapter.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

//    public static void insertCategoryLanStatus(CategoryLanStatus categoryLanStatus, SqliteTestDbOpenHelper helper) {
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(CategoryLanDBAdapter.COL_ID, categoryLanStatus.getId());
//        values.put(CategoryLanDBAdapter.COL_CODE, categoryLanStatus.getCode());
//        values.put(CategoryLanDBAdapter.COL_ARA, categoryLanStatus.getAra());
//        values.put(CategoryLanDBAdapter.COL_ENG, categoryLanStatus.getEng());
//        values.put(CategoryLanDBAdapter.COL_SPA, categoryLanStatus.getSpa());
//        values.put(CategoryLanDBAdapter.COL_FRA, categoryLanStatus.getFra());
//        values.put(CategoryLanDBAdapter.COL_HIN, categoryLanStatus.getHin());
//        values.put(CategoryLanDBAdapter.COL_IND, categoryLanStatus.getInd());
//        values.put(CategoryLanDBAdapter.COL_ITA, categoryLanStatus.getIta());
//        values.put(CategoryLanDBAdapter.COL_JPN, categoryLanStatus.getJpn());
//        values.put(CategoryLanDBAdapter.COL_KOR, categoryLanStatus.getKor());
//        values.put(CategoryLanDBAdapter.COL_POL, categoryLanStatus.getPol());
//        values.put(CategoryLanDBAdapter.COL_POR, categoryLanStatus.getPor());
//        values.put(CategoryLanDBAdapter.COL_RUS, categoryLanStatus.getRus());
//        values.put(CategoryLanDBAdapter.COL_TUR, categoryLanStatus.getTur());
//        values.put(CategoryLanDBAdapter.COL_VIE, categoryLanStatus.getVie());
//        values.put(CategoryLanDBAdapter.COL_Hans, categoryLanStatus.getHans());
//        values.put(CategoryLanDBAdapter.COL_Hant, categoryLanStatus.getHant());
//        db.insertWithOnConflict(CategoryLanDBAdapter.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//        db.close();
//    }

    public static void insertCategoryDspStatus(CategoryDspStatus categoryDspStatus, SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryDspDBAdapter.COL_ID, categoryDspStatus.getId());
        values.put(CategoryDspDBAdapter.COL_CODE, categoryDspStatus.getCode());
        values.put(CategoryDspDBAdapter.COL_LOCATION, categoryDspStatus.getLocation());
        db.insertWithOnConflict(CategoryDspDBAdapter.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public static void insertKkbApp(
            int id,
            String name,
            String type,
            String updateDate,
            int intVal1, int intVal2, int intVal3,
            String strVal1, String strVal2, String strVal3,
            SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KkbAppDBAdapter.COL_ID, id);
        values.put(KkbAppDBAdapter.COL_NAME, name);
        values.put(KkbAppDBAdapter.COL_TYPE, type);
        values.put(KkbAppDBAdapter.COL_UPDATE_DATE, updateDate);
        values.put(KkbAppDBAdapter.COL_VAL_INT_1, intVal1);
        values.put(KkbAppDBAdapter.COL_VAL_INT_2, intVal2);
        values.put(KkbAppDBAdapter.COL_VAL_INT_3, intVal3);
        values.put(KkbAppDBAdapter.COL_VAL_STR_1, strVal1);
        values.put(KkbAppDBAdapter.COL_VAL_STR_2, strVal2);
        values.put(KkbAppDBAdapter.COL_VAL_STR_3, strVal3);
        db.insertWithOnConflict(TABLE_KKBAPP, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public static void createKkbAppTable(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("CREATE TABLE " + TABLE_KKBAPP + " (" +
                KkbAppDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                KkbAppDBAdapter.COL_NAME + " TEXT NOT NULL," +
                KkbAppDBAdapter.COL_TYPE + " TEXT NOT NULL," +
                KkbAppDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL," +
                KkbAppDBAdapter.COL_VAL_INT_1 + " INTEGER DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_INT_2 + " INTEGER DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_INT_3 + " INTEGER DEFAULT 0," +
                KkbAppDBAdapter.COL_VAL_STR_1 + " TEXT NOT NULL," +
                KkbAppDBAdapter.COL_VAL_STR_2 + " TEXT NOT NULL," +
                KkbAppDBAdapter.COL_VAL_STR_3 + " TEXT NOT NULL);");

        db.close();
    }

    public static void createItemsTable(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ItemDBAdapter.TABLE_NAME + " ("
                + ItemDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemDBAdapter.COL_AMOUNT + " TEXT NOT NULL,"
                + ItemDBAdapter.COL_CATEGORY + " TEXT NOT NULL,"
                + ItemDBAdapter.COL_MEMO + " TEXT NOT NULL,"
                + ItemDBAdapter.COL_EVENT_D + " TEXT NOT NULL,"
                + ItemDBAdapter.COL_EVENT_YM + " TEXT NOT NULL,"
                + ItemDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);");

        db.close();
    }

    public static void createCategoryDspTable(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL("CREATE TABLE " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                CategoryDspDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                CategoryDspDBAdapter.COL_LOCATION + " INTEGER DEFAULT 0," +
                CategoryDspDBAdapter.COL_CODE + " INTEGER DEFAULT 0);");
    }

    public static void createCategoriesTable(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL(
                "CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
                        CategoryDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        CategoryDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                        CategoryDBAdapter.COL_NAME + " TEXT NOT NULL," +
                        CategoryDBAdapter.COL_COLOR + " INTEGER NOT NULL DEFAULT 0," +
                        CategoryDBAdapter.COL_DRAWABLE + " INTEGER NOT NULL DEFAULT 0," +
                        CategoryDBAdapter.COL_LOCATION + " INTEGER NOT NULL DEFAULT 0," +
                        CategoryDBAdapter.COL_SUB_CATEGORIES + " INTEGER NOT NULL DEFAULT 0," +
                        CategoryDBAdapter.COL_DESC + " TEXT NOT NULL," +
                        CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL);");
        database.execSQL(
                "CREATE TABLE " + CategoryLanDBAdapter.TABLE_NAME + " (" +
                        CategoryLanDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        CategoryLanDBAdapter.COL_CODE + " INTEGER NOT NULL DEFAULT 0," +
                        CategoryLanDBAdapter.COL_NAME + " TEXT NOT NULL," +
                        CategoryLanDBAdapter.COL_EN + " TEXT NOT NULL," +
                        CategoryLanDBAdapter.COL_ES + " TEXT NOT NULL," +
                        CategoryLanDBAdapter.COL_FR + " TEXT NOT NULL," +
                        CategoryLanDBAdapter.COL_IT + " TEXT NOT NULL," +
                        CategoryLanDBAdapter.COL_JA + " TEXT NOT NULL," +
                        CategoryLanDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL);");
        PrepDB.initCategoriesTable(database);
    }

    public static void upgrade_items_1_3(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase database = helper.getWritableDatabase();

        /*** item table
         * migration_1_2
         * ***/
        database.execSQL("ALTER TABLE " + ItemDBAdapter.TABLE_NAME +
                " ADD COLUMN " + ItemDBAdapter.COL_CATEGORY_CODE + " INTEGER NOT NULL DEFAULT 0;");

        //not necessary because no item exists in the table
//        Cursor c = database.query("SELECT " + ItemDBAdapter.COL_ID + ", " +
//                ItemDBAdapter.COL_CATEGORY + " FROM " + ItemDBAdapter.TABLE_NAME);
        Cursor c = database.rawQuery("SELECT " + ItemDBAdapter.COL_ID + ", " +
                ItemDBAdapter.COL_CATEGORY + " FROM " + ItemDBAdapter.TABLE_NAME, new String[]{});

        if (c.moveToFirst()) {
            do {
                String catName = c.getString(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY));
                int colId = c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID));
                ContentValues values = new ContentValues();
                String[] defaultCategory = new String[]{"Income", "Comm", "Meal", "Until", "Health", "Edu", "Cloth", "Trans", "Ent", "Ins", "Tax", "Other"};
                int catCode = 0;

                for (int i=0; i<defaultCategory.length; ++i) {
                    if (catName.equalsIgnoreCase(defaultCategory[i])) {
                        catCode = i;
                    } else if (catName.equalsIgnoreCase("Until")) {
                        catCode = 3;
                    }
                }

                values.put(ItemDBAdapter.COL_CATEGORY_CODE, catCode);
                database.update(ItemDBAdapter.TABLE_NAME, values,
                        ItemDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
            } while (c.moveToNext());
        }
        c.close();
        /*** item table
         * migration_2_3
         * ***/
        database.execSQL("ALTER TABLE " + ItemDBAdapter.TABLE_NAME +
                " ADD COLUMN " + ItemDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL DEFAULT '';");
        Cursor c2 = database.rawQuery(
                "SELECT " + ItemDBAdapter.COL_ID + ", " +
                        ItemDBAdapter.COL_AMOUNT + ", " +
                        ItemDBAdapter.COL_EVENT_D + ", " +
                        ItemDBAdapter.COL_EVENT_YM + "," +
                        ItemDBAdapter.COL_UPDATE_DATE +
                        " FROM " + ItemDBAdapter.TABLE_NAME, new String[]{});

        if (c2.moveToFirst()) {
            do {
                String eventD = c2.getString(c2.getColumnIndex(ItemDBAdapter.COL_EVENT_D));
                String eventYM = c2.getString(c2.getColumnIndex(ItemDBAdapter.COL_EVENT_YM));
                String eventDate;
                String updateDate = c2.getString(c2.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE));
                int colId = c2.getInt(c2.getColumnIndex(ItemDBAdapter.COL_ID));
                ContentValues values = new ContentValues();

                /*** event_date ***/
                eventDate = eventYM.replace('/','-') + "-" + eventD;
                values.put(ItemDBAdapter.COL_EVENT_DATE, eventDate);

                /*** update_date ***/
                updateDate = updateDate.split(" ")[0].replace('/','-') + " 00:00:00";
                values.put(ItemDBAdapter.COL_UPDATE_DATE, updateDate);

                /*** flipping negative to positive***/
                String amount = c2.getString(c2.getColumnIndex(ItemDBAdapter.COL_AMOUNT));
                int newAmount = Math.abs(Integer.parseInt(amount));
                values.put(ItemDBAdapter.COL_AMOUNT, newAmount*1000);

                /*** reflecting the result to db ***/
//                database.update(ItemDBAdapter.TABLE_NAME, OnConflictStrategy.REPLACE, values,
//                        ItemDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
                database.update(ItemDBAdapter.TABLE_NAME, values,
                        ItemDBAdapter.COL_ID+"=?", new String[] {String.valueOf(colId)});
            } while (c2.moveToNext());
        }
        c2.close();

        database.execSQL("ALTER TABLE " + ItemDBAdapter.TABLE_NAME + " RENAME TO " + ItemDBAdapter.TABLE_NAME + "_old;");
        database.execSQL("CREATE TABLE " + ItemDBAdapter.TABLE_NAME + " ("+
                ItemDBAdapter.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                ItemDBAdapter.COL_AMOUNT + " INTEGER NOT NULL," +
                ItemDBAdapter.COL_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '"+ UtilCurrency.CURRENCY_OLD+"', " + // to accommodate old versions code
                ItemDBAdapter.COL_CATEGORY_CODE + " INTEGER NOT NULL DEFAULT 0," +
                ItemDBAdapter.COL_MEMO + " TEXT NOT NULL," +
                ItemDBAdapter.COL_EVENT_DATE + " TEXT NOT NULL," +
                ItemDBAdapter.COL_UPDATE_DATE + " TEXT NOT NULL);");
        database.execSQL("INSERT INTO " + ItemDBAdapter.TABLE_NAME + " (" +
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
                ItemDBAdapter.COL_UPDATE_DATE+" FROM "+ ItemDBAdapter.TABLE_NAME +"_old;");
        database.execSQL("DROP TABLE "+ ItemDBAdapter.TABLE_NAME +"_old;");
    }

    public static void prepareTables(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase database = helper.getWritableDatabase();


    }

    public static void upgrade_categories_5_6(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase database = helper.getWritableDatabase();

        /*** CategoryDsp table ***/
        database.execSQL("CREATE TABLE " + CategoryDspDBAdapter.TABLE_NAME + " (" +
                CategoryDspDBAdapter.COL_ID + " INTEGER PRIMARY KEY," +
                CategoryDspDBAdapter.COL_LOCATION + " INTEGER DEFAULT 0," +
                CategoryDspDBAdapter.COL_CODE + " INTEGER DEFAULT 0);");

        database.execSQL("DROP TABLE "+ CategoryDBAdapter.TABLE_NAME);
        database.execSQL("CREATE TABLE " + CategoryDBAdapter.TABLE_NAME + " (" +
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
                CategoryDBAdapter.COL_SAVED_DATE + " TEXT NOT NULL);");

        database.execSQL("DROP TABLE "+ CategoryLanDBAdapter.TABLE_NAME);
        database.execSQL("CREATE TABLE " + CategoryLanDBAdapter.TABLE_NAME + " (" +
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
                CategoryLanDBAdapter.COL_Hant + " TEXT NOT NULL);");

        PrepDB.initCategoriesTableRevised(database);
        PrepDB.addMoreCategories(database);
    }

    /***
     * clearing database before ending test
     * @param helper
     */
    public static void clearDatabase(SqliteTestDbOpenHelper helper) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KKBAPP);
        db.execSQL("DROP TABLE IF EXISTS " + ItemDBAdapter.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS subscriptions");
        db.execSQL("DROP TABLE IF EXISTS " + CategoryDBAdapter.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryLanDBAdapter.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CategoryDspDBAdapter.TABLE_NAME);
        db.close();
    }
}