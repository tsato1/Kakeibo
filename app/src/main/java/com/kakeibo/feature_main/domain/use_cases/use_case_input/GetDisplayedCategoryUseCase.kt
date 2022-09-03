package com.kakeibo.feature_main.domain.use_cases.use_case_input

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import kotlinx.coroutines.flow.Flow

class GetDisplayedCategoryUseCase(
    private val displayedCategoryRepository: DisplayedCategoryRepository
) {

    operator fun invoke(): Flow<Resource<List<DisplayedCategoryModel>>> {
        return displayedCategoryRepository.getAllDisplayedCategories()
    }

}