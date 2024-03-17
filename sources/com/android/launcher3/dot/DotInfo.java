package com.android.launcher3.dot;

import com.android.launcher3.notification.NotificationKeyData;
import java.util.ArrayList;
import java.util.List;

public class DotInfo {
    public static final int MAX_COUNT = 999;
    private final List<NotificationKeyData> mNotificationKeys = new ArrayList();
    private int mTotalCount;

    public boolean addOrUpdateNotificationKey(NotificationKeyData notificationKeyData) {
        NotificationKeyData notificationKeyData2;
        int indexOf = this.mNotificationKeys.indexOf(notificationKeyData);
        if (indexOf == -1) {
            notificationKeyData2 = null;
        } else {
            notificationKeyData2 = this.mNotificationKeys.get(indexOf);
        }
        if (notificationKeyData2 == null) {
            boolean add = this.mNotificationKeys.add(notificationKeyData);
            if (add) {
                this.mTotalCount += notificationKeyData.count;
            }
            return add;
        } else if (notificationKeyData2.count == notificationKeyData.count) {
            return false;
        } else {
            int i = this.mTotalCount - notificationKeyData2.count;
            this.mTotalCount = i;
            this.mTotalCount = i + notificationKeyData.count;
            notificationKeyData2.count = notificationKeyData.count;
            return true;
        }
    }

    public boolean removeNotificationKey(NotificationKeyData notificationKeyData) {
        boolean remove = this.mNotificationKeys.remove(notificationKeyData);
        if (remove) {
            this.mTotalCount -= notificationKeyData.count;
        }
        return remove;
    }

    public List<NotificationKeyData> getNotificationKeys() {
        return this.mNotificationKeys;
    }

    public int getNotificationCount() {
        return Math.min(this.mTotalCount, MAX_COUNT);
    }

    public String toString() {
        return Integer.toString(this.mTotalCount);
    }
}
