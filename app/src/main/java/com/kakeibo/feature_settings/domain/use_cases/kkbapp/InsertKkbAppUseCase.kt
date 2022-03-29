package com.kakeibo.feature_settings.domain.use_cases.kkbapp

import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.feature_settings.domain.repositories.KkbAppRepository

class InsertKkbAppUseCase(
    private val repository: KkbAppRepository
) {
    suspend operator fun invoke(kkbAppEntity: KkbAppEntity): Long {
        return repository.insertEntry(kkbAppEntity)
    }
}