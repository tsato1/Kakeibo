package com.kakeibo.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.kakeibo.core.data.constants.ConstCategoryLanDB
import java.util.*

object UtilSystem {
    private val TAG = UtilSystem::class.java.simpleName
    private val langStrs = arrayOf(
            ConstCategoryLanDB.COL_ARA,
            ConstCategoryLanDB.COL_ENG,
            ConstCategoryLanDB.COL_SPA,
            ConstCategoryLanDB.COL_FRA,
            ConstCategoryLanDB.COL_HIN,
            ConstCategoryLanDB.COL_IND,
            ConstCategoryLanDB.COL_ITA,
            ConstCategoryLanDB.COL_JPN,
            ConstCategoryLanDB.COL_KOR,
            ConstCategoryLanDB.COL_POL,
            ConstCategoryLanDB.COL_POR,
            ConstCategoryLanDB.COL_RUS,
            ConstCategoryLanDB.COL_TUR,
            ConstCategoryLanDB.COL_VIE)

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
                langCode != Locale(ConstCategoryLanDB.COL_ENG).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_SPA).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_FRA).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_HIN).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_IND).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_JPN).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_KOR).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_ITA).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_POL).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_POR).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_RUS).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_TUR).isO3Language &&
                langCode != Locale(ConstCategoryLanDB.COL_VIE).isO3Language) {
            langCode = ConstCategoryLanDB.COL_ENG
        }
        return langCode
    }

    fun isLangChanged(context: Context): Boolean {
        return mPrevLang != getCurrentLangCode(context)
    }
}