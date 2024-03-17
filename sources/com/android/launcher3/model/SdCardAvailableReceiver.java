package com.android.launcher3.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Set;

public class SdCardAvailableReceiver extends BroadcastReceiver {
    private final Context mContext;
    private final LauncherModel mModel;
    private final Set<PackageUserKey> mPackages;

    public SdCardAvailableReceiver(LauncherAppState launcherAppState, Set<PackageUserKey> set) {
        this.mModel = launcherAppState.getModel();
        this.mContext = launcherAppState.getContext();
        this.mPackages = set;
    }

    public void onReceive(Context context, Intent intent) {
        LauncherApps launcherApps = (LauncherApps) context.getSystemService(LauncherApps.class);
        PackageManagerHelper packageManagerHelper = new PackageManagerHelper(context);
        for (PackageUserKey next : this.mPackages) {
            UserHandle userHandle = next.mUser;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            if (!launcherApps.isPackageEnabled(next.mPackageName, userHandle)) {
                if (packageManagerHelper.isAppOnSdcard(next.mPackageName, userHandle)) {
                    arrayList2.add(next.mPackageName);
                } else {
                    arrayList.add(next.mPackageName);
                }
            }
            if (!arrayList.isEmpty()) {
                this.mModel.onPackagesRemoved(userHandle, (String[]) arrayList.toArray(new String[arrayList.size()]));
            }
            if (!arrayList2.isEmpty()) {
                this.mModel.onPackagesUnavailable((String[]) arrayList2.toArray(new String[arrayList2.size()]), userHandle, false);
            }
        }
        this.mContext.unregisterReceiver(this);
    }
}
