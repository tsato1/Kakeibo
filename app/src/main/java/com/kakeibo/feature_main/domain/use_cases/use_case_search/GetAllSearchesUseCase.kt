package com.kakeibo.feature_main.domain.use_cases.use_case_search

import com.kakeibo.feature_main.domain.repositories.SearchRepository

class GetAllSearchesUseCase(
    private val repository: SearchRepository
) {

    operator fun invoke() {

    }

}