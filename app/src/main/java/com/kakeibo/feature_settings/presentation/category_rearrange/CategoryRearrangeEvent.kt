package com.kakeibo.feature_settings.presentation.category_rearrange

import com.kakeibo.feature_settings.domain.models.CategoryModel

sealed class CategoryRearrangeEvent {
    data class Add(val categoryModel: CategoryModel) : CategoryRearrangeEvent()
    data class Remove(val categoryModel: CategoryModel) : CategoryRearrangeEvent()
    object SaveAndReorder : CategoryRearrangeEvent()
    object SaveWithoutReorder : CategoryRearrangeEvent()
}