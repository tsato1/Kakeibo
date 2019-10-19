package com.kakeibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kakeibo.KkbCategory;

public class CategoriesLanDBAdapter extends DBAdapter {
    private static final String TAG = CategoriesLanDBAdapter.class.getSimpleName();

    public static final String TABLE_CATEGORY_LAN = "categories_lan";
    public static final String COL_ID = "_id";
    public static final String COL_CODE = "code";
    public static final String COL_NAME = "name";
    public static final String COL_EN = "en";
    public static final String COL_ES = "es";
    public static final String COL_FR = "fr";
    public static final String COL_IT = "it";
    public static final String COL_JA = "ja";
    public static final String COL_SAVED_DATE = "saved_date";

    private SQLiteDatabase _db;

    public CategoriesLanDBAdapter() {
    }

    public CategoriesLanDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public boolean deleteItem(int id) {
        return _db.delete(TABLE_CATEGORY_LAN, COL_ID + "=" + id, null) > 0;
    }

    public Cursor getAllOrderByCode() {
        String query = "SELECT * FROM " + TABLE_CATEGORY_LAN + " ORDER BY " + COL_CODE;
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getCategoryStrByCode(int catCode, String langCode) {
        String query = "SELECT "+langCode+" FROM " + TABLE_CATEGORY_LAN +
                " WHERE " + COL_CODE + "=" + catCode;
        return _db.rawQuery(query, new String[]{});
    }

    public void saveCategoryLan(KkbCategory category) {
        ContentValues values = new ContentValues();
        values.put(COL_CODE, category.getCode());
        values.put(COL_NAME, category.getName());
        values.put(COL_EN, category.getEN());
        values.put(COL_ES, category.getES());
        values.put(COL_FR, category.getFR());
        values.put(COL_IT, category.getIT());
        values.put(COL_JA, category.getJA());
        values.put(COL_SAVED_DATE, category.getSavedDate());
        _db.insertOrThrow(TABLE_CATEGORY_LAN, null, values);

        Log.d(TAG, "saveItem() called");
    }
}
