package com.kakeibo.feature_main.data.repositories

import com.kakeibo.core.data.local.SearchDao
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.feature_main.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(
    private val dao: SearchDao
) : SearchRepository {

    override fun getAllSearches(): Flow<List<SearchModel>> {
        TODO("Not yet implemented") // implement for paid app
    }

    override suspend fun getSearchById(id: Long): SearchModel {
        return dao.getSearchById(id).toSearchModel()
    }

    override suspend fun insertSearch(searchModel: SearchModel): Long {
        return dao.insertSearch(searchModel.toSearchEntity())
    }

    override suspend fun deleteAllSearches() {
        dao.deleteAllSearches()
    }

    override suspend fun deleteSearchById(id: Long) {
        dao.deleteSearchById(id)
    }
}