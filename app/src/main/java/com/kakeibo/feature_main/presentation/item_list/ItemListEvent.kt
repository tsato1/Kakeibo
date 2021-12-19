package com.kakeibo.feature_main.presentation.item_list

import com.kakeibo.feature_main.domain.models.DisplayedItem

sealed class ItemListEvent {
    data class DeleteItem(val displayedItem: DisplayedItem): ItemListEvent()
    object RestoreItem: ItemListEvent()
}