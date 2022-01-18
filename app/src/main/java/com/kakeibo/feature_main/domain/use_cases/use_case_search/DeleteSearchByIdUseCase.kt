package com.kakeibo.feature_main.domain.use_cases.use_case_search

import com.kakeibo.feature_main.domain.repositories.SearchRepository

class DeleteSearchByIdUseCase(
    private val repository: SearchRepository
) {

    suspend operator fun invoke(id: Long) {
        repository.deleteSearchById(id)
    }

}