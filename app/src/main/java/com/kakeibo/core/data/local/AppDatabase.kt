package com.kakeibo.core.data.local

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.BuildConfig
import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.data.local.entities.KkbApp
import com.kakeibo.data.Subscription
import com.kakeibo.core.data.constants.PrepDB7
import com.kakeibo.feature_main.data.sources.local.ItemDao
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.data.sources.local.entities.LocallyDeletedItemIdEntity
import com.kakeibo.feature_settings.data.sources.local.CustomCategoryDao

@Database(
    entities = [
        KkbApp::class,
        ItemEntity::class,
        CategoryDspEntity::class,
        CategoryEntity::class,
//            SearchCriteria::class,
        Subscription::class],
//        LocallyDeletedItemIdEntity::class],
    version = BuildConfig.versionDB,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val kkbAppDao: KkbAppDao
    abstract val itemDao: ItemDao
    abstract val categoryDao: CategoryDao
    abstract val categoryDspDao: CategoryDspDao
    abstract val customCategoryDao: CustomCategoryDao
//    abstract fun searchCriteriaDao(): SearchCriteriaDao
//    abstract val subscriptionDao: SubscriptionDao

    companion object {
        private val TAG = AppDatabase::class.java.simpleName

        const val DATABASE_NAME = "kakeibo.db"

//        private var INSTANCE: AppDatabase? = null

        @VisibleForTesting
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_1_2")
                PrepDB7.migrate_1_2(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_2_3")
                PrepDB7.migrate_2_3(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_3_4")
                PrepDB7.migrate_3_4(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_4_5")
                PrepDB7.migrate_4_5(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_5_7: Migration = object : Migration(5, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_5_7")
                PrepDB7.migrate_5_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_6_7")
                PrepDB7.migrate_6_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_7_8")
                PrepDB7.migrate_7_8(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                Log.d(TAG, "migration_8_9")
//                PrepDB7.migrate_8_9(database)
            }
        }

//        fun getInstance(context: Context): AppDatabase {
//            if (INSTANCE == null) {
//                synchronized(AppDatabase::class) {
//                    if (INSTANCE == null) {
//                        INSTANCE = Room.databaseBuilder(
//                            context.applicationContext, AppDatabase::class.java, DATABASE_NAME
//                        )
//                            .addCallback(sAppDatabaseCallback)
//                            .addMigrations(
//                                MIGRATION_1_2,
//                                MIGRATION_2_3,
//                                MIGRATION_3_4,
//                                MIGRATION_4_5,
//                                MIGRATION_5_7,
//                                MIGRATION_6_7,
//                                MIGRATION_7_8
//                            )
//                            .build()
//                    }
//                }
//            }

//            return INSTANCE as AppDatabase
//        }

//        val sAppDatabaseCallback = object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                val mKkbAppStatusDao = (INSTANCE as AppDatabase).kkbAppDao
//                val mCategoryStatusDao = (INSTANCE as AppDatabase).categoryDao
//                val mCategoryDspStatusDao = (INSTANCE as AppDatabase).categoryDspDao
//
//                GlobalScope.launch {
//                    mKkbAppStatusDao.insert(PrepDB.initKkbAppTable())
//                    mCategoryStatusDao.insertCategories(PrepDB.prepCategoryStatuses())
//                    mCategoryDspStatusDao.insertCategoryDsps(PrepDB.prepDspCategoryStatuses())
//                }

//                PopulateDbAsync(INSTANCE as AppDatabase).execute()
//            }
//        }

//        private class PopulateDbAsync (db: AppDatabase) : AsyncTask<Unit, Unit, Unit>() {
//            private val mKkbAppStatusDao = db.kkbAppDao
//            private val mCategoryStatusDao = db.categoryDao
//            private val mCategoryDspStatusDao = db.categoryDspDao
//
//            override fun doInBackground(vararg params: Unit) {
//                mKkbAppStatusDao.insert(PrepDB.initKkbAppTable())
//                mCategoryStatusDao.insertCategories(PrepDB.prepCategoryStatuses())
//                mCategoryDspStatusDao.insertCategoryDsps(PrepDB.prepDspCategoryStatuses())
//            }
//        }
    }
}