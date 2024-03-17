package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.util.FloatProperty;
import com.android.launcher3.anim.AnimatorPlaybackController;
import java.util.ArrayList;

public class PendingAnimation extends AnimatedPropertySetter {
    private final ArrayList<AnimatorPlaybackController.Holder> mAnimHolders = new ArrayList<>();
    private final long mDuration;

    public PendingAnimation(long j) {
        this.mDuration = j;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void add(Animator animator, TimeInterpolator timeInterpolator, SpringProperty springProperty) {
        animator.setInterpolator(timeInterpolator);
        add(animator, springProperty);
    }

    public void add(Animator animator) {
        add(animator, SpringProperty.DEFAULT);
    }

    public void add(Animator animator, SpringProperty springProperty) {
        this.mAnim.play(animator.setDuration(this.mDuration));
        AnimatorPlaybackController.addAnimationHoldersRecur(animator, this.mDuration, springProperty, this.mAnimHolders);
    }

    public void setInterpolator(TimeInterpolator timeInterpolator) {
        this.mAnim.setInterpolator(timeInterpolator);
    }

    public <T> void addFloat(T t, FloatProperty<T> floatProperty, float f, float f2, TimeInterpolator timeInterpolator) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(t, floatProperty, new float[]{f, f2});
        ofFloat.setInterpolator(timeInterpolator);
        add(ofFloat);
    }

    public AnimatorSet buildAnim() {
        if (this.mAnimHolders.isEmpty()) {
            add(ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(this.mDuration));
        }
        return super.buildAnim();
    }

    public AnimatorPlaybackController createPlaybackController() {
        return new AnimatorPlaybackController(buildAnim(), this.mDuration, this.mAnimHolders);
    }
}
