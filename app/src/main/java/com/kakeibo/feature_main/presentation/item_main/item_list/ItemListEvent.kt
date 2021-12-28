package com.kakeibo.feature_main.presentation.item_main.item_list

import com.kakeibo.feature_main.domain.models.DisplayedItemModel

sealed class ItemListEvent {
    data class DeleteItem(val displayedItemModel: DisplayedItemModel): ItemListEvent()
    object RestoreItem: ItemListEvent()
}