package com.android.launcher3.hybridhotseat;

import android.content.Context;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.GridBackupTable;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.util.Executors;

public class HotseatRestoreHelper {
    public static void createBackup(Context context) {
        Executors.MODEL_EXECUTOR.execute(new Runnable(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                HotseatRestoreHelper.lambda$createBackup$0(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$createBackup$0(Context context) {
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction = (LauncherDbUtils.SQLiteTransaction) LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_TRANSACTION).getBinder("value");
        try {
            InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
            new GridBackupTable(context, sQLiteTransaction.getDb(), idp.numDatabaseHotseatIcons, idp.numColumns, idp.numRows).createCustomBackupTable(LauncherSettings.Favorites.HYBRID_HOTSEAT_BACKUP_TABLE);
            sQLiteTransaction.commit();
            LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_REFRESH_HOTSEAT_RESTORE_TABLE);
            if (sQLiteTransaction != null) {
                sQLiteTransaction.close();
                return;
            }
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static void restoreBackup(Context context) {
        Executors.MODEL_EXECUTOR.execute(new Runnable(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                HotseatRestoreHelper.lambda$restoreBackup$1(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$restoreBackup$1(Context context) {
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction = (LauncherDbUtils.SQLiteTransaction) LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_TRANSACTION).getBinder("value");
        try {
            if (LauncherDbUtils.tableExists(sQLiteTransaction.getDb(), LauncherSettings.Favorites.HYBRID_HOTSEAT_BACKUP_TABLE)) {
                InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
                new GridBackupTable(context, sQLiteTransaction.getDb(), idp.numDatabaseHotseatIcons, idp.numColumns, idp.numRows).restoreFromCustomBackupTable(LauncherSettings.Favorites.HYBRID_HOTSEAT_BACKUP_TABLE, true);
                sQLiteTransaction.commit();
                LauncherAppState.getInstance(context).getModel().forceReload();
                if (sQLiteTransaction != null) {
                    sQLiteTransaction.close();
                    return;
                }
                return;
            } else if (sQLiteTransaction != null) {
                sQLiteTransaction.close();
                return;
            } else {
                return;
            }
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }
}
