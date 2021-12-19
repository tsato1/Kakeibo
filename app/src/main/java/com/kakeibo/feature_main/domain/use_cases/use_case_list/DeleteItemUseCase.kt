package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.feature_main.domain.models.DisplayedItem
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository

class DeleteItemUseCase(
    private val repository: DisplayedItemRepository
) {

    suspend operator fun invoke(displayedItem: DisplayedItem) {
        repository.deleteItem(displayedItem)
    }

}