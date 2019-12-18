package com.kakeibo.db;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CategoriesDspDBAdapter {
    private static final String TAG = CategoriesDspDBAdapter.class.getSimpleName();

    public static final String TABLE_NAME = "categories_dsp";
    public static final String COL_ID = "_id";
    public static final String COL_LOCATION = "location";
    public static final String COL_CODE = "code";

    private SQLiteDatabase _db;

    public CategoriesDspDBAdapter open() throws SQLException {
        _db = DBAdapter.getInstance().openDatabase();
        return this;
    }

    public void close() {
        DBAdapter.getInstance().closeDatabase();
    }

    public boolean deleteItem(int id) {
        return _db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }

    public Cursor getDspCategoryCodes() {
        String query = "SELECT "+COL_CODE+" FROM "+ TABLE_NAME +" ORDER BY " + COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }

    public Cursor getKkbDspCategories(String langCode) {
        String query =
                "SELECT " +
                        CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_CODE+","+
                        langCode+","+
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_DRAWABLE+","+
                        CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_LOCATION+","+
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_PARENT+
                " FROM " + TABLE_NAME +
                " INNER JOIN " + CategoriesLanDBAdapter.TABLE_NAME +
                " ON " +
                        CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_CODE + "=" +
                        CategoriesLanDBAdapter.TABLE_NAME+"."+CategoriesLanDBAdapter.COL_CODE +
                " INNER JOIN " + CategoriesDBAdapter.TABLE_NAME +
                " ON " +
                        CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_CODE +"=" +
                        CategoriesDBAdapter.TABLE_NAME+"."+CategoriesDBAdapter.COL_CODE +
                " ORDER BY " + CategoriesDspDBAdapter.TABLE_NAME+"."+CategoriesDspDBAdapter.COL_LOCATION;
        return _db.rawQuery(query, new String[]{});
    }
}