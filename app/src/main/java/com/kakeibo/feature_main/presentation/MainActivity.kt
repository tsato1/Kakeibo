package com.kakeibo.feature_main.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.kakeibo.feature_main.presentation.item_detail.item_edit.components.ItemDetailScreen
import com.kakeibo.feature_main.presentation.item_main.item_chart.components.ItemChartScreen
import com.kakeibo.feature_main.presentation.item_detail.item_input.components.ItemInputScreen
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.item_calendar.components.ItemCalendarScreen
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ItemListScreen
import com.kakeibo.feature_main.presentation.item_search.components.ItemSearchScreen
import com.kakeibo.feature_main.presentation.nav_drawer.components.NavDrawerItem
import com.kakeibo.feature_main.presentation.nav_drawer.NavDrawerItem
import com.kakeibo.feature_main.presentation.nav_drawer.components.AboutScreen
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.feature_settings.presentation.SettingsActivity
import com.kakeibo.ui.theme.KakeiboTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
                        topBar = { TopNavigationBar(scope, scaffoldState, navController) },
                        drawerBackgroundColor = MaterialTheme.colors.background,
                        drawerContent= { DrawerContent(scope, scaffoldState, navController) }
                    ) {
                        ScreenController(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
// todo: firebase signin




//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.sign_in -> {
////                triggerSignIn()
//                true
//            }
//            R.id.sign_out -> {
////                triggerSignOut()
//                true
//            }
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
////            R.id.in_app_purchases -> {
////                _startForResult.launch(Intent(this, InAppPurchasesActivity::class.java))
////                true
////            }
//            R.id.about -> {
//                startActivity(Intent(this, AboutActivity::class.java))
//                true
//            }
//            else -> true
//        }
//    }

    /*
     * Sign in with FirebaseUI Auth.
     */
//    private fun triggerSignIn() {
//        Log.d(TAG, "Attempting SIGN-IN!")
//        val providers: MutableList<IdpConfig> = ArrayList()
//        providers.add(EmailBuilder().build())
//        providers.add(GoogleBuilder().build())
//        val intent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .build()
//        _startForResult.launch(intent)
//    }

    /*
     * Sign out with FirebaseUI Auth.
     */
//    private fun triggerSignOut() {
//        _subscriptionViewModel.unregisterInstanceId()
//        AuthUI.getInstance().signOut(this).addOnCompleteListener {
//            Log.d(TAG, "User SIGNED OUT!")
//            _authenticationViewModel.updateFirebaseUser()
//            Toast.makeText(this, R.string.sign_out_success, Toast.LENGTH_LONG).show()
//        }
//    }

//    class SmartPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
//        val fragments: MutableList<Fragment> = mutableListOf()
//
//        override fun getItemCount(): Int = 3
//
//        override fun createFragment(position: Int): Fragment {
//            return when (position) {
//                0 -> {
//                    val fragment1 = InputFragment.newInstance()
//                    fragments.add(fragment1)
//                    fragment1
//                }
//                1 -> {
//                    val fragment2 = ReportFragment.newInstance()
//                    fragments.add(fragment2)
//                    fragment2
//                }
//                2 -> {
//                    val fragment3 = SearchFragment.newInstance()
//                    fragments.add(fragment3)
//                    fragment3
//                }
//                else -> throw Exception("unknown item type")
//            }
//        }
//    }

    /*
     * Called from TabFragment1 upon tapping one of the category buttons
     */
//    fun onItemSaved(date: String) {
//        _viewPager.currentItem = 1 // move to tabFragment2
//        if (_smartPagerAdapter.fragments.size == 0) {
//            _smartPagerAdapter.createFragment(0)
//            _smartPagerAdapter.createFragment(1)
//            _smartPagerAdapter.createFragment(2)
//        }
//        (_smartPagerAdapter.fragments[1] as ReportFragment).focusOnSavedItem(date)
//    }

    /*
     * Called from TabFragment3 upon tapping search button
     */
//    fun onSearch(query: Query) {
//        _viewPager.currentItem = 1 // move to tabFragment2
//        if (_smartPagerAdapter.fragments.size == 0) {
//            _smartPagerAdapter.createFragment(0)
//            _smartPagerAdapter.createFragment(1)
//            _smartPagerAdapter.createFragment(2)
//        }
//        (_smartPagerAdapter.fragments[1] as ReportFragment).onSearch(query)
//    }

//    internal inner class FabClickListener : View.OnClickListener {
//        override fun onClick(view: View) {
//            if (view.id == R.id.fab_start) {
//                if (_viewPager.currentItem == 2) {
//                    (_smartPagerAdapter.fragments[2] as SearchFragment).addCriteria()
//                }
//            }
//            else if (view.id == R.id.fab_end) {
//                if (_viewPager.currentItem == 1) {
//                    (_smartPagerAdapter.fragments[1] as ReportFragment).export()
//                } else if (_viewPager.currentItem == 2) {
//                    (_smartPagerAdapter.fragments[2] as SearchFragment).doSearch()
//                }
//            }
//        }
//    }
}

@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun TopNavigationBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController
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
                        Screen.ItemListScreen.route,
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
                    Screen.ItemListScreen.route,
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
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

@Composable
fun DrawerContent(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    navController: NavController
) {
    val navDrawerItems = listOf(
        NavDrawerItem.SignIn,
        NavDrawerItem.SignOut,
        NavDrawerItem.About
    )
    
    Column(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        Box(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.onSurface)
        )
        Spacer(modifier = Modifier
            .height(6.dp)
            .fillMaxWidth()
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navDrawerItems.forEach { navDrawerItem ->
            NavDrawerItem(
                item = navDrawerItem,
                selected = currentRoute == navDrawerItem.route,
                onItemClick = {
                    navController.navigate(navDrawerItem.route) {
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

                    scope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
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
        startDestination = Screen.ItemListScreen.route
    ) {
        composable(route = Screen.ItemListScreen.route) {
            ItemListScreen(navController = navController, viewModel = itemMainViewModel)
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