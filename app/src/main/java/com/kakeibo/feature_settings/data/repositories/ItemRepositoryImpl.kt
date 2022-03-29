package com.kakeibo.feature_settings.data.repositories

import com.kakeibo.core.data.local.ItemDao
import com.kakeibo.feature_settings.domain.repositories.ItemRepository

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {

    override suspend fun deleteAllItems(): Int {
        return dao.deleteAllItems()
    }

}