package com.kakeibo.data.disk;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.MediumTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.kakeibo.AppExecutors;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.room.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests for the {@link LocalDataSource} implementation with Room.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@MediumTest
public class LocalDataSourceTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final AppExecutors executors = new AppExecutors();

    private static final ItemStatus ITEM_STATUS_1 = new ItemStatus(
            1,
            1000,
            "currency code",
            0,
            0,
            "memo0",
            "1999-09-09",
            "2010-04-22");

    private AppDatabase mDatabase;
    private LocalDataSource mDataSource;

    @Before
    public void initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        mDatabase = Room
                .inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        AppDatabase.class).allowMainThreadQueries()
                .allowMainThreadQueries()
                .build();
        mDataSource = LocalDataSource.getInstance(executors, mDatabase);
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void insertAndGetItemStatus() {
        // When inserting a new user in the data source
        mDataSource.insertOrUpdateItemStatus(ITEM_STATUS_1);

        //The user can be retrieved
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(mDataSource.getAllItemStatus());

            /*** below doesn't work because ItemStatus deesn't contain equals() and hashcode() ***/
//            assertThat(dbItemStatuses, contains(ITEM_STATUS_1));

            ItemStatus dbItemStatus = dbItemStatuses.get(0); // there is only one item
            assertEquals(dbItemStatus.getId(), ITEM_STATUS_1.getId());
            assertEquals(dbItemStatus.getAmount(), ITEM_STATUS_1.getAmount());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateAndGetItemStatus() {
        // Given that we have a user in the data source
        mDataSource.insertOrUpdateItemStatus(ITEM_STATUS_1);

        // When we are updating the name of the user
        ItemStatus updatedItemStatus = new ItemStatus(
                ITEM_STATUS_1.getId(),
                36000,
                "currency code",
                0,
                1,
                "memo1",
                "1999-09-09", "2010-04-22");
        mDataSource.insertOrUpdateItemStatus(updatedItemStatus);

        //The retrieved user has the updated username
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(mDataSource.getAllItemStatus());

            ItemStatus dbItemStatus = dbItemStatuses.get(0); // there should be only one item
            assertEquals(dbItemStatus.getId(), ITEM_STATUS_1.getId());
            assertEquals(dbItemStatus.getAmount(), new BigDecimal(36));
            assertEquals(dbItemStatus.getCategoryCode(), 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void idAutoIncrementTest() {
        // Given that we have a user in the data source
        mDataSource.insertOrUpdateItemStatus(ITEM_STATUS_1);

        ItemStatus upsertedItemStatus = new ItemStatus(
                // without id
                2000,
                "currency code",
                0,
                2,
                "memo2",
                "1999-09-09",
                "2010-04-22");
        mDataSource.insertOrUpdateItemStatus(upsertedItemStatus);

        //The retrieved user has the updated username
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(mDataSource.getAllItemStatus());
            assertEquals(dbItemStatuses.size(), 2); // there should be two items

            ItemStatus dbItemStatus = dbItemStatuses.get(0);
            assertEquals(dbItemStatus.getId(), ITEM_STATUS_1.getId());
            assertEquals(dbItemStatus.getAmount(), ITEM_STATUS_1.getAmount());
            assertEquals(dbItemStatus.getCategoryCode(), ITEM_STATUS_1.getCategoryCode());

            ItemStatus dbItemStatus2 = dbItemStatuses.get(1);
            assertEquals(dbItemStatus2.getId(), 2); // id must be 2, not 0
            assertEquals(dbItemStatus2.getCategoryCode(), upsertedItemStatus.getCategoryCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteAndGetItemStatus() {
        // Given that we have a user in the data source
        mDataSource.insertOrUpdateItemStatus(ITEM_STATUS_1);

        //When we are deleting all users
        mDataSource.deleteAllItemStatus();

        // The user is no longer in the data source
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(mDataSource.getAllItemStatus());
            assertEquals(dbItemStatuses.size(), 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}