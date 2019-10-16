package com.kakeibo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kakeibo.KkbCategory;


public class CategoriesDBAdapter extends DBAdapter {

    private static final String TAG = CategoriesDBAdapter.class.getSimpleName();

    public static final String TABLE_CATEGORY = "categories";
    public static final String COL_ID = "_id";
    public static final String COL_CODE = "code";
    public static final String COL_NAME = "name";
    public static final String COL_COLOR = "color"; //0=insignificant 1=mid 2=significant
    public static final String COL_DRAWABLE = "drawable";
    public static final String COL_LOCATION = "location"; //location in GridView on fragment1
    public static final String COL_SUB_CATEGORIES = "sub_categories";
    public static final String COL_DESC = "description";
    public static final String COL_SAVED_DATE = "saved_date";

    private SQLiteDatabase _db;

    public CategoriesDBAdapter() {
    }

    public CategoriesDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public boolean deleteItem(int id) {
        return _db.delete(TABLE_CATEGORY, COL_ID + "=" + id, null) > 0;
    }

    public Cursor getParentCategories() {
        String query = "SELECT * FROM " + TABLE_CATEGORY + " ORDER BY " + COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }

    public void saveCategory(KkbCategory category) {
        ContentValues values = new ContentValues();
        values.put(COL_CODE, category.getCode());
        values.put(COL_NAME, category.getName());
        values.put(COL_COLOR, category.getColor());
        values.put(COL_DRAWABLE, category.getDrawable());
        values.put(COL_LOCATION, category.getLocation());
        values.put(COL_SUB_CATEGORIES, category.getSubCategories());
        values.put(COL_DESC, category.getDescription());
        values.put(COL_SAVED_DATE, category.getSavedDate());

        SQLiteDatabase db = DBAdapter.getInstance().openDatabase();
        db.insertOrThrow(TABLE_CATEGORY, null, values);
        DBAdapter.getInstance().closeDatabase();

        Log.d(TAG, "saveItem() called");
    }
}
