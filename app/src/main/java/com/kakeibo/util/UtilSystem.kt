package com.kakeibo.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.kakeibo.db.CategoryLanDBAdapter
import java.util.*

object UtilSystem {
    private val TAG = UtilSystem::class.java.simpleName
    private val langStrs = arrayOf(
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
            CategoryLanDBAdapter.COL_VIE)

    private var mPrevLang = "eng"

    val allSupportedLanguages = Arrays.asList(*langStrs)

    private fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    fun getCurrentLangCode(context: Context): String {
        Log.d(TAG, "getCurrentLangCode() called")
        val locale = getCurrentLocale(context)
        var langCode = locale.isO3Language
        val langScrt = locale.script
        langCode = getCurrentLangCode(langCode)
        /*** for Chinese  */
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
                ", locale.getISO3Language() = " + locale.isO3Language)
        mPrevLang = langCode
        return langCode
    }

    fun getCurrentLangCode(langCode: String): String {
        var langCode = langCode
        if ("" != langCode &&
                // !langCode.equals(new Locale(CategoryLanDBAdapter.COL_ARA).getISO3Language()) &&
                langCode != Locale(CategoryLanDBAdapter.COL_ENG).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_SPA).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_FRA).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_HIN).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_IND).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_JPN).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_KOR).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_ITA).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_POL).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_POR).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_RUS).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_TUR).isO3Language &&
                langCode != Locale(CategoryLanDBAdapter.COL_VIE).isO3Language) {
            langCode = CategoryLanDBAdapter.COL_ENG
        }
        return langCode
    }

    fun isLangChanged(context: Context): Boolean {
        return mPrevLang != getCurrentLangCode(context)
    }
}