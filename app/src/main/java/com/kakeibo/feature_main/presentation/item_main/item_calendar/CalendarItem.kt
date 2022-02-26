package com.kakeibo.feature_main.presentation.item_main.item_calendar

import com.kakeibo.feature_main.domain.models.DisplayedItemModel

data class CalendarItem(
    val parent: Parent, // DB FORMAT
    val children: List<DisplayedItemModel>
) {
    data class Parent(val date: String, val income: String, val expense: String)
}