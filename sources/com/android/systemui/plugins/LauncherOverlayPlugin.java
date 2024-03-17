package com.android.systemui.plugins;

import android.app.Activity;
import com.android.systemui.plugins.annotations.ProvidesInterface;
import com.android.systemui.plugins.shared.LauncherExterns;
import com.android.systemui.plugins.shared.LauncherOverlayManager;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_LAUNCHER_OVERLAY", version = 1)
public interface LauncherOverlayPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_LAUNCHER_OVERLAY";
    public static final int VERSION = 1;

    LauncherOverlayManager createOverlayManager(Activity activity, LauncherExterns launcherExterns);
}
