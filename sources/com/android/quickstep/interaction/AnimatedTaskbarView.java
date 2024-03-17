package com.android.quickstep.interaction;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.launcher3.R;
import java.util.ArrayList;

public class AnimatedTaskbarView extends ConstraintLayout {
    private View mBackground;
    private View mIcon1;
    private View mIcon2;
    private View mIcon3;
    private View mIcon4;
    private View mIcon5;
    private View mIcon6;
    private View mIconContainer;
    /* access modifiers changed from: private */
    public Animator mRunningAnimator;

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public AnimatedTaskbarView(Context context) {
        super(context);
    }

    public AnimatedTaskbarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AnimatedTaskbarView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public AnimatedTaskbarView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mBackground = findViewById(R.id.taskbar_background);
        this.mIconContainer = findViewById(R.id.icon_container);
        this.mIcon1 = findViewById(R.id.taskbar_icon_1);
        this.mIcon2 = findViewById(R.id.taskbar_icon_2);
        this.mIcon3 = findViewById(R.id.taskbar_icon_3);
        this.mIcon4 = findViewById(R.id.taskbar_icon_4);
        this.mIcon5 = findViewById(R.id.taskbar_icon_5);
        this.mIcon6 = findViewById(R.id.taskbar_icon_6);
    }

    public void animateDisappearanceToHotseat(ViewGroup viewGroup) {
        ArrayList arrayList = new ArrayList();
        int top = viewGroup.getTop();
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.TRANSLATION_Y, new float[]{0.0f, (float) this.mBackground.getHeight()}));
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.ALPHA, new float[]{1.0f, 0.0f}));
        arrayList.add(createIconDisappearanceToHotseatAnimator(this.mIcon1, viewGroup.findViewById(R.id.hotseat_icon_1), top));
        arrayList.add(createIconDisappearanceToHotseatAnimator(this.mIcon2, viewGroup.findViewById(R.id.hotseat_icon_2), top));
        arrayList.add(createIconDisappearanceToHotseatAnimator(this.mIcon3, viewGroup.findViewById(R.id.hotseat_icon_3), top));
        arrayList.add(createIconDisappearanceToHotseatAnimator(this.mIcon4, viewGroup.findViewById(R.id.hotseat_icon_4), top));
        arrayList.add(createIconDisappearanceToHotseatAnimator(this.mIcon5, viewGroup.findViewById(R.id.hotseat_icon_5), top));
        arrayList.add(createIconDisappearanceToHotseatAnimator(this.mIcon6, viewGroup.findViewById(R.id.hotseat_icon_6), top));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AnimatedTaskbarView.this.setVisibility(4);
            }

            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                AnimatedTaskbarView.this.setVisibility(0);
            }
        });
        start(animatorSet);
    }

    public void animateAppearanceFromHotseat(ViewGroup viewGroup) {
        ArrayList arrayList = new ArrayList();
        int top = viewGroup.getTop();
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.TRANSLATION_Y, new float[]{(float) this.mBackground.getHeight(), 0.0f}));
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.ALPHA, new float[]{0.0f, 1.0f}));
        arrayList.add(createIconAppearanceFromHotseatAnimator(this.mIcon1, viewGroup.findViewById(R.id.hotseat_icon_1), top));
        arrayList.add(createIconAppearanceFromHotseatAnimator(this.mIcon2, viewGroup.findViewById(R.id.hotseat_icon_2), top));
        arrayList.add(createIconAppearanceFromHotseatAnimator(this.mIcon3, viewGroup.findViewById(R.id.hotseat_icon_3), top));
        arrayList.add(createIconAppearanceFromHotseatAnimator(this.mIcon4, viewGroup.findViewById(R.id.hotseat_icon_4), top));
        arrayList.add(createIconAppearanceFromHotseatAnimator(this.mIcon5, viewGroup.findViewById(R.id.hotseat_icon_5), top));
        arrayList.add(createIconAppearanceFromHotseatAnimator(this.mIcon6, viewGroup.findViewById(R.id.hotseat_icon_6), top));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                AnimatedTaskbarView.this.setVisibility(0);
            }
        });
        start(animatorSet);
    }

    public void animateDisappearanceToBottom() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.TRANSLATION_Y, new float[]{0.0f, (float) this.mBackground.getHeight()}));
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.ALPHA, new float[]{1.0f, 0.0f}));
        arrayList.add(ObjectAnimator.ofFloat(this.mIconContainer, View.SCALE_X, new float[]{1.0f, 0.0f}));
        arrayList.add(ObjectAnimator.ofFloat(this.mIconContainer, View.SCALE_Y, new float[]{1.0f, 0.0f}));
        initializeIconContainerPivot();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AnimatedTaskbarView.this.setVisibility(4);
                AnimatedTaskbarView.this.resetIconContainerPivot();
            }

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                AnimatedTaskbarView.this.resetIconContainerPivot();
            }

            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                AnimatedTaskbarView.this.setVisibility(0);
            }
        });
        start(animatorSet);
    }

    public void animateAppearanceFromBottom() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.TRANSLATION_Y, new float[]{(float) this.mBackground.getHeight(), 0.0f}));
        arrayList.add(ObjectAnimator.ofFloat(this.mBackground, View.ALPHA, new float[]{0.0f, 1.0f}));
        arrayList.add(ObjectAnimator.ofFloat(this.mIconContainer, View.SCALE_X, new float[]{0.0f, 1.0f}));
        arrayList.add(ObjectAnimator.ofFloat(this.mIconContainer, View.SCALE_Y, new float[]{0.0f, 1.0f}));
        initializeIconContainerPivot();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                AnimatedTaskbarView.this.setVisibility(0);
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AnimatedTaskbarView.this.resetIconContainerPivot();
            }

            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                AnimatedTaskbarView.this.resetIconContainerPivot();
            }
        });
        start(animatorSet);
    }

    private void initializeIconContainerPivot() {
        this.mIconContainer.setPivotX(((float) getWidth()) / 2.0f);
        this.mIconContainer.setPivotY(((float) getHeight()) * 0.8f);
    }

    /* access modifiers changed from: private */
    public void resetIconContainerPivot() {
        this.mIconContainer.resetPivot();
        this.mIconContainer.setScaleX(1.0f);
        this.mIconContainer.setScaleY(1.0f);
    }

    private void start(final Animator animator) {
        Animator animator2 = this.mRunningAnimator;
        if (animator2 != null) {
            animator2.cancel();
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animator) {
                super.onAnimationCancel(animator);
                Animator unused = AnimatedTaskbarView.this.mRunningAnimator = null;
            }

            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                Animator unused = AnimatedTaskbarView.this.mRunningAnimator = null;
            }

            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                Animator unused = AnimatedTaskbarView.this.mRunningAnimator = animator;
            }
        });
        animator.start();
    }

    private Animator createIconDisappearanceToHotseatAnimator(final View view, View view2, int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f, (float) ((i + view2.getTop()) - (getTop() + view.getTop()))}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, new float[]{0.0f, (float) (view2.getLeft() - view.getLeft())}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.0f, ((float) view2.getWidth()) / ((float) view.getWidth())}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.0f, ((float) view2.getHeight()) / ((float) view.getHeight())}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                view.setVisibility(4);
            }

            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                view.setVisibility(0);
            }
        });
        return animatorSet;
    }

    private Animator createIconAppearanceFromHotseatAnimator(final View view, View view2, int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{(float) ((i + view2.getTop()) - (getTop() + view.getTop())), 0.0f}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_X, new float[]{(float) (view2.getLeft() - view.getLeft()), 0.0f}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{((float) view2.getWidth()) / ((float) view.getWidth()), 1.0f}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{((float) view2.getHeight()) / ((float) view.getHeight()), 1.0f}));
        arrayList.add(ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f}));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                view.setVisibility(0);
            }
        });
        return animatorSet;
    }
}
