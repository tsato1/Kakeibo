package com.kakeibo.feature_main.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adcolony.sdk.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.kakeibo.BuildConfig
import com.kakeibo.Constants.KEY_LOGGED_IN_EMAIL
import com.kakeibo.Constants.KEY_PASSWORD
import com.kakeibo.Constants.NO_EMAIL
import com.kakeibo.Constants.NO_PASSWORD
import com.kakeibo.R
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.data.remote.BasicAuthInterceptor
import com.kakeibo.core.presentation.AuthViewModel
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_export.DriveServiceHelper
import com.kakeibo.feature_main.presentation.common.FirebaseViewModel
import com.kakeibo.feature_main.presentation.nav_drawer.components.DrawerContent
import com.kakeibo.feature_main.presentation.common.components.ImportExportDialog
import com.kakeibo.feature_main.presentation.common.components.TopNavigationBar
import com.kakeibo.feature_main.presentation.item_detail.item_edit.components.ItemEditScreen
import com.kakeibo.feature_main.presentation.item_detail.item_input.components.ItemInputScreen
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.item_calendar.components.ItemCalendarScreen
import com.kakeibo.feature_main.presentation.item_main.item_chart.components.ItemChartScreen
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ItemListScreen
import com.kakeibo.feature_main.presentation.item_search.components.ItemSearchScreen
import com.kakeibo.feature_main.presentation.nav_drawer.components.AboutScreen
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.KakeiboTheme
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import com.kakeibo.util.UtilFiles
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

//when saving an item, pass the month to list screen viewmodel to open that month's data
//string: "export is initiated"

//todo keyboard goes away
//todo typography
//todo input : accept comma and period
//todo settings: categoryListScreen: order by functionality

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var interstitialAd: InterstitialAd? = null

    private val authViewModel: AuthViewModel by viewModels()
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private val itemMainViewModel: ItemMainViewModel by viewModels()

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var currEmail: String? = null
    private var currPassword: String? = null

    override fun onStart() {
        super.onStart()
        itemMainViewModel.setSharedPreferencesStates()
        itemMainViewModel.loadKkbAppStates()
        itemMainViewModel.onEvent(
            ItemMainEvent.LoadItems(
                0L,
                UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB),
                -1L
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* login */
        if (isLoggedIn()) {
            authenticateApi(currEmail ?: "", currPassword ?: "")
            redirectLogin()
        }

        /* ads */
        MobileAds.initialize(this) { }
        val adRequest = AdRequest.Builder().build()
        var adUnitId = getString(R.string.upload_ad)
        if (BuildConfig.DEBUG) { /* in debug mode */
            adUnitId = "ca-app-pub-3940256099942544/1033173712"
        }
        InterstitialAd.load(this, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {

            }
        })

        /* Ad Colony */
        val adViewListener: AdColonyAdViewListener = object : AdColonyAdViewListener() {
            override fun onRequestFilled(ad: AdColonyAdView) {
                /** Add this ad object to whatever layout you have set up for this placement  */
            }
        }
        AdColony.requestAdView(getString(R.string.main_banner_ad_zone_id), adViewListener, AdColonyAdSize.BANNER)

        AdColony.configure(this, getString(R.string.admob_app_id))
        val interstitialListener: AdColonyInterstitialListener = object : AdColonyInterstitialListener() {
            override fun onRequestFilled(ad: AdColonyInterstitial) {
                /* Store and use this ad object to show your ad when appropriate */
            }
        }
        AdColony.requestInterstitial(getString(R.string.upload_ad_zone_id), interstitialListener)

        setContent {
            KakeiboTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

                val openImportExportDialog = remember { mutableStateOf(false) }
                if (openImportExportDialog.value) {
                    ImportExportDialog(
                        onDismissRequest = { openImportExportDialog.value = false },
                        onConfirmButtonClick = {
                            if (!firebaseViewModel.isSignedIn()) {
                                triggerSignIn()
                            }

                            triggerGoogleSignIn()

                            openImportExportDialog.value = false
                        },
                        itemMainViewModel = itemMainViewModel
                    )
                }

                //disposable!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
//                DisposableEffect(lifecycleOwner) {
//                    val observer = LifecycleEventObserver { _, event ->
//                        if (event == Lifecycle.Event.ON_START) {
//                            itemMainViewModel.setSharedPreferencesStates()
//                            itemMainViewModel.loadKkbAppStates()
//                            itemMainViewModel.onEvent(
//                                ItemMainEvent.LoadItems(
//                                    0L,
//                                    UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB),
//                                    -1L
//                                )
//                            )
//                        }
//                    }
//                    lifecycleOwner.lifecycle.addObserver(observer)
//                    onDispose {
//                        lifecycleOwner.lifecycle.removeObserver(observer)
//                    }
//                }

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
                                onExportClick = { openImportExportDialog.value = true }
                            )
                        },
                        drawerBackgroundColor = MaterialTheme.colors.background,
                        drawerContent= {
                            DrawerContent(
                                scope = scope,
                                scaffoldState = scaffoldState,
                                navController = navController,
                                onSigninClick = { triggerSignIn() },
                                onSignoutClick = { triggerSignOut() }
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

    private fun isLoggedIn(): Boolean {
        currEmail = appPreferences.getString(KEY_LOGGED_IN_EMAIL, NO_EMAIL) ?: NO_EMAIL
        currPassword = appPreferences.getString(KEY_PASSWORD, NO_PASSWORD) ?: NO_PASSWORD
        return currEmail != NO_EMAIL && currPassword != NO_PASSWORD
    }

    private fun authenticateApi(email: String, password: String) {
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun redirectLogin() {
        Log.d("asdf", "redirectLogin()")
//        val navOptions = NavOptions.Builder()
//            .setPopUpTo(R.id.authFragment, true)
//            .build()
//
//        findNavController().navigate(
//            AuthFragmentDirections.actionAuthFragmentToNotesFragment(),
//            navOptions
//        )
    }

    private fun subscribeToObservers() {
        authViewModel.loginStatus.observe(this) { result ->
            result?.let {
                when (it) {
                    is Resource.Success -> {
//                        loginProgressBar.visibility = View.GONE
//                        showSnackbar(it.data ?: "Login successful")

                        appPreferences.set(KEY_LOGGED_IN_EMAIL, currEmail)
                        appPreferences.set(KEY_PASSWORD, currPassword)

                        authenticateApi(currEmail ?: "", currPassword ?: "")
                        redirectLogin()
                    }
                    is Resource.Error -> {
//                        loginProgressBar.visibility = View.GONE
//                        showSnackbar(it.message ?: "An unknown error occurred")
                    }
                    is Resource.Loading -> {
//                        loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

        authViewModel.registerStatus.observe(this) { result ->
            result?.let {
                when (it) {
                    is Resource.Success -> {
//                        registerProgressBar.visibility = View.GONE
//                        showSnackbar(it.data ?: "Successfully registered an account")
                    }
                    is Resource.Error -> {
//                        registerProgressBar.visibility = View.GONE
//                        showSnackbar(it.message ?: "An unknown error occurred")
                    }
                    is Resource.Loading -> {
//                        registerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    /* */
    private val signInLauncherFirebase = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) { // Successfully signed in
            firebaseViewModel.updateFirebaseUser()
        }
        else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Toast.makeText(
                this,
                "Sign in canceled: ${response?.error?.errorCode}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

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
        signInLauncherFirebase.launch(intent)
    }

    /*
     * Sign out with FirebaseUI Auth.
     */
    private fun triggerSignOut() {
//        _subscriptionViewModel.unregisterInstanceId()
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            firebaseViewModel.updateFirebaseUser()
            Toast.makeText(this, R.string.sign_out_success, Toast.LENGTH_LONG).show()
        }
    }

    private fun triggerGoogleSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
        GoogleSignIn.getClient(this, signInOptions).also {
            googleSignInLauncher.launch(it.signInIntent)
        }
    }

    private val googleSignInLauncher = registerForActivityResult (
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                task.getResult(ApiException::class.java).also {
                    connectToGDrive(it)
                }
            }
            catch (e: ApiException) {

            }
        }
    }

    private fun connectToGDrive(googleAccount: GoogleSignInAccount) {
        // Signed in successfully, show authenticated UI.
        // Use the authenticated account to sign in to the Drive service.
        val credential = GoogleAccountCredential
            .usingOAuth2(this, setOf(DriveScopes.DRIVE_FILE))
            .also {
                it.selectedAccount = googleAccount.account
            }

        val googleDriveService = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
            .setApplicationName("Drive API Migration")
            .build()

        DriveServiceHelper(googleDriveService).also { helper ->
            helper.createFilePickerIntent().also {
                requestCreateDocument.launch(it)
            }
//            when (mOrderType) {
//                ImportExportActivity.OrderType.EXPORT -> {
//                    val pickerIntent = it.createFilePickerIntent()
//                    _requestCreateDocument.launch(pickerIntent)
//                }
//                else -> {
//                    val pickerIntent = it.openFilePickerIntent()
////                    _requestOpenDocument.launch(pickerIntent)
//                }
//            }
        }
    }

    private val requestCreateDocument = registerForActivityResult (
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->

        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    contentResolver.openOutputStream(uri)?.let { os ->
                        val content = UtilFiles.getFileValue(UtilFiles.FILE_NAME, this)
                        os.write(content!!.toByteArray())
                        os.close()

                        Toast.makeText(this, R.string.file_created, Toast.LENGTH_LONG).show()

                        /* ads */
                        if (interstitialAd != null) {
                            interstitialAd?.show(this)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
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
        startDestination = Screen.ItemListScreen.route +
                "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}"
    ) {
        composable(
            route = Screen.ItemListScreen.route +
                    "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument(
                    name = "focusDate"
                ) {
                    type = NavType.StringType
                    defaultValue = UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB)
                },
                navArgument(
                    name = "focusItemId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "reload"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val searchId = it.arguments?.getLong("searchId") ?: 0L
            val focusDate = it.arguments?.getString("focusDate") ?: ""
            val focusItemId = it.arguments?.getLong("focusItemId") ?: -1L
            val reload = it.arguments?.getBoolean("reload") ?: false
            ItemListScreen(
                navController = navController,
                viewModel = itemMainViewModel,
                searchId = searchId,
                focusDate = focusDate,
                focusItemId = focusItemId,
                reload = reload
            )
        }
        composable(
            route = Screen.ItemChartScreen.route +
                    "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument(
                    name = "focusDate"
                ) {
                    type = NavType.StringType
                    defaultValue = UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB)
                },
                navArgument(
                    name = "focusItemId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "reload"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            ItemChartScreen(navController = navController, viewModel = itemMainViewModel)
        }
        composable(
            route = Screen.ItemCalendarScreen.route +
                    "?searchId={searchId}/?focusDate={focusDate}/?focusItemId={focusItemId}/?reload={reload}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument(
                    name = "focusDate"
                ) {
                    type = NavType.StringType
                    defaultValue = UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB)
                },
                navArgument(
                    name = "focusItemId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1L
                },
                navArgument(
                    name = "reload"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
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
                    defaultValue = 0L
                }
            )
        ) {
            ItemEditScreen(navController = navController)
        }
        composable(route = Screen.ItemSearchScreen.route) {
            ItemSearchScreen(navController = navController)
        }
        composable(route = Screen.AboutScreen.route) {
            AboutScreen()
        }
    }
}