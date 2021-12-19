package com.kakeibo.feature_main.presentation.item_input

import com.kakeibo.feature_main.domain.models.DisplayedCategory

data class DisplayedCategoryListState(
    val displayedCategoryList: List<DisplayedCategory> = emptyList()
)