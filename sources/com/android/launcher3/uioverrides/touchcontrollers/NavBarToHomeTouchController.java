package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.TopTaskTracker;
import com.android.quickstep.util.AnimatorControllerWithResistance;
import com.android.quickstep.util.OverviewToHomeAnim;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.HashMap;
import java.util.Objects;

public class NavBarToHomeTouchController implements TouchController, SingleAxisSwipeDetector.Listener {
    private static final float OVERVIEW_TO_HOME_SCRIM_MULTIPLIER = 0.5f;
    private static final Interpolator PULLBACK_INTERPOLATOR = Interpolators.DEACCEL_3;
    private AnimatorPlaybackController mCurrentAnimation;
    private LauncherState mEndState = LauncherState.NORMAL;
    private final Launcher mLauncher;
    private boolean mNoIntercept;
    private final float mPullbackDistance;
    private LauncherState mStartState;
    private final SingleAxisSwipeDetector mSwipeDetector;

    public NavBarToHomeTouchController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mSwipeDetector = new SingleAxisSwipeDetector(launcher, this, SingleAxisSwipeDetector.VERTICAL);
        this.mPullbackDistance = launcher.getResources().getDimension(R.dimen.home_pullback_distance);
    }

    public final boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mStartState = this.mLauncher.getStateManager().getState();
            boolean z = !canInterceptTouch(motionEvent);
            this.mNoIntercept = z;
            if (z) {
                return false;
            }
            this.mSwipeDetector.setDetectableScrollConditions(1, false);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(motionEvent);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    private boolean canInterceptTouch(MotionEvent motionEvent) {
        if (!((motionEvent.getEdgeFlags() & 256) != 0)) {
            return false;
        }
        if (this.mStartState.overviewUi || this.mStartState == LauncherState.ALL_APPS) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenViewWithType(this.mLauncher, FeatureFlags.ENABLE_ALL_APPS_EDU.get() ? 523775 : AbstractFloatingView.TYPE_ALL) != null) {
            return true;
        }
        return FeatureFlags.ASSISTANT_GIVES_LAUNCHER_FOCUS.get() && TopTaskTracker.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).getCachedTopTask(false).isExcludedAssistant();
    }

    public final boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeDetector.onTouchEvent(motionEvent);
    }

    private float getShiftRange() {
        return (float) this.mLauncher.getDeviceProfile().heightPx;
    }

    public void onDragStart(boolean z, float f) {
        initCurrentAnimation();
    }

    private void initCurrentAnimation() {
        PendingAnimation pendingAnimation = new PendingAnimation((long) (getShiftRange() * 2.0f));
        if (this.mStartState.overviewUi) {
            RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
            AnimatorControllerWithResistance.createRecentsResistanceFromOverviewAnim(this.mLauncher, pendingAnimation);
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
                Objects.requireNonNull(recentsView);
                pendingAnimation.addOnFrameCallback(new Runnable() {
                    public final void run() {
                        RecentsView.this.redrawLiveTile();
                    }
                });
            }
            AbstractFloatingView.closeOpenContainer(this.mLauncher, 2048);
        } else if (this.mStartState == LauncherState.ALL_APPS) {
            AllAppsTransitionController allAppsController = this.mLauncher.getAllAppsController();
            Interpolator interpolator = PULLBACK_INTERPOLATOR;
            pendingAnimation.setFloat(allAppsController, AllAppsTransitionController.ALL_APPS_PULL_BACK_TRANSLATION, -this.mPullbackDistance, interpolator);
            pendingAnimation.setFloat(allAppsController, AllAppsTransitionController.ALL_APPS_PULL_BACK_ALPHA, 0.5f, interpolator);
        }
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView != null) {
            topOpenView.addHintCloseAnim(this.mPullbackDistance, PULLBACK_INTERPOLATOR, pendingAnimation);
        }
        AnimatorPlaybackController createPlaybackController = pendingAnimation.createPlaybackController();
        this.mCurrentAnimation = createPlaybackController;
        createPlaybackController.getTarget().addListener(LauncherAnimUtils.newCancelListener(new Runnable() {
            public final void run() {
                NavBarToHomeTouchController.this.clearState();
            }
        }));
    }

    /* access modifiers changed from: private */
    public void clearState() {
        this.mCurrentAnimation = null;
        this.mSwipeDetector.finishedScrolling();
        this.mSwipeDetector.setDetectableScrollConditions(0, false);
    }

    public boolean onDrag(float f) {
        this.mCurrentAnimation.setPlayFraction(Utilities.getProgress(Math.min(0.0f, f), 0.0f, getShiftRange()));
        return true;
    }

    public void onDragEnd(float f) {
        boolean isFling = this.mSwipeDetector.isFling(f);
        float progressFraction = this.mCurrentAnimation.getProgressFraction();
        if (PULLBACK_INTERPOLATOR.getInterpolation(progressFraction) >= 0.5f || (f < 0.0f && isFling)) {
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
                RecentsView recentsView = (RecentsView) this.mLauncher.getOverviewPanel();
                recentsView.switchToScreenshot((HashMap<Integer, ThumbnailData>) null, new Runnable() {
                    public final void run() {
                        RecentsView.this.finishRecentsAnimation(true, (Runnable) null);
                    }
                });
            }
            if (this.mStartState.overviewUi) {
                new OverviewToHomeAnim(this.mLauncher, new Runnable() {
                    public final void run() {
                        NavBarToHomeTouchController.this.lambda$onDragEnd$1$NavBarToHomeTouchController();
                    }
                }).animateWithVelocity(f);
            } else {
                this.mLauncher.getStateManager().goToState(this.mEndState, true, AnimatorListeners.forSuccessCallback(new Runnable() {
                    public final void run() {
                        NavBarToHomeTouchController.this.lambda$onDragEnd$2$NavBarToHomeTouchController();
                    }
                }));
            }
            if (this.mStartState != this.mEndState) {
                logHomeGesture();
            }
            if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
                AbstractFloatingView.closeAllOpenViews(this.mLauncher);
            }
            TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
            return;
        }
        ValueAnimator animationPlayer = this.mCurrentAnimation.getAnimationPlayer();
        animationPlayer.setFloatValues(new float[]{progressFraction, 0.0f});
        animationPlayer.addListener(AnimatorListeners.forSuccessCallback(new Runnable() {
            public final void run() {
                NavBarToHomeTouchController.this.lambda$onDragEnd$3$NavBarToHomeTouchController();
            }
        }));
        animationPlayer.setDuration(80).start();
    }

    public /* synthetic */ void lambda$onDragEnd$1$NavBarToHomeTouchController() {
        onSwipeInteractionCompleted(this.mEndState);
    }

    public /* synthetic */ void lambda$onDragEnd$2$NavBarToHomeTouchController() {
        onSwipeInteractionCompleted(this.mEndState);
    }

    public /* synthetic */ void lambda$onDragEnd$3$NavBarToHomeTouchController() {
        onSwipeInteractionCompleted(this.mStartState);
    }

    private void onSwipeInteractionCompleted(LauncherState launcherState) {
        clearState();
        this.mLauncher.getStateManager().goToState(launcherState, false);
        AccessibilityManagerCompat.sendStateEventToTest(this.mLauncher, launcherState.ordinal);
    }

    private void logHomeGesture() {
        this.mLauncher.getStatsLogManager().logger().withSrcState(this.mStartState.statsLogOrdinal).withDstState(this.mEndState.statsLogOrdinal).log(StatsLogManager.LauncherEvent.LAUNCHER_HOME_GESTURE);
    }
}
