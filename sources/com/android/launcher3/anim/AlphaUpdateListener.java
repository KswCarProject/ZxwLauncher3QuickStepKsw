package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class AlphaUpdateListener extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
    public static final float ALPHA_CUTOFF_THRESHOLD = 0.01f;
    private View mView;

    public AlphaUpdateListener(View view) {
        this.mView = view;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        updateVisibility(this.mView);
    }

    public void onAnimationEnd(Animator animator) {
        updateVisibility(this.mView);
    }

    public void onAnimationStart(Animator animator) {
        this.mView.setVisibility(0);
    }

    public static void updateVisibility(View view) {
        if (view.getAlpha() < 0.01f && view.getVisibility() != 4) {
            view.setVisibility(4);
        } else if (view.getAlpha() > 0.01f && view.getVisibility() != 0) {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int descendantFocusability = viewGroup.getDescendantFocusability();
                viewGroup.setDescendantFocusability(393216);
                viewGroup.setVisibility(0);
                viewGroup.setDescendantFocusability(descendantFocusability);
                return;
            }
            view.setVisibility(0);
        }
    }
}
