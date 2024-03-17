package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.view.View;
import com.android.launcher3.LauncherAnimUtils;
import java.util.function.Consumer;

public class AnimatedPropertySetter extends PropertySetter {
    protected final AnimatorSet mAnim = new AnimatorSet();
    protected ValueAnimator mProgressAnimator;

    public Animator setViewAlpha(View view, float f, TimeInterpolator timeInterpolator) {
        if (view == null || view.getAlpha() == f) {
            return NO_OP;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{f});
        ofFloat.addListener(new AlphaUpdateListener(view));
        ofFloat.setInterpolator(timeInterpolator);
        add(ofFloat);
        return ofFloat;
    }

    public Animator setViewBackgroundColor(View view, int i, TimeInterpolator timeInterpolator) {
        if (view == null || ((view.getBackground() instanceof ColorDrawable) && ((ColorDrawable) view.getBackground()).getColor() == i)) {
            return NO_OP;
        }
        ObjectAnimator ofArgb = ObjectAnimator.ofArgb(view, LauncherAnimUtils.VIEW_BACKGROUND_COLOR, new int[]{i});
        ofArgb.setInterpolator(timeInterpolator);
        add(ofArgb);
        return ofArgb;
    }

    public <T> Animator setFloat(T t, FloatProperty<T> floatProperty, float f, TimeInterpolator timeInterpolator) {
        if (((Float) floatProperty.get(t)).floatValue() == f) {
            return NO_OP;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(t, floatProperty, new float[]{f});
        ofFloat.setInterpolator(timeInterpolator);
        add(ofFloat);
        return ofFloat;
    }

    public <T> Animator setInt(T t, IntProperty<T> intProperty, int i, TimeInterpolator timeInterpolator) {
        if (((Integer) intProperty.get(t)).intValue() == i) {
            return NO_OP;
        }
        ObjectAnimator ofInt = ObjectAnimator.ofInt(t, intProperty, new int[]{i});
        ofInt.setInterpolator(timeInterpolator);
        add(ofInt);
        return ofInt;
    }

    public void addOnFrameCallback(Runnable runnable) {
        addOnFrameListener(new ValueAnimator.AnimatorUpdateListener(runnable) {
            public final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.run();
            }
        });
    }

    public void addOnFrameListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        if (this.mProgressAnimator == null) {
            this.mProgressAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        }
        this.mProgressAnimator.addUpdateListener(animatorUpdateListener);
    }

    public void addEndListener(Consumer<Boolean> consumer) {
        if (this.mProgressAnimator == null) {
            this.mProgressAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        }
        this.mProgressAnimator.addListener(AnimatorListeners.forEndCallback(consumer));
    }

    public void addListener(Animator.AnimatorListener animatorListener) {
        this.mAnim.addListener(animatorListener);
    }

    public void add(Animator animator) {
        this.mAnim.play(animator);
    }

    public AnimatorSet buildAnim() {
        ValueAnimator valueAnimator = this.mProgressAnimator;
        if (valueAnimator != null) {
            add(valueAnimator);
            this.mProgressAnimator = null;
        }
        return this.mAnim;
    }
}
