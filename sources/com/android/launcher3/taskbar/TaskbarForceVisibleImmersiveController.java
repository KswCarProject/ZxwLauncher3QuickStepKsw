package com.android.launcher3.taskbar;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.AnimatedFloat;
import java.util.Optional;
import java.util.function.Consumer;

public class TaskbarForceVisibleImmersiveController implements TouchController {
    private static final int NAV_BAR_ICONS_DIM_ANIMATION_DURATION_MS = 500;
    private static final int NAV_BAR_ICONS_DIM_ANIMATION_START_DELAY_MS = 4500;
    private static final float NAV_BAR_ICONS_DIM_PCT = 0.15f;
    private static final int NAV_BAR_ICONS_UNDIM_ANIMATION_DURATION_MS = 250;
    private static final float NAV_BAR_ICONS_UNDIM_PCT = 1.0f;
    private final TaskbarActivityContext mContext;
    private TaskbarControllers mControllers;
    private final Runnable mDimmingRunnable = new Runnable() {
        public final void run() {
            TaskbarForceVisibleImmersiveController.this.dimIcons();
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final AnimatedFloat mIconAlphaForDimming = new AnimatedFloat(new Runnable() {
        public final void run() {
            TaskbarForceVisibleImmersiveController.this.updateIconDimmingAlpha();
        }
    });
    private final Consumer<MultiValueAlpha> mImmersiveModeAlphaUpdater = new Consumer() {
        public final void accept(Object obj) {
            TaskbarForceVisibleImmersiveController.this.lambda$new$0$TaskbarForceVisibleImmersiveController((MultiValueAlpha) obj);
        }
    };
    private boolean mIsImmersiveMode;
    private final Runnable mUndimmingRunnable = new Runnable() {
        public final void run() {
            TaskbarForceVisibleImmersiveController.this.undimIcons();
        }
    };

    public /* synthetic */ void lambda$new$0$TaskbarForceVisibleImmersiveController(MultiValueAlpha multiValueAlpha) {
        multiValueAlpha.getProperty(0).setValue(this.mIconAlphaForDimming.value);
    }

    public TaskbarForceVisibleImmersiveController(TaskbarActivityContext taskbarActivityContext) {
        this.mContext = taskbarActivityContext;
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
    }

    public void updateSysuiFlags(int i) {
        this.mIsImmersiveMode = (i & 16777216) != 0;
        if (!this.mContext.isNavBarForceVisible()) {
            return;
        }
        if (this.mIsImmersiveMode) {
            startIconDimming();
        } else {
            startIconUndimming();
        }
    }

    public void onDestroy() {
        startIconUndimming();
    }

    private void startIconUndimming() {
        this.mHandler.removeCallbacks(this.mDimmingRunnable);
        this.mHandler.removeCallbacks(this.mUndimmingRunnable);
        this.mHandler.post(this.mUndimmingRunnable);
    }

    /* access modifiers changed from: private */
    public void undimIcons() {
        this.mIconAlphaForDimming.animateToValue(1.0f).setDuration(250).start();
    }

    private void startIconDimming() {
        this.mHandler.removeCallbacks(this.mDimmingRunnable);
        this.mHandler.postDelayed(this.mDimmingRunnable, (long) AccessibilityManagerCompat.getRecommendedTimeoutMillis(this.mContext, NAV_BAR_ICONS_DIM_ANIMATION_START_DELAY_MS, 5));
    }

    /* access modifiers changed from: private */
    public void dimIcons() {
        this.mIconAlphaForDimming.animateToValue(NAV_BAR_ICONS_DIM_PCT).setDuration(500).start();
    }

    private boolean isNavbarShownInImmersiveMode() {
        return this.mIsImmersiveMode && this.mContext.isNavBarForceVisible();
    }

    /* access modifiers changed from: private */
    public void updateIconDimmingAlpha() {
        getBackButtonAlphaOptional().ifPresent(this.mImmersiveModeAlphaUpdater);
        getHomeButtonAlphaOptional().ifPresent(this.mImmersiveModeAlphaUpdater);
    }

    private Optional<MultiValueAlpha> getBackButtonAlphaOptional() {
        TaskbarControllers taskbarControllers = this.mControllers;
        if (taskbarControllers == null || taskbarControllers.navbarButtonsViewController == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.mControllers.navbarButtonsViewController.getBackButtonAlpha());
    }

    private Optional<MultiValueAlpha> getHomeButtonAlphaOptional() {
        TaskbarControllers taskbarControllers = this.mControllers;
        if (taskbarControllers == null || taskbarControllers.navbarButtonsViewController == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.mControllers.navbarButtonsViewController.getHomeButtonAlpha());
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isNavbarShownInImmersiveMode() || this.mControllers.taskbarStashController.supportsManualStashing()) {
            return false;
        }
        return onControllerTouchEvent(motionEvent);
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            startIconUndimming();
            return false;
        } else if (action != 1 && action != 3) {
            return false;
        } else {
            startIconDimming();
            return false;
        }
    }
}
