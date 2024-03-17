package com.android.launcher3.model.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.android.launcher3.icons.BitmapInfo;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.pm.PackageInstallInfo;
import com.android.launcher3.util.PackageManagerHelper;

public abstract class ItemInfoWithIcon extends ItemInfo {
    public static final int FLAG_ADAPTIVE_ICON = 256;
    public static final int FLAG_DISABLED_BY_PUBLISHER = 16;
    public static final int FLAG_DISABLED_LOCKED_USER = 32;
    public static final int FLAG_DISABLED_MASK = 4159;
    public static final int FLAG_DISABLED_NOT_AVAILABLE = 2;
    public static final int FLAG_DISABLED_QUIET_USER = 8;
    public static final int FLAG_DISABLED_SAFEMODE = 1;
    public static final int FLAG_DISABLED_SUSPENDED = 4;
    public static final int FLAG_DISABLED_VERSION_LOWER = 4096;
    public static final int FLAG_ICON_BADGED = 512;
    public static final int FLAG_INCREMENTAL_DOWNLOAD_ACTIVE = 2048;
    public static final int FLAG_INSTALL_SESSION_ACTIVE = 1024;
    public static final int FLAG_SHOW_DOWNLOAD_PROGRESS_MASK = 3072;
    public static final int FLAG_SYSTEM_MASK = 192;
    public static final int FLAG_SYSTEM_NO = 128;
    public static final int FLAG_SYSTEM_YES = 64;
    public static final String TAG = "ItemInfoDebug";
    public BitmapInfo bitmap = BitmapInfo.LOW_RES_INFO;
    private int mProgressLevel = 100;
    public int runtimeStatusFlags = 0;

    public abstract ItemInfoWithIcon clone();

    protected ItemInfoWithIcon() {
    }

    protected ItemInfoWithIcon(ItemInfoWithIcon itemInfoWithIcon) {
        super(itemInfoWithIcon);
        this.bitmap = itemInfoWithIcon.bitmap;
        this.mProgressLevel = itemInfoWithIcon.mProgressLevel;
        this.runtimeStatusFlags = itemInfoWithIcon.runtimeStatusFlags;
        this.user = itemInfoWithIcon.user;
    }

    public boolean isDisabled() {
        return (this.runtimeStatusFlags & FLAG_DISABLED_MASK) != 0;
    }

    public boolean usingLowResIcon() {
        return this.bitmap.isLowRes();
    }

    public boolean isAppStartable() {
        int i = this.runtimeStatusFlags;
        return (i & 1024) == 0 && ((i & 2048) != 0 || this.mProgressLevel == 100);
    }

    public int getProgressLevel() {
        if ((this.runtimeStatusFlags & FLAG_SHOW_DOWNLOAD_PROGRESS_MASK) != 0) {
            return this.mProgressLevel;
        }
        return 100;
    }

    public void setProgressLevel(PackageInstallInfo packageInstallInfo) {
        setProgressLevel(packageInstallInfo.progress, packageInstallInfo.state);
        if (packageInstallInfo.state == 3) {
            FileLog.d(TAG, "Icon info: " + this + " marked broken with install info: " + packageInstallInfo, new Exception());
        }
    }

    public void setProgressLevel(int i, int i2) {
        int i3;
        int i4 = 100;
        if (i2 == 1) {
            this.mProgressLevel = i;
            if (i < 100) {
                i3 = this.runtimeStatusFlags | 1024;
            } else {
                i3 = this.runtimeStatusFlags & -1025;
            }
            this.runtimeStatusFlags = i3;
        } else if (i2 == 2) {
            this.mProgressLevel = i;
            int i5 = this.runtimeStatusFlags & -1025;
            this.runtimeStatusFlags = i5;
            this.runtimeStatusFlags = i < 100 ? i5 | 2048 : i5 & -2049;
        } else {
            if (i2 != 0) {
                i4 = 0;
            }
            this.mProgressLevel = i4;
            int i6 = this.runtimeStatusFlags & -1025;
            this.runtimeStatusFlags = i6;
            this.runtimeStatusFlags = i6 & -2049;
        }
    }

    public Intent getMarketIntent(Context context) {
        ComponentName targetComponent = getTargetComponent();
        if (targetComponent != null) {
            return new PackageManagerHelper(context).getMarketIntent(targetComponent.getPackageName());
        }
        return null;
    }

    public FastBitmapDrawable newIcon(Context context) {
        return newIcon(context, 0);
    }

    public FastBitmapDrawable newIcon(Context context, int i) {
        FastBitmapDrawable newIcon = this.bitmap.newIcon(context, i);
        newIcon.setIsDisabled(isDisabled());
        return newIcon;
    }
}
