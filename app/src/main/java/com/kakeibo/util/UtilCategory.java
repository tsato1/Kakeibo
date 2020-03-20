package com.kakeibo.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseArray;

import com.kakeibo.KkbCategory;
import com.kakeibo.db.CategoryDBAdapter;
import com.kakeibo.db.CategoryDspDBAdapter;
import com.kakeibo.db.CategoryLanDBAdapter;
import com.kakeibo.db.TmpCategory;

import java.util.ArrayList;
import java.util.List;

public class UtilCategory {
    private static String TAG = UtilCategory.class.getSimpleName();

    private static List<String> sAllCategoryStrList = new ArrayList<>(); //key(i)=category code
    private static List<Integer> sDspCategoryCodeList = new ArrayList<>(); // ordered by location
    private static SparseArray< KkbCategory> sAllKkbCategoryArr = new SparseArray<>();
    private static List<KkbCategory> sAllKkbCategoryList = new ArrayList<>();//ordered by category code
    private static List<KkbCategory> sDspKkbCategoryList = new ArrayList<>();//ordered by location
    private static List<KkbCategory> sNonDspKkbCategoryList = new ArrayList<>();

    public static int NUM_MAX_DSP_CATEGORIES = 16;

    public static void reloadCategoryLists(Context context) {
        setAllCategoryStrList(context);
        setAllKkbCategoryArr(context);
        setAllKkbCategoryList(context);
        setDspKkbCategoryList(context);
        setNonDspKkbCategoryList(context);
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
                        c.getString(1), //second arg is category name in specified language (column name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sAllKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoryDBAdapter.close();
    }

    private static void setAllKkbCategoryArr(Context context) {
        Log.d(TAG, "setAllKkbCategoryArr() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sAllKkbCategoryArr.clear();
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        Cursor c = categoryDBAdapter.getAllKkbCategories(langCode);// ordered by category code
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is name (colum name is like 'jpn')
                        0,
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sAllKkbCategoryArr.append(kkbCategory.getCode(), kkbCategory);
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
        Cursor c = categoryDspDBAdapter.getKkbDspCategories(langCode); // ordered by location
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
                        c.getString(1), //second arg is category name (column name is like 'jpn')
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

    public static List<Integer> getDspCategoryCodeList(Context context) {
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

    public static int getCategoryDrawable(Context context, int categoryCode) {
        if (sAllKkbCategoryArr ==null || sAllKkbCategoryArr.size()==0) {
            setAllKkbCategoryArr(context);
        }

        return sAllKkbCategoryArr.get(categoryCode).getDrawable();
    }

    public static String getCategoryStr(Context context, int categoryCode) {
        if (sAllCategoryStrList ==null || sAllCategoryStrList.isEmpty()) {
            setAllCategoryStrList(context);
        }

        return sAllCategoryStrList.get(categoryCode);
    }

    public static void updateDspTable(Context context, List<Integer> categoryCodes) {
        CategoryDspDBAdapter categoryDspDBAdapter = new CategoryDspDBAdapter();
        categoryDspDBAdapter.open();
        categoryDspDBAdapter.deleteAllItems();
        for (int i = 0; i < categoryCodes.size(); i++) {
            categoryDspDBAdapter.saveItem(categoryCodes.get(i), i); // because categoryCodes is ordered by location
        }
        categoryDspDBAdapter.close();
        reloadCategoryLists(context);
    }

    public static void addNewCategory(Context context, TmpCategory tmpCategory) {
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        categoryDBAdapter.saveCategory(new KkbCategory(
                tmpCategory.code,
                "", // will not be saved
                tmpCategory.color,
                0, // will not be saved
                tmpCategory.drawable,
                tmpCategory.location,
                0, // will not be saved
                "", // will not be saved
                UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)));
        categoryDBAdapter.close();

        CategoryLanDBAdapter categoryLanDBAdapter = new CategoryLanDBAdapter();
        categoryLanDBAdapter.open();
        categoryLanDBAdapter.saveCategoryLan(tmpCategory);
        categoryLanDBAdapter.close();

        reloadCategoryLists(context);
    }
}
