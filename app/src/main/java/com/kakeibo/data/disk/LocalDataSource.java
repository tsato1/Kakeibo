package com.kakeibo.data.disk;


import android.util.Log;

import androidx.lifecycle.LiveData;

import com.kakeibo.AppExecutors;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.data.SubscriptionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class LocalDataSource {
    private static volatile LocalDataSource INSTANCE = null;

    private final Executor executor;
    private final AppDatabase appDatabase;

    /**
     * Get the list of subscriptions from the localDataSource and get notified when the data changes.
     */
    public LiveData<List<SubscriptionStatus>> subscriptions;
    public LiveData<List<ItemStatus>> items;

    private LocalDataSource(Executor executor, AppDatabase appDatabase) {
        this.executor = executor;
        this.appDatabase = appDatabase;

        subscriptions = appDatabase.subscriptionStatusDao().getAll();
        items = appDatabase.itemStatusDao().getAll();
    }

    public static LocalDataSource getInstance(AppExecutors executors, AppDatabase database) {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSource(executors.diskIO, database);
                }
            }
        }
        return INSTANCE;
    }

    /***
     *
     *  Subscriptions
     *
     *  ***/
    public void updateSubscriptions(final List<SubscriptionStatus> subscriptions) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        // Delete existing subscriptions.
                        appDatabase.subscriptionStatusDao().deleteAll();
                        // Put new subscriptions data into localDataSource.
                        appDatabase.subscriptionStatusDao().insertAll(subscriptions);
                    }
                });
            }
        });
    }

    /** Delete local user data when the user signs out */
    public void deleteLocalUserData() {
        updateSubscriptions(new ArrayList<SubscriptionStatus>());
    }

    /***
     *
     *  ItemStatus
     *
     *  ***/
    public void insertOrUpdateItemStatus(ItemStatus itemStatus) {
        appDatabase.itemStatusDao().insert(itemStatus);
    }

    public LiveData<List<ItemStatus>> getAllItemStatus() {
        return appDatabase.itemStatusDao().getAll();
    }

//    public void updateItemStatuses(final List<ItemStatus> itemStatuses) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Delete existing subscriptions.
//                        appDatabase.itemStatusDao().deleteAll();
//                        // Put new subscriptions data into localDataSource.
//                        appDatabase.itemStatusDao().insertAll(itemStatuses);
//                    }
//                });
//            }
//        });
//    }

    public void deleteAllItemStatus() {
        appDatabase.itemStatusDao().deleteAll();
//        updateItemStatuses(new ArrayList<ItemStatus>());
    }
}
