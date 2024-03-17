package com.android.launcher3.pm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Parcelable;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.icons.ShortcutCachingLogic;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.Executors;

public class PinRequestHelper {
    public static WorkspaceItemInfo createWorkspaceItemFromPinItemRequest(Context context, final LauncherApps.PinItemRequest pinItemRequest, final long j) {
        if (pinItemRequest == null || pinItemRequest.getRequestType() != 1 || !pinItemRequest.isValid()) {
            return null;
        }
        if (j > 0) {
            Executors.MODEL_EXECUTOR.execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(j);
                    } catch (InterruptedException unused) {
                    }
                    if (pinItemRequest.isValid()) {
                        pinItemRequest.accept();
                    }
                }
            });
        } else if (!pinItemRequest.accept()) {
            return null;
        }
        ShortcutInfo shortcutInfo = pinItemRequest.getShortcutInfo();
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo(shortcutInfo, context);
        workspaceItemInfo.bitmap = new ShortcutCachingLogic().loadIcon(context, shortcutInfo);
        LauncherAppState.getInstance(context).getModel().updateAndBindWorkspaceItem(workspaceItemInfo, shortcutInfo);
        return workspaceItemInfo;
    }

    public static LauncherApps.PinItemRequest getPinItemRequest(Intent intent) {
        Parcelable parcelableExtra = intent.getParcelableExtra("android.content.pm.extra.PIN_ITEM_REQUEST");
        if (parcelableExtra instanceof LauncherApps.PinItemRequest) {
            return (LauncherApps.PinItemRequest) parcelableExtra;
        }
        return null;
    }
}
