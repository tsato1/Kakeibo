//package com.kakeibo.data.disk;
//
//import androidx.lifecycle.LiveData;
//import androidx.sqlite.db.SupportSQLiteQuery;
//
//import com.kakeibo.AppExecutors;
//import com.kakeibo.data.CategoryDspStatus;
//import com.kakeibo.data.CategoryStatus;
//import com.kakeibo.data.ItemStatus;
//import com.kakeibo.data.KkbAppStatus;
//import com.kakeibo.data.SubscriptionStatus;
//import com.kakeibo.util.UtilSystem;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.Executor;
//
//public class LocalDataSource {
//    private static volatile LocalDataSource INSTANCE = null;
//
//    private final Executor executor;
//    private final AppDatabase appDatabase;
//
//    /**
//     * Get the list of subscriptions from the localDataSource and get notified when the data changes.
//     */
//    public LiveData<KkbAppStatus> kkbAppStatus;
//    public LiveData<List<SubscriptionStatus>> subscriptions;
//    public LiveData<List<ItemStatus>> items;
//    public LiveData<List<CategoryDspStatus>> categoryDspStatuses;
//
////    public LiveData<List<CategoryStatus>> categories;
//    public LiveData<List<Integer>> categoryCodes;     // ordered by location
//
//    public LiveData<List<CategoryStatus>> categoryStatusesForDsp; // orderd by location
////    public LiveData<List<Integer>> dspCategoryCodes;     // ordered by location
////    public LiveData<List<CategoryStatus>> nonDspCategories;
////    public LiveData<List<CategoryStatus>> customCategories;
//
//
////    public LiveData<List<KkbCategory>> sDspKkbCategoryList; // ordered by location
////    public LiveData<List<Integer>> sDspKkbCategoryCodeList; // ordered by location
////    public LiveData<List<Integer>> sAllKkbCategoryCodeList; // ordered by code
////    public LiveData<List<KkbCategory>> sNonDspKkbCategoryList;
////    public LiveData<List<KkbCategory>> sCustomKkbCategoryList;
//
////    public LiveData<SparseArray<KkbCategory>> sAllKkbCategoryArr;
//
//    private LocalDataSource(Executor executor, AppDatabase appDatabase) {
//        this.executor = executor;
//        this.appDatabase = appDatabase;
//
//        kkbAppStatus = appDatabase.kkbAppStatusDao().getFirst();
//        subscriptions = appDatabase.subscriptionStatusDao().getAll();
//        items = appDatabase.itemStatusDao().getAll();
//
//        String langCode = UtilSystem.getCurrentLangCode(Locale.getDefault().getISO3Country());
////        categories = appDatabase.categoryStatusDao().getAllStatusesLiveData(langCode);
//        categoryCodes = appDatabase.categoryStatusDao().getCategoryCodes();
//        categoryDspStatuses = appDatabase.categoryDspStatusDao().getAll();
//        categoryStatusesForDsp = appDatabase.categoryDspStatusDao().getCategoryStatusesForDsp();
////        dspCategoryCodes = appDatabase.categoryDspStatusDao().getDspCodes();
////        nonDspCategories = appDatabase.categoryDspStatusDao().getNonDspStatusesLiveData(langCode);
////        customCategories = appDatabase.categoryStatusDao().getCustomStatusesLiveData(langCode);
//    }
//
//    public static LocalDataSource getInstance(AppExecutors executors, AppDatabase database) {
//        if (INSTANCE == null) {
//            synchronized (LocalDataSource.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = new LocalDataSource(executors.diskIO, database);
//                }
//            }
//        }
//        return INSTANCE;
//    }
//
//    /***
//     *
//     *  Subscriptions
//     *
//     *  ***/
//    public void updateSubscriptions(final List<SubscriptionStatus> subscriptions) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Delete existing subscriptions.
//                        appDatabase.subscriptionStatusDao().deleteAll();
//                        // Put new subscriptions data into localDataSource.
//                        appDatabase.subscriptionStatusDao().insertAll(subscriptions);
//                    }
//                });
//            }
//        });
//    }
//
//    /** Delete local user data when the user signs out */
//    public void deleteLocalUserData() {
//        updateSubscriptions(new ArrayList<SubscriptionStatus>());
//    }
//
//
//    /***
//     *
//     *  kkbAppStatus
//     *
//     *  ***/
//    //no update/insert/delete function provided in kkbStatusDao()
//
//    /***
//     *
//     *  ItemStatus
//     *
//     *  ***/
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
//
//    public void insertItemStatus(final ItemStatus itemStatus) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        appDatabase.itemStatusDao().insert(itemStatus);
//                    }
//                });
//            }
//        });
//    }
//
//    public void deleteAllItemStatus() {
//        appDatabase.itemStatusDao().deleteAll();
//        updateItemStatuses(new ArrayList<ItemStatus>());
//    }
//
//    public List<ItemStatus> queryItems(SupportSQLiteQuery query) {
////        return appDatabase.itemStatusDao().queryItems(query);
////        executor.execute(new Runnable() {
////            @Override
////            public void run() {
////                appDatabase.runInTransaction(new Runnable() {
////                    @Override
////                    public void run() {
////                        appDatabase.itemStatusDao().queryItems(query);
////                    }
////                });
////            }
////        });
//        return null;
//    }
//
//    /***
//     *
//     *  CategoryStatus
//     *
//     *  ***/
//    public void updateCategoryStatuses(final List<CategoryStatus> categoryStatuses) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Delete existing subscriptions.
//                        appDatabase.categoryStatusDao().deleteAll();
//                        // Put new subscriptions data into localDataSource.
//                        appDatabase.categoryStatusDao().insertAll(categoryStatuses);
//                    }
//                });
//            }
//        });
//    }
//
//    public void insertCategoryStatus(final CategoryStatus categoryStatus) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        appDatabase.categoryStatusDao().insert(categoryStatus);
//                    }
//                });
//            }
//        });
//    }
//
//    public void deleteAllCategoryStatus() {
//        appDatabase.categoryStatusDao().deleteAll();
//        updateCategoryStatuses(new ArrayList<CategoryStatus>());
//    }
//
//    /***
//     *
//     *  CategoryLanStatus
//     *
//     *  ***/
////    public void updateCategoryLanStatuses(final List<CategoryLanStatus> categoryLanStatuses) {
////        executor.execute(new Runnable() {
////            @Override
////            public void run() {
////                appDatabase.runInTransaction(new Runnable() {
////                    @Override
////                    public void run() {
////                        // Delete existing subscriptions.
////                        appDatabase.categoryLanStatusDao().deleteAll();
////                        // Put new subscriptions data into localDataSource.
////                        appDatabase.categoryLanStatusDao().insertAll(categoryLanStatuses);
////                    }
////                });
////            }
////        });
////    }
////
////    public void insertCategoryLanStatus(final CategoryLanStatus categoryLanStatus) {
////        executor.execute(new Runnable() {
////            @Override
////            public void run() {
////                appDatabase.runInTransaction(new Runnable() {
////                    @Override
////                    public void run() {
////                        appDatabase.categoryLanStatusDao().insert(categoryLanStatus);
////                    }
////                });
////            }
////        });
////    }
////
////    public void deleteAllCategoryLanStatus() {
////        appDatabase.categoryLanStatusDao().deleteAll();
////        updateCategoryLanStatuses(new ArrayList<CategoryLanStatus>());
////    }
//
//    /***
//     *
//     *  DspCategoryStatus and CategoryDspStatus table
//     *
//     *  ***/
//    public void updateDspTable(final List<Integer> categoryCodes) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        appDatabase.categoryStatusDao().deleteAll();
//
//                        List<CategoryDspStatus> categoryDspStatuses = new ArrayList<>();
//                        for (int i = 0; i < categoryCodes.size(); i++) {
//                            CategoryDspStatus categoryDspStatus = new CategoryDspStatus(
//                                    categoryCodes.get(i),
//                                    i
//                            );
//                            categoryDspStatuses.add(categoryDspStatus);
//                        }
//                        appDatabase.categoryDspStatusDao().insertAll(categoryDspStatuses);
//                    }
//                });
//            }
//        });
//    }
//
//    public void deleteAllCategoryDspStatus() {
//        appDatabase.categoryDspStatusDao().deleteAll();
//        updateDspTable(new ArrayList<>());
//    }
//}
