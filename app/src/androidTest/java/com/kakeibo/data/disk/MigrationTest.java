//package com.kakeibo.data.disk;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
//
//import com.kakeibo.LiveDataTestUtil;
//import com.kakeibo.data.Category;
//import com.kakeibo.data.Item;
//import com.kakeibo.data.KkbApp;
//
//import androidx.room.Room;
//import androidx.room.testing.MigrationTestHelper;
//import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.List;
//
//import static com.kakeibo.data.disk.AppDatabase.MIGRATION_1_2;
//import static com.kakeibo.data.disk.AppDatabase.MIGRATION_2_3;
//import static com.kakeibo.data.disk.AppDatabase.MIGRATION_3_4;
//import static com.kakeibo.data.disk.AppDatabase.MIGRATION_4_5;
//import static com.kakeibo.data.disk.AppDatabase.MIGRATION_5_7;
//import static com.kakeibo.data.disk.AppDatabase.MIGRATION_6_7;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//
//
///**
// * Test the migration
// * First, set the original db version at the variable DATABASE_OLD_VERSION
// * Second, set the destination db version at the variable DATABASE_NEW_VERSION
// */
//@RunWith(AndroidJUnit4ClassRunner.class)
//public class MigrationTest {
//    @Rule
//    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
//
//    private static final String TEST_DB_NAME = "kakeibo.db";
//    public static final int DATABASE_OLD_VERSION = 4;
//    public static final int DATABASE_NEW_VERSION = 7;
//
//    // Helper for creating Room databases and migrations
//    @Rule
//    public final MigrationTestHelper mMigrationTestHelper =
//            new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
//                    AppDatabase.class.getCanonicalName(),
//                    new FrameworkSQLiteOpenHelperFactory());
//
//    // Helper for creating SQLite database in version 1
//    private SqliteTestDbOpenHelper mSqliteTestDbHelper;
//
//    @Before
//    public void setUp() {
//        // To test migrations from version 1 of the database, we need to create the database
//        // with version 1 using SQLite API
//        mSqliteTestDbHelper = new SqliteTestDbOpenHelper(ApplicationProvider.getApplicationContext(),
//                TEST_DB_NAME,
//                DATABASE_OLD_VERSION);
//        // We're creating the table for every test, to ensure that the table is in the correct state
//        SqliteDatabaseTestHelper.createKkbAppTable(mSqliteTestDbHelper);
//        SqliteDatabaseTestHelper.createItemsTable(mSqliteTestDbHelper);
//        SqliteDatabaseTestHelper.createCategoriesTable(mSqliteTestDbHelper);
//        SqliteDatabaseTestHelper.createCategoryDspTable(mSqliteTestDbHelper);
//    }
//
//    @After
//    public void tearDown() {
//        // Clear the database after every test
//        SqliteDatabaseTestHelper.clearDatabase(mSqliteTestDbHelper);
//    }
//
//    @Test
//    public void migrationFrom1To7_containsCorrectData() throws IOException {
//        // Create the database with the initial version 1 schema and insert one item
//        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v1_2(
//                17,
//                "11117",
//                "Until",
//                "memo_test",
//                "11",
//                "2020/07",
//                "2020/08/08",
//                mSqliteTestDbHelper);
//
//        mMigrationTestHelper.runMigrationsAndValidate(
//                TEST_DB_NAME,
//                DATABASE_NEW_VERSION,
//                true,
//                MIGRATION_1_2);
//
//        // Get the latest, migrated, version of the database
//        AppDatabase latestDb = getMigratedRoomDatabase();
//
//        // Check that the correct data is in the database
//        try {
//            List<Item> dbItems = LiveDataTestUtil.getOrAwaitValue(latestDb.itemDao().getAll());
//            Item dbItem = dbItems.get(0);
//            assertEquals(dbItem.getId(), 17);
//            assertEquals(dbItem.getAmount(), new BigDecimal(11117000));
//            assertEquals(dbItem.getCurrencyCode(), "===");
//            assertEquals(dbItem.getMemo(), "memo_test");
//            assertEquals(dbItem.getCategoryCode(), 3);
//            assertEquals(dbItem.getEventDate(), "2020-07-11");
//            assertEquals(dbItem.getUpdateDate(), "2020-08-08 00:00:00");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void migrationFrom2To7_containsCorrectData() throws IOException {
//        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v1_2(
//                17,
//                "11117",
//                "Income",
//                "memo_test",
//                "11",
//                "2020/07",
//                "2020/08/08",
//                mSqliteTestDbHelper);
//
//        mMigrationTestHelper.runMigrationsAndValidate(
//                TEST_DB_NAME,
//                DATABASE_NEW_VERSION,
//                true,
//                MIGRATION_2_3);
//
//        // Get the latest, migrated, version of the database
//        AppDatabase latestDb = getMigratedRoomDatabase();
//
//        // Check that the correct data is in the database
//        try {
//            List<Item> dbItems = LiveDataTestUtil.getOrAwaitValue(latestDb.itemDao().getAll());
//            Item dbItem = dbItems.get(0);
//            assertEquals(dbItem.getId(), 17);
//            assertEquals(dbItem.getAmount(), new BigDecimal(11117000)); // no need to divide
//            assertEquals(dbItem.getCurrencyCode(), "===");
//            assertEquals(dbItem.getMemo(), "memo_test");
//            assertEquals(dbItem.getCategoryCode(), 0);
//            assertEquals(dbItem.getEventDate(), "2020-07-11");
//            assertEquals(dbItem.getUpdateDate(), "2020-08-08 00:00:00");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void migrationFrom3To7_containsCorrectData() throws IOException {
//        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v1_2(
//                16,
//                "11116",
//                "Until",
//                "memo_test16",
//                "11",
//                "2020/07",
//                "2020/08/08",
//                mSqliteTestDbHelper);
//
//        SqliteDatabaseTestHelper.upgrade_items_1_3(mSqliteTestDbHelper);
//
//        SqliteDatabaseTestHelper.insertItemStatusToSqlite_v3_6(
//                17,
//                new BigDecimal(11117),
//                "---",
//                0,
//                0,
//                "memo_test17",
//                "2020-07-07",
//                "2020-08-08 00:00:00",
//                mSqliteTestDbHelper);
//
//        mMigrationTestHelper.runMigrationsAndValidate(
//                TEST_DB_NAME,
//                DATABASE_NEW_VERSION,
//                true,
//                MIGRATION_3_4);
//
//        // Get the latest, migrated, version of the database
//        AppDatabase latestDb = getMigratedRoomDatabase();
//
//        // Check that the correct data is in the database
//        try {
//            List<Item> dbItems = LiveDataTestUtil.getOrAwaitValue(latestDb.itemDao().getAll());
//            Item dbItem1 = dbItems.get(0);
//            assertEquals(dbItem1.getId(), 16);
//            assertEquals(dbItem1.getAmount(), new BigDecimal(11116000)); // no need to divide
//            assertEquals(dbItem1.getCurrencyCode(), "===");
//            assertEquals(dbItem1.getMemo(), "memo_test16");
//            assertEquals(dbItem1.getCategoryCode(), 3);
//            assertEquals(dbItem1.getEventDate(), "2020-07-11");
//            assertEquals(dbItem1.getUpdateDate(), "2020-08-08 00:00:00");
//
//            Item dbItem2 = dbItems.get(1);
//            assertEquals(dbItem2.getId(), 17);
//            assertEquals(dbItem2.getAmount(), new BigDecimal(11117));
//            assertEquals(dbItem2.getCurrencyCode(), "---");
//            assertEquals(dbItem2.getMemo(), "memo_test17");
//            assertEquals(dbItem2.getCategoryCode(), 0);
//            assertEquals(dbItem2.getEventDate(), "2020-07-07");
//            assertEquals(dbItem2.getUpdateDate(), "2020-08-08 00:00:00");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // db version 4 has just additional KkbApp table
//    @Test
//    public void migrationFrom4To7_containsCorrectData() throws IOException {
//        SqliteDatabaseTestHelper.upgrade_categories_5_6(mSqliteTestDbHelper);
//
//        SqliteDatabaseTestHelper.insertKkbApp(
//                1,
//                "name", "type", "updateDate",
//                1, 2, 3, "1", "2", "3",
//                mSqliteTestDbHelper);
//
//        mMigrationTestHelper.runMigrationsAndValidate(
//                TEST_DB_NAME,
//                DATABASE_NEW_VERSION,
//                true,
//                MIGRATION_4_5,
//                MIGRATION_5_7);
//
//        // Get the latest, migrated, version of the database
//        AppDatabase latestDb = getMigratedRoomDatabase();
//
//        // Check that the correct data is in the database
//        try {
//            KkbApp kkbApp = LiveDataTestUtil.getOrAwaitValue(
//                    latestDb.kkbAppDao().getFirst());
//            assertEquals(kkbApp.getId(), 1);
//            assertEquals(kkbApp.getName(), "name");
//            assertEquals(kkbApp.getType(), "type");
//            assertEquals(kkbApp.getUpdateDate(), "updateDate");
//            assertEquals(kkbApp.getValInt1(), 1);
//            assertEquals(kkbApp.getValInt2(), 2);
//            assertEquals(kkbApp.getValInt3(), 3);
//            assertEquals(kkbApp.getValStr1(), "1");
//            assertEquals(kkbApp.getValStr2(), "2");
//            assertEquals(kkbApp.getValStr3(), "3");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void migrationFrom5To7_containsCorrectData() throws IOException {
//        // upgrade the db version from 5 to 6
//        SqliteDatabaseTestHelper.upgrade_categories_5_6(mSqliteTestDbHelper);
//        SqliteDatabaseTestHelper.upgrade_items_1_3(mSqliteTestDbHelper);
//
//        SqliteDatabaseTestHelper.insertCategoryStatus_v6(
//                3,
//                57,
//                "category name",
//                57,
//                0,
//                57,
//                new byte[]{0,1,2,3,4},
//                57,
//                "description test",
//                "2020/08/06",
//                mSqliteTestDbHelper);
//
//        mMigrationTestHelper.runMigrationsAndValidate(
//                TEST_DB_NAME,
//                DATABASE_NEW_VERSION,
//                true,
//                MIGRATION_5_7);
//
//        // Get the latest, migrated, version of the database
//        AppDatabase latestDb = getMigratedRoomDatabase();
//
//        // Check that the correct data is in the database
//        try {
//            List<Category> dbCategories = LiveDataTestUtil.getOrAwaitValue(
//                    latestDb.categoryDao().getAll());
//            Category dbCategory = dbCategories.get(0);
//            assertEquals(dbCategory.getId(), 1);
//            assertEquals(dbCategory.getCode(), 0); // Income=0
//            assertEquals(dbCategory.getColor(), 1); // Income=1
//            assertEquals(dbCategory.getSignificance(), 0);
//            assertEquals(dbCategory.getParent(), -1);
//            assertEquals(dbCategory.getDescription(), "");
//            assertEquals(dbCategory.getSavedDate(), "");
//
//            dbCategory = dbCategories.get(1);
//            assertEquals(dbCategory.getId(), 2);
//            assertEquals(dbCategory.getCode(), 1); // Comm = 1
//            assertEquals(dbCategory.getColor(), 0); // Expense = 0
//            assertEquals(dbCategory.getSignificance(), 0);
//            assertEquals(dbCategory.getParent(), -1);
//            assertEquals(dbCategory.getDescription(), "");
//            assertEquals(dbCategory.getSavedDate(), "");
//
//            dbCategory = dbCategories.get(2);
//            assertEquals(dbCategory.getId(), 3);
//            assertEquals(dbCategory.getCode(), 57);
//            assertEquals(dbCategory.getColor(), 57);
//            assertEquals(dbCategory.getSignificance(), 0); // there is no original significance that can be set in version 5
//            assertEquals(dbCategory.getDrawable(), 57);
//            assertNull(dbCategory.getImage());
//            assertEquals(dbCategory.getParent(), -1); // there is no original parent that can be set in version 5
//            assertEquals(dbCategory.getDescription(), "description test");
//            assertEquals(dbCategory.getSavedDate(), "2020/08/06");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void migrationFrom6To7_containsCorrectData() throws IOException {
//        SqliteDatabaseTestHelper.upgrade_items_1_3(mSqliteTestDbHelper);
//        SqliteDatabaseTestHelper.upgrade_categories_5_6(mSqliteTestDbHelper);
//
//        SqliteDatabaseTestHelper.insertCategoryStatus_v6(
//                25,
//                1001,
//                "category name",
//                67,
//                0,
//                67,
//                new byte[]{0,1,2,3,4},
//                67,
//                "description test",
//                "2020/08/06",
//                mSqliteTestDbHelper);
//
//        mMigrationTestHelper.runMigrationsAndValidate(
//                TEST_DB_NAME,
//                DATABASE_NEW_VERSION,
//                true,
//                MIGRATION_6_7);
//
//        // Get the latest, migrated, version of the database
//        AppDatabase latestDb = getMigratedRoomDatabase();
//
//        // Check that the correct data is in the database
//        try {
//            List<Category> dbCategories = LiveDataTestUtil.getOrAwaitValue(
//                    latestDb.categoryDao().getAll());
//            Category dbCategory = dbCategories.get(0);
//            assertEquals(dbCategory.getId(), 1);
//            assertEquals(dbCategory.getCode(), 0); // Income=0
//            assertEquals(dbCategory.getName(), "");
//            assertEquals(dbCategory.getColor(), 1); // Income=1
//            assertEquals(dbCategory.getSignificance(), 0);
//            assertEquals(dbCategory.getParent(), -1);
//            assertEquals(dbCategory.getDescription(), "");
//            assertEquals(dbCategory.getSavedDate(), "");
//
//            dbCategory = dbCategories.get(1);
//            assertEquals(dbCategory.getId(), 2);
//            assertEquals(dbCategory.getCode(), 1); // Comm = 1
//            assertEquals(dbCategory.getName(), "");
//            assertEquals(dbCategory.getColor(), 0); // Expense = 0
//            assertEquals(dbCategory.getSignificance(), 0);
//            assertEquals(dbCategory.getParent(), -1);
//            assertEquals(dbCategory.getDescription(), "");
//            assertEquals(dbCategory.getSavedDate(), "");
//
//            dbCategory = dbCategories.get(23);
//            assertEquals(dbCategory.getId(), 24);
//            assertEquals(dbCategory.getCode(), 23);
//            assertEquals(dbCategory.getName(), "");
//            assertEquals(dbCategory.getColor(), 0); // Expense = 0, Income = 1
//            assertEquals(dbCategory.getSignificance(), 0);
//            assertEquals(dbCategory.getParent(), -1);
//            assertEquals(dbCategory.getDescription(), "");
//            assertEquals(dbCategory.getSavedDate(), "");
//
//            dbCategory = dbCategories.get(24);
//            assertEquals(dbCategory.getId(), 25);
//            assertEquals(dbCategory.getCode(), 1001);
//            assertEquals(dbCategory.getName(), "category name");
//            assertEquals(dbCategory.getColor(), 67);
//            assertEquals(dbCategory.getSignificance(), 0);
//            assertEquals(dbCategory.getDrawable(), 67);
////            assertNull(dbCategoryStatus.getImage());
//            assertEquals(dbCategory.getParent(), 67);
//            assertEquals(dbCategory.getDescription(), "description test");
//            assertEquals(dbCategory.getSavedDate(), "2020/08/06");
//
//
////            List<CategoryLanStatus> dbCategoryLanStatuses = LiveDataTestUtil.getOrAwaitValue(
////                    latestDb.categoryLanStatusDao().getAll());
////            CategoryLanStatus dbCategoryLanStatus = dbCategoryLanStatuses.get(0);
////            assertEquals(dbCategoryLanStatus.getId(), 1);
////            assertEquals(dbCategoryLanStatus.getCode(), 1);
////            assertEquals(dbCategoryLanStatus.getEng(), "Income");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private AppDatabase getMigratedRoomDatabase() {
//        AppDatabase database = Room.databaseBuilder(ApplicationProvider.getApplicationContext(),
//                AppDatabase.class, TEST_DB_NAME)
//                .addMigrations(
//                        MIGRATION_1_2,
//                        MIGRATION_2_3,
//                        MIGRATION_3_4,
//                        MIGRATION_4_5,
//                        MIGRATION_5_7,
//                        MIGRATION_6_7)
//                .build();
//        // close the database and release any stream resources when the test finishes
//        mMigrationTestHelper.closeWhenFinished(database);
//        return database;
//    }
//}
