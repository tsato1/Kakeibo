package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import kotlinx.coroutines.flow.Flow

interface DisplayedItemRepository {

    fun getAllItems(): Flow<Resource<List<DisplayedItemModel>>>

    suspend fun getItemById(id: Long): DisplayedItemModel?

    fun getItemsByYear(y: String): Flow<Resource<List<DisplayedItemModel>>>

    fun getItemsByYearMonth(ym: String): Flow<Resource<List<DisplayedItemModel>>>

    fun getSpecificItems(query: String, args: List<String>): Flow<Resource<List<DisplayedItemModel>>>

    suspend fun insertItem(displayedItemModel: DisplayedItemModel): Long

    suspend fun insertItems(displayedItemModelList: List<DisplayedItemModel>)

    suspend fun deleteItemById(id: Long)

    suspend fun deleteAllItems()

}