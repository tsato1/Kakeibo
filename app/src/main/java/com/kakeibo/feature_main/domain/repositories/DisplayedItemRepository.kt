package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItem
import kotlinx.coroutines.flow.Flow

interface DisplayedItemRepository {

//    fun getItems(): Flow<Resource<List<DisplayedItem>>>



    suspend fun getItemById(id: Long): DisplayedItem?



    fun getItemsByYear(y: String): Flow<Resource<List<DisplayedItem>>> // todo group by month, create model for this

    fun getItemsByYearMonth(ym: String): Flow<Resource<List<DisplayedItem>>>




    suspend fun insertItem(displayedItem: DisplayedItem): Long

    suspend fun insertItems(displayedItemList: List<DisplayedItem>)



    suspend fun deleteItem(displayedItem: DisplayedItem)

    suspend fun deleteItemById(id: Long)

    suspend fun deleteAll()

}