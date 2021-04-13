package com.kakeibo.data.disk

import com.kakeibo.AppExecutors
import com.kakeibo.data.*
import com.kakeibo.util.UtilDate
import java.util.concurrent.Executor

class LocalDataSource private constructor(
        private val executor: Executor,
        private val appDatabase: AppDatabase) {
    /**
     * Get the list of subscriptions from the localDataSource and get notified when the data changes.
     */
    val kkbApp = appDatabase.kkbAppStatusDao().getFirst()
    val subscriptions = appDatabase.subscriptionStatusDao().getAll()
    val items = appDatabase.itemStatusDao().getAll()
    var itemsThisYear = appDatabase.itemStatusDao().getItemsByYear(UtilDate.getTodaysY())
    var itemsThisMonth = appDatabase.itemStatusDao().getItemsByMonth(UtilDate.getTodaysYM(UtilDate.DATE_FORMAT_DB))
    val categories = appDatabase.categoryStatusDao().getAll()
    val categoriesDisplayed = appDatabase.categoryStatusDao().getCategoriesDisplayed()
    val categoriesNotDisplayed = appDatabase.categoryStatusDao().getCategoriesNotDisplay()
    val categoryDsps = appDatabase.categoryDspStatusDao().getAll()

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
    fun updateKkbApp(kkbAppStatus: KkbAppStatus) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.kkbAppStatusDao().deleteAll()
                appDatabase.kkbAppStatusDao().insert(kkbAppStatus)
            }
        }
    }

    fun updateVal2(val2: Int) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.kkbAppStatusDao().updateVal2(val2)
            }
        }
    }

    /***
     *
     * ItemStatus
     *
     */
    private fun insertItems(itemStatuses: List<ItemStatus>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.itemStatusDao().deleteAll()
                appDatabase.itemStatusDao().insertAll(itemStatuses)
            }
        }
    }

    fun insertItem(itemStatus: ItemStatus) {
        executor.execute {
            appDatabase.runInTransaction { appDatabase.itemStatusDao().insert(itemStatus) }
        }
    }

    fun getItemsByMonth(year: String, month: String) {
        executor.execute {
            itemsThisMonth = appDatabase.itemStatusDao().getItemsByMonth("'$year-$month'")
        }
    }

    fun deleteAllItems() = insertItems(listOf())

    fun deleteItem(id: Long) {
        executor.execute {
            appDatabase.itemStatusDao().delete(id)
        }
    }

    /***
     *
     * CategoryStatus
     *
     */
    private fun insertCategories(categoryStatuses: List<CategoryStatus>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.categoryStatusDao().deleteAll()
                appDatabase.categoryStatusDao().insertAll(categoryStatuses)
            }
        }
    }

    fun insertCategory(categoryStatus: CategoryStatus) {
        executor.execute {
            appDatabase.runInTransaction { appDatabase.categoryStatusDao().insert(categoryStatus) }
        }
    }

    fun deleteAllCategories() = insertCategories(listOf())

    fun deleteCategory(id: Long) {
        executor.execute {
            appDatabase.categoryStatusDao().delete(id)
        }
    }

    /***
     *
     * DspCategoryStatus and CategoryDspStatus table
     *
     */
    fun insertCategoryDsps(categoryDspStatuses: List<CategoryDspStatus>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.categoryDspStatusDao().deleteAll()
                appDatabase.categoryDspStatusDao().insertAll(categoryDspStatuses)
            }
        }
    }

    fun deleteAllCategoryDsps() = insertCategoryDsps(listOf())

    companion object {
        @Volatile
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(executors: AppExecutors, database: AppDatabase): LocalDataSource =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: LocalDataSource(executors.diskIO, database).also { INSTANCE = it }
                }
    }
}