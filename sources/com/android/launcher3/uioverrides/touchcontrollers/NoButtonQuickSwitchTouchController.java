package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.BothAxesSwipeDetector;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.VibratorWrapper;
import com.android.quickstep.util.WorkspaceRevealAnim;
import com.android.quickstep.views.LauncherRecentsView;
import com.android.quickstep.views.RecentsView;

public class NoButtonQuickSwitchTouchController implements TouchController, BothAxesSwipeDetector.Listener {
    private static final long ATOMIC_DURATION_FROM_PAUSED_TO_OVERVIEW = 300;
    private static final Interpolator FADE_OUT_INTERPOLATOR = Interpolators.DEACCEL_3;
    private static final Interpolator SCALE_DOWN_INTERPOLATOR = Interpolators.LINEAR;
    private static final Interpolator TRANSLATE_OUT_INTERPOLATOR = Interpolators.ACCEL_0_75;
    private static final float Y_ANIM_MIN_PROGRESS = 0.25f;
    protected final Animator.AnimatorListener mClearStateOnCancelListener = LauncherAnimUtils.newCancelListener(new Runnable() {
        public final void run() {
            NoButtonQuickSwitchTouchController.this.clearState();
        }
    });
    private boolean mIsHomeScreenVisible = true;
    private final BaseQuickstepLauncher mLauncher;
    private final float mMaxYProgress;
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private boolean mNoIntercept;
    private AnimatorPlaybackController mNonOverviewAnim;
    /* access modifiers changed from: private */
    public final LauncherRecentsView mRecentsView;
    private LauncherState mStartState;
    private final BothAxesSwipeDetector mSwipeDetector;
    private AnimatorPlaybackController mXOverviewAnim;
    private final float mXRange;
    private AnimatedFloat mYOverviewAnim;
    private final float mYRange;

    public NoButtonQuickSwitchTouchController(BaseQuickstepLauncher baseQuickstepLauncher) {
        this.mLauncher = baseQuickstepLauncher;
        this.mSwipeDetector = new BothAxesSwipeDetector(baseQuickstepLauncher, this);
        LauncherRecentsView launcherRecentsView = (LauncherRecentsView) baseQuickstepLauncher.getOverviewPanel();
        this.mRecentsView = launcherRecentsView;
        this.mXRange = ((float) baseQuickstepLauncher.getDeviceProfile().widthPx) / 2.0f;
        float shelfTrackingDistance = (float) LayoutUtils.getShelfTrackingDistance(baseQuickstepLauncher, baseQuickstepLauncher.getDeviceProfile(), launcherRecentsView.getPagedOrientationHandler());
        this.mYRange = shelfTrackingDistance;
        this.mMaxYProgress = ((float) baseQuickstepLauncher.getDeviceProfile().heightPx) / shelfTrackingDistance;
        this.mMotionPauseDetector = new MotionPauseDetector(baseQuickstepLauncher);
        this.mMotionPauseMinDisplacement = baseQuickstepLauncher.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            boolean z = !canInterceptTouch(motionEvent);
            this.mNoIntercept = z;
            if (z) {
                return false;
            }
            this.mSwipeDetector.setDetectableScrollConditions(2, false);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(motionEvent);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeDetector.onTouchEvent(motionEvent);
    }

    private boolean canInterceptTouch(MotionEvent motionEvent) {
        if (this.mLauncher.isInState(LauncherState.NORMAL) && (motionEvent.getEdgeFlags() & 256) != 0 && (SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getLastSystemUiStateFlags() & 128) == 0) {
            return true;
        }
        return false;
    }

    public void onDragStart(boolean z) {
        this.mMotionPauseDetector.clear();
        if (z) {
            this.mStartState = this.mLauncher.getStateManager().getState();
            this.mMotionPauseDetector.setOnMotionPauseListener(new MotionPauseDetector.OnMotionPauseListener() {
                public final void onMotionPauseDetected() {
                    NoButtonQuickSwitchTouchController.this.onMotionPauseDetected();
                }
            });
            this.mSwipeDetector.setDetectableScrollConditions(3, false);
            setupAnimators();
        }
    }

    /* access modifiers changed from: private */
    public void onMotionPauseDetected() {
        VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).vibrate(VibratorWrapper.OVERVIEW_HAPTIC);
    }

    private void setupAnimators() {
        StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
        Interpolator interpolator = FADE_OUT_INTERPOLATOR;
        stateAnimationConfig.setInterpolator(3, interpolator);
        stateAnimationConfig.setInterpolator(10, interpolator);
        stateAnimationConfig.setInterpolator(1, interpolator);
        stateAnimationConfig.setInterpolator(13, interpolator);
        stateAnimationConfig.setInterpolator(0, TRANSLATE_OUT_INTERPOLATOR);
        updateNonOverviewAnim(LauncherState.QUICK_SWITCH, stateAnimationConfig);
        this.mNonOverviewAnim.dispatchOnStart();
        if (this.mRecentsView.getTaskViewCount() == 0) {
            this.mRecentsView.setOnEmptyMessageUpdatedListener(new RecentsView.OnEmptyMessageUpdatedListener() {
                public final void onEmptyMessageUpdated(boolean z) {
                    NoButtonQuickSwitchTouchController.this.lambda$setupAnimators$0$NoButtonQuickSwitchTouchController(z);
                }
            });
        }
        setupOverviewAnimators();
    }

    public /* synthetic */ void lambda$setupAnimators$0$NoButtonQuickSwitchTouchController(boolean z) {
        if (!z && this.mSwipeDetector.isDraggingState()) {
            setupOverviewAnimators();
        }
    }

    private void updateNonOverviewAnim(LauncherState launcherState, StateAnimationConfig stateAnimationConfig) {
        stateAnimationConfig.duration = (long) (Math.max(this.mXRange, this.mYRange) * 2.0f);
        stateAnimationConfig.animFlags |= 10;
        AnimatorPlaybackController createAnimationToNewWorkspace = this.mLauncher.getStateManager().createAnimationToNewWorkspace(launcherState, stateAnimationConfig);
        this.mNonOverviewAnim = createAnimationToNewWorkspace;
        createAnimationToNewWorkspace.getTarget().addListener(this.mClearStateOnCancelListener);
    }

    private void setupOverviewAnimators() {
        LauncherState launcherState = LauncherState.QUICK_SWITCH;
        LauncherState launcherState2 = LauncherState.OVERVIEW;
        RecentsView.RECENTS_SCALE_PROPERTY.set(this.mRecentsView, Float.valueOf(launcherState.getOverviewScaleAndOffset(this.mLauncher)[0]));
        float f = 1.0f;
        RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET.set(this.mRecentsView, Float.valueOf(1.0f));
        Log.d(TestProtocol.BAD_STATE, "NBQSTC setupOverviewAnimators setContentAlpha=1");
        this.mRecentsView.setContentAlpha(1.0f);
        this.mRecentsView.setFullscreenProgress(launcherState.getOverviewFullscreenProgress());
        MultiValueAlpha.AlphaProperty visibilityAlpha = this.mLauncher.getActionsView().getVisibilityAlpha();
        if ((launcherState.getVisibleElements(this.mLauncher) & 8) == 0) {
            f = 0.0f;
        }
        visibilityAlpha.setValue(f);
        float[] overviewScaleAndOffset = launcherState2.getOverviewScaleAndOffset(this.mLauncher);
        PendingAnimation pendingAnimation = new PendingAnimation((long) (this.mXRange * 2.0f));
        pendingAnimation.setFloat(this.mRecentsView, RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET, overviewScaleAndOffset[1], Interpolators.LINEAR);
        pendingAnimation.setViewBackgroundColor(this.mLauncher.getScrimView(), LauncherState.QUICK_SWITCH.getWorkspaceScrimColor(this.mLauncher), Interpolators.LINEAR);
        if (this.mRecentsView.getTaskViewCount() == 0) {
            pendingAnimation.addFloat(this.mRecentsView, RecentsView.CONTENT_ALPHA, 0.0f, 1.0f, Interpolators.LINEAR);
            Log.d(TestProtocol.BAD_STATE, "NBQSTC setupOverviewAnimators from: 0 to: 1");
            pendingAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    Log.d(TestProtocol.BAD_STATE, "NBQSTC setupOverviewAnimators onStart");
                }

                public void onAnimationCancel(Animator animator) {
                    Log.d(TestProtocol.BAD_STATE, "NBQSTC setupOverviewAnimators onCancel, alpha=" + (NoButtonQuickSwitchTouchController.this.mRecentsView == null ? -1.0f : ((Float) RecentsView.CONTENT_ALPHA.get(NoButtonQuickSwitchTouchController.this.mRecentsView)).floatValue()));
                }

                public void onAnimationEnd(Animator animator) {
                    Log.d(TestProtocol.BAD_STATE, "NBQSTC setupOverviewAnimators onEnd");
                }
            });
        }
        AnimatorPlaybackController createPlaybackController = pendingAnimation.createPlaybackController();
        this.mXOverviewAnim = createPlaybackController;
        createPlaybackController.dispatchOnStart();
        PendingAnimation pendingAnimation2 = new PendingAnimation((long) (this.mYRange * 2.0f));
        LauncherRecentsView launcherRecentsView = this.mRecentsView;
        FloatProperty<RecentsView> floatProperty = RecentsView.RECENTS_SCALE_PROPERTY;
        float f2 = overviewScaleAndOffset[0];
        Interpolator interpolator = SCALE_DOWN_INTERPOLATOR;
        pendingAnimation2.setFloat(launcherRecentsView, floatProperty, f2, interpolator);
        pendingAnimation2.setFloat(this.mRecentsView, RecentsView.FULLSCREEN_PROGRESS, launcherState2.getOverviewFullscreenProgress(), interpolator);
        AnimatorPlaybackController createPlaybackController2 = pendingAnimation2.createPlaybackController();
        this.mYOverviewAnim = new AnimatedFloat(new Runnable(AnimatorControllerWithResistance.createForRecents(createPlaybackController2, this.mLauncher, this.mRecentsView.getPagedViewOrientedState(), this.mLauncher.getDeviceProfile(), this.mRecentsView, RecentsView.RECENTS_SCALE_PROPERTY, this.mRecentsView, RecentsView.TASK_SECONDARY_TRANSLATION)) {
            public final /* synthetic */ AnimatorControllerWithResistance f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NoButtonQuickSwitchTouchController.this.lambda$setupOverviewAnimators$1$NoButtonQuickSwitchTouchController(this.f$1);
            }
        });
        createPlaybackController2.dispatchOnStart();
    }

    public /* synthetic */ void lambda$setupOverviewAnimators$1$NoButtonQuickSwitchTouchController(AnimatorControllerWithResistance animatorControllerWithResistance) {
        AnimatedFloat animatedFloat = this.mYOverviewAnim;
        if (animatedFloat != null) {
            animatorControllerWithResistance.setProgress(animatedFloat.value, this.mMaxYProgress);
        }
    }

    public boolean onDrag(PointF pointF, MotionEvent motionEvent) {
        AnimatorPlaybackController animatorPlaybackController;
        float max = Math.max(0.0f, pointF.x) / this.mXRange;
        float mapRange = Utilities.mapRange(Math.max(0.0f, -pointF.y) / this.mYRange, Y_ANIM_MIN_PROGRESS, 1.0f);
        if (this.mIsHomeScreenVisible && (animatorPlaybackController = this.mNonOverviewAnim) != null) {
            animatorPlaybackController.setPlayFraction(max);
        }
        boolean z = false;
        this.mIsHomeScreenVisible = FADE_OUT_INTERPOLATOR.getInterpolation(max) <= 0.99f;
        MotionPauseDetector motionPauseDetector = this.mMotionPauseDetector;
        if ((-pointF.y) < this.mMotionPauseMinDisplacement) {
            z = true;
        }
        motionPauseDetector.setDisallowPause(z);
        this.mMotionPauseDetector.addPosition(motionEvent);
        AnimatorPlaybackController animatorPlaybackController2 = this.mXOverviewAnim;
        if (animatorPlaybackController2 != null) {
            animatorPlaybackController2.setPlayFraction(max);
        }
        AnimatedFloat animatedFloat = this.mYOverviewAnim;
        if (animatedFloat != null) {
            animatedFloat.updateValue(mapRange);
        }
        return true;
    }

    public void onDragEnd(PointF pointF) {
        LauncherState launcherState;
        float f;
        PointF pointF2 = pointF;
        boolean isFling = this.mSwipeDetector.isFling(pointF2.x);
        boolean isFling2 = this.mSwipeDetector.isFling(pointF2.y);
        boolean z = !isFling && !isFling2;
        if (!this.mMotionPauseDetector.isPaused() || !z) {
            if (!isFling || !isFling2) {
                if (isFling) {
                    launcherState = pointF2.x > 0.0f ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
                } else if (isFling2) {
                    launcherState = pointF2.y > 0.0f ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
                } else {
                    launcherState = (!((this.mXOverviewAnim.getInterpolatedProgress() > 0.5f ? 1 : (this.mXOverviewAnim.getInterpolatedProgress() == 0.5f ? 0 : -1)) > 0) || ((this.mYOverviewAnim.value > 1.0f ? 1 : (this.mYOverviewAnim.value == 1.0f ? 0 : -1)) > 0)) ? LauncherState.NORMAL : LauncherState.QUICK_SWITCH;
                }
            } else if (pointF2.x < 0.0f) {
                launcherState = LauncherState.NORMAL;
            } else if (pointF2.y > 0.0f) {
                launcherState = LauncherState.QUICK_SWITCH;
            } else {
                launcherState = Math.abs(pointF2.x) > Math.abs(pointF2.y) ? LauncherState.QUICK_SWITCH : LauncherState.NORMAL;
            }
            float boundToRange = Utilities.boundToRange(this.mXOverviewAnim.getProgressFraction() + ((pointF2.x * ((float) RefreshRateTracker.getSingleFrameMs(this.mLauncher))) / this.mXRange), 0.0f, 1.0f);
            float f2 = launcherState == LauncherState.NORMAL ? 0.0f : 1.0f;
            long calculateDuration = BaseSwipeDetector.calculateDuration(pointF2.x, Math.abs(f2 - boundToRange));
            ValueAnimator animationPlayer = this.mXOverviewAnim.getAnimationPlayer();
            animationPlayer.setFloatValues(new float[]{boundToRange, f2});
            animationPlayer.setDuration(calculateDuration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(pointF2.x));
            this.mXOverviewAnim.dispatchOnStart();
            boolean z2 = isFling2 && pointF2.y < 0.0f && launcherState == LauncherState.NORMAL;
            float boundToRange2 = Utilities.boundToRange(this.mYOverviewAnim.value - ((pointF2.y * ((float) RefreshRateTracker.getSingleFrameMs(this.mLauncher))) / this.mYRange), 0.0f, this.mMaxYProgress);
            if (z2) {
                f = 1.0f;
            } else {
                f = launcherState == LauncherState.NORMAL ? boundToRange2 : 0.0f;
            }
            long abs = (long) ((Math.abs(f - boundToRange2) * this.mYRange) / Math.max(1.0f, Math.abs(pointF2.y)));
            ObjectAnimator animateToValue = this.mYOverviewAnim.animateToValue(boundToRange2, f);
            animateToValue.setDuration(abs);
            this.mYOverviewAnim.updateValue(boundToRange2);
            ValueAnimator animationPlayer2 = this.mNonOverviewAnim.getAnimationPlayer();
            if (!z2 || this.mIsHomeScreenVisible) {
                boolean z3 = launcherState == LauncherState.NORMAL;
                if (z3) {
                    this.mNonOverviewAnim.getTarget().removeListener(this.mClearStateOnCancelListener);
                    this.mNonOverviewAnim.dispatchOnCancel();
                }
                animationPlayer2.setFloatValues(new float[]{this.mNonOverviewAnim.getProgressFraction(), z3 ? 0.0f : 1.0f});
                this.mNonOverviewAnim.dispatchOnStart();
            } else {
                StateAnimationConfig stateAnimationConfig = new StateAnimationConfig();
                stateAnimationConfig.animFlags = 1;
                updateNonOverviewAnim(launcherState, stateAnimationConfig);
                animationPlayer2 = this.mNonOverviewAnim.getAnimationPlayer();
                this.mNonOverviewAnim.dispatchOnStart();
                new WorkspaceRevealAnim(this.mLauncher, false).start();
            }
            if (launcherState == LauncherState.QUICK_SWITCH) {
                VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).vibrate(RecentsView.SCROLL_VIBRATION_PRIMITIVE, 0.6f, RecentsView.SCROLL_VIBRATION_FALLBACK);
            }
            animationPlayer2.setDuration(Math.max(calculateDuration, abs));
            this.mNonOverviewAnim.setEndAction(new Runnable(launcherState) {
                public final /* synthetic */ LauncherState f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NoButtonQuickSwitchTouchController.this.lambda$onDragEnd$2$NoButtonQuickSwitchTouchController(this.f$1);
                }
            });
            cancelAnimations();
            animationPlayer.start();
            animateToValue.start();
            animationPlayer2.start();
            return;
        }
        cancelAnimations();
        StateAnimationConfig stateAnimationConfig2 = new StateAnimationConfig();
        stateAnimationConfig2.duration = 300;
        AnimatorSet createAtomicAnimation = this.mLauncher.getStateManager().createAtomicAnimation(this.mStartState, LauncherState.OVERVIEW, stateAnimationConfig2);
        createAtomicAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NoButtonQuickSwitchTouchController.this.lambda$onDragEnd$2$NoButtonQuickSwitchTouchController(LauncherState.OVERVIEW);
            }
        });
        createAtomicAnimation.start();
        this.mLauncher.getStateManager().createAnimationToNewWorkspace(LauncherState.OVERVIEW, stateAnimationConfig2.duration, 1).dispatchOnStart();
    }

    /* access modifiers changed from: private */
    /* renamed from: onAnimationToStateCompleted */
    public void lambda$onDragEnd$2$NoButtonQuickSwitchTouchController(LauncherState launcherState) {
        StatsLogManager.LauncherEvent launcherEvent;
        StatsLogManager.StatsLogger withDstState = this.mLauncher.getStatsLogManager().logger().withSrcState(2).withDstState(launcherState.statsLogOrdinal);
        int i = this.mStartState.statsLogOrdinal;
        int i2 = launcherState.statsLogOrdinal;
        if (launcherState == LauncherState.QUICK_SWITCH) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_RIGHT;
        } else if (launcherState.ordinal > this.mStartState.ordinal) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEUP;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_UNKNOWN_SWIPEDOWN;
        }
        withDstState.log(StatsLogManager.getLauncherAtomEvent(i, i2, launcherEvent));
        this.mLauncher.getStateManager().goToState(launcherState, false, AnimatorListeners.forEndCallback((Runnable) new Runnable() {
            public final void run() {
                NoButtonQuickSwitchTouchController.this.clearState();
            }
        }));
    }

    private void cancelAnimations() {
        AnimatorPlaybackController animatorPlaybackController = this.mNonOverviewAnim;
        if (animatorPlaybackController != null) {
            animatorPlaybackController.getAnimationPlayer().cancel();
        }
        AnimatorPlaybackController animatorPlaybackController2 = this.mXOverviewAnim;
        if (animatorPlaybackController2 != null) {
            animatorPlaybackController2.getAnimationPlayer().cancel();
        }
        AnimatedFloat animatedFloat = this.mYOverviewAnim;
        if (animatedFloat != null) {
            animatedFloat.cancelAnimation();
        }
        this.mMotionPauseDetector.clear();
    }

    /* access modifiers changed from: private */
    public void clearState() {
        cancelAnimations();
        this.mNonOverviewAnim = null;
        this.mXOverviewAnim = null;
        this.mYOverviewAnim = null;
        this.mIsHomeScreenVisible = true;
        this.mSwipeDetector.finishedScrolling();
        this.mRecentsView.setOnEmptyMessageUpdatedListener((RecentsView.OnEmptyMessageUpdatedListener) null);
    }
}
