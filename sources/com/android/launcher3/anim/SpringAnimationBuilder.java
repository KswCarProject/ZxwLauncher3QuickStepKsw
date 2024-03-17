package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.FloatProperty;
import com.android.launcher3.util.window.RefreshRateTracker;

public class SpringAnimationBuilder {
    private static final float THRESHOLD_MULTIPLIER = 0.65f;
    private double a;
    private double b;
    private double beta;
    private double gamma;
    private final Context mContext;
    private float mDampingRatio = 0.5f;
    private float mDuration = 0.0f;
    /* access modifiers changed from: private */
    public float mEndValue;
    private float mMinVisibleChange = 1.0f;
    private float mStartValue;
    private float mStiffness = 1500.0f;
    private double mValueThreshold;
    private float mVelocity = 0.0f;
    private double mVelocityThreshold;
    private double va;
    private double vb;

    public SpringAnimationBuilder(Context context) {
        this.mContext = context;
    }

    public SpringAnimationBuilder setEndValue(float f) {
        this.mEndValue = f;
        return this;
    }

    public SpringAnimationBuilder setStartValue(float f) {
        this.mStartValue = f;
        return this;
    }

    public SpringAnimationBuilder setValues(float... fArr) {
        if (fArr.length > 1) {
            this.mStartValue = fArr[0];
            this.mEndValue = fArr[fArr.length - 1];
        } else {
            this.mEndValue = fArr[0];
        }
        return this;
    }

    public SpringAnimationBuilder setStiffness(float f) {
        if (f > 0.0f) {
            this.mStiffness = f;
            return this;
        }
        throw new IllegalArgumentException("Spring stiffness constant must be positive.");
    }

    public SpringAnimationBuilder setDampingRatio(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("Damping ratio must be between 0 and 1");
        }
        this.mDampingRatio = f;
        return this;
    }

    public SpringAnimationBuilder setMinimumVisibleChange(float f) {
        if (f > 0.0f) {
            this.mMinVisibleChange = f;
            return this;
        }
        throw new IllegalArgumentException("Minimum visible change must be positive.");
    }

    public SpringAnimationBuilder setStartVelocity(float f) {
        this.mVelocity = f;
        return this;
    }

    public float getInterpolatedValue(float f) {
        return getValue(this.mDuration * f);
    }

    private float getValue(float f) {
        double d = (double) f;
        return ((float) (exponentialComponent(d) * cosSinX(d))) + this.mEndValue;
    }

    public SpringAnimationBuilder computeParams() {
        int singleFrameMs = RefreshRateTracker.getSingleFrameMs(this.mContext);
        double sqrt = Math.sqrt((double) this.mStiffness);
        float f = this.mDampingRatio;
        double sqrt2 = Math.sqrt((double) (1.0f - (f * f))) * sqrt;
        double d = ((double) (this.mDampingRatio * 2.0f)) * sqrt;
        this.beta = d;
        this.gamma = sqrt2;
        double d2 = (double) (this.mStartValue - this.mEndValue);
        this.a = d2;
        double d3 = ((d * d2) / (sqrt2 * 2.0d)) + (((double) this.mVelocity) / sqrt2);
        this.b = d3;
        this.va = ((d2 * d) / 2.0d) - (d3 * sqrt2);
        this.vb = (sqrt2 * d2) + ((d * d3) / 2.0d);
        double d4 = (double) (this.mMinVisibleChange * THRESHOLD_MULTIPLIER);
        this.mValueThreshold = d4;
        double d5 = (double) singleFrameMs;
        this.mVelocityThreshold = (d4 * 1000.0d) / d5;
        double atan2 = Math.atan2(-d2, d3);
        double d6 = this.gamma;
        double d7 = atan2 / d6;
        double d8 = 3.141592653589793d / d6;
        while (true) {
            if (d7 >= 0.0d && Math.abs(exponentialComponent(d7) * cosSinV(d7)) < this.mVelocityThreshold) {
                break;
            }
            d7 += d8;
        }
        double max = Math.max(0.0d, d7 - (d8 / 2.0d));
        double d9 = d5 / 2000.0d;
        while (d7 - max >= d9) {
            double d10 = (max + d7) / 2.0d;
            if (isAtEquilibrium(d10)) {
                d7 = d10;
            } else {
                max = d10;
            }
        }
        this.mDuration = (float) d7;
        return this;
    }

    public long getDuration() {
        return (long) (((double) this.mDuration) * 1000.0d);
    }

    public <T> ValueAnimator build(final T t, final FloatProperty<T> floatProperty) {
        computeParams();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, this.mDuration});
        ofFloat.setDuration(getDuration()).setInterpolator(Interpolators.LINEAR);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(floatProperty, t) {
            public final /* synthetic */ FloatProperty f$1;
            public final /* synthetic */ Object f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                SpringAnimationBuilder.this.lambda$build$0$SpringAnimationBuilder(this.f$1, this.f$2, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                floatProperty.set(t, Float.valueOf(SpringAnimationBuilder.this.mEndValue));
            }
        });
        return ofFloat;
    }

    public /* synthetic */ void lambda$build$0$SpringAnimationBuilder(FloatProperty floatProperty, Object obj, ValueAnimator valueAnimator) {
        floatProperty.set(obj, Float.valueOf(getInterpolatedValue(valueAnimator.getAnimatedFraction())));
    }

    private boolean isAtEquilibrium(double d) {
        double exponentialComponent = exponentialComponent(d);
        if (Math.abs(cosSinX(d) * exponentialComponent) < this.mValueThreshold && Math.abs(exponentialComponent * cosSinV(d)) < this.mVelocityThreshold) {
            return true;
        }
        return false;
    }

    private double exponentialComponent(double d) {
        return Math.pow(2.718281828459045d, ((-this.beta) * d) / 2.0d);
    }

    private double cosSinX(double d) {
        return cosSin(d, this.a, this.b);
    }

    private double cosSinV(double d) {
        return cosSin(d, this.va, this.vb);
    }

    private double cosSin(double d, double d2, double d3) {
        double d4 = d * this.gamma;
        return (d2 * Math.cos(d4)) + (d3 * Math.sin(d4));
    }
}
