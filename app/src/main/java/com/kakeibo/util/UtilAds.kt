package com.kakeibo.util

import com.kakeibo.db.KkbAppDBAdapter

object UtilAds {
    val isBannerAdsDisplayAgreed: Boolean
        get() {
//            kkbAppDBAdapter.open()
//            val c = kkbAppDBAdapter.valueInt2
//            var valueInt_2 = KkbAppDBAdapter.COL_VAL_INT_2_DEFAULT
//            if (c != null && c.moveToFirst()) {
//                valueInt_2 = c.getInt(0)
//            }
//            kkbAppDBAdapter.close()
//            return valueInt_2 == KkbAppDBAdapter.COL_VAL_INT_2_SHOWADS
            return true;
        }
}