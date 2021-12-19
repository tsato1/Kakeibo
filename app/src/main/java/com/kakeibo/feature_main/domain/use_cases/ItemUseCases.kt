package com.kakeibo.feature_main.domain.use_cases

import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetItemByIdUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_input.InsertItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.DeleteItemUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetItemListByYearMonthUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetItemListByYearUseCase
import com.kakeibo.feature_main.domain.use_cases.use_case_list.GetAllItemsUseCase

data class ItemUseCases(
    val getItemByIdUseCase: GetItemByIdUseCase,
    val getAllItemsUseCase: GetAllItemsUseCase,
    val getItemListByYearUseCase: GetItemListByYearUseCase,
    val getItemListByYearMonthUseCase: GetItemListByYearMonthUseCase,
    val insertItemUseCase: InsertItemUseCase,
    val deleteItemUseCase: DeleteItemUseCase
)