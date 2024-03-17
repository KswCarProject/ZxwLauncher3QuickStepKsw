package com.android.launcher3.model.data;

import android.os.UserHandle;
import java.util.Objects;

public class PackageItemInfo extends ItemInfoWithIcon {
    public final String packageName;
    public final int widgetCategory;

    public PackageItemInfo(String str, UserHandle userHandle) {
        this(str, -1, userHandle);
    }

    public PackageItemInfo(String str, int i, UserHandle userHandle) {
        this.packageName = str;
        this.widgetCategory = i;
        this.user = userHandle;
        this.itemType = -1;
    }

    public PackageItemInfo(PackageItemInfo packageItemInfo) {
        this.packageName = packageItemInfo.packageName;
        this.widgetCategory = packageItemInfo.widgetCategory;
        this.user = packageItemInfo.user;
        this.itemType = -1;
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " packageName=" + this.packageName;
    }

    public PackageItemInfo clone() {
        return new PackageItemInfo(this);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PackageItemInfo packageItemInfo = (PackageItemInfo) obj;
        if (!Objects.equals(this.packageName, packageItemInfo.packageName) || !Objects.equals(this.user, packageItemInfo.user) || this.widgetCategory != packageItemInfo.widgetCategory) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.packageName, this.user, Integer.valueOf(this.widgetCategory)});
    }
}
