package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import kotlinx.coroutines.flow.Flow

interface DisplayedItemRepository {

    fun getAllItems(): Flow<Resource<List<DisplayedItemModel>>>

    suspend fun getItemById(id: Long): DisplayedItemModel?

    fun getItemsByYear(y: String): Flow<Resource<List<DisplayedItemModel>>> // todo group by month, create model for this

    fun getItemsByYearMonth(ym: String): Flow<Resource<List<DisplayedItemModel>>>

    suspend fun insertItem(itemModel: DisplayedItemModel): Long

    suspend fun insertItems(itemModelList: List<DisplayedItemModel>)

    suspend fun deleteItemById(id: Long)

    suspend fun deleteAllItems()

}