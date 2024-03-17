package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.ItemInstallQueue;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Executors;

public class SessionCommitReceiver extends BroadcastReceiver {
    public static final String ADD_ICON_PREFERENCE_KEY = "pref_add_icon_to_home";
    private static final String LOG = "SessionCommitReceiver";

    public void onReceive(Context context, Intent intent) {
        Executors.MODEL_EXECUTOR.execute(new Runnable(context, intent) {
            public final /* synthetic */ Context f$0;
            public final /* synthetic */ Intent f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void run() {
                SessionCommitReceiver.processIntent(this.f$0, this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    public static void processIntent(Context context, Intent intent) {
        if (isEnabled(context)) {
            PackageInstaller.SessionInfo sessionInfo = (PackageInstaller.SessionInfo) intent.getParcelableExtra("android.content.pm.extra.SESSION");
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if ("android.content.pm.action.SESSION_COMMITTED".equals(intent.getAction()) && sessionInfo != null && userHandle != null) {
                InstallSessionHelper installSessionHelper = InstallSessionHelper.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
                installSessionHelper.restoreDbIfApplicable(sessionInfo);
                if (TextUtils.isEmpty(sessionInfo.getAppPackageName()) || sessionInfo.getInstallReason() != 4 || installSessionHelper.promiseIconAddedForId(sessionInfo.getSessionId())) {
                    installSessionHelper.removePromiseIconId(sessionInfo.getSessionId());
                    if (TestProtocol.sDebugTracing) {
                        Log.d(TestProtocol.MISSING_PROMISE_ICON, "SessionCommitReceiver, TextUtils.isEmpty=" + TextUtils.isEmpty(sessionInfo.getAppPackageName()) + ", info.getInstallReason()=" + sessionInfo.getInstallReason() + ", INSTALL_REASON_USER=" + 4 + ", icon added=" + installSessionHelper.promiseIconAddedForId(sessionInfo.getSessionId()));
                        return;
                    }
                    return;
                }
                FileLog.d(LOG, "Adding package name to install queue. Package name: " + sessionInfo.getAppPackageName() + ", has app icon: " + (sessionInfo.getAppIcon() != null) + ", has app label: " + (!TextUtils.isEmpty(sessionInfo.getAppLabel())));
                ItemInstallQueue.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).queueItem(sessionInfo.getAppPackageName(), userHandle);
            } else if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.MISSING_PROMISE_ICON, "SessionCommitReceiver invalid intent");
            }
        } else if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.MISSING_PROMISE_ICON, "SessionCommitReceiver not enabled");
        }
    }

    public static boolean isEnabled(Context context) {
        return Utilities.getPrefs(context).getBoolean(ADD_ICON_PREFERENCE_KEY, true);
    }
}
