package com.kakeibo.export;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import com.kakeibo.R;
import com.kakeibo.Utilities;
import com.kakeibo.settings.SettingsActivity;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to create a file inside a folder.
 */
public class CreateFileInFolderActivity extends BaseExportActivity {
    private static final String TAG = CreateFileInFolderActivity.class.getSimpleName();

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
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write("Hello World!");
                    }

                    String title = "Kakeibo_Export_" + Utilities.getTodaysDate(mStrDateFormat);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(title)
                            .setMimeType("text/csv")
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> showMessage(getString(R.string.file_created,
                                driveFile.getDriveId().encodeToString())))
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage(getString(R.string.file_create_error));
                });
    }

    public void loadSharedPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String f = pref.getString(SettingsActivity.PREF_KEY_DATE_FORMAT, Utilities.DATE_FORMAT_YMD);
        mIntDateFormat = Integer.parseInt(f);

        switch (mIntDateFormat) {
            case 1: // MDY
                mStrDateFormat = Utilities.getTodaysDate(Utilities.DATE_FORMAT_MDY);
                break;
            case 2: // DMY
                mStrDateFormat = Utilities.getTodaysDate(Utilities.DATE_FORMAT_DMY);
                break;
            default:  // YMD
                mStrDateFormat = Utilities.getTodaysDate(Utilities.DATE_FORMAT_YMD);
        }
    }
}