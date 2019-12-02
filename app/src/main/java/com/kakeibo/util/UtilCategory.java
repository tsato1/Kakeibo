package com.kakeibo.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kakeibo.db.CategoriesLanDBAdapter;

import java.util.Locale;

public class UtilCategory {
    private static String TAG = UtilCategory.class.getSimpleName();

    public static int numCategories = 16;

    public static String getCategoryStrFromCode(Context context, int catCode) {
        String out = "";

        Locale locale = UtilSystem.getCurrentLocale(context);
        String langCode = locale.getLanguage();
        Log.d(TAG, "locale.getLanguage() = " + langCode);

        if (!langCode.equals(new Locale(CategoriesLanDBAdapter.COL_AR).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_EN).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_ES).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_FR).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_HI).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_JA).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_KO).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_IN).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_IT).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_PT).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_RU).getLanguage())) {
            langCode = CategoriesLanDBAdapter.COL_EN;
        }

        CategoriesLanDBAdapter categoriesLanDBAdapter = new CategoriesLanDBAdapter();
        categoriesLanDBAdapter.open();

        Cursor c = categoriesLanDBAdapter.getCategoryStrByCode(catCode, langCode);
        if (c!=null && c.moveToFirst()) {
            out = c.getString(0);
        }

        categoriesLanDBAdapter.close();

        return out;
    }
}
