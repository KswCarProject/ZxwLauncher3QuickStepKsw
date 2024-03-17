package com.android.launcher3.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public interface ActivityLifecycleCallbacksAdapter extends Application.ActivityLifecycleCallbacks {
    void onActivityCreated(Activity activity, Bundle bundle) {
    }

    void onActivityDestroyed(Activity activity) {
    }

    void onActivityPaused(Activity activity) {
    }

    void onActivityResumed(Activity activity) {
    }

    void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    void onActivityStarted(Activity activity) {
    }

    void onActivityStopped(Activity activity) {
    }
}
