package com.android.systemui.plugins.shared;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import java.io.PrintWriter;

public interface LauncherOverlayManager extends Application.ActivityLifecycleCallbacks {

    public interface LauncherOverlay {
        void onScrollChange(float f, boolean z);

        void onScrollInteractionBegin();

        void onScrollInteractionEnd();

        void setOverlayCallbacks(LauncherOverlayCallbacks launcherOverlayCallbacks);
    }

    public interface LauncherOverlayCallbacks {
        void onScrollChanged(float f);
    }

    void dump(String str, PrintWriter printWriter) {
    }

    void hideOverlay(int i) {
    }

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

    void onAttachedToWindow() {
    }

    void onDetachedFromWindow() {
    }

    void onDeviceProvideChanged() {
    }

    void openOverlay() {
    }

    boolean startSearch(byte[] bArr, Bundle bundle) {
        return false;
    }

    void hideOverlay(boolean z) {
        hideOverlay(z ? 200 : 0);
    }
}
