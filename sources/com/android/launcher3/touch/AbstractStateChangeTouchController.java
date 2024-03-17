package com.android.launcher3.touch;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.FlingBlockCheck;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.window.RefreshRateTracker;

public abstract class AbstractStateChangeTouchController implements TouchController, SingleAxisSwipeDetector.Listener {
    private boolean mAllAppsOvershootStarted;
    private boolean mCanBlockFling;
    protected final Animator.AnimatorListener mClearStateOnCancelListener = LauncherAnimUtils.newCancelListener(new Runnable() {
        public final void run() {
            AbstractStateChangeTouchController.this.clearState();
        }
    });
    protected AnimatorPlaybackController mCurrentAnimation;
    protected final SingleAxisSwipeDetector mDetector;
    private float mDisplacementShift;
    private final FlingBlockCheck mFlingBlockCheck = new FlingBlockCheck();
    protected LauncherState mFromState;
    protected boolean mGoingBetweenStates = true;
    private boolean mIsLogContainerSet;
    protected final Launcher mLauncher;
    private boolean mNoIntercept;
    protected float mProgressMultiplier;
    protected int mStartContainerType;
    private float mStartProgress;
    protected LauncherState mStartState;
    protected final SingleAxisSwipeDetector.Direction mSwipeDirection;
    protected LauncherState mToState;

    /* access modifiers changed from: protected */
    public abstract boolean canInterceptTouch(MotionEvent motionEvent);

    /* access modifiers changed from: protected */
    public abstract LauncherState getTargetState(LauncherState launcherState, boolean z);

    /* access modifiers changed from: protected */
    public abstract float initCurrentAnimation();

    /* access modifiers changed from: protected */
    public void onReachedFinalState(LauncherState launcherState) {
    }

    /* access modifiers changed from: protected */
    public void onReinitToState(LauncherState launcherState) {
    }

    public AbstractStateChangeTouchController(Launcher launcher, SingleAxisSwipeDetector.Direction direction) {
        this.mLauncher = launcher;
        this.mDetector = new SingleAxisSwipeDetector(launcher, this, direction);
        this.mSwipeDirection = direction;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        int i;
        if (motionEvent.getAction() == 0) {
            boolean z = true;
            boolean z2 = !canInterceptTouch(motionEvent);
            this.mNoIntercept = z2;
            if (z2) {
                return false;
            }
            if (this.mCurrentAnimation != null) {
                i = 3;
            } else {
                i = getSwipeDirection();
                if (i == 0) {
                    this.mNoIntercept = true;
                    return false;
                }
                z = false;
            }
            this.mDetector.setDetectableScrollConditions(i, z);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(motionEvent);
        return this.mDetector.isDraggingOrSettling();
    }

    private int getSwipeDirection() {
        LauncherState state = this.mLauncher.getStateManager().getState();
        int i = 1;
        if (getTargetState(state, true) == state) {
            i = 0;
        }
        return getTargetState(state, false) != state ? i | 2 : i;
    }

    public final boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return this.mDetector.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return this.mLauncher.getAllAppsController().getShiftRange();
    }

    private boolean reinitCurrentAnimation(boolean z, boolean z2) {
        LauncherState launcherState = this.mFromState;
        if (launcherState == null) {
            launcherState = this.mLauncher.getStateManager().getState();
        } else if (z) {
            launcherState = this.mToState;
        }
        LauncherState targetState = getTargetState(launcherState, z2);
        onReinitToState(targetState);
        if ((launcherState == this.mFromState && targetState == this.mToState) || launcherState == targetState) {
            return false;
        }
        this.mFromState = launcherState;
        this.mToState = targetState;
        this.mStartProgress = 0.0f;
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getTarget().removeListener(this.mClearStateOnCancelListener);
        }
        this.mProgressMultiplier = initCurrentAnimation();
        this.mCurrentAnimation.dispatchOnStart();
        return true;
    }

    public void onDragStart(boolean z, float f) {
        LauncherState state = this.mLauncher.getStateManager().getState();
        this.mStartState = state;
        boolean z2 = false;
        this.mIsLogContainerSet = false;
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController == null) {
            this.mFromState = state;
            this.mToState = null;
            cancelAnimationControllers();
            reinitCurrentAnimation(false, this.mDetector.wasInitialTouchPositive());
            this.mDisplacementShift = 0.0f;
        } else {
            animatorPlaybackController.pause();
            this.mStartProgress = this.mCurrentAnimation.getProgressFraction();
        }
        if (this.mFromState == LauncherState.NORMAL) {
            z2 = true;
        }
        this.mCanBlockFling = z2;
        this.mFlingBlockCheck.unblockFling();
    }

    public boolean onDrag(float f) {
        float f2 = (this.mProgressMultiplier * (f - this.mDisplacementShift)) + this.mStartProgress;
        updateProgress(f2);
        boolean isPositive = this.mSwipeDirection.isPositive(f - this.mDisplacementShift);
        if (f2 <= 0.0f) {
            if (reinitCurrentAnimation(false, isPositive)) {
                this.mDisplacementShift = f;
                if (this.mCanBlockFling) {
                    this.mFlingBlockCheck.blockFling();
                }
            }
        } else if (f2 >= 1.0f) {
            if (reinitCurrentAnimation(true, isPositive)) {
                this.mDisplacementShift = f;
                if (this.mCanBlockFling) {
                    this.mFlingBlockCheck.blockFling();
                }
            }
            if (this.mToState == LauncherState.ALL_APPS) {
                this.mAllAppsOvershootStarted = true;
                float f3 = f2 - 1.0f;
                this.mLauncher.getAppsView().onPull(f3, f3);
            }
        } else {
            this.mFlingBlockCheck.onEvent();
        }
        return true;
    }

    public boolean onDrag(float f, MotionEvent motionEvent) {
        if (!this.mIsLogContainerSet) {
            if (this.mStartState == LauncherState.ALL_APPS) {
                this.mStartContainerType = 4;
            } else if (this.mStartState == LauncherState.NORMAL) {
                this.mStartContainerType = 2;
            } else if (this.mStartState == LauncherState.OVERVIEW) {
                this.mStartContainerType = 3;
            }
            this.mIsLogContainerSet = true;
        }
        return onDrag(f);
    }

    /* access modifiers changed from: protected */
    public void updateProgress(float f) {
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.setPlayFraction(f);
        }
    }

    /* access modifiers changed from: protected */
    public StateAnimationConfig getConfigForStates(LauncherState launcherState, LauncherState launcherState2) {
        return new StateAnimationConfig();
    }

    public void onDragEnd(float f) {
        LauncherState launcherState;
        if (this.mCurrentAnimation != null) {
            boolean isFling = this.mDetector.isFling(f);
            boolean z = isFling && this.mFlingBlockCheck.isBlocked();
            boolean z2 = z ? false : isFling;
            float progressFraction = this.mCurrentAnimation.getProgressFraction();
            float f2 = this.mProgressMultiplier * f;
            float interpolatedProgress = this.mCurrentAnimation.getInterpolatedProgress();
            if (z2) {
                launcherState = Float.compare(Math.signum(f), Math.signum(this.mProgressMultiplier)) == 0 ? this.mToState : this.mFromState;
            } else {
                launcherState = interpolatedProgress > ((!this.mLauncher.getDeviceProfile().isTablet || (this.mToState != LauncherState.ALL_APPS && this.mFromState != LauncherState.ALL_APPS)) ? 0.5f : 0.3f) ? this.mToState : this.mFromState;
            }
            LauncherState launcherState2 = launcherState;
            int blockedFlingDurationFactor = (!z || launcherState2 != this.mFromState) ? 1 : LauncherAnimUtils.blockedFlingDurationFactor(f);
            long j = 0;
            float f3 = 1.0f;
            float f4 = 0.0f;
            if (launcherState2 != this.mToState) {
                this.mCurrentAnimation.getTarget().removeListener(this.mClearStateOnCancelListener);
                this.mCurrentAnimation.dispatchOnCancel();
                if (progressFraction <= 0.0f) {
                    f3 = 0.0f;
                } else {
                    f3 = Utilities.boundToRange((f2 * ((float) RefreshRateTracker.getSingleFrameMs(this.mLauncher))) + progressFraction, 0.0f, 1.0f);
                    j = BaseSwipeDetector.calculateDuration(f, Math.min(progressFraction, 1.0f) - 0.0f) * ((long) blockedFlingDurationFactor);
                }
            } else if (progressFraction >= 1.0f) {
                f4 = 1.0f;
            } else {
                float boundToRange = Utilities.boundToRange((f2 * ((float) RefreshRateTracker.getSingleFrameMs(this.mLauncher))) + progressFraction, 0.0f, 1.0f);
                j = BaseSwipeDetector.calculateDuration(f, 1.0f - Math.max(progressFraction, 0.0f)) * ((long) blockedFlingDurationFactor);
                f4 = 1.0f;
                f3 = boundToRange;
            }
            this.mCurrentAnimation.setEndAction(new Runnable(launcherState2) {
                public final /* synthetic */ LauncherState f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AbstractStateChangeTouchController.this.lambda$onDragEnd$0$AbstractStateChangeTouchController(this.f$1);
                }
            });
            ValueAnimator animationPlayer = this.mCurrentAnimation.getAnimationPlayer();
            animationPlayer.setFloatValues(new float[]{f3, f4});
            updateSwipeCompleteAnimation(animationPlayer, j, launcherState2, f, z2);
            this.mCurrentAnimation.dispatchOnStart();
            if (launcherState2 == LauncherState.ALL_APPS) {
                if (this.mAllAppsOvershootStarted) {
                    this.mLauncher.getAppsView().onRelease();
                    this.mAllAppsOvershootStarted = false;
                } else {
                    this.mLauncher.getAppsView().addSpringFromFlingUpdateListener(animationPlayer, f, progressFraction);
                }
            }
            animationPlayer.start();
        }
    }

    /* access modifiers changed from: protected */
    public void updateSwipeCompleteAnimation(ValueAnimator valueAnimator, long j, LauncherState launcherState, float f, boolean z) {
        valueAnimator.setDuration(j).setInterpolator(Interpolators.scrollInterpolatorForVelocity(f));
    }

    /* access modifiers changed from: protected */
    /* renamed from: onSwipeInteractionCompleted */
    public void lambda$onDragEnd$0$AbstractStateChangeTouchController(LauncherState launcherState) {
        onReachedFinalState(this.mToState);
        clearState();
        if (this.mGoingBetweenStates || this.mToState != launcherState) {
            goToTargetState(launcherState);
        } else {
            lambda$goToTargetState$1$AbstractStateChangeTouchController(this.mToState);
        }
    }

    /* access modifiers changed from: protected */
    public void goToTargetState(LauncherState launcherState) {
        if (!this.mLauncher.isInState(launcherState)) {
            this.mLauncher.getStateManager().goToState(launcherState, false, AnimatorListeners.forEndCallback((Runnable) new Runnable(launcherState) {
                public final /* synthetic */ LauncherState f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AbstractStateChangeTouchController.this.lambda$goToTargetState$1$AbstractStateChangeTouchController(this.f$1);
                }
            }));
        } else {
            lambda$goToTargetState$1$AbstractStateChangeTouchController(launcherState);
        }
        this.mLauncher.getRootView().getSysUiScrim().createSysuiMultiplierAnim(1.0f).setDuration(0).start();
    }

    /* access modifiers changed from: private */
    /* renamed from: logReachedState */
    public void lambda$goToTargetState$1$AbstractStateChangeTouchController(LauncherState launcherState) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (this.mStartState != launcherState) {
            StatsLogManager.StatsLogger withContainerInfo = this.mLauncher.getStatsLogManager().logger().withSrcState(this.mStartState.statsLogOrdinal).withDstState(launcherState.statsLogOrdinal).withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(this.mLauncher.getWorkspace().getCurrentPage())).build());
            int i = this.mStartState.statsLogOrdinal;
            int i2 = launcherState.statsLogOrdinal;
            if (this.mToState.ordinal > this.mFromState.ordinal) {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEUP;
            } else {
                launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEDOWN;
            }
            withContainerInfo.log(StatsLogManager.getLauncherAtomEvent(i, i2, launcherEvent));
        }
    }

    /* access modifiers changed from: protected */
    public void clearState() {
        cancelAnimationControllers();
        this.mGoingBetweenStates = true;
        this.mDetector.finishedScrolling();
        this.mDetector.setDetectableScrollConditions(0, false);
    }

    private void cancelAnimationControllers() {
        this.mCurrentAnimation = null;
    }
}
