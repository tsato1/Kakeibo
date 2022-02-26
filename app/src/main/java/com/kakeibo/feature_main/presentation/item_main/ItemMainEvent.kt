package com.kakeibo.feature_main.presentation.item_main

import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import kotlinx.datetime.LocalDate

sealed class ItemMainEvent {
    data class DateChanged(val localDate: LocalDate) : ItemMainEvent()
    data class DeleteItem(val displayedItemModel: DisplayedItemModel) : ItemMainEvent()
    object RestoreItem : ItemMainEvent()
    data class LoadItems(val searchId: Long) : ItemMainEvent()
    object ExitSearchMode : ItemMainEvent()
}