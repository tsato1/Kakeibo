package com.kakeibo.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kakeibo.MainActivity;
import com.kakeibo.settings.SettingsActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;

public class UtilCurrency {
    private final static String TAG = UtilCurrency.class.getSimpleName();
    public final static String CURRENCY_NONE = "---";
    public final static String CURRENCY_OLD = "===";

    public static int getIntAmountFromBigDecimal(BigDecimal amount, int currencyFractionDigits) {
        if (currencyFractionDigits == 0) {
            return amount.intValue();
        } else if (currencyFractionDigits == 1) {
            return amount.multiply(BigDecimal.valueOf(10)).intValue();
        } else if (currencyFractionDigits == 2) {
            return amount.multiply(BigDecimal.valueOf(100)).intValue();
        } else if (currencyFractionDigits == 3) {
            return amount.multiply(BigDecimal.valueOf(1000)).intValue();
        }

        return -1;
    }

    public static BigDecimal getBigDecimalAmountFromInt(int amount, int currencyFractionDigits) {
        return BigDecimal.valueOf(amount, currencyFractionDigits);
    }
}
