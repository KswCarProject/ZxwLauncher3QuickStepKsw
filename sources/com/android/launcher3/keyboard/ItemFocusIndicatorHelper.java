package com.android.launcher3.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.FloatProperty;
import android.view.View;
import androidx.core.view.ViewCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.Themes;

public abstract class ItemFocusIndicatorHelper<T> implements ValueAnimator.AnimatorUpdateListener {
    public static final FloatProperty<ItemFocusIndicatorHelper> ALPHA = new FloatProperty<ItemFocusIndicatorHelper>("alpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(ItemFocusIndicatorHelper itemFocusIndicatorHelper, float f) {
            itemFocusIndicatorHelper.setAlpha(f);
        }

        public Float get(ItemFocusIndicatorHelper itemFocusIndicatorHelper) {
            return Float.valueOf(itemFocusIndicatorHelper.mAlpha);
        }
    };
    private static final long ANIM_DURATION = 150;
    private static final float MIN_VISIBLE_ALPHA = 0.2f;
    private static final RectEvaluator RECT_EVALUATOR = new RectEvaluator(new Rect());
    public static final FloatProperty<ItemFocusIndicatorHelper> SHIFT = new FloatProperty<ItemFocusIndicatorHelper>("shift") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(ItemFocusIndicatorHelper itemFocusIndicatorHelper, float f) {
            float unused = itemFocusIndicatorHelper.mShift = f;
        }

        public Float get(ItemFocusIndicatorHelper itemFocusIndicatorHelper) {
            return Float.valueOf(itemFocusIndicatorHelper.mShift);
        }
    };
    private static final Rect sTempRect1 = new Rect();
    private static final Rect sTempRect2 = new Rect();
    /* access modifiers changed from: private */
    public float mAlpha;
    private final View mContainer;
    private ObjectAnimator mCurrentAnimation;
    private T mCurrentItem;
    private final Rect mDirtyRect = new Rect();
    private boolean mIsDirty = false;
    private T mLastFocusedItem;
    private final int mMaxAlpha;
    protected final Paint mPaint;
    private float mRadius;
    /* access modifiers changed from: private */
    public float mShift;
    private T mTargetItem;

    /* access modifiers changed from: protected */
    public boolean shouldDraw(T t) {
        return true;
    }

    public abstract void viewToRect(T t, Rect rect);

    public ItemFocusIndicatorHelper(View view, int i) {
        this.mContainer = view;
        Paint paint = new Paint(1);
        this.mPaint = paint;
        this.mMaxAlpha = Color.alpha(i);
        paint.setColor(i | ViewCompat.MEASURED_STATE_MASK);
        setAlpha(0.0f);
        this.mShift = 0.0f;
        if (FeatureFlags.ENABLE_DEVICE_SEARCH.get()) {
            this.mRadius = Themes.getDialogCornerRadius(view.getContext());
        }
    }

    /* access modifiers changed from: protected */
    public void setAlpha(float f) {
        this.mAlpha = f;
        this.mPaint.setAlpha((int) (f * ((float) this.mMaxAlpha)));
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidateDirty();
    }

    /* access modifiers changed from: protected */
    public void invalidateDirty() {
        if (this.mIsDirty) {
            this.mContainer.invalidate(this.mDirtyRect);
            this.mIsDirty = false;
        }
        Rect drawRect = getDrawRect();
        if (drawRect != null) {
            this.mContainer.invalidate(drawRect);
        }
    }

    public void draw(Canvas canvas) {
        Rect drawRect;
        if (this.mAlpha > 0.0f && (drawRect = getDrawRect()) != null) {
            this.mDirtyRect.set(drawRect);
            float f = this.mRadius;
            canvas.drawRoundRect((float) this.mDirtyRect.left, (float) this.mDirtyRect.top, (float) this.mDirtyRect.right, (float) this.mDirtyRect.bottom, f, f, this.mPaint);
            this.mIsDirty = true;
        }
    }

    private Rect getDrawRect() {
        T t;
        T t2 = this.mCurrentItem;
        if (t2 == null || !shouldDraw(t2)) {
            return null;
        }
        T t3 = this.mCurrentItem;
        Rect rect = sTempRect1;
        viewToRect(t3, rect);
        if (this.mShift <= 0.0f || (t = this.mTargetItem) == null) {
            return rect;
        }
        Rect rect2 = sTempRect2;
        viewToRect(t, rect2);
        return RECT_EVALUATOR.evaluate(this.mShift, rect, rect2);
    }

    /* access modifiers changed from: protected */
    public void changeFocus(T t, boolean z) {
        if (z) {
            endCurrentAnimation();
            if (this.mAlpha > 0.2f) {
                this.mTargetItem = t;
                ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{1.0f}), PropertyValuesHolder.ofFloat(SHIFT, new float[]{1.0f})});
                this.mCurrentAnimation = ofPropertyValuesHolder;
                ofPropertyValuesHolder.addListener(new ViewSetListener(t, true));
            } else {
                setCurrentItem(t);
                this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{1.0f})});
            }
            this.mLastFocusedItem = t;
        } else if (this.mLastFocusedItem == t) {
            this.mLastFocusedItem = null;
            endCurrentAnimation();
            ObjectAnimator ofPropertyValuesHolder2 = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{0.0f})});
            this.mCurrentAnimation = ofPropertyValuesHolder2;
            ofPropertyValuesHolder2.addListener(new ViewSetListener(null, false));
        }
        invalidateDirty();
        if (!z) {
            t = null;
        }
        this.mLastFocusedItem = t;
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.addUpdateListener(this);
            this.mCurrentAnimation.setDuration(150).start();
        }
    }

    /* access modifiers changed from: protected */
    public void endCurrentAnimation() {
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mCurrentAnimation = null;
        }
    }

    /* access modifiers changed from: protected */
    public void setCurrentItem(T t) {
        this.mCurrentItem = t;
        this.mShift = 0.0f;
        this.mTargetItem = null;
    }

    private class ViewSetListener extends AnimatorListenerAdapter {
        private final boolean mCallOnCancel;
        private boolean mCalled = false;
        private final T mItemToSet;

        ViewSetListener(T t, boolean z) {
            this.mItemToSet = t;
            this.mCallOnCancel = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (!this.mCallOnCancel) {
                this.mCalled = true;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCalled) {
                ItemFocusIndicatorHelper.this.setCurrentItem(this.mItemToSet);
                this.mCalled = true;
            }
        }
    }
}
