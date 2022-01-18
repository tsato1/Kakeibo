package com.kakeibo.feature_main.domain.use_cases

import com.kakeibo.feature_main.domain.use_cases.use_case_search.*

data class SearchUseCases(
    val getAllSearchesUseCase: GetAllSearchesUseCase,
    val getSearchByIDUseCase: GetSearchByIdUseCase,
    val insertSearchUseCase: InsertSearchUseCase,
    val deleteAllSearchesUseCase: DeleteAllSearchesUseCase,
    val deleteSearchByIdUseCase: DeleteSearchByIdUseCase
)