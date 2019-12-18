package com.kakeibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kakeibo.KkbCategory;


public class CategoriesDBAdapter extends DBAdapter {
    private static final String TAG = CategoriesDBAdapter.class.getSimpleName();

    public static final String TABLE_NAME = "categories";
    public static final String COL_ID = "_id";
    public static final String COL_CODE = "code";
    public static final String COL_NAME = "name";                       //deprecated on dbv=6
    public static final String COL_COLOR = "color"; // 0=income color, 1=expense color, 11-20=custom
    public static final String COL_SIGNIFICANCE = "sign"; //0=insignificant 1=mid 2=significant
    public static final String COL_DRAWABLE = "drawable";
    public static final String COL_LOCATION = "location";               //deprecated on dbv=6
    public static final String COL_SUB_CATEGORIES = "sub_categories";   //deprecated on dbv=6
    public static final String COL_PARENT = "parent";
    public static final String COL_DESC = "description";
    public static final String COL_VAL1 = "val1";
    public static final String COL_VAL2 = "val2";
    public static final String COL_VAL3 = "val3";
    public static final String COL_SAVED_DATE = "saved_date"; //default=""

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
        return _db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }

    public Cursor getParentCategories() { // todo name it getAllCategories and do it in DspAdapter
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getAllKkbCategories(String langCode) {
        String query =
                "SELECT "+
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_CODE+","+
                        langCode+","+
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_DRAWABLE+","+
                        CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_LOCATION+","+
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_PARENT+
                " FROM " + TABLE_NAME +
                " INNER JOIN " + CategoriesLanDBAdapter.TABLE_NAME +
                " ON " +
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_CODE + "=" +
                        CategoriesLanDBAdapter.TABLE_NAME+"."+CategoriesLanDBAdapter.COL_CODE +
                " INNER JOIN " + CategoriesDspDBAdapter.TABLE_NAME +
                " ON " +
                        CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_CODE+"="+
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_CODE +
                " ORDER BY " + CategoriesDspDBAdapter.TABLE_NAME+"."+COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }

    public void saveCategory(KkbCategory category) {
        ContentValues values = new ContentValues();
        values.put(COL_CODE, category.getCode());
        values.put(COL_COLOR, category.getColor());
        values.put(COL_SIGNIFICANCE, category.getSignificance());
        values.put(COL_DRAWABLE, category.getDrawable());
        values.put(COL_LOCATION, category.getLocation());
        values.put(COL_PARENT, category.getParent());
        values.put(COL_DESC, category.getDescription());
        values.put(COL_SAVED_DATE, category.getSavedDate());

        SQLiteDatabase db = DBAdapter.getInstance().openDatabase();
        db.insertOrThrow(TABLE_NAME, null, values);
        DBAdapter.getInstance().closeDatabase();

        Log.d(TAG, "saveItem() called");
    }
}
