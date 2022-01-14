package com.kakeibo.feature_main.presentation.item_main.item_chart

import com.kakeibo.feature_main.domain.models.DisplayedItemModel

data class ItemChartState(
    val incomeTotal: Long = 0L,
    val expenseTotal: Long = 0L,
    val incomeList: List<DisplayedItemModel> = emptyList(),
    val expenseList: List<DisplayedItemModel> = emptyList(),
    val incomeMap: Map<Int, List<DisplayedItemModel>> = emptyMap(),
    val expenseMap: Map<Int, List<DisplayedItemModel>> = emptyMap(),
    val isLoading: Boolean = false
)