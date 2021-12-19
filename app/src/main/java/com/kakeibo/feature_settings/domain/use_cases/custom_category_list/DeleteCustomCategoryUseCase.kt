package com.kakeibo.feature_settings.domain.use_cases.custom_category_list

import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository

class DeleteCustomCategoryUseCase(
    private val repository: CustomCategoryRepository
) {
    suspend operator fun invoke(categoryModel: CategoryModel) {
        repository.deleteCustomCategory(categoryModel)
    }
}