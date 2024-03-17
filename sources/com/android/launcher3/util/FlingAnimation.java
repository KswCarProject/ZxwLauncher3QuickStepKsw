package com.android.launcher3.util;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;

public class FlingAnimation implements ValueAnimator.AnimatorUpdateListener, Runnable {
    private static final int DRAG_END_DELAY = 300;
    private static final float MAX_ACCELERATION = 0.5f;
    protected float mAX;
    protected float mAY;
    protected final TimeInterpolator mAlphaInterpolator = new DecelerateInterpolator(0.75f);
    protected float mAnimationTimeFraction;
    protected final DragLayer mDragLayer;
    protected final DropTarget.DragObject mDragObject;
    protected final DragOptions mDragOptions;
    private final ButtonDropTarget mDropTarget;
    protected int mDuration;
    protected RectF mFrom;
    protected Rect mIconRect;
    private final Launcher mLauncher;
    protected final float mUX;
    protected final float mUY;

    public FlingAnimation(DropTarget.DragObject dragObject, PointF pointF, ButtonDropTarget buttonDropTarget, Launcher launcher, DragOptions dragOptions) {
        this.mDropTarget = buttonDropTarget;
        this.mLauncher = launcher;
        this.mDragObject = dragObject;
        this.mUX = pointF.x / 1000.0f;
        this.mUY = pointF.y / 1000.0f;
        this.mDragLayer = launcher.getDragLayer();
        this.mDragOptions = dragOptions;
    }

    public void run() {
        this.mIconRect = this.mDropTarget.getIconRect(this.mDragObject);
        this.mDragObject.dragView.cancelAnimation();
        this.mDragObject.dragView.requestLayout();
        Rect rect = new Rect();
        this.mDragLayer.getViewRectRelativeToSelf(this.mDragObject.dragView, rect);
        RectF rectF = new RectF(rect);
        this.mFrom = rectF;
        rectF.inset(((1.0f - this.mDragObject.dragView.getScaleX()) * ((float) rect.width())) / 2.0f, ((1.0f - this.mDragObject.dragView.getScaleY()) * ((float) rect.height())) / 2.0f);
        int initFlingUpDuration = Math.abs(this.mUY) > Math.abs(this.mUX) ? initFlingUpDuration() : initFlingLeftDuration();
        this.mDuration = initFlingUpDuration;
        this.mAnimationTimeFraction = ((float) initFlingUpDuration) / ((float) (initFlingUpDuration + 300));
        final int i = initFlingUpDuration + 300;
        final long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        AnonymousClass1 r3 = new TimeInterpolator() {
            private int mCount = -1;
            private float mOffset = 0.0f;

            public float getInterpolation(float f) {
                int i = this.mCount;
                if (i < 0) {
                    this.mCount = i + 1;
                } else if (i == 0) {
                    this.mOffset = Math.min(0.5f, ((float) (AnimationUtils.currentAnimationTimeMillis() - currentAnimationTimeMillis)) / ((float) i));
                    this.mCount++;
                }
                return Math.min(1.0f, this.mOffset + f);
            }
        };
        this.mDropTarget.onDrop(this.mDragObject, this.mDragOptions);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration((long) i).setInterpolator(r3);
        ofFloat.addUpdateListener(this);
        ofFloat.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
            public final void run() {
                FlingAnimation.this.lambda$run$0$FlingAnimation();
            }
        }));
        this.mDragLayer.playDropAnimation(this.mDragObject.dragView, ofFloat, 0);
    }

    public /* synthetic */ void lambda$run$0$FlingAnimation() {
        this.mLauncher.getStateManager().goToState(LauncherState.NORMAL);
        this.mDropTarget.completeDrop(this.mDragObject);
    }

    /* access modifiers changed from: protected */
    public int initFlingUpDuration() {
        float f = -this.mFrom.bottom;
        float f2 = this.mUY;
        float f3 = (f2 * f2) + (f * 2.0f * 0.5f);
        if (f3 >= 0.0f) {
            this.mAY = 0.5f;
        } else {
            this.mAY = (f2 * f2) / ((-f) * 2.0f);
            f3 = 0.0f;
        }
        double sqrt = (((double) (-f2)) - Math.sqrt((double) f3)) / ((double) this.mAY);
        this.mAX = (float) (((((double) ((-this.mFrom.centerX()) + this.mIconRect.exactCenterX())) - (((double) this.mUX) * sqrt)) * 2.0d) / (sqrt * sqrt));
        return (int) Math.round(sqrt);
    }

    /* access modifiers changed from: protected */
    public int initFlingLeftDuration() {
        float f = -this.mFrom.right;
        float f2 = this.mUX;
        float f3 = (f2 * f2) + (f * 2.0f * 0.5f);
        if (f3 >= 0.0f) {
            this.mAX = 0.5f;
        } else {
            this.mAX = (f2 * f2) / ((-f) * 2.0f);
            f3 = 0.0f;
        }
        double sqrt = (((double) (-f2)) - Math.sqrt((double) f3)) / ((double) this.mAX);
        this.mAY = (float) (((((double) ((-this.mFrom.centerY()) + this.mIconRect.exactCenterY())) - (((double) this.mUY) * sqrt)) * 2.0d) / (sqrt * sqrt));
        return (int) Math.round(sqrt);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        float f = this.mAnimationTimeFraction;
        float f2 = animatedFraction > f ? 1.0f : animatedFraction / f;
        DragView dragView = (DragView) this.mDragLayer.getAnimatedView();
        float f3 = ((float) this.mDuration) * f2;
        dragView.setTranslationX((this.mUX * f3) + this.mFrom.left + (((this.mAX * f3) * f3) / 2.0f));
        dragView.setTranslationY((this.mUY * f3) + this.mFrom.top + (((this.mAY * f3) * f3) / 2.0f));
        dragView.setAlpha(1.0f - this.mAlphaInterpolator.getInterpolation(f2));
    }
}
