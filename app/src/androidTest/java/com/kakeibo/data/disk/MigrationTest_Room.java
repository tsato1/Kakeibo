//package com.kakeibo.data.disk;
//
//import android.content.ContentValues;
//import android.database.sqlite.SQLiteDatabase;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import androidx.room.Room;
//import androidx.room.testing.MigrationTestHelper;
//import androidx.sqlite.db.SupportSQLiteDatabase;
//import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import com.kakeibo.data.CategoryDsp;
//import com.kakeibo.data.Category;
//import com.kakeibo.data.Item;
//import com.kakeibo.db.CategoryDBAdapter;
//import com.kakeibo.db.CategoryDspDBAdapter;
//import com.kakeibo.db.CategoryLanDBAdapter;
//import com.kakeibo.db.ItemDBAdapter;
//import com.kakeibo.LiveDataTestUtil;
//import com.kakeibo.db.KkbAppDBAdapter;
//import com.kakeibo.util.UtilCurrency;
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
//
//@RunWith(AndroidJUnit4ClassRunner.class)
//public class MigrationTest_Room {
//    @Rule
//    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
//
//    private static final String TEST_DB_NAME = "kakeibo.db";
//
//    // Helper for creating Room databases and migrations
//    @Rule
//    public final MigrationTestHelper mMigrationTestHelper =
//            new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
//                    AppDatabase.class.getCanonicalName(),
//                    new FrameworkSQLiteOpenHelperFactory());
//
//    private static SupportSQLiteDatabase mDb;
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//        mDb.execSQL("DROP TABLE IF EXISTS " + KkbAppDBAdapter.TABLE_KKBAPP);
//        mDb.execSQL("DROP TABLE IF EXISTS " + ItemDBAdapter.TABLE_NAME);
//        mDb.execSQL("DROP TABLE IF EXISTS subscriptions");
//        mDb.execSQL("DROP TABLE IF EXISTS " + CategoryDBAdapter.TABLE_NAME);
//        mDb.execSQL("DROP TABLE IF EXISTS " + CategoryLanDBAdapter.TABLE_NAME);
//        mDb.execSQL("DROP TABLE IF EXISTS " + CategoryDspDBAdapter.TABLE_NAME);
//        mMigrationTestHelper.closeWhenFinished(mDb);
//    }
//
//    @Test
//    public void startInVersion7_containsCorrectData() throws IOException {
//        // Create the database with the latest version
//        mDb = mMigrationTestHelper.createDatabase(TEST_DB_NAME, 7);
//        // db has schema version 2. insert some data
//        insertItemStatus(
//                7,
//                new BigDecimal(7),
//                0,
//                "currency code",
//                0,
//                "memo test7",
//                "2020-11-11",
//                "2020-11-22",
//                mDb);
//        byte[] b = new byte[]{0,1,0};
//        insertCategoryStatus(
//                0,
//                7,
//                7,
//                7,
//                7,
//                b,
//                8,
//                "desc!",
//                "2020-07-07",
//                mDb);
//        insertCategoryLanStatus(
//                0,
//                7,
//                "ara!", "eng!", "spa!", "fra!", "hin!", "ind!",
//                "ita!", "jpn!", "kor!", "pol!", "por!", "rus!",
//                "tur!", "vie!", "Hans!", "Hant!",
//                mDb);
//        insertCategoryDspStatus(
//                1,
//                7,
//                77,
//                mDb);
//
//        // open the db with Room
//        AppDatabase appDatabase = getMigratedRoomDatabase();
//
//        // verify that the data is correct
//        try {
//            List<Item> dbItems = LiveDataTestUtil.getOrAwaitValue(
//                    appDatabase.itemDao().getAll());
//            Item dbItem = dbItems.get(0);
//            assertEquals(dbItem.getId(), 7);
//            assertEquals(dbItem.getAmount(), new BigDecimal(7));
//            assertEquals(dbItem.getCurrencyCode(), "currency code");
//            assertEquals(dbItem.getCategoryCode(), 0);
//            assertEquals(dbItem.getMemo(), "memo test7");
//            assertEquals(dbItem.getEventDate(),"2020-11-11");
//            assertEquals(dbItem.getUpdateDate(),"2020-11-22");
//
//            List<Category> dbCategories = LiveDataTestUtil.getOrAwaitValue(
//                    appDatabase.categoryDao().getAll());
//            Category dbCategory = dbCategories.get(0);
//            assertEquals(dbCategory.getId(), 0);
//            assertEquals(dbCategory.getCode(), 7);
//            assertEquals(dbCategory.getColor(), 7);
//            assertEquals(dbCategory.getSignificance(), 7);
//            assertEquals(dbCategory.getDrawable(), 7);
////            assertEquals(dbCategoryStatus.getImage(), b);
//            assertEquals(dbCategory.getParent(), 8);
//            assertEquals(dbCategory.getDescription(), "desc!");
//            assertEquals(dbCategory.getSavedDate(), "2020-07-07");
//
//
//
//
//            List<Category> dspCategories = LiveDataTestUtil.getOrAwaitValue(
//                    appDatabase.categoryDspDao().getCategoryStatusesForDsp());
//            Category category = dspCategories.get(0);
//            assertEquals(category.getId(), 1);
//            assertEquals(category.getCode(), 7);
//            assertEquals(category.getName(), "");//arbitrary column name doesn't work
//            assertEquals(category.getColor(), 7);
//            assertEquals(category.getSignificance(), 7);
//            assertEquals(category.getDrawable(), 7);
////            assertEquals(dbCategoryStatus.getImage(), b);
//            assertEquals(category.getParent(), 8);
//            assertEquals(category.getDescription(), "desc!");
//            assertEquals(category.getSavedDate(), "2020-07-07");
//
//
////
////            List<CategoryLanStatus> dbCategroyLanStatuses = LiveDataTestUtil.getOrAwaitValue(
////                    appDatabase.categoryLanStatusDao().getAll());
////            CategoryLanStatus dbCategoryLanStatus = dbCategroyLanStatuses.get(0);
////            assertEquals(dbCategoryLanStatus.getId(), 0);
////            assertEquals(dbCategoryLanStatus.getCode(), 7);
////            assertEquals(dbCategoryLanStatus.getJpn(), "jpn!");
//
//            List<CategoryDsp> dbCategoryDsps = LiveDataTestUtil.getOrAwaitValue(
//                    appDatabase.categoryDspDao().getAll());
//            CategoryDsp dbCategoryDsp = dbCategoryDsps.get(0);
//            assertEquals(dbCategoryDsp.getId(), 1);
//            assertEquals(dbCategoryDsp.getCode(), 7);
//            assertEquals(dbCategoryDsp.getLocation(), 77);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // this is the test from an old room version to a new room version
//    @Test
//    public void migrationFrom7To8_containsCorrectData() throws IOException {
////        // Create the database with version 6
////        SupportSQLiteDatabase db = mMigrationTestHelper.createDatabase(TEST_DB_NAME, 7);
////        // Insert some data
////        insertItemStatus(///, db);
////        //Prepare for the next version
////        db.close();
////
////        // Re-open the database with version 7 and provide
////        // MIGRATION_7_8 as the migration process.
////        mMigrationTestHelper.runMigrationsAndValidate(
////                TEST_DB_NAME,
////                8,
////                true,
////                MIGRATION_7_8);
////
////        AppDatabase latestDb = getMigratedRoomDatabase();
////        // MigrationTestHelper automatically verifies the schema changes, but not the data validity
////        // Validate that the data was migrated properly.
////        try {
////            List<ItemStatus> dbItemStatuses = LiveDataTestUtil.getOrAwaitValue(latestDb.itemStatusDao().getAll());
////            ItemStatus dbItemStatus = dbItemStatuses.get(0);
////            assertEquals(dbItemStatus.getId(), ///.getId());
////            assertEquals(dbItemStatus.getMemo(), ///.getMemo());
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
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
//
//    /***
//     * Below are used only in startInVersion7_containsCorrectData()
//     * @param db
//     */
//    private void insertItemStatus(
//            int id,
//            BigDecimal amount,
//            int fractionDigits,
//            String currencyCode,
//            int categoryCode,
//            String memo,
//            String eventDate,
//            String updateDate,
//            SupportSQLiteDatabase db) {
//        ContentValues values = new ContentValues();
//        values.put(ItemDBAdapter.COL_ID, id);
//        values.put(ItemDBAdapter.COL_AMOUNT, UtilCurrency.getLongAmountFromBigDecimal(amount, fractionDigits));
//        values.put(ItemDBAdapter.COL_CURRENCY_CODE, currencyCode);
//        values.put(ItemDBAdapter.COL_CATEGORY_CODE, categoryCode);
//        values.put(ItemDBAdapter.COL_MEMO, memo);
//        values.put(ItemDBAdapter.COL_EVENT_DATE, eventDate);
//        values.put(ItemDBAdapter.COL_UPDATE_DATE, updateDate);
//        db.insert(ItemDBAdapter.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values);
//    }
//
//    private void insertCategoryStatus(
//            int id,
//            int code,
//            int color,
//            int sign,
//            int drawable,
//            byte[] image,
//            int parent,
//            String desc,
//            String savedDate,
//            SupportSQLiteDatabase db) {
//        ContentValues values = new ContentValues();
//        values.put(CategoryDBAdapter.COL_ID, id);
//        values.put(CategoryDBAdapter.COL_CODE, code);
//        values.put(CategoryDBAdapter.COL_COLOR, color);
//        values.put(CategoryDBAdapter.COL_SIGNIFICANCE, sign);
//        values.put(CategoryDBAdapter.COL_DRAWABLE, drawable);
//        values.put(CategoryDBAdapter.COL_IMAGE, image);
//        values.put(CategoryDBAdapter.COL_PARENT, parent);
//        values.put(CategoryDBAdapter.COL_DESC, desc);
//        values.put(CategoryDBAdapter.COL_SAVED_DATE, savedDate);
//        db.insert(CategoryDBAdapter.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values);
//    }
//
//    private void insertCategoryLanStatus(
//            int id,
//            int code,
//            String ara, String eng, String spa, String fra, String hin, String ind, String ita,
//            String jpn, String kor, String pol, String por, String rus, String tur, String vie,
//            String Hans, String Hant,
//            SupportSQLiteDatabase db) {
//        ContentValues values = new ContentValues();
//        values.put(CategoryLanDBAdapter.COL_ID, id);
//        values.put(CategoryLanDBAdapter.COL_CODE, code);
//        values.put(CategoryLanDBAdapter.COL_ARA, ara);
//        values.put(CategoryLanDBAdapter.COL_ENG, eng);
//        values.put(CategoryLanDBAdapter.COL_SPA, spa);
//        values.put(CategoryLanDBAdapter.COL_FRA, fra);
//        values.put(CategoryLanDBAdapter.COL_HIN, hin);
//        values.put(CategoryLanDBAdapter.COL_IND, ind);
//        values.put(CategoryLanDBAdapter.COL_ITA, ita);
//        values.put(CategoryLanDBAdapter.COL_JPN, jpn);
//        values.put(CategoryLanDBAdapter.COL_KOR, kor);
//        values.put(CategoryLanDBAdapter.COL_POL, pol);
//        values.put(CategoryLanDBAdapter.COL_POR, por);
//        values.put(CategoryLanDBAdapter.COL_RUS, rus);
//        values.put(CategoryLanDBAdapter.COL_TUR, tur);
//        values.put(CategoryLanDBAdapter.COL_VIE, vie);
//        values.put(CategoryLanDBAdapter.COL_Hans, Hans);
//        values.put(CategoryLanDBAdapter.COL_Hant, Hant);
//        db.insert(CategoryLanDBAdapter.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values);
//    }
//
//    private void insertCategoryDspStatus(
//            int id,
//            int code,
//            int location,
//            SupportSQLiteDatabase db) {
//        ContentValues values = new ContentValues();
//        values.put(CategoryDspDBAdapter.COL_ID, id);
//        values.put(CategoryDspDBAdapter.COL_CODE, code);
//        values.put(CategoryDspDBAdapter.COL_LOCATION, location);
//        db.insert(CategoryDspDBAdapter.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values);
//    }
//}
