package com.kakeibo.feature_main.presentation.item_input

import androidx.compose.ui.focus.FocusState
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel

sealed class ItemInputEvent {
    data class SelectDate(val value: String) : ItemInputEvent()
    data class EnterAmount(val value: String) : ItemInputEvent()
    data class ChangeAmountFocus(val focusState: FocusState) : ItemInputEvent()
    data class EnterMemo(val value: String) : ItemInputEvent()
    data class ChangeMemoFocus(val focusState: FocusState) : ItemInputEvent()
    data class SaveItemWithCategory(val displayedCategory: DisplayedCategoryModel) : ItemInputEvent()
    object SaveItem : ItemInputEvent()
    object DeleteItem : ItemInputEvent()
}