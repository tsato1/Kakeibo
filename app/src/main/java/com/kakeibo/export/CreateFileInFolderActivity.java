package com.kakeibo.export;

import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import com.kakeibo.R;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * An activity to create a file inside a folder.
 */
public class CreateFileInFolderActivity extends BaseExportActivity {
    private static final String TAG = CreateFileInFolderActivity.class.getSimpleName();

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

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("New file")
                            .setMimeType("text/plain")
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
}