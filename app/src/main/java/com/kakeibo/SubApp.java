package com.kakeibo;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.kakeibo.billing.BillingClientLifecycle;
import com.kakeibo.data.DataRepository;
import com.kakeibo.data.disk.AppDatabase;
import com.kakeibo.data.disk.LocalDataSource;
import com.kakeibo.data.network.WebDataSource;
import com.kakeibo.data.network.firebase.FakeServerFunctions;
import com.kakeibo.data.network.firebase.ServerFunctions;
import com.kakeibo.data.network.firebase.ServerFunctionsImpl;
import com.kakeibo.util.UtilDate;

import java.util.Currency;
import java.util.Locale;

/**
 * Android Application class. Used for accessing singletons.
 */
public class SubApp extends Application {

    private final AppExecutors executors = new AppExecutors();

    private static SharedPreferences _preferences;

    private static SubApp _instance;

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public LocalDataSource getLocalDataSource() {
        return LocalDataSource.getInstance(executors, getDatabase());
    }

    public ServerFunctions getServerFunctions() {
        if (Constants.USE_FAKE_SERVER) {
            return FakeServerFunctions.getInstance();
        } else {
            return ServerFunctionsImpl.getInstance();
        }
    }

    public WebDataSource getWebDataSource() {
        return WebDataSource.getInstance(executors, getServerFunctions());
    }

    public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository
                .getInstance(getLocalDataSource(), getWebDataSource(), getBillingClientLifecycle());
    }

    /***********************************/
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        PreferenceManager.setDefaultValues(_instance, R.xml.preferences, false);
        _preferences = PreferenceManager.getDefaultSharedPreferences(_instance);
    }

    private static SharedPreferences getSharedPreferences() {
        if (_preferences == null) {
            PreferenceManager.setDefaultValues(_instance, R.xml.preferences, false);
            _preferences = PreferenceManager.getDefaultSharedPreferences(_instance);
        }
        return _preferences;
    }

    /*** dateFormat ***/
    public static int getDateFormat(int key) {
        String strKey = _instance.getString(key);
        String dateFormatIndex = getSharedPreferences().getString(strKey, UtilDate.DATE_FORMAT_YMD);
        return Integer.parseInt(dateFormatIndex);
    }

    /*** fraction digits ***/
    public static int getFractionDigits(int key) {
        String strKey = _instance.getString(key);

        Locale locale = Locale.getDefault();
        int defValue = 0;
        try {
            Currency currency = Currency.getInstance(locale);
            defValue = currency.getDefaultFractionDigits();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        String digitsIndex = getSharedPreferences().getString(strKey, ""+defValue);
        String[] fractionDigits = _instance.getResources().getStringArray(R.array.pref_list_fraction_digits);
        return Integer.parseInt(fractionDigits[Integer.parseInt(digitsIndex)]);
    }

    /*** num category icons per row ***/
    public static int getNumColumns(int key) {
        String strKey = _instance.getString(key);
        String numColumnsIndex = getSharedPreferences().getString(strKey, "1");
        String[] numColumns = _instance.getResources().getStringArray(R.array.pref_list_num_columns);
        return Integer.parseInt(numColumns[Integer.parseInt(numColumnsIndex)]);
    }
}
