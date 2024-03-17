package com.android.launcher3.pm;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInstaller;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.SessionCommitReceiver;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.ItemInstallQueue;
import com.android.launcher3.pm.InstallSessionTracker;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InstallSessionHelper {
    private static final boolean DEBUG = false;
    public static final MainThreadInitializedObject<InstallSessionHelper> INSTANCE = new MainThreadInitializedObject<>($$Lambda$d1v8xDiD2NDGavmVk2vssxtNlg.INSTANCE);
    private static final String LOG = "InstallSessionHelper";
    protected static final String PROMISE_ICON_IDS = "promise_icon_ids";
    private final Context mAppContext;
    private final PackageInstaller mInstaller;
    private final LauncherApps mLauncherApps;
    private IntSet mPromiseIconIds;
    private final HashMap<String, Boolean> mSessionVerifiedMap = new HashMap<>();

    public InstallSessionHelper(Context context) {
        this.mInstaller = context.getPackageManager().getPackageInstaller();
        this.mAppContext = context.getApplicationContext();
        this.mLauncherApps = (LauncherApps) context.getSystemService(LauncherApps.class);
    }

    private IntSet getPromiseIconIds() {
        Preconditions.assertWorkerThread();
        IntSet intSet = this.mPromiseIconIds;
        if (intSet != null) {
            return intSet;
        }
        this.mPromiseIconIds = IntSet.wrap(IntArray.fromConcatString(Utilities.getPrefs(this.mAppContext).getString(PROMISE_ICON_IDS, "")));
        IntArray intArray = new IntArray();
        for (PackageInstaller.SessionInfo sessionId : getActiveSessions().values()) {
            intArray.add(sessionId.getSessionId());
        }
        IntArray intArray2 = new IntArray();
        for (int size = this.mPromiseIconIds.size() - 1; size >= 0; size--) {
            if (!intArray.contains(this.mPromiseIconIds.getArray().get(size))) {
                intArray2.add(this.mPromiseIconIds.getArray().get(size));
            }
        }
        for (int size2 = intArray2.size() - 1; size2 >= 0; size2--) {
            this.mPromiseIconIds.getArray().removeValue(intArray2.get(size2));
        }
        return this.mPromiseIconIds;
    }

    public HashMap<PackageUserKey, PackageInstaller.SessionInfo> getActiveSessions() {
        HashMap<PackageUserKey, PackageInstaller.SessionInfo> hashMap = new HashMap<>();
        for (PackageInstaller.SessionInfo next : getAllVerifiedSessions()) {
            hashMap.put(new PackageUserKey(next.getAppPackageName(), getUserHandle(next)), next);
        }
        return hashMap;
    }

    public PackageInstaller.SessionInfo getActiveSessionInfo(UserHandle userHandle, String str) {
        for (PackageInstaller.SessionInfo next : getAllVerifiedSessions()) {
            boolean equals = str.equals(next.getAppPackageName());
            if (Utilities.ATLEAST_Q && !userHandle.equals(getUserHandle(next))) {
                equals = false;
                continue;
            }
            if (equals) {
                return next;
            }
        }
        return null;
    }

    private void updatePromiseIconPrefs() {
        Utilities.getPrefs(this.mAppContext).edit().putString(PROMISE_ICON_IDS, getPromiseIconIds().getArray().toConcatString()).apply();
    }

    /* access modifiers changed from: package-private */
    public PackageInstaller.SessionInfo getVerifiedSessionInfo(int i) {
        return verify(this.mInstaller.getSessionInfo(i));
    }

    private PackageInstaller.SessionInfo verify(PackageInstaller.SessionInfo sessionInfo) {
        String str;
        String str2;
        boolean z = false;
        if (sessionInfo == null || sessionInfo.getInstallerPackageName() == null || TextUtils.isEmpty(sessionInfo.getAppPackageName())) {
            if (TestProtocol.sDebugTracing) {
                StringBuilder append = new StringBuilder().append("InstallSessionHelper verify, info=");
                if (sessionInfo == null) {
                    z = true;
                }
                StringBuilder append2 = append.append(z).append(", info install name");
                if (sessionInfo == null) {
                    str = null;
                } else {
                    str = sessionInfo.getInstallerPackageName();
                }
                StringBuilder append3 = append2.append(str).append(", empty pkg name");
                if (sessionInfo == null) {
                    str2 = null;
                } else {
                    str2 = sessionInfo.getAppPackageName();
                }
                Log.d(TestProtocol.MISSING_PROMISE_ICON, append3.append(TextUtils.isEmpty(str2)).toString());
            }
            return null;
        }
        String installerPackageName = sessionInfo.getInstallerPackageName();
        synchronized (this.mSessionVerifiedMap) {
            if (!this.mSessionVerifiedMap.containsKey(installerPackageName)) {
                if (new PackageManagerHelper(this.mAppContext).getApplicationInfo(installerPackageName, getUserHandle(sessionInfo), 1) != null) {
                    z = true;
                }
                this.mSessionVerifiedMap.put(installerPackageName, Boolean.valueOf(z));
            }
        }
        if (this.mSessionVerifiedMap.get(installerPackageName).booleanValue()) {
            return sessionInfo;
        }
        return null;
    }

    public List<PackageInstaller.SessionInfo> getAllVerifiedSessions() {
        List<PackageInstaller.SessionInfo> list;
        if (Utilities.ATLEAST_Q) {
            list = this.mLauncherApps.getAllPackageInstallerSessions();
        } else {
            list = this.mInstaller.getAllSessions();
        }
        ArrayList arrayList = new ArrayList(list);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (verify((PackageInstaller.SessionInfo) it.next()) == null) {
                it.remove();
            }
        }
        return arrayList;
    }

    public boolean restoreDbIfApplicable(PackageInstaller.SessionInfo sessionInfo) {
        if (!FeatureFlags.ENABLE_DATABASE_RESTORE.get() || !isRestore(sessionInfo)) {
            return false;
        }
        LauncherSettings.Settings.call(this.mAppContext.getContentResolver(), LauncherSettings.Settings.METHOD_RESTORE_BACKUP_TABLE);
        return true;
    }

    private static boolean isRestore(PackageInstaller.SessionInfo sessionInfo) {
        return sessionInfo.getInstallReason() == 2;
    }

    public boolean promiseIconAddedForId(int i) {
        return getPromiseIconIds().contains(i);
    }

    public void removePromiseIconId(int i) {
        if (promiseIconAddedForId(i)) {
            getPromiseIconIds().getArray().removeValue(i);
            updatePromiseIconPrefs();
        }
    }

    /* access modifiers changed from: package-private */
    public void tryQueuePromiseAppIcon(PackageInstaller.SessionInfo sessionInfo) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.MISSING_PROMISE_ICON, "InstallSessionHelper tryQueuePromiseAppIcon, FeatureFlags=" + FeatureFlags.PROMISE_APPS_NEW_INSTALLS.get() + ", SessionCommitReceiveEnabled" + SessionCommitReceiver.isEnabled(this.mAppContext) + ", verifySessionInfo(sessionInfo)=" + verifySessionInfo(sessionInfo) + ", !promiseIconAdded=" + (sessionInfo != null && !promiseIconAddedForId(sessionInfo.getSessionId())));
        }
        if (FeatureFlags.PROMISE_APPS_NEW_INSTALLS.get() && SessionCommitReceiver.isEnabled(this.mAppContext) && verifySessionInfo(sessionInfo) && !promiseIconAddedForId(sessionInfo.getSessionId())) {
            FileLog.d(LOG, "Adding package name to install queue: " + sessionInfo.getAppPackageName());
            ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mAppContext).queueItem(sessionInfo.getAppPackageName(), getUserHandle(sessionInfo));
            getPromiseIconIds().add(sessionInfo.getSessionId());
            updatePromiseIconPrefs();
        }
    }

    public boolean verifySessionInfo(PackageInstaller.SessionInfo sessionInfo) {
        Integer num;
        if (TestProtocol.sDebugTracing) {
            boolean z = sessionInfo == null || !new PackageManagerHelper(this.mAppContext).isAppInstalled(sessionInfo.getAppPackageName(), getUserHandle(sessionInfo));
            boolean z2 = sessionInfo != null && !TextUtils.isEmpty(sessionInfo.getAppLabel());
            StringBuilder append = new StringBuilder().append("InstallSessionHelper verifySessionInfo, verify(sessionInfo)=").append(verify(sessionInfo)).append(", reason=");
            if (sessionInfo == null) {
                num = null;
            } else {
                num = Integer.valueOf(sessionInfo.getInstallReason());
            }
            Log.d(TestProtocol.MISSING_PROMISE_ICON, append.append(num).append(", PackageManager.INSTALL_REASON_USER=").append(4).append(", hasIcon=").append((sessionInfo == null || sessionInfo.getAppIcon() == null) ? false : true).append(", label is ! empty=").append(z2).append(" +, app not installed=").append(z).toString());
        }
        if (verify(sessionInfo) == null || sessionInfo.getInstallReason() != 4 || sessionInfo.getAppIcon() == null || TextUtils.isEmpty(sessionInfo.getAppLabel()) || new PackageManagerHelper(this.mAppContext).isAppInstalled(sessionInfo.getAppPackageName(), getUserHandle(sessionInfo))) {
            return false;
        }
        return true;
    }

    public InstallSessionTracker registerInstallTracker(InstallSessionTracker.Callback callback) {
        InstallSessionTracker installSessionTracker = new InstallSessionTracker(this, callback, this.mInstaller, this.mLauncherApps);
        installSessionTracker.register();
        return installSessionTracker;
    }

    public static UserHandle getUserHandle(PackageInstaller.SessionInfo sessionInfo) {
        return Utilities.ATLEAST_Q ? sessionInfo.getUser() : Process.myUserHandle();
    }
}
