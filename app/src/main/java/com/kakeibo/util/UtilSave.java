package com.kakeibo.util;

public class UtilSave {

    public static boolean isAmountOK(String str) {
        if ("".equals(str)) {
            return false;
        }
        if ("0".equals(str) || "0.0".equals(str) || "0.00".equals(str) || "0.000".equals(str)) {
            return false;
        }

        return true;
    }
}
