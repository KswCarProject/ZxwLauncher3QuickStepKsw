package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.window.WindowManagerProxy;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.util.MultiValueUpdateListener;
import kotlinx.coroutines.scheduling.WorkQueueKt;

public class AllAppsEduView extends AbstractFloatingView {
    /* access modifiers changed from: private */
    public AnimatorSet mAnimation;
    private boolean mCanInterceptTouch;
    /* access modifiers changed from: private */
    public GradientDrawable mCircle;
    private int mCircleSizePx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_circle_size);
    /* access modifiers changed from: private */
    public GradientDrawable mGradient;
    /* access modifiers changed from: private */
    public Launcher mLauncher;
    private int mMaxHeightPx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_max_height);
    private int mPaddingPx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_padding);
    private AllAppsEduTouchController mTouchController;
    private int mWidthPx = getResources().getDimensionPixelSize(R.dimen.swipe_edu_width);

    public boolean canInterceptEventsInSystemGestureRegion() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 512) != 0;
    }

    public boolean onBackPressed() {
        return true;
    }

    public AllAppsEduView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCircle = (GradientDrawable) context.getDrawable(R.drawable.all_apps_edu_circle);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mGradient.draw(canvas);
        this.mCircle.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsOpen = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsOpen = false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        this.mLauncher.getDragLayer().removeView(this);
    }

    private boolean shouldInterceptTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mCanInterceptTouch = (motionEvent.getEdgeFlags() & 256) == 0;
        }
        return this.mCanInterceptTouch;
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        if (!shouldInterceptTouch(motionEvent)) {
            return true;
        }
        this.mTouchController.onControllerTouchEvent(motionEvent);
        updateAnimationOnTouchEvent(motionEvent);
        return true;
    }

    private void updateAnimationOnTouchEvent(MotionEvent motionEvent) {
        if (this.mAnimation != null) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mAnimation.pause();
            } else if (actionMasked == 1 || actionMasked == 3) {
                this.mAnimation.resume();
            } else if (this.mTouchController.isDraggingOrSettling()) {
                this.mAnimation = null;
                handleClose(false);
            }
        }
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (!shouldInterceptTouch(motionEvent)) {
            return true;
        }
        this.mTouchController.onControllerInterceptTouchEvent(motionEvent);
        updateAnimationOnTouchEvent(motionEvent);
        return true;
    }

    private void playAnimation() {
        if (this.mAnimation == null) {
            this.mAnimation = new AnimatorSet();
            Rect rect = new Rect(this.mCircle.getBounds());
            Rect rect2 = new Rect(this.mGradient.getBounds());
            Rect rect3 = new Rect();
            AnimatorPlaybackController access$100 = this.mTouchController.initAllAppsAnimation();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.setInterpolator(Interpolators.LINEAR);
            long j = (long) 1800;
            ofFloat.setDuration(j);
            AnonymousClass1 r12 = r0;
            AnonymousClass1 r0 = new MultiValueUpdateListener(this, WindowManagerProxy.MIN_TABLET_WIDTH, (float) ((this.mMaxHeightPx - this.mCircleSizePx) - this.mPaddingPx), 1200, rect3, rect, rect2, 0.75f, access$100) {
                MultiValueUpdateListener.FloatProp mCircleAlpha;
                MultiValueUpdateListener.FloatProp mCircleScale;
                MultiValueUpdateListener.FloatProp mDeltaY;
                MultiValueUpdateListener.FloatProp mGradientAlpha;
                final /* synthetic */ AllAppsEduView this$0;
                final /* synthetic */ Rect val$circleBoundsOg;
                final /* synthetic */ int val$firstPart;
                final /* synthetic */ Rect val$gradientBoundsOg;
                final /* synthetic */ float val$maxAllAppsProgress;
                final /* synthetic */ int val$secondPart;
                final /* synthetic */ AnimatorPlaybackController val$stateAnimationController;
                final /* synthetic */ Rect val$temp;
                final /* synthetic */ float val$transY;

                {
                    int i = r14;
                    int i2 = r16;
                    this.this$0 = r13;
                    this.val$firstPart = i;
                    this.val$transY = r15;
                    this.val$secondPart = i2;
                    this.val$temp = r17;
                    this.val$circleBoundsOg = r18;
                    this.val$gradientBoundsOg = r19;
                    this.val$maxAllAppsProgress = r20;
                    this.val$stateAnimationController = r21;
                    this.mCircleAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 255.0f, 0.0f, (float) i, Interpolators.LINEAR);
                    this.mCircleScale = new MultiValueUpdateListener.FloatProp(2.0f, 1.0f, 0.0f, (float) i, Interpolators.OVERSHOOT_1_7);
                    this.mDeltaY = new MultiValueUpdateListener.FloatProp(0.0f, r15, (float) i, (float) i2, Interpolators.FAST_OUT_SLOW_IN);
                    this.mGradientAlpha = new MultiValueUpdateListener.FloatProp(0.0f, 255.0f, (float) i, ((float) i2) * 0.3f, Interpolators.LINEAR);
                }

                public void onUpdate(float f, boolean z) {
                    this.val$temp.set(this.val$circleBoundsOg);
                    this.val$temp.offset(0, (int) (-this.mDeltaY.value));
                    Utilities.scaleRectAboutCenter(this.val$temp, this.mCircleScale.value);
                    this.this$0.mCircle.setBounds(this.val$temp);
                    this.this$0.mCircle.setAlpha((int) this.mCircleAlpha.value);
                    this.this$0.mGradient.setAlpha((int) this.mGradientAlpha.value);
                    this.val$temp.set(this.val$gradientBoundsOg);
                    Rect rect = this.val$temp;
                    rect.top = (int) (((float) rect.top) - this.mDeltaY.value);
                    this.this$0.mGradient.setBounds(this.val$temp);
                    this.this$0.invalidate();
                    this.val$stateAnimationController.setPlayFraction(Utilities.mapToRange(this.mDeltaY.value, 0.0f, this.val$transY, 0.0f, this.val$maxAllAppsProgress, Interpolators.LINEAR));
                }
            };
            ofFloat.addUpdateListener(r12);
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AllAppsEduView.this.mCircle.setAlpha(0);
                    AllAppsEduView.this.mGradient.setAlpha(0);
                }
            });
            this.mLauncher.getAppsView().setVisibility(0);
            this.mAnimation.play(ofFloat);
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.75f, 0.0f});
            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AnimatorPlaybackController.this.setPlayFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            ofFloat2.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            ofFloat2.setStartDelay(j);
            ofFloat2.setDuration(250);
            this.mAnimation.play(ofFloat2);
            this.mAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = AllAppsEduView.this.mAnimation = null;
                    AllAppsEduView.this.mLauncher.getStateManager().goToState(LauncherState.NORMAL, false);
                    AllAppsEduView.this.handleClose(false);
                }
            });
            this.mAnimation.start();
        }
    }

    private void init(Launcher launcher) {
        int[] iArr;
        this.mLauncher = launcher;
        this.mTouchController = new AllAppsEduTouchController(this.mLauncher);
        int colorAccent = Themes.getColorAccent(launcher);
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        if (Themes.getAttrBoolean(launcher, R.attr.isMainColorDark)) {
            iArr = new int[]{-1275068417, ViewCompat.MEASURED_SIZE_MASK};
        } else {
            iArr = new int[]{ColorUtils.setAlphaComponent(colorAccent, WorkQueueKt.MASK), ColorUtils.setAlphaComponent(colorAccent, 0)};
        }
        GradientDrawable gradientDrawable = new GradientDrawable(orientation, iArr);
        this.mGradient = gradientDrawable;
        float f = ((float) this.mWidthPx) / 2.0f;
        gradientDrawable.setCornerRadii(new float[]{f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f});
        int i = this.mMaxHeightPx;
        int i2 = this.mCircleSizePx;
        int i3 = this.mPaddingPx;
        int i4 = (i - i2) + i3;
        this.mCircle.setBounds(i3, i4, i3 + i2, i2 + i4);
        GradientDrawable gradientDrawable2 = this.mGradient;
        int i5 = this.mMaxHeightPx;
        gradientDrawable2.setBounds(0, i5 - this.mCircleSizePx, this.mWidthPx, i5);
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(this.mWidthPx, this.mMaxHeightPx);
        layoutParams.ignoreInsets = true;
        layoutParams.leftMargin = (deviceProfile.widthPx - this.mWidthPx) / 2;
        layoutParams.topMargin = (deviceProfile.heightPx - deviceProfile.hotseatBarSizePx) - this.mMaxHeightPx;
        setLayoutParams(layoutParams);
    }

    public static void show(Launcher launcher) {
        AllAppsEduView allAppsEduView = (AllAppsEduView) launcher.getLayoutInflater().inflate(R.layout.all_apps_edu_view, launcher.getDragLayer(), false);
        allAppsEduView.init(launcher);
        launcher.getDragLayer().addView(allAppsEduView);
        launcher.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALL_APPS_EDU_SHOWN);
        allAppsEduView.requestLayout();
        allAppsEduView.playAnimation();
    }

    private static class AllAppsEduTouchController extends PortraitStatesTouchController {
        /* access modifiers changed from: protected */
        public boolean canInterceptTouch(MotionEvent motionEvent) {
            return true;
        }

        private AllAppsEduTouchController(Launcher launcher) {
            super(launcher);
        }

        /* access modifiers changed from: private */
        public AnimatorPlaybackController initAllAppsAnimation() {
            this.mFromState = LauncherState.NORMAL;
            this.mToState = LauncherState.ALL_APPS;
            this.mProgressMultiplier = initCurrentAnimation();
            return this.mCurrentAnimation;
        }

        /* access modifiers changed from: private */
        public boolean isDraggingOrSettling() {
            return this.mDetector.isDraggingOrSettling();
        }
    }
}
