package com.kakeibo.export;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import com.kakeibo.R;
import com.kakeibo.Util;
import com.kakeibo.settings.SettingsActivity;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to create a file inside a folder.
 */
public class CreateFileInFolderActivity extends BaseExportActivity {
    private static final String TAG = CreateFileInFolderActivity.class.getSimpleName();

    public static final String TMP_FILE_ORDER_DATE = "tmp_order_date.csv";
    public static final String TMP_FILE_ORDER_CATEGORY = "tmp_order_cat.csv";

    private int mIntDateFormat;
    private String mStrDateFormat;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPreference();
        loadAds();
    }

    @Override
    protected void onDriveClientReady() {
        Log.d(TAG, "onDriveClientReady()");
        pickFolder()
                .addOnSuccessListener(this,
                        driveId -> createFileInFolder(driveId.asDriveFolder()))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "No folder selected", e);
                    showMessage(getString(R.string.folder_not_selected));
                    finish();
                });
    }

    private void createFileInFolder(final DriveFolder parent) {
        Log.d(TAG, "createFileInFolder()");
        getDriveResourceClient()
                .createContents()
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    String str = UtilFiles.getFileValue(TMP_FILE_ORDER_DATE, this);

                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(str);
                    }

                    String title = "Kakeibo_Export_" + Util.getTodaysDate(mStrDateFormat);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title)
                            .setMimeType("text/csv")
                            .setStarred(true)
                            .build();

                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d(TAG, "The interstitial wasn't loaded yet.");
                    }

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> showMessage(getString(R.string.file_created)))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                });

        finish();
    }

    public void loadSharedPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String f = pref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Util.DATE_FORMAT_YMD);
        mIntDateFormat = Integer.parseInt(f);

        switch (mIntDateFormat) {
            case 1: // MDY
                mStrDateFormat = Util.getTodaysDate(Util.DATE_FORMAT_MDY) + " kk:mm:ss";
                break;
            case 2: // DMY
                mStrDateFormat = Util.getTodaysDate(Util.DATE_FORMAT_DMY) + " kk:mm:ss";
                break;
            default:  // YMD
                mStrDateFormat = Util.getTodaysDate(Util.DATE_FORMAT_YMD) + " kk:mm:ss";
        }
    }

    private void loadAds() {
        MobileAds.initialize(this, "ca-app-pub-3282892636336089~3692682630");
        mInterstitialAd = new InterstitialAd(this);
        AdRequest.Builder request = new AdRequest.Builder();

        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); /*** in debug mode ***/
        //mInterstitialAd.setAdUnitId("ca-app-pub-3282892636336089/5503106683");
        mInterstitialAd.loadAd(request.build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
}