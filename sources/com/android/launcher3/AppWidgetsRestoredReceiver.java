package com.android.launcher3;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Process;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.widget.LauncherAppWidgetHost;

public class AppWidgetsRestoredReceiver extends BroadcastReceiver {
    private static final String TAG = "AWRestoredReceiver";

    public void onReceive(Context context, Intent intent) {
        if ("android.appwidget.action.APPWIDGET_HOST_RESTORED".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("hostId", 0);
            Log.d(TAG, "Widget ID map received for host:" + intExtra);
            if (intExtra == 1024) {
                int[] intArrayExtra = intent.getIntArrayExtra("appWidgetOldIds");
                int[] intArrayExtra2 = intent.getIntArrayExtra("appWidgetIds");
                if (intArrayExtra == null || intArrayExtra2 == null || intArrayExtra.length != intArrayExtra2.length) {
                    Log.e(TAG, "Invalid host restored received");
                } else {
                    RestoreDbTask.setRestoredAppWidgetIds(context, intArrayExtra, intArrayExtra2);
                }
            }
        }
    }

    public static void restoreAppWidgetIds(Context context, int[] iArr, int[] iArr2) {
        ContentResolver contentResolver;
        String str;
        String str2;
        String[] strArr;
        Context context2 = context;
        int[] iArr3 = iArr;
        int[] iArr4 = iArr2;
        LauncherAppWidgetHost launcherAppWidgetHost = new LauncherAppWidgetHost(context2);
        int i = 0;
        if (!RestoreDbTask.isPending(context)) {
            Log.e(TAG, "Skipping widget ID remap as DB already in use");
            int length = iArr4.length;
            while (i < length) {
                int i2 = iArr4[i];
                Log.d(TAG, "Deleting widgetId: " + i2);
                launcherAppWidgetHost.deleteAppWidgetId(i2);
                i++;
            }
            return;
        }
        ContentResolver contentResolver2 = context.getContentResolver();
        AppWidgetManager instance = AppWidgetManager.getInstance(context);
        int i3 = 0;
        while (i3 < iArr3.length) {
            Log.i(TAG, "Widget state restore id " + iArr3[i3] + " => " + iArr4[i3]);
            int i4 = LoaderTask.isValidProvider(instance.getAppWidgetInfo(iArr4[i3])) ? 4 : 2;
            long serialNumberForUser = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context2).getSerialNumberForUser(Process.myUserHandle());
            String num = Integer.toString(iArr3[i3]);
            String[] strArr2 = new String[2];
            strArr2[i] = num;
            strArr2[1] = Long.toString(serialNumberForUser);
            String[] strArr3 = strArr2;
            if (new ContentWriter(context2, new ContentWriter.CommitParams("appWidgetId=? and (restored & 1) = 1 and profileId=?", strArr2)).put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(iArr4[i3])).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(i4)).commit() == 0) {
                Uri uri = LauncherSettings.Favorites.CONTENT_URI;
                String[] strArr4 = {LauncherSettings.Favorites.APPWIDGET_ID};
                String[] strArr5 = new String[1];
                strArr5[i] = num;
                str = LauncherSettings.Favorites.APPWIDGET_ID;
                strArr = strArr3;
                contentResolver = contentResolver2;
                str2 = LauncherSettings.Favorites.RESTORED;
                Cursor query = contentResolver2.query(uri, strArr4, "appWidgetId=?", strArr5, (String) null);
                try {
                    if (!query.moveToFirst()) {
                        launcherAppWidgetHost.deleteAppWidgetId(iArr4[i3]);
                    }
                } finally {
                    query.close();
                }
            } else {
                str = LauncherSettings.Favorites.APPWIDGET_ID;
                strArr = strArr3;
                contentResolver = contentResolver2;
                str2 = LauncherSettings.Favorites.RESTORED;
            }
            new ContentWriter(context2, ContentWriter.CommitParams.backupCommitParams("appWidgetId=? and profileId=?", strArr)).put(str, Integer.valueOf(iArr4[i3])).put(str2, Integer.valueOf(i4)).commit();
            i3++;
            iArr3 = iArr;
            contentResolver2 = contentResolver;
            i = 0;
        }
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null) {
            instanceNoCreate.getModel().forceReload();
        }
    }
}
