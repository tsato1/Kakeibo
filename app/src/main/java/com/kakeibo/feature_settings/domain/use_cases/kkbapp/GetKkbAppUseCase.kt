package com.kakeibo.feature_settings.domain.use_cases.kkbapp

import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.feature_settings.domain.repositories.KkbAppRepository
import kotlinx.coroutines.flow.Flow

class GetKkbAppUseCase(
    private val repository: KkbAppRepository
) {
    operator fun invoke(): Flow<KkbAppEntity> {
        return repository.getFirstEntry()
    }
}