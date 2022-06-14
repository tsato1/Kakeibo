package com.kakeibo.core.domain.use_cases.kkbapp

import com.kakeibo.core.domain.models.KkbAppModel
import com.kakeibo.core.domain.repositories.KkbAppRepository

class GetKkbAppUseCase(
    private val repository: KkbAppRepository
) {
    suspend operator fun invoke(): KkbAppModel? {
        return repository.getFirstEntry()
    }
}