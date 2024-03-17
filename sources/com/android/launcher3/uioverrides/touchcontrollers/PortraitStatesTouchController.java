package com.android.launcher3.uioverrides.touchcontrollers;

import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.uioverrides.states.OverviewState;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.system.InteractionJankMonitorWrapper;

public class PortraitStatesTouchController extends AbstractStateChangeTouchController {
    public static final float ALL_APPS_CONTENT_FADE_MAX_CLAMPING_THRESHOLD = 0.8f;
    public static final float ALL_APPS_CONTENT_FADE_MIN_CLAMPING_THRESHOLD = 0.5f;
    public static final Interpolator ALL_APPS_FADE = Interpolators.clampToProgress(Interpolators.LINEAR, 0.4f, 1.0f);
    private static final float ALL_APPS_FULL_DEPTH_PROGRESS = 0.5f;
    public static final float ALL_APPS_SCRIM_OPAQUE_THRESHOLD = 0.5f;
    public static final float ALL_APPS_SCRIM_VISIBLE_THRESHOLD = 0.1f;
    private static final float ALL_APPS_STATE_TRANSITION = 0.4f;
    public static final Interpolator ALL_APPS_VERTICAL_PROGRESS = Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.LINEAR, 0.4f, 1.0f), 0.4f, 1.0f);
    public static final Interpolator BLUR = Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.LINEAR, 0.0f, 0.5f), 0.0f, 0.4f);
    public static final Interpolator HOTSEAT_FADE;
    public static final Interpolator HOTSEAT_SCALE;
    public static final Interpolator HOTSEAT_TRANSLATE;
    private static final Interpolator LINEAR_EARLY;
    public static final Interpolator SCRIM_FADE;
    private static final Interpolator STEP_TRANSITION;
    private static final String TAG = "PortraitStatesTouchCtrl";
    public static final Interpolator WORKSPACE_FADE;
    public static final Interpolator WORKSPACE_SCALE;
    private final PortraitOverviewStateTouchHelper mOverviewPortraitStateTouchHelper;

    /* access modifiers changed from: protected */
    public boolean canInterceptTouch(MotionEvent motionEvent) {
        return false;
    }

    static {
        Interpolator clampToProgress = Interpolators.clampToProgress(Interpolators.LINEAR, 0.0f, 0.4f);
        LINEAR_EARLY = clampToProgress;
        Interpolator clampToProgress2 = Interpolators.clampToProgress(Interpolators.FINAL_FRAME, 0.0f, 0.4f);
        STEP_TRANSITION = clampToProgress2;
        WORKSPACE_FADE = clampToProgress2;
        WORKSPACE_SCALE = clampToProgress;
        HOTSEAT_FADE = clampToProgress2;
        HOTSEAT_SCALE = clampToProgress;
        HOTSEAT_TRANSLATE = clampToProgress2;
        SCRIM_FADE = clampToProgress;
    }

    public PortraitStatesTouchController(Launcher launcher) {
        super(launcher, SingleAxisSwipeDetector.VERTICAL);
        this.mOverviewPortraitStateTouchHelper = new PortraitOverviewStateTouchHelper(launcher);
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState launcherState, boolean z) {
        if (launcherState != LauncherState.ALL_APPS || z) {
            return launcherState == LauncherState.OVERVIEW ? z ? LauncherState.OVERVIEW : LauncherState.NORMAL : (launcherState != LauncherState.NORMAL || !z) ? launcherState : LauncherState.ALL_APPS;
        }
        return LauncherState.NORMAL;
    }

    private StateAnimationConfig getNormalToAllAppsAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        if (this.mLauncher.getDeviceProfile().isTablet) {
            stateAnimationConfig.setInterpolator(10, Interpolators.INSTANT);
            stateAnimationConfig.setInterpolator(11, Interpolators.clampToProgress(Interpolators.LINEAR, 0.1f, 0.5f));
        } else {
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
        return stateAnimationConfig;
    }

    private StateAnimationConfig getAllAppsToNormalAnimation() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        if (this.mLauncher.getDeviceProfile().isTablet) {
            stateAnimationConfig.setInterpolator(10, Interpolators.FINAL_FRAME);
            stateAnimationConfig.setInterpolator(11, Interpolators.clampToProgress(Interpolators.LINEAR, 0.5f, 0.9f));
        } else {
            stateAnimationConfig.setInterpolator(13, Interpolators.reverse(BLUR));
            stateAnimationConfig.setInterpolator(3, Interpolators.reverse(WORKSPACE_FADE));
            stateAnimationConfig.setInterpolator(1, Interpolators.reverse(WORKSPACE_SCALE));
            stateAnimationConfig.setInterpolator(16, Interpolators.reverse(HOTSEAT_FADE));
            stateAnimationConfig.setInterpolator(4, Interpolators.reverse(HOTSEAT_SCALE));
            stateAnimationConfig.setInterpolator(5, Interpolators.reverse(HOTSEAT_TRANSLATE));
            stateAnimationConfig.setInterpolator(11, Interpolators.reverse(SCRIM_FADE));
            stateAnimationConfig.setInterpolator(10, Interpolators.reverse(ALL_APPS_FADE));
            stateAnimationConfig.setInterpolator(0, Interpolators.reverse(ALL_APPS_VERTICAL_PROGRESS));
        }
        return stateAnimationConfig;
    }

    /* access modifiers changed from: protected */
    public StateAnimationConfig getConfigForStates(LauncherState launcherState, LauncherState launcherState2) {
        if (launcherState == LauncherState.NORMAL && launcherState2 == LauncherState.ALL_APPS) {
            return getNormalToAllAppsAnimation();
        }
        if (launcherState == LauncherState.ALL_APPS && launcherState2 == LauncherState.NORMAL) {
            return getAllAppsToNormalAnimation();
        }
        return new StateAnimationConfig();
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation() {
        StateAnimationConfig stateAnimationConfig;
        float shiftRange = getShiftRange();
        long j = (long) (2.0f * shiftRange);
        float verticalProgress = (this.mToState.getVerticalProgress(this.mLauncher) * shiftRange) - (this.mFromState.getVerticalProgress(this.mLauncher) * shiftRange);
        if (verticalProgress == 0.0f) {
            stateAnimationConfig = new StateAnimationConfig();
        } else {
            stateAnimationConfig = getConfigForStates(this.mFromState, this.mToState);
        }
        stateAnimationConfig.duration = j;
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.getTarget().removeListener(this.mClearStateOnCancelListener);
            this.mCurrentAnimation.dispatchOnCancel();
        }
        this.mGoingBetweenStates = true;
        if (this.mFromState == LauncherState.OVERVIEW && this.mToState == LauncherState.NORMAL && this.mOverviewPortraitStateTouchHelper.shouldSwipeDownReturnToApp()) {
            this.mLauncher.getStateManager().goToState(LauncherState.OVERVIEW, false);
            this.mGoingBetweenStates = false;
            this.mCurrentAnimation = this.mOverviewPortraitStateTouchHelper.createSwipeDownToTaskAppAnimation(j, Interpolators.LINEAR).createPlaybackController();
            this.mLauncher.getStateManager().setCurrentUserControlledAnimation(this.mCurrentAnimation);
            verticalProgress = (float) LayoutUtils.getShelfTrackingDistance(this.mLauncher, this.mLauncher.getDeviceProfile(), ((RecentsView) this.mLauncher.getOverviewPanel()).getPagedOrientationHandler());
        } else {
            this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, stateAnimationConfig);
        }
        this.mCurrentAnimation.getTarget().addListener(this.mClearStateOnCancelListener);
        if (verticalProgress == 0.0f) {
            verticalProgress = Math.signum((float) (this.mFromState.ordinal - this.mToState.ordinal)) * OverviewState.getDefaultSwipeHeight(this.mLauncher);
        }
        return 1.0f / verticalProgress;
    }

    /* access modifiers changed from: protected */
    public void onSwipeInteractionCompleted(LauncherState launcherState) {
        super.lambda$onDragEnd$0$AbstractStateChangeTouchController(launcherState);
        if (this.mStartState == LauncherState.NORMAL && launcherState == LauncherState.OVERVIEW) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).onOverviewShown(true, TAG);
        }
    }

    static boolean isTouchOverHotseat(Launcher launcher, MotionEvent motionEvent) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        return motionEvent.getY() >= ((float) (launcher.getDragLayer().getHeight() - (deviceProfile.hotseatBarSizePx + deviceProfile.getInsets().bottom)));
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            InteractionJankMonitorWrapper.begin(this.mLauncher.getRootView(), 25);
        } else if (action == 1 || action == 3) {
            InteractionJankMonitorWrapper.cancel(25);
        }
        return super.onControllerInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onReinitToState(LauncherState launcherState) {
        super.onReinitToState(launcherState);
        if (launcherState != LauncherState.ALL_APPS) {
            InteractionJankMonitorWrapper.cancel(25);
        }
    }

    /* access modifiers changed from: protected */
    public void onReachedFinalState(LauncherState launcherState) {
        super.onReachedFinalState(launcherState);
        if (launcherState == LauncherState.ALL_APPS) {
            InteractionJankMonitorWrapper.end(25);
        }
    }

    /* access modifiers changed from: protected */
    public void clearState() {
        super.clearState();
        InteractionJankMonitorWrapper.cancel(25);
    }
}
