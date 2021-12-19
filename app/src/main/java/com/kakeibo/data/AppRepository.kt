//package com.kakeibo.data
//
//import android.app.Application
//import androidx.lifecycle.LiveData
//import androidx.paging.PagingSource
//import com.kakeibo.feature_settings.settings_category.data.sources.local.CategoryDao
//import com.kakeibo.feature_settings.settings_category.data.sources.local.CategoryDspDao
//import com.kakeibo.feature_item.data.sources.local.ItemDao
//import retrofit2.Response
//import javax.inject.Inject
//
//class AppRepository @Inject constructor(
//    private val itemDao: ItemDao,
//    private val categoryDao: CategoryDao,
//    private val categoryDspDao: CategoryDspDao,
//    private val context: Application
//) {
//
//    private var currentNotesResponse: Response<List<Item>>? = null
//
//    /* items */
//    fun getAllItems(): PagingSource<Int, Item> {
//        return itemDao.getAllItems()
//    }
//
//    fun getItemsByYear(year: String): LiveData<List<Item>> {
//        return itemDao.getItemsByYear(year)
//    }
//
//    fun getItemsByYearMonth(ym: String): LiveData<List<Item>> {
//        return itemDao.getItemsByYearMonth(ym)
//    }
//
//    suspend fun insertItem(item: Item) {
//        itemDao.insert(item)
//    }
//
//    suspend fun insertItems(items: List<Item>) {
//        itemDao.insertAll(items)
//    }
//
//    suspend fun deleteItem(id: Long) {
//        itemDao.delete(id)
//    }
//
//    suspend fun deleteAllItems() {
//        itemDao.deleteAll()
//    }
//
//    /* categories */
//    fun getAllCategories(): LiveData<List<Category>> {
//        return categoryDao.getAll()
//    }
//
//    fun getAllCategoriesDisplayed(): LiveData<List<Category>> {
//        return categoryDao.getCategoriesDisplayed()
//    }
//
//    fun getAllCategoryNotDisplayed(): LiveData<List<Category>> {
//        return categoryDao.getCategoriesNotDisplayed()
//    }
//
//    suspend fun insertCateogry(category: Category) {
//        categoryDao.insert(category)
//    }
//
//    suspend fun insertCategories(categories: List<Category>) {
//        categoryDao.insertAll(categories)
//    }
//
//    suspend fun deleteCategory(id: Long) {
//        categoryDao.delete(id)
//    }
//
//    suspend fun deleteAllCategories() {
//        categoryDao.deleteAll()
//    }
//
//    /* categoryDsps */
//    fun getAllCategoryDsps(): LiveData<List<CategoryDsp>> {
//        return categoryDspDao.getAll()
//    }
//
//    suspend fun insertCategoryDsps(categoryDsps: List<CategoryDsp>) {
//        categoryDspDao.insertAll(categoryDsps)
//    }
//
//    suspend fun deleteAllCategoryDsps() {
//        categoryDspDao.deleteAll()
//    }
//
//}