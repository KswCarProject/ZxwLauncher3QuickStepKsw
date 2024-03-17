package com.android.quickstep.interaction;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.anim.Interpolators;
import com.android.quickstep.util.VibratorWrapper;

public class EdgeBackGesturePanel extends View {
    private static final int ARROW_ANGLE_ADDED_PER_1000_SPEED = 4;
    private static final int ARROW_ANGLE_WHEN_EXTENDED_DEGREES = 56;
    private static final int ARROW_LENGTH_DP = 18;
    private static final int ARROW_MAX_ANGLE_SPEED_OFFSET_DEGREES = 4;
    private static final float ARROW_THICKNESS_DP = 2.5f;
    private static final int BASE_TRANSLATION_DP = 32;
    private static final FloatPropertyCompat<EdgeBackGesturePanel> CURRENT_ANGLE = new FloatPropertyCompat<EdgeBackGesturePanel>("currentAngle") {
        public void setValue(EdgeBackGesturePanel edgeBackGesturePanel, float f) {
            edgeBackGesturePanel.setCurrentAngle(f);
        }

        public float getValue(EdgeBackGesturePanel edgeBackGesturePanel) {
            return edgeBackGesturePanel.getCurrentAngle();
        }
    };
    private static final FloatPropertyCompat<EdgeBackGesturePanel> CURRENT_TRANSLATION = new FloatPropertyCompat<EdgeBackGesturePanel>("currentTranslation") {
        public void setValue(EdgeBackGesturePanel edgeBackGesturePanel, float f) {
            edgeBackGesturePanel.setCurrentTranslation(f);
        }

        public float getValue(EdgeBackGesturePanel edgeBackGesturePanel) {
            return edgeBackGesturePanel.getCurrentTranslation();
        }
    };
    private static final FloatPropertyCompat<EdgeBackGesturePanel> CURRENT_VERTICAL_TRANSLATION = new FloatPropertyCompat<EdgeBackGesturePanel>("verticalTranslation") {
        public void setValue(EdgeBackGesturePanel edgeBackGesturePanel, float f) {
            edgeBackGesturePanel.setVerticalTranslation(f);
        }

        public float getValue(EdgeBackGesturePanel edgeBackGesturePanel) {
            return edgeBackGesturePanel.getVerticalTranslation();
        }
    };
    private static final long DISAPPEAR_ARROW_ANIMATION_DURATION_MS = 100;
    private static final long DISAPPEAR_FADE_ANIMATION_DURATION_MS = 80;
    private static final int GESTURE_DURATION_FOR_CLICK_MS = 400;
    private static final String LOG_TAG = "EdgeBackGesturePanel";
    private static final int RUBBER_BAND_AMOUNT = 15;
    private static final int RUBBER_BAND_AMOUNT_APPEAR = 4;
    private static final Interpolator RUBBER_BAND_INTERPOLATOR = new PathInterpolator(0.2f, 1.0f, 1.0f, 1.0f);
    private static final Interpolator RUBBER_BAND_INTERPOLATOR_APPEAR = new PathInterpolator(0.25f, 1.0f, 1.0f, 1.0f);
    private final SpringAnimation mAngleAnimation;
    private final SpringForce mAngleAppearForce;
    private final SpringForce mAngleDisappearForce;
    private float mAngleOffset;
    private final ValueAnimator mArrowDisappearAnimation;
    private final float mArrowLength;
    private int mArrowPaddingEnd;
    private final Path mArrowPath = new Path();
    private final float mArrowThickness;
    private boolean mArrowsPointLeft;
    private BackCallback mBackCallback;
    private final float mBaseTranslation;
    private float mCurrentAngle;
    private float mCurrentTranslation;
    private final float mDensity;
    private float mDesiredAngle;
    private float mDesiredTranslation;
    private float mDesiredVerticalTranslation;
    private float mDisappearAmount;
    private final Point mDisplaySize = new Point();
    private boolean mDragSlopPassed;
    private int mFingerOffset;
    private boolean mIsLeftPanel;
    private float mMaxTranslation;
    private int mMinArrowPosition;
    private final float mMinDeltaForSwitch;
    private final Paint mPaint;
    private float mPreviousTouchTranslation;
    private final SpringForce mRegularTranslationSpring;
    private int mScreenSize;
    private final DynamicAnimation.OnAnimationEndListener mSetGoneEndListener = new DynamicAnimation.OnAnimationEndListener() {
        public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            dynamicAnimation.removeEndListener(this);
            if (!z) {
                EdgeBackGesturePanel.this.setVisibility(8);
            }
        }
    };
    private float mStartX;
    private float mStartY;
    private final float mSwipeThreshold;
    private float mTotalTouchDelta;
    private final SpringAnimation mTranslationAnimation;
    private boolean mTriggerBack;
    private final SpringForce mTriggerBackSpring;
    private VelocityTracker mVelocityTracker;
    private float mVerticalTranslation;
    private final SpringAnimation mVerticalTranslationAnimation;
    private long mVibrationTime;

    interface BackCallback {
        void cancelBack();

        void triggerBack();
    }

    private static float lerp(float f, float f2, float f3) {
        return f + ((f2 - f) * f3);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public EdgeBackGesturePanel(Context context, ViewGroup viewGroup, ViewGroup.LayoutParams layoutParams) {
        super(context);
        Paint paint = new Paint();
        this.mPaint = paint;
        this.mDensity = context.getResources().getDisplayMetrics().density;
        this.mBaseTranslation = dp(32.0f);
        this.mArrowLength = dp(18.0f);
        float dp = dp(ARROW_THICKNESS_DP);
        this.mArrowThickness = dp;
        this.mMinDeltaForSwitch = dp(32.0f);
        paint.setStrokeWidth(dp);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mArrowDisappearAnimation = ofFloat;
        ofFloat.setDuration(DISAPPEAR_ARROW_ANIMATION_DURATION_MS);
        ofFloat.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                EdgeBackGesturePanel.this.lambda$new$0$EdgeBackGesturePanel(valueAnimator);
            }
        });
        SpringAnimation springAnimation = new SpringAnimation(this, CURRENT_ANGLE);
        this.mAngleAnimation = springAnimation;
        SpringForce dampingRatio = new SpringForce().setStiffness(500.0f).setDampingRatio(0.5f);
        this.mAngleAppearForce = dampingRatio;
        this.mAngleDisappearForce = new SpringForce().setStiffness(1500.0f).setDampingRatio(0.5f).setFinalPosition(90.0f);
        springAnimation.setSpring(dampingRatio).setMaxValue(90.0f);
        SpringAnimation springAnimation2 = new SpringAnimation(this, CURRENT_TRANSLATION);
        this.mTranslationAnimation = springAnimation2;
        SpringForce dampingRatio2 = new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f);
        this.mRegularTranslationSpring = dampingRatio2;
        this.mTriggerBackSpring = new SpringForce().setStiffness(450.0f).setDampingRatio(0.75f);
        springAnimation2.setSpring(dampingRatio2);
        SpringAnimation springAnimation3 = new SpringAnimation(this, CURRENT_VERTICAL_TRANSLATION);
        this.mVerticalTranslationAnimation = springAnimation3;
        springAnimation3.setSpring(new SpringForce().setStiffness(1500.0f).setDampingRatio(0.75f));
        int i = context.getResources().getConfiguration().uiMode;
        paint.setColor(context.getColor(R.color.gesture_tutorial_back_arrow_color));
        loadDimens();
        updateArrowDirection();
        this.mSwipeThreshold = (float) ResourceUtils.getDimenByName("navigation_edge_action_drag_threshold", context.getResources(), 16);
        viewGroup.addView(this, layoutParams);
        setVisibility(8);
    }

    public /* synthetic */ void lambda$new$0$EdgeBackGesturePanel(ValueAnimator valueAnimator) {
        this.mDisappearAmount = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void setIsLeftPanel(boolean z) {
        this.mIsLeftPanel = z;
    }

    /* access modifiers changed from: package-private */
    public boolean getIsLeftPanel() {
        return this.mIsLeftPanel;
    }

    /* access modifiers changed from: package-private */
    public void setDisplaySize(Point point) {
        this.mDisplaySize.set(point.x, point.y);
        this.mScreenSize = Math.min(this.mDisplaySize.x, this.mDisplaySize.y);
    }

    /* access modifiers changed from: package-private */
    public void setBackCallback(BackCallback backCallback) {
        this.mBackCallback = backCallback;
    }

    /* access modifiers changed from: private */
    public float getCurrentAngle() {
        return this.mCurrentAngle;
    }

    /* access modifiers changed from: private */
    public float getCurrentTranslation() {
        return this.mCurrentTranslation;
    }

    /* access modifiers changed from: package-private */
    public void onMotionEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mDragSlopPassed = false;
            resetOnDown();
            this.mStartX = motionEvent.getX();
            this.mStartY = motionEvent.getY();
            setVisibility(0);
            updatePosition(motionEvent.getY());
        } else if (actionMasked == 1) {
            if (this.mTriggerBack) {
                triggerBack();
            } else {
                cancelBack();
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else if (actionMasked == 2) {
            handleMoveEvent(motionEvent);
        } else if (actionMasked == 3) {
            cancelBack();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateArrowDirection();
        loadDimens();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = this.mCurrentTranslation - (this.mArrowThickness / 2.0f);
        canvas.save();
        if (!this.mIsLeftPanel) {
            f = ((float) getWidth()) - f;
        }
        canvas.translate(f, (((float) getHeight()) * 0.5f) + this.mVerticalTranslation);
        canvas.drawPath(calculatePath(polarToCartX(this.mCurrentAngle) * this.mArrowLength, polarToCartY(this.mCurrentAngle) * this.mArrowLength), this.mPaint);
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mMaxTranslation = (float) (getWidth() - this.mArrowPaddingEnd);
    }

    private void loadDimens() {
        Resources resources = getResources();
        this.mArrowPaddingEnd = ResourceUtils.getDimenByName("navigation_edge_panel_padding", resources, 8);
        this.mMinArrowPosition = ResourceUtils.getDimenByName("navigation_edge_arrow_min_y", resources, 64);
        this.mFingerOffset = ResourceUtils.getDimenByName("navigation_edge_finger_offset", resources, 48);
    }

    private void updateArrowDirection() {
        this.mArrowsPointLeft = getLayoutDirection() == 0;
        invalidate();
    }

    private float getStaticArrowWidth() {
        return polarToCartX(56.0f) * this.mArrowLength;
    }

    private float polarToCartX(float f) {
        return (float) Math.cos(Math.toRadians((double) f));
    }

    private float polarToCartY(float f) {
        return (float) Math.sin(Math.toRadians((double) f));
    }

    private Path calculatePath(float f, float f2) {
        if (!this.mArrowsPointLeft) {
            f = -f;
        }
        float lerp = lerp(1.0f, 0.75f, this.mDisappearAmount);
        float f3 = f * lerp;
        float f4 = f2 * lerp;
        this.mArrowPath.reset();
        this.mArrowPath.moveTo(f3, f4);
        this.mArrowPath.lineTo(0.0f, 0.0f);
        this.mArrowPath.lineTo(f3, -f4);
        return this.mArrowPath;
    }

    private void triggerBack() {
        BackCallback backCallback = this.mBackCallback;
        if (backCallback != null) {
            backCallback.triggerBack();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        if ((Math.abs(this.mVelocityTracker.getXVelocity()) < 500.0f) || SystemClock.uptimeMillis() - this.mVibrationTime >= 400) {
            VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).vibrate(VibratorWrapper.EFFECT_CLICK);
        }
        float f = this.mAngleOffset;
        if (f > -4.0f) {
            this.mAngleOffset = Math.max(-8.0f, f - 8.0f);
            updateAngle(true);
        }
        final $$Lambda$EdgeBackGesturePanel$em4uXOotbLRxb54yoD3GkxnWIuw r0 = new Runnable() {
            public final void run() {
                EdgeBackGesturePanel.this.lambda$triggerBack$2$EdgeBackGesturePanel();
            }
        };
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    dynamicAnimation.removeEndListener(this);
                    if (!z) {
                        r0.run();
                    }
                }
            });
        } else {
            r0.run();
        }
    }

    public /* synthetic */ void lambda$triggerBack$2$EdgeBackGesturePanel() {
        this.mAngleOffset = Math.max(0.0f, this.mAngleOffset + 8.0f);
        updateAngle(true);
        this.mTranslationAnimation.setSpring(this.mTriggerBackSpring);
        setDesiredTranslation(this.mDesiredTranslation - dp(32.0f), true);
        animate().alpha(0.0f).setDuration(DISAPPEAR_FADE_ANIMATION_DURATION_MS).withEndAction(new Runnable() {
            public final void run() {
                EdgeBackGesturePanel.this.lambda$triggerBack$1$EdgeBackGesturePanel();
            }
        });
        this.mArrowDisappearAnimation.start();
    }

    public /* synthetic */ void lambda$triggerBack$1$EdgeBackGesturePanel() {
        setVisibility(8);
    }

    private void cancelBack() {
        BackCallback backCallback = this.mBackCallback;
        if (backCallback != null) {
            backCallback.cancelBack();
        }
        if (this.mTranslationAnimation.isRunning()) {
            this.mTranslationAnimation.addEndListener(this.mSetGoneEndListener);
        } else {
            setVisibility(8);
        }
    }

    private void resetOnDown() {
        animate().cancel();
        this.mAngleAnimation.cancel();
        this.mTranslationAnimation.cancel();
        this.mVerticalTranslationAnimation.cancel();
        this.mArrowDisappearAnimation.cancel();
        this.mAngleOffset = 0.0f;
        this.mTranslationAnimation.setSpring(this.mRegularTranslationSpring);
        setTriggerBack(false, false);
        setDesiredTranslation(0.0f, false);
        setCurrentTranslation(0.0f);
        updateAngle(false);
        this.mPreviousTouchTranslation = 0.0f;
        this.mTotalTouchDelta = 0.0f;
        this.mVibrationTime = 0;
        setDesiredVerticalTransition(0.0f, false);
    }

    private void handleMoveEvent(MotionEvent motionEvent) {
        float f;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float abs = Math.abs(x - this.mStartX);
        float f2 = y - this.mStartY;
        float f3 = abs - this.mPreviousTouchTranslation;
        if (Math.abs(f3) > 0.0f) {
            if (Math.signum(f3) == Math.signum(this.mTotalTouchDelta)) {
                this.mTotalTouchDelta += f3;
            } else {
                this.mTotalTouchDelta = f3;
            }
        }
        this.mPreviousTouchTranslation = abs;
        if (!this.mDragSlopPassed && abs > this.mSwipeThreshold) {
            this.mDragSlopPassed = true;
            VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(getContext()).vibrate(VibratorWrapper.EFFECT_CLICK);
            this.mVibrationTime = SystemClock.uptimeMillis();
            this.mDisappearAmount = 0.0f;
            setAlpha(1.0f);
            setTriggerBack(true, true);
        }
        float f4 = this.mBaseTranslation;
        if (abs > f4) {
            float interpolation = RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.clamp((abs - f4) / (((float) this.mScreenSize) - f4), 0.0f, 1.0f));
            float f5 = this.mMaxTranslation;
            float f6 = this.mBaseTranslation;
            f = f6 + (interpolation * (f5 - f6));
        } else {
            float interpolation2 = RUBBER_BAND_INTERPOLATOR_APPEAR.getInterpolation(MathUtils.clamp((f4 - abs) / f4, 0.0f, 1.0f));
            float f7 = this.mBaseTranslation;
            f = f7 - (interpolation2 * (f7 / 4.0f));
        }
        boolean z = this.mTriggerBack;
        boolean z2 = false;
        if (Math.abs(this.mTotalTouchDelta) > this.mMinDeltaForSwitch) {
            z = this.mTotalTouchDelta > 0.0f;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = this.mVelocityTracker.getXVelocity();
        float min = Math.min((((float) Math.hypot((double) xVelocity, (double) this.mVelocityTracker.getYVelocity())) / 1000.0f) * 4.0f, 4.0f) * Math.signum(xVelocity);
        this.mAngleOffset = min;
        boolean z3 = this.mIsLeftPanel;
        if ((z3 && this.mArrowsPointLeft) || (!z3 && !this.mArrowsPointLeft)) {
            this.mAngleOffset = min * -1.0f;
        }
        if (Math.abs(f2) <= Math.abs(x - this.mStartX) * 2.0f) {
            z2 = z;
        }
        setTriggerBack(z2, true);
        if (!this.mTriggerBack) {
            f = 0.0f;
        } else {
            boolean z4 = this.mIsLeftPanel;
            if ((z4 && this.mArrowsPointLeft) || (!z4 && !this.mArrowsPointLeft)) {
                f -= getStaticArrowWidth();
            }
        }
        setDesiredTranslation(f, true);
        updateAngle(true);
        float height = (((float) getHeight()) / 2.0f) - this.mArrowLength;
        setDesiredVerticalTransition(RUBBER_BAND_INTERPOLATOR.getInterpolation(MathUtils.clamp(Math.abs(f2) / (15.0f * height), 0.0f, 1.0f)) * height * Math.signum(f2), true);
    }

    private void updatePosition(float f) {
        float max = Math.max(f - ((float) this.mFingerOffset), (float) this.mMinArrowPosition) - (((float) getLayoutParams().height) / 2.0f);
        setX(this.mIsLeftPanel ? 0.0f : (float) (this.mDisplaySize.x - getLayoutParams().width));
        setY((float) MathUtils.clamp((int) max, 0, this.mDisplaySize.y));
    }

    private void setDesiredVerticalTransition(float f, boolean z) {
        if (this.mDesiredVerticalTranslation != f) {
            this.mDesiredVerticalTranslation = f;
            if (!z) {
                setVerticalTranslation(f);
            } else {
                this.mVerticalTranslationAnimation.animateToFinalPosition(f);
            }
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void setVerticalTranslation(float f) {
        this.mVerticalTranslation = f;
        invalidate();
    }

    /* access modifiers changed from: private */
    public float getVerticalTranslation() {
        return this.mVerticalTranslation;
    }

    private void setDesiredTranslation(float f, boolean z) {
        if (this.mDesiredTranslation != f) {
            this.mDesiredTranslation = f;
            if (!z) {
                setCurrentTranslation(f);
            } else {
                this.mTranslationAnimation.animateToFinalPosition(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentTranslation(float f) {
        this.mCurrentTranslation = f;
        invalidate();
    }

    private void setTriggerBack(boolean z, boolean z2) {
        if (this.mTriggerBack != z) {
            this.mTriggerBack = z;
            this.mAngleAnimation.cancel();
            updateAngle(z2);
            this.mTranslationAnimation.cancel();
        }
    }

    private void updateAngle(boolean z) {
        boolean z2 = this.mTriggerBack;
        float f = z2 ? this.mAngleOffset + 56.0f : 90.0f;
        if (f != this.mDesiredAngle) {
            if (!z) {
                setCurrentAngle(f);
            } else {
                this.mAngleAnimation.setSpring(z2 ? this.mAngleAppearForce : this.mAngleDisappearForce);
                this.mAngleAnimation.animateToFinalPosition(f);
            }
            this.mDesiredAngle = f;
        }
    }

    /* access modifiers changed from: private */
    public void setCurrentAngle(float f) {
        this.mCurrentAngle = f;
        invalidate();
    }

    private float dp(float f) {
        return this.mDensity * f;
    }
}
