package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItem
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import kotlinx.coroutines.flow.Flow

class GetItemListByYearUseCase(
    private val repository: DisplayedItemRepository
) {

    operator fun invoke(y: String): Flow<Resource<List<DisplayedItem>>> {
        return repository.getItemsByYear(y)
    }

}