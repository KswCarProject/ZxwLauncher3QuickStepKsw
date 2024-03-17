package com.android.launcher3.uioverrides;

import android.app.ActivityTaskManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.widget.LauncherAppWidgetHostView;

class QuickstepInteractionHandler implements RemoteViews.InteractionHandler {
    private static final String TAG = "QuickstepInteractionHandler";
    private final QuickstepLauncher mLauncher;

    QuickstepInteractionHandler(QuickstepLauncher quickstepLauncher) {
        this.mLauncher = quickstepLauncher;
    }

    public boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
        LauncherAppWidgetHostView findHostViewAncestor = findHostViewAncestor(view);
        if (findHostViewAncestor == null) {
            Log.e(TAG, "View did not have a LauncherAppWidgetHostView ancestor.");
            return RemoteViews.startPendingIntent(findHostViewAncestor, pendingIntent, remoteResponse.getLaunchOptions(view));
        }
        Pair launchOptions = remoteResponse.getLaunchOptions(view);
        ActivityOptionsWrapper activityLaunchOptions = this.mLauncher.getAppTransitionManager().getActivityLaunchOptions(findHostViewAncestor);
        Object tag = findHostViewAncestor.getTag();
        IBinder iBinder = null;
        if (tag instanceof ItemInfo) {
            iBinder = this.mLauncher.getLaunchCookie((ItemInfo) tag);
            activityLaunchOptions.options.setLaunchCookie(iBinder);
        }
        if (Utilities.ATLEAST_S && !pendingIntent.isActivity()) {
            try {
                ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(pendingIntent.getCreatorPackage(), activityLaunchOptions.options.getRemoteAnimationAdapter(), iBinder);
            } catch (RemoteException unused) {
            }
        }
        activityLaunchOptions.options.setPendingIntentLaunchFlags(268435456);
        activityLaunchOptions.options.setSplashScreenStyle(0);
        Pair create = Pair.create((Intent) launchOptions.first, activityLaunchOptions.options);
        if (pendingIntent.isActivity()) {
            logAppLaunch(tag);
        }
        return RemoteViews.startPendingIntent(findHostViewAncestor, pendingIntent, create);
    }

    private void logAppLaunch(Object obj) {
        StatsLogManager.StatsLogger logger = this.mLauncher.getStatsLogManager().logger();
        if (obj instanceof ItemInfo) {
            logger.withItemInfo((ItemInfo) obj);
        }
        logger.log(StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP);
    }

    private LauncherAppWidgetHostView findHostViewAncestor(View view) {
        while (view != null) {
            if (view instanceof LauncherAppWidgetHostView) {
                return (LauncherAppWidgetHostView) view;
            }
            view = (View) view.getParent();
        }
        return null;
    }
}
