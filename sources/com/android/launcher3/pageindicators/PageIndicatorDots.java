package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Themes;

public class PageIndicatorDots extends View implements PageIndicator {
    private static final long ANIMATION_DURATION = 150;
    private static final Property<PageIndicatorDots, Float> CURRENT_POSITION = new Property<PageIndicatorDots, Float>(Float.TYPE, "current_position") {
        public Float get(PageIndicatorDots pageIndicatorDots) {
            return Float.valueOf(pageIndicatorDots.mCurrentPosition);
        }

        public void set(PageIndicatorDots pageIndicatorDots, Float f) {
            float unused = pageIndicatorDots.mCurrentPosition = f.floatValue();
            pageIndicatorDots.invalidate();
            pageIndicatorDots.invalidateOutline();
        }
    };
    private static final int DOT_ACTIVE_ALPHA = 255;
    private static final int DOT_INACTIVE_ALPHA = 128;
    private static final int ENTER_ANIMATION_DURATION = 400;
    private static final float ENTER_ANIMATION_OVERSHOOT_TENSION = 4.9f;
    private static final int ENTER_ANIMATION_STAGGERED_DELAY = 150;
    private static final int ENTER_ANIMATION_START_DELAY = 300;
    private static final float SHIFT_PER_ANIMATION = 0.5f;
    private static final float SHIFT_THRESHOLD = 0.1f;
    private static final RectF sTempRect = new RectF();
    private int mActivePage;
    /* access modifiers changed from: private */
    public ObjectAnimator mAnimator;
    private final Paint mCirclePaint;
    /* access modifiers changed from: private */
    public float mCurrentPosition;
    /* access modifiers changed from: private */
    public final float mDotRadius;
    /* access modifiers changed from: private */
    public float[] mEntryAnimationRadiusFactors;
    /* access modifiers changed from: private */
    public float mFinalPosition;
    private final boolean mIsRtl;
    private int mNumPages;

    public PageIndicatorDots(Context context) {
        this(context, (AttributeSet) null);
    }

    public PageIndicatorDots(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PageIndicatorDots(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Paint paint = new Paint(1);
        this.mCirclePaint = paint;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Themes.getAttrColor(context, R.attr.folderPaginationColor));
        this.mDotRadius = getResources().getDimension(R.dimen.page_indicator_dot_size) / 2.0f;
        setOutlineProvider(new MyOutlineProver());
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    public void setScroll(int i, int i2) {
        int i3 = this.mNumPages;
        if (i3 > 1) {
            if (this.mIsRtl) {
                i = i2 - i;
            }
            int i4 = i2 / (i3 - 1);
            int i5 = i / i4;
            int i6 = i5 * i4;
            int i7 = i6 + i4;
            float f = ((float) i4) * 0.1f;
            float f2 = (float) i;
            if (f2 < ((float) i6) + f) {
                animateToPosition((float) i5);
            } else if (f2 > ((float) i7) - f) {
                animateToPosition((float) (i5 + 1));
            } else {
                animateToPosition(((float) i5) + 0.5f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateToPosition(float f) {
        this.mFinalPosition = f;
        if (Math.abs(this.mCurrentPosition - f) < 0.1f) {
            this.mCurrentPosition = this.mFinalPosition;
        }
        if (this.mAnimator == null && Float.compare(this.mCurrentPosition, this.mFinalPosition) != 0) {
            float f2 = this.mCurrentPosition;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, CURRENT_POSITION, new float[]{f2 > this.mFinalPosition ? f2 - 0.5f : f2 + 0.5f});
            this.mAnimator = ofFloat;
            ofFloat.addListener(new AnimationCycleListener());
            this.mAnimator.setDuration(150);
            this.mAnimator.start();
        }
    }

    public void stopAllAnimations() {
        ObjectAnimator objectAnimator = this.mAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mAnimator = null;
        }
        float f = (float) this.mActivePage;
        this.mFinalPosition = f;
        CURRENT_POSITION.set(this, Float.valueOf(f));
    }

    public void prepareEntryAnimation() {
        this.mEntryAnimationRadiusFactors = new float[this.mNumPages];
        invalidate();
    }

    public void playEntryAnimation() {
        int length = this.mEntryAnimationRadiusFactors.length;
        if (length == 0) {
            this.mEntryAnimationRadiusFactors = null;
            invalidate();
            return;
        }
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(ENTER_ANIMATION_OVERSHOOT_TENSION);
        AnimatorSet animatorSet = new AnimatorSet();
        for (final int i = 0; i < length; i++) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(400);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PageIndicatorDots.this.mEntryAnimationRadiusFactors[i] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PageIndicatorDots.this.invalidate();
                }
            });
            duration.setInterpolator(overshootInterpolator);
            duration.setStartDelay((long) ((i * 150) + 300));
            animatorSet.play(duration);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                float[] unused = PageIndicatorDots.this.mEntryAnimationRadiusFactors = null;
                PageIndicatorDots.this.invalidateOutline();
                PageIndicatorDots.this.invalidate();
            }
        });
        animatorSet.start();
    }

    public void setActiveMarker(int i) {
        if (this.mActivePage != i) {
            this.mActivePage = i;
        }
    }

    public void setMarkersCount(int i) {
        this.mNumPages = i;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getMode(i) == 1073741824 ? View.MeasureSpec.getSize(i) : (int) (((float) ((this.mNumPages * 3) + 2)) * this.mDotRadius), View.MeasureSpec.getMode(i2) == 1073741824 ? View.MeasureSpec.getSize(i2) : (int) (this.mDotRadius * 4.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = this.mDotRadius * 3.0f;
        float width = ((float) getWidth()) - (((float) this.mNumPages) * f);
        float f2 = this.mDotRadius;
        float f3 = ((width + f2) / 2.0f) + f2;
        float height = (float) (getHeight() / 2);
        int i = 0;
        if (this.mEntryAnimationRadiusFactors != null) {
            if (this.mIsRtl) {
                f3 = ((float) getWidth()) - f3;
                f = -f;
            }
            while (i < this.mEntryAnimationRadiusFactors.length) {
                this.mCirclePaint.setAlpha(i == this.mActivePage ? 255 : 128);
                canvas.drawCircle(f3, height, this.mDotRadius * this.mEntryAnimationRadiusFactors[i], this.mCirclePaint);
                f3 += f;
                i++;
            }
            return;
        }
        this.mCirclePaint.setAlpha(128);
        while (i < this.mNumPages) {
            canvas.drawCircle(f3, height, this.mDotRadius, this.mCirclePaint);
            f3 += f;
            i++;
        }
        this.mCirclePaint.setAlpha(255);
        RectF activeRect = getActiveRect();
        float f4 = this.mDotRadius;
        canvas.drawRoundRect(activeRect, f4, f4, this.mCirclePaint);
    }

    /* access modifiers changed from: private */
    public RectF getActiveRect() {
        float f = this.mCurrentPosition;
        float f2 = (float) ((int) f);
        float f3 = f - f2;
        float f4 = this.mDotRadius;
        float f5 = f4 * 2.0f;
        float f6 = f4 * 3.0f;
        RectF rectF = sTempRect;
        rectF.top = (((float) getHeight()) * 0.5f) - this.mDotRadius;
        rectF.bottom = (((float) getHeight()) * 0.5f) + this.mDotRadius;
        rectF.left = (((((float) getWidth()) - (((float) this.mNumPages) * f6)) + this.mDotRadius) / 2.0f) + (f2 * f6);
        rectF.right = rectF.left + f5;
        if (f3 < 0.5f) {
            rectF.right += f3 * f6 * 2.0f;
        } else {
            rectF.right += f6;
            rectF.left += (f3 - 0.5f) * f6 * 2.0f;
        }
        if (this.mIsRtl) {
            float width = rectF.width();
            rectF.right = ((float) getWidth()) - rectF.left;
            rectF.left = rectF.right - width;
        }
        return rectF;
    }

    private class MyOutlineProver extends ViewOutlineProvider {
        private MyOutlineProver() {
        }

        public void getOutline(View view, Outline outline) {
            if (PageIndicatorDots.this.mEntryAnimationRadiusFactors == null) {
                RectF access$400 = PageIndicatorDots.this.getActiveRect();
                outline.setRoundRect((int) access$400.left, (int) access$400.top, (int) access$400.right, (int) access$400.bottom, PageIndicatorDots.this.mDotRadius);
            }
        }
    }

    private class AnimationCycleListener extends AnimatorListenerAdapter {
        private boolean mCancelled;

        private AnimationCycleListener() {
            this.mCancelled = false;
        }

        public void onAnimationCancel(Animator animator) {
            this.mCancelled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCancelled) {
                ObjectAnimator unused = PageIndicatorDots.this.mAnimator = null;
                PageIndicatorDots pageIndicatorDots = PageIndicatorDots.this;
                pageIndicatorDots.animateToPosition(pageIndicatorDots.mFinalPosition);
            }
        }
    }
}
