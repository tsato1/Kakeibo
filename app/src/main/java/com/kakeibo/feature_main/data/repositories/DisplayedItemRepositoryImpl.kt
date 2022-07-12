package com.kakeibo.feature_main.data.repositories

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kakeibo.R
import com.kakeibo.core.data.local.ItemDao
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.data.local.entities.LocallyDeletedItemIdEntity
import com.kakeibo.core.data.remote.ItemApi
import com.kakeibo.core.data.remote.requests.DeleteItemRequest
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DisplayedItemRepositoryImpl(
    private val context: Context,
    private val dao: ItemDao,
    private val api: ItemApi
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

    override suspend fun insertItem(itemEntity: ItemEntity): Long {
        return dao.insertItem(itemEntity)
    }

    override suspend fun insertItems(itemEntityList: List<ItemEntity>) {
        itemEntityList.forEach {
            insertItem(it)
        }
    }

    override suspend fun deleteItemById(id: Long): Int {
        itemDataSource.deleteItemById(id)

        val response = try {
            api.deleteItem(DeleteItemRequest(id))
        }
        catch (e: Exception) {
            null
        }

        val affectedRows = dao.deleteItemById(id)

        if (response == null || !response.isSuccessful) {
            dao.insertLocallyDeletedItemId(LocallyDeletedItemIdEntity(id))
        }
        else {
            deleteLocallyDeletedItemId(id)
        }

        return affectedRows
    }

    override suspend fun deleteAllItems() {
        dao.deleteAllItems()
        itemDataSource.deleteAllItems()
    }

    private var currentItemsResponse: Response<List<ItemEntity>>? = null
    override suspend fun syncItems() {
        dao.getAllLocallyDeletedItemIds().onEach { locallyDeletedItemIdEntity -> // sync with server
            deleteItemById(locallyDeletedItemIdEntity.deletedItemId)
        }

        dao.getAllUnsyncedItems().onEach { itemEntity ->
            insertItem(itemEntity)
        }

        currentItemsResponse = api.getItems()
        currentItemsResponse?.body()?.let { list ->
            dao.deleteAllItems()
            insertItems(
                list.onEach {
                    it.isSynced = true
                }
            )
        }
    }

    suspend fun deleteLocallyDeletedItemId(Id: Long) {
        dao.deleteLocallyDeletedItemId(Id)
    }

}