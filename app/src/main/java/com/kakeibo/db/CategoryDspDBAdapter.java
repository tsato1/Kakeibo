package com.kakeibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kakeibo.KkbCategory;

public class CategoryDspDBAdapter {
    private static final String TAG = CategoryDspDBAdapter.class.getSimpleName();

    public static final String TABLE_NAME = "categories_dsp";
    public static final String COL_ID = "_id";
    public static final String COL_LOCATION = "location";
    public static final String COL_CODE = "code";

    private SQLiteDatabase _db;

    public CategoryDspDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public boolean deleteItem(int id) {
        return _db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }

    public boolean deleteAllItems() {
        return _db.delete(TABLE_NAME, null, null) > 0;
    }

    public Cursor getDspCategoryCodes() {
        String query = "SELECT "+COL_CODE+" FROM "+ TABLE_NAME +" ORDER BY " + COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getKkbDspCategories(String langCode) {
        String query =
                "SELECT " +
                        CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_CODE+","+
                        langCode+","+
                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
                        CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_LOCATION+","+
                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+
                " FROM " + TABLE_NAME +
                " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
                " ON " +
                        CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_CODE + "=" +
                        CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
                " INNER JOIN " + CategoryDBAdapter.TABLE_NAME +
                " ON " +
                        CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_CODE +"=" +
                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE +
                " ORDER BY " + CategoryDspDBAdapter.TABLE_NAME+"."+ CategoryDspDBAdapter.COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }

    public void saveItem(int location, KkbCategory category) {
        ContentValues values = new ContentValues();
        values.put(COL_CODE, category.getCode());
        values.put(COL_LOCATION, location);
        _db.insertOrThrow(TABLE_NAME, null, values);
    }
}