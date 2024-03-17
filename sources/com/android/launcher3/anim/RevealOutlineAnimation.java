package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

public abstract class RevealOutlineAnimation extends ViewOutlineProvider {
    protected Rect mOutline = new Rect();
    protected float mOutlineRadius;

    /* access modifiers changed from: package-private */
    public abstract void setProgress(float f);

    /* access modifiers changed from: package-private */
    public abstract boolean shouldRemoveElevationDuringAnimation();

    public ValueAnimator createRevealAnimator(View view, boolean z) {
        return createRevealAnimator(view, z, 0.0f);
    }

    public ValueAnimator createRevealAnimator(final View view, boolean z, float f) {
        ValueAnimator valueAnimator;
        if (z) {
            valueAnimator = ValueAnimator.ofFloat(new float[]{1.0f - f, 0.0f});
        } else {
            valueAnimator = ValueAnimator.ofFloat(new float[]{f, 1.0f});
        }
        final float elevation = view.getElevation();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean mIsClippedToOutline;
            private ViewOutlineProvider mOldOutlineProvider;

            public void onAnimationStart(Animator animator) {
                this.mIsClippedToOutline = view.getClipToOutline();
                this.mOldOutlineProvider = view.getOutlineProvider();
                view.setOutlineProvider(RevealOutlineAnimation.this);
                view.setClipToOutline(true);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    view.setTranslationZ(-elevation);
                }
            }

            public void onAnimationEnd(Animator animator) {
                view.setOutlineProvider(this.mOldOutlineProvider);
                view.setClipToOutline(this.mIsClippedToOutline);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    view.setTranslationZ(0.0f);
                }
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(view) {
            public final /* synthetic */ View f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RevealOutlineAnimation.this.lambda$createRevealAnimator$0$RevealOutlineAnimation(this.f$1, valueAnimator);
            }
        });
        return valueAnimator;
    }

    public /* synthetic */ void lambda$createRevealAnimator$0$RevealOutlineAnimation(View view, ValueAnimator valueAnimator) {
        setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        view.invalidateOutline();
    }

    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(this.mOutline, this.mOutlineRadius);
    }

    public float getRadius() {
        return this.mOutlineRadius;
    }

    public void getOutline(Rect rect) {
        rect.set(this.mOutline);
    }
}
