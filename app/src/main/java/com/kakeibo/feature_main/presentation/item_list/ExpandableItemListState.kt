package com.kakeibo.feature_main.presentation.item_list

import com.kakeibo.feature_main.presentation.item_list.components.ExpandableItem

data class ExpandableItemListState(
    val expandableItemList: List<ExpandableItem> = emptyList(),
    val isLoading: Boolean = false
)