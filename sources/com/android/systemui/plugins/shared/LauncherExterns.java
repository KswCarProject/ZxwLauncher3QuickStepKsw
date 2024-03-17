package com.android.systemui.plugins.shared;

import android.content.SharedPreferences;
import com.android.systemui.plugins.shared.LauncherOverlayManager;

public interface LauncherExterns {
    SharedPreferences getDevicePrefs();

    SharedPreferences getSharedPrefs();

    void runOnOverlayHidden(Runnable runnable);

    void setLauncherOverlay(LauncherOverlayManager.LauncherOverlay launcherOverlay);
}
