package com.kakeibo.feature_main.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector?) {
    /* The following pages can be visited via bottom nav bar */
    object ItemListScreen: Screen("item_list_screen", "List", Icons.Default.List)
    object ItemChartScreen: Screen("item_chart", "Chart", Icons.Default.PieChart)
    object ItemCalendarScreen: Screen("item_calendar", "", Icons.Default.CalendarViewMonth)
    object ItemSearchScreen: Screen("item_search", "Search", Icons.Default.Search)

    object ItemInputScreen: Screen("item_input_screen", "Input", Icons.Default.Input)
    object ItemDetailScreen: Screen("item_detail_screen", "Detail", Icons.Default.Details)

    object DammyScreen: Screen("", "", null)
}