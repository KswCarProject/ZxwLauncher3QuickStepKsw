package com.android.launcher3.statehandlers;

import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.UiThreadHelper;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.SystemUiProxy;

public class BackButtonAlphaHandler implements StateManager.StateHandler<LauncherState> {
    private final AnimatedFloat mBackAlpha = new AnimatedFloat(new Runnable() {
        public final void run() {
            BackButtonAlphaHandler.this.updateBackAlpha();
        }
    });
    private final BaseQuickstepLauncher mLauncher;

    public void setState(LauncherState launcherState) {
    }

    public BackButtonAlphaHandler(BaseQuickstepLauncher baseQuickstepLauncher) {
        this.mLauncher = baseQuickstepLauncher;
    }

    public void setStateWithAnimation(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        if (DisplayController.getNavigationMode(this.mLauncher) == DisplayController.NavigationMode.TWO_BUTTONS) {
            this.mBackAlpha.value = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getLastNavButtonAlpha();
            pendingAnimation.setFloat(this.mBackAlpha, AnimatedFloat.VALUE, this.mLauncher.shouldBackButtonBeHidden(launcherState) ? 0.0f : 1.0f, Interpolators.LINEAR);
        }
    }

    /* access modifiers changed from: private */
    public void updateBackAlpha() {
        UiThreadHelper.setBackButtonAlphaAsync(this.mLauncher, BaseQuickstepLauncher.SET_BACK_BUTTON_ALPHA, this.mBackAlpha.value, false);
    }
}
