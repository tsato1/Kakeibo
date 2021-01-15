package com.kakeibo.data.disk

import androidx.sqlite.db.SupportSQLiteQuery
import com.kakeibo.AppExecutors
import com.kakeibo.data.CategoryDspStatus
import com.kakeibo.data.CategoryStatus
import com.kakeibo.data.ItemStatus
import com.kakeibo.data.SubscriptionStatus
import java.util.*
import java.util.concurrent.Executor

class LocalDataSource private constructor(
        private val executor: Executor,
        private val appDatabase: AppDatabase) {
    /**
     * Get the list of subscriptions from the localDataSource and get notified when the data changes.
     */
    val kkbAppStatus = appDatabase.kkbAppStatusDao().getFirst()
    val subscriptions = appDatabase.subscriptionStatusDao().getAll()
    val items = appDatabase.itemStatusDao().getAll()
    val categories = appDatabase.categoryStatusDao().getAll()
    val categoriesForDisplay = appDatabase.categoryStatusDao().getCategoriesForDisplay()
    val categoryCodes = appDatabase.categoryStatusDao().getAllCodes()
    val categoryDspStatuses = appDatabase.categoryDspStatusDao().getAll()

    /***
     *
     * Subscriptions
     *
     */
    fun updateSubscriptions(subscriptions: List<SubscriptionStatus>) {
        executor.execute {
            appDatabase.runInTransaction {
                // Delete existing subscriptions.
                appDatabase.subscriptionStatusDao().deleteAll()
                // Put new subscriptions data into localDataSource.
                appDatabase.subscriptionStatusDao().insertAll(subscriptions)
            }
        }
    }

    /** Delete local user data when the user signs out  */
    fun deleteLocalUserData() = updateSubscriptions(listOf())

    /***
     *
     * kkbAppStatus
     *
     */
    //no update/insert/delete function provided in kkbStatusDao()
    /***
     *
     * ItemStatus
     *
     */
    fun updateItemStatuses(itemStatuses: List<ItemStatus>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.itemStatusDao().deleteAll()
                appDatabase.itemStatusDao().insertAll(itemStatuses)
            }
        }
    }

    fun insertItemStatus(itemStatus: ItemStatus) {
        executor.execute {
            appDatabase.runInTransaction { appDatabase.itemStatusDao().insert(itemStatus) }
        }
    }

    fun deleteAllItemStatus() {
        appDatabase.itemStatusDao().deleteAll()
        updateItemStatuses(ArrayList())
    }

    fun queryItems(query: SupportSQLiteQuery): List<ItemStatus>? {
//        return appDatabase.itemStatusDao().queryItems(query);
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                appDatabase.runInTransaction(new Runnable() {
//                    @Override
//                    public void run() {
//                        appDatabase.itemStatusDao().queryItems(query);
//                    }
//                });
//            }
//        });
        return null
    }

    /***
     *
     * CategoryStatus
     *
     */
    fun insertAllCategories(categoryStatuses: List<CategoryStatus>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.categoryStatusDao().deleteAll()
                appDatabase.categoryStatusDao().insertAll(categoryStatuses)
            }
        }
    }

    fun insertCategoryStatus(categoryStatus: CategoryStatus) {
        executor.execute {
            appDatabase.runInTransaction { appDatabase.categoryStatusDao().insert(categoryStatus) }
        }
    }

    fun deleteAllCategoryStatus() {
        appDatabase.categoryStatusDao().deleteAll()
        this.insertAllCategories(ArrayList())
    }
    /***
     *
     * CategoryLanStatus
     *
     */
    //    public void updateCategoryLanStatuses(final List<CategoryLanStatus> categoryLanStatuses) {
    //        executor.execute(new Runnable() {
    //            @Override
    //            public void run() {
    //                appDatabase.runInTransaction(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        // Delete existing subscriptions.
    //                        appDatabase.categoryLanStatusDao().deleteAll();
    //                        // Put new subscriptions data into localDataSource.
    //                        appDatabase.categoryLanStatusDao().insertAll(categoryLanStatuses);
    //                    }
    //                });
    //            }
    //        });
    //    }
    //
    //    public void insertCategoryLanStatus(final CategoryLanStatus categoryLanStatus) {
    //        executor.execute(new Runnable() {
    //            @Override
    //            public void run() {
    //                appDatabase.runInTransaction(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        appDatabase.categoryLanStatusDao().insert(categoryLanStatus);
    //                    }
    //                });
    //            }
    //        });
    //    }
    //
    //    public void deleteAllCategoryLanStatus() {
    //        appDatabase.categoryLanStatusDao().deleteAll();
    //        updateCategoryLanStatuses(new ArrayList<CategoryLanStatus>());
    //    }

    /***
     *
     * DspCategoryStatus and CategoryDspStatus table
     *
     */
    fun insertAllCategoryDsps(categoryCodes: List<Int>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.categoryStatusDao().deleteAll()
                val categoryDspStatuses: MutableList<CategoryDspStatus> = ArrayList()
                for (i in categoryCodes.indices) {
                    categoryDspStatuses.add(CategoryDspStatus( categoryCodes[i], i ))
                }
                appDatabase.categoryDspStatusDao().insertAll(categoryDspStatuses)
            }
        }
    }

    fun deleteAllCategoryDspStatus() {
        appDatabase.categoryDspStatusDao().deleteAll()
        this.insertAllCategories(ArrayList())
    }

    companion object {
        @Volatile
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(executors: AppExecutors, database: AppDatabase): LocalDataSource =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: LocalDataSource(executors.diskIO, database).also { INSTANCE = it }
                }
    }
}