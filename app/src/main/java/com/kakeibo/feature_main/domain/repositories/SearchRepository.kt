package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.feature_main.domain.models.SearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun getAllSearches(): Flow<List<SearchModel>>

    suspend fun getSearchById(id: Long): SearchModel

    suspend fun insertSearch(searchModel: SearchModel): Long

    suspend fun deleteAllSearches()

    suspend fun deleteSearchById(id: Long)

}