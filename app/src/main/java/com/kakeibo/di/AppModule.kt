package com.kakeibo.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kakeibo.Constants.ITEM_BASE_URL
import com.kakeibo.core.data.constants.PrepDB
import com.kakeibo.core.data.local.AppDatabase
import com.kakeibo.core.data.local.CategoryDao
import com.kakeibo.core.data.local.CategoryDspDao
import com.kakeibo.core.data.local.KkbAppDao
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.data.preferences.AppPreferencesImpl
import com.kakeibo.core.data.remote.AuthApi
import com.kakeibo.core.data.remote.AuthApiImpl
import com.kakeibo.core.data.remote.BasicAuthInterceptor
import com.kakeibo.core.data.remote.ItemApi
import com.kakeibo.core.data.repositories.KkbAppRepositoryImpl
import com.kakeibo.core.domain.repositories.KkbAppRepository
import com.kakeibo.core.domain.use_cases.KkbAppUseCases
import com.kakeibo.core.domain.use_cases.kkbapp.GetKkbAppUseCase
import com.kakeibo.core.domain.use_cases.kkbapp.InsertKkbAppUseCase
import com.kakeibo.feature_main.data.repositories.DisplayedCategoryRepositoryImpl
import com.kakeibo.feature_main.data.repositories.DisplayedItemRepositoryImpl
import com.kakeibo.feature_main.data.repositories.SearchRepositoryImpl
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import com.kakeibo.feature_main.domain.repositories.SearchRepository
import com.kakeibo.feature_main.domain.use_cases.DisplayedCategoryUseCases
import com.kakeibo.feature_main.domain.use_cases.DisplayedItemUseCases
import com.kakeibo.feature_main.domain.use_cases.SearchUseCases
import com.kakeibo.feature_main.domain.use_cases.use_case_input.InsertItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.DeleteItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetAllItemsUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetItemByIdUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetSpecificItemsUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_search.*
import com.kakeibo.feature_settings.data.repositories.CategoryRearrangeRepositoryImpl
import com.kakeibo.feature_settings.data.repositories.CustomCategoryRepositoryImpl
import com.kakeibo.feature_settings.data.repositories.ItemRepositoryImpl
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository
import com.kakeibo.feature_settings.domain.repositories.ItemRepository
import com.kakeibo.feature_settings.domain.use_cases.CategoryRearrangeUseCases
import com.kakeibo.feature_settings.domain.use_cases.CustomCategoryUseCases
import com.kakeibo.feature_settings.domain.use_cases.ItemUseCases
import com.kakeibo.feature_settings.domain.use_cases.custom_category_detail.GetCustomCategoryByIdUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_detail.InsertCustomCategoryUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_list.DeleteCustomCategoryUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_list.GetAllCustomCategoriesUseCase
import com.kakeibo.feature_settings.domain.use_cases.items.DeleteAllItemsUseCase
import com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories.GetDisplayedCategoriesUseCase
import com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories.GetNonDisplayedCategoriesUseCase
import com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories.UpdateDisplayedCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthApi(client: HttpClient): AuthApi = AuthApiImpl(client)

    @Singleton
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @Singleton
    @Provides
    fun provideItemApi(basicAuthInterceptor: BasicAuthInterceptor): ItemApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(ITEM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ItemApi::class.java)
    }

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
                AppDatabase.MIGRATION_8_9,
                AppDatabase.MIGRATION_9_10
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
    Repositories
     */
    @Singleton
    @Provides
    fun provideKkbAppRepository(db: AppDatabase): KkbAppRepository {
        return KkbAppRepositoryImpl(db.kkbAppDao)
    }

    @Provides
    @Singleton
    fun provideDisplayedItemRepository(
        @ApplicationContext context: Context,
        db: AppDatabase,
        itemApi: ItemApi
    ): DisplayedItemRepository {
        return DisplayedItemRepositoryImpl(context, db.itemDao, itemApi)
    }

    @Provides
    @Singleton
    fun provideItemRepository(db: AppDatabase): ItemRepository {
        return ItemRepositoryImpl(db.itemDao)
    }

    @Provides
    @Singleton
    fun provideDisplayedCategoryRepository(
        @ApplicationContext context: Context,
        db: AppDatabase
    ): DisplayedCategoryRepository {
        return DisplayedCategoryRepositoryImpl(context, db.categoryDao)
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

    /*
    Usecases
     */
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
        itemRepository: DisplayedItemRepository,
        searchRepository: SearchRepository
    ): DisplayedItemUseCases {
        return DisplayedItemUseCases(
            getItemByIdUseCase = GetItemByIdUseCase(itemRepository),
            getAllItemsUseCase = GetAllItemsUseCase(itemRepository),
            getSpecificItemsUseCase = GetSpecificItemsUseCase(itemRepository),
            insertItemUseCase = InsertItemUseCase(itemRepository, context),
            insertSearchUseCase = InsertSearchUseCase(searchRepository, context),
            deleteItemUseCase = DeleteItemUseCase(itemRepository)
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
            getDisplayedCategoryUseCase = com.kakeibo.feature_main.domain.use_cases.use_case_input.GetDisplayedCategoryUseCase(repostiroy),
        )
    }

    @Provides
    @Singleton
    fun provideSearchUseCases(
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