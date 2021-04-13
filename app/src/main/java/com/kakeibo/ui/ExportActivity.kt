package com.kakeibo.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
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
import com.google.api.services.drive.model.FileList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakeibo.BuildConfig
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.ui.export.DriveServiceHelper
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.viewmodel.FirebaseUserViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.getTodaysDate
import com.kakeibo.util.UtilFiles
import java.io.IOException


class ExportActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ExportActivity"
        const val FILE_ORDER_DATE = "tmp_order_date.csv"
        const val FILE_ORDER_CATEGORY = "tmp_order_cat.csv"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
        private var mReportType = 0
    }

    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var mOpenFileId: String? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var _interstitialAd: InterstitialAd? = null

    private val _authenticationViewModel: FirebaseUserViewModel by viewModels()

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
                    a(googleAccount)
                }
            }
        })

        /* ads */
        val adRequest = AdRequest.Builder().build()
        var adUnitId = getString(R.string.main_banner_ad)
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
            startActivityForResult(it.signInIntent, REQUEST_CODE_SIGN_IN)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == RESULT_OK && resultData != null) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(resultData)
                handleSignInResult(task)
            }
            REQUEST_CODE_OPEN_DOCUMENT -> {
                if (resultCode == RESULT_OK && resultData != null) {
                    val uri = resultData.data
                    if (uri != null) {
                        try {
                            val os = contentResolver.openOutputStream(uri)
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
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    /*
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val googleAccount = completedTask.getResult(ApiException::class.java)
            a(googleAccount)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Google Sign-in failed. Error: $e", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun a(googleAccount: GoogleSignInAccount) {
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

    private fun handleSignInResult1(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                    Log.d(TAG, "Signed in as " + googleAccount.email)

                    // Use the authenticated account to sign in to the Drive service.
                    val credential = GoogleAccountCredential.usingOAuth2(
                            this, setOf(DriveScopes.DRIVE_FILE))
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
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "Unable to sign in.", exception)
                }
    }

    /*
     * Opens the Storage Access Framework file picker using [.REQUEST_CODE_OPEN_DOCUMENT].
     */
    private fun openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.")
            val pickerIntent = mDriveServiceHelper?.createFilePickerIntent()

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT)
        }
    }

    /*
     * Opens a file from its `uri` returned from the Storage Access Framework file picker
     * initiated by [.openFilePicker].
     */
    private fun openFileFromFilePicker(uri: Uri) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening " + uri.path)
            mDriveServiceHelper?.let {
                it.openFileUsingStorageAccessFramework(contentResolver, uri)
                        .addOnSuccessListener {
                            setReadOnlyMode()
                            createFile()
                        }
                        .addOnFailureListener { exception: Exception? ->
                            Log.e(TAG, "Unable to open file from picker.", exception)
                        }
            }
        }
    }

    /*
     * Creates a new file via the Drive REST API.
     */
    private fun createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.")
            val strDateFormat = when (SubApp.getDateFormat(R.string.pref_key_date_format)) {
                1 -> getTodaysDate(UtilDate.DATE_FORMAT_MDY) + " kk:mm:ss"
                2 -> getTodaysDate(UtilDate.DATE_FORMAT_DMY) + " kk:mm:ss"
                else -> getTodaysDate(UtilDate.DATE_FORMAT_YMD) + " kk:mm:ss"
            }
            val fileName = "Kakeibo_Export_${getTodaysDate(strDateFormat)}"
            mDriveServiceHelper?.let {
                it.createFile(fileName, mReportType, this)
                        .addOnSuccessListener { fileId: String? ->
                            Toast.makeText(this, R.string.file_created, Toast.LENGTH_LONG).show()
                            /* ads  */
                            _interstitialAd?.let {
                                _interstitialAd!!.show(this)
                                UtilFiles.deleteFile(fileName, this)
                            }
                            finish()
                        }
                        .addOnFailureListener { exception: Exception? ->
                            Log.e(TAG, "Couldn't create file.", exception)
                            Toast.makeText(this, "Couldn't create file.", Toast.LENGTH_LONG).show()
                            finish()
                        }
            }
        }
    }

    /*
     * Retrieves the title and content of a file identified by `fileId` and populates the UI.
     */
    private fun readFile(fileId: String) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Reading file $fileId")
            mDriveServiceHelper?.let {
                it.readFile(fileId)
                        .addOnSuccessListener { nameAndContent: Pair<String, String> ->
                            setReadWriteMode(fileId) }
                        .addOnFailureListener { exception: Exception? ->
                            Log.e(TAG, "Couldn't read file.", exception) }
            }
        }
    }

    /*
     * Saves the currently opened file created via [.createFile] if one exists.
     */
    private fun saveFile() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            val fileName = "fileName"
            val fileContent = "fileContent"
            mDriveServiceHelper?.let {
                it.saveFile(mOpenFileId, fileName, fileContent)
                        .addOnFailureListener { exception: Exception? ->
                            Log.e(TAG, "Unable to save file via REST.", exception) }
            }
        }
    }

    /*
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private fun query() {
        if (mDriveServiceHelper != null) {
            mDriveServiceHelper?.let {
                it.queryFiles()
                        .addOnSuccessListener { fileList: FileList ->
                            val builder = StringBuilder()
                            for (file in fileList.files) {
                                builder.append(file.name).append("\n")
                            }
                            val fileNames = builder.toString()
                            setReadOnlyMode()
                        }
                        .addOnFailureListener { exception: Exception? ->
                            Log.e(TAG, "Unable to query files.", exception) }
            }
        }
    }

    /*
     * Updates the UI to read-only mode.
     */
    private fun setReadOnlyMode() {
        mOpenFileId = null
    }

    /*
     * Updates the UI to read/write mode on the document identified by `fileId`.
     */
    private fun setReadWriteMode(fileId: String) {
        mOpenFileId = fileId
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
//        requestSignOut()
    }

    private fun requestSignOut() {
        /* signout from firebase */
        mFirebaseAuth?.signOut()
        /* signout from googleSignInClient */
        mGoogleSignInClient?.let { it.signOut().addOnCompleteListener(this) { } }
    }

    fun screenTapped(view: View?) {
        finish()
    }
}