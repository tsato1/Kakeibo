package com.kakeibo.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kakeibo.KkbCategory;
import com.kakeibo.db.CategoryDBAdapter;
import com.kakeibo.db.CategoryDspDBAdapter;
import com.kakeibo.db.CategoryLanDBAdapter;

import java.util.ArrayList;
import java.util.List;

public class UtilCategory {
    private static String TAG = UtilCategory.class.getSimpleName();

    private static List<String> sAllCategoryStrList = new ArrayList<>(); //key(i)=category code
    private static List<String> sDspCategoryStrList = new ArrayList<>(); //key(i)=location
    private static List<Integer> sDspCategoryCodeList = new ArrayList<>();
    private static List<KkbCategory> sAllKkbCategoryList = new ArrayList<>();//key(i)=category code
    private static List<KkbCategory> sDspKkbCategoryList = new ArrayList<>();//key(i)=location
    private static List<KkbCategory> sNonDspKkbCategoryList = new ArrayList<>();//key: code, val: KkbCategory

    public static int numCategories = 16;

    public static void reloadCategoryLists(Context context) {
        setAllCategoryStrList(context);
        setAllKkbCategoryList(context);
        setDspKkbCategoryList(context);
    }

    private static void setAllCategoryStrList(Context context) {
        Log.d(TAG, "setAllCategoryStrList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);
        Log.d(TAG, "langCode="+langCode);

        sAllCategoryStrList.clear();
        CategoryLanDBAdapter categoryLanDBAdapter = new CategoryLanDBAdapter();
        categoryLanDBAdapter.open();
        Cursor c = categoryLanDBAdapter.getAllCategoryStrs(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                sAllCategoryStrList.add(c.getString(0));
            } while (c.moveToNext());
        }
        categoryLanDBAdapter.close();
    }

    private static void setAllKkbCategoryList(Context context) {
        Log.d(TAG, "setAllKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sAllKkbCategoryList.clear();
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        Cursor c = categoryDBAdapter.getAllKkbCategories(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is name (colum name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_LOCATION)),
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sAllKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoryDBAdapter.close();
    }

    private static void setDspKkbCategoryList(Context context) {
        Log.d(TAG, "setDspKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sDspKkbCategoryList.clear();
        sDspCategoryCodeList.clear();
        CategoryDspDBAdapter categoryDspDBAdapter = new CategoryDspDBAdapter();
        categoryDspDBAdapter.open();
        Cursor c = categoryDspDBAdapter.getKkbDspCategories(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is name (colum name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_LOCATION)),
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sDspKkbCategoryList.add(kkbCategory);
                sDspCategoryCodeList.add(kkbCategory.getCode());
            } while (c.moveToNext());
        }
        categoryDspDBAdapter.close();
    }

    private static void setNonDspKkbCategoryList(Context context) {
        Log.d(TAG, "setNonDspKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sNonDspKkbCategoryList.clear();
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        Cursor c = categoryDBAdapter.getNonDspKkbCategories(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is category name (colum name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sNonDspKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoryDBAdapter.close();
    }

    /*** category string list ***/
//    public static List<String> getAllCategoryStrList(Context context) {
//        if (sAllCategoryStrList !=null && !sAllCategoryStrList.isEmpty()) return sAllCategoryStrList;
//
//        setAllCategoryStrList(context);
//
//        return sAllCategoryStrList;
//    }
//
//    public static List<String> getDspCategoryStrList(Context context) {
//        if (sDspCategoryStrList !=null && !sDspCategoryStrList.isEmpty()) return sDspCategoryStrList;
//
//        setDspKkbCategoryList(context); // make changes to this function so that the list gets filled
//
//        return sDspCategoryStrList;
//    }

    public static List<Integer> getDspCategoryIntList(Context context) {
        if (sDspCategoryCodeList != null && !sDspCategoryCodeList.isEmpty()) return sDspCategoryCodeList;

        setDspKkbCategoryList(context);

        return sDspCategoryCodeList;
    }

    /*** KkbCateogry list ***/
    public static List<KkbCategory> getAllKkbCategoryList(Context context) {
        if (sAllKkbCategoryList !=null && !sAllKkbCategoryList.isEmpty()) return sAllKkbCategoryList;

        setAllKkbCategoryList(context);

        return sAllKkbCategoryList;
    }

    public static List<KkbCategory> getDspKkbCategoryList(Context context) {
        if (sDspKkbCategoryList !=null && !sDspKkbCategoryList.isEmpty()) return sDspKkbCategoryList;

        setDspKkbCategoryList(context);

        return sDspKkbCategoryList;
    }

    public static List<KkbCategory> getNonDspKkbCategoryList(Context context) {
        if (sNonDspKkbCategoryList != null && !sNonDspKkbCategoryList.isEmpty()) return sNonDspKkbCategoryList;

        setNonDspKkbCategoryList(context);

        return sNonDspKkbCategoryList;
    }

    public static String getCategory(Context context, int ctgrCode) {
        if (sAllCategoryStrList ==null || sAllCategoryStrList.isEmpty()) {
            setAllCategoryStrList(context);
        }

        return sAllCategoryStrList.get(ctgrCode);
    }
}
