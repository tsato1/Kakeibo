package com.kakeibo.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kakeibo.KkbCategory;
import com.kakeibo.db.CategoriesDBAdapter;
import com.kakeibo.db.CategoriesDspDBAdapter;
import com.kakeibo.db.CategoriesLanDBAdapter;

import java.util.ArrayList;
import java.util.List;

public class UtilCategory {
    private static String TAG = UtilCategory.class.getSimpleName();

    private static List<String> sAllCategoryStrList = new ArrayList<>(); //key(i)=category code
    private static List<String> sDspCategoryStrList = new ArrayList<>(); //key(i)=location
    private static List<KkbCategory> sAllKkbCategoryList = new ArrayList<>();//key(i)=category code
    private static List<KkbCategory> sDspKkbCategoryList = new ArrayList<>();//key(i)=location

    public static int numCategories = 16;

    public static void reloadCategoryLists(Context context) {
        setAllCategoryStrList(context);
        setAllKkbCategoryList(context);
        setDspCategoryStrList(context);
        setDspKkbCategoryList(context);
    }

    private static void setAllCategoryStrList(Context context) {
        Log.d(TAG, "setAllCategoryStrList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);
        Log.d(TAG, "langCode="+langCode);

        sAllCategoryStrList.clear();
        CategoriesLanDBAdapter categoriesLanDBAdapter = new CategoriesLanDBAdapter();
        categoriesLanDBAdapter.open();
        Cursor c = categoriesLanDBAdapter.getAllCategoryStrs(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                sAllCategoryStrList.add(c.getString(0));
            } while (c.moveToNext());
        }
        categoriesLanDBAdapter.close();
    }

    private static void setDspCategoryStrList(Context context) {
        Log.d(TAG, "setDspCategoryStrList() called");
    }

    private static void setAllKkbCategoryList(Context context) {
        Log.d(TAG, "setAllKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sAllKkbCategoryList.clear();
        CategoriesDBAdapter categoriesDBAdapter = new CategoriesDBAdapter();
        categoriesDBAdapter.open();
        Cursor c = categoriesDBAdapter.getAllKkbCategories(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is name (colum name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_DRAWABLE)),
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_LOCATION)),
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_PARENT)),
                        "",
                        "");
                sAllKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoriesDBAdapter.close();
    }

    private static void setDspKkbCategoryList(Context context) {
        Log.d(TAG, "setDspKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sDspKkbCategoryList.clear();
        CategoriesDspDBAdapter categoriesDspDBAdapter = new CategoriesDspDBAdapter();
        categoriesDspDBAdapter.open();
        Cursor c = categoriesDspDBAdapter.getKkbDspCategories(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is name (colum name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_DRAWABLE)),
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_LOCATION)),
                        c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_PARENT)),
                        "",
                        "");
                sDspKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoriesDspDBAdapter.close();
    }

    /*** category string list ***/
    public static List<String> getAllCategoryStrList(Context context) {
        if (sAllCategoryStrList !=null && !sAllCategoryStrList.isEmpty()) return sAllCategoryStrList;

        setAllCategoryStrList(context);

        return sAllCategoryStrList;
    }

    public static List<String> getDspCategoryStrList(Context context) {
        if (sDspCategoryStrList !=null && !sDspCategoryStrList.isEmpty()) return sDspCategoryStrList;

        setDspCategoryStrList(context);

        return sDspCategoryStrList;
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

    public static String getCategory(Context context, int ctgrCode) {
        if (sAllCategoryStrList ==null || sAllCategoryStrList.isEmpty()) {
            setAllCategoryStrList(context);
        }

        return sAllCategoryStrList.get(ctgrCode);
    }
}
