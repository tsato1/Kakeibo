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

    private static List<String> mAllCategoryStrList = new ArrayList<>(); //key(i)=category code
    private static List<String> mDspCategoryStrList = new ArrayList<>(); //key(i)=location
    private static List<KkbCategory> mAllKkbCategoryList = new ArrayList<>();//key(i)=category code
    private static List<KkbCategory> mDspKkbCategoryList = new ArrayList<>();//key(i)=location

    public static int numCategories = 16;

    public static void resetCategoryLists(Context context) {
        setAllCategoryStrList(context);
        setAllKkbCategoryList(context);
        setDspCategoryStrList(context);
        setDspKkbCategoryList(context);
    }

    public static void setAllCategoryStrList(Context context) {
        Log.d(TAG, "setAllCategoryStrList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);
        Log.d(TAG, "langCode="+langCode);

        mAllCategoryStrList.clear();
        CategoriesLanDBAdapter categoriesLanDBAdapter = new CategoriesLanDBAdapter();
        categoriesLanDBAdapter.open();
        Cursor c = categoriesLanDBAdapter.getAllCategoryStrs(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                mAllCategoryStrList.add(c.getString(0));
            } while (c.moveToNext());
        }
        categoriesLanDBAdapter.close();
    }
    public static void setDspCategoryStrList(Context context) {
        Log.d(TAG, "setDspCategoryStrList() called");
    }

    public static void setAllKkbCategoryList(Context context) {
        Log.d(TAG, "setAllKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        mAllKkbCategoryList.clear();
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
                mAllKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoriesDBAdapter.close();
    }
    public static void setDspKkbCategoryList(Context context) {
        Log.d(TAG, "setDspKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        mDspKkbCategoryList.clear();
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
                mDspKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoriesDspDBAdapter.close();
    }


    /*** category string list ***/
    public static List<String> getAllCategoryStrList(Context context) {
        if (mAllCategoryStrList !=null && !mAllCategoryStrList.isEmpty()) return mAllCategoryStrList;

        setAllCategoryStrList(context);

        return mAllCategoryStrList;
    }
    public static List<String> getDspCategoryStrList(Context context) {
        if (mDspCategoryStrList !=null && !mDspCategoryStrList.isEmpty()) return mDspCategoryStrList;

        setDspCategoryStrList(context);

        return mDspCategoryStrList;
    }

    /*** KkbCateogry list ***/
    public static List<KkbCategory> getAllKkbCategoryList(Context context) {
        if (mAllKkbCategoryList !=null && !mAllKkbCategoryList.isEmpty()) return mAllKkbCategoryList;

        setAllKkbCategoryList(context);

        return mAllKkbCategoryList;
    }
    public static List<KkbCategory> getDspKkbCategoryList(Context context) {
        if (mDspKkbCategoryList !=null && !mDspKkbCategoryList.isEmpty()) return mDspKkbCategoryList;

        setDspKkbCategoryList(context);

        return mDspKkbCategoryList;
    }

    public static String getCategory(Context context, int ctgrCode) {
        if (mAllCategoryStrList ==null || mAllCategoryStrList.isEmpty()) {
            setAllCategoryStrList(context);
        }

        return mAllCategoryStrList.get(ctgrCode);
    }
}
