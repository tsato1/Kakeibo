package com.kakeibo.core.data.local

import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.BuildConfig
import com.kakeibo.core.data.constants.PrepDB7
import com.kakeibo.core.data.local.entities.*
//import com.kakeibo.feature_subscriptions.Subscription

@Database(
    entities = [
//        KkbAppEntity::class,
        ItemEntity::class,
        CategoryDspEntity::class,
        CategoryEntity::class,
        SearchEntity::class,
//        Subscription::class,
        LocallyDeletedItemIdEntity::class],
    version = BuildConfig.versionDB,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

//    abstract val kkbAppDao: KkbAppDao
    abstract val itemDao: ItemDao
    abstract val categoryDao: CategoryDao
    abstract val categoryDspDao: CategoryDspDao
    abstract val searchDao: SearchDao
//    abstract val subscriptionDao: SubscriptionDao

    companion object {
        private val TAG = AppDatabase::class.java.simpleName

        const val DATABASE_NAME = "kakeibo.db"

        @VisibleForTesting
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_1_2(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_2_3(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_3_4(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_4_5(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_5_7: Migration = object : Migration(5, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_5_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_6_7(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_7_8(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_8_9(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_9_10(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_10_11(database)
            }
        }

        @VisibleForTesting
        val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                PrepDB7.migrate_11_12(database)
            }
        }
    }
}