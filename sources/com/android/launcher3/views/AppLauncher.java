package com.android.launcher3.views;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.RunnableList;

public interface AppLauncher extends ActivityContext {
    public static final String TAG = "AppLauncher";

    boolean isAppBlockedForSafeMode() {
        return false;
    }

    boolean onErrorStartingShortcut(Intent intent, ItemInfo itemInfo) {
        return false;
    }

    boolean startActivitySafely(View view, Intent intent, ItemInfo itemInfo) {
        Context context = (Context) this;
        if (!isAppBlockedForSafeMode() || PackageManagerHelper.isSystemApp(context, intent)) {
            UserHandle userHandle = null;
            Bundle bundle = view != null ? getActivityLaunchOptions(view, itemInfo).toBundle() : null;
            if (itemInfo != null) {
                userHandle = itemInfo.user;
            }
            intent.addFlags(268435456);
            if (view != null) {
                intent.setSourceBounds(Utilities.getViewBounds(view));
            }
            try {
                if ((itemInfo instanceof WorkspaceItemInfo) && (itemInfo.itemType == 1 || itemInfo.itemType == 6) && !((WorkspaceItemInfo) itemInfo).isPromise()) {
                    startShortcutIntentSafely(intent, bundle, itemInfo);
                } else {
                    if (userHandle != null) {
                        if (!userHandle.equals(Process.myUserHandle())) {
                            ((LauncherApps) context.getSystemService(LauncherApps.class)).startMainActivity(intent.getComponent(), userHandle, intent.getSourceBounds(), bundle);
                        }
                    }
                    context.startActivity(intent, bundle);
                }
                if (itemInfo != null) {
                    logAppLaunch(getStatsLogManager(), itemInfo, new InstanceIdSequence().newInstanceId());
                }
                return true;
            } catch (ActivityNotFoundException | NullPointerException | SecurityException e) {
                Toast.makeText(context, R.string.activity_not_found, 0).show();
                Log.e(TAG, "Unable to launch. tag=" + itemInfo + " intent=" + intent, e);
                return false;
            }
        } else {
            Toast.makeText(context, R.string.safemode_shortcut_error, 0).show();
            return false;
        }
    }

    void logAppLaunch(StatsLogManager statsLogManager, ItemInfo itemInfo, InstanceId instanceId) {
        statsLogManager.logger().withItemInfo(itemInfo).withInstanceId(instanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP);
    }

    ActivityOptionsWrapper getActivityLaunchOptions(View view, ItemInfo itemInfo) {
        int i;
        int i2;
        int i3;
        FastBitmapDrawable icon;
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i4 = 0;
        if (!(view instanceof BubbleTextView) || (icon = ((BubbleTextView) view).getIcon()) == null) {
            i2 = measuredWidth;
            i = 0;
            i3 = 0;
        } else {
            Rect bounds = icon.getBounds();
            i = (measuredWidth - bounds.width()) / 2;
            i3 = view.getPaddingTop();
            i2 = bounds.width();
            measuredHeight = bounds.height();
        }
        ActivityOptions makeClipRevealAnimation = ActivityOptions.makeClipRevealAnimation(view, i, i3, i2, measuredHeight);
        if (!(view == null || view.getDisplay() == null)) {
            i4 = view.getDisplay().getDisplayId();
        }
        makeClipRevealAnimation.setLaunchDisplayId(i4);
        return new ActivityOptionsWrapper(makeClipRevealAnimation, new RunnableList());
    }

    void startShortcutIntentSafely(Intent intent, Bundle bundle, ItemInfo itemInfo) {
        StrictMode.VmPolicy vmPolicy;
        try {
            vmPolicy = StrictMode.getVmPolicy();
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            if (itemInfo.itemType == 6) {
                startShortcut(intent.getPackage(), ((WorkspaceItemInfo) itemInfo).getDeepShortcutId(), intent.getSourceBounds(), bundle, itemInfo.user);
            } else {
                ((Context) this).startActivity(intent, bundle);
            }
            StrictMode.setVmPolicy(vmPolicy);
        } catch (SecurityException e) {
            if (!onErrorStartingShortcut(intent, itemInfo)) {
                throw e;
            }
        } catch (Throwable th) {
            StrictMode.setVmPolicy(vmPolicy);
            throw th;
        }
    }

    void startShortcut(String str, String str2, Rect rect, Bundle bundle, UserHandle userHandle) {
        try {
            ((LauncherApps) ((Context) this).getSystemService(LauncherApps.class)).startShortcut(str, str2, rect, bundle, userHandle);
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to start shortcut", e);
        }
    }
}
