package com.kakeibo.feature_main.data.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.data.sources.local.ItemDao
import com.kakeibo.feature_main.domain.models.DisplayedItem
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class DisplayedItemRepositoryImpl(
    private val itemDao: ItemDao
) : DisplayedItemRepository {

    private var currentItemsResponse: Response<List<DisplayedItem>>? = null

//    suspend fun syncItems() {
//        val locallyDeletedItemIds = itemDao.getAllLocallyDeletedItemIds()
//        locallyDeletedItemIds.forEach { locallyDeletedItemId -> // sync with server
//            deleteItemById(locallyDeletedItemId.deletedItemId)
//        }
//
//        val unsyncedItems = itemDao.getAllUnsyncedItemEntities()
//        unsyncedItems.forEach { unsyncedItem -> // sync with server
//            insertItem(unsyncedItem)
//        }
//
//        currentItemsResponse = noteApi.getItems() // get the current version of items from the server
//        currentItemsResponse?.body()?.let { items ->
//            // update the local database
//            itemDao.deleteAllItems()
//            insertItems(items.onEach { item ->
//                item.isSynced = true
//            })
//        }
//    }

    /* should be fetching data only from api into db */
//    override fun getItems(): Flow<Resource<List<DisplayedItem>>> {
//        return networkBoundResource(
//            query = itemDao.getAllItems(),
//            fetch = {
//                syncItems(),
//                currentItemsResponse
//            },
//            saveFetchedResult = { response ->
//                response?.body()?.let {
//                    insertItems(it.onEach { item ->
//                        item.isSynced = true
//                    })
//                }
//            },
//            shouldFetch = {
//                checkForInternetConnection(context = context)
//            }
//        )
//    }

    override suspend fun getItemById(id: Long): DisplayedItem? {
         return itemDao.getItemById(id)?.toDisplayedItem()
    }

    override fun getItemsByYear(y: String): Flow<Resource<List<DisplayedItem>>> = flow {
        emit(Resource.Loading())
        val displayedItem = itemDao.getItemsByYear(y).map { it.toDisplayedItem() }
        emit(Resource.Success(displayedItem))
    }

    override fun getItemsByYearMonth(ym: String): Flow<Resource<List<DisplayedItem>>> = flow {
        emit(Resource.Loading())
        val displayedItem = itemDao.getItemsByYearMonth(ym).map { it.toDisplayedItem() }
        emit(Resource.Success(displayedItem))
    }




    override suspend fun insertItem(displayedItem: DisplayedItem): Long {
        return itemDao.insertItem(displayedItem.toItemEntity())
    }

    override suspend fun insertItems(displayedItemList: List<DisplayedItem>) {
        itemDao.insertItems(displayedItemList.map { it.toItemEntity() })
    }




    override suspend fun deleteItem(displayedItem: DisplayedItem) {
        itemDao.deleteItem(displayedItem.toItemEntity())
    }

    override suspend fun deleteItemById(id: Long) {
        itemDao.deleteItemById(id)
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

    override suspend fun deleteAll() {
        itemDao.deleteAllItems()
    }

}