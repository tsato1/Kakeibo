package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository

class DeleteItemUseCase(
    private val repository: DisplayedItemRepository
) {

    @Throws(ItemEntity.ItemNotFoundException::class)
    suspend operator fun invoke(displayedItemModel: DisplayedItemModel): Int {
        return displayedItemModel.id?.let {
            repository.deleteItemById(it)
        } ?: throw ItemEntity.ItemNotFoundException(
            "The item was not found in database. Deletion not executed."
        )
    }

}