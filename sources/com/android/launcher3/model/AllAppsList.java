package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.LocaleList;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.AppFilter;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.pm.PackageInstallInfo;
import com.android.launcher3.util.FlagOp;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.SafeCloseable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AllAppsList {
    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;
    private static final Consumer<AppInfo> NO_OP_CONSUMER = $$Lambda$AllAppsList$MJCf_L9puQCI7ysUJC4z3OhgTr8.INSTANCE;
    private static final String TAG = "AllAppsList";
    public final ArrayList<AppInfo> data = new ArrayList<>(42);
    private AppFilter mAppFilter;
    private boolean mDataChanged = false;
    private int mFlags;
    private IconCache mIconCache;
    private AlphabeticIndexCompat mIndex;
    private Consumer<AppInfo> mRemoveListener = NO_OP_CONSUMER;

    static /* synthetic */ void lambda$static$0(AppInfo appInfo) {
    }

    public AllAppsList(IconCache iconCache, AppFilter appFilter) {
        this.mIconCache = iconCache;
        this.mAppFilter = appFilter;
        this.mIndex = new AlphabeticIndexCompat(LocaleList.getDefault());
    }

    public boolean getAndResetChangeFlag() {
        boolean z = this.mDataChanged;
        this.mDataChanged = false;
        return z;
    }

    public boolean hasShortcutHostPermission() {
        return (this.mFlags & 1) != 0;
    }

    public void setFlags(int i, boolean z) {
        if (z) {
            this.mFlags = i | this.mFlags;
        } else {
            this.mFlags = (~i) & this.mFlags;
        }
        this.mDataChanged = true;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void add(AppInfo appInfo, LauncherActivityInfo launcherActivityInfo) {
        add(appInfo, launcherActivityInfo, true);
    }

    public void add(AppInfo appInfo, LauncherActivityInfo launcherActivityInfo, boolean z) {
        if (this.mAppFilter.shouldShowApp(appInfo.componentName) && findAppInfo(appInfo.componentName, appInfo.user) == null) {
            if (z) {
                this.mIconCache.getTitleAndIcon(appInfo, launcherActivityInfo, false);
                appInfo.sectionName = this.mIndex.computeSectionName(appInfo.title);
            } else {
                appInfo.title = "";
            }
            this.data.add(appInfo);
            this.mDataChanged = true;
        }
    }

    public AppInfo addPromiseApp(Context context, PackageInstallInfo packageInstallInfo) {
        return addPromiseApp(context, packageInstallInfo, true);
    }

    public AppInfo addPromiseApp(Context context, PackageInstallInfo packageInstallInfo, boolean z) {
        if (new PackageManagerHelper(context).isAppInstalled(packageInstallInfo.packageName, packageInstallInfo.user)) {
            return null;
        }
        AppInfo appInfo = new AppInfo(packageInstallInfo);
        if (z) {
            this.mIconCache.getTitleAndIcon(appInfo, appInfo.usingLowResIcon());
            appInfo.sectionName = this.mIndex.computeSectionName(appInfo.title);
        } else {
            appInfo.title = "";
        }
        this.data.add(appInfo);
        this.mDataChanged = true;
        return appInfo;
    }

    public void updateSectionName(AppInfo appInfo) {
        appInfo.sectionName = this.mIndex.computeSectionName(appInfo.title);
    }

    public List<AppInfo> updatePromiseInstallInfo(PackageInstallInfo packageInstallInfo) {
        ArrayList arrayList = new ArrayList();
        UserHandle userHandle = packageInstallInfo.user;
        for (int size = this.data.size() - 1; size >= 0; size--) {
            AppInfo appInfo = this.data.get(size);
            ComponentName targetComponent = appInfo.getTargetComponent();
            if (targetComponent != null && targetComponent.getPackageName().equals(packageInstallInfo.packageName) && appInfo.user.equals(userHandle)) {
                if (packageInstallInfo.state == 2 || packageInstallInfo.state == 1) {
                    if (!appInfo.isAppStartable() || packageInstallInfo.state != 1) {
                        appInfo.setProgressLevel(packageInstallInfo);
                        arrayList.add(appInfo);
                    }
                } else if (packageInstallInfo.state == 3 && !appInfo.isAppStartable()) {
                    removeApp(size);
                }
            }
        }
        return arrayList;
    }

    private void removeApp(int i) {
        AppInfo remove = this.data.remove(i);
        if (remove != null) {
            this.mDataChanged = true;
            this.mRemoveListener.accept(remove);
        }
    }

    public void clear() {
        this.data.clear();
        this.mDataChanged = false;
        this.mIndex = new AlphabeticIndexCompat(LocaleList.getDefault());
    }

    public List<LauncherActivityInfo> addPackage(Context context, String str, UserHandle userHandle) {
        List<LauncherActivityInfo> activityList = ((LauncherApps) context.getSystemService(LauncherApps.class)).getActivityList(str, userHandle);
        for (LauncherActivityInfo next : activityList) {
            add(new AppInfo(context, next, userHandle), next);
        }
        return activityList;
    }

    public void removePackage(String str, UserHandle userHandle) {
        ArrayList<AppInfo> arrayList = this.data;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            AppInfo appInfo = arrayList.get(size);
            if (appInfo.user.equals(userHandle) && str.equals(appInfo.componentName.getPackageName())) {
                removeApp(size);
            }
        }
    }

    public void updateDisabledFlags(Predicate<ItemInfo> predicate, FlagOp flagOp) {
        ArrayList<AppInfo> arrayList = this.data;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            AppInfo appInfo = arrayList.get(size);
            if (predicate.test(appInfo)) {
                appInfo.runtimeStatusFlags = flagOp.apply(appInfo.runtimeStatusFlags);
                this.mDataChanged = true;
            }
        }
    }

    public void updateIconsAndLabels(HashSet<String> hashSet, UserHandle userHandle) {
        Iterator<AppInfo> it = this.data.iterator();
        while (it.hasNext()) {
            AppInfo next = it.next();
            if (next.user.equals(userHandle) && hashSet.contains(next.componentName.getPackageName())) {
                this.mIconCache.updateTitleAndIcon(next);
                next.sectionName = this.mIndex.computeSectionName(next.title);
                this.mDataChanged = true;
            }
        }
    }

    public List<LauncherActivityInfo> updatePackage(Context context, String str, UserHandle userHandle) {
        List<LauncherActivityInfo> activityList = ((LauncherApps) context.getSystemService(LauncherApps.class)).getActivityList(str, userHandle);
        if (activityList.size() > 0) {
            for (int size = this.data.size() - 1; size >= 0; size--) {
                AppInfo appInfo = this.data.get(size);
                if (userHandle.equals(appInfo.user) && str.equals(appInfo.componentName.getPackageName()) && !findActivity(activityList, appInfo.componentName)) {
                    Log.w(TAG, "Changing shortcut target due to app component name change.");
                    removeApp(size);
                }
            }
            for (LauncherActivityInfo next : activityList) {
                AppInfo findAppInfo = findAppInfo(next.getComponentName(), userHandle);
                if (findAppInfo == null) {
                    add(new AppInfo(context, next, userHandle), next);
                } else {
                    Intent makeLaunchIntent = AppInfo.makeLaunchIntent(next);
                    this.mIconCache.getTitleAndIcon(findAppInfo, next, false);
                    findAppInfo.sectionName = this.mIndex.computeSectionName(findAppInfo.title);
                    findAppInfo.setProgressLevel(PackageManagerHelper.getLoadingProgress(next), 2);
                    findAppInfo.intent = makeLaunchIntent;
                    this.mDataChanged = true;
                }
            }
        } else {
            for (int size2 = this.data.size() - 1; size2 >= 0; size2--) {
                AppInfo appInfo2 = this.data.get(size2);
                if (userHandle.equals(appInfo2.user) && str.equals(appInfo2.componentName.getPackageName())) {
                    this.mIconCache.remove(appInfo2.componentName, userHandle);
                    removeApp(size2);
                }
            }
        }
        return activityList;
    }

    private static boolean findActivity(List<LauncherActivityInfo> list, ComponentName componentName) {
        for (LauncherActivityInfo componentName2 : list) {
            if (componentName2.getComponentName().equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    public AppInfo findAppInfo(ComponentName componentName, UserHandle userHandle) {
        Iterator<AppInfo> it = this.data.iterator();
        while (it.hasNext()) {
            AppInfo next = it.next();
            if (componentName.equals(next.componentName) && userHandle.equals(next.user)) {
                return next;
            }
        }
        return null;
    }

    public AppInfo[] copyData() {
        AppInfo[] appInfoArr = (AppInfo[]) this.data.toArray(AppInfo.EMPTY_ARRAY);
        Arrays.sort(appInfoArr, AppInfo.COMPONENT_KEY_COMPARATOR);
        return appInfoArr;
    }

    public SafeCloseable trackRemoves(Consumer<AppInfo> consumer) {
        this.mRemoveListener = consumer;
        return new SafeCloseable() {
            public final void close() {
                AllAppsList.this.lambda$trackRemoves$1$AllAppsList();
            }
        };
    }

    public /* synthetic */ void lambda$trackRemoves$1$AllAppsList() {
        this.mRemoveListener = NO_OP_CONSUMER;
    }
}
