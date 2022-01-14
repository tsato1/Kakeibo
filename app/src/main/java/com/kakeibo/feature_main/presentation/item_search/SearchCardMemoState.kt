package com.kakeibo.feature_main.presentation.item_search

data class SearchCardMemoState(
    val memo: String = "",
    val hint: String = "",
    val isHintVisible: Boolean = true
)