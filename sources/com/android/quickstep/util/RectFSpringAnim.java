package com.android.quickstep.util;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.FlingSpringAnim;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.util.DynamicResource;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.systemui.plugins.ResourceProvider;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class RectFSpringAnim extends RemoteAnimationTargets.ReleaseCheck {
    private static final FloatPropertyCompat<RectFSpringAnim> RECT_CENTER_X = new FloatPropertyCompat<RectFSpringAnim>("rectCenterXSpring") {
        public float getValue(RectFSpringAnim rectFSpringAnim) {
            return rectFSpringAnim.mCurrentCenterX;
        }

        public void setValue(RectFSpringAnim rectFSpringAnim, float f) {
            float unused = rectFSpringAnim.mCurrentCenterX = f;
            rectFSpringAnim.onUpdate();
        }
    };
    private static final FloatPropertyCompat<RectFSpringAnim> RECT_SCALE_PROGRESS = new FloatPropertyCompat<RectFSpringAnim>("rectScaleProgress") {
        public float getValue(RectFSpringAnim rectFSpringAnim) {
            return rectFSpringAnim.mCurrentScaleProgress;
        }

        public void setValue(RectFSpringAnim rectFSpringAnim, float f) {
            float unused = rectFSpringAnim.mCurrentScaleProgress = f;
            rectFSpringAnim.onUpdate();
        }
    };
    private static final FloatPropertyCompat<RectFSpringAnim> RECT_Y = new FloatPropertyCompat<RectFSpringAnim>("rectYSpring") {
        public float getValue(RectFSpringAnim rectFSpringAnim) {
            return rectFSpringAnim.mCurrentY;
        }

        public void setValue(RectFSpringAnim rectFSpringAnim, float f) {
            float unused = rectFSpringAnim.mCurrentY = f;
            rectFSpringAnim.onUpdate();
        }
    };
    public static final int TRACKING_BOTTOM = 2;
    public static final int TRACKING_CENTER = 1;
    public static final int TRACKING_TOP = 0;
    private final List<Animator.AnimatorListener> mAnimatorListeners = new ArrayList();
    private boolean mAnimsStarted;
    /* access modifiers changed from: private */
    public float mCurrentCenterX;
    private final RectF mCurrentRect = new RectF();
    /* access modifiers changed from: private */
    public float mCurrentScaleProgress;
    /* access modifiers changed from: private */
    public float mCurrentY;
    private int mMaxVelocityPxPerS;
    private float mMinVisChange;
    private final List<OnUpdateListener> mOnUpdateListeners = new ArrayList();
    private SpringAnimation mRectScaleAnim;
    private boolean mRectScaleAnimEnded;
    private FlingSpringAnim mRectXAnim;
    private boolean mRectXAnimEnded;
    private FlingSpringAnim mRectYAnim;
    private boolean mRectYAnimEnded;
    private final RectF mStartRect;
    private final RectF mTargetRect;
    public final int mTracking;

    public interface OnUpdateListener {
        void onCancel() {
        }

        void onUpdate(RectF rectF, float f);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Tracking {
    }

    public RectFSpringAnim(RectF rectF, RectF rectF2, Context context, DeviceProfile deviceProfile) {
        this.mStartRect = rectF;
        this.mTargetRect = rectF2;
        this.mCurrentCenterX = rectF.centerX();
        ResourceProvider provider = DynamicResource.provider(context);
        this.mMinVisChange = provider.getDimension(R.dimen.swipe_up_fling_min_visible_change);
        this.mMaxVelocityPxPerS = (int) provider.getDimension(R.dimen.swipe_up_max_velocity);
        setCanRelease(true);
        int i = 0;
        if (deviceProfile == null) {
            this.mTracking = rectF.bottom < rectF2.bottom ? 2 : i;
        } else {
            int i2 = deviceProfile.heightPx;
            float f = ((float) i2) / 3.0f;
            if (rectF2.bottom > ((float) (deviceProfile.heightPx - deviceProfile.workspacePadding.bottom))) {
                this.mTracking = 2;
            } else if (rectF2.top < f) {
                this.mTracking = 0;
            } else {
                this.mTracking = 1;
            }
        }
        this.mCurrentY = getTrackedYFromRect(rectF);
    }

    private float getTrackedYFromRect(RectF rectF) {
        int i = this.mTracking;
        if (i == 0) {
            return rectF.top;
        }
        if (i != 2) {
            return rectF.centerY();
        }
        return rectF.bottom;
    }

    public void onTargetPositionChanged() {
        FlingSpringAnim flingSpringAnim = this.mRectXAnim;
        if (!(flingSpringAnim == null || flingSpringAnim.getTargetPosition() == this.mTargetRect.centerX())) {
            this.mRectXAnim.updatePosition(this.mCurrentCenterX, this.mTargetRect.centerX());
        }
        FlingSpringAnim flingSpringAnim2 = this.mRectYAnim;
        if (flingSpringAnim2 != null) {
            int i = this.mTracking;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2 && flingSpringAnim2.getTargetPosition() != this.mTargetRect.bottom) {
                        this.mRectYAnim.updatePosition(this.mCurrentY, this.mTargetRect.bottom);
                    }
                } else if (flingSpringAnim2.getTargetPosition() != this.mTargetRect.centerY()) {
                    this.mRectYAnim.updatePosition(this.mCurrentY, this.mTargetRect.centerY());
                }
            } else if (flingSpringAnim2.getTargetPosition() != this.mTargetRect.top) {
                this.mRectYAnim.updatePosition(this.mCurrentY, this.mTargetRect.top);
            }
        }
    }

    public void addOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.mOnUpdateListeners.add(onUpdateListener);
    }

    public void addAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimatorListeners.add(animatorListener);
    }

    public void start(Context context, PointF pointF) {
        PointF pointF2 = pointF;
        $$Lambda$RectFSpringAnim$ZloMU1iAIpWQX5m_VnrbAvueEX8 r10 = new DynamicAnimation.OnAnimationEndListener() {
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                RectFSpringAnim.this.lambda$start$0$RectFSpringAnim(dynamicAnimation, z, f, f2);
            }
        };
        $$Lambda$RectFSpringAnim$iZDgaingn4bLwxYrdJl_MZtr8M8 r13 = new DynamicAnimation.OnAnimationEndListener() {
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                RectFSpringAnim.this.lambda$start$1$RectFSpringAnim(dynamicAnimation, z, f, f2);
            }
        };
        float f = pointF2.x * 1000.0f;
        float f2 = pointF2.y * 1000.0f;
        float dampedScroll = ((float) OverScroll.dampedScroll(Math.abs(f), this.mMaxVelocityPxPerS)) * Math.signum(f);
        float dampedScroll2 = ((float) OverScroll.dampedScroll(Math.abs(f2), this.mMaxVelocityPxPerS)) * Math.signum(f2);
        float f3 = this.mCurrentCenterX;
        float centerX = this.mTargetRect.centerX();
        Context context2 = context;
        this.mRectXAnim = new FlingSpringAnim(this, context2, RECT_CENTER_X, f3, centerX, dampedScroll, this.mMinVisChange, Math.min(f3, centerX), Math.max(f3, centerX), r10);
        float f4 = this.mCurrentY;
        float trackedYFromRect = getTrackedYFromRect(this.mTargetRect);
        float f5 = dampedScroll2;
        this.mRectYAnim = new FlingSpringAnim(this, context2, RECT_Y, f4, trackedYFromRect, f5, this.mMinVisChange, Math.min(f4, trackedYFromRect), Math.max(f4, trackedYFromRect), r13);
        float abs = Math.abs(1.0f / this.mStartRect.height());
        ResourceProvider provider = DynamicResource.provider(context);
        float f6 = provider.getFloat(R.dimen.swipe_up_rect_scale_damping_ratio);
        this.mRectScaleAnim = (SpringAnimation) ((SpringAnimation) ((SpringAnimation) ((SpringAnimation) new SpringAnimation(this, RECT_SCALE_PROGRESS).setSpring(new SpringForce(1.0f).setDampingRatio(f6).setStiffness(provider.getFloat(R.dimen.swipe_up_rect_scale_stiffness))).setStartVelocity(pointF2.y * abs)).setMaxValue(1.0f)).setMinimumVisibleChange(abs)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                RectFSpringAnim.this.lambda$start$2$RectFSpringAnim(dynamicAnimation, z, f, f2);
            }
        });
        setCanRelease(false);
        this.mAnimsStarted = true;
        this.mRectXAnim.start();
        this.mRectYAnim.start();
        this.mRectScaleAnim.start();
        for (Animator.AnimatorListener onAnimationStart : this.mAnimatorListeners) {
            onAnimationStart.onAnimationStart((Animator) null);
        }
    }

    public /* synthetic */ void lambda$start$0$RectFSpringAnim(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mRectXAnimEnded = true;
        maybeOnEnd();
    }

    public /* synthetic */ void lambda$start$1$RectFSpringAnim(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mRectYAnimEnded = true;
        maybeOnEnd();
    }

    public /* synthetic */ void lambda$start$2$RectFSpringAnim(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.mRectScaleAnimEnded = true;
        maybeOnEnd();
    }

    public void end() {
        if (this.mAnimsStarted) {
            this.mRectXAnim.end();
            this.mRectYAnim.end();
            if (this.mRectScaleAnim.canSkipToEnd()) {
                this.mRectScaleAnim.skipToEnd();
            }
        }
        this.mRectXAnimEnded = true;
        this.mRectYAnimEnded = true;
        this.mRectScaleAnimEnded = true;
        maybeOnEnd();
    }

    private boolean isEnded() {
        return this.mRectXAnimEnded && this.mRectYAnimEnded && this.mRectScaleAnimEnded;
    }

    /* access modifiers changed from: private */
    public void onUpdate() {
        if (!isEnded() && !this.mOnUpdateListeners.isEmpty()) {
            float mapRange = Utilities.mapRange(this.mCurrentScaleProgress, this.mStartRect.width(), this.mTargetRect.width());
            float mapRange2 = Utilities.mapRange(this.mCurrentScaleProgress, this.mStartRect.height(), this.mTargetRect.height());
            int i = this.mTracking;
            if (i == 0) {
                RectF rectF = this.mCurrentRect;
                float f = this.mCurrentCenterX;
                float f2 = mapRange / 2.0f;
                float f3 = this.mCurrentY;
                rectF.set(f - f2, f3, f + f2, mapRange2 + f3);
            } else if (i == 1) {
                RectF rectF2 = this.mCurrentRect;
                float f4 = this.mCurrentCenterX;
                float f5 = mapRange / 2.0f;
                float f6 = this.mCurrentY;
                float f7 = mapRange2 / 2.0f;
                rectF2.set(f4 - f5, f6 - f7, f4 + f5, f6 + f7);
            } else if (i == 2) {
                RectF rectF3 = this.mCurrentRect;
                float f8 = this.mCurrentCenterX;
                float f9 = mapRange / 2.0f;
                float f10 = this.mCurrentY;
                rectF3.set(f8 - f9, f10 - mapRange2, f8 + f9, f10);
            }
            for (OnUpdateListener onUpdate : this.mOnUpdateListeners) {
                onUpdate.onUpdate(this.mCurrentRect, this.mCurrentScaleProgress);
            }
        }
    }

    private void maybeOnEnd() {
        if (this.mAnimsStarted && isEnded()) {
            this.mAnimsStarted = false;
            setCanRelease(true);
            for (Animator.AnimatorListener onAnimationEnd : this.mAnimatorListeners) {
                onAnimationEnd.onAnimationEnd((Animator) null);
            }
        }
    }

    public void cancel() {
        if (this.mAnimsStarted) {
            for (OnUpdateListener onCancel : this.mOnUpdateListeners) {
                onCancel.onCancel();
            }
        }
        end();
    }
}
