package com.kakeibo.feature_main.presentation.item_detail

import androidx.compose.ui.focus.FocusState
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel

sealed class ItemDetailEvent {
    data class AmountEntered(val value: String) : ItemDetailEvent()
    data class AmountFocusChanged(val focusState: FocusState) : ItemDetailEvent()
    data class CategorySelected(val displayedCategory: DisplayedCategoryModel) : ItemDetailEvent()
    data class MemoEntered(val value: String) : ItemDetailEvent()
    data class MemoFocusChanged(val focusState: FocusState) : ItemDetailEvent()
    data class SaveItemWithCategory(val displayedCategory: DisplayedCategoryModel) : ItemDetailEvent()
    object SaveItem : ItemDetailEvent()
    object DeleteItem : ItemDetailEvent()
}