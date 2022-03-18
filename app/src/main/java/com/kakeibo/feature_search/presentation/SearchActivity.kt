//package com.kakeibo.feature_search.presentation
//
//import android.app.Activity
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.navigation.NavController
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.kakeibo.R
//import com.kakeibo.feature_main.presentation.item_search.components.ItemSearchScreen
//import com.kakeibo.feature_search.presentation.util.Screen
//import com.kakeibo.ui.theme.KakeiboTheme
//import dagger.hilt.android.AndroidEntryPoint
//
//@OptIn(ExperimentalComposeUiApi::class)
//@AndroidEntryPoint
//class SearchActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            KakeiboTheme {
//                val navController = rememberNavController()
//                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
//
//                Surface(
//                    color = MaterialTheme.colors.background
//                ) {
//                    Scaffold(
//                        scaffoldState = scaffoldState,
//                        topBar = { TopNavigationBar(navController) },
//                    ) {
//                        ScreenController(navController)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TopNavigationBar(
//    navController: NavController
//) {
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    val activity = (LocalContext.current as? Activity)
//
//    TopAppBar(
//        title = { Text(text = stringResource(id = R.string.search)) },
//        navigationIcon = {
//            IconButton(
//                onClick = {
//                    when (currentRoute) {
//                        com.kakeibo.feature_settings.presentation.util.Screen.SettingsListScreen.route -> activity?.finish()
//                        else -> navController.navigateUp()
//                    }
//                }
//            ) {
//                Icon(Icons.Default.ArrowBack, "Back")
//            }
//        }
//    )
//}
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun ScreenController(
//    navController: NavHostController
//) {
//    NavHost(
//        navController = navController,
//        startDestination = Screen.ItemSearchScreen.route
//    ) {
//        composable(route = Screen.ItemSearchScreen.route) {
//            ItemSearchScreen(navController = navController)
//        }
//    }
//}