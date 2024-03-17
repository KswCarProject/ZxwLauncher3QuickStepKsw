package com.android.systemui.shared.system;

import android.app.Activity;
import android.app.ActivityClient;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ActivityTaskManager;
import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.IRecentsAnimationController;
import android.view.IRecentsAnimationRunner;
import android.view.RemoteAnimationTarget;
import android.window.TaskSnapshot;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.List;
import java.util.function.Consumer;

public class ActivityManagerWrapper {
    public static final String CLOSE_SYSTEM_WINDOWS_REASON_HOME_KEY = "homekey";
    public static final String CLOSE_SYSTEM_WINDOWS_REASON_RECENTS = "recentapps";
    private static final String INVOCATION_TIME_MS_KEY = "invocation_time_ms";
    private static final int NUM_RECENT_ACTIVITIES_REQUEST = 3;
    private static final String TAG = "ActivityManagerWrapper";
    private static final ActivityManagerWrapper sInstance = new ActivityManagerWrapper();
    private final ActivityTaskManager mAtm = ActivityTaskManager.getInstance();

    private ActivityManagerWrapper() {
    }

    public static ActivityManagerWrapper getInstance() {
        return sInstance;
    }

    public int getCurrentUserId() {
        try {
            UserInfo currentUser = ActivityManager.getService().getCurrentUser();
            if (currentUser != null) {
                return currentUser.id;
            }
            return 0;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ActivityManager.RunningTaskInfo getRunningTask() {
        return getRunningTask(false);
    }

    public ActivityManager.RunningTaskInfo getRunningTask(boolean z) {
        List tasks = this.mAtm.getTasks(1, z);
        if (tasks.isEmpty()) {
            return null;
        }
        return (ActivityManager.RunningTaskInfo) tasks.get(0);
    }

    public ActivityManager.RunningTaskInfo[] getRunningTasks(boolean z) {
        List tasks = this.mAtm.getTasks(3, z);
        return (ActivityManager.RunningTaskInfo[]) tasks.toArray(new ActivityManager.RunningTaskInfo[tasks.size()]);
    }

    public List<ActivityManager.RecentTaskInfo> getRecentTasks(int i, int i2) {
        return this.mAtm.getRecentTasks(i, 2, i2);
    }

    public ThumbnailData getTaskThumbnail(int i, boolean z) {
        TaskSnapshot taskSnapshot;
        try {
            taskSnapshot = ActivityTaskManager.getService().getTaskSnapshot(i, z);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to retrieve task snapshot", e);
            taskSnapshot = null;
        }
        if (taskSnapshot != null) {
            return new ThumbnailData(taskSnapshot);
        }
        return new ThumbnailData();
    }

    public void invalidateHomeTaskSnapshot(Activity activity) {
        IBinder iBinder;
        try {
            ActivityClient instance = ActivityClient.getInstance();
            if (activity == null) {
                iBinder = null;
            } else {
                iBinder = activity.getActivityToken();
            }
            instance.invalidateHomeTaskSnapshot(iBinder);
        } catch (Throwable th) {
            Log.w(TAG, "Failed to invalidate home snapshot", th);
        }
    }

    public void startRecentsActivity(Intent intent, long j, RecentsAnimationListener recentsAnimationListener, final Consumer<Boolean> consumer, Handler handler) {
        final boolean startRecentsActivity = startRecentsActivity(intent, j, recentsAnimationListener);
        if (consumer != null) {
            handler.post(new Runnable() {
                public void run() {
                    consumer.accept(Boolean.valueOf(startRecentsActivity));
                }
            });
        }
    }

    public boolean startRecentsActivity(Intent intent, long j, final RecentsAnimationListener recentsAnimationListener) {
        AnonymousClass2 r0 = null;
        if (recentsAnimationListener != null) {
            try {
                r0 = new IRecentsAnimationRunner.Stub() {
                    public void onAnimationStart(IRecentsAnimationController iRecentsAnimationController, RemoteAnimationTarget[] remoteAnimationTargetArr, RemoteAnimationTarget[] remoteAnimationTargetArr2, Rect rect, Rect rect2) {
                        recentsAnimationListener.onAnimationStart(new RecentsAnimationControllerCompat(iRecentsAnimationController), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr), RemoteAnimationTargetCompat.wrap(remoteAnimationTargetArr2), rect, rect2);
                    }

                    public void onAnimationCanceled(int[] iArr, TaskSnapshot[] taskSnapshotArr) {
                        recentsAnimationListener.onAnimationCanceled(ThumbnailData.wrap(iArr, taskSnapshotArr));
                    }

                    public void onTasksAppeared(RemoteAnimationTarget[] remoteAnimationTargetArr) {
                        RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr = new RemoteAnimationTargetCompat[remoteAnimationTargetArr.length];
                        for (int i = 0; i < remoteAnimationTargetArr.length; i++) {
                            remoteAnimationTargetCompatArr[i] = new RemoteAnimationTargetCompat(remoteAnimationTargetArr[i]);
                        }
                        recentsAnimationListener.onTasksAppeared(remoteAnimationTargetCompatArr);
                    }
                };
            } catch (Exception unused) {
                return false;
            }
        }
        ActivityTaskManager.getService().startRecentsActivity(intent, j, r0);
        return true;
    }

    public void cancelRecentsAnimation(boolean z) {
        try {
            ActivityTaskManager.getService().cancelRecentsAnimation(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to cancel recents animation", e);
        }
    }

    public void startActivityFromRecentsAsync(Task.TaskKey taskKey, ActivityOptions activityOptions, final Consumer<Boolean> consumer, Handler handler) {
        final boolean startActivityFromRecents = startActivityFromRecents(taskKey, activityOptions);
        if (consumer != null) {
            handler.post(new Runnable() {
                public void run() {
                    consumer.accept(Boolean.valueOf(startActivityFromRecents));
                }
            });
        }
    }

    public boolean startActivityFromRecents(Task.TaskKey taskKey, ActivityOptions activityOptions) {
        return startActivityFromRecents(taskKey.id, activityOptions);
    }

    public boolean startActivityFromRecents(int i, ActivityOptions activityOptions) {
        Bundle bundle;
        if (activityOptions == null) {
            bundle = null;
        } else {
            try {
                bundle = activityOptions.toBundle();
            } catch (Exception unused) {
                return false;
            }
        }
        ActivityTaskManager.getService().startActivityFromRecents(i, bundle);
        return true;
    }

    public void registerTaskStackListener(TaskStackChangeListener taskStackChangeListener) {
        TaskStackChangeListeners.getInstance().registerTaskStackListener(taskStackChangeListener);
    }

    public void unregisterTaskStackListener(TaskStackChangeListener taskStackChangeListener) {
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(taskStackChangeListener);
    }

    public void closeSystemWindows(String str) {
        try {
            ActivityManager.getService().closeSystemDialogs(str);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to close system windows", e);
        }
    }

    public void removeTask(int i) {
        try {
            Log.d("removeTask", "getService = " + ActivityTaskManager.getService() + ", taskid = " + i);
            Log.d("removeTask", "removeTask = " + ActivityTaskManager.getService().removeTask(i));
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to remove task=" + i, e);
        }
    }

    public void removeAllRecentTasks() {
        try {
            ActivityTaskManager.getService().removeAllVisibleRecentTasks();
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to remove all tasks", e);
        }
    }

    public boolean isScreenPinningActive() {
        try {
            return ActivityTaskManager.getService().getLockTaskModeState() == 2;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isScreenPinningEnabled() {
        if (Settings.System.getInt(AppGlobals.getInitialApplication().getContentResolver(), "lock_to_app_enabled", 0) != 0) {
            return true;
        }
        return false;
    }

    public boolean isLockToAppActive() {
        try {
            return ActivityTaskManager.getService().getLockTaskModeState() != 0;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean isLockTaskKioskModeActive() {
        try {
            return ActivityTaskManager.getService().getLockTaskModeState() == 1;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean showVoiceSession(IBinder iBinder, Bundle bundle, int i) {
        IVoiceInteractionManagerService asInterface = IVoiceInteractionManagerService.Stub.asInterface(ServiceManager.getService("voiceinteraction"));
        if (asInterface == null) {
            return false;
        }
        bundle.putLong(INVOCATION_TIME_MS_KEY, SystemClock.elapsedRealtime());
        try {
            return asInterface.showSessionFromSession(iBinder, bundle, i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean supportsFreeformMultiWindow(Context context) {
        boolean z = Settings.Global.getInt(context.getContentResolver(), "enable_freeform_support", 0) != 0;
        if (!ActivityTaskManager.supportsMultiWindow(context)) {
            return false;
        }
        if (context.getPackageManager().hasSystemFeature("android.software.freeform_window_management") || z) {
            return true;
        }
        return false;
    }

    public static boolean isHomeTask(ActivityManager.RunningTaskInfo runningTaskInfo) {
        return runningTaskInfo.configuration.windowConfiguration.getActivityType() == 2;
    }
}
