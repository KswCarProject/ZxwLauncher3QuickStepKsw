package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.view.View;
import java.util.function.Consumer;

public abstract class PropertySetter {
    public static final PropertySetter NO_ANIM_PROPERTY_SETTER = new PropertySetter() {
        public void add(Animator animator) {
            animator.setDuration(0);
            animator.start();
        }
    };
    protected static final AnimatorSet NO_OP = new AnimatorSet();

    public abstract void add(Animator animator);

    public Animator setViewAlpha(View view, float f, TimeInterpolator timeInterpolator) {
        if (view != null) {
            view.setAlpha(f);
            AlphaUpdateListener.updateVisibility(view);
        }
        return NO_OP;
    }

    public Animator setViewBackgroundColor(View view, int i, TimeInterpolator timeInterpolator) {
        if (view != null) {
            view.setBackgroundColor(i);
        }
        return NO_OP;
    }

    public <T> Animator setFloat(T t, FloatProperty<T> floatProperty, float f, TimeInterpolator timeInterpolator) {
        floatProperty.setValue(t, f);
        return NO_OP;
    }

    public <T> Animator setInt(T t, IntProperty<T> intProperty, int i, TimeInterpolator timeInterpolator) {
        intProperty.setValue(t, i);
        return NO_OP;
    }

    public void addEndListener(Consumer<Boolean> consumer) {
        consumer.accept(true);
    }

    public AnimatorSet buildAnim() {
        return NO_OP;
    }
}
