package com.android.quickstep;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;

public class AnimatedFloat {
    private static final Runnable NO_OP = $$Lambda$AnimatedFloat$PU374XMcAp47MKOhTiMQ9MU5xUo.INSTANCE;
    public static final FloatProperty<AnimatedFloat> VALUE = new FloatProperty<AnimatedFloat>("value") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(AnimatedFloat animatedFloat, float f) {
            animatedFloat.updateValue(f);
        }

        public Float get(AnimatedFloat animatedFloat) {
            return Float.valueOf(animatedFloat.value);
        }
    };
    /* access modifiers changed from: private */
    public Float mEndValue;
    private final Runnable mUpdateCallback;
    /* access modifiers changed from: private */
    public ObjectAnimator mValueAnimator;
    public float value;

    static /* synthetic */ void lambda$static$0() {
    }

    public AnimatedFloat() {
        this(NO_OP);
    }

    public AnimatedFloat(Runnable runnable) {
        this.mUpdateCallback = runnable;
    }

    public ObjectAnimator animateToValue(float f) {
        return animateToValue(this.value, f);
    }

    public ObjectAnimator animateToValue(float f, final float f2) {
        cancelAnimation();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, VALUE, new float[]{f, f2});
        this.mValueAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (AnimatedFloat.this.mValueAnimator == animator) {
                    Float unused = AnimatedFloat.this.mEndValue = Float.valueOf(f2);
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (AnimatedFloat.this.mValueAnimator == animator) {
                    ObjectAnimator unused = AnimatedFloat.this.mValueAnimator = null;
                    Float unused2 = AnimatedFloat.this.mEndValue = null;
                }
            }
        });
        return this.mValueAnimator;
    }

    public void updateValue(float f) {
        if (Float.compare(f, this.value) != 0) {
            this.value = f;
            this.mUpdateCallback.run();
        }
    }

    public void startAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator != null) {
            objectAnimator.start();
        }
    }

    public void cancelAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    public void finishAnimation() {
        ObjectAnimator objectAnimator = this.mValueAnimator;
        if (objectAnimator != null && objectAnimator.isRunning()) {
            this.mValueAnimator.end();
        }
    }

    public ObjectAnimator getCurrentAnimation() {
        return this.mValueAnimator;
    }

    public boolean isAnimating() {
        return this.mValueAnimator != null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.mEndValue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isAnimatingToValue(float r2) {
        /*
            r1 = this;
            boolean r0 = r1.isAnimating()
            if (r0 == 0) goto L_0x0014
            java.lang.Float r0 = r1.mEndValue
            if (r0 == 0) goto L_0x0014
            float r0 = r0.floatValue()
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x0014
            r2 = 1
            goto L_0x0015
        L_0x0014:
            r2 = 0
        L_0x0015:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.AnimatedFloat.isAnimatingToValue(float):boolean");
    }
}
