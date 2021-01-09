//package com.kakeibo.data.disk
//
//import androidx.lifecycle.LiveData
//import androidx.sqlite.db.SimpleSQLiteQuery
//import androidx.sqlite.db.SupportSQLiteQuery
//import com.kakeibo.AppExecutors
//import com.kakeibo.data.CategoryDspStatus
//import com.kakeibo.data.CategoryStatus
//import com.kakeibo.data.ItemStatus
//import com.kakeibo.data.SubscriptionStatus
//import com.kakeibo.util.UtilSystem
//import kotlinx.coroutines.flow.first
//import java.util.*
//import java.util.concurrent.Executor
//
//class LocalDataSource private constructor(
//        private val executor: Executor,
//        private val appDatabase: AppDatabase)
//{
//    /**
//     * Get the list of subscriptions from the localDataSource and get notified when the data changes.
//     */
//    @JvmField val subscriptions = appDatabase.subscriptionStatusDao().all
//    @JvmField val items = appDatabase.itemStatusDao().getAll()
//    @JvmField val categoryDspStatuses = appDatabase.categoryDspStatusDao().all
//    //    public LiveData<List<CategoryStatus>> categories;
//    @JvmField val categoryCodes = appDatabase.categoryStatusDao().categoryCodes
//    @JvmField val categoryStatusesForDsp = appDatabase.categoryDspStatusDao().categoryStatusesForDsp
//
//    /***
//     *
//     * Subscriptions
//     *
//     */
//    fun updateSubscriptions(subscriptions: List<SubscriptionStatus?>?) {
//        executor.execute {
//            appDatabase.runInTransaction {
//                // Delete existing subscriptions.
//                appDatabase.subscriptionStatusDao().deleteAll()
//                // Put new subscriptions data into localDataSource.
//                appDatabase.subscriptionStatusDao().insertAll(subscriptions)
//            }
//        }
//    }
//
//    /** Delete local user data when the user signs out  */
//    fun deleteLocalUserData() {
//        updateSubscriptions(ArrayList())
//    }
//
//    /***
//     *
//     * ItemStatus
//     *
//     */
//    fun updateItemStatuses(itemStatuses: List<ItemStatus?>?) {
//        executor.execute {
//            appDatabase.runInTransaction {
//                appDatabase.itemStatusDao().deleteAll()
//                appDatabase.itemStatusDao().insertAll(itemStatuses)
//            }
//        }
//    }
//
//    fun insertItemStatus(itemStatus: ItemStatus?) {
//        executor.execute { appDatabase.runInTransaction { appDatabase.itemStatusDao().insert(itemStatus) } }
//    }
//
//    fun deleteAllItemStatus() {
//        appDatabase.itemStatusDao().deleteAll()
//        updateItemStatuses(ArrayList())
//    }
//
//    suspend fun queryItems(query: SimpleSQLiteQuery?): List<ItemStatus> {
//        return appDatabase.itemStatusDao().queryItems(query).first()
//    }
//
//    /***
//     *
//     * CategoryStatus
//     *
//     */
//    fun updateCategoryStatuses(categoryStatuses: List<CategoryStatus?>?) {
//        executor.execute {
//            appDatabase.runInTransaction {
//                appDatabase.categoryStatusDao().deleteAll()
//                appDatabase.categoryStatusDao().insertAll(categoryStatuses)
//            }
//        }
//    }
//
//    fun insertCategoryStatus(categoryStatus: CategoryStatus?) {
//        executor.execute { appDatabase.runInTransaction { appDatabase.categoryStatusDao().insert(categoryStatus) } }
//    }
//
//    fun deleteAllCategoryStatus() {
//        appDatabase.categoryStatusDao().deleteAll()
//        updateCategoryStatuses(ArrayList())
//    }
//    /***
//     *
//     * CategoryLanStatus
//     *
//     */
//    //    public void updateCategoryLanStatuses(final List<CategoryLanStatus> categoryLanStatuses) {
//    //        executor.execute(new Runnable() {
//    //            @Override
//    //            public void run() {
//    //                appDatabase.runInTransaction(new Runnable() {
//    //                    @Override
//    //                    public void run() {
//    //                        // Delete existing subscriptions.
//    //                        appDatabase.categoryLanStatusDao().deleteAll();
//    //                        // Put new subscriptions data into localDataSource.
//    //                        appDatabase.categoryLanStatusDao().insertAll(categoryLanStatuses);
//    //                    }
//    //                });
//    //            }
//    //        });
//    //    }
//    //
//    //    public void insertCategoryLanStatus(final CategoryLanStatus categoryLanStatus) {
//    //        executor.execute(new Runnable() {
//    //            @Override
//    //            public void run() {
//    //                appDatabase.runInTransaction(new Runnable() {
//    //                    @Override
//    //                    public void run() {
//    //                        appDatabase.categoryLanStatusDao().insert(categoryLanStatus);
//    //                    }
//    //                });
//    //            }
//    //        });
//    //    }
//    //
//    //    public void deleteAllCategoryLanStatus() {
//    //        appDatabase.categoryLanStatusDao().deleteAll();
//    //        updateCategoryLanStatuses(new ArrayList<CategoryLanStatus>());
//    //    }
//    /***
//     *
//     * DspCategoryStatus and CategoryDspStatus table
//     *
//     */
//    fun updateDspTable(categoryCodes: List<Int?>) {
//        executor.execute {
//            appDatabase.runInTransaction {
//                appDatabase.categoryStatusDao().deleteAll()
//                val categoryDspStatuses: MutableList<CategoryDspStatus> = ArrayList()
//                for (i in categoryCodes.indices) {
//                    val categoryDspStatus = CategoryDspStatus(
//                            categoryCodes[i]!!,
//                            i
//                    )
//                    categoryDspStatuses.add(categoryDspStatus)
//                }
//                appDatabase.categoryDspStatusDao().insertAll(categoryDspStatuses)
//            }
//        }
//    }
//
//    fun deleteAllCategoryDspStatus() {
//        appDatabase.categoryDspStatusDao().deleteAll()
//        updateDspTable(ArrayList())
//    }
//
//    companion object {
//        @Volatile
//        private var INSTANCE: LocalDataSource? = null
//        fun getInstance(executors: AppExecutors, database: AppDatabase): LocalDataSource? {
//            if (INSTANCE == null) {
//                synchronized(LocalDataSource::class.java) {
//                    if (INSTANCE == null) {
//                        INSTANCE = LocalDataSource(executors.diskIO, database)
//                    }
//                }
//            }
//            return INSTANCE
//        }
//    }
//
//    //    public LiveData<List<Integer>> dspCategoryCodes;     // ordered by location
//    //    public LiveData<List<CategoryStatus>> nonDspCategories;
//    //    public LiveData<List<CategoryStatus>> customCategories;
//    //    public LiveData<List<KkbCategory>> sDspKkbCategoryList; // ordered by location
//    //    public LiveData<List<Integer>> sDspKkbCategoryCodeList; // ordered by location
//    //    public LiveData<List<Integer>> sAllKkbCategoryCodeList; // ordered by code
//    //    public LiveData<List<KkbCategory>> sNonDspKkbCategoryList;
//    //    public LiveData<List<KkbCategory>> sCustomKkbCategoryList;
//    //    public LiveData<SparseArray<KkbCategory>> sAllKkbCategoryArr;
//    init {
//        val langCode = UtilSystem.getCurrentLangCode(Locale.getDefault().isO3Country)
//        //        categories = appDatabase.categoryStatusDao().getAllStatusesLiveData(langCode);
//        //        dspCategoryCodes = appDatabase.categoryDspStatusDao().getDspCodes();
////        nonDspCategories = appDatabase.categoryDspStatusDao().getNonDspStatusesLiveData(langCode);
////        customCategories = appDatabase.categoryStatusDao().getCustomStatusesLiveData(langCode);
//    }
//}