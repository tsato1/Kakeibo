package com.kakeibo.feature_main.presentation.item_search

data class SearchCardAmountState(
    val from: String = "",
    val fromHint: String = "",
    val isFromHintVisible: Boolean = false,
    val to: String = "",
    val toHint: String = "",
    val isToHintVisible: Boolean = false
)