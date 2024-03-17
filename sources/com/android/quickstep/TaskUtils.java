package com.android.quickstep;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import java.util.List;

public final class TaskUtils {
    private static final String TAG = "TaskUtils";

    private TaskUtils() {
    }

    public static CharSequence getTitle(Context context, Task task) {
        UserHandle of = UserHandle.of(task.key.userId);
        ApplicationInfo applicationInfo = new PackageManagerHelper(context).getApplicationInfo(task.getTopComponent().getPackageName(), of, 0);
        if (applicationInfo == null) {
            Log.e(TAG, "Failed to get title for task " + task);
            return "";
        }
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getUserBadgedLabel(applicationInfo.loadLabel(packageManager), of);
    }

    public static ComponentKey getLaunchComponentKeyForTask(Task.TaskKey taskKey) {
        ComponentName componentName;
        if (taskKey.sourceComponent != null) {
            componentName = taskKey.sourceComponent;
        } else {
            componentName = taskKey.getComponent();
        }
        return new ComponentKey(componentName, UserHandle.of(taskKey.userId));
    }

    public static boolean taskIsATargetWithMode(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, int i, int i2) {
        for (RemoteAnimationTargetCompat remoteAnimationTargetCompat : remoteAnimationTargetCompatArr) {
            if (remoteAnimationTargetCompat.mode == i2 && remoteAnimationTargetCompat.taskId == i) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkCurrentOrManagedUserId(int i, Context context) {
        if (i == UserHandle.myUserId()) {
            return true;
        }
        List<UserHandle> userProfiles = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getUserProfiles();
        for (int size = userProfiles.size() - 1; size >= 0; size--) {
            if (i == userProfiles.get(size).getIdentifier()) {
                return true;
            }
        }
        return false;
    }

    public static void closeSystemWindowsAsync(String str) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(str) {
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ActivityManagerWrapper.getInstance().closeSystemWindows(this.f$0);
            }
        });
    }
}
