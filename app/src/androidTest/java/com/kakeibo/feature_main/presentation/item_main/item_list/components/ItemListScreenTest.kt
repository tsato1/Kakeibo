package com.kakeibo.feature_main.presentation.item_main.item_list.components

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import com.google.accompanist.pager.ExperimentalPagerApi
import com.kakeibo.di.AppModule
import com.kakeibo.feature_main.presentation.MainActivity
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.KakeiboTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalPagerApi::class
)
class ItemListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()

        composeRule.setContent {
            val navController = rememberNavController()
            KakeiboTheme {
                NavHost(navController = navController, startDestination = Screen.ItemListScreen.route) {
                    composable(route = Screen.ItemListScreen.route) {
//                        ItemListScreen(navController = navController)
                    }
                }
            }
        }
    }

    @Test
    fun clickToggleOrderSection_isVisible() {
        val context = ApplicationProvider.getApplicationContext<Context>()
//        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertDoesNotExist()
//        composeRule.onNodeWithContentDescription("Sort").performClick()
//        composeRule.onNodeWithTag(TestTags.ORDER_SECTION).assertIsDisplayed()
    }

}