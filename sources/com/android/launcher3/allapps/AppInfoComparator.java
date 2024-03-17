package com.android.launcher3.allapps;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.LabelComparator;
import java.util.Comparator;

public class AppInfoComparator implements Comparator<AppInfo> {
    private final LabelComparator mLabelComparator = new LabelComparator();
    private final UserHandle mMyUser = Process.myUserHandle();
    private final UserCache mUserManager;

    public AppInfoComparator(Context context) {
        this.mUserManager = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
    }

    public int compare(AppInfo appInfo, AppInfo appInfo2) {
        LabelComparator labelComparator = this.mLabelComparator;
        String str = "";
        String charSequence = appInfo.title == null ? str : appInfo.title.toString();
        if (appInfo2.title != null) {
            str = appInfo2.title.toString();
        }
        int compare = labelComparator.compare(charSequence, str);
        if (compare != 0) {
            return compare;
        }
        int compareTo = appInfo.componentName.compareTo(appInfo2.componentName);
        if (compareTo != 0) {
            return compareTo;
        }
        if (this.mMyUser.equals(appInfo.user)) {
            return -1;
        }
        return Long.valueOf(this.mUserManager.getSerialNumberForUser(appInfo.user)).compareTo(Long.valueOf(this.mUserManager.getSerialNumberForUser(appInfo2.user)));
    }
}
