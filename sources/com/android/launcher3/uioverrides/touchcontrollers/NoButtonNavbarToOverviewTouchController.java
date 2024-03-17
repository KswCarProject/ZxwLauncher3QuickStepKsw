package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.taskbar.LauncherTaskbarUIController;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.OverviewToHomeAnim;
import com.android.quickstep.util.VibratorWrapper;
import com.android.quickstep.views.RecentsView;

public class NoButtonNavbarToOverviewTouchController extends PortraitStatesTouchController {
    private static final float ONE_HANDED_ACTIVATED_SLOP_MULTIPLIER = 2.5f;
    private static final float OVERVIEW_MOVEMENT_FACTOR = 0.25f;
    private static final long TRANSLATION_ANIM_MIN_DURATION_MS = 80;
    private static final float TRANSLATION_ANIM_VELOCITY_DP_PER_MS = 0.8f;
    private boolean mDidTouchStartInNavBar;
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private ObjectAnimator mNormalToHintOverviewScrimAnimator;
    private AnimatorPlaybackController mOverviewResistYAnim;
    private boolean mReachedOverview;
    private final RecentsView mRecentsView;
    private PointF mStartDisplacement = new PointF();
    private float mStartY;
    private boolean mStartedOverview;

    public NoButtonNavbarToOverviewTouchController(Launcher launcher) {
        super(launcher);
        this.mRecentsView = (RecentsView) launcher.getOverviewPanel();
        this.mMotionPauseDetector = new MotionPauseDetector(launcher);
        this.mMotionPauseMinDisplacement = (float) ViewConfiguration.get(launcher).getScaledTouchSlop();
    }

    /* access modifiers changed from: protected */
    public boolean canInterceptTouch(MotionEvent motionEvent) {
        this.mDidTouchStartInNavBar = (motionEvent.getEdgeFlags() & 256) != 0;
        if (!super.canInterceptTouch(motionEvent) || this.mLauncher.isInState(LauncherState.HINT_STATE)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState launcherState, boolean z) {
        if (launcherState == LauncherState.NORMAL && this.mDidTouchStartInNavBar) {
            return LauncherState.HINT_STATE;
        }
        if (launcherState != LauncherState.OVERVIEW || !z) {
            return super.getTargetState(launcherState, z);
        }
        return LauncherState.OVERVIEW;
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation() {
        return this.mToState == LauncherState.HINT_STATE ? -1.0f / ((float) this.mLauncher.getDeviceProfile().heightPx) : super.initCurrentAnimation();
    }

    public void onDragStart(boolean z, float f) {
        LauncherTaskbarUIController taskbarUIController;
        if (this.mLauncher.isInState(LauncherState.ALL_APPS) && (taskbarUIController = ((BaseQuickstepLauncher) this.mLauncher).getTaskbarUIController()) != null) {
            taskbarUIController.setShouldDelayLauncherStateAnim(true);
        }
        super.onDragStart(z, f);
        this.mMotionPauseDetector.clear();
        if (handlingOverviewAnim()) {
            this.mMotionPauseDetector.setOnMotionPauseListener(new MotionPauseDetector.OnMotionPauseListener() {
                public final void onMotionPauseDetected() {
                    NoButtonNavbarToOverviewTouchController.this.onMotionPauseDetected();
                }
            });
        }
        if (this.mFromState == LauncherState.NORMAL && this.mToState == LauncherState.HINT_STATE) {
            this.mNormalToHintOverviewScrimAnimator = ObjectAnimator.ofArgb(this.mLauncher.getScrimView(), LauncherAnimUtils.VIEW_BACKGROUND_COLOR, new int[]{this.mFromState.getWorkspaceScrimColor(this.mLauncher), this.mToState.getWorkspaceScrimColor(this.mLauncher)});
        }
        this.mStartedOverview = false;
        this.mReachedOverview = false;
        this.mOverviewResistYAnim = null;
    }

    /* access modifiers changed from: protected */
    public void updateProgress(float f) {
        super.updateProgress(f);
        ObjectAnimator objectAnimator = this.mNormalToHintOverviewScrimAnimator;
        if (objectAnimator != null) {
            objectAnimator.setCurrentFraction(f);
        }
    }

    public void onDragEnd(float f) {
        LauncherTaskbarUIController taskbarUIController = ((BaseQuickstepLauncher) this.mLauncher).getTaskbarUIController();
        if (taskbarUIController != null) {
            taskbarUIController.setShouldDelayLauncherStateAnim(false);
        }
        if (this.mStartedOverview) {
            goToOverviewOrHomeOnDragEnd(f);
        } else {
            super.onDragEnd(f);
        }
        this.mMotionPauseDetector.clear();
        this.mNormalToHintOverviewScrimAnimator = null;
        if (this.mLauncher.isInState(LauncherState.OVERVIEW)) {
            clearState();
        }
    }

    /* access modifiers changed from: protected */
    public void updateSwipeCompleteAnimation(ValueAnimator valueAnimator, long j, LauncherState launcherState, float f, boolean z) {
        super.updateSwipeCompleteAnimation(valueAnimator, j, launcherState, f, z);
        if (launcherState == LauncherState.HINT_STATE) {
            valueAnimator.setDuration((long) LauncherState.HINT_STATE.getTransitionDuration(this.mLauncher, true));
        }
    }

    /* access modifiers changed from: private */
    public void onMotionPauseDetected() {
        if (this.mCurrentAnimation != null) {
            this.mNormalToHintOverviewScrimAnimator = null;
            this.mCurrentAnimation.getTarget().addListener(LauncherAnimUtils.newCancelListener(new Runnable() {
                public final void run() {
                    NoButtonNavbarToOverviewTouchController.this.lambda$onMotionPauseDetected$1$NoButtonNavbarToOverviewTouchController();
                }
            }));
            this.mCurrentAnimation.getTarget().removeListener(this.mClearStateOnCancelListener);
            this.mCurrentAnimation.dispatchOnCancel();
            this.mStartedOverview = true;
            VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).vibrate(VibratorWrapper.OVERVIEW_HAPTIC);
        }
    }

    public /* synthetic */ void lambda$onMotionPauseDetected$1$NoButtonNavbarToOverviewTouchController() {
        this.mLauncher.getStateManager().goToState(LauncherState.OVERVIEW, true, AnimatorListeners.forSuccessCallback(new Runnable() {
            public final void run() {
                NoButtonNavbarToOverviewTouchController.this.lambda$onMotionPauseDetected$0$NoButtonNavbarToOverviewTouchController();
            }
        }));
    }

    public /* synthetic */ void lambda$onMotionPauseDetected$0$NoButtonNavbarToOverviewTouchController() {
        this.mOverviewResistYAnim = AnimatorControllerWithResistance.createRecentsResistanceFromOverviewAnim(this.mLauncher, (PendingAnimation) null).createPlaybackController();
        this.mReachedOverview = true;
        maybeSwipeInteractionToOverviewComplete();
    }

    /* access modifiers changed from: private */
    public void maybeSwipeInteractionToOverviewComplete() {
        if (this.mReachedOverview && !this.mDetector.isDraggingState()) {
            onSwipeInteractionCompleted(LauncherState.OVERVIEW);
        }
    }

    private boolean handlingOverviewAnim() {
        return this.mDidTouchStartInNavBar && this.mStartState == LauncherState.NORMAL && (SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags() & 128) == 0;
    }

    public boolean onDrag(float f, float f2, MotionEvent motionEvent) {
        AnimatorPlaybackController animatorPlaybackController;
        if (this.mStartedOverview) {
            if (!this.mReachedOverview) {
                this.mStartDisplacement.set(f2, f);
                this.mStartY = motionEvent.getY();
            } else {
                this.mRecentsView.setTranslationX((f2 - this.mStartDisplacement.x) * OVERVIEW_MOVEMENT_FACTOR);
                float f3 = (this.mStartDisplacement.y - f) / this.mStartY;
                if (f3 <= 0.0f || (animatorPlaybackController = this.mOverviewResistYAnim) == null) {
                    this.mRecentsView.setTranslationY((f - this.mStartDisplacement.y) * OVERVIEW_MOVEMENT_FACTOR);
                } else {
                    animatorPlaybackController.setPlayFraction(f3);
                }
            }
        }
        this.mMotionPauseDetector.setDisallowPause(!handlingOverviewAnim() || (-f) < this.mMotionPauseMinDisplacement);
        this.mMotionPauseDetector.addPosition(motionEvent);
        if (this.mStartedOverview || super.onDrag(f, f2, motionEvent)) {
            return true;
        }
        return false;
    }

    private void goToOverviewOrHomeOnDragEnd(float f) {
        $$Lambda$NoButtonNavbarToOverviewTouchController$wXhCOUSC9LcVTGjjY4Rl3i668eM r3;
        boolean z = !this.mMotionPauseDetector.isPaused();
        if (z) {
            new OverviewToHomeAnim(this.mLauncher, new Runnable() {
                public final void run() {
                    NoButtonNavbarToOverviewTouchController.this.lambda$goToOverviewOrHomeOnDragEnd$2$NoButtonNavbarToOverviewTouchController();
                }
            }).animateWithVelocity(f);
        }
        if (this.mReachedOverview) {
            long max = (long) Math.max(80.0f, dpiFromPx(Math.max(Math.abs(this.mRecentsView.getTranslationX()), Math.abs(this.mRecentsView.getTranslationY()))) / 0.8f);
            ViewPropertyAnimator duration = this.mRecentsView.animate().translationX(0.0f).translationY(0.0f).setInterpolator(Interpolators.ACCEL_DEACCEL).setDuration(max);
            if (z) {
                r3 = null;
            } else {
                r3 = new Runnable() {
                    public final void run() {
                        NoButtonNavbarToOverviewTouchController.this.maybeSwipeInteractionToOverviewComplete();
                    }
                };
            }
            duration.withEndAction(r3);
            if (!z) {
                StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
                stateAnimationConfig.duration = max;
                LauncherState state = this.mLauncher.getStateManager().getState();
                this.mLauncher.getStateManager().createAtomicAnimation(state, state, stateAnimationConfig).start();
            }
        }
    }

    public /* synthetic */ void lambda$goToOverviewOrHomeOnDragEnd$2$NoButtonNavbarToOverviewTouchController() {
        onSwipeInteractionCompleted(LauncherState.NORMAL);
    }

    private float dpiFromPx(float f) {
        return Utilities.dpiFromPx(f, this.mLauncher.getResources().getDisplayMetrics().densityDpi);
    }

    public void onOneHandedModeStateChanged(boolean z) {
        if (z) {
            this.mDetector.setTouchSlopMultiplier(ONE_HANDED_ACTIVATED_SLOP_MULTIPLIER);
        } else {
            this.mDetector.setTouchSlopMultiplier(1.0f);
        }
    }
}
