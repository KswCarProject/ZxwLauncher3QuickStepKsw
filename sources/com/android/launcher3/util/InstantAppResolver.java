package com.android.launcher3.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.android.launcher3.R;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.util.ResourceBasedOverride;

public class InstantAppResolver implements ResourceBasedOverride {
    public boolean isInstantApp(ApplicationInfo applicationInfo) {
        return false;
    }

    public boolean isInstantApp(AppInfo appInfo) {
        return false;
    }

    public static InstantAppResolver newInstance(Context context) {
        return (InstantAppResolver) ResourceBasedOverride.Overrides.getObject(InstantAppResolver.class, context, R.string.instant_app_resolver_class);
    }

    public boolean isInstantApp(Context context, String str) {
        try {
            return isInstantApp(context.getPackageManager().getPackageInfo(str, 0).applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("InstantAppResolver", "Failed to determine whether package is instant app " + str, e);
            return false;
        }
    }
}
