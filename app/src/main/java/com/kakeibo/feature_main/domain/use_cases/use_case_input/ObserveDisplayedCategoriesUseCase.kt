package com.kakeibo.feature_main.domain.use_cases.use_case_input

import com.kakeibo.feature_main.domain.models.DisplayedCategory
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository

class ObserveDisplayedCategoriesUseCase(
    private val displayedCategoryRepository: DisplayedCategoryRepository
) {

    suspend operator fun invoke(): List<DisplayedCategory> {
        return displayedCategoryRepository.observeDisplayedCategories()
    }

}