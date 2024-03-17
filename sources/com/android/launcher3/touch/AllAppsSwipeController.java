package com.android.launcher3.touch;

import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.states.StateAnimationConfig;

public class AllAppsSwipeController extends AbstractStateChangeTouchController {
    public static final Interpolator ALLAPPS_STAGGERED_FADE_EARLY_RESPONDER = Interpolators.clampToProgress(Interpolators.LINEAR, 0.0f, 0.5f);
    public static final Interpolator ALLAPPS_STAGGERED_FADE_LATE_RESPONDER = Interpolators.clampToProgress(Interpolators.LINEAR, 0.5f, 1.0f);
    private static final float ALLAPPS_STAGGERED_FADE_THRESHOLD = 0.5f;
    public static final Interpolator ALL_APPS_FADE = Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.DECELERATED_EASE, 0.2f, 1.0f), (float) ALL_APPS_STATE_TRANSITION, (float) ALL_APPS_FADE_END);
    private static final float ALL_APPS_FADE_END = 0.4717f;
    private static final float ALL_APPS_FULL_DEPTH_PROGRESS = 0.5f;
    private static final float ALL_APPS_STATE_TRANSITION = 0.305f;
    public static final Interpolator ALL_APPS_VERTICAL_PROGRESS = Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.EMPHASIZED_DECELERATE, 0.4f, 1.0f), (float) ALL_APPS_STATE_TRANSITION, 1.0f);
    public static final Interpolator BLUR = Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.LINEAR, 0.0f, 0.5f), (float) WORKSPACE_MOTION_START, (float) ALL_APPS_STATE_TRANSITION);
    public static final Interpolator HOTSEAT_FADE;
    public static final Interpolator HOTSEAT_SCALE;
    public static final Interpolator HOTSEAT_TRANSLATE = Interpolators.clampToProgress(Interpolators.EMPHASIZED_ACCELERATE, (float) WORKSPACE_MOTION_START, (float) ALL_APPS_STATE_TRANSITION);
    public static final Interpolator SCRIM_FADE = Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.LINEAR, 0.0f, 0.8f), (float) WORKSPACE_MOTION_START, (float) ALL_APPS_STATE_TRANSITION);
    public static final Interpolator WORKSPACE_FADE;
    private static final float WORKSPACE_MOTION_START = 0.1667f;
    public static final Interpolator WORKSPACE_SCALE = Interpolators.clampToProgress(Interpolators.EMPHASIZED_ACCELERATE, (float) WORKSPACE_MOTION_START, (float) ALL_APPS_STATE_TRANSITION);

    static {
        Interpolator clampToProgress = Interpolators.clampToProgress(Interpolators.FINAL_FRAME, 0.0f, (float) ALL_APPS_STATE_TRANSITION);
        WORKSPACE_FADE = clampToProgress;
        HOTSEAT_FADE = clampToProgress;
        HOTSEAT_SCALE = clampToProgress;
    }

    public AllAppsSwipeController(Launcher launcher) {
        super(launcher, SingleAxisSwipeDetector.VERTICAL);
    }

    /* access modifiers changed from: protected */
    public boolean canInterceptTouch(MotionEvent motionEvent) {
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
            return false;
        }
        if (!this.mLauncher.isInState(LauncherState.NORMAL) && !this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            return false;
        }
        if (!this.mLauncher.isInState(LauncherState.ALL_APPS) || this.mLauncher.getAppsView().shouldContainerScroll(motionEvent)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState launcherState, boolean z) {
        if (launcherState != LauncherState.NORMAL || !z) {
            return (launcherState != LauncherState.ALL_APPS || z) ? launcherState : LauncherState.NORMAL;
        }
        return LauncherState.ALL_APPS;
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation() {
        float shiftRange = getShiftRange();
        StateAnimationConfig configForStates = getConfigForStates(this.mFromState, this.mToState);
        configForStates.duration = (long) (2.0f * shiftRange);
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, configForStates);
        return 1.0f / ((this.mToState.getVerticalProgress(this.mLauncher) * shiftRange) - (this.mFromState.getVerticalProgress(this.mLauncher) * shiftRange));
    }

    /* access modifiers changed from: protected */
    public StateAnimationConfig getConfigForStates(LauncherState launcherState, LauncherState launcherState2) {
        StateAnimationConfig configForStates = super.getConfigForStates(launcherState, launcherState2);
        if (launcherState == LauncherState.NORMAL && launcherState2 == LauncherState.ALL_APPS) {
            applyNormalToAllAppsAnimConfig(this.mLauncher, configForStates);
        } else if (launcherState == LauncherState.ALL_APPS && launcherState2 == LauncherState.NORMAL) {
            applyAllAppsToNormalConfig(this.mLauncher, configForStates);
        }
        return configForStates;
    }

    public static void applyAllAppsToNormalConfig(Launcher launcher, StateAnimationConfig stateAnimationConfig) {
        boolean z = launcher.getDeviceProfile().isTablet;
        stateAnimationConfig.setInterpolator(11, ALLAPPS_STAGGERED_FADE_LATE_RESPONDER);
        stateAnimationConfig.setInterpolator(10, z ? Interpolators.FINAL_FRAME : ALLAPPS_STAGGERED_FADE_EARLY_RESPONDER);
        if (!z) {
            stateAnimationConfig.setInterpolator(3, Interpolators.INSTANT);
        }
    }

    public static void applyNormalToAllAppsAnimConfig(Launcher launcher, StateAnimationConfig stateAnimationConfig) {
        if (launcher.getDeviceProfile().isTablet) {
            stateAnimationConfig.setInterpolator(11, ALLAPPS_STAGGERED_FADE_EARLY_RESPONDER);
            stateAnimationConfig.setInterpolator(10, Interpolators.INSTANT);
            return;
        }
        stateAnimationConfig.setInterpolator(13, BLUR);
        stateAnimationConfig.setInterpolator(3, WORKSPACE_FADE);
        stateAnimationConfig.setInterpolator(1, WORKSPACE_SCALE);
        stateAnimationConfig.setInterpolator(16, HOTSEAT_FADE);
        stateAnimationConfig.setInterpolator(4, HOTSEAT_SCALE);
        stateAnimationConfig.setInterpolator(5, HOTSEAT_TRANSLATE);
        stateAnimationConfig.setInterpolator(11, SCRIM_FADE);
        stateAnimationConfig.setInterpolator(10, ALL_APPS_FADE);
        stateAnimationConfig.setInterpolator(0, ALL_APPS_VERTICAL_PROGRESS);
    }
}
