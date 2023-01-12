package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository

class DeleteItemUseCase(
    private val displayedItemRepository: DisplayedItemRepository
) {

    @Throws(ItemEntity.ItemNotFoundException::class)
    suspend operator fun invoke(displayedItemModel: DisplayedItemModel, syncWithRemote: Int): Int {

        return displayedItemModel.uuid?.let {
            displayedItemRepository.deleteItemById(it, syncWithRemote)
        } ?: throw ItemEntity.ItemNotFoundException(
            "The item was not found in database. Deletion not executed."
        )
    }

}