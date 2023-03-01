package com.kakeibo.feature_main.data.repositories

import android.app.Application
import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kakeibo.R
import com.kakeibo.core.data.local.ItemDao
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.data.local.entities.LocallyDeletedItemIdEntity
import com.kakeibo.core.data.remote.ItemApi
import com.kakeibo.core.data.remote.requests.DeleteItemRequest
import com.kakeibo.core.util.NetworkWatcher
import com.kakeibo.core.util.Resource
import com.kakeibo.core.util.networkBoundResource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Response

class DisplayedItemRepositoryImpl(
    private val context: Context,
    private val dao: ItemDao,
    private val api: ItemApi
) : DisplayedItemRepository {

    private val defaultCategories = context.resources.getStringArray(R.array.default_category)

    override fun getAllItems(): Flow<Resource<List<DisplayedItemModel>>> = flow {
        dao.getAllItems() // todo
    }

    override suspend fun getItemById(id: Long): DisplayedItemModel? = withContext(Dispatchers.IO) {
        dao.getItemById(id)?.toDisplayedItemModel().also { displayedItemModel ->
            displayedItemModel?.let {
                if (it.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                    it.categoryName = defaultCategories[it.categoryCode]
                }
            }
        }
    }

    override fun getSpecificItems(
        query: String, args: List<String>, syncWithRemote: Int
    ): Flow<Resource<List<DisplayedItemModel>>> {
        return networkBoundResource(
            query = {
                dao.getSpecificItems(SimpleSQLiteQuery(query, args.toTypedArray()))
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
            },
            fetch = {
                syncItems(syncWithRemote)
                currentItemsResponse
            },
            saveFetchedResult = { response -> // inserts the notes in the response into database
                response?.body()?.let {
                    // insert items in database
                    insertItems(
                        it.onEach { item ->
                            item.isSynced = true
                        },
                        syncWithRemote
                    )
                }
            },
            shouldFetch = {
                NetworkWatcher.getInstance(context.applicationContext as Application).isOnline
            }
        )
    }

    override suspend fun insertItem(itemEntity: ItemEntity, syncWithRemote: Int): Long =
        withContext(Dispatchers.IO) {
            val response = if (syncWithRemote == 1) {
                try {
                    api.addItem(itemEntity)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            if (response != null && response.isSuccessful) {
                dao.insertItem(itemEntity.apply { isSynced = true })
            } else {
                dao.insertItem(itemEntity) // meaning isSynced is false
            }
        }

    override suspend fun insertItems(itemEntityList: List<ItemEntity>, syncWithRemote: Int) =
        withContext(Dispatchers.IO) {
            itemEntityList.forEach {
                insertItem(it, syncWithRemote)
            }
        }

    override suspend fun deleteItem(id: Long, uuid: String, syncWithRemote: Int): Int =
        withContext(Dispatchers.IO) {
//            val response = if (syncWithRemote == 1) {
//                try {
//                    api.deleteItem(DeleteItemRequest(uuid))
//                } catch (e: Exception) {
//                    null
//                }
//            } else {
//                null
//            }

            val affectedRows = dao.deleteItemById(id)

//            if (response == null || !response.isSuccessful) {
//                item.id?.let {
//                    dao.insertLocallyDeletedItemId(
//                        LocallyDeletedItemIdEntity(
//                            deletedItemUUID = item.uuid,
//                            deletedItemId = it
//                        )
//                    )
//                }
//            } else {
//                deleteLocallyDeletedItemId(item.uuid)
//            }

            affectedRows
        }

    private var currentItemsResponse: Response<List<ItemEntity>>? = null
    override suspend fun syncItems(syncWithRemote: Int): Unit = withContext(Dispatchers.IO) {
        dao.getAllLocallyDeletedItemIds().onEach { locallyDeletedItemIdEntity -> // sync with server
            deleteItem(
                id = locallyDeletedItemIdEntity.deletedItemId,
                uuid = locallyDeletedItemIdEntity.deletedItemUUID,
                syncWithRemote
            )
        }

        dao.getAllUnsyncedItems().onEach { itemEntity ->
            insertItem(itemEntity, syncWithRemote)
        }

        currentItemsResponse = api.getItems()
        currentItemsResponse?.body()?.let { list ->
            dao.deleteAllItems()
            insertItems(
                list.onEach {
                    it.isSynced = true
                },
                syncWithRemote
            )
        }
    }

    override suspend fun deleteLocallyDeletedItemId(uuid: String) = withContext(Dispatchers.IO) {
        dao.deleteLocallyDeletedItemId(uuid)
    }

}