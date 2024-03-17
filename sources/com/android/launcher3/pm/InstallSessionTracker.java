package com.android.launcher3.pm;

import android.content.pm.LauncherApps;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.PackageUserKey;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;

public class InstallSessionTracker extends PackageInstaller.SessionCallback {
    private SparseArray<PackageUserKey> mActiveSessions = null;
    private final PackageInstaller mInstaller;
    private final LauncherApps mLauncherApps;
    private final WeakReference<Callback> mWeakCallback;
    private final WeakReference<InstallSessionHelper> mWeakHelper;

    public interface Callback {
        void onInstallSessionCreated(PackageInstallInfo packageInstallInfo);

        void onPackageStateChanged(PackageInstallInfo packageInstallInfo);

        void onSessionFailure(String str, UserHandle userHandle);

        void onUpdateSessionDisplay(PackageUserKey packageUserKey, PackageInstaller.SessionInfo sessionInfo);
    }

    public void onActiveChanged(int i, boolean z) {
    }

    InstallSessionTracker(InstallSessionHelper installSessionHelper, Callback callback, PackageInstaller packageInstaller, LauncherApps launcherApps) {
        this.mWeakHelper = new WeakReference<>(installSessionHelper);
        this.mWeakCallback = new WeakReference<>(callback);
        this.mInstaller = packageInstaller;
        this.mLauncherApps = launcherApps;
    }

    public void onCreated(int i) {
        InstallSessionHelper installSessionHelper = (InstallSessionHelper) this.mWeakHelper.get();
        Callback callback = (Callback) this.mWeakCallback.get();
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.MISSING_PROMISE_ICON, "Session created sessionId=" + i + ", callback=" + callback + ", helper=" + installSessionHelper);
        }
        if (callback != null && installSessionHelper != null) {
            PackageInstaller.SessionInfo pushSessionDisplayToLauncher = pushSessionDisplayToLauncher(i, installSessionHelper, callback);
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.MISSING_PROMISE_ICON, "Session created sessionId=" + i + ", sessionInfo=" + pushSessionDisplayToLauncher);
            }
            if (pushSessionDisplayToLauncher != null) {
                callback.onInstallSessionCreated(PackageInstallInfo.fromInstallingState(pushSessionDisplayToLauncher));
            }
            installSessionHelper.tryQueuePromiseAppIcon(pushSessionDisplayToLauncher);
        }
    }

    public void onFinished(int i, boolean z) {
        InstallSessionHelper installSessionHelper = (InstallSessionHelper) this.mWeakHelper.get();
        Callback callback = (Callback) this.mWeakCallback.get();
        if (callback != null && installSessionHelper != null) {
            SparseArray<PackageUserKey> activeSessionMap = getActiveSessionMap(installSessionHelper);
            PackageUserKey packageUserKey = activeSessionMap.get(i);
            activeSessionMap.remove(i);
            if (packageUserKey != null && packageUserKey.mPackageName != null) {
                String str = packageUserKey.mPackageName;
                callback.onPackageStateChanged(PackageInstallInfo.fromState(z ? 0 : 3, str, packageUserKey.mUser));
                if (!z && installSessionHelper.promiseIconAddedForId(i)) {
                    callback.onSessionFailure(str, packageUserKey.mUser);
                    installSessionHelper.removePromiseIconId(i);
                }
            }
        }
    }

    public void onProgressChanged(int i, float f) {
        PackageInstaller.SessionInfo verifiedSessionInfo;
        InstallSessionHelper installSessionHelper = (InstallSessionHelper) this.mWeakHelper.get();
        Callback callback = (Callback) this.mWeakCallback.get();
        if (callback != null && installSessionHelper != null && (verifiedSessionInfo = installSessionHelper.getVerifiedSessionInfo(i)) != null && verifiedSessionInfo.getAppPackageName() != null) {
            callback.onPackageStateChanged(PackageInstallInfo.fromInstallingState(verifiedSessionInfo));
        }
    }

    public void onBadgingChanged(int i) {
        PackageInstaller.SessionInfo pushSessionDisplayToLauncher;
        InstallSessionHelper installSessionHelper = (InstallSessionHelper) this.mWeakHelper.get();
        Callback callback = (Callback) this.mWeakCallback.get();
        if (callback != null && installSessionHelper != null && (pushSessionDisplayToLauncher = pushSessionDisplayToLauncher(i, installSessionHelper, callback)) != null) {
            installSessionHelper.tryQueuePromiseAppIcon(pushSessionDisplayToLauncher);
        }
    }

    private PackageInstaller.SessionInfo pushSessionDisplayToLauncher(int i, InstallSessionHelper installSessionHelper, Callback callback) {
        PackageInstaller.SessionInfo verifiedSessionInfo = installSessionHelper.getVerifiedSessionInfo(i);
        if (verifiedSessionInfo == null || verifiedSessionInfo.getAppPackageName() == null) {
            return null;
        }
        PackageUserKey packageUserKey = new PackageUserKey(verifiedSessionInfo.getAppPackageName(), InstallSessionHelper.getUserHandle(verifiedSessionInfo));
        getActiveSessionMap(installSessionHelper).put(verifiedSessionInfo.getSessionId(), packageUserKey);
        callback.onUpdateSessionDisplay(packageUserKey, verifiedSessionInfo);
        return verifiedSessionInfo;
    }

    private SparseArray<PackageUserKey> getActiveSessionMap(InstallSessionHelper installSessionHelper) {
        if (this.mActiveSessions == null) {
            this.mActiveSessions = new SparseArray<>();
            installSessionHelper.getActiveSessions().forEach(new BiConsumer() {
                public final void accept(Object obj, Object obj2) {
                    InstallSessionTracker.this.lambda$getActiveSessionMap$0$InstallSessionTracker((PackageUserKey) obj, (PackageInstaller.SessionInfo) obj2);
                }
            });
        }
        return this.mActiveSessions;
    }

    public /* synthetic */ void lambda$getActiveSessionMap$0$InstallSessionTracker(PackageUserKey packageUserKey, PackageInstaller.SessionInfo sessionInfo) {
        this.mActiveSessions.put(sessionInfo.getSessionId(), packageUserKey);
    }

    /* access modifiers changed from: package-private */
    public void register() {
        if (Build.VERSION.SDK_INT < 29) {
            this.mInstaller.registerSessionCallback(this, Executors.MODEL_EXECUTOR.getHandler());
        } else {
            this.mLauncherApps.registerPackageInstallerSessionCallback(Executors.MODEL_EXECUTOR, this);
        }
    }

    public void unregister() {
        if (Build.VERSION.SDK_INT < 29) {
            this.mInstaller.unregisterSessionCallback(this);
        } else {
            this.mLauncherApps.unregisterPackageInstallerSessionCallback(this);
        }
    }
}
