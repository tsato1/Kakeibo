package com.kakeibo.feature_settings.presentation.category_reorder

import com.kakeibo.feature_settings.domain.models.CategoryModel

data class CategoryReorderState(
    val displayedCategoryList: List<CategoryModel> = emptyList(),
    val isLoading: Boolean = false
)