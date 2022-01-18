package com.kakeibo.feature_main.domain.use_cases

import com.kakeibo.feature_main.domain.use_cases.use_case_input.InsertItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.*

data class ItemUseCases(
    val getItemByIdUseCase: GetItemByIdUseCase,
    val getAllItemsUseCase: GetAllItemsUseCase,
    val getSpecificItemsUseCase: GetSpecificItemsUseCase,
    val getItemListByYearUseCase: GetItemListByYearUseCase,
    val getItemListByYearMonthUseCase: GetItemListByYearMonthUseCase,
    val insertItemUseCase: InsertItemUseCase,
    val deleteItemUseCase: DeleteItemUseCase
)