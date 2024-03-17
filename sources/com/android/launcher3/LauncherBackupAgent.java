package com.android.launcher3;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.provider.RestoreDbTask;
import java.io.File;
import java.io.IOException;

public class LauncherBackupAgent extends BackupAgent {
    private static final String TAG = "LauncherBackupAgent";

    public void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) {
    }

    public void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) {
    }

    public void onCreate() {
        super.onCreate();
        FileLog.setDir(getFilesDir());
    }

    public void onRestoreFile(ParcelFileDescriptor parcelFileDescriptor, long j, File file, int i, long j2, long j3) throws IOException {
        if (file.delete()) {
            FileLog.d(TAG, "Removed obsolete file: " + file);
        }
        super.onRestoreFile(parcelFileDescriptor, j, file, i, j2, j3);
    }

    public void onRestoreFinished() {
        RestoreDbTask.setPending(this);
    }
}
