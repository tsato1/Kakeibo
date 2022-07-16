package com.kakeibo.feature_main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.kakeibo.R

sealed class Screen(val route: String, val label: Int, val icon: ImageVector?) {
    /* The following pages can be visited via bottom nav bar */
    object ItemListScreen: Screen("item_list", R.string.list, Icons.Default.List)
    object ItemChartScreen: Screen("item_chart", R.string.chart, Icons.Default.PieChart)
    object ItemCalendarScreen: Screen("item_calendar", R.string.calendar, Icons.Default.CalendarViewMonth)
    object ItemSearchScreen: Screen("item_search", R.string.search, Icons.Default.Search)

    object ItemInputScreen: Screen("item_input", R.string.input, Icons.Default.Input)
    object ItemDetailScreen: Screen("item_detail", R.string.detail, Icons.Default.Details)

    object AboutScreen: Screen("about", R.string.about, null)
    object DammyScreen: Screen("", -1, null)
}