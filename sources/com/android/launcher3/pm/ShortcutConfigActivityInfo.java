package com.android.launcher3.pm;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import android.widget.Toast;
import com.android.launcher3.R;
import com.android.launcher3.icons.ComponentWithLabelAndIcon;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ShortcutConfigActivityInfo implements ComponentWithLabelAndIcon {
    private static final String TAG = "SCActivityInfo";
    private final ComponentName mCn;
    private final UserHandle mUser;

    public WorkspaceItemInfo createWorkspaceItemInfo() {
        return null;
    }

    public abstract Drawable getFullResIcon(IconCache iconCache);

    public int getItemType() {
        return 1;
    }

    public boolean isPersistable() {
        return true;
    }

    protected ShortcutConfigActivityInfo(ComponentName componentName, UserHandle userHandle) {
        this.mCn = componentName;
        this.mUser = userHandle;
    }

    public ComponentName getComponent() {
        return this.mCn;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public boolean startConfigActivity(Activity activity, int i) {
        Intent component = new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(getComponent());
        try {
            activity.startActivityForResult(component, i);
            return true;
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            return false;
        } catch (SecurityException e) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + component + ". Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e);
            return false;
        }
    }

    static class ShortcutConfigActivityInfoVL extends ShortcutConfigActivityInfo {
        private final ActivityInfo mInfo;

        ShortcutConfigActivityInfoVL(ActivityInfo activityInfo) {
            super(new ComponentName(activityInfo.packageName, activityInfo.name), Process.myUserHandle());
            this.mInfo = activityInfo;
        }

        public CharSequence getLabel(PackageManager packageManager) {
            return this.mInfo.loadLabel(packageManager);
        }

        public Drawable getFullResIcon(IconCache iconCache) {
            return iconCache.getFullResIcon(this.mInfo);
        }
    }

    public static class ShortcutConfigActivityInfoVO extends ShortcutConfigActivityInfo {
        private final LauncherActivityInfo mInfo;

        public ShortcutConfigActivityInfoVO(LauncherActivityInfo launcherActivityInfo) {
            super(launcherActivityInfo.getComponentName(), launcherActivityInfo.getUser());
            this.mInfo = launcherActivityInfo;
        }

        public CharSequence getLabel(PackageManager packageManager) {
            return this.mInfo.getLabel();
        }

        public Drawable getFullResIcon(IconCache iconCache) {
            return iconCache.getFullResIcon(this.mInfo);
        }

        public boolean startConfigActivity(Activity activity, int i) {
            if (getUser().equals(Process.myUserHandle())) {
                return ShortcutConfigActivityInfo.super.startConfigActivity(activity, i);
            }
            try {
                activity.startIntentSenderForResult(((LauncherApps) activity.getSystemService(LauncherApps.class)).getShortcutConfigActivityIntent(this.mInfo), i, (Intent) null, 0, 0, 0);
                return true;
            } catch (IntentSender.SendIntentException unused) {
                Toast.makeText(activity, R.string.activity_not_found, 0).show();
                return false;
            }
        }
    }

    public static List<ShortcutConfigActivityInfo> queryList(Context context, PackageUserKey packageUserKey) {
        List<UserHandle> list;
        String str;
        ArrayList arrayList = new ArrayList();
        UserHandle myUserHandle = Process.myUserHandle();
        if (packageUserKey == null) {
            list = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getUserProfiles();
            str = null;
        } else {
            List<UserHandle> singletonList = Collections.singletonList(packageUserKey.mUser);
            str = packageUserKey.mPackageName;
            list = singletonList;
        }
        LauncherApps launcherApps = (LauncherApps) context.getSystemService(LauncherApps.class);
        for (UserHandle next : list) {
            boolean equals = myUserHandle.equals(next);
            for (LauncherActivityInfo next2 : launcherApps.getShortcutConfigActivityList(str, next)) {
                if (equals || next2.getApplicationInfo().targetSdkVersion >= 26) {
                    arrayList.add(new ShortcutConfigActivityInfoVO(next2));
                }
            }
        }
        return arrayList;
    }
}
