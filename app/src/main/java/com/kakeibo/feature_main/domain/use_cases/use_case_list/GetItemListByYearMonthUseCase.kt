package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItem
import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository
import kotlinx.coroutines.flow.Flow

class GetItemListByYearMonthUseCase(
    private val repository: DisplayedItemRepository
) {

    operator fun invoke(ym: String): Flow<Resource<List<DisplayedItem>>> {
        return repository.getItemsByYearMonth(ym)
    }

}