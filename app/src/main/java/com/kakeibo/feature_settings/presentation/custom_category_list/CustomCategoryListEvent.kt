package com.kakeibo.feature_settings.presentation.custom_category_list

import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.util.CustomCategoryListOrder

sealed class CustomCategoryListEvent {
    object CreateNew : CustomCategoryListEvent()
    data class Reorder(val listOrder: CustomCategoryListOrder) : CustomCategoryListEvent()
    data class Delete(val categoryModel: CategoryModel) : CustomCategoryListEvent()
    object Restore : CustomCategoryListEvent()
    object ToggleOrderSection : CustomCategoryListEvent()
}