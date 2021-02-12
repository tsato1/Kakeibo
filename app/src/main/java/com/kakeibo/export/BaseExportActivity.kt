//package com.kakeibo.export
//
//import android.app.Activity
//import android.content.Intent
//import android.util.Log
//import android.widget.Toast
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.drive.*
//import com.google.android.gms.drive.query.Filters
//import com.google.android.gms.drive.query.SearchableField
//import com.google.android.gms.tasks.Task
//import com.google.android.gms.tasks.TaskCompletionSource
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import com.kakeibo.R
//
///**
// * An abstract activity that handles authorization and connection to the Drive
// * services.
// */
//abstract class BaseExportActivity : Activity() {
//    /**
//     * Handles high-level drive functions like sync
//     */
//    protected var driveClient: DriveClient? = null
//        private set
//
//    /**
//     * Handle access to Drive resources/files.
//     */
//    protected var driveResourceClient: DriveResourceClient? = null
//        private set
//
//    /**
//     * Tracks completion of the drive picker
//     */
//    private var mOpenItemTaskSource: TaskCompletionSource<DriveId>? = null
//
//    /**
//     *
//     */
//    private var mGoogleSignInClient: GoogleSignInClient? = null
//    private var mAuth: FirebaseAuth? = null
//    override fun onStart() {
//        super.onStart()
//        Log.d(TAG, "onStart()")
//        signIn()
//    }
//
//    /**
//     * Starts the sign-in process and initializes the Drive client.
//     */
//    protected fun signIn() {
//        Log.d(TAG, "signIn()")
//        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .requestScopes(Drive.SCOPE_FILE)
//                .requestScopes(Drive.SCOPE_APPFOLDER)
//                .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions)
//        mAuth = FirebaseAuth.getInstance()
//        startActivityForResult(mGoogleSignInClient!!.getSignInIntent(), REQUEST_CODE_SIGN_IN)
//    }
//
//    private fun signOut() {
//        if (mAuth != null) mAuth!!.signOut()
//        if (mGoogleSignInClient != null) {
//            mGoogleSignInClient!!.signOut()
//                    .addOnCompleteListener(this) { }
//        }
//    }
//
//    private fun revokeAccess() {
//        mGoogleSignInClient!!.revokeAccess()
//                .addOnCompleteListener(this) { }
//    }
//
//    /**
//     * Handles resolution callbacks.
//     */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d(TAG, "onActivityResult()")
//        when (requestCode) {
//            REQUEST_CODE_SIGN_IN -> {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Sign-in successful")
//                    try {
//                        val account = task.getResult(ApiException::class.java)
//                        initializeDriveClient(account)
//                    } catch (e: ApiException) {
//                        // Google Sign In failed, update UI appropriately
//                        Log.w(TAG, "Google sign in failed", e)
//                    }
//                } else {
//                    Log.w(TAG, "Sign-in failed. 2")
//                }
//            }
//            REQUEST_CODE_OPEN_ITEM -> if (resultCode == RESULT_OK) {
//                Log.e(TAG, "Result_OK")
//                val driveId: DriveId = data.getParcelableExtra(
//                        OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID)
//                mOpenItemTaskSource!!.setResult(driveId)
//            } else {
//                mOpenItemTaskSource!!.setException(RuntimeException("Unable to open file"))
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    /**
//     * Continues the sign-in process, initializing the Drive clients with the current
//     * user's account.
//     */
//    private fun initializeDriveClient(signInAccount: GoogleSignInAccount) {
//        Log.d(TAG, "initializeDriveClient()")
//        driveClient = Drive.getDriveClient(applicationContext, signInAccount)
//        driveResourceClient = Drive.getDriveResourceClient(applicationContext, signInAccount)
//        onDriveClientReady()
//        Log.d(TAG, "firebaseAuthWithGoogle:" + signInAccount.id)
//        val credential = GoogleAuthProvider.getCredential(signInAccount.idToken, null)
//        mAuth!!.signInWithCredential(credential)
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d(TAG, "signInWithCredential:success")
//                        val user = mAuth!!.currentUser
//                    } else {
//                        Log.w(TAG, "signInWithCredential:failure", task.exception)
//                        showMessage("Firebase signInWithCredential:failure")
//                        finish()
//                    }
//                }
//    }
//
//    /**
//     * Prompts the user to select a text file using OpenFileActivity.
//     *
//     * @return Task that resolves with the selected item's ID.
//     */
//    protected fun pickTextFile(): Task<DriveId> {
//        val openOptions = OpenFileActivityOptions.Builder()
//                .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
//                .setActivityTitle(getString(R.string.select_file))
//                .build()
//        return pickItem(openOptions)
//    }
//
//    /**
//     * Prompts the user to select a folder using OpenFileActivity.
//     *
//     * @return Task that resolves with the selected item's ID.
//     */
//    protected fun pickFolder(): Task<DriveId> {
//        Log.d(TAG, "pickFolder()")
//        val openOptions = OpenFileActivityOptions.Builder()
//                .setSelectionFilter(
//                        Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
//                .setActivityTitle(getString(R.string.select_folder))
//                .build()
//        return pickItem(openOptions)
//    }
//
//    /**
//     * Prompts the user to select a folder using OpenFileActivity.
//     *
//     * @param openOptions Filter that should be applied to the selection
//     * @return Task that resolves with the selected item's ID.
//     */
//    private fun pickItem(openOptions: OpenFileActivityOptions): Task<DriveId> {
//        Log.d(TAG, "pickItem()")
//        mOpenItemTaskSource = TaskCompletionSource()
//        driveClient!!
//                .newOpenFileActivityIntentSender(openOptions)
//                .continueWith<Void> { task ->
//                    startIntentSenderForResult(
//                            task.result, REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0)
//                    null
//                }
//        return mOpenItemTaskSource!!.task
//    }
//
//    /**
//     * Shows a toast message.
//     */
//    protected fun showMessage(message: String?) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
//
//    /**
//     * Called after the user has signed in and the Drive client has been initialized.
//     */
//    protected abstract fun onDriveClientReady()
//    override fun onStop() {
//        super.onStop()
//        Log.d(TAG, "onStop() called")
//        signOut()
//    }
//
//    companion object {
//        val TAG = BaseExportActivity::class.java.simpleName
//
//        /**
//         * Request code for google sign-in
//         */
//        protected const val REQUEST_CODE_SIGN_IN = 0
//
//        /**
//         * Request code for the Drive picker
//         */
//        protected const val REQUEST_CODE_OPEN_ITEM = 1
//    }
//}