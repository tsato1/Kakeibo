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
