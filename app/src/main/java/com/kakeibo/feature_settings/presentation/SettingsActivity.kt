package com.kakeibo.feature_settings.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adcolony.sdk.AdColony
import com.adcolony.sdk.AdColonyAdSize
import com.adcolony.sdk.AdColonyAdView
import com.adcolony.sdk.AdColonyAdViewListener
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.ads.MobileAds
import com.kakeibo.feature_settings.presentation.custom_category_detail.components.CustomCategoryDetailScreen
import com.kakeibo.feature_settings.presentation.custom_category_list.components.CustomCategoryListScreen
import com.kakeibo.feature_settings.presentation.settings_list.components.SettingsListScreen
import com.kakeibo.feature_settings.presentation.util.Screen
import com.kakeibo.ui.theme.KakeiboTheme
import com.kakeibo.R
import com.kakeibo.feature_settings.presentation.category_rearrange.components.CategoryRearrangeScreen
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* ads */
        MobileAds.initialize(this) { }
        val adViewListener: AdColonyAdViewListener = object : AdColonyAdViewListener() {
            override fun onRequestFilled(ad: AdColonyAdView) {
                /** Add this ad object to whatever layout you have set up for this placement  */
            }
        }
        AdColony.requestAdView(getString(R.string.main_banner_ad_zone_id), adViewListener, AdColonyAdSize.BANNER)

        setContent {
            KakeiboTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = { TopNavigationBar(navController) },
                    ) {
                        ScreenController(navController)
                    }
                }
            }
        }
    }

}

@Composable
fun TopNavigationBar(
    navController: NavController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val activity = (LocalContext.current as? Activity)

    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.settings))
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    when (currentRoute) {
                        Screen.SettingsListScreen.route -> activity?.finish()
                        else -> navController.navigateUp()
                    }
                }
            ) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }
    )
}

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun ScreenController(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SettingsListScreen.route,
    ) {
        composable(route = Screen.SettingsListScreen.route) {
            SettingsListScreen(navController = navController)
        }
        composable(route = Screen.CustomCategoryListScreen.route) {
            CustomCategoryListScreen(navController = navController)
        }
        composable(
            route = Screen.CustomCategoryDetailScreen.route +
                    "?categoryId={categoryId}&categoryCode={categoryCode}",
            arguments = listOf(
                navArgument(
                    name = "categoryId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "categoryCode"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            CustomCategoryDetailScreen(navController = navController)
        }
        composable(route = Screen.CategoryRearrangeScreen.route) {
            CategoryRearrangeScreen(navController = navController)
        }
    }
}