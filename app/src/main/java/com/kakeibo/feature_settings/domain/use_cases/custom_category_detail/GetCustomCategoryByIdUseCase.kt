package com.kakeibo.feature_settings.domain.use_cases.custom_category_detail

import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository

class GetCustomCategoryByIdUseCase(
    private val repository: CustomCategoryRepository
) {

    suspend operator fun invoke(id: Long): CategoryModel {
        return repository.getCustomCategoryById(id)
    }

}