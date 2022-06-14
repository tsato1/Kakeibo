package com.kakeibo.feature_main.data.repositories

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
        args: List<String>
    ): Flow<Resource<List<DisplayedItemModel>>> {
        return flow { emit(Resource.Success(items)) }
    }

    override suspend fun insertItem(displayedItemModel: DisplayedItemModel): Long {
        items.add(displayedItemModel)
        return (items.size - 1).toLong()
    }

    override suspend fun insertItems(displayedItemModelList: List<DisplayedItemModel>) {
        items.addAll(displayedItemModelList)
    }

    override suspend fun deleteItemById(id: Long): Int {
        items.removeAt(id.toInt())
        return 1
    }

    override suspend fun deleteAllItems() {
        items.clear()
    }
}