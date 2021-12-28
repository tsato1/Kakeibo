package com.kakeibo.feature_main.presentation.item_main.item_list

import com.kakeibo.feature_main.presentation.item_main.item_list.components.ExpandableItem

data class ExpandableItemListState(
    val expandableItemList: List<ExpandableItem> = emptyList(),
    val isLoading: Boolean = false
)