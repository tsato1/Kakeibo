package com.kakeibo.data.disk

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.BuildConfig
import com.kakeibo.data.*
import com.kakeibo.db.PrepDB
import com.kakeibo.db.PrepDB7

@Database(entities = [
    KkbAppStatus::class,
    ItemStatus::class,
    CategoryStatus::class,
    CategoryDspStatus::class,
    SubscriptionStatus::class
], version = BuildConfig.versionDB)
abstract class AppDatabase : RoomDatabase() {
    abstract fun kkbAppStatusDao(): KkbAppStatusDao
    abstract fun itemStatusDao(): ItemStatusDao
    abstract fun categoryStatusDao(): CategoryStatusDao
    abstract fun categoryDspStatusDao(): CategoryDspStatusDao
    abstract fun subscriptionStatusDao(): SubscriptionStatusDao

    private class PopulateDbAsync (db: AppDatabase) : AsyncTask<Unit, Unit, Unit>() {
        private val mCategoryStatusDao = db.categoryStatusDao()
        private val mCategoryDspStatusDao = db.categoryDspStatusDao()

        override fun doInBackground(vararg params: Unit) {
            Log.d("asdf", "asdf")
            mCategoryStatusDao.insertAll(PrepDB.prepCategoryStatuses())
            mCategoryDspStatusDao.deleteAll()
            mCategoryDspStatusDao.insertAll(PrepDB.prepDspCategoryStatuses())
        }
    }

    companion object {
        private val TAG = AppDatabase::class.java.simpleName
        private const val DATABASE_NAME = "kakeibo.db"
        private var INSTANCE: AppDatabase? = null

        @VisibleForTesting
        val MIGRATION_1_7: Migration = object : Migration(1, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_1_7")
                PrepDB7.migrate_1_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_2_7: Migration = object : Migration(2, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_2_7")
                PrepDB7.migrate_2_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_3_7: Migration = object : Migration(3, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_3_7")
                PrepDB7.migrate_3_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_4_5: Migration = object : Migration(4, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d(TAG, "migration_4_7")
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

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                AppDatabase::class.java, DATABASE_NAME)
                                .addCallback(sAppDatabaseCallback)
                                .addMigrations(
                                        MIGRATION_1_7,
                                        MIGRATION_2_7,
                                        MIGRATION_3_7,
                                        MIGRATION_4_5,
                                        MIGRATION_5_7,
                                        MIGRATION_6_7)
                                .build()
                    }
                }
            }

            return INSTANCE as AppDatabase
        }

        private val sAppDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsync(INSTANCE as AppDatabase).execute()
            }
        }
    }
}