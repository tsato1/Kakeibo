package com.kakeibo.export;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

    public static final String TMP_FILE_NAME = "tmp.csv";

    private int mIntDateFormat;
    private String mStrDateFormat;

    @Override
    public void onResume() {
        super.onResume();
        loadSharedPreference();
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
                    String str = UtilFiles.getFileValue(TMP_FILE_NAME, this);

                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(str);
                    }

                    String title = "Kakeibo_Export_" + Util.getTodaysDate(mStrDateFormat);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title)
                            .setMimeType("text/csv")
                            .setStarred(true)
                            .build();

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
}