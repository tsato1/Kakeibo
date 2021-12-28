package com.kakeibo.feature_main.presentation.item_input

import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel

data class DisplayedCategoryListState(
    val displayedCategoryList: List<DisplayedCategoryModel> = emptyList(),
    val isLoading: Boolean = false
)