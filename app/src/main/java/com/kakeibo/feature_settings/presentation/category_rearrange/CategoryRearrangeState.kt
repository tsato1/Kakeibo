package com.kakeibo.feature_settings.presentation.category_rearrange

import com.kakeibo.feature_settings.domain.models.CategoryModel

data class CategoryRearrangeState(
    val displayedCategoryList: List<CategoryModel> = emptyList(),
    val nonDisplayedCategoryList: List<CategoryModel> = emptyList(),
    val finalCategoryList: MutableList<CategoryModel> = mutableListOf(),
    val isDisplayedCategoryListLoading: Boolean = false,
    val isNonDisplayedCategoryListLoading: Boolean = false
)