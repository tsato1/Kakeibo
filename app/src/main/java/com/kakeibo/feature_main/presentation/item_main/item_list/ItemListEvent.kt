package com.kakeibo.feature_main.presentation.item_main.item_list

import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import kotlinx.datetime.LocalDate

sealed class ItemListEvent {
    data class DateChanged(val value: LocalDate) : ItemListEvent()
    data class DeleteItem(val displayedItemModel: DisplayedItemModel): ItemListEvent()
    object RestoreItem: ItemListEvent()
}