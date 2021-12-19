package com.kakeibo.feature_main.presentation.item_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kakeibo.feature_main.presentation.common.components.DatePickerRow
import com.kakeibo.feature_main.presentation.common.components.DateType
import com.kakeibo.feature_main.presentation.item_list.ItemListViewModel
import com.kakeibo.feature_main.presentation.util.Screen

@Composable
fun ItemListScreen(
    navController: NavController,
    viewModel: ItemListViewModel = hiltViewModel(),
    itemId: Long
) {
    val itemListState = viewModel.expandableItemListState.value
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.ItemInputScreen.route)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        bottomBar = { BottomBar(navController = navController) },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            DatePickerRow(
                context = LocalContext.current,
                viewModel = viewModel,
                type = DateType.YM
            )
            Spacer(modifier = Modifier.height(16.dp))
            CollapsableLazyColumn(
                sections = itemListState.expandableItemList,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
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

        items.forEach { screen ->
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
                        false
                    )

                    if (screen.route != Screen.DammyScreen.route && currentRoute != screen.route)
                        navController.navigate(screen.route)
                }
            )
        }
    }
}
