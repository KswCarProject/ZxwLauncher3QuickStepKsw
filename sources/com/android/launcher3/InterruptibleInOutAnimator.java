package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Property;

public class InterruptibleInOutAnimator {
    private static final int IN = 1;
    private static final int OUT = 2;
    private static final int STOPPED = 0;
    private static final Property<InterruptibleInOutAnimator, Float> VALUE = new Property<InterruptibleInOutAnimator, Float>(Float.TYPE, "value") {
        public Float get(InterruptibleInOutAnimator interruptibleInOutAnimator) {
            return Float.valueOf(interruptibleInOutAnimator.mValue);
        }

        public void set(InterruptibleInOutAnimator interruptibleInOutAnimator, Float f) {
            float unused = interruptibleInOutAnimator.mValue = f.floatValue();
        }
    };
    private ValueAnimator mAnimator;
    int mDirection = 0;
    private boolean mFirstRun = true;
    private long mOriginalDuration;
    private float mOriginalFromValue;
    private float mOriginalToValue;
    private Object mTag = null;
    /* access modifiers changed from: private */
    public float mValue;

    public InterruptibleInOutAnimator(long j, float f, float f2) {
        ObjectAnimator duration = ObjectAnimator.ofFloat(this, VALUE, new float[]{f, f2}).setDuration(j);
        this.mAnimator = duration;
        this.mOriginalDuration = j;
        this.mOriginalFromValue = f;
        this.mOriginalToValue = f2;
        duration.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                InterruptibleInOutAnimator.this.mDirection = 0;
            }
        });
    }

    private void animate(int i) {
        long currentPlayTime = this.mAnimator.getCurrentPlayTime();
        float f = i == 1 ? this.mOriginalToValue : this.mOriginalFromValue;
        float f2 = this.mFirstRun ? this.mOriginalFromValue : this.mValue;
        cancel();
        this.mDirection = i;
        long j = this.mOriginalDuration;
        this.mAnimator.setDuration(Math.max(0, Math.min(j - currentPlayTime, j)));
        this.mAnimator.setFloatValues(new float[]{f2, f});
        this.mAnimator.start();
        this.mFirstRun = false;
    }

    public void cancel() {
        this.mAnimator.cancel();
        this.mDirection = 0;
    }

    public void end() {
        this.mAnimator.end();
        this.mDirection = 0;
    }

    public boolean isStopped() {
        return this.mDirection == 0;
    }

    public void animateIn() {
        animate(1);
    }

    public void animateOut() {
        animate(2);
    }

    public void setTag(Object obj) {
        this.mTag = obj;
    }

    public Object getTag() {
        return this.mTag;
    }

    public ValueAnimator getAnimator() {
        return this.mAnimator;
    }
}
