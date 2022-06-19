package com.kakeibo.feature_main.domain.use_cases

import com.kakeibo.feature_main.domain.use_cases.use_case_input.InsertItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.*
import com.kakeibo.feature_main.domain.use_cases.use_case_search.InsertSearchUseCase

data class DisplayedItemUseCases(
    val getItemByIdUseCase: GetItemByIdUseCase,
    val getAllItemsUseCase: GetAllItemsUseCase,
    val getSpecificItemsUseCase: GetSpecificItemsUseCase,
    val insertItemUseCase: InsertItemUseCase,
    val insertSearchUseCase: InsertSearchUseCase,
    val deleteItemUseCase: DeleteItemUseCase
)