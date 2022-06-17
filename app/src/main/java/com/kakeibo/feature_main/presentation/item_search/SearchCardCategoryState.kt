package com.kakeibo.feature_main.presentation.item_search

import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel

data class SearchCardCategoryState(
    val categoryModel: DisplayedCategoryModel = DisplayedCategoryModel(
        _id = -100L,
        code = CATEGORY_NOT_CHOSEN,
        name = "",
        color = CATEGORY_NOT_CHOSEN,
        sign = CATEGORY_NOT_CHOSEN,
        drawable = "",
        image = null,
        parent = CATEGORY_NOT_CHOSEN,
        description = "",
        savedDate = ""
    )
)

const val CATEGORY_NOT_CHOSEN = -100