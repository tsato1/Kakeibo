package com.kakeibo.feature_main.presentation.item_main.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kakeibo.feature_main.presentation.util.Screen

@Composable
fun BottomBar(navController: NavController) {
    val screens = listOf(
        Screen.ItemListScreen,
        Screen.ItemChartScreen,
        Screen.ItemCalendarScreen,
        Screen.DammyScreen
    )

    BottomNavigation(
        elevation = 16.dp,
        backgroundColor = MaterialTheme.colors.onSurface,
        contentColor = MaterialTheme.colors.background
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = "Icon"
                        )
                    }
                },
                selected = currentRoute == screen.route,
                label = { Text(text = screen.label) },
                onClick = {
                    navController.popBackStack(
                        navController.graph.startDestinationId,
                        true
                    )

                    if (screen.route != Screen.DammyScreen.route && currentRoute != screen.route)
                        navController.navigate(screen.route)
                }
            )
        }
    }
}
