//package com.kakeibo.export;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.RequestConfiguration;
//import com.google.android.gms.drive.DriveContents;
//import com.google.android.gms.drive.DriveFolder;
//import com.google.android.gms.drive.MetadataChangeSet;
//
//import com.kakeibo.BuildConfig;
//import com.kakeibo.R;
//import com.kakeibo.SubApp;
//import com.kakeibo.util.UtilDate;
//import com.kakeibo.util.UtilFiles;
//
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//
//import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
//import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;
//import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE;
//
///**
// * An activity to create a file inside a folder.
// */
//public class CreateFileInFolderActivity extends BaseExportActivity {
//    private static final String TAG = CreateFileInFolderActivity.class.getSimpleName();
//
//    public static final String FILE_ORDER_DATE = "tmp_order_date.csv";
//    public static final String FILE_ORDER_CATEGORY = "tmp_order_cat.csv";
//
//    private static int REPORT_VIEW_TYPE;
//
//    private String mStrDateFormat;
//
//    /*** ads ***/
//    private InterstitialAd mInterstitialAd;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate() called");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_export);
//        /*** ads ***/
//        loadAds();
//
//        REPORT_VIEW_TYPE = getIntent().getIntExtra("REPORT_VIEW_TYPE", 0);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        loadSharedPreference();
//    }
//
//    @Override
//    protected void onDriveClientReady() {
//        Log.d(TAG, "onDriveClientReady()");
//        pickFolder()
//                .addOnSuccessListener(this,
//                        driveId -> createFileInFolder(driveId.asDriveFolder()))
//                .addOnFailureListener(this, e -> {
//                    Log.e(TAG, "No folder selected", e);
//                    showMessage(getString(R.string.folder_not_selected));
//                    finish();
//                });
//    }
//
//    private void createFileInFolder(final DriveFolder parent) {
//        Log.d(TAG, "createFileInFolder()");
//        getDriveResourceClient()
//                .createContents()
//                .continueWithTask(task -> {
//                    DriveContents contents = task.getResult();
//                    OutputStream outputStream = contents.getOutputStream();
//
//                    String str = "";
//                    if (REPORT_VIEW_TYPE == 0) {
//                        str = UtilFiles.getFileValue(FILE_ORDER_DATE, this);
//                    } else if (REPORT_VIEW_TYPE == 1) {
//                        str = UtilFiles.getFileValue(FILE_ORDER_CATEGORY, this);
//                    }
//
//                    try (Writer writer = new OutputStreamWriter(outputStream)) {
//                        writer.write(str);
//                    } catch (NullPointerException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, R.string.empty_report, Toast.LENGTH_LONG).show();
//                    }
//
//                    String title = "Kakeibo_Export_" + UtilDate.getTodaysDate(mStrDateFormat);
//
//                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                            .setTitle(title)
//                            .setMimeType("text/csv")
//                            .setStarred(true)
//                            .build();
//
//                    return getDriveResourceClient().createFile(parent, changeSet, contents);
//                })
//                .addOnSuccessListener(aVoid -> {
//                    showMessage(getString(R.string.file_created));
//                    /*** ads ***/
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                        Log.d(TAG, "The interstitial wasn't loaded yet.");
//                    }
//                })
//                .addOnFailureListener(this, e -> {
//                    Log.e(TAG, "Unable to create file", e);
//                    showMessage(getString(R.string.file_create_error));
//                });
//
//        finish();
//    }
//
//    public void loadSharedPreference() {
//        int dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format);
//
//        switch (dateFormat) {
//            case 1: // MDY
//                mStrDateFormat = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_MDY) + " kk:mm:ss";
//                break;
//            case 2: // DMY
//                mStrDateFormat = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DMY) + " kk:mm:ss";
//                break;
//            default:  // YMD
//                mStrDateFormat = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_YMD) + " kk:mm:ss";
//        }
//    }
//
//    /*** ads ***/
//    private void loadAds() {
//        RequestConfiguration conf= new RequestConfiguration
//                .Builder()
//                .setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
//                .setTagForUnderAgeOfConsent(TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
//                .setMaxAdContentRating(MAX_AD_CONTENT_RATING_G)
//                .build();
//        MobileAds.setRequestConfiguration(conf);
//
//        MobileAds.initialize(this, getString(R.string.admob_app_id));
//        mInterstitialAd = new InterstitialAd(this);
//        AdRequest.Builder request = new AdRequest.Builder();
//
//        if (BuildConfig.DEBUG) {
//            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); /*** in debug mode ***/
//        } else {
//            mInterstitialAd.setAdUnitId(getString(R.string.upload_ad));
//        }
//
//        mInterstitialAd.loadAd(request.build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//            @Override
//            public void onAdLoaded() {
//            }
//        });
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop() called");
//    }
//
//    public void screenTapped(View view) {
//        finish();
//    }
//}