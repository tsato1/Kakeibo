package com.kakeibo.feature_main.presentation.item_main

import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import java.util.Calendar

sealed class ItemMainEvent {
    data class DeleteItem(val displayedItemModel: DisplayedItemModel) : ItemMainEvent()
    object RestoreItem : ItemMainEvent()
    data class LoadItems(val searchId: Long, val focusDate: Calendar, val focusItemId: String) : ItemMainEvent()
    object ExitSearchMode : ItemMainEvent()
}