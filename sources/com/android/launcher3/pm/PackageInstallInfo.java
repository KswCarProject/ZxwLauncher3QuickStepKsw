package com.android.launcher3.pm;

import android.content.ComponentName;
import android.content.pm.PackageInstaller;
import android.os.UserHandle;

public final class PackageInstallInfo {
    public static final int STATUS_FAILED = 3;
    public static final int STATUS_INSTALLED = 0;
    public static final int STATUS_INSTALLED_DOWNLOADING = 2;
    public static final int STATUS_INSTALLING = 1;
    public final ComponentName componentName;
    public final String packageName;
    public final int progress;
    public final int state;
    public final UserHandle user;

    private PackageInstallInfo(PackageInstaller.SessionInfo sessionInfo) {
        this.state = 1;
        String appPackageName = sessionInfo.getAppPackageName();
        this.packageName = appPackageName;
        this.componentName = new ComponentName(appPackageName, "");
        this.progress = (int) (sessionInfo.getProgress() * 100.0f);
        this.user = InstallSessionHelper.getUserHandle(sessionInfo);
    }

    public PackageInstallInfo(String str, int i, int i2, UserHandle userHandle) {
        this.state = i;
        this.packageName = str;
        this.componentName = new ComponentName(str, "");
        this.progress = i2;
        this.user = userHandle;
    }

    public static PackageInstallInfo fromInstallingState(PackageInstaller.SessionInfo sessionInfo) {
        return new PackageInstallInfo(sessionInfo);
    }

    public static PackageInstallInfo fromState(int i, String str, UserHandle userHandle) {
        return new PackageInstallInfo(str, i, 0, userHandle);
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + dumpProperties() + ")";
    }

    private String dumpProperties() {
        return "componentName=" + this.componentName + "packageName=" + this.packageName + " state=" + stateToString() + " progress=" + this.progress + " user=" + this.user;
    }

    private String stateToString() {
        int i = this.state;
        if (i == 0) {
            return "STATUS_INSTALLED";
        }
        if (i == 1) {
            return "STATUS_INSTALLING";
        }
        if (i != 2) {
            return i != 3 ? "INVALID STATE" : "STATUS_FAILED";
        }
        return "STATUS_INSTALLED_DOWNLOADING";
    }
}
