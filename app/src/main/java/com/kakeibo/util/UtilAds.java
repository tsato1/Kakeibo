//package com.kakeibo.util;
//
//import android.database.Cursor;
//
//import com.kakeibo.db.KkbAppDBAdapter;
//
//public class UtilAds {
//
//    public static boolean isBannerAdsDisplayAgreed() {
//        KkbAppDBAdapter kkbAppDBAdapter = new KkbAppDBAdapter();
//        kkbAppDBAdapter.open();
//
//        Cursor c = kkbAppDBAdapter.getValueInt2();
//
//        int valueInt_2 = KkbAppDBAdapter.COL_VAL_INT_2_DEFAULT;
//        if (c!=null && c.moveToFirst()) {
//            valueInt_2 = c.getInt(0);
//        }
//
//        kkbAppDBAdapter.close();
//        return valueInt_2==KkbAppDBAdapter.COL_VAL_INT_2_SHOWADS;
//    }
//}
