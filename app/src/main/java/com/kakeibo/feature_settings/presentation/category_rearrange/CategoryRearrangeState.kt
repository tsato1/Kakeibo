package com.kakeibo.feature_settings.presentation.category_rearrange

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kakeibo.feature_settings.domain.models.CategoryModel

data class CategoryRearrangeState(
    val displayedCategoryList: List<CategoryModel> = emptyList(),
    val nonDisplayedCategoryList: List<CategoryModel> = emptyList(),
    val finalCategoryList: SnapshotStateList<CategoryModel> = mutableStateListOf(),
    val isDisplayedCategoryListLoading: Boolean = false,
    val isNonDisplayedCategoryListLoading: Boolean = false
)