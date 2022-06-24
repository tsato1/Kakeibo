package com.kakeibo.core.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.getOrAwaitValue
import com.kakeibo.util.UtilCurrency
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class) /* declaring that this class will run on emulator (not jvm) */
@SmallTest /* declaring that this test is a unit test */
class ItemDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var itemDao: ItemDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder( /* creating db in memory only */
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        itemDao = database.itemDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    /*
    we don't want concurrency (multi-threading) in tests.
    run blocking is a way to execute a coroutine in the main thread.
    it blocks the main thread, but we can execute a coroutine inside the block
    */
    @Test
    fun insert() = runTest {
        val itemToBeTested = ItemEntity(
            id = 1,
            amount = BigDecimal(100),
            currencyCode = UtilCurrency.CURRENCY_NONE,
            categoryCode = 1,
            memo = "test memo",
            eventDate = "2022-06-15",
            updateDate = "2022-06-15"
        )

        itemDao.insertItem(itemToBeTested)

        val searchModel = SearchModel(
            memo = "test memo"
        )
        val allItems = itemDao.getSpecificItems(
            SimpleSQLiteQuery(searchModel.toQuery(), searchModel.toArgs().toTypedArray())
        ).asLiveData().getOrAwaitValue()
        assertThat(allItems).contains(itemToBeTested)
    }

    @Test
    fun delete() = runTest {
        val itemToBeTested = ItemEntity(
            id = 1,
            amount = BigDecimal(100),
            currencyCode = UtilCurrency.CURRENCY_NONE,
            categoryCode = 1,
            memo = "test memo",
            eventDate = "2022-06-15",
            updateDate = "2022-06-15"
        )

        itemDao.insertItem(itemToBeTested)
        itemDao.deleteItemById(1)

        val searchModel = SearchModel(
            fromDate = "2022-06-01", toDate = "2022-06-30"
        )
        val allItems = itemDao.getSpecificItems(
            SimpleSQLiteQuery(searchModel.toQuery(), searchModel.toArgs().toTypedArray())
        ).asLiveData().getOrAwaitValue()
        assertThat(allItems).doesNotContain(itemToBeTested)
    }

}