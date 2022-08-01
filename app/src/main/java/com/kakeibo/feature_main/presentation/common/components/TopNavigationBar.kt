package com.kakeibo.feature_main.presentation.common.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.feature_settings.presentation.SettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.kakeibo.R

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
        modifier = Modifier.height(40.dp),
        title = { Text(text = "") },
        navigationIcon = {
            IconButton(
                onClick = {
                    when (currentRoute) {
                        Screen.ItemListScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
                        Screen.ItemChartScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
                        Screen.ItemCalendarScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}" ->
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        else ->
                            navController.navigateUp()
                    }
                }
            ) {
                when (currentRoute) {
                    Screen.ItemListScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
                    Screen.ItemChartScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
                    Screen.ItemCalendarScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}" ->
                        Icon(Icons.Default.Menu, "Menu")
                    else ->
                        Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        },
        actions = {
            if (currentRoute == Screen.ItemListScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}" ||
                currentRoute == Screen.ItemChartScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}" ||
                currentRoute == Screen.ItemCalendarScreen.route + "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}"
            ) {
                IconButton(
                    onClick = { onExportClick() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload, contentDescription = "Export"
                    )
                }
                IconButton(
                    onClick = {
                        val searchId = navBackStackEntry?.arguments?.getLong("searchId")

                        if (currentRoute == Screen.ItemSearchScreen.route) {
                            // do nothing
                        } else if (searchId == null || searchId == 0L) { /* if NOT in search mode */
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
                        } else if (searchId != 0L) { /* if in search mode */
                            Toast.makeText(
                                context,
                                R.string.exit_search_first,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            /* something must be wrong */
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
        }
    )
}