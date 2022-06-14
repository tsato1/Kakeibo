package com.kakeibo.core.domain.use_cases

import com.kakeibo.core.domain.use_cases.kkbapp.GetKkbAppUseCase
import com.kakeibo.core.domain.use_cases.kkbapp.InsertKkbAppUseCase

data class KkbAppUseCases(
    val getKkbAppUseCase: GetKkbAppUseCase,
    val insertKkbAppUseCase: InsertKkbAppUseCase
)