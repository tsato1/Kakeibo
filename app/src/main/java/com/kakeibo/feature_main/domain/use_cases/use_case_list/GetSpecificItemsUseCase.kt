package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository

class GetSpecificItemsUseCase(
    private val repository: DisplayedItemRepository
) {

    suspend operator fun invoke(query: String, args: List<String>): List<DisplayedItemModel> {
        return repository.getSpecificItems(query, args)
    }

}