package com.kakeibo.util;

import com.kakeibo.R;
import com.kakeibo.SubApp;

import java.math.BigDecimal;

public class UtilCurrency {
    private final static String TAG = UtilCurrency.class.getSimpleName();
    public final static String CURRENCY_NONE = "---";
    public final static String CURRENCY_OLD = "===";

    public static long getLongFromBigDecimal(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(1000)).longValue();
    }

    public static long getLongAmountFromBigDecimal(BigDecimal amount, int currencyFractionDigits) {
        if (currencyFractionDigits == 0) {
            return amount.intValue();
        } else if (currencyFractionDigits == 1) {
            return amount.multiply(BigDecimal.valueOf(10)).longValue();
        } else if (currencyFractionDigits == 2) {
            return amount.multiply(BigDecimal.valueOf(100)).longValue();
        } else if (currencyFractionDigits == 3) {
            return amount.multiply(BigDecimal.valueOf(1000)).longValue();
        }

        return -1;
    }

    public static BigDecimal getBigDecimalFromLong(long amount) {
        return BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(1000),
                        SubApp.getFractionDigits(R.string.pref_key_fraction_digits),
                        BigDecimal.ROUND_HALF_UP);
    }

    public static boolean checkAmount(String str) {
        return str.matches("\\d+(\\.\\d+)?");
    }
}
