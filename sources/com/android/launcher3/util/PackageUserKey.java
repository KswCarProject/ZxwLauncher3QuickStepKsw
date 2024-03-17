package com.android.launcher3.util;

import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.PackageItemInfo;
import java.util.Objects;

public class PackageUserKey {
    private int mHashCode;
    public String mPackageName;
    public UserHandle mUser;
    public int mWidgetCategory;

    public static PackageUserKey fromItemInfo(ItemInfo itemInfo) {
        if (itemInfo.getTargetComponent() == null) {
            return null;
        }
        return new PackageUserKey(itemInfo.getTargetComponent().getPackageName(), itemInfo.user);
    }

    public static PackageUserKey fromNotification(StatusBarNotification statusBarNotification) {
        return new PackageUserKey(statusBarNotification.getPackageName(), statusBarNotification.getUser());
    }

    public static PackageUserKey fromPackageItemInfo(PackageItemInfo packageItemInfo) {
        if (!TextUtils.isEmpty(packageItemInfo.packageName) || packageItemInfo.widgetCategory == -1) {
            return new PackageUserKey(packageItemInfo.packageName, packageItemInfo.user);
        }
        return new PackageUserKey(packageItemInfo.widgetCategory, packageItemInfo.user);
    }

    public PackageUserKey(String str, UserHandle userHandle) {
        update(str, userHandle);
    }

    public PackageUserKey(int i, UserHandle userHandle) {
        update("", i, userHandle);
    }

    public void update(String str, UserHandle userHandle) {
        update(str, -1, userHandle);
    }

    private void update(String str, int i, UserHandle userHandle) {
        this.mPackageName = str;
        this.mWidgetCategory = i;
        this.mUser = userHandle;
        this.mHashCode = Objects.hash(new Object[]{str, Integer.valueOf(i), userHandle});
    }

    public boolean updateFromItemInfo(ItemInfo itemInfo) {
        if (itemInfo.getTargetComponent() == null || !ShortcutUtil.supportsShortcuts(itemInfo)) {
            return false;
        }
        update(itemInfo.getTargetComponent().getPackageName(), itemInfo.user);
        return true;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PackageUserKey)) {
            return false;
        }
        PackageUserKey packageUserKey = (PackageUserKey) obj;
        if (!Objects.equals(this.mPackageName, packageUserKey.mPackageName) || this.mWidgetCategory != packageUserKey.mWidgetCategory || !Objects.equals(this.mUser, packageUserKey.mUser)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return this.mPackageName + "#" + this.mUser + ",category=" + this.mWidgetCategory;
    }
}
