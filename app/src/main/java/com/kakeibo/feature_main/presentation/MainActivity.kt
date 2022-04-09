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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakeibo.BuildConfig
import com.kakeibo.R
import com.kakeibo.feature_export.DriveServiceHelper
import com.kakeibo.feature_main.presentation.common.FirebaseViewModel
import com.kakeibo.feature_main.presentation.common.components.DrawerContent
import com.kakeibo.feature_main.presentation.common.components.ImportExportDialog
import com.kakeibo.feature_main.presentation.common.components.TopNavigationBar
import com.kakeibo.feature_main.presentation.item_detail.item_edit.components.ItemEditScreen
import com.kakeibo.feature_main.presentation.item_main.item_chart.components.ItemChartScreen
import com.kakeibo.feature_main.presentation.item_detail.item_input.components.ItemInputScreen
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.item_calendar.components.ItemCalendarScreen
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ItemListScreen
import com.kakeibo.feature_main.presentation.item_search.components.ItemSearchScreen
import com.kakeibo.feature_main.presentation.nav_drawer.components.AboutScreen
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.KakeiboTheme
import com.kakeibo.util.UtilFiles
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


//todo typography
//todo input : accept comma and period


@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var interstitialAd: InterstitialAd? = null

    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private val itemMainViewModel: ItemMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

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
                Log.e("MainActivity", "Failed to lead ad")
            }
        })

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
                "Error Occurred. Please contact developer: ${response?.error?.errorCode}",
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
        Log.d("MainActivity", "Google sign in luancher!")
        if (result.resultCode == RESULT_OK) {
            Log.d("MainActivity", "Google sign in luancher!   111")
            try {
                Log.d("MainActivity", "Google sign in luancher!   222")
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                task.getResult(ApiException::class.java).also {
                    triggerSignIn()
                    connectToGDrive(it)
                }
            }
            catch (e: ApiException) {
                Log.w("MainActivity", "Google sign in failed", e)
            }
        }
    }

    private fun connectToGDrive(googleAccount: GoogleSignInAccount) {
        Log.w("MainActivity", "Connecting to GDrive")
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

        DriveServiceHelper(googleDriveService).also {
            it.createFilePickerIntent().also {
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
                        else {
                            Log.e("MainActivity", "Error. Ads not shown.")
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
        startDestination = Screen.ItemListScreen.route + "?searchId={searchId}"
    ) {
        composable(
            route = Screen.ItemListScreen.route + "?searchId={searchId}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            val searchId = it.arguments?.getLong("searchId") ?: 0L
            ItemListScreen(navController = navController, viewModel = itemMainViewModel, searchId)
        }
        composable(
            route = Screen.ItemChartScreen.route + "?searchId={searchId}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) {
            ItemChartScreen(navController = navController, viewModel = itemMainViewModel)
        }
        composable(
            route = Screen.ItemCalendarScreen.route + "?searchId={searchId}",
            arguments = listOf(
                navArgument(
                    name = "searchId"
                ) {
                    type = NavType.LongType
                    defaultValue = 0L
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