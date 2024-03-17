package com.android.launcher3.anim;

import android.content.Context;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.android.launcher3.R;
import com.android.launcher3.util.DynamicResource;
import com.android.systemui.plugins.ResourceProvider;

public class FlingSpringAnim {
    private final FlingAnimation mFlingAnim;
    private final boolean mSkipFlingAnim;
    private SpringAnimation mSpringAnim;
    private float mTargetPosition;

    public <K> FlingSpringAnim(K k, Context context, FloatPropertyCompat<K> floatPropertyCompat, float f, float f2, float f3, float f4, float f5, float f6, DynamicAnimation.OnAnimationEndListener onAnimationEndListener) {
        float f7 = f3;
        float f8 = f5;
        float f9 = f6;
        ResourceProvider provider = DynamicResource.provider(context);
        float f10 = provider.getFloat(R.dimen.swipe_up_rect_xy_damping_ratio);
        float f11 = provider.getFloat(R.dimen.swipe_up_rect_xy_stiffness);
        K k2 = k;
        FloatPropertyCompat<K> floatPropertyCompat2 = floatPropertyCompat;
        FlingAnimation maxValue = ((FlingAnimation) new FlingAnimation(k, floatPropertyCompat).setFriction(provider.getFloat(R.dimen.swipe_up_rect_xy_fling_friction)).setMinimumVisibleChange(f4)).setStartVelocity(f7).setMinValue(f8).setMaxValue(f9);
        this.mFlingAnim = maxValue;
        this.mTargetPosition = f2;
        this.mSkipFlingAnim = (f <= f8 && f7 < 0.0f) || (f >= f9 && f7 > 0.0f);
        maxValue.addEndListener(new DynamicAnimation.OnAnimationEndListener(k, floatPropertyCompat, f11, f10, onAnimationEndListener) {
            public final /* synthetic */ Object f$1;
            public final /* synthetic */ FloatPropertyCompat f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ float f$4;
            public final /* synthetic */ DynamicAnimation.OnAnimationEndListener f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                FlingSpringAnim.this.lambda$new$0$FlingSpringAnim(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dynamicAnimation, z, f, f2);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$FlingSpringAnim(Object obj, FloatPropertyCompat floatPropertyCompat, float f, float f2, DynamicAnimation.OnAnimationEndListener onAnimationEndListener, DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
        SpringAnimation spring = ((SpringAnimation) ((SpringAnimation) new SpringAnimation(obj, floatPropertyCompat).setStartValue(f3)).setStartVelocity(f4)).setSpring(new SpringForce(this.mTargetPosition).setStiffness(f).setDampingRatio(f2));
        this.mSpringAnim = spring;
        spring.addEndListener(onAnimationEndListener);
        this.mSpringAnim.animateToFinalPosition(this.mTargetPosition);
    }

    public float getTargetPosition() {
        return this.mTargetPosition;
    }

    public void updatePosition(float f, float f2) {
        this.mFlingAnim.setMinValue(Math.min(f, f2)).setMaxValue(Math.max(f, f2));
        this.mTargetPosition = f2;
        SpringAnimation springAnimation = this.mSpringAnim;
        if (springAnimation != null) {
            springAnimation.animateToFinalPosition(f2);
        }
    }

    public void start() {
        this.mFlingAnim.start();
        if (this.mSkipFlingAnim) {
            this.mFlingAnim.cancel();
        }
    }

    public void end() {
        this.mFlingAnim.cancel();
        if (this.mSpringAnim.canSkipToEnd()) {
            this.mSpringAnim.skipToEnd();
        }
    }
}
