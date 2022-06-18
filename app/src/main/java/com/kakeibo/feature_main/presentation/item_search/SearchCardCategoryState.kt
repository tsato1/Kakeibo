package com.kakeibo.feature_main.presentation.item_search

import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel

data class SearchCardCategoryState(
    val categoryModel: DisplayedCategoryModel = DisplayedCategoryModel(
        _id = -100L,
        code = CATEGORY_INVALID,
        name = "",
        color = CATEGORY_INVALID,
        sign = CATEGORY_INVALID,
        drawable = "",
        image = null,
        parent = CATEGORY_INVALID,
        description = "",
        savedDate = ""
    )
)

const val CATEGORY_INVALID = -100