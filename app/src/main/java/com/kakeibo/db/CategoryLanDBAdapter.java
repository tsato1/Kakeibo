package com.kakeibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kakeibo.KkbCategory;

public class CategoryLanDBAdapter extends DBAdapter {
    private static final String TAG = CategoryLanDBAdapter.class.getSimpleName();

    public static final String TABLE_NAME = "categories_lan";
    public static final String COL_ID = "_id";
    public static final String COL_CODE = "code";
    public static final String COL_NAME = "name"; //deprecated in dbv=6
    public static final String COL_ARA = "ara";
    public static final String COL_ENG = "eng";
    public static final String COL_SPA = "spa";
    public static final String COL_FRA = "fra";
    public static final String COL_HIN = "hin";
    public static final String COL_IND = "ind";
    public static final String COL_ITA = "ita";
    public static final String COL_JPN = "jpn";
    public static final String COL_KOR = "kor";
    public static final String COL_POL = "pol";
    public static final String COL_POR = "por";
    public static final String COL_RUS = "rus";
    public static final String COL_TUR = "tur";
    public static final String COL_VIE = "vie";
    public static final String COL_Hans = "Hans";
    public static final String COL_Hant = "Hant";
    public static final String COL_EN = "en"; // deprecated in dbv=6
    public static final String COL_ES = "es"; // deprecated in dbv=6
    public static final String COL_FR = "fr"; // deprecated in dbv=6
    public static final String COL_IT = "it"; // deprecated in dbv=6
    public static final String COL_JA = "ja"; // deprecated in dbv=6
    public static final String COL_SAVED_DATE = "saved_date"; // deprecated in dbv=6


    private SQLiteDatabase _db;

    public CategoryLanDBAdapter() {
    }

    public CategoryLanDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public boolean deleteItem(int id) {
        return _db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }

    public Cursor getAllCategoryStrs(String langCode) {
        String query = "SELECT "+langCode+" FROM " + TABLE_NAME + " ORDER BY " + COL_CODE;
        return _db.rawQuery(query, new String[]{});
    }

    public void saveCategoryLan(KkbCategory category) {
        ContentValues values = new ContentValues();
        values.put(COL_CODE, category.getCode());
        _db.insertOrThrow(TABLE_NAME, null, values);

        Log.d(TAG, "saveItem() called");
    }
}
