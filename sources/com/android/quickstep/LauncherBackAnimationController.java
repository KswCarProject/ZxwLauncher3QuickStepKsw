package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Pair;
import android.view.RemoteAnimationTarget;
import android.view.SurfaceControl;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.window.BackEvent;
import android.window.IOnBackInvokedCallback;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.QuickstepTransitionManager;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.quickstep.LauncherBackAnimationController;
import com.android.quickstep.util.RectFSpringAnim;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;

public class LauncherBackAnimationController {
    private static final int CANCEL_TRANSITION_DURATION = 233;
    private static final float MIN_WINDOW_SCALE = 0.7f;
    /* access modifiers changed from: private */
    public boolean mAnimatorSetInProgress = false;
    private IOnBackInvokedCallback mBackCallback;
    /* access modifiers changed from: private */
    public boolean mBackInProgress;
    /* access modifiers changed from: private */
    public float mBackProgress;
    private RemoteAnimationTargetCompat mBackTarget;
    private final Interpolator mCancelInterpolator;
    private final RectF mCancelRect = new RectF();
    private final RectF mCurrentRect = new RectF();
    private final PointF mInitialTouchPos = new PointF();
    private final BaseQuickstepLauncher mLauncher;
    private final QuickstepTransitionManager mQuickstepTransitionManager;
    /* access modifiers changed from: private */
    public boolean mSpringAnimationInProgress = false;
    private final Rect mStartRect = new Rect();
    private SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();
    private final Matrix mTransformMatrix = new Matrix();
    private final int mWindowMaxDeltaY;
    private final float mWindowScaleEndCornerRadius;
    private final int mWindowScaleMarginX;
    private final float mWindowScaleStartCornerRadius;

    public LauncherBackAnimationController(BaseQuickstepLauncher baseQuickstepLauncher, QuickstepTransitionManager quickstepTransitionManager) {
        float f = 0.0f;
        this.mBackProgress = 0.0f;
        this.mBackInProgress = false;
        this.mLauncher = baseQuickstepLauncher;
        this.mQuickstepTransitionManager = quickstepTransitionManager;
        this.mWindowScaleEndCornerRadius = QuickStepContract.supportsRoundedCornersOnWindows(baseQuickstepLauncher.getResources()) ? (float) baseQuickstepLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_back_window_corner_radius) : f;
        this.mWindowScaleStartCornerRadius = QuickStepContract.getWindowCornerRadius(baseQuickstepLauncher);
        this.mWindowScaleMarginX = baseQuickstepLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_back_window_scale_x_margin);
        this.mWindowMaxDeltaY = baseQuickstepLauncher.getResources().getDimensionPixelSize(R.dimen.swipe_back_window_max_delta_y);
        this.mCancelInterpolator = AnimationUtils.loadInterpolator(baseQuickstepLauncher, R.interpolator.back_cancel);
    }

    public void registerBackCallbacks(final Handler handler) {
        this.mBackCallback = new IOnBackInvokedCallback.Stub() {
            public void onBackStarted() {
            }

            public /* synthetic */ void lambda$onBackCancelled$0$LauncherBackAnimationController$1() {
                LauncherBackAnimationController.this.resetPositionAnimated();
            }

            public void onBackCancelled() {
                handler.post(new Runnable() {
                    public final void run() {
                        LauncherBackAnimationController.AnonymousClass1.this.lambda$onBackCancelled$0$LauncherBackAnimationController$1();
                    }
                });
            }

            public /* synthetic */ void lambda$onBackInvoked$1$LauncherBackAnimationController$1() {
                LauncherBackAnimationController.this.startTransition();
            }

            public void onBackInvoked() {
                handler.post(new Runnable() {
                    public final void run() {
                        LauncherBackAnimationController.AnonymousClass1.this.lambda$onBackInvoked$1$LauncherBackAnimationController$1();
                    }
                });
            }

            public void onBackProgressed(BackEvent backEvent) {
                float unused = LauncherBackAnimationController.this.mBackProgress = backEvent.getProgress();
                LauncherBackAnimationController launcherBackAnimationController = LauncherBackAnimationController.this;
                float unused2 = launcherBackAnimationController.mBackProgress = 1.0f - (((1.0f - launcherBackAnimationController.mBackProgress) * (1.0f - LauncherBackAnimationController.this.mBackProgress)) * (1.0f - LauncherBackAnimationController.this.mBackProgress));
                if (!LauncherBackAnimationController.this.mBackInProgress) {
                    LauncherBackAnimationController.this.startBack(backEvent);
                    return;
                }
                LauncherBackAnimationController launcherBackAnimationController2 = LauncherBackAnimationController.this;
                launcherBackAnimationController2.updateBackProgress(launcherBackAnimationController2.mBackProgress, backEvent);
            }
        };
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).setBackToLauncherCallback(this.mBackCallback);
    }

    /* access modifiers changed from: private */
    public void resetPositionAnimated() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mCancelRect.set(this.mCurrentRect);
        ofFloat.setDuration(233);
        ofFloat.setInterpolator(this.mCancelInterpolator);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LauncherBackAnimationController.this.lambda$resetPositionAnimated$0$LauncherBackAnimationController(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                LauncherBackAnimationController.this.finishAnimation();
            }
        });
        ofFloat.start();
    }

    public /* synthetic */ void lambda$resetPositionAnimated$0$LauncherBackAnimationController(ValueAnimator valueAnimator) {
        updateCancelProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void unregisterBackCallbacks() {
        if (this.mBackCallback != null) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).clearBackToLauncherCallback(this.mBackCallback);
        }
        this.mBackCallback = null;
    }

    /* access modifiers changed from: private */
    public void startBack(BackEvent backEvent) {
        this.mBackInProgress = true;
        RemoteAnimationTarget departingAnimationTarget = backEvent.getDepartingAnimationTarget();
        if (departingAnimationTarget != null) {
            this.mTransaction.show(departingAnimationTarget.leash).apply();
            this.mTransaction.setAnimationTransaction();
            this.mBackTarget = new RemoteAnimationTargetCompat(departingAnimationTarget);
            this.mInitialTouchPos.set(backEvent.getTouchX(), backEvent.getTouchY());
            this.mStartRect.set(this.mBackTarget.windowConfiguration.getMaxBounds());
        }
    }

    /* access modifiers changed from: private */
    public void updateBackProgress(float f, BackEvent backEvent) {
        float f2;
        if (this.mBackTarget != null) {
            float width = (float) this.mStartRect.width();
            float height = (float) this.mStartRect.height();
            float mapRange = Utilities.mapRange(f, width - Math.abs(backEvent.getTouchX() - this.mInitialTouchPos.x), Utilities.mapRange(f, 1.0f, 0.7f) * width);
            float f3 = (height / width) * mapRange;
            float sin = ((height - f3) * 0.5f) + (((float) Math.sin(((double) ((backEvent.getTouchY() - this.mInitialTouchPos.y) / height)) * 3.141592653589793d * 0.5d)) * ((float) this.mWindowMaxDeltaY));
            if (backEvent.getSwipeEdge() == 1) {
                f2 = ((float) this.mWindowScaleMarginX) * f;
            } else {
                f2 = (width - (((float) this.mWindowScaleMarginX) * f)) - mapRange;
            }
            this.mCurrentRect.set(f2, sin, mapRange + f2, f3 + sin);
            applyTransform(this.mCurrentRect, Utilities.mapRange(f, this.mWindowScaleStartCornerRadius, this.mWindowScaleEndCornerRadius));
        }
    }

    private void updateCancelProgress(float f) {
        if (this.mBackTarget != null) {
            this.mCurrentRect.set(Utilities.mapRange(f, this.mCancelRect.left, (float) this.mStartRect.left), Utilities.mapRange(f, this.mCancelRect.top, (float) this.mStartRect.top), Utilities.mapRange(f, this.mCancelRect.right, (float) this.mStartRect.right), Utilities.mapRange(f, this.mCancelRect.bottom, (float) this.mStartRect.bottom));
            applyTransform(this.mCurrentRect, Utilities.mapRange(f, Utilities.mapRange(this.mBackProgress, this.mWindowScaleStartCornerRadius, this.mWindowScaleEndCornerRadius), this.mWindowScaleStartCornerRadius));
        }
    }

    private void applyTransform(RectF rectF, float f) {
        SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(this.mBackTarget.leash);
        float width = rectF.width() / ((float) this.mStartRect.width());
        this.mTransformMatrix.reset();
        this.mTransformMatrix.setScale(width, width);
        this.mTransformMatrix.postTranslate(rectF.left, rectF.top);
        builder.withMatrix(this.mTransformMatrix).withWindowCrop(this.mStartRect).withCornerRadius(f);
        SyncRtSurfaceTransactionApplierCompat.SurfaceParams build = builder.build();
        if (build.surface.isValid()) {
            build.applyTo(this.mTransaction);
        }
        this.mTransaction.apply();
    }

    /* access modifiers changed from: private */
    public void startTransition() {
        if (this.mBackTarget == null) {
            finishAnimation();
        } else if (!this.mLauncher.isDestroyed()) {
            if (this.mLauncher.hasSomeInvisibleFlag(8)) {
                this.mLauncher.addForceInvisibleFlag(4);
                this.mLauncher.getStateManager().moveToRestState();
            }
            AbstractFloatingView.closeAllOpenViewsExcept(this.mLauncher, false, AbstractFloatingView.TYPE_REBIND_SAFE);
            float mapRange = Utilities.mapRange(this.mBackProgress, this.mWindowScaleStartCornerRadius, this.mWindowScaleEndCornerRadius);
            Pair<RectFSpringAnim, AnimatorSet> createWallpaperOpenAnimations = this.mQuickstepTransitionManager.createWallpaperOpenAnimations(new RemoteAnimationTargetCompat[]{this.mBackTarget}, new RemoteAnimationTargetCompat[0], false, this.mCurrentRect, mapRange);
            startTransitionAnimations((RectFSpringAnim) createWallpaperOpenAnimations.first, (AnimatorSet) createWallpaperOpenAnimations.second);
            this.mLauncher.clearForceInvisibleFlag(15);
        }
    }

    /* access modifiers changed from: private */
    public void finishAnimation() {
        this.mBackTarget = null;
        this.mBackInProgress = false;
        this.mBackProgress = 0.0f;
        this.mTransformMatrix.reset();
        this.mCancelRect.setEmpty();
        this.mCurrentRect.setEmpty();
        this.mStartRect.setEmpty();
        this.mInitialTouchPos.set(0.0f, 0.0f);
        this.mAnimatorSetInProgress = false;
        this.mSpringAnimationInProgress = false;
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).onBackToLauncherAnimationFinished();
    }

    private void startTransitionAnimations(RectFSpringAnim rectFSpringAnim, AnimatorSet animatorSet) {
        boolean z = true;
        this.mAnimatorSetInProgress = animatorSet != null;
        if (rectFSpringAnim == null) {
            z = false;
        }
        this.mSpringAnimationInProgress = z;
        if (rectFSpringAnim != null) {
            rectFSpringAnim.addAnimatorListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    boolean unused = LauncherBackAnimationController.this.mSpringAnimationInProgress = false;
                    LauncherBackAnimationController.this.tryFinishBackAnimation();
                }
            });
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                boolean unused = LauncherBackAnimationController.this.mAnimatorSetInProgress = false;
                LauncherBackAnimationController.this.tryFinishBackAnimation();
            }
        });
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public void tryFinishBackAnimation() {
        if (!this.mSpringAnimationInProgress && !this.mAnimatorSetInProgress) {
            finishAnimation();
        }
    }
}
