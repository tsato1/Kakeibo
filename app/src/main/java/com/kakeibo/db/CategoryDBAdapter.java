//package com.kakeibo.db;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.DatabaseUtils;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import com.kakeibo.util.UtilDate;
//
//import java.sql.Blob;
//
//
//public class CategoryDBAdapter extends DBAdapter {
//    private static final String TAG = CategoryDBAdapter.class.getSimpleName();
//
//    public static final String TABLE_NAME = "categories";
//    public static final String COL_ID = "_id";
//    public static final String COL_CODE = "code";
//    public static final String COL_NAME = "name";  //deprecated on dbv=6 // re-added on dbv=7
//    public static final String COL_COLOR = "color"; // 1=income, 0=expense, 11-20=custom
//    public static final String COL_SIGNIFICANCE = "sign"; //0=insignificant 1=mid 2=significant
//    public static final String COL_DRAWABLE = "drawable";
//    public static final String COL_IMAGE = "image";                     //added on dbv=6
//    public static final String COL_LOCATION = "location";               //deprecated on dbv=6
//    public static final String COL_SUB_CATEGORIES = "sub_categories";   //deprecated on dbv=6
//    public static final String COL_PARENT = "parent";
//    public static final String COL_DESC = "description";
//    public static final String COL_VAL1 = "val1";
//    public static final String COL_VAL2 = "val2";
//    public static final String COL_VAL3 = "val3";
//    public static final String COL_SAVED_DATE = "saved_date"; //default=""
//
////    private SQLiteDatabase _db;
////
////    public CategoryDBAdapter() {
////    }
////
////    public CategoryDBAdapter open() throws SQLException {
////        _db = DBAdapter.getInstance().openDatabase();
////        return this;
////    }
////
////    public void close() {
////        DBAdapter.getInstance().closeDatabase();
////    }
////
////    public boolean deleteItem(int categoryCode) {
////        return _db.delete(TABLE_NAME, COL_CODE + "=" + categoryCode, null) > 0;
////    }
////
////    public Cursor getParentCategories() {
////        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_LOCATION;
////        return _db.rawQuery(query, new String[]{});
////    }
////
////    public Cursor getAllKkbCategories(String langCode) {
////        String query =
////                "SELECT "+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_ID+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE+","+
////                        langCode+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+
////                " FROM " + TABLE_NAME +
////                " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
////                " ON " +
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
////                        CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
////                " ORDER BY " + CategoryDBAdapter.TABLE_NAME+"."+COL_CODE;
////        return _db.rawQuery(query, new String[]{});
////    }
////
////    public Cursor getNonDspKkbCategories(String langCode) {
////        String query =
////                "SELECT "+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_ID+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE+","+
////                        langCode+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_COLOR+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_DRAWABLE+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_IMAGE+","+
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_PARENT+
////                " FROM " + TABLE_NAME +
////                " INNER JOIN " + CategoryLanDBAdapter.TABLE_NAME +
////                " ON " +
////                        CategoryDBAdapter.TABLE_NAME+"."+ CategoryDBAdapter.COL_CODE + "=" +
////                        CategoryLanDBAdapter.TABLE_NAME+"."+ CategoryLanDBAdapter.COL_CODE +
////                " WHERE " + TABLE_NAME+"."+CategoryDBAdapter.COL_CODE +
////                " NOT IN " +
////                " (" +
////                        " SELECT " + CategoryDspDBAdapter.COL_CODE +
////                        " FROM " + CategoryDspDBAdapter.TABLE_NAME +
////                ")"+
////                " ORDER BY " + CategoryDBAdapter.TABLE_NAME+"."+COL_CODE;
////        return _db.rawQuery(query, new String[]{});
////    }
////
////    public Cursor getUpdateDate(int categoryCode) {
////        String query = "SELECT " + COL_SAVED_DATE + " FROM " + TABLE_NAME + " WHERE " + COL_CODE + "=" + categoryCode;
////        return _db.rawQuery(query, new String[]{});
////    }
////
////    public long saveCategory(KkbCategory category) {
////        ContentValues values = new ContentValues();
////        values.put(COL_CODE, category.getCode());
////        values.put(COL_COLOR, category.getColor());
////        values.put(COL_SIGNIFICANCE, category.getSignificance());
////        values.put(COL_DRAWABLE, category.getDrawable());
////        values.put(COL_IMAGE, category.getImage());
//////        values.put(COL_LOCATION, category.getLocation()); //deprecated
////        values.put(COL_PARENT, category.getParent());
////        values.put(COL_DESC, category.getDescription());
////        values.put(COL_SAVED_DATE, UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS));
////
////        SQLiteDatabase db = DBAdapter.getInstance().openDatabase();
////        long row = db.insertOrThrow(TABLE_NAME, null, values);
////        DBAdapter.getInstance().closeDatabase();
////
////        Log.d(TAG, "saveItem() called");
////        return row;
////    }
////
////    public int editCategory(KkbCategory kkbCategory) {
////        ContentValues values = new ContentValues();
////        values.put(COL_COLOR, kkbCategory.getColor());
////        values.put(COL_SIGNIFICANCE, kkbCategory.getSignificance());
////        values.put(COL_DRAWABLE, kkbCategory.getDrawable());
////        values.put(COL_IMAGE, kkbCategory.getImage());
//////        values.put(COL_LOCATION, category.getLocation()); //deprecated
////        values.put(COL_PARENT, kkbCategory.getParent());
////        values.put(COL_DESC, kkbCategory.getDescription());
////        values.put(COL_SAVED_DATE, UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS));
////
////        return _db.update(TABLE_NAME, values, COL_CODE+"="+kkbCategory.getCode(), null);
////    }
////
////    /***
////     * user-created categories should be having the CODE between 1000 and 2000
////     *
////     * this fun returns the CODE that doesn't exist in the current table and therefore can be used for a newly created category
////     * ***/
////    public Cursor calcCodeForNewCategory() {
////        String query =
////                "SELECT "+ COL_CODE + " + 1" +
////                        " FROM " + TABLE_NAME + " t " +
////                        " WHERE NOT EXISTS " +
////                        "(" +
////                        "   SELECT " + COL_CODE +
////                        "   FROM " + TABLE_NAME +
////                        "   WHERE " + COL_CODE + " = t." + COL_CODE + " + 1" +
////                        ")" +
////                        " AND " + COL_CODE +" BETWEEN 1000 and 2000" +
////                        " ORDER BY " + COL_CODE +
////                        " LIMIT 1";
////        return _db.rawQuery(query, new String[]{});
////    }
//}
