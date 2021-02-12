//package com.kakeibo.export
//
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import com.google.android.gms.ads.*
//import com.google.android.gms.drive.*
//import com.google.android.gms.tasks.Task
//import com.kakeibo.BuildConfig
//import com.kakeibo.R
//import com.kakeibo.SubApp
//import com.kakeibo.util.UtilDate
//import com.kakeibo.util.UtilDate.getTodaysDate
//import com.kakeibo.util.UtilFiles.getFileValue
//import java.io.OutputStreamWriter
//
///**
// * An activity to create a file inside a folder.
// */
//class CreateFileInFolderActivity : BaseExportActivity() {
//    private var mStrDateFormat: String? = null
//
//    /*** ads  */
//    private var mInterstitialAd: InterstitialAd? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d(TAG, "onCreate() called")
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_export)
//        /*** ads  */
//        loadAds()
//        REPORT_VIEW_TYPE = intent.getIntExtra("REPORT_VIEW_TYPE", 0)
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        loadSharedPreference()
//    }
//
//    override fun onDriveClientReady() {
//        Log.d(TAG, "onDriveClientReady()")
//        pickFolder()
//                .addOnSuccessListener(this
//                ) { driveId: DriveId -> createFileInFolder(driveId.asDriveFolder()) }
//                .addOnFailureListener(this) { e: Exception? ->
//                    Log.e(TAG, "No folder selected", e)
//                    showMessage(getString(R.string.folder_not_selected))
//                    finish()
//                }
//    }
//
//    private fun createFileInFolder(parent: DriveFolder) {
//        Log.d(TAG, "createFileInFolder()")
//        driveResourceClient!!
//                .createContents()
//                .continueWithTask { task: Task<DriveContents> ->
//                    val contents = task.result
//                    val outputStream = contents.outputStream
//                    var str: String? = ""
//                    if (REPORT_VIEW_TYPE == 0) {
//                        str = getFileValue(FILE_ORDER_DATE, this)
//                    } else if (REPORT_VIEW_TYPE == 1) {
//                        str = getFileValue(FILE_ORDER_CATEGORY, this)
//                    }
//                    try {
//                        OutputStreamWriter(outputStream).use { writer -> writer.write(str) }
//                    } catch (e: NullPointerException) {
//                        e.printStackTrace()
//                        Toast.makeText(this, R.string.empty_report, Toast.LENGTH_LONG).show()
//                    }
//                    val title = "Kakeibo_Export_" + getTodaysDate(mStrDateFormat)
//                    val changeSet = MetadataChangeSet.Builder()
//                            .setTitle(title)
//                            .setMimeType("text/csv")
//                            .setStarred(true)
//                            .build()
//                    driveResourceClient!!.createFile(parent, changeSet, contents)
//                }
//                .addOnSuccessListener { aVoid: DriveFile? ->
//                    showMessage(getString(R.string.file_created))
//                    /*** ads  */
//                    if (mInterstitialAd!!.isLoaded) {
//                        mInterstitialAd!!.show()
//                    } else {
//                        Log.d(TAG, "The interstitial wasn't loaded yet.")
//                    }
//                }
//                .addOnFailureListener(this) { e: Exception? ->
//                    Log.e(TAG, "Unable to create file", e)
//                    showMessage(getString(R.string.file_create_error))
//                }
//        finish()
//    }
//
//    fun loadSharedPreference() {
//        val dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
//        mStrDateFormat = when (dateFormat) {
//            1 -> getTodaysDate(UtilDate.DATE_FORMAT_MDY) + " kk:mm:ss"
//            2 -> getTodaysDate(UtilDate.DATE_FORMAT_DMY) + " kk:mm:ss"
//            else -> getTodaysDate(UtilDate.DATE_FORMAT_YMD) + " kk:mm:ss"
//        }
//    }
//
//    /*** ads  */
//    private fun loadAds() {
//        val conf = RequestConfiguration.Builder()
//                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
//                .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
//                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
//                .build()
//        MobileAds.setRequestConfiguration(conf)
//        MobileAds.initialize(this, getString(R.string.admob_app_id))
//        mInterstitialAd = InterstitialAd(this)
//        val request = AdRequest.Builder()
//        if (BuildConfig.DEBUG) {
//            mInterstitialAd!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
//            /*** in debug mode  */
//        } else {
//            mInterstitialAd!!.adUnitId = getString(R.string.upload_ad)
//        }
//        mInterstitialAd!!.loadAd(request.build())
//        mInterstitialAd!!.adListener = object : AdListener() {
//            override fun onAdClosed() {
//                mInterstitialAd!!.loadAd(AdRequest.Builder().build())
//            }
//
//            override fun onAdLoaded() {}
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        Log.d(TAG, "onStop() called")
//    }
//
//    fun screenTapped(view: View?) {
//        finish()
//    }
//
//    companion object {
//        private val TAG = CreateFileInFolderActivity::class.java.simpleName
//        const val FILE_ORDER_DATE = "tmp_order_date.csv"
//        const val FILE_ORDER_CATEGORY = "tmp_order_cat.csv"
//        private var REPORT_VIEW_TYPE = 0
//    }
//}