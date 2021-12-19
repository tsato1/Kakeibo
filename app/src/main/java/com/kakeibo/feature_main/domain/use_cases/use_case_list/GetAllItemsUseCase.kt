package com.kakeibo.feature_main.domain.use_cases.use_case_list

import com.kakeibo.feature_main.domain.repositories.DisplayedItemRepository

class GetAllItemsUseCase(
    private val repository: DisplayedItemRepository
) {

//    operator fun invoke(): Flow<List<ItemEntity>> {
//        return repository.getItems()
//    }

}