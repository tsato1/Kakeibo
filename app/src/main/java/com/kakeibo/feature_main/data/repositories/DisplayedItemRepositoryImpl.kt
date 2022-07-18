package com.kakeibo.feature_main.data.repositories

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kakeibo.R
import com.kakeibo.core.data.local.ItemDao
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class DisplayedItemRepositoryImpl(
    context: Context,
    private val dao: ItemDao
) : DisplayedItemRepository {

    private val defaultCategories = context.resources.getStringArray(R.array.default_category)

    override fun getAllItems(): Flow<Resource<List<DisplayedItemModel>>> = flow {
        dao.getAllItems() // todo
    }

    override suspend fun getItemById(id: Long): DisplayedItemModel? {
        return dao.getItemById(id)?.toDisplayedItemModel().also { displayedItemModel ->
            displayedItemModel?.let {
                if (it.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                    it.categoryName = defaultCategories[it.categoryCode]
                }
            }
        }
    }

//    override fun getItemsByYear(y: String): Flow<Resource<List<DisplayedItemModel>>> = flow {
//        emit(Resource.Loading())
//
//        val displayedItems = dao.getItemsInYear(y)
//            .map {
//                it.map {
//                    it.toDisplayedItemModel()
//                }
//            }
//            .first()
//
//        emit(Resource.Loading(displayedItems))
//
//        try {
//
//        } catch (e: HttpException) {
//            emit(Resource.Error(e.message ?: "HttpException", data = displayedItems))
//        } catch (e: IOException) {
//            emit(Resource.Error(e.message ?: "Couldn't reach server", data = displayedItems))
//        }
//
//        val flow = dao.getItemsInYear(y)
//            .map {
//                it.map {
//                    it.toDisplayedItemModel()
//                }
//            }
//            .map {
//                Resource.Success(it)
//            }
//
//        emitAll(flow)
//    }
//
//    override fun getItemsByYearMonth(ym: String): Flow<Resource<List<DisplayedItemModel>>> = flow {
//        emit(Resource.Loading())
//
//        val displayedItems = dao.getItemsInMonth(ym)
//            .map {
//                it.map {
//                    it.toDisplayedItemModel()
//                }
//            }
//            .first()
//
//        try {
//
//        } catch (e: HttpException) {
//            emit(Resource.Error(e.message ?: "HttpException", data = displayedItems))
//        } catch (e: IOException) {
//            emit(Resource.Error(e.message ?: "Couldn't reach server", data = displayedItems))
//        }
//
//        val flow = dao.getItemsInMonth(ym)
//            .map {
//                it.map {
//                    it.toDisplayedItemModel()
//                }
//            }
//            .map {
//                Resource.Success(it)
//            }
//
//        emitAll(flow)
//    }

    override fun getSpecificItems(query: String, args: List<String>): Flow<Resource<List<DisplayedItemModel>>> = flow {
        emit(Resource.Loading())

        val displayedItems = dao.getSpecificItems(SimpleSQLiteQuery(query, args.toTypedArray()))
            .map { list ->
                list.map { displayedItemEntity ->
                    displayedItemEntity.toDisplayedItemModel().also { displayedItemModel ->
                        if (displayedItemModel.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                            displayedItemModel.categoryName =
                                defaultCategories[displayedItemModel.categoryCode]
                        }
                    }
                }
            }
            .first()

        try {

        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "HttpException", data = displayedItems))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Couldn't reach server", data = displayedItems))
        }

        val flow = dao.getSpecificItems(SimpleSQLiteQuery(query, args.toTypedArray()))
            .map { list ->
                list.map { displayedItemEntity ->
                    displayedItemEntity.toDisplayedItemModel().also { displayedItemModel ->
                        if (displayedItemModel.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                            displayedItemModel.categoryName =
                                defaultCategories[displayedItemModel.categoryCode]
                        }
                    }
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

    override suspend fun insertItem(displayedItemModel: DisplayedItemModel): Long {
        return dao.insertItem(displayedItemModel.toItemEntity())
    }

    override suspend fun insertItems(displayedItemModelList: List<DisplayedItemModel>) {
        displayedItemModelList.forEach {
            insertItem(it)
        }
    }

    override suspend fun deleteItemById(id: Long): Int {
        return dao.deleteItemById(id)
//        itemDataSource.deleteItemById(id)
        // todo : work on this once api is implemented
//        val response = try {
//            itemApi.deleteItem(DeleteItemRequest(id))
//        }
//        catch (e: Exception) {
//            null
//        }
//
//        itemDao.deleteItemById(id)
//
//        if (response == null || !response.isSuccessful) {
//            itemDao.insertLocallyDeletedItemId(LocallyDeletedItemIdEntity(id))
//        }
//        else {
//            deleteLocallyDeletedItemId(id)
//        }
    }

//    suspend fun deleteLocallyDeletedItemId(Id: Long) {
//        itemDao.deleteLocallyDeletedItemId(Id)
//    }

    override suspend fun deleteAllItems() {
        dao.deleteAllItems()
//        itemDataSource.deleteAllItems()
    }

}