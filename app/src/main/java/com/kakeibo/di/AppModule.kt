package com.kakeibo.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.core.data.preferences.AppPreferencesImpl
import com.kakeibo.core.data.constants.PrepDB
import com.kakeibo.core.data.local.*
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.feature_main.data.repositories.DisplayedCategoryRepositoryImpl
import com.kakeibo.feature_main.data.repositories.DisplayedItemRepositoryImpl
import com.kakeibo.feature_main.data.repositories.SearchRepositoryImpl
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import com.kakeibo.feature_main.domain.repositories.SearchRepository
import com.kakeibo.feature_main.domain.use_cases.*
import com.kakeibo.feature_main.domain.use_cases.use_case_input.InsertItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.*
import com.kakeibo.feature_main.domain.use_cases.use_case_search.*
import com.kakeibo.feature_settings.data.repositories.CategoryRearrangeRepositoryImpl
import com.kakeibo.feature_settings.data.repositories.CustomCategoryRepositoryImpl
import com.kakeibo.feature_settings.data.repositories.ItemRepositoryImpl
import com.kakeibo.core.data.repositories.KkbAppRepositoryImpl
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository
import com.kakeibo.feature_settings.domain.repositories.ItemRepository
import com.kakeibo.core.domain.repositories.KkbAppRepository
import com.kakeibo.feature_settings.domain.use_cases.CustomCategoryUseCases
import com.kakeibo.feature_settings.domain.use_cases.CategoryRearrangeUseCases
import com.kakeibo.feature_settings.domain.use_cases.ItemUseCases
import com.kakeibo.core.domain.use_cases.KkbAppUseCases
import com.kakeibo.feature_settings.domain.use_cases.custom_category_detail.GetCustomCategoryByIdUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_detail.InsertCustomCategoryUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_list.DeleteCustomCategoryUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_list.GetAllCustomCategoriesUseCase
import com.kakeibo.feature_settings.domain.use_cases.items.DeleteAllItemsUseCase
import com.kakeibo.core.domain.use_cases.kkbapp.GetKkbAppUseCase
import com.kakeibo.core.domain.use_cases.kkbapp.InsertKkbAppUseCase
import com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories.GetDisplayedCategoriesUseCase
import com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories.GetNonDisplayedCategoriesUseCase
import com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories.UpdateDisplayedCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        app: Application,
        providerKkbAppDao: Provider<KkbAppDao>,
        providerCategoryDao: Provider<CategoryDao>,
        providerCategoryDspDao: Provider<CategoryDspDao>
    ): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .addCallback(
                object: RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        MainScope().launch {
                            providerKkbAppDao.get().insert(PrepDB.initKkbAppTable())
                            providerCategoryDao.get().insertCategories(PrepDB.prepCategoryStatuses())
                            providerCategoryDspDao.get().insertCategoryDsps(PrepDB.prepDspCategoryStatuses())
                        }
                    }
                }
            )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_7,
                AppDatabase.MIGRATION_6_7,
                AppDatabase.MIGRATION_7_8,
                AppDatabase.MIGRATION_8_9
            )
            .build()
    }

    /*
    kkbAppDao, categoryDao, and categoryDspDao are used in the providing db function above
     */
    @Singleton
    @Provides
    fun provideKkbAppDao(db: AppDatabase) = db.kkbAppDao

    @Singleton
    @Provides
    fun provideCategoryDao(db: AppDatabase) = db.categoryDao

    @Singleton
    @Provides
    fun provideCategoryDspDao(db: AppDatabase) = db.categoryDspDao

    /*

     */
    @Singleton
    @Provides
    fun provideKkbAppRepository(db: AppDatabase): KkbAppRepository {
        return KkbAppRepositoryImpl(db.kkbAppDao)
    }

    @Provides
    @Singleton
    fun provideDisplayedItemRepository(db: AppDatabase): DisplayedItemRepository {
        return DisplayedItemRepositoryImpl(db.itemDao)
    }

    @Provides
    @Singleton
    fun provideItemRepository(db: AppDatabase): ItemRepository {
        return ItemRepositoryImpl(db.itemDao)
    }

    @Provides
    @Singleton
    fun provideDisplayedCategoryRepository(db: AppDatabase): DisplayedCategoryRepository {
        return DisplayedCategoryRepositoryImpl(db.categoryDao)
    }

    @Provides
    @Singleton
    fun provideCategoryRearrangeRepository(db: AppDatabase): CategoryRearrangeRepository {
        return CategoryRearrangeRepositoryImpl(db.categoryDao, db.categoryDspDao)
    }

    @Provides
    @Singleton
    fun provideCustomCategoryRepository(db: AppDatabase): CustomCategoryRepository {
        return CustomCategoryRepositoryImpl(db.categoryDao)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(db: AppDatabase): SearchRepository {
        return SearchRepositoryImpl(db.searchDao)
    }

    @Provides
    @Singleton
    fun provideKkbAppUseCases(repository: KkbAppRepository): KkbAppUseCases {
        return KkbAppUseCases(
            getKkbAppUseCase = GetKkbAppUseCase(repository),
            insertKkbAppUseCase = InsertKkbAppUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDisplayedItemUseCases(
        @ApplicationContext context: Context,
        repository: DisplayedItemRepository
    ): DisplayedItemUseCases {
        return DisplayedItemUseCases(
            getItemByIdUseCase = GetItemByIdUseCase(repository),
            getAllItemsUseCase = GetAllItemsUseCase(repository),
            getSpecificItemsUseCase = GetSpecificItemsUseCase(repository),
            insertItemUseCase = InsertItemUseCase(repository, context),
            deleteItemUseCase = DeleteItemUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideItemUseCases(
        repository: ItemRepository
    ): ItemUseCases {
        return ItemUseCases(
            deleteAllItemsUseCase = DeleteAllItemsUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDisplayedCategoryUseCases(repostiroy: DisplayedCategoryRepository): DisplayedCategoryUseCases {
        return DisplayedCategoryUseCases(
            getDisplayedCategoriesUseCase = com.kakeibo.feature_main.domain.use_cases.use_case_input.GetDisplayedCategoriesUseCase(repostiroy),
        )
    }

    @Provides
    @Singleton
    fun provideSearchUseCase(
        @ApplicationContext context: Context,
        searchRepository: SearchRepository
    ): SearchUseCases {
        return SearchUseCases(
            getAllSearchesUseCase = GetAllSearchesUseCase(searchRepository),
            getSearchByIDUseCase = GetSearchByIdUseCase(searchRepository),
            insertSearchUseCase = InsertSearchUseCase(searchRepository, context),
            deleteAllSearchesUseCase = DeleteAllSearchesUseCase(searchRepository),
            deleteSearchByIdUseCase = DeleteSearchByIdUseCase(searchRepository)
        )
    }

    @Provides
    @Singleton
    fun provideRearrangeCategoryUseCases(repository: CategoryRearrangeRepository): CategoryRearrangeUseCases {
        return CategoryRearrangeUseCases(
            getDisplayedCategoriesUseCase = GetDisplayedCategoriesUseCase(repository),
            getNonDisplayedCategoriesUseCase = GetNonDisplayedCategoriesUseCase(repository),
            updateDisplayedCategoriesUseCase = UpdateDisplayedCategoriesUseCase(repository)
        )
    }

    @Provides
    @Singleton
    fun provideCustomCategoryUseCases(
        @ApplicationContext context: Context,
        repository: CustomCategoryRepository
    ): CustomCategoryUseCases {
        return CustomCategoryUseCases(
            getAllCustomCategoriesUseCase = GetAllCustomCategoriesUseCase(repository),
            getCustomCategoryByIdUseCase = GetCustomCategoryByIdUseCase(repository),
            insertCustomCategoryUseCase = InsertCustomCategoryUseCase(repository, context),
            deleteCategoryUseCase = DeleteCustomCategoryUseCase(repository)
        )
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferencesImpl(context)
    }

//    @Singleton
//    @Provides
//    fun provideEncryptedSharedPreferences(
//        @ApplicationContext context: Context
//    ): SharedPreferences {
//
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build()
//
//        return EncryptedSharedPreferences.create(
//            context,
//            ENCRYPTED_SHARED_PREF_NAME,
//            masterKey,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }

}