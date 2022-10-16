package com.kakeibo.feature_main.presentation.item_search

import androidx.compose.ui.focus.FocusState
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import java.util.Calendar

sealed class ItemSearchEvent {
    data class AddSearchCriterion(val criterion: SearchCriterion) : ItemSearchEvent()
    data class DiscardSearchCriterion(val criterion: SearchCriterion) : ItemSearchEvent()
    data class DateFromSelected(val from: Calendar) : ItemSearchEvent()
    data class DateToSelected(val to: Calendar) : ItemSearchEvent()
    data class AmountFromEntered(val from: String) : ItemSearchEvent()
    data class AmountToEntered(val to: String) : ItemSearchEvent()
    data class AmountFromFocusChanged(val focusState: FocusState) : ItemSearchEvent()
    data class AmountToFocusChanged(val focusState: FocusState) : ItemSearchEvent()
    data class CategorySelected(val categoryModel: DisplayedCategoryModel) : ItemSearchEvent()
    data class MemoEntered(val memo: String) : ItemSearchEvent()
    data class MemoFocusChanged(val focusState: FocusState) : ItemSearchEvent()
    object PerformSearch : ItemSearchEvent()
}