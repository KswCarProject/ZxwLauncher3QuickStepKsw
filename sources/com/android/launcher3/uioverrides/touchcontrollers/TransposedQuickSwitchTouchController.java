package com.android.launcher3.uioverrides.touchcontrollers;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.touch.SingleAxisSwipeDetector;

public class TransposedQuickSwitchTouchController extends QuickSwitchTouchController {
    public TransposedQuickSwitchTouchController(Launcher launcher) {
        super(launcher, SingleAxisSwipeDetector.VERTICAL);
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState launcherState, boolean z) {
        return super.getTargetState(launcherState, z ^ this.mLauncher.getDeviceProfile().isSeascape());
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation() {
        float initCurrentAnimation = super.initCurrentAnimation();
        return this.mLauncher.getDeviceProfile().isSeascape() ? initCurrentAnimation : -initCurrentAnimation;
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return ((float) this.mLauncher.getDeviceProfile().heightPx) / 2.0f;
    }
}
