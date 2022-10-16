package com.kakeibo.feature_main.presentation.item_search

import java.util.*

data class SearchCardDateRangeState(
    val from: Calendar = Calendar.getInstance(),
    val to: Calendar = Calendar.getInstance()
)