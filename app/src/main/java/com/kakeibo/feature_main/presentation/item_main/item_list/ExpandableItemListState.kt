package com.kakeibo.feature_main.presentation.item_main.item_list

data class ExpandableItemListState(
    val expandableItemList: List<ExpandableItem> = emptyList(),
    val isLoading: Boolean = false
)