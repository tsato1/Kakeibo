package com.kakeibo.feature_settings.domain.repositories

import com.kakeibo.core.data.local.entities.KkbAppEntity
import kotlinx.coroutines.flow.Flow

interface KkbAppRepository {

    fun getFirstEntry(): Flow<KkbAppEntity>

    suspend fun insertEntry(kkbAppEntity: KkbAppEntity): Long

}