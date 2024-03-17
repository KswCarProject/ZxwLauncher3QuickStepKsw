package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.icons.BaseIconFactory;
import com.android.launcher3.icons.cache.CachingLogic;
import com.android.launcher3.util.ResourceBasedOverride;

public class LauncherActivityCachingLogic implements CachingLogic<LauncherActivityInfo>, ResourceBasedOverride {
    public static LauncherActivityCachingLogic newInstance(Context context) {
        return (LauncherActivityCachingLogic) ResourceBasedOverride.Overrides.getObject(LauncherActivityCachingLogic.class, context, R.string.launcher_activity_logic_class);
    }

    public ComponentName getComponent(LauncherActivityInfo launcherActivityInfo) {
        return launcherActivityInfo.getComponentName();
    }

    public UserHandle getUser(LauncherActivityInfo launcherActivityInfo) {
        return launcherActivityInfo.getUser();
    }

    public CharSequence getLabel(LauncherActivityInfo launcherActivityInfo) {
        return launcherActivityInfo.getLabel();
    }

    public BitmapInfo loadIcon(Context context, LauncherActivityInfo launcherActivityInfo) {
        LauncherIcons obtain = LauncherIcons.obtain(context);
        try {
            BitmapInfo createBadgedIconBitmap = obtain.createBadgedIconBitmap(LauncherAppState.getInstance(context).getIconProvider().getIcon(launcherActivityInfo, obtain.mFillResIconDpi), new BaseIconFactory.IconOptions().setUser(launcherActivityInfo.getUser()));
            if (obtain != null) {
                obtain.close();
            }
            return createBadgedIconBitmap;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }
}
