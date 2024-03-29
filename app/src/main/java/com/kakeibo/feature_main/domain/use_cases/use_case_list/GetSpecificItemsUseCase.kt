package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import kotlinx.coroutines.flow.Flow

class GetSpecificItemsUseCase(
    private val displayedItemRepository: DisplayedItemRepository
) {

    operator fun invoke(
        query: String, args: List<String>, syncWithRemote: Int
    ): Flow<Resource<List<DisplayedItemModel>>> {
        return displayedItemRepository.getSpecificItems(query, args, syncWithRemote)
    }

}