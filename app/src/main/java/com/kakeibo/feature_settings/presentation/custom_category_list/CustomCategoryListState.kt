package com.kakeibo.feature_settings.presentation.custom_category_list

import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.util.CustomCategoryListOrder

data class CustomCategoryListState(
    val customCategoryList: List<CategoryModel> = emptyList(),
    val listOrder: CustomCategoryListOrder = CustomCategoryListOrder.Name,
    val isLoading: Boolean = false,
    val isOrderSectionVisible: Boolean = false
)