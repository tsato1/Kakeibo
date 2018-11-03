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
    public final static String CURRENCY_NONE = "-";

    public static String setUpCurrency(Activity activity) {
        Log.d(TAG, "setUpCurrency() called");

        Locale[] locs = Locale.getAvailableLocales();
        HashSet<String> setCurrencyCode = new HashSet<>();

        for(Locale loc : locs) {
            try {
                Currency currency = Currency.getInstance( loc );

                if ( currency != null ) {
                    setCurrencyCode.add( currency.getCurrencyCode() );
                    Log.d(TAG, "loc: " + loc.toString() + ", " + currency.getCurrencyCode());
                }
            } catch(Exception exc) {
                Log.d(TAG, "Locale not found");
            }
        }

        Locale locale = Locale.getDefault();
        Currency currency = Currency.getInstance(locale);
        String currencyCode = currency.getCurrencyCode();

        ArrayList<String> arlCurrencyCodes = new ArrayList<>(setCurrencyCode);
        Collections.sort(arlCurrencyCodes);
        arlCurrencyCodes.add(CURRENCY_NONE);

        int currencyIndex = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arlCurrencyCodes.size(); ++i) {
            if (arlCurrencyCodes.get(i).equals(currencyCode)) currencyIndex = i;
            sb.append(arlCurrencyCodes.get(i));
            sb.append("\t");
        }

        /*** save currencyCode and currencyIndex to sharedPreference ***/
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SettingsActivity.PREF_KEY_FRACTION_DIGITS, currencyCode);
        editor.putInt(SettingsActivity.PREF_KEY_CURRENCY_INDEX, currencyIndex);
        editor.apply();

        /*** save array of currencyCode to file ***/
        UtilFiles.writeToFile(MainActivity.FILE_CURRENCY, sb.toString(), activity, Context.MODE_PRIVATE);

        return currencyCode;
    }

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
