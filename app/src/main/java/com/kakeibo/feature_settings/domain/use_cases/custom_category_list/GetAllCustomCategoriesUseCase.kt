package com.kakeibo.feature_settings.domain.use_cases.custom_category_list

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllCustomCategoriesUseCase(
    private val repository: CustomCategoryRepository
) {
    operator fun invoke(): Flow<Resource<List<CategoryModel>>> {
        return repository.getAllCustomCategories()
    }
}