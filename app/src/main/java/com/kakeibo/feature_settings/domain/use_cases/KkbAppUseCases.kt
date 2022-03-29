package com.kakeibo.feature_settings.domain.use_cases

import com.kakeibo.feature_settings.domain.use_cases.kkbapp.GetKkbAppUseCase
import com.kakeibo.feature_settings.domain.use_cases.kkbapp.InsertKkbAppUseCase

data class KkbAppUseCases(
    val getKkbAppUseCase: GetKkbAppUseCase,
    val insertKkbAppUseCase: InsertKkbAppUseCase
)