package com.android.launcher3.util;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.systemui.flags.FlagManager;
import java.net.URISyntaxException;
import java.util.List;

public class PackageManagerHelper {
    private static final String TAG = "PackageManagerHelper";
    private final Context mContext;
    private final LauncherApps mLauncherApps;
    private final PackageManager mPm;

    public PackageManagerHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mLauncherApps = (LauncherApps) context.getSystemService(LauncherApps.class);
    }

    public boolean isAppOnSdcard(String str, UserHandle userHandle) {
        ApplicationInfo applicationInfo = getApplicationInfo(str, userHandle, 8192);
        return (applicationInfo == null || (applicationInfo.flags & 262144) == 0) ? false : true;
    }

    public boolean isAppSuspended(String str, UserHandle userHandle) {
        ApplicationInfo applicationInfo = getApplicationInfo(str, userHandle, 0);
        if (applicationInfo == null || !isAppSuspended(applicationInfo)) {
            return false;
        }
        return true;
    }

    public boolean isAppInstalled(String str, UserHandle userHandle) {
        return getApplicationInfo(str, userHandle, 0) != null;
    }

    public ApplicationInfo getApplicationInfo(String str, UserHandle userHandle, int i) {
        try {
            ApplicationInfo applicationInfo = this.mLauncherApps.getApplicationInfo(str, i, userHandle);
            if ((applicationInfo.flags & 8388608) == 0 || !applicationInfo.enabled) {
                return null;
            }
            return applicationInfo;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public boolean isSafeMode() {
        return this.mPm.isSafeMode();
    }

    public Intent getAppLaunchIntent(String str, UserHandle userHandle) {
        List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList(str, userHandle);
        if (activityList.isEmpty()) {
            return null;
        }
        return AppInfo.makeLaunchIntent(activityList.get(0));
    }

    public static boolean isAppSuspended(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & BasicMeasure.EXACTLY) != 0;
    }

    public boolean hasPermissionForActivity(Intent intent, String str) {
        ResolveInfo resolveActivity = this.mPm.resolveActivity(intent, 0);
        if (resolveActivity == null) {
            return false;
        }
        if (TextUtils.isEmpty(resolveActivity.activityInfo.permission)) {
            return true;
        }
        if (TextUtils.isEmpty(str) || this.mPm.checkPermission(resolveActivity.activityInfo.permission, str) != 0) {
            return false;
        }
        if (TextUtils.isEmpty(AppOpsManager.permissionToOp(resolveActivity.activityInfo.permission))) {
            return true;
        }
        try {
            if (this.mPm.getApplicationInfo(str, 0).targetSdkVersion >= 23) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public Intent getMarketIntent(String str) {
        return new Intent("android.intent.action.VIEW").setData(new Uri.Builder().scheme("market").authority("details").appendQueryParameter(FlagManager.EXTRA_ID, str).build()).putExtra("android.intent.extra.REFERRER", new Uri.Builder().scheme("android-app").authority(this.mContext.getPackageName()).build());
    }

    public static Intent getMarketSearchIntent(Context context, String str) {
        try {
            Intent parseUri = Intent.parseUri(context.getString(R.string.market_search_intent), 0);
            if (!TextUtils.isEmpty(str)) {
                parseUri.setData(parseUri.getData().buildUpon().appendQueryParameter("q", str).build());
            }
            return parseUri;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Intent getStyleWallpapersIntent(Context context) {
        return new Intent("android.intent.action.SET_WALLPAPER").setComponent(new ComponentName(context.getString(R.string.wallpaper_picker_package), context.getString(R.string.custom_activity_picker)));
    }

    public void startDetailsActivityForInfo(ItemInfo itemInfo, Rect rect, Bundle bundle) {
        if (itemInfo instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) itemInfo;
            if ((itemInfoWithIcon.runtimeStatusFlags & 1024) != 0) {
                this.mContext.startActivity(new PackageManagerHelper(this.mContext).getMarketIntent(itemInfoWithIcon.getTargetComponent().getPackageName()));
                return;
            }
        }
        ComponentName componentName = null;
        if (itemInfo instanceof AppInfo) {
            componentName = ((AppInfo) itemInfo).componentName;
        } else if (itemInfo instanceof WorkspaceItemInfo) {
            componentName = itemInfo.getTargetComponent();
        } else if (itemInfo instanceof PendingAddItemInfo) {
            componentName = ((PendingAddItemInfo) itemInfo).componentName;
        } else if (itemInfo instanceof LauncherAppWidgetInfo) {
            componentName = ((LauncherAppWidgetInfo) itemInfo).providerName;
        }
        if (componentName != null) {
            try {
                this.mLauncherApps.startAppDetailsActivity(componentName, itemInfo.user, rect, bundle);
            } catch (ActivityNotFoundException | SecurityException e) {
                Toast.makeText(this.mContext, R.string.activity_not_found, 0).show();
                Log.e(TAG, "Unable to launch settings", e);
            }
        }
    }

    public static IntentFilter getPackageFilter(String str, String... strArr) {
        IntentFilter intentFilter = new IntentFilter();
        for (String addAction : strArr) {
            intentFilter.addAction(addAction);
        }
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart(str, 0);
        return intentFilter;
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        String str;
        PackageManager packageManager = context.getPackageManager();
        ComponentName component = intent.getComponent();
        if (component == null) {
            ResolveInfo resolveActivity = packageManager.resolveActivity(intent, 65536);
            str = (resolveActivity == null || resolveActivity.activityInfo == null) ? null : resolveActivity.activityInfo.packageName;
        } else {
            str = component.getPackageName();
        }
        if (str == null) {
            str = intent.getPackage();
        }
        if (str == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            if (packageInfo == null || packageInfo.applicationInfo == null || (packageInfo.applicationInfo.flags & 1) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static Pair<String, Resources> findSystemApk(String str, PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceivers(new Intent(str), 1048576)) {
            String str2 = resolveInfo.activityInfo.packageName;
            try {
                return Pair.create(str2, packageManager.getResourcesForApplication(str2));
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w(TAG, "Failed to find resources for " + str2);
            }
        }
        return null;
    }

    public static boolean isLauncherAppTarget(Intent intent) {
        if (intent == null || !"android.intent.action.MAIN".equals(intent.getAction()) || intent.getComponent() == null || intent.getCategories() == null || intent.getCategories().size() != 1 || !intent.hasCategory("android.intent.category.LAUNCHER") || !TextUtils.isEmpty(intent.getDataString())) {
            return false;
        }
        Bundle extras = intent.getExtras();
        if (extras == null || extras.keySet().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean hasShortcutsPermission(Context context) {
        try {
            return ((LauncherApps) context.getSystemService(LauncherApps.class)).hasShortcutHostPermission();
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to make shortcut manager call", e);
            return false;
        }
    }

    public static int getLoadingProgress(LauncherActivityInfo launcherActivityInfo) {
        if (Utilities.ATLEAST_S) {
            return (int) (launcherActivityInfo.getLoadingProgress() * 100.0f);
        }
        return 100;
    }
}
