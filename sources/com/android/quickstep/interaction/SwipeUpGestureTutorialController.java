package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.OverviewComponentObserver;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.SwipeUpAnimationLogic;
import com.android.quickstep.interaction.TutorialController;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.Objects;

abstract class SwipeUpGestureTutorialController extends TutorialController {
    private static final int FAKE_PREVIOUS_TASK_MARGIN = Utilities.dpToPx(12.0f);
    private static final long HOME_SWIPE_ANIMATION_DURATION_MILLIS = 625;
    private static final long OVERVIEW_SWIPE_ANIMATION_DURATION_MILLIS = 1000;
    protected static final long TASK_VIEW_END_ANIMATION_DURATION_MILLIS = 300;
    /* access modifiers changed from: private */
    public float mFakeTaskViewRadius;
    /* access modifiers changed from: private */
    public final Rect mFakeTaskViewRect;
    /* access modifiers changed from: private */
    public final AnimatorListenerAdapter mResetTaskView = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            SwipeUpGestureTutorialController.this.mFakeHotseatView.setVisibility(4);
            SwipeUpGestureTutorialController.this.mFakeIconView.setVisibility(4);
            if (SwipeUpGestureTutorialController.this.mTutorialFragment.getActivity() != null) {
                int fullscreenHeight = SwipeUpGestureTutorialController.this.mTutorialFragment.getRootView().getFullscreenHeight();
                SwipeUpGestureTutorialController.this.mFakeTaskViewRect.set(0, 0, SwipeUpGestureTutorialController.this.mTutorialFragment.getRootView().getWidth(), fullscreenHeight);
            }
            float unused = SwipeUpGestureTutorialController.this.mFakeTaskViewRadius = 0.0f;
            SwipeUpGestureTutorialController.this.mFakeTaskView.invalidateOutline();
            SwipeUpGestureTutorialController.this.mFakeTaskView.setVisibility(0);
            SwipeUpGestureTutorialController.this.mFakeTaskView.setAlpha(1.0f);
            SwipeUpGestureTutorialController.this.mFakePreviousTaskView.setVisibility(4);
            SwipeUpGestureTutorialController.this.mFakePreviousTaskView.setAlpha(1.0f);
            SwipeUpGestureTutorialController.this.mFakePreviousTaskView.setToSingleRowLayout(false);
            boolean unused2 = SwipeUpGestureTutorialController.this.mShowTasks = false;
            boolean unused3 = SwipeUpGestureTutorialController.this.mShowPreviousTasks = false;
            SwipeUpGestureTutorialController.this.mRunningWindowAnim = null;
        }
    };
    SwipeUpAnimationLogic.RunningWindowAnim mRunningWindowAnim;
    /* access modifiers changed from: private */
    public boolean mShowPreviousTasks = false;
    /* access modifiers changed from: private */
    public boolean mShowTasks = false;
    final ViewSwipeUpAnimation mTaskViewSwipeUpAnimation;

    SwipeUpGestureTutorialController(TutorialFragment tutorialFragment, TutorialController.TutorialType tutorialType) {
        super(tutorialFragment, tutorialType);
        Rect rect = new Rect();
        this.mFakeTaskViewRect = rect;
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this.mContext);
        OverviewComponentObserver overviewComponentObserver = new OverviewComponentObserver(this.mContext, recentsAnimationDeviceState);
        ViewSwipeUpAnimation viewSwipeUpAnimation = new ViewSwipeUpAnimation(this.mContext, recentsAnimationDeviceState, new GestureState(overviewComponentObserver, -1));
        this.mTaskViewSwipeUpAnimation = viewSwipeUpAnimation;
        overviewComponentObserver.onDestroy();
        recentsAnimationDeviceState.destroy();
        viewSwipeUpAnimation.initDp(InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getDeviceProfile(this.mContext).copy(this.mContext));
        rect.set(0, 0, this.mTutorialFragment.getRootView().getWidth(), this.mTutorialFragment.getRootView().getFullscreenHeight());
        this.mFakeTaskViewRadius = 0.0f;
        AnonymousClass2 r7 = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(SwipeUpGestureTutorialController.this.mFakeTaskViewRect, SwipeUpGestureTutorialController.this.mFakeTaskViewRadius);
            }
        };
        this.mFakeTaskView.setClipToOutline(true);
        this.mFakeTaskView.setOutlineProvider(r7);
        this.mFakePreviousTaskView.setClipToOutline(true);
        this.mFakePreviousTaskView.setOutlineProvider(r7);
    }

    private void cancelRunningAnimation() {
        SwipeUpAnimationLogic.RunningWindowAnim runningWindowAnim = this.mRunningWindowAnim;
        if (runningWindowAnim != null) {
            runningWindowAnim.cancel();
        }
        this.mRunningWindowAnim = null;
    }

    /* access modifiers changed from: package-private */
    public void fadeOutFakeTaskView(boolean z, final boolean z2, final Runnable runnable) {
        cancelRunningAnimation();
        PendingAnimation pendingAnimation = new PendingAnimation(300);
        if (z) {
            pendingAnimation.setFloat(this.mTaskViewSwipeUpAnimation.getCurrentShift(), AnimatedFloat.VALUE, 1.0f, Interpolators.ACCEL);
            pendingAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator, boolean z) {
                    PendingAnimation pendingAnimation = new PendingAnimation(300);
                    if (z2) {
                        pendingAnimation.setFloat(SwipeUpGestureTutorialController.this.mTaskViewSwipeUpAnimation.getCurrentShift(), AnimatedFloat.VALUE, 0.0f, Interpolators.ACCEL);
                        pendingAnimation.addListener(SwipeUpGestureTutorialController.this.mResetTaskView);
                    } else {
                        pendingAnimation.setViewAlpha(SwipeUpGestureTutorialController.this.mFakeTaskView, 0.0f, Interpolators.ACCEL);
                        pendingAnimation.setViewAlpha(SwipeUpGestureTutorialController.this.mFakePreviousTaskView, 0.0f, Interpolators.ACCEL);
                    }
                    Runnable runnable = runnable;
                    if (runnable != null) {
                        pendingAnimation.addListener(AnimatorListeners.forSuccessCallback(runnable));
                    }
                    AnimatorSet buildAnim = pendingAnimation.buildAnim();
                    if (z2 && SwipeUpGestureTutorialController.this.mTutorialFragment.isLargeScreen()) {
                        buildAnim.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                                super.onAnimationStart(animator);
                                AnimatorSet createAnimationToMultiRowLayout = SwipeUpGestureTutorialController.this.mFakePreviousTaskView.createAnimationToMultiRowLayout();
                                if (createAnimationToMultiRowLayout != null) {
                                    createAnimationToMultiRowLayout.setDuration(300).start();
                                }
                            }
                        });
                    }
                    buildAnim.setStartDelay(100);
                    buildAnim.start();
                    SwipeUpGestureTutorialController.this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap((Animator) buildAnim);
                }
            });
        } else {
            if (z2) {
                pendingAnimation.setFloat(this.mTaskViewSwipeUpAnimation.getCurrentShift(), AnimatedFloat.VALUE, 0.0f, Interpolators.ACCEL);
                pendingAnimation.addListener(this.mResetTaskView);
            } else {
                pendingAnimation.setViewAlpha(this.mFakeTaskView, 0.0f, Interpolators.ACCEL);
                pendingAnimation.setViewAlpha(this.mFakePreviousTaskView, 0.0f, Interpolators.ACCEL);
            }
            if (runnable != null) {
                pendingAnimation.addListener(AnimatorListeners.forSuccessCallback(runnable));
            }
        }
        AnimatorSet buildAnim = pendingAnimation.buildAnim();
        hideFakeTaskbar(false);
        buildAnim.start();
        this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap((Animator) buildAnim);
    }

    /* access modifiers changed from: package-private */
    public void resetFakeTaskView(boolean z) {
        this.mFakeTaskView.setVisibility(0);
        PendingAnimation pendingAnimation = new PendingAnimation(300);
        pendingAnimation.setFloat(this.mTaskViewSwipeUpAnimation.getCurrentShift(), AnimatedFloat.VALUE, 0.0f, Interpolators.ACCEL);
        pendingAnimation.setViewAlpha(this.mFakeTaskView, 1.0f, Interpolators.ACCEL);
        pendingAnimation.addListener(this.mResetTaskView);
        AnimatorSet buildAnim = pendingAnimation.buildAnim();
        showFakeTaskbar(z);
        buildAnim.start();
        this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap((Animator) buildAnim);
    }

    /* access modifiers changed from: package-private */
    public void animateFakeTaskViewHome(PointF pointF, Runnable runnable) {
        cancelRunningAnimation();
        hideFakeTaskbar(true);
        this.mFakePreviousTaskView.setVisibility(4);
        this.mFakeHotseatView.setVisibility(0);
        this.mShowPreviousTasks = false;
        RectFSpringAnim handleSwipeUpToHome = this.mTaskViewSwipeUpAnimation.handleSwipeUpToHome(pointF);
        PendingAnimation pendingAnimation = new PendingAnimation(300);
        pendingAnimation.setViewAlpha(this.mFakeIconView, 0.0f, Interpolators.ACCEL);
        if (runnable != null) {
            pendingAnimation.addListener(AnimatorListeners.forSuccessCallback(runnable));
        }
        AnimatorSet buildAnim = pendingAnimation.buildAnim();
        Objects.requireNonNull(buildAnim);
        handleSwipeUpToHome.addAnimatorListener(AnimatorListeners.forSuccessCallback(new Runnable(buildAnim) {
            public final /* synthetic */ AnimatorSet f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        }));
        this.mRunningWindowAnim = SwipeUpAnimationLogic.RunningWindowAnim.wrap(handleSwipeUpToHome);
    }

    public void setNavBarGestureProgress(Float f) {
        if (!isGestureCompleted()) {
            if (this.mTutorialType == TutorialController.TutorialType.HOME_NAVIGATION_COMPLETE || this.mTutorialType == TutorialController.TutorialType.OVERVIEW_NAVIGATION_COMPLETE) {
                this.mFakeTaskView.setVisibility(4);
                this.mFakePreviousTaskView.setVisibility(4);
                return;
            }
            this.mShowTasks = true;
            this.mFakeTaskView.setVisibility(0);
            if (this.mShowPreviousTasks) {
                this.mFakePreviousTaskView.setVisibility(0);
            }
            if (this.mRunningWindowAnim == null && f != null) {
                this.mTaskViewSwipeUpAnimation.updateDisplacement(f.floatValue());
            }
        }
    }

    public void onMotionPaused(boolean z) {
        if (!isGestureCompleted() && this.mShowTasks) {
            if (!this.mShowPreviousTasks) {
                AnimatedTaskView animatedTaskView = this.mFakePreviousTaskView;
                int i = FAKE_PREVIOUS_TASK_MARGIN;
                animatedTaskView.setTranslationX((float) (-((this.mFakePreviousTaskView.getWidth() * 2) + i)));
                this.mFakePreviousTaskView.animate().setDuration(300).translationX((float) (-(this.mFakePreviousTaskView.getWidth() + i))).start();
            }
            this.mShowPreviousTasks = true;
        }
    }

    class ViewSwipeUpAnimation extends SwipeUpAnimationLogic {
        ViewSwipeUpAnimation(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, GestureState gestureState) {
            super(context, recentsAnimationDeviceState, gestureState);
            this.mRemoteTargetHandles[0] = new RemoteTargetGluer.RemoteTargetHandle(this.mRemoteTargetHandles[0].getTaskViewSimulator(), new FakeTransformParams());
            for (RemoteTargetGluer.RemoteTargetHandle taskViewSimulator : this.mTargetGluer.getRemoteTargetHandles()) {
                taskViewSimulator.getTaskViewSimulator().getOrientationState().ignoreAllowHomeRotationPreference();
            }
        }

        /* access modifiers changed from: package-private */
        public void initDp(DeviceProfile deviceProfile) {
            initTransitionEndpoints(deviceProfile);
            this.mRemoteTargetHandles[0].getTaskViewSimulator().setPreviewBounds(new Rect(0, 0, deviceProfile.widthPx, deviceProfile.heightPx), deviceProfile.getInsets());
        }

        public void updateFinalShift() {
            this.mRemoteTargetHandles[0].getPlaybackController().setProgress(this.mCurrentShift.value, this.mDragLengthFactor);
            this.mRemoteTargetHandles[0].getTaskViewSimulator().apply(this.mRemoteTargetHandles[0].getTransformParams());
        }

        /* access modifiers changed from: package-private */
        public AnimatedFloat getCurrentShift() {
            return this.mCurrentShift;
        }

        /* access modifiers changed from: package-private */
        public RectFSpringAnim handleSwipeUpToHome(PointF pointF) {
            PointF pointF2 = new PointF(pointF.x, pointF.y);
            float f = this.mCurrentShift.value;
            float boundToRange = Utilities.boundToRange(f - ((pointF2.y * ((float) RefreshRateTracker.getSingleFrameMs(this.mContext))) / ((float) this.mTransitionDragLength)), 0.0f, this.mDragLengthFactor);
            final long min = Math.min(350, ((long) Math.round(Math.abs(((1.0f - f) * ((float) this.mTransitionDragLength)) / pointF2.y))) * 2);
            RectFSpringAnim rectFSpringAnim = createWindowAnimationToHome(boundToRange, new SwipeUpAnimationLogic.HomeAnimationFactory() {
                public AnimatorPlaybackController createActivityAnimationToHome() {
                    return AnimatorPlaybackController.wrap(new AnimatorSet(), min);
                }

                public RectF getWindowTargetRect() {
                    int dpToPx = Utilities.dpToPx(60.0f);
                    int hotseatIconLeft = SwipeUpGestureTutorialController.this.getHotseatIconLeft();
                    int hotseatIconTop = SwipeUpGestureTutorialController.this.getHotseatIconTop();
                    return new RectF((float) hotseatIconLeft, (float) hotseatIconTop, (float) (hotseatIconLeft + dpToPx), (float) (hotseatIconTop + dpToPx));
                }

                public void update(RectF rectF, float f, float f2) {
                    SwipeUpGestureTutorialController.this.mFakeIconView.setVisibility(0);
                    SwipeUpGestureTutorialController.this.mFakeIconView.update(rectF, f, 0.9f, f2, 255, false, SwipeUpGestureTutorialController.this.mFakeIconView, ViewSwipeUpAnimation.this.mDp);
                    SwipeUpGestureTutorialController.this.mFakeIconView.setAlpha(1.0f);
                    SwipeUpGestureTutorialController.this.mFakeTaskView.setAlpha(getWindowAlpha(f));
                    SwipeUpGestureTutorialController.this.mFakePreviousTaskView.setAlpha(getWindowAlpha(f));
                }

                public void onCancel() {
                    SwipeUpGestureTutorialController.this.mFakeIconView.setVisibility(4);
                }
            })[0];
            rectFSpringAnim.start(this.mContext, pointF2);
            return rectFSpringAnim;
        }
    }

    /* access modifiers changed from: protected */
    public Animator createFingerDotHomeSwipeAnimator(final float f) {
        Animator duration = createFingerDotSwipeUpAnimator(f).setDuration(HOME_SWIPE_ANIMATION_DURATION_MILLIS);
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                SwipeUpGestureTutorialController.this.animateFakeTaskViewHome(new PointF(0.0f, f / 625.0f), (Runnable) null);
            }
        });
        return duration;
    }

    /* access modifiers changed from: protected */
    public Animator createFingerDotOverviewSwipeAnimator(float f) {
        Animator duration = createFingerDotSwipeUpAnimator(f).setDuration(OVERVIEW_SWIPE_ANIMATION_DURATION_MILLIS);
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                SwipeUpGestureTutorialController.this.mFakePreviousTaskView.setVisibility(0);
                SwipeUpGestureTutorialController.this.onMotionPaused(true);
            }
        });
        return duration;
    }

    private Animator createFingerDotSwipeUpAnimator(float f) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f) {
            public final /* synthetic */ float f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                SwipeUpGestureTutorialController.this.lambda$createFingerDotSwipeUpAnimator$0$SwipeUpGestureTutorialController(this.f$1, valueAnimator);
            }
        });
        return ofFloat;
    }

    public /* synthetic */ void lambda$createFingerDotSwipeUpAnimator$0$SwipeUpGestureTutorialController(float f, ValueAnimator valueAnimator) {
        float animatedFraction = (-f) * valueAnimator.getAnimatedFraction();
        setNavBarGestureProgress(Float.valueOf(animatedFraction));
        this.mFingerDotView.setTranslationY(f + animatedFraction);
    }

    private class FakeTransformParams extends TransformParams {
        private FakeTransformParams() {
        }

        public SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] createSurfaceParams(TransformParams.BuilderProxy builderProxy) {
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder((SurfaceControl) null);
            builderProxy.onBuildTargetParams(builder, (RemoteAnimationTargetCompat) null, this);
            return new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[]{builder.build()};
        }

        public void applySurfaceParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr) {
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams surfaceParams = surfaceParamsArr[0];
            SwipeUpGestureTutorialController.this.mFakeTaskView.setAnimationMatrix(surfaceParams.matrix);
            SwipeUpGestureTutorialController.this.mFakePreviousTaskView.setAnimationMatrix(surfaceParams.matrix);
            SwipeUpGestureTutorialController.this.mFakeTaskViewRect.set(surfaceParams.windowCrop);
            float unused = SwipeUpGestureTutorialController.this.mFakeTaskViewRadius = surfaceParams.cornerRadius;
            SwipeUpGestureTutorialController.this.mFakeTaskView.invalidateOutline();
            SwipeUpGestureTutorialController.this.mFakePreviousTaskView.invalidateOutline();
        }
    }
}
