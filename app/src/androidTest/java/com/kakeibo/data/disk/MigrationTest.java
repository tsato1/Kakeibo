package com.kakeibo.data.disk;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.kakeibo.LiveDataTestUtil;
import com.kakeibo.data.CategoryStatus;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.data.KkbAppStatus;

import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.kakeibo.data.disk.AppDatabase.MIGRATION_1_7;
import static com.kakeibo.data.disk.AppDatabase.MIGRATION_2_7;
import static com.kakeibo.data.disk.AppDatabase.MIGRATION_3_7;
import static com.kakeibo.data.disk.AppDatabase.MIGRATION_4_5;
import static com.kakeibo.data.disk.AppDatabase.MIGRATION_5_7;
import static com.kakeibo.data.disk.AppDatabase.MIGRATION_6_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Test the migration
 * First, set the original db version at the variable DATABASE_OLD_VERSION
 * Second, set the destination db version at the variable DATABASE_NEW_VERSION
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class MigrationTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final String TEST_DB_NAME = "kakeibo.db";
    public static final int DATABASE_OLD_VERSION = 4;
    public static final int DATABASE_NEW_VERSION = 7;

    // Helper for creating Room databases and migrations
    @Rule
    public final MigrationTestHelper mMigrationTestHelper =
            new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                    AppDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());

    // Helper for creating SQLite database in version 1
    private SqliteTestDbOpenHelper mSqliteTestDbHelper;

    @Before
    public void setUp() {
        // To test migrations from version 1 of the database, we need to create the database
        // with version 1 using SQLite API
        mSqliteTestDbHelper = new SqliteTestDbOpenHelper(ApplicationProvider.getApplicationContext(),
                TEST_DB_NAME,
                DATABASE_OLD_VERSION);
        // We're creating the table for every test, to ensure that the table is in the correct state
        SqliteDatabaseTestHelper.createKkbAppTable(mSqliteTestDbHelper);
        SqliteDatabaseTestHelper.createItemsTable(mSqliteTestDbHelper);
        SqliteDatabaseTestHelper.createCategoriesTable(mSqliteTestDbHelper);
        SqliteDatabaseTestHelper.createCategoryDspTable(mSqliteTestDbHelper);
    }

    @After
    public void tearDown() {
        // Clear the database after every test
        SqliteDatabaseTestHelper.clearDatabase(mSqliteTestDbHelper);
    }

    @Test
    public void migrationFrom1To7_containsCorrectData() throws IOException {
        // Create the database with the initial version 1 schema and insert one item
        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v1_2(
                17,
                "11117",
                "Until",
                "memo_test",
                "11",
                "2020/07",
                "2020/08/08",
                mSqliteTestDbHelper);

        mMigrationTestHelper.runMigrationsAndValidate(
                TEST_DB_NAME,
                DATABASE_NEW_VERSION,
                true,
                MIGRATION_1_7);

        // Get the latest, migrated, version of the database
        AppDatabase latestDb = getMigratedRoomDatabase();

        // Check that the correct data is in the database
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(latestDb.itemStatusDao().getAll());
            ItemStatus dbItemStatus = dbItemStatuses.get(0);
            assertEquals(dbItemStatus.getId(), 17);
            assertEquals(dbItemStatus.getAmount(), new BigDecimal(11117000));
            assertEquals(dbItemStatus.getCurrencyCode(), "===");
            assertEquals(dbItemStatus.getMemo(), "memo_test");
            assertEquals(dbItemStatus.getCategoryCode(), 3);
            assertEquals(dbItemStatus.getEventDate(), "2020-07-11");
            assertEquals(dbItemStatus.getUpdateDate(), "2020-08-08 00:00:00");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void migrationFrom2To7_containsCorrectData() throws IOException {
        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v1_2(
                17,
                "11117",
                "Income",
                "memo_test",
                "11",
                "2020/07",
                "2020/08/08",
                mSqliteTestDbHelper);

        mMigrationTestHelper.runMigrationsAndValidate(
                TEST_DB_NAME,
                DATABASE_NEW_VERSION,
                true,
                MIGRATION_2_7);

        // Get the latest, migrated, version of the database
        AppDatabase latestDb = getMigratedRoomDatabase();

        // Check that the correct data is in the database
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(latestDb.itemStatusDao().getAll());
            ItemStatus dbItemStatus = dbItemStatuses.get(0);
            assertEquals(dbItemStatus.getId(), 17);
            assertEquals(dbItemStatus.getAmount(), new BigDecimal(11117000)); // no need to divide
            assertEquals(dbItemStatus.getCurrencyCode(), "===");
            assertEquals(dbItemStatus.getMemo(), "memo_test");
            assertEquals(dbItemStatus.getCategoryCode(), 0);
            assertEquals(dbItemStatus.getEventDate(), "2020-07-11");
            assertEquals(dbItemStatus.getUpdateDate(), "2020-08-08 00:00:00");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void migrationFrom3To7_containsCorrectData() throws IOException {
        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v1_2(
                16,
                "11116",
                "Until",
                "memo_test16",
                "11",
                "2020/07",
                "2020/08/08",
                mSqliteTestDbHelper);

        SqliteDatabaseTestHelper.upgrade_items_1_3(mSqliteTestDbHelper);

        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v3_6(
                17,
                new BigDecimal(11117),
                "---",
                0,
                0,
                "memo_test17",
                "2020-07-07",
                "2020-08-08 00:00:00",
                mSqliteTestDbHelper);

        mMigrationTestHelper.runMigrationsAndValidate(
                TEST_DB_NAME,
                DATABASE_NEW_VERSION,
                true,
                MIGRATION_3_7);

        // Get the latest, migrated, version of the database
        AppDatabase latestDb = getMigratedRoomDatabase();

        // Check that the correct data is in the database
        try {
            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(latestDb.itemStatusDao().getAll());
            ItemStatus dbItemStatus1 = dbItemStatuses.get(0);
            assertEquals(dbItemStatus1.getId(), 16);
            assertEquals(dbItemStatus1.getAmount(), new BigDecimal(11116000)); // no need to divide
            assertEquals(dbItemStatus1.getCurrencyCode(), "===");
            assertEquals(dbItemStatus1.getMemo(), "memo_test16");
            assertEquals(dbItemStatus1.getCategoryCode(), 3);
            assertEquals(dbItemStatus1.getEventDate(), "2020-07-11");
            assertEquals(dbItemStatus1.getUpdateDate(), "2020-08-08 00:00:00");

            ItemStatus dbItemStatus2 = dbItemStatuses.get(1);
            assertEquals(dbItemStatus2.getId(), 17);
            assertEquals(dbItemStatus2.getAmount(), new BigDecimal(11117));
            assertEquals(dbItemStatus2.getCurrencyCode(), "---");
            assertEquals(dbItemStatus2.getMemo(), "memo_test17");
            assertEquals(dbItemStatus2.getCategoryCode(), 0);
            assertEquals(dbItemStatus2.getEventDate(), "2020-07-07");
            assertEquals(dbItemStatus2.getUpdateDate(), "2020-08-08 00:00:00");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // db version 4 has just additional KkbApp table
    @Test
    public void migrationFrom4To7_containsCorrectData() throws IOException {
        SqliteDatabaseTestHelper.upgrade_categories_5_6(mSqliteTestDbHelper);

        SqliteDatabaseTestHelper.insertKkbApp(
                1,
                "name", "type", "updateDate",
                1, 2, 3, "1", "2", "3",
                mSqliteTestDbHelper);

        mMigrationTestHelper.runMigrationsAndValidate(
                TEST_DB_NAME,
                DATABASE_NEW_VERSION,
                true,
                MIGRATION_4_5,
                MIGRATION_5_7);

        // Get the latest, migrated, version of the database
        AppDatabase latestDb = getMigratedRoomDatabase();

        // Check that the correct data is in the database
        try {
            KkbAppStatus kkbAppStatus = LiveDataTestUtil.getOrAwaitValue(
                    latestDb.kkbAppStatusDao().getFirst());
            assertEquals(kkbAppStatus.getId(), 1);
            assertEquals(kkbAppStatus.getName(), "name");
            assertEquals(kkbAppStatus.getType(), "type");
            assertEquals(kkbAppStatus.getUpdateDate(), "updateDate");
            assertEquals(kkbAppStatus.getValInt1(), 1);
            assertEquals(kkbAppStatus.getValInt2(), 2);
            assertEquals(kkbAppStatus.getValInt3(), 3);
            assertEquals(kkbAppStatus.getValStr1(), "1");
            assertEquals(kkbAppStatus.getValStr2(), "2");
            assertEquals(kkbAppStatus.getValStr3(), "3");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void migrationFrom5To7_containsCorrectData() throws IOException {
        // upgrade the db version from 5 to 6
        SqliteDatabaseTestHelper.upgrade_categories_5_6(mSqliteTestDbHelper);
        SqliteDatabaseTestHelper.upgrade_items_1_3(mSqliteTestDbHelper);

        SqliteDatabaseTestHelper.insertCategoryStatus_v6(
                3,
                57,
                "category name",
                57,
                0,
                57,
                new byte[]{0,1,2,3,4},
                57,
                "description test",
                "2020/08/06",
                mSqliteTestDbHelper);

        mMigrationTestHelper.runMigrationsAndValidate(
                TEST_DB_NAME,
                DATABASE_NEW_VERSION,
                true,
                MIGRATION_5_7);

        // Get the latest, migrated, version of the database
        AppDatabase latestDb = getMigratedRoomDatabase();

        // Check that the correct data is in the database
        try {
            List<CategoryStatus> dbCategoryStatuses = LiveDataTestUtil.getOrAwaitValue(
                    latestDb.categoryStatusDao().getAllStatusesLiveData());
            CategoryStatus dbCategoryStatus = dbCategoryStatuses.get(0);
            assertEquals(dbCategoryStatus.getId(), 1);
            assertEquals(dbCategoryStatus.getCode(), 0); // Income=0
            assertEquals(dbCategoryStatus.getColor(), 1); // Income=1
            assertEquals(dbCategoryStatus.getSignificance(), 0);
            assertEquals(dbCategoryStatus.getParent(), -1);
            assertEquals(dbCategoryStatus.getDescription(), "");
            assertEquals(dbCategoryStatus.getSavedDate(), "");

            dbCategoryStatus = dbCategoryStatuses.get(1);
            assertEquals(dbCategoryStatus.getId(), 2);
            assertEquals(dbCategoryStatus.getCode(), 1); // Comm = 1
            assertEquals(dbCategoryStatus.getColor(), 0); // Expense = 0
            assertEquals(dbCategoryStatus.getSignificance(), 0);
            assertEquals(dbCategoryStatus.getParent(), -1);
            assertEquals(dbCategoryStatus.getDescription(), "");
            assertEquals(dbCategoryStatus.getSavedDate(), "");

            dbCategoryStatus = dbCategoryStatuses.get(2);
            assertEquals(dbCategoryStatus.getId(), 3);
            assertEquals(dbCategoryStatus.getCode(), 57);
            assertEquals(dbCategoryStatus.getColor(), 57);
            assertEquals(dbCategoryStatus.getSignificance(), 0); // there is no original significance that can be set in version 5
            assertEquals(dbCategoryStatus.getDrawable(), 57);
            assertNull(dbCategoryStatus.getImage());
            assertEquals(dbCategoryStatus.getParent(), -1); // there is no original parent that can be set in version 5
            assertEquals(dbCategoryStatus.getDescription(), "description test");
            assertEquals(dbCategoryStatus.getSavedDate(), "2020/08/06");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void migrationFrom6To7_containsCorrectData() throws IOException {
        SqliteDatabaseTestHelper.upgrade_items_1_3(mSqliteTestDbHelper);
        SqliteDatabaseTestHelper.upgrade_categories_5_6(mSqliteTestDbHelper);

        SqliteDatabaseTestHelper.insertCategoryStatus_v6(
                25,
                1001,
                "category name",
                67,
                0,
                67,
                new byte[]{0,1,2,3,4},
                67,
                "description test",
                "2020/08/06",
                mSqliteTestDbHelper);

        mMigrationTestHelper.runMigrationsAndValidate(
                TEST_DB_NAME,
                DATABASE_NEW_VERSION,
                true,
                MIGRATION_6_7);

        // Get the latest, migrated, version of the database
        AppDatabase latestDb = getMigratedRoomDatabase();

        // Check that the correct data is in the database
        try {
            List<CategoryStatus> dbCategoryStatuses = LiveDataTestUtil.getOrAwaitValue(
                    latestDb.categoryStatusDao().getAllStatusesLiveData());
            CategoryStatus dbCategoryStatus = dbCategoryStatuses.get(0);
            assertEquals(dbCategoryStatus.getId(), 1);
            assertEquals(dbCategoryStatus.getCode(), 0); // Income=0
            assertEquals(dbCategoryStatus.getName(), "");
            assertEquals(dbCategoryStatus.getColor(), 1); // Income=1
            assertEquals(dbCategoryStatus.getSignificance(), 0);
            assertEquals(dbCategoryStatus.getParent(), -1);
            assertEquals(dbCategoryStatus.getDescription(), "");
            assertEquals(dbCategoryStatus.getSavedDate(), "");

            dbCategoryStatus = dbCategoryStatuses.get(1);
            assertEquals(dbCategoryStatus.getId(), 2);
            assertEquals(dbCategoryStatus.getCode(), 1); // Comm = 1
            assertEquals(dbCategoryStatus.getName(), "");
            assertEquals(dbCategoryStatus.getColor(), 0); // Expense = 0
            assertEquals(dbCategoryStatus.getSignificance(), 0);
            assertEquals(dbCategoryStatus.getParent(), -1);
            assertEquals(dbCategoryStatus.getDescription(), "");
            assertEquals(dbCategoryStatus.getSavedDate(), "");

            dbCategoryStatus = dbCategoryStatuses.get(23);
            assertEquals(dbCategoryStatus.getId(), 24);
            assertEquals(dbCategoryStatus.getCode(), 23);
            assertEquals(dbCategoryStatus.getName(), "");
            assertEquals(dbCategoryStatus.getColor(), 0); // Expense = 0, Income = 1
            assertEquals(dbCategoryStatus.getSignificance(), 0);
            assertEquals(dbCategoryStatus.getParent(), -1);
            assertEquals(dbCategoryStatus.getDescription(), "");
            assertEquals(dbCategoryStatus.getSavedDate(), "");

            dbCategoryStatus = dbCategoryStatuses.get(24);
            assertEquals(dbCategoryStatus.getId(), 25);
            assertEquals(dbCategoryStatus.getCode(), 1001);
            assertEquals(dbCategoryStatus.getName(), "category name");
            assertEquals(dbCategoryStatus.getColor(), 67);
            assertEquals(dbCategoryStatus.getSignificance(), 0);
            assertEquals(dbCategoryStatus.getDrawable(), 67);
//            assertNull(dbCategoryStatus.getImage());
            assertEquals(dbCategoryStatus.getParent(), 67);
            assertEquals(dbCategoryStatus.getDescription(), "description test");
            assertEquals(dbCategoryStatus.getSavedDate(), "2020/08/06");


//            List<CategoryLanStatus> dbCategoryLanStatuses = LiveDataTestUtil.getOrAwaitValue(
//                    latestDb.categoryLanStatusDao().getAll());
//            CategoryLanStatus dbCategoryLanStatus = dbCategoryLanStatuses.get(0);
//            assertEquals(dbCategoryLanStatus.getId(), 1);
//            assertEquals(dbCategoryLanStatus.getCode(), 1);
//            assertEquals(dbCategoryLanStatus.getEng(), "Income");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private AppDatabase getMigratedRoomDatabase() {
        AppDatabase database = Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class, TEST_DB_NAME)
                .addMigrations(
                        MIGRATION_1_7,
                        MIGRATION_2_7,
                        MIGRATION_3_7,
                        MIGRATION_4_5,
                        MIGRATION_5_7,
                        MIGRATION_6_7)
                .build();
        // close the database and release any stream resources when the test finishes
        mMigrationTestHelper.closeWhenFinished(database);
        return database;
    }
}
