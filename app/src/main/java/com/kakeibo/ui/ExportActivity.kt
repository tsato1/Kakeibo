package com.kakeibo.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
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
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.getTodaysDate
import com.kakeibo.util.UtilFiles.getFileValue
import java.io.IOException

class ExportActivity : AppCompatActivity() {
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var mOpenFileId: String? = null
    private var _context: Context? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mStrDateFormat: String? = null
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)
        _context = this
        mReportType = intent.getIntExtra("REPORT_VIEW_TYPE", 0)
        val dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        mStrDateFormat = when (dateFormat) {
            1 -> getTodaysDate(UtilDate.DATE_FORMAT_MDY) + " kk:mm:ss"
            2 -> getTodaysDate(UtilDate.DATE_FORMAT_DMY) + " kk:mm:ss"
            else -> getTodaysDate(UtilDate.DATE_FORMAT_YMD) + " kk:mm:ss"
        }
        requestSignIn()
        /*** ads  */
        MobileAds.initialize(this) { }
        mInterstitialAd = InterstitialAd(this)
        if (BuildConfig.DEBUG) {
            mInterstitialAd!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            /*** in debug mode  */
        } else {
            mInterstitialAd!!.adUnitId = getString(R.string.main_banner_ad)
        }
        mInterstitialAd!!.loadAd(AdRequest.Builder().build())
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == RESULT_OK && resultData != null) {
                handleSignInResult(resultData)
            }
            REQUEST_CODE_OPEN_DOCUMENT -> {
                if (resultCode == RESULT_OK && resultData != null) {
                    val uri = resultData.data
                    if (uri != null) {
//                        openFileFromFilePicker(uri);
                        try {
                            val os = contentResolver.openOutputStream(uri)
                            if (os != null) {
                                val content: String? = if (mReportType == TabFragment2.REPORT_BY_DATE) {
                                    getFileValue(FILE_ORDER_DATE, _context!!)
                                } else if (mReportType == TabFragment2.REPORT_BY_CATEGORY) {
                                    getFileValue(FILE_ORDER_CATEGORY, _context!!)
                                } else {
                                    "Empty Report"
                                }
                                os.write(content!!.toByteArray())
                                os.close()
                                /*** ads  */
                                if (mInterstitialAd!!.isLoaded) {
                                    mInterstitialAd!!.show()
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

    /**
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
        startActivityForResult(mGoogleSignInClient!!.getSignInIntent(), REQUEST_CODE_SIGN_IN)
    }

    private fun requestSignOut() {
        /*** signout from firebase  */
        if (mFirebaseAuth != null) mFirebaseAuth!!.signOut()
        /*** signout from googleSignInClient  */
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient!!.signOut()
                    .addOnCompleteListener(this) { }
        }
    }

    /**
     * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
     */
    private fun handleSignInResult(result: Intent) {
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
                    /*** firebase login  */
                    val authCredential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                    mFirebaseAuth!!.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    val user = mFirebaseAuth!!.currentUser
                                    if (user != null)
                                        Log.d(TAG, "Firebase user.getDisplayName()= " + user.displayName)
                                } else {
                                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    finish()
                                }
                            }
                    openFilePicker()
                }
                .addOnFailureListener { exception: Exception? ->
                    Log.e(TAG, "Unable to sign in.", exception) }
    }

    /**
     * Opens the Storage Access Framework file picker using [.REQUEST_CODE_OPEN_DOCUMENT].
     */
    private fun openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.")
            val pickerIntent = mDriveServiceHelper!!.createFilePickerIntent()

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT)
        }
    }

    /**
     * Opens a file from its `uri` returned from the Storage Access Framework file picker
     * initiated by [.openFilePicker].
     */
    private fun openFileFromFilePicker(uri: Uri) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening " + uri.path)
            mDriveServiceHelper!!.openFileUsingStorageAccessFramework(contentResolver, uri)
                    .addOnSuccessListener { nameAndContent: Pair<String?, String?>? ->
                        setReadOnlyMode()
                        createFile() }
                    .addOnFailureListener { exception: Exception?
                        -> Log.e(TAG, "Unable to open file from picker.", exception) }
        }
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private fun createFile() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.")
            mDriveServiceHelper!!.createFile(getTodaysDate(mStrDateFormat!!), mReportType, _context)
                    .addOnSuccessListener { fileId: String? ->
//                        readFile(fileId);
                        showMessage(getString(R.string.file_created))
                        /*** ads  */
                        if (mInterstitialAd!!.isLoaded) {
                            mInterstitialAd!!.show()
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.")
                        }
                        finish()
                    }
                    .addOnFailureListener { exception: Exception? ->
                        Log.e(TAG, "Couldn't create file.", exception)
                        finish()
                    }
        }
    }

    /**
     * Retrieves the title and content of a file identified by `fileId` and populates the UI.
     */
    private fun readFile(fileId: String) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Reading file $fileId")
            mDriveServiceHelper!!.readFile(fileId)
                    .addOnSuccessListener { nameAndContent: Pair<String, String> ->
                        setReadWriteMode(fileId) }
                    .addOnFailureListener { exception: Exception? ->
                        Log.e(TAG, "Couldn't read file.", exception) }
        }
    }

    /**
     * Saves the currently opened file created via [.createFile] if one exists.
     */
    private fun saveFile() {
        if (mDriveServiceHelper != null && mOpenFileId != null) {
            val fileName = "fileName"
            val fileContent = "fileContent"
            mDriveServiceHelper!!.saveFile(mOpenFileId, fileName, fileContent)
                    .addOnFailureListener { exception: Exception? ->
                        Log.e(TAG, "Unable to save file via REST.", exception) }
        }
    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private fun query() {
        if (mDriveServiceHelper != null) {
            mDriveServiceHelper!!.queryFiles()
                    .addOnSuccessListener { fileList: FileList ->
                        val builder = StringBuilder()
                        for (file in fileList.files) {
                            builder.append(file.name).append("\n")
                        }
                        val fileNames = builder.toString()
                        // todo should be disposable
//                        mFileTitleEditText.setText("File List");
//                        mDocContentEditText.setText(fileNames);
                        setReadOnlyMode()
                    }
                    .addOnFailureListener { exception: Exception? ->
                        Log.e(TAG, "Unable to query files.", exception) }
        }
    }

    /**
     * Updates the UI to read-only mode.
     */
    private fun setReadOnlyMode() {
        mOpenFileId = null
    }

    /**
     * Updates the UI to read/write mode on the document identified by `fileId`.
     */
    private fun setReadWriteMode(fileId: String) {
        mOpenFileId = fileId
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        requestSignOut()
    }

    protected fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun screenTapped(view: View?) {
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
        const val FILE_ORDER_DATE = "tmp_order_date.csv"
        const val FILE_ORDER_CATEGORY = "tmp_order_cat.csv"
        private const val REQUEST_CODE_SIGN_IN = 1
        private const val REQUEST_CODE_OPEN_DOCUMENT = 2
        private var mReportType = 0
    }
}