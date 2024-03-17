package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.system.ActivityManagerWrapper;

public class QuickSwitchTouchController extends AbstractStateChangeTouchController {
    protected final RecentsView mOverviewPanel;

    public QuickSwitchTouchController(Launcher launcher) {
        this(launcher, SingleAxisSwipeDetector.HORIZONTAL);
    }

    protected QuickSwitchTouchController(Launcher launcher, SingleAxisSwipeDetector.Direction direction) {
        super(launcher, direction);
        this.mOverviewPanel = (RecentsView) launcher.getOverviewPanel();
    }

    /* access modifiers changed from: protected */
    public boolean canInterceptTouch(MotionEvent motionEvent) {
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (this.mLauncher.isInState(LauncherState.NORMAL) && (motionEvent.getEdgeFlags() & 256) != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState launcherState, boolean z) {
        if ((SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags() & 128) != 0) {
            return LauncherState.NORMAL;
        }
        return z ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
    }

    public void onDragStart(boolean z, float f) {
        super.onDragStart(z, f);
        this.mStartContainerType = 1;
        TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
    }

    /* access modifiers changed from: protected */
    public void onSwipeInteractionCompleted(LauncherState launcherState) {
        super.lambda$onDragEnd$0$AbstractStateChangeTouchController(launcherState);
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        setupInterpolators(stateAnimationConfig);
        stateAnimationConfig.duration = (long) (getShiftRange() * 2.0f);
        RecentsView.RECENTS_SCALE_PROPERTY.set(this.mOverviewPanel, Float.valueOf(LauncherState.QUICK_SWITCH.getOverviewScaleAndOffset(this.mLauncher)[0] * 0.85f));
        RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET.set(this.mOverviewPanel, Float.valueOf(1.0f));
        Log.d(TestProtocol.BAD_STATE, "QuickSwitchTouchController initCurrentAnimation setContentAlpha=1");
        this.mOverviewPanel.setContentAlpha(1.0f);
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, stateAnimationConfig);
        this.mCurrentAnimation.getTarget().addListener(this.mClearStateOnCancelListener);
        this.mCurrentAnimation.getAnimationPlayer().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                QuickSwitchTouchController.this.lambda$initCurrentAnimation$0$QuickSwitchTouchController(valueAnimator);
            }
        });
        return 1.0f / getShiftRange();
    }

    public /* synthetic */ void lambda$initCurrentAnimation$0$QuickSwitchTouchController(ValueAnimator valueAnimator) {
        updateFullscreenProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void setupInterpolators(StateAnimationConfig stateAnimationConfig) {
        stateAnimationConfig.setInterpolator(3, Interpolators.DEACCEL_2);
        stateAnimationConfig.setInterpolator(10, Interpolators.DEACCEL_2);
        if (DisplayController.getNavigationMode(this.mLauncher) == DisplayController.NavigationMode.NO_BUTTON) {
            stateAnimationConfig.setInterpolator(2, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(0, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(6, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(8, Interpolators.ACCEL_2);
            stateAnimationConfig.setInterpolator(9, Interpolators.INSTANT);
            return;
        }
        stateAnimationConfig.setInterpolator(2, Interpolators.LINEAR);
        stateAnimationConfig.setInterpolator(0, Interpolators.LINEAR);
    }

    /* access modifiers changed from: protected */
    public void updateProgress(float f) {
        super.updateProgress(f);
        updateFullscreenProgress(Utilities.boundToRange(f, 0.0f, 1.0f));
    }

    private void updateFullscreenProgress(float f) {
        this.mOverviewPanel.setFullscreenProgress(f);
        int i = 0;
        if (f > 0.85f) {
            TaskView taskViewAt = this.mOverviewPanel.getTaskViewAt(0);
            if (taskViewAt != null) {
                i = taskViewAt.getThumbnail().getSysUiStatusNavFlags();
            }
            this.mLauncher.getSystemUiController().updateUiState(3, i);
            return;
        }
        this.mLauncher.getSystemUiController().updateUiState(3, 0);
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return ((float) this.mLauncher.getDeviceProfile().widthPx) / 2.0f;
    }
}
