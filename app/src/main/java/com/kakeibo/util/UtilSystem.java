package com.kakeibo.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.kakeibo.db.CategoryLanDBAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UtilSystem {
    private final static String TAG = UtilSystem.class.getSimpleName();
    private final static String[] langStrs = {
            CategoryLanDBAdapter.COL_ARA,
            CategoryLanDBAdapter.COL_ENG,
            CategoryLanDBAdapter.COL_SPA,
            CategoryLanDBAdapter.COL_FRA,
            CategoryLanDBAdapter.COL_HIN,
            CategoryLanDBAdapter.COL_IND,
            CategoryLanDBAdapter.COL_ITA,
            CategoryLanDBAdapter.COL_JPN,
            CategoryLanDBAdapter.COL_KOR,
            CategoryLanDBAdapter.COL_POL,
            CategoryLanDBAdapter.COL_POR,
            CategoryLanDBAdapter.COL_RUS,
            CategoryLanDBAdapter.COL_TUR,
            CategoryLanDBAdapter.COL_VIE,
//            CategoryLanDBAdapter.COL_Hans,
//            CategoryLanDBAdapter.COL_Hant
    };
    private static String mPrevLang = "eng";
    private final static List<String> _langList = Arrays.asList(langStrs);

    private static Locale getCurrentLocale(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return context.getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static String getCurrentLangCode(Context context) {
        Log.d(TAG, "getCurrentLangCode() called");

        Locale locale = getCurrentLocale(context);
        String langCode = locale.getISO3Language();
        String langScrt = locale.getScript();

        langCode = getCurrentLangCode(langCode);

        /*** for Chinese ***/
//        if (!"".equals(langScrt) &&
//                !langScrt.equals(new Locale(CategoryLanDBAdapter.COL_Hans).getScript())) {
//            langCode = CategoryLanDBAdapter.COL_Hans;
//        } else if (!"".equals(langScrt) &&
//                !langScrt.equals(new Locale(CategoryLanDBAdapter.COL_Hant).getScript())) {
//            langCode = CategoryLanDBAdapter.COL_Hant;
//        }

        Log.d(TAG, "prevCode = " + mPrevLang +
                " currCode = " + langCode +
                ", locale.getScript() = " + langScrt +
                ", locale.getISO3Language() = " + locale.getISO3Language());

        mPrevLang = langCode;
        return langCode;
    }

    public static String getCurrentLangCode(String langCode) {
        if (!"".equals(langCode) &&
//                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_ARA).getISO3Language()) &&
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
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_TUR).getISO3Language()) &&
                !langCode.equals(new Locale(CategoryLanDBAdapter.COL_VIE).getISO3Language())
        ) {
            langCode = CategoryLanDBAdapter.COL_ENG;
        }

        return langCode;
    }

    public static boolean isLangChanged(Context context) {
        return !mPrevLang.equals(getCurrentLangCode(context));
    }

    public static List<String> getAllSupportedLanguages() {
        return _langList;
    }
}
