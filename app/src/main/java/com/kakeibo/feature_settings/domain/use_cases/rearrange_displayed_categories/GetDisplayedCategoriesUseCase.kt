package com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository
import kotlinx.coroutines.flow.Flow

class GetDisplayedCategoriesUseCase(
    private val repository: CategoryRearrangeRepository
) {

    operator fun invoke(): Flow<Resource<List<CategoryModel>>> {
        return repository.getDisplayedCategories()
    }

}