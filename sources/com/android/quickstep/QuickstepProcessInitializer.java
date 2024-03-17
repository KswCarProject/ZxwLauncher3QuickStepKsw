package com.android.quickstep;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import android.view.ThreadedRenderer;
import com.android.launcher3.MainProcessInitializer;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;

public class QuickstepProcessInitializer extends MainProcessInitializer {
    private static final int SETUP_DELAY_MILLIS = 5000;
    private static final String TAG = "QuickstepProcessInitializer";

    public QuickstepProcessInitializer(Context context) {
        InteractionJankMonitorWrapper.cancel(-1);
    }

    /* access modifiers changed from: protected */
    public void init(Context context) {
        if (((UserManager) context.getSystemService("user")).isManagedProfile()) {
            context.getPackageManager().setApplicationEnabledSetting(context.getPackageName(), 2, 0);
            Log.w(TAG, "Disabling com.android.launcher3, unable to run in a managed profile");
            return;
        }
        super.init(context);
        ThreadedRenderer.setContextPriority(ThreadedRenderer.EGL_CONTEXT_PRIORITY_HIGH_IMG);
        try {
            ActivityManager.getService().enableBinderTracing();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to enable binder tracing", e);
        }
    }
}
