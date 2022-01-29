package com.kakeibo.feature_main.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.accompanist.pager.ExperimentalPagerApi
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.FirebaseViewModel
import com.kakeibo.feature_main.presentation.common.components.DrawerContent
import com.kakeibo.feature_main.presentation.common.components.TopNavigationBar
import com.kakeibo.feature_main.presentation.item_detail.item_edit.components.ItemDetailScreen
import com.kakeibo.feature_main.presentation.item_main.item_chart.components.ItemChartScreen
import com.kakeibo.feature_main.presentation.item_detail.item_input.components.ItemInputScreen
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.item_calendar.components.ItemCalendarScreen
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ItemListScreen
import com.kakeibo.feature_main.presentation.item_search.components.ItemSearchScreen
import com.kakeibo.feature_main.presentation.nav_drawer.components.AboutScreen
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.KakeiboTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: FirebaseViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) { // Successfully signed in
            viewModel.updateFirebaseUser()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(
                this,
                "Error Occurred. Please contact developer: ${response?.error?.errorCode}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KakeiboTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            TopNavigationBar(
                                scope = scope,
                                scaffoldState = scaffoldState,
                                navController = navController,
                                onExportClick = {
                                    asdf
                                }
                            )
                        },
                        drawerBackgroundColor = MaterialTheme.colors.background,
                        drawerContent= {
                            DrawerContent(
                                scope = scope,
                                scaffoldState = scaffoldState,
                                navController = navController,
                                onSigninClick = {
                                    triggerSignIn()
                                },
                                onSignoutClick = {
                                    triggerSignOut()
                                }
                            )
                        }
                    ) {
                        ScreenController(
                            navController = navController
                        )
                    }
                }
            }
        }
    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {

//            R.id.import_from_google_drive -> {
////                var importType = ImportExportActivity.ImportType.APPEND
//
//                val dialog = AlertDialog.Builder(this)
//                dialog.setIcon(R.mipmap.ic_mikan)
//                dialog.setTitle(R.string.choose_import_method)
//                dialog.setSingleChoiceItems(R.array.import_methods, 0) { _, which ->
//                    when (which) {
////                        0 -> importType = ImportExportActivity.ImportType.APPEND
////                        1 -> importType = ImportExportActivity.ImportType.SCRATCH
//                    }
//                }
//                dialog.setPositiveButton(R.string.proceed) { _, _ ->
////                    val intent = Intent(this, ImportExportActivity::class.java)
////                    intent.putExtra("ORDER_TYPE", ImportExportActivity.OrderType.IMPORT)
////                    intent.putExtra("IMPORT_TYPE", importType)
////                    startActivity(intent)
//                }
//                dialog.setNegativeButton(R.string.cancel) { _, _ -> }
//                dialog.setNeutralButton(R.string.explanation) { _, _ ->
//                    val url = "https://sites.google.com/view/kakeibo/home/import-export"
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = Uri.parse(url)
//                    startActivity(intent)
//                }
//                dialog.show()
//                true
//            }

    /*
     * Sign in with FirebaseUI Auth.
     */
    private fun triggerSignIn() {
        val providers: MutableList<AuthUI.IdpConfig> = ArrayList()
        providers.add(AuthUI.IdpConfig.EmailBuilder().build())
        providers.add(AuthUI.IdpConfig.GoogleBuilder().build())
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(intent)
    }

    /*
     * Sign out with FirebaseUI Auth.
     */
    private fun triggerSignOut() {
//        _subscriptionViewModel.unregisterInstanceId()
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            viewModel.updateFirebaseUser()
            Toast.makeText(this, R.string.sign_out_success, Toast.LENGTH_LONG).show()
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ScreenController(
    navController: NavHostController,
    itemMainViewModel: ItemMainViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ItemListScreen.route + "?searchId={searchId}"
    ) {
        composable(
            route = Screen.ItemListScreen.route + "?searchId={searchId}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            val searchId = it.arguments?.getLong("searchId") ?: -1L
            ItemListScreen(navController = navController, viewModel = itemMainViewModel, searchId)
        }
        composable(route = Screen.ItemChartScreen.route) {
            ItemChartScreen(navController = navController, viewModel = itemMainViewModel)
        }
        composable(route = Screen.ItemCalendarScreen.route) {
            ItemCalendarScreen(navController = navController, viewModel = itemMainViewModel)
        }
        composable(route = Screen.ItemInputScreen.route) {
            ItemInputScreen(navController = navController)
        }
        composable(
            route = Screen.ItemDetailScreen.route + "?itemId={itemId}",
            arguments = listOf(
                navArgument(
                    name = "itemId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            val itemId = it.arguments?.getLong("itemId") ?: -1L
            ItemDetailScreen(navController = navController, itemId = itemId)
        }
        composable(route = Screen.ItemSearchScreen.route) {
            ItemSearchScreen(navController = navController)
        }
        composable(route = Screen.AboutScreen.route) {
            AboutScreen()
        }
    }
}