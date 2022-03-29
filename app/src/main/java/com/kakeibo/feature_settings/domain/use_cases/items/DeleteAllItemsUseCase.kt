package com.kakeibo.feature_settings.domain.use_cases.items

import com.kakeibo.feature_settings.domain.repositories.ItemRepository

class DeleteAllItemsUseCase(
    private val repository: ItemRepository
) {

    suspend operator fun invoke(): Int {
        return repository.deleteAllItems()
    }

}