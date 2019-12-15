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
        String langScrt = locale.getScript();

        if (!"".equals(langCode) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_AR).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_EN).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_ES).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_FR).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_HI).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_JA).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_KO).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_IND).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_IT).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_PL).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_PT).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_RU).getLanguage()) &&
                !langCode.equals(new Locale(CategoriesLanDBAdapter.COL_TR).getLanguage())) {
            langCode = CategoriesLanDBAdapter.COL_EN;
        }
        if (!"".equals(langScrt) &&
                !langScrt.equals(new Locale(CategoriesLanDBAdapter.COL_ZH_Hans).getScript())) {
            langCode = CategoriesLanDBAdapter.COL_ZH_Hans;
        } else if (!"".equals(langScrt) &&
                !langScrt.equals(new Locale(CategoriesLanDBAdapter.COL_ZH_Hant).getScript())) {
            langCode = CategoriesLanDBAdapter.COL_ZH_Hant;
        }
        Log.d(TAG, "locale.getLanguage() = " + langCode + "locale.getScript() = " + langScrt);

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
