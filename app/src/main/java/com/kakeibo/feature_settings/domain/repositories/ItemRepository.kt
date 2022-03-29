package com.kakeibo.feature_settings.domain.repositories

interface ItemRepository {

    suspend fun deleteAllItems(): Int

}