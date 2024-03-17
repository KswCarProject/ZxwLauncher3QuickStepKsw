package com.android.launcher3.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.provider.LauncherDbUtils;

public class GridBackupTable {
    private static final int ID_PROPERTY = -1;
    private static final String KEY_DB_VERSION = "rank";
    private static final String KEY_GRID_X_SIZE = "spanX";
    private static final String KEY_GRID_Y_SIZE = "spanY";
    private static final String KEY_HOTSEAT_SIZE = "screen";
    public static final int OPTION_REQUIRES_SANITIZATION = 1;
    private static final int STATE_NOT_FOUND = 0;
    private static final int STATE_RAW = 1;
    private static final int STATE_SANITIZED = 2;
    private static final String TAG = "GridBackupTable";
    private final Context mContext;
    private final SQLiteDatabase mDb;
    private final int mOldGridX;
    private final int mOldGridY;
    private final int mOldHotseatSize;
    private int mRestoredGridX;
    private int mRestoredGridY;
    private int mRestoredHotseatSize;

    private @interface BackupState {
    }

    public GridBackupTable(Context context, SQLiteDatabase sQLiteDatabase, int i, int i2, int i3) {
        this.mContext = context;
        this.mDb = sQLiteDatabase;
        this.mOldHotseatSize = i;
        this.mOldGridX = i2;
        this.mOldGridY = i3;
    }

    public void createCustomBackupTable(String str) {
        copyTable(this.mDb, LauncherSettings.Favorites.TABLE_NAME, str, UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getSerialNumberForUser(Process.myUserHandle()));
        encodeDBProperties(0);
    }

    public void restoreFromCustomBackupTable(String str, boolean z) {
        if (LauncherDbUtils.tableExists(this.mDb, str)) {
            copyTable(this.mDb, str, LauncherSettings.Favorites.TABLE_NAME, UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getSerialNumberForUser(Process.myUserHandle()));
            if (z) {
                LauncherDbUtils.dropTable(this.mDb, str);
            }
        }
    }

    private static void copyTable(SQLiteDatabase sQLiteDatabase, String str, String str2, long j) {
        LauncherDbUtils.dropTable(sQLiteDatabase, str2);
        LauncherSettings.Favorites.addTableToDb(sQLiteDatabase, j, false, str2);
        sQLiteDatabase.execSQL("INSERT INTO " + str2 + " SELECT * FROM " + str + " where _id > " + -1);
    }

    private void encodeDBProperties(int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", -1);
        contentValues.put("rank", Integer.valueOf(this.mDb.getVersion()));
        contentValues.put("spanX", Integer.valueOf(this.mOldGridX));
        contentValues.put("spanY", Integer.valueOf(this.mOldGridY));
        contentValues.put("screen", Integer.valueOf(this.mOldHotseatSize));
        contentValues.put(LauncherSettings.Favorites.OPTIONS, Integer.valueOf(i));
        this.mDb.insert(LauncherSettings.Favorites.BACKUP_TABLE_NAME, (String) null, contentValues);
    }

    public int loadDBProperties() {
        Cursor query = this.mDb.query(LauncherSettings.Favorites.BACKUP_TABLE_NAME, new String[]{"rank", "spanX", "spanY", "screen", LauncherSettings.Favorites.OPTIONS}, "_id=-1", (String[]) null, (String) null, (String) null, (String) null);
        try {
            boolean z = false;
            if (!query.moveToNext()) {
                Log.e(TAG, "Meta data not found in backup table");
                if (query != null) {
                    query.close();
                }
                return 0;
            } else if (!validateDBVersion(this.mDb.getVersion(), query.getInt(0))) {
                if (query != null) {
                    query.close();
                }
                return 0;
            } else {
                int i = 1;
                this.mRestoredGridX = query.getInt(1);
                this.mRestoredGridY = query.getInt(2);
                this.mRestoredHotseatSize = query.getInt(3);
                if ((query.getInt(4) & 1) == 0) {
                    z = true;
                }
                if (z) {
                    i = 2;
                }
                if (query != null) {
                    query.close();
                }
                return i;
            }
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public boolean restoreFromRawBackupIfAvailable(long j) {
        if (!LauncherDbUtils.tableExists(this.mDb, LauncherSettings.Favorites.BACKUP_TABLE_NAME) || loadDBProperties() != 1 || this.mOldHotseatSize != this.mRestoredHotseatSize || this.mOldGridX != this.mRestoredGridX || this.mOldGridY != this.mRestoredGridY) {
            return false;
        }
        copyTable(this.mDb, LauncherSettings.Favorites.BACKUP_TABLE_NAME, LauncherSettings.Favorites.TABLE_NAME, j);
        Log.d(TAG, "Backup restored");
        return true;
    }

    public void doBackup(long j, int i) {
        copyTable(this.mDb, LauncherSettings.Favorites.TABLE_NAME, LauncherSettings.Favorites.BACKUP_TABLE_NAME, j);
        encodeDBProperties(i);
    }

    private static boolean validateDBVersion(int i, int i2) {
        if (i == i2) {
            return true;
        }
        Log.e(TAG, String.format("Launcher.db version mismatch, expecting %d but %d was found", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        return false;
    }
}
