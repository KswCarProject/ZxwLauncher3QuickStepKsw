package com.android.launcher3.notification;

import java.util.HashSet;
import java.util.Set;

public class NotificationGroup {
    private Set<String> mChildKeys = new HashSet();
    private String mGroupSummaryKey;

    public void setGroupSummaryKey(String str) {
        this.mGroupSummaryKey = str;
    }

    public String getGroupSummaryKey() {
        return this.mGroupSummaryKey;
    }

    public void addChildKey(String str) {
        this.mChildKeys.add(str);
    }

    public void removeChildKey(String str) {
        this.mChildKeys.remove(str);
    }

    public boolean isEmpty() {
        return this.mChildKeys.isEmpty();
    }
}
