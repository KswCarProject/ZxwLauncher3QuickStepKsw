package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.ViewOverlay;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.launcher3.R;
import java.util.ArrayList;

public class AnimatedTaskView extends ConstraintLayout {
    private CardView mBottomTaskView;
    /* access modifiers changed from: private */
    public View mFullTaskView;
    /* access modifiers changed from: private */
    public float mTaskViewAnimatedRadius;
    /* access modifiers changed from: private */
    public final Rect mTaskViewAnimatedRect = new Rect();
    /* access modifiers changed from: private */
    public ViewOutlineProvider mTaskViewOutlineProvider = null;
    private CardView mTopTaskView;

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public AnimatedTaskView(Context context) {
        super(context);
    }

    public AnimatedTaskView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AnimatedTaskView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public AnimatedTaskView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mFullTaskView = findViewById(R.id.full_task_view);
        this.mTopTaskView = (CardView) findViewById(R.id.top_task_view);
        this.mBottomTaskView = (CardView) findViewById(R.id.bottom_task_view);
        setToSingleRowLayout(false);
    }

    /* access modifiers changed from: package-private */
    public AnimatorSet createAnimationToMultiRowLayout() {
        if (this.mTaskViewOutlineProvider == null) {
            return null;
        }
        Outline outline = new Outline();
        this.mTaskViewOutlineProvider.getOutline(this, outline);
        final Rect rect = new Rect();
        outline.getRect(rect);
        int height = this.mTopTaskView.getHeight();
        final float radius = outline.getRadius();
        float dimensionPixelSize = (float) getContext().getResources().getDimensionPixelSize(R.dimen.gesture_tutorial_small_task_view_corner_radius);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(rect, height, radius, dimensionPixelSize) {
            public final /* synthetic */ Rect f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ float f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AnimatedTaskView.this.lambda$createAnimationToMultiRowLayout$0$AnimatedTaskView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                AnimatedTaskView.this.mTaskViewAnimatedRect.set(rect);
                float unused = AnimatedTaskView.this.mTaskViewAnimatedRadius = radius;
                AnimatedTaskView.this.mFullTaskView.setClipToOutline(true);
                AnimatedTaskView.this.mFullTaskView.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setRoundRect(AnimatedTaskView.this.mTaskViewAnimatedRect, AnimatedTaskView.this.mTaskViewAnimatedRadius);
                    }
                });
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AnimatedTaskView.this.mFullTaskView.setOutlineProvider(AnimatedTaskView.this.mTaskViewOutlineProvider);
            }

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                AnimatedTaskView.this.mFullTaskView.setOutlineProvider(AnimatedTaskView.this.mTaskViewOutlineProvider);
            }
        });
        ArrayList arrayList = new ArrayList();
        arrayList.add(ObjectAnimator.ofFloat(this.mBottomTaskView, View.TRANSLATION_X, new float[]{(float) (-this.mBottomTaskView.getWidth()), 0.0f}));
        arrayList.add(ofFloat);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                AnimatedTaskView.this.setToSingleRowLayout(true);
                AnimatedTaskView.this.setPadding(0, rect.top, 0, AnimatedTaskView.this.getHeight() - rect.bottom);
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AnimatedTaskView.this.setToMultiRowLayout();
            }

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                AnimatedTaskView.this.setToMultiRowLayout();
            }
        });
        return animatorSet;
    }

    public /* synthetic */ void lambda$createAnimationToMultiRowLayout$0$AnimatedTaskView(Rect rect, int i, float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.mTaskViewAnimatedRect.bottom = (int) (((float) rect.bottom) + (((float) (i - rect.bottom)) * floatValue));
        this.mTaskViewAnimatedRadius = f + (floatValue * (f2 - f));
        this.mFullTaskView.invalidateOutline();
    }

    /* access modifiers changed from: package-private */
    public void setToSingleRowLayout(boolean z) {
        int i = 0;
        this.mFullTaskView.setVisibility(0);
        this.mTopTaskView.setVisibility(4);
        CardView cardView = this.mBottomTaskView;
        if (!z) {
            i = 4;
        }
        cardView.setVisibility(i);
    }

    /* access modifiers changed from: package-private */
    public void setToMultiRowLayout() {
        this.mFullTaskView.setVisibility(4);
        this.mTopTaskView.setVisibility(0);
        this.mBottomTaskView.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void setFakeTaskViewFillColor(int i) {
        this.mFullTaskView.setBackgroundColor(i);
        this.mTopTaskView.setCardBackgroundColor(i);
        this.mBottomTaskView.setCardBackgroundColor(i);
    }

    public void setClipToOutline(boolean z) {
        this.mFullTaskView.setClipToOutline(z);
    }

    public void setOutlineProvider(ViewOutlineProvider viewOutlineProvider) {
        this.mTaskViewOutlineProvider = viewOutlineProvider;
        this.mFullTaskView.setOutlineProvider(viewOutlineProvider);
    }
}
