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

    private static List<Integer> sDspKkbCategoryCodeList = new ArrayList<>(); // ordered by location
    private static SparseArray<KkbCategory> sAllKkbCategoryArr = new SparseArray<>();
    private static List<KkbCategory> sAllKkbCategoryList = new ArrayList<>();//ordered by category code
    private static List<KkbCategory> sDspKkbCategoryList = new ArrayList<>();//ordered by location
    private static List<KkbCategory> sNonDspKkbCategoryList = new ArrayList<>();
    private static List<KkbCategory> sCustomKkbCategoryList = new ArrayList<>();

    public final static int NUM_MAX_DSP_CATEGORIES = 16;
    public final static int CUSTOM_CATEGORY_CODE_START = 1000;
    public final static int NUM_MAX_CUSTOM_CATEGORIES = 5;

    public static void reloadCategoryLists(Context context) {
        setAllKkbCategoryArr(context);
        setAllKkbCategoryList(context);
        setDspKkbCategoryList(context);
        setNonDspKkbCategoryList(context);
    }

    private static void setAllKkbCategoryList(Context context) {
        Log.d(TAG, "setAllKkbCategoryList() called");

        String langCode = UtilSystem.getCurrentLangCode(context);

        sAllKkbCategoryList.clear();
        sCustomKkbCategoryList.clear();
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        Cursor c = categoryDBAdapter.getAllKkbCategories(langCode);
        if (c!=null && c.moveToFirst()) {
            do {
                KkbCategory kkbCategory = new KkbCategory(
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_CODE)),
                        c.getString(1), //second arg is category name in specified language (column name is like 'jpn')
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_COLOR)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        c.getBlob(c.getColumnIndex(CategoryDBAdapter.COL_IMAGE)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sAllKkbCategoryList.add(kkbCategory);
                if (kkbCategory.getCode()>=CUSTOM_CATEGORY_CODE_START) sCustomKkbCategoryList.add(kkbCategory);
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
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_COLOR)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        c.getBlob(c.getColumnIndex(CategoryDBAdapter.COL_IMAGE)),
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
        sDspKkbCategoryCodeList.clear();
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
                        c.getBlob(c.getColumnIndex(CategoryDBAdapter.COL_IMAGE)),
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_LOCATION)),
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sDspKkbCategoryList.add(kkbCategory);
                sDspKkbCategoryCodeList.add(kkbCategory.getCode());
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
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_COLOR)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
                        c.getBlob(c.getColumnIndex(CategoryDBAdapter.COL_IMAGE)),
                        0,
                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
                        "",
                        "");
                sNonDspKkbCategoryList.add(kkbCategory);
            } while (c.moveToNext());
        }
        categoryDBAdapter.close();
    }

//    private static void setCustomKkbCategoryCodeList(Context context) {
//        Log.d(TAG, "setCustomKkbCategoryList() called");
//
//        String langCode = UtilSystem.getCurrentLangCode(context);
//        Log.d(TAG, "langCode="+langCode);
//
//        sCustomKkbCategoryList.clear();
//        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
//        categoryDBAdapter.open();
//        Cursor c = categoryDBAdapter.getCustomKkbCategories(langCode);
//        if (c!=null && c.moveToFirst()) {
//            do {
//                KkbCategory kkbCategory = new KkbCategory(
//                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_CODE)),
//                        "",
//                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_COLOR)),
//                        0,
//                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_DRAWABLE)),
//                        c.getBlob(c.getColumnIndex(CategoryDBAdapter.COL_IMAGE)),
//                        0,
//                        c.getInt(c.getColumnIndex(CategoryDBAdapter.COL_PARENT)),
//                        "",
//                        "");
//                sCustomKkbCategoryList.add(kkbCategory);
//            } while (c.moveToNext());
//        }
//        categoryDBAdapter.close();
//    }

    public static List<Integer> getDspCategoryCodeList(Context context) {
        if (sDspKkbCategoryCodeList != null && !sDspKkbCategoryCodeList.isEmpty()) return sDspKkbCategoryCodeList;

        setDspKkbCategoryList(context);

        return sDspKkbCategoryCodeList;
    }

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

    public static List<KkbCategory> getCustomKkbCategoryList(Context context) {
        if (sCustomKkbCategoryList != null && !sCustomKkbCategoryList.isEmpty()) return sCustomKkbCategoryList;

        setAllKkbCategoryList(context);

        return sCustomKkbCategoryList;
    }

    public static int getCategoryColor(Context context, int categoryCode) {
        if (sAllKkbCategoryArr == null || sAllKkbCategoryArr.size()==0) {
            setAllKkbCategoryArr(context);
        }

        return sAllKkbCategoryArr.get(categoryCode).getColor();
    }

    public static int getCategoryDrawable(Context context, int categoryCode) {
        if (sAllKkbCategoryArr == null || sAllKkbCategoryArr.size()==0) {
            setAllKkbCategoryArr(context);
        }

        return sAllKkbCategoryArr.get(categoryCode).getDrawable();
    }

    public static byte[] getCategoryImage(Context context, int categoryCode) {
        if (sAllKkbCategoryArr == null || sAllKkbCategoryArr.size() == 0) {
            setAllKkbCategoryArr(context);
        }

        return sAllKkbCategoryArr.get(categoryCode).getImage();
    }

    public static String getCategoryStr(Context context, int categoryCode) {
        if (sAllKkbCategoryArr == null || sAllKkbCategoryArr.size() == 0) {
            setAllKkbCategoryArr(context);
        }

        return sAllKkbCategoryArr.get(categoryCode).getName();
    }

    public static List<String> getCategoryLangStrs(int categoryCode) {
        CategoryLanDBAdapter categoryLanDBAdapter = new CategoryLanDBAdapter();
        categoryLanDBAdapter.open();

        List<String> out = new ArrayList<>();
        Cursor c = categoryLanDBAdapter.getCategoryStrsByCode(categoryCode);
        if (c!=null && c.moveToFirst()) {
            int i = 2; /*** 0: id, 1: cateogryCode(int) ***/
            do { /*** number of rows == 1 -> don't have to moveNext() ***/
                switch (i) {
                    case 2:
                        out.add("ARA,"+c.getString(i));
                        break;
                    case 3:
                        out.add("ENG,"+c.getString(i));
                        break;
                    case 4:
                        out.add("SPA,"+c.getString(i));
                        break;
                    case 5:
                        out.add("FRA,"+c.getString(i));
                        break;
                    case 6:
                        out.add("HIN,"+c.getString(i));
                        break;
                    case 7:
                        out.add("IND,"+c.getString(i));
                        break;
                    case 8:
                        out.add("ITA,"+c.getString(i));
                        break;
                    case 9:
                        out.add("JPN,"+c.getString(i));
                        break;
                    case 10:
                        out.add("KOR,"+c.getString(i));
                        break;
                    case 11:
                        out.add("POL,"+c.getString(i));
                        break;
                    case 12:
                        out.add("POR,"+c.getString(i));
                        break;
                    case 13:
                        out.add("RUS,"+c.getString(i));
                        break;
                    case 14:
                        out.add("TUR,"+c.getString(i));
                        break;
                    case 15:
                        out.add("VIE,"+c.getString(i));
                        break;
                    case 16:
                        out.add("HANS,"+c.getString(i));
                        break;
                    case 17:
                        out.add("HANT,"+c.getString(i));
                        break;

                }
                i++;
            } while (i <= 17);
        }
        /***
         * 2=ARA, 3=ENG, 4=SPA, 5=FRA, 6=HIN, 7=IND, 8=ITA, 9=JPN, 10=KOR,
         * 11=POL, 12=POR, 13=RUS, 14=TUR, 15=VIE, 16=HANS, 17=HANT
         */

        categoryLanDBAdapter.close();
        return out;
    }

    public static boolean deleteCustomKkbCategory(Context context, int categoryCode) {
        boolean out = false;
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        CategoryLanDBAdapter categoryLanDBAdapter = new CategoryLanDBAdapter();
        categoryDBAdapter.open();
        categoryLanDBAdapter.open();
        if (categoryDBAdapter.deleteItem(categoryCode) && categoryLanDBAdapter.deleteItem(categoryCode)) { out = true; }
        categoryDBAdapter.close();
        categoryLanDBAdapter.close();
        reloadCategoryLists(context);
        return out;
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

    public static long addNewCategory(Context context, TmpCategory tmpCategory) {
        CategoryDBAdapter categoryDBAdapter = new CategoryDBAdapter();
        categoryDBAdapter.open();
        Cursor c = categoryDBAdapter.calcCodeForNewCategory();

        /*** determining the category code to be assigned ***/
        int newCode = CUSTOM_CATEGORY_CODE_START;
        if (c != null && c.moveToFirst()) {
            newCode = c.getInt(0);
            Log.d("asdf", "asdf2    newCode = " + newCode);
        }
        Log.d("asdf", "asdf3    newCode = " + newCode);

        /*** allow only 10 custom categories ***/
        if (newCode >= CUSTOM_CATEGORY_CODE_START + NUM_MAX_CUSTOM_CATEGORIES) { return -2; }
        /*** catch null ***/
        if (tmpCategory == null) { return -3; }

        long row1 = categoryDBAdapter.saveCategory(new KkbCategory(
                newCode,
                "", // will not be saved
                tmpCategory.color,
                0, // will not be saved (default = 0)
                tmpCategory.drawable,
                tmpCategory.image,
                -1, // deprecated -> will not be saved (default = -1)
                -1, // will not be saved (default = -1)
                "", // will not be saved
                "")); // will be set in categoryDBAdapter.saveCategory function
        categoryDBAdapter.close();

        CategoryLanDBAdapter categoryLanDBAdapter = new CategoryLanDBAdapter();
        categoryLanDBAdapter.open();
        tmpCategory.code = newCode;
        long row2 = categoryLanDBAdapter.saveCategoryLan(tmpCategory);
        categoryLanDBAdapter.close();

        reloadCategoryLists(context);

        return row1==-1&&row2==-1? -1: 1; // 1==success, -1==failure
    }
}
