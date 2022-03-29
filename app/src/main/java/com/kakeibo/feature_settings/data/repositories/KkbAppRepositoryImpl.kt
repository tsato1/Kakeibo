package com.kakeibo.feature_settings.data.repositories

import com.kakeibo.core.data.local.KkbAppDao
import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.feature_settings.domain.repositories.KkbAppRepository
import kotlinx.coroutines.flow.Flow

class KkbAppRepositoryImpl(
    private val dao: KkbAppDao
) : KkbAppRepository {

    override fun getFirstEntry(): Flow<KkbAppEntity> {
        return dao.getFirst()
    }

    override suspend fun insertEntry(kkbAppEntity: KkbAppEntity): Long {
        return dao.insert(kkbAppEntity)
    }

}