package com.kakeibo.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.kakeibo.db.CategoryLanDBAdapter;

import java.util.Locale;

public class UtilSystem {
    private final static String TAG = UtilSystem.class.getSimpleName();
    private static String mPrevLang = "eng";

    private static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    static String getCurrentLangCode(Context context) {
        Log.d(TAG, "getCurrentLangCode() called");

        Locale locale = getCurrentLocale(context);
        String langCode = locale.getISO3Language();
        String langScrt = locale.getScript();

        if (!"".equals(langCode) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_ARA).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_ENG).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_SPA).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_FRA).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_HIN).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_IND).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_JPN).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_KOR).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_ITA).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_POL).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_POR).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_RUS).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_TUR).getISO3Language())) {
            langCode = CategoryLanDBAdapter.COL_ENG;
        }
        /*** for Chinese ***/
        if (!"".equals(langScrt) &&
                !langScrt.equals(new Locale(CategoryLanDBAdapter.COL_Hans).getScript())) {
            langCode = CategoryLanDBAdapter.COL_Hans;
        } else if (!"".equals(langScrt) &&
                !langScrt.equals(new Locale(CategoryLanDBAdapter.COL_Hant).getScript())) {
            langCode = CategoryLanDBAdapter.COL_Hant;
        }
        Log.d(TAG, "prevCode = " + mPrevLang +
                " currCode = " + langCode +
                ", locale.getScript() = " + langScrt +
                ", locale.getISO3Language() = " + locale.getISO3Language());

        mPrevLang = langCode;
        return langCode;
    }

    public static boolean isLangChanged(Context context) {
        return !mPrevLang.equals(getCurrentLangCode(context));
    }
}
