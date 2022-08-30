package com.kakeibo.feature_main.data.repositories

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : DisplayedItemRepository {

    private val items = mutableListOf<DisplayedItemModel>()

    override fun getAllItems(): Flow<Resource<List<DisplayedItemModel>>> {
        return flow { emit(Resource.Success(items)) }
    }

    override suspend fun getItemById(id: Long): DisplayedItemModel? {
        return items.find { it.id == id }
    }

    override fun getSpecificItems(
        query: String,
        args: List<String>,
        syncWithRemote: Int
    ): Flow<Resource<List<DisplayedItemModel>>> {
        return flow { emit(Resource.Success(items)) }
    }

    override suspend fun insertItem(itemEntity: ItemEntity, syncWithRemote: Int): Long {
//        items.add(displayedItemModel)
        return (items.size - 1).toLong()
    }

    override suspend fun insertItems(itemEntityList: List<ItemEntity>, syncWithRemote: Int) {
//        items.addAll(displayedItemModelList)
    }

    override suspend fun deleteItemById(id: Long, syncWithRemote: Int): Int {
        items.removeAt(id.toInt())
        return 1
    }

    override suspend fun syncItems(syncWithRemote: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocallyDeletedItemId(id: Long) {
        TODO("Not yet implemented")
    }
}