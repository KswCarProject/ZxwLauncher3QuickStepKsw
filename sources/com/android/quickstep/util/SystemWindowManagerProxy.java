package com.android.quickstep.util;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;
import com.android.launcher3.util.window.WindowManagerProxy;

public class SystemWindowManagerProxy extends WindowManagerProxy {
    public SystemWindowManagerProxy(Context context) {
        super(true);
    }

    /* access modifiers changed from: protected */
    public String getDisplayId(Display display) {
        return display.getUniqueId();
    }

    public boolean isInternalDisplay(Display display) {
        return display.getType() == 1;
    }

    public int getRotation(Context context) {
        return context.getResources().getConfiguration().windowConfiguration.getRotation();
    }

    /* access modifiers changed from: protected */
    public Display[] getDisplays(Context context) {
        return ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplays("android.hardware.display.category.ALL_INCLUDING_DISABLED");
    }
}
