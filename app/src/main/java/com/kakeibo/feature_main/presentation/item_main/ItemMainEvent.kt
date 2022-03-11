package com.kakeibo.feature_main.presentation.item_main

import com.kakeibo.feature_main.domain.models.DisplayedItemModel

sealed class ItemMainEvent {
    data class DeleteItem(val displayedItemModel: DisplayedItemModel) : ItemMainEvent()
    object RestoreItem : ItemMainEvent()
    data class LoadItems(val searchId: Long) : ItemMainEvent()
    object ExitSearchMode : ItemMainEvent()
}