package com.android.launcher3.taskbar;

import com.android.launcher3.BaseQuickstepLauncher;

public class DesktopTaskbarUIController extends TaskbarUIController {
    private final BaseQuickstepLauncher mLauncher;

    public boolean supportsVisualStashing() {
        return false;
    }

    public DesktopTaskbarUIController(BaseQuickstepLauncher baseQuickstepLauncher) {
        this.mLauncher = baseQuickstepLauncher;
    }

    /* access modifiers changed from: protected */
    public void init(TaskbarControllers taskbarControllers) {
        this.mLauncher.getHotseat().setIconsAlpha(0.0f);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mLauncher.getHotseat().setIconsAlpha(1.0f);
    }
}
