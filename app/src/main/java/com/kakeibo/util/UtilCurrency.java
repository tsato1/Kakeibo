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
    public static int sDefaultCurrencyIndex; /*** the position in sCurrencyIndex ***/

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
