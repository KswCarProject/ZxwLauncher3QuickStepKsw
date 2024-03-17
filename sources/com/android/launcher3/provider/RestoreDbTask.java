package com.android.launcher3.provider;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseLongArray;
import com.android.launcher3.AppWidgetsRestoredReceiver;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.DeviceGridState;
import com.android.launcher3.model.GridBackupTable;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.LogConfig;
import java.io.InvalidObjectException;
import java.util.Arrays;

public class RestoreDbTask {
    private static final String APPWIDGET_IDS = "appwidget_ids";
    private static final String APPWIDGET_OLD_IDS = "appwidget_old_ids";
    private static final String INFO_COLUMN_DEFAULT_VALUE = "dflt_value";
    private static final String INFO_COLUMN_NAME = "name";
    private static final String RESTORED_DEVICE_TYPE = "restored_task_pending";
    private static final String TAG = "RestoreDbTask";

    public static void restoreIfNeeded(Context context, LauncherProvider.DatabaseHelper databaseHelper) {
        if (isPending(context)) {
            if (!performRestore(context, databaseHelper)) {
                databaseHelper.createEmptyDB(databaseHelper.getWritableDatabase());
            }
            Utilities.getPrefs(context).edit().remove(RESTORED_DEVICE_TYPE).commit();
            InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).reinitializeAfterRestore(context);
        }
    }

    private static boolean performRestore(Context context, LauncherProvider.DatabaseHelper databaseHelper) {
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction;
        SQLiteDatabase writableDatabase = databaseHelper.getWritableDatabase();
        try {
            sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(writableDatabase);
            RestoreDbTask restoreDbTask = new RestoreDbTask();
            restoreDbTask.backupWorkspace(context, writableDatabase);
            restoreDbTask.sanitizeDB(context, databaseHelper, writableDatabase, new BackupManager(context));
            restoreDbTask.restoreAppWidgetIdsIfExists(context);
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return true;
        } catch (Exception e) {
            FileLog.e(TAG, "Failed to verify db", e);
            return false;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static boolean restoreIfPossible(Context context, LauncherProvider.DatabaseHelper databaseHelper, BackupManager backupManager) {
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction;
        SQLiteDatabase writableDatabase = databaseHelper.getWritableDatabase();
        try {
            sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(writableDatabase);
            new RestoreDbTask().restoreWorkspace(context, writableDatabase, databaseHelper, backupManager);
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return true;
        } catch (Exception e) {
            FileLog.e(TAG, "Failed to restore db", e);
            return false;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    private void backupWorkspace(Context context, SQLiteDatabase sQLiteDatabase) throws Exception {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        new GridBackupTable(context, sQLiteDatabase, idp.numDatabaseHotseatIcons, idp.numColumns, idp.numRows).doBackup(getDefaultProfileId(sQLiteDatabase), 1);
    }

    private void restoreWorkspace(Context context, SQLiteDatabase sQLiteDatabase, LauncherProvider.DatabaseHelper databaseHelper, BackupManager backupManager) throws Exception {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        if (new GridBackupTable(context, sQLiteDatabase, idp.numDatabaseHotseatIcons, idp.numColumns, idp.numRows).restoreFromRawBackupIfAvailable(getDefaultProfileId(sQLiteDatabase))) {
            int sanitizeDB = sanitizeDB(context, databaseHelper, sQLiteDatabase, backupManager);
            LauncherAppState.getInstance(context).getModel().forceReload();
            restoreAppWidgetIdsIfExists(context);
            if (sanitizeDB == 0) {
                LauncherDbUtils.dropTable(sQLiteDatabase, LauncherSettings.Favorites.BACKUP_TABLE_NAME);
            }
        }
    }

    private int sanitizeDB(Context context, LauncherProvider.DatabaseHelper databaseHelper, SQLiteDatabase sQLiteDatabase, BackupManager backupManager) throws Exception {
        SparseLongArray sparseLongArray;
        int i;
        SQLiteDatabase sQLiteDatabase2 = sQLiteDatabase;
        long defaultUserSerial = databaseHelper.getDefaultUserSerial();
        long defaultProfileId = getDefaultProfileId(sQLiteDatabase2);
        LongSparseArray<Long> managedProfileIds = getManagedProfileIds(sQLiteDatabase2, defaultProfileId);
        LongSparseArray longSparseArray = new LongSparseArray(managedProfileIds.size() + 1);
        longSparseArray.put(defaultProfileId, Long.valueOf(defaultUserSerial));
        for (int size = managedProfileIds.size() - 1; size >= 0; size--) {
            long keyAt = managedProfileIds.keyAt(size);
            UserHandle userForAncestralSerialNumber = getUserForAncestralSerialNumber(backupManager, keyAt);
            LauncherProvider.DatabaseHelper databaseHelper2 = databaseHelper;
            if (userForAncestralSerialNumber != null) {
                longSparseArray.put(keyAt, Long.valueOf(databaseHelper2.getSerialNumberForUser(userForAncestralSerialNumber)));
            }
        }
        int size2 = longSparseArray.size();
        String[] strArr = new String[size2];
        strArr[0] = Long.toString(defaultProfileId);
        for (int i2 = size2 - 1; i2 >= 1; i2--) {
            strArr[i2] = Long.toString(longSparseArray.keyAt(i2));
        }
        String[] strArr2 = new String[size2];
        Arrays.fill(strArr2, "?");
        int delete = sQLiteDatabase2.delete(LauncherSettings.Favorites.TABLE_NAME, "profileId NOT IN (" + TextUtils.join(", ", Arrays.asList(strArr2)) + ")", strArr);
        FileLog.d(TAG, delete + " items from unrestored user(s) were deleted");
        boolean isPropertyEnabled = Utilities.isPropertyEnabled(LogConfig.KEEP_ALL_ICONS);
        ContentValues contentValues = new ContentValues();
        contentValues.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf((isPropertyEnabled ? 4 : 0) | 1));
        sQLiteDatabase2.update(LauncherSettings.Favorites.TABLE_NAME, contentValues, (String) null, (String[]) null);
        contentValues.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf((isPropertyEnabled ? 8 : 0) | 7));
        sQLiteDatabase2.update(LauncherSettings.Favorites.TABLE_NAME, contentValues, "itemType = ?", new String[]{Integer.toString(4)});
        SparseLongArray sparseLongArray2 = new SparseLongArray(longSparseArray.size());
        int size3 = longSparseArray.size() - 1;
        int i3 = 0;
        while (size3 >= 0) {
            long keyAt2 = longSparseArray.keyAt(size3);
            int i4 = delete;
            long longValue = ((Long) longSparseArray.valueAt(size3)).longValue();
            if (keyAt2 != longValue) {
                if (longSparseArray.indexOfKey(longValue) >= 0) {
                    sparseLongArray2.put(i3, longValue);
                    i3++;
                    longValue -= Long.MIN_VALUE;
                }
                sparseLongArray = sparseLongArray2;
                i = size3;
                migrateProfileId(sQLiteDatabase, keyAt2, longValue);
                i3 = i3;
            } else {
                sparseLongArray = sparseLongArray2;
                i = size3;
            }
            size3 = i - 1;
            delete = i4;
            sparseLongArray2 = sparseLongArray;
        }
        SparseLongArray sparseLongArray3 = sparseLongArray2;
        int i5 = delete;
        for (int size4 = sparseLongArray3.size() - 1; size4 >= 0; size4--) {
            long valueAt = sparseLongArray3.valueAt(size4);
            migrateProfileId(sQLiteDatabase, valueAt - Long.MIN_VALUE, valueAt);
        }
        if (defaultUserSerial != defaultProfileId) {
            changeDefaultColumn(sQLiteDatabase2, defaultUserSerial);
        }
        if (Utilities.getPrefs(context).getInt(RESTORED_DEVICE_TYPE, 0) != 1) {
            removeScreenIdGaps(sQLiteDatabase2);
        }
        return i5;
    }

    /* JADX WARNING: type inference failed for: r6v4, types: [int] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeScreenIdGaps(android.database.sqlite.SQLiteDatabase r10) {
        /*
            r9 = this;
            java.lang.String r0 = "RestoreDbTask"
            java.lang.String r1 = "Removing gaps between screenIds"
            com.android.launcher3.logging.FileLog.d(r0, r1)
            r2 = 1
            java.lang.String r4 = "favorites"
            java.lang.String r5 = "screen"
            java.lang.String r6 = "container = -100"
            r7 = 0
            java.lang.String r8 = "screen"
            r3 = r10
            com.android.launcher3.util.IntArray r0 = com.android.launcher3.provider.LauncherDbUtils.queryIntArray(r2, r3, r4, r5, r6, r7, r8)
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x001d
            return
        L_0x001d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "UPDATE "
            r1.<init>(r2)
            java.lang.String r2 = "favorites"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = " SET "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "screen"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r3 = " =\nCASE\n"
            java.lang.StringBuilder r1 = r1.append(r3)
            r3 = 0
            boolean r4 = r0.contains(r3)
            r4 = r4 ^ 1
        L_0x0043:
            int r5 = r0.size()
            if (r3 >= r5) goto L_0x0076
            java.lang.String r5 = "WHEN "
            java.lang.StringBuilder r5 = r1.append(r5)
            java.lang.StringBuilder r5 = r5.append(r2)
            java.lang.String r6 = " == "
            java.lang.StringBuilder r5 = r5.append(r6)
            int r6 = r0.get(r3)
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = " THEN "
            java.lang.StringBuilder r5 = r5.append(r6)
            int r6 = r4 + 1
            java.lang.StringBuilder r4 = r5.append(r4)
            java.lang.String r5 = "\n"
            r4.append(r5)
            int r3 = r3 + 1
            r4 = r6
            goto L_0x0043
        L_0x0076:
            java.lang.String r0 = "ELSE screen\nEND WHERE "
            java.lang.StringBuilder r0 = r1.append(r0)
            java.lang.String r2 = "container"
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r2 = " = "
            java.lang.StringBuilder r0 = r0.append(r2)
            r2 = -100
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r2 = ";"
            r0.append(r2)
            java.lang.String r0 = r1.toString()
            r10.execSQL(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.provider.RestoreDbTask.removeScreenIdGaps(android.database.sqlite.SQLiteDatabase):void");
    }

    /* access modifiers changed from: protected */
    public void migrateProfileId(SQLiteDatabase sQLiteDatabase, long j, long j2) {
        FileLog.d(TAG, "Changing profile user id from " + j + " to " + j2);
        ContentValues contentValues = new ContentValues();
        contentValues.put("profileId", Long.valueOf(j2));
        sQLiteDatabase.update(LauncherSettings.Favorites.TABLE_NAME, contentValues, "profileId = ?", new String[]{Long.toString(j)});
    }

    /* access modifiers changed from: protected */
    public void changeDefaultColumn(SQLiteDatabase sQLiteDatabase, long j) {
        sQLiteDatabase.execSQL("ALTER TABLE favorites RENAME TO favorites_old;");
        LauncherSettings.Favorites.addTableToDb(sQLiteDatabase, j, false);
        sQLiteDatabase.execSQL("INSERT INTO favorites SELECT * FROM favorites_old;");
        LauncherDbUtils.dropTable(sQLiteDatabase, "favorites_old");
    }

    private LongSparseArray<Long> getManagedProfileIds(SQLiteDatabase sQLiteDatabase, long j) {
        LongSparseArray<Long> longSparseArray = new LongSparseArray<>();
        Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT profileId from favorites WHERE profileId != ? GROUP BY profileId", new String[]{Long.toString(j)});
        while (rawQuery.moveToNext()) {
            try {
                longSparseArray.put(rawQuery.getLong(rawQuery.getColumnIndex("profileId")), (Object) null);
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        if (rawQuery != null) {
            rawQuery.close();
        }
        return longSparseArray;
        throw th;
    }

    private UserHandle getUserForAncestralSerialNumber(BackupManager backupManager, long j) {
        if (!Utilities.ATLEAST_Q) {
            return null;
        }
        return backupManager.getUserForAncestralSerialNumber(j);
    }

    /* access modifiers changed from: protected */
    public long getDefaultProfileId(SQLiteDatabase sQLiteDatabase) throws Exception {
        Cursor rawQuery = sQLiteDatabase.rawQuery("PRAGMA table_info (favorites)", (String[]) null);
        try {
            int columnIndex = rawQuery.getColumnIndex(INFO_COLUMN_NAME);
            while (rawQuery.moveToNext()) {
                if ("profileId".equals(rawQuery.getString(columnIndex))) {
                    long j = rawQuery.getLong(rawQuery.getColumnIndex(INFO_COLUMN_DEFAULT_VALUE));
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    return j;
                }
            }
            throw new InvalidObjectException("Table does not have a profile id column");
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static boolean isPending(Context context) {
        return Utilities.getPrefs(context).contains(RESTORED_DEVICE_TYPE);
    }

    public static void setPending(Context context) {
        FileLog.d(TAG, "Restore data received through full backup ");
        Utilities.getPrefs(context).edit().putInt(RESTORED_DEVICE_TYPE, new DeviceGridState(context).getDeviceType()).commit();
    }

    private void restoreAppWidgetIdsIfExists(Context context) {
        SharedPreferences prefs = Utilities.getPrefs(context);
        if (!prefs.contains(APPWIDGET_OLD_IDS) || !prefs.contains(APPWIDGET_IDS)) {
            FileLog.d(TAG, "No app widget ids to restore.");
        } else {
            AppWidgetsRestoredReceiver.restoreAppWidgetIds(context, IntArray.fromConcatString(prefs.getString(APPWIDGET_OLD_IDS, "")).toArray(), IntArray.fromConcatString(prefs.getString(APPWIDGET_IDS, "")).toArray());
        }
        prefs.edit().remove(APPWIDGET_OLD_IDS).remove(APPWIDGET_IDS).apply();
    }

    public static void setRestoredAppWidgetIds(Context context, int[] iArr, int[] iArr2) {
        Utilities.getPrefs(context).edit().putString(APPWIDGET_OLD_IDS, IntArray.wrap(iArr).toConcatString()).putString(APPWIDGET_IDS, IntArray.wrap(iArr2).toConcatString()).commit();
    }
}
