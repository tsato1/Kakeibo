package com.kakeibo.feature_settings.presentation.category_rearrange.presentation

import com.kakeibo.feature_main.domain.models.DisplayedCategory

data class CategoryRearrangeState(
    val displayedCategoryList: List<DisplayedCategory> = emptyList(),
    val nonDisplayedCategoryList: List<DisplayedCategory> = emptyList(),
    val isDisplayedCategoryListLoading: Boolean = false,
    val isNonDisplayedCategoryListLoading: Boolean = false
)