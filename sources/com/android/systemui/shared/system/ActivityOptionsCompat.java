package com.android.systemui.shared.system;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Handler;

public abstract class ActivityOptionsCompat {
    public static ActivityOptions makeSplitScreenOptions(boolean z) {
        return ActivityOptions.makeBasic();
    }

    public static ActivityOptions makeFreeformOptions() {
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        makeBasic.setLaunchWindowingMode(5);
        return makeBasic;
    }

    public static ActivityOptions makeRemoteAnimation(RemoteAnimationAdapterCompat remoteAnimationAdapterCompat) {
        return ActivityOptions.makeRemoteAnimation(remoteAnimationAdapterCompat.getWrapped(), remoteAnimationAdapterCompat.getRemoteTransition().getTransition());
    }

    public static ActivityOptions makeRemoteTransition(RemoteTransitionCompat remoteTransitionCompat) {
        return ActivityOptions.makeRemoteTransition(remoteTransitionCompat.getTransition());
    }

    public static ActivityOptions makeCustomAnimation(Context context, int i, int i2, final Runnable runnable, final Handler handler) {
        return ActivityOptions.makeCustomTaskAnimation(context, i, i2, handler, new ActivityOptions.OnAnimationStartedListener() {
            public void onAnimationStarted(long j) {
                Runnable runnable = runnable;
                if (runnable != null) {
                    handler.post(runnable);
                }
            }
        }, (ActivityOptions.OnAnimationFinishedListener) null);
    }

    public static ActivityOptions setFreezeRecentTasksList(ActivityOptions activityOptions) {
        activityOptions.setFreezeRecentTasksReordering();
        return activityOptions;
    }

    public static ActivityOptions setLauncherSourceInfo(ActivityOptions activityOptions, long j) {
        activityOptions.setSourceInfo(1, j);
        return activityOptions;
    }
}
