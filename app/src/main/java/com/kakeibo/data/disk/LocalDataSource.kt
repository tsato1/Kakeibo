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
    val kkbApp = appDatabase.kkbAppDao().getFirst()
    val subscriptions = appDatabase.subscriptionDao().getAll()
    val items = appDatabase.itemDao().getAll()
    var itemsThisYear = appDatabase.itemDao().getItemsByYear(UtilDate.getTodaysY())
    var itemsThisMonth = appDatabase.itemDao().getItemsByMonth(UtilDate.getTodaysYM(UtilDate.DATE_FORMAT_DB))
    val categories = appDatabase.categoryDao().getAll()
    val categoriesDisplayed = appDatabase.categoryDao().getCategoriesDisplayed()
    val categoriesNotDisplayed = appDatabase.categoryDao().getCategoriesNotDisplay()
    val categoryDsps = appDatabase.categoryDspDao().getAll()

    /***
     *
     * Subscriptions
     *
     */
    fun updateSubscriptions(subscriptions: List<Subscription>) {
        executor.execute {
            appDatabase.runInTransaction {
                // Delete existing subscriptions.
                appDatabase.subscriptionDao().deleteAll()
                // Put new subscriptions data into localDataSource.
                appDatabase.subscriptionDao().insertAll(subscriptions)
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
    fun updateKkbApp(kkbApp: KkbApp) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.kkbAppDao().deleteAll()
                appDatabase.kkbAppDao().insert(kkbApp)
            }
        }
    }

    fun updateVal2(val2: Int) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.kkbAppDao().updateVal2(val2)
            }
        }
    }

    /***
     *
     * ItemStatus
     *
     */
    private fun insertItems(items: List<Item>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.itemDao().deleteAll()
                appDatabase.itemDao().insertAll(items)
            }
        }
    }

    fun insertItem(item: Item) {
        executor.execute {
            appDatabase.runInTransaction { appDatabase.itemDao().insert(item) }
        }
    }

    fun getItemsByMonth(year: String, month: String) {
        executor.execute {
            itemsThisMonth = appDatabase.itemDao().getItemsByMonth("'$year-$month'")
        }
    }

    fun deleteAllItems() = insertItems(listOf())

    fun deleteItem(id: Long) {
        executor.execute {
            appDatabase.itemDao().delete(id)
        }
    }

    /***
     *
     * CategoryStatus
     *
     */
    private fun insertCategories(categories: List<Category>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.categoryDao().deleteAll()
                appDatabase.categoryDao().insertAll(categories)
            }
        }
    }

    fun insertCategory(category: Category) {
        executor.execute {
            appDatabase.runInTransaction { appDatabase.categoryDao().insert(category) }
        }
    }

    fun deleteAllCategories() = insertCategories(listOf())

    fun deleteCategory(id: Long) {
        executor.execute {
            appDatabase.categoryDao().delete(id)
        }
    }

    /***
     *
     * DspCategoryStatus and CategoryDspStatus table
     *
     */
    fun insertCategoryDsps(categoryDsps: List<CategoryDsp>) {
        executor.execute {
            appDatabase.runInTransaction {
                appDatabase.categoryDspDao().deleteAll()
                appDatabase.categoryDspDao().insertAll(categoryDsps)
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