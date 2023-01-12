package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import kotlinx.coroutines.flow.Flow

interface DisplayedItemRepository {

    fun getAllItems(): Flow<Resource<List<DisplayedItemModel>>>

    suspend fun getItemById(id: Long): DisplayedItemModel?

    fun getSpecificItems(query: String, args: List<String>, syncWithRemote: Int): Flow<Resource<List<DisplayedItemModel>>>

    suspend fun insertItem(itemEntity: ItemEntity, syncWithRemote: Int): Long

    suspend fun insertItems(itemEntityList: List<ItemEntity>, syncWithRemote: Int)

    suspend fun deleteItemById(id: String, syncWithRemote: Int): Int

    suspend fun syncItems(syncWithRemote: Int)

    suspend fun deleteLocallyDeletedItemId(id: String)

}