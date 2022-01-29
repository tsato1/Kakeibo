package com.kakeibo.feature_main.presentation.common.components

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.feature_settings.presentation.SettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun TopNavigationBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController,
    onExportClick: () -> Unit
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    TopAppBar(
        title = {
            Text(text = "Kakeibo")
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    when (currentRoute) {
                        Screen.ItemListScreen.route + "?searchId={searchId}",
                        Screen.ItemChartScreen.route,
                        Screen.ItemCalendarScreen.route ->
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        else ->
                            navController.navigateUp()
                    }
                }
            ) {
                when (currentRoute) {
                    Screen.ItemListScreen.route + "?searchId={searchId}",
                    Screen.ItemChartScreen.route,
                    Screen.ItemCalendarScreen.route ->
                        Icon(Icons.Default.Menu, "Menu")
                    else ->
                        Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        },
        actions = {
            IconButton(
                onClick = { onExportClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Upload, contentDescription = "Export"
                )
            }
            IconButton(
                onClick = {
                    navController.navigate(Screen.ItemSearchScreen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true

                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "Search"
                )
            }
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings, contentDescription = "Settings"
                )
            }
        }
    )
}