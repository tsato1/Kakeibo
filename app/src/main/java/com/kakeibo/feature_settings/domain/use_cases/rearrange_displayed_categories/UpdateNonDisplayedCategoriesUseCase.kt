package com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories

import com.kakeibo.feature_main.domain.models.DisplayedCategory
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository

class UpdateNonDisplayedCategoriesUseCase(
    private val repository: CategoryRearrangeRepository
) {
    suspend operator fun invoke(list: List<DisplayedCategory>) {
        repository.updateNonDisplayedCategories(list)
    }
}