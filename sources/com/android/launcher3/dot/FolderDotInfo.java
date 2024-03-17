package com.android.launcher3.dot;

import android.view.ViewDebug;
import com.android.launcher3.Utilities;

public class FolderDotInfo extends DotInfo {
    private static final int MIN_COUNT = 0;
    private int mNumNotifications;

    public void addDotInfo(DotInfo dotInfo) {
        if (dotInfo != null) {
            int size = this.mNumNotifications + dotInfo.getNotificationKeys().size();
            this.mNumNotifications = size;
            this.mNumNotifications = Utilities.boundToRange(size, 0, (int) DotInfo.MAX_COUNT);
        }
    }

    public void subtractDotInfo(DotInfo dotInfo) {
        if (dotInfo != null) {
            int size = this.mNumNotifications - dotInfo.getNotificationKeys().size();
            this.mNumNotifications = size;
            this.mNumNotifications = Utilities.boundToRange(size, 0, (int) DotInfo.MAX_COUNT);
        }
    }

    public int getNotificationCount() {
        return this.mNumNotifications;
    }

    @ViewDebug.ExportedProperty(category = "launcher")
    public boolean hasDot() {
        return this.mNumNotifications > 0;
    }
}
