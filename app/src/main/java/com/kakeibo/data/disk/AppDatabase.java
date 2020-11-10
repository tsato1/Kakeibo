package com.kakeibo.data.disk;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kakeibo.BuildConfig;
import com.kakeibo.data.CategoryDspStatus;
import com.kakeibo.data.CategoryLanStatus;
import com.kakeibo.data.CategoryStatus;
import com.kakeibo.data.ItemStatus;
import com.kakeibo.data.SubscriptionStatus;
import com.kakeibo.db.PrepDB7;

@Database(entities = {
        ItemStatus.class,
        CategoryStatus.class,
        CategoryLanStatus.class,
        CategoryDspStatus.class,
        SubscriptionStatus.class},
        version = BuildConfig.versionDB)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();

    private static final String DATABASE_NAME = "kakeibo.db";

    private static AppDatabase INSTANCE;

    public abstract ItemStatusDao itemStatusDao();
    public abstract CategoryStatusDao categoryStatusDao();
    public abstract CategoryLanStatusDao categoryLanStatusDao();
    public abstract CategoryDspStatusDao categoryDspStatusDao();
    public abstract SubscriptionStatusDao subscriptionStatusDao();

    private static final Object sLock = new Object();

    @VisibleForTesting
    static final Migration MIGRATION_1_7 = new Migration(1, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG,  "migration_1_7");
            PrepDB7.migrate_1_7(database);
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_2_7 = new Migration(2, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG,  "migration_2_7");
            PrepDB7.migrate_2_7(database);
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_3_7 = new Migration(3, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG,  "migration_3_7");
            PrepDB7.migrate_3_7(database);
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_4_7 = new Migration(4, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG,  "migration_4_7");
            PrepDB7.migrate_4_7(database);
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_5_7 = new Migration(5, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG,  "migration_5_7");
            PrepDB7.migrate_5_7(database);
        }
    };

    @VisibleForTesting
    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.d(TAG, "migration_6_7");
            PrepDB7.migrate_6_7(database);
        }
    };
        ;

    public static AppDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                        .addMigrations(
                                MIGRATION_1_7,
                                MIGRATION_2_7,
                                MIGRATION_3_7,
                                MIGRATION_4_7,
                                MIGRATION_5_7,
                                MIGRATION_6_7)
                        .build();
            }
            return INSTANCE;
        }
    }
}