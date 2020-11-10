package com.kakeibo;

import android.app.Application;

import com.kakeibo.billing.BillingClientLifecycle;
import com.kakeibo.data.DataRepository;
import com.kakeibo.data.disk.AppDatabase;
import com.kakeibo.data.disk.LocalDataSource;
import com.kakeibo.data.network.WebDataSource;
import com.kakeibo.data.network.firebase.FakeServerFunctions;
import com.kakeibo.data.network.firebase.ServerFunctions;
import com.kakeibo.data.network.firebase.ServerFunctionsImpl;

/**
 * Android Application class. Used for accessing singletons.
 */
public class SubApp extends Application {
    private final AppExecutors executors = new AppExecutors();

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
}
