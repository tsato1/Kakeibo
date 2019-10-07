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

    public static long getLongAmountFromBigDecimal(BigDecimal amount, int currencyFractionDigits) {
        if (currencyFractionDigits == 0) {
            return amount.intValue();
        } else if (currencyFractionDigits == 1) {
            return amount.multiply(BigDecimal.valueOf(10)).longValue();
        } else if (currencyFractionDigits == 2) {
            return amount.multiply(BigDecimal.valueOf(100)).longValue();
        } else if (currencyFractionDigits == 3) {
            Log.d("UtilCurrency", "asdf="+amount.multiply(BigDecimal.valueOf(1000)).longValue());
            return amount.multiply(BigDecimal.valueOf(1000)).longValue();
        }

        return -1;
    }

    public static BigDecimal getBigDecimalAmountFromInt(int amount, int currencyFractionDigits) {
        return BigDecimal.valueOf(amount, currencyFractionDigits);
    }
}
