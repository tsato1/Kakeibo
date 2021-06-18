package com.kakeibo.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
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
import com.google.firebase.auth.GoogleAuthProvider
import com.kakeibo.BuildConfig
import com.kakeibo.R
import com.kakeibo.ui.export.DriveServiceHelper
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.viewmodel.FirebaseUserViewModel
import com.kakeibo.util.UtilFiles
import java.io.IOException

class ExportActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ExportActivity"
        const val FILE_ORDER_DATE = "tmp_order_date.csv"
        const val FILE_ORDER_CATEGORY = "tmp_order_cat.csv"
        private var mReportType = 0
    }

    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var _interstitialAd: InterstitialAd? = null

    private val _authenticationViewModel: FirebaseUserViewModel by viewModels()

    val _requestSignIn = registerForActivityResult (
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }
    val _requestOpenDocument = registerForActivityResult (
        ActivityResultContracts.StartActivityForResult()
    ) {result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                try {
                    val os = contentResolver.openOutputStream(it)
                    if (os != null) {
                        val content: String? = when (mReportType) {
                            Medium.FRAGMENT_REPORT_DATE_MONTHLY -> {
                                UtilFiles.getFileValue(FILE_ORDER_DATE, this)
                            }
                            Medium.FRAGMENT_REPORT_CATEGORY_MONTHLY -> {
                                UtilFiles.getFileValue(FILE_ORDER_CATEGORY, this)
                            }
                            else -> {
                                "Empty Report"
                            }
                        }
                        os.write(content!!.toByteArray())
                        os.close()

                        /* ads */
                        if (_interstitialAd != null) {
                            _interstitialAd!!.show(this)
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.")
                        }

                        Toast.makeText(this, R.string.file_created, Toast.LENGTH_LONG).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)
        mReportType = intent.getIntExtra("REPORT_VIEW_TYPE", 0)

        _authenticationViewModel.firebaseUser.observe(this, {
            if (it == null) {
                requestSignIn()
            } else {
                val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
                googleAccount?.let {
                    connectToGDrive(googleAccount)
                }
            }
        })

        /* ads */
        val adRequest = AdRequest.Builder().build()
        var adUnitId = getString(R.string.upload_ad)
        if (BuildConfig.DEBUG) { /* in debug mode */
            adUnitId = "ca-app-pub-3940256099942544/1033173712"
        }
        InterstitialAd.load(this, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                _interstitialAd = interstitialAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                _interstitialAd = null
            }
        })
    }

    /*
     * Starts a sign-in activity using [.REQUEST_CODE_SIGN_IN].
     */
    private fun requestSignIn() {
        Log.d(TAG, "Requesting sign-in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient?.let {
            _requestSignIn.launch(it.signInIntent)
        }
    }

    /*
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val googleAccount = completedTask.getResult(ApiException::class.java)
            googleAccount?.let {
                connectToGDrive(googleAccount)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Google Sign-in failed. Error: $e", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun connectToGDrive(googleAccount: GoogleSignInAccount) {
        // Signed in successfully, show authenticated UI.
        // Use the authenticated account to sign in to the Drive service.
        val credential = GoogleAccountCredential.usingOAuth2(this, setOf(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = googleAccount.account
        val googleDriveService = Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential)
            .setApplicationName("Drive API Migration")
            .build()

        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
        // Its instantiation is required before handling any onClick actions.
        mDriveServiceHelper = DriveServiceHelper(googleDriveService)
        /* firebase login  */
        val authCredential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        mFirebaseAuth?.let {
            it.signInWithCredential(authCredential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = it.currentUser
                    if (user != null) Log.d(TAG, "Firebase user.getDisplayName()= " + user.displayName)

                    _authenticationViewModel.updateFirebaseUser()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "signInWithCredential:failure", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
        openFilePicker()
    }

    /*
     * Opens the Storage Access Framework file picker using [.REQUEST_CODE_OPEN_DOCUMENT].
     */
    private fun openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.")
            val pickerIntent = mDriveServiceHelper?.createFilePickerIntent()

            // The result of the SAF Intent is handled in onActivityResult.
            _requestOpenDocument.launch(pickerIntent)
        }
    }

    fun screenTapped(view: View?) {
        finish()
    }
}