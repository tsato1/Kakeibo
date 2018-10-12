package com.kakeibo.util;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;

public class UtilCurrency {
    private final static String TAG = UtilCurrency.class.getSimpleName();
    public final static String CURRENCY_NONE = "-";

    public static int sAllCurrencyLength;
    public static ArrayList<String> sCurrencyCodes = new ArrayList<>();
    public static ArrayList<String> sCurrencyIndex = new ArrayList<>();
    public static int sDefaultCurrencyIndex; // where in sCurrencyIndex

    public static BigDecimal getBDAmount(int input /*** int from db ***/, int digit) {
        return BigDecimal.valueOf(input, digit);
    }

    public static int getIntAmount(BigDecimal input, int digit) {
        if (digit == 0) {
            return input.intValue();
        } else if (digit == 1) {
            return input.multiply(BigDecimal.valueOf(10)).intValue();
        } else if (digit == 2) {
            return input.multiply(BigDecimal.valueOf(100)).intValue();
        } else if (digit == 3) {
            return input.multiply(BigDecimal.valueOf(1000)).intValue();
        }

        return 0;
    }

    int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    public static void setUpCurrency() {
        Log.d(TAG, "setUpCurrency() called");
        Locale[] locs = Locale.getAvailableLocales();
        HashSet<String> currencyCodes = new HashSet<>();

        for(Locale loc : locs) {
            try {
                Currency currency = Currency.getInstance( loc );

                if ( currency != null ) {
                    currencyCodes.add( currency.getCurrencyCode() );
                }
            } catch(Exception exc) {
                Log.e(TAG, "Locale not found");
            }
        }

        Locale locale = Locale.getDefault();
        Currency currency = Currency.getInstance(locale);
        String currencyCode = currency.getCurrencyCode();

        sCurrencyCodes = new ArrayList<>(currencyCodes);
        Collections.sort(sCurrencyCodes);
        sCurrencyCodes.add(CURRENCY_NONE);
        sAllCurrencyLength = sCurrencyCodes.size();
        for (int i = 0; i < sCurrencyCodes.size(); ++i) {
            sCurrencyIndex.add(String.valueOf(i));
            if (sCurrencyCodes.get(i).equals(currencyCode)) sDefaultCurrencyIndex = i;
        }
    }
}
