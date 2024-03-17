package com.android.launcher3.model.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.pm.PackageInstallInfo;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.Comparator;

public class AppInfo extends ItemInfoWithIcon implements WorkspaceItemFactory {
    public static final Comparator<AppInfo> COMPONENT_KEY_COMPARATOR = $$Lambda$AppInfo$Rnjjf5_2D5dlVI2DtC6MgDjpISk.INSTANCE;
    public static final AppInfo[] EMPTY_ARRAY = new AppInfo[0];
    public ComponentName componentName;
    public Intent intent;
    public String sectionName;

    static /* synthetic */ int lambda$static$0(AppInfo appInfo, AppInfo appInfo2) {
        int hashCode = appInfo.user.hashCode() - appInfo2.user.hashCode();
        return hashCode != 0 ? hashCode : appInfo.componentName.compareTo(appInfo2.componentName);
    }

    public AppInfo() {
        this.sectionName = "";
        this.itemType = 0;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public AppInfo(Context context, LauncherActivityInfo launcherActivityInfo, UserHandle userHandle) {
        this(launcherActivityInfo, userHandle, ((UserManager) context.getSystemService(UserManager.class)).isQuietModeEnabled(userHandle));
    }

    public AppInfo(LauncherActivityInfo launcherActivityInfo, UserHandle userHandle, boolean z) {
        this.sectionName = "";
        this.componentName = launcherActivityInfo.getComponentName();
        this.container = LauncherSettings.Favorites.CONTAINER_ALL_APPS;
        this.user = userHandle;
        this.intent = makeLaunchIntent(launcherActivityInfo);
        if (z) {
            this.runtimeStatusFlags |= 8;
        }
        updateRuntimeFlagsForActivityTarget(this, launcherActivityInfo);
    }

    public AppInfo(AppInfo appInfo) {
        super(appInfo);
        this.sectionName = "";
        this.componentName = appInfo.componentName;
        this.title = Utilities.trim(appInfo.title);
        this.intent = new Intent(appInfo.intent);
    }

    public AppInfo(ComponentName componentName2, CharSequence charSequence, UserHandle userHandle, Intent intent2) {
        this.sectionName = "";
        this.componentName = componentName2;
        this.title = charSequence;
        this.user = userHandle;
        this.intent = intent2;
    }

    public AppInfo(PackageInstallInfo packageInstallInfo) {
        this.sectionName = "";
        this.componentName = packageInstallInfo.componentName;
        this.intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(this.componentName).setFlags(270532608);
        setProgressLevel(packageInstallInfo);
        this.user = packageInstallInfo.user;
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " componentName=" + this.componentName;
    }

    public WorkspaceItemInfo makeWorkspaceItem(Context context) {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo(this);
        if ((this.runtimeStatusFlags & 1024) != 0) {
            workspaceItemInfo.status |= 2;
            workspaceItemInfo.status |= 4;
            workspaceItemInfo.status |= 1024;
        }
        if ((this.runtimeStatusFlags & 2048) != 0) {
            workspaceItemInfo.runtimeStatusFlags |= 2048;
        }
        return workspaceItemInfo;
    }

    public static Intent makeLaunchIntent(LauncherActivityInfo launcherActivityInfo) {
        return makeLaunchIntent(launcherActivityInfo.getComponentName());
    }

    public static Intent makeLaunchIntent(ComponentName componentName2) {
        return new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(componentName2).setFlags(270532608);
    }

    public ComponentName getTargetComponent() {
        return this.componentName;
    }

    public static void updateRuntimeFlagsForActivityTarget(ItemInfoWithIcon itemInfoWithIcon, LauncherActivityInfo launcherActivityInfo) {
        ApplicationInfo applicationInfo = launcherActivityInfo.getApplicationInfo();
        if (PackageManagerHelper.isAppSuspended(applicationInfo)) {
            itemInfoWithIcon.runtimeStatusFlags |= 4;
        }
        itemInfoWithIcon.runtimeStatusFlags |= (applicationInfo.flags & 1) == 0 ? 128 : 64;
        if (applicationInfo.targetSdkVersion >= 26 && Process.myUserHandle().equals(launcherActivityInfo.getUser())) {
            itemInfoWithIcon.runtimeStatusFlags |= 256;
        }
        itemInfoWithIcon.setProgressLevel(PackageManagerHelper.getLoadingProgress(launcherActivityInfo), 2);
    }

    public AppInfo clone() {
        return new AppInfo(this);
    }
}
