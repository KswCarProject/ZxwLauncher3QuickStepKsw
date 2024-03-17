package com.android.launcher3.icons;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.FloatProperty;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.core.graphics.ColorUtils;
import kotlinx.coroutines.scheduling.WorkQueueKt;

public class FastBitmapDrawable extends Drawable implements Drawable.Callback {
    private static final Interpolator ACCEL = new AccelerateInterpolator();
    public static final int CLICK_FEEDBACK_DURATION = 200;
    private static final Interpolator DEACCEL = new DecelerateInterpolator();
    private static final float DISABLED_BRIGHTNESS = 0.5f;
    private static final float DISABLED_DESATURATION = 1.0f;
    protected static final int FULLY_OPAQUE = 255;
    private static final float PRESSED_SCALE = 1.1f;
    private static final FloatProperty<FastBitmapDrawable> SCALE = new FloatProperty<FastBitmapDrawable>("scale") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public Float get(FastBitmapDrawable fastBitmapDrawable) {
            return Float.valueOf(fastBitmapDrawable.mScale);
        }

        public void setValue(FastBitmapDrawable fastBitmapDrawable, float f) {
            float unused = fastBitmapDrawable.mScale = f;
            fastBitmapDrawable.invalidateSelf();
        }
    };
    public static final int WHITE_SCRIM_ALPHA = 138;
    private int mAlpha;
    private Drawable mBadge;
    protected final Bitmap mBitmap;
    private ColorFilter mColorFilter;
    float mDisabledAlpha;
    protected final int mIconColor;
    protected boolean mIsDisabled;
    private boolean mIsPressed;
    protected final Paint mPaint;
    /* access modifiers changed from: private */
    public float mScale;
    private ObjectAnimator mScaleAnimation;

    public int getOpacity() {
        return -3;
    }

    public boolean isStateful() {
        return true;
    }

    public boolean isThemed() {
        return false;
    }

    public FastBitmapDrawable(Bitmap bitmap) {
        this(bitmap, 0);
    }

    public FastBitmapDrawable(BitmapInfo bitmapInfo) {
        this(bitmapInfo.icon, bitmapInfo.color);
    }

    protected FastBitmapDrawable(Bitmap bitmap, int i) {
        this.mPaint = new Paint(3);
        this.mDisabledAlpha = 1.0f;
        this.mScale = 1.0f;
        this.mAlpha = 255;
        this.mBitmap = bitmap;
        this.mIconColor = i;
        setFilterBitmap(true);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        updateBadgeBounds(rect);
    }

    private void updateBadgeBounds(Rect rect) {
        Drawable drawable = this.mBadge;
        if (drawable != null) {
            setBadgeBounds(drawable, rect);
        }
    }

    public final void draw(Canvas canvas) {
        if (this.mScale != 1.0f) {
            int save = canvas.save();
            Rect bounds = getBounds();
            float f = this.mScale;
            canvas.scale(f, f, bounds.exactCenterX(), bounds.exactCenterY());
            drawInternal(canvas, bounds);
            Drawable drawable = this.mBadge;
            if (drawable != null) {
                drawable.draw(canvas);
            }
            canvas.restoreToCount(save);
            return;
        }
        drawInternal(canvas, getBounds());
        Drawable drawable2 = this.mBadge;
        if (drawable2 != null) {
            drawable2.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void drawInternal(Canvas canvas, Rect rect) {
        canvas.drawBitmap(this.mBitmap, (Rect) null, rect, this.mPaint);
    }

    public int getIconColor() {
        return ColorUtils.compositeColors(GraphicsUtils.setColorAlphaBound(-1, WHITE_SCRIM_ALPHA), this.mIconColor);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        updateFilter();
    }

    public void setAlpha(int i) {
        if (this.mAlpha != i) {
            this.mAlpha = i;
            this.mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    public void setFilterBitmap(boolean z) {
        this.mPaint.setFilterBitmap(z);
        this.mPaint.setAntiAlias(z);
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void resetScale() {
        ObjectAnimator objectAnimator = this.mScaleAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mScaleAnimation = null;
        }
        this.mScale = 1.0f;
        invalidateSelf();
    }

    public float getAnimatedScale() {
        if (this.mScaleAnimation == null) {
            return 1.0f;
        }
        return this.mScale;
    }

    public int getIntrinsicWidth() {
        return this.mBitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        return this.mBitmap.getHeight();
    }

    public int getMinimumWidth() {
        return getBounds().width();
    }

    public int getMinimumHeight() {
        return getBounds().height();
    }

    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        boolean z;
        int length = iArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            } else if (iArr[i] == 16842919) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (this.mIsPressed == z) {
            return false;
        }
        this.mIsPressed = z;
        ObjectAnimator objectAnimator = this.mScaleAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mScaleAnimation = null;
        }
        if (this.mIsPressed) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, SCALE, new float[]{1.1f});
            this.mScaleAnimation = ofFloat;
            ofFloat.setDuration(200);
            this.mScaleAnimation.setInterpolator(ACCEL);
            this.mScaleAnimation.start();
        } else if (isVisible()) {
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, SCALE, new float[]{1.0f});
            this.mScaleAnimation = ofFloat2;
            ofFloat2.setDuration(200);
            this.mScaleAnimation.setInterpolator(DEACCEL);
            this.mScaleAnimation.start();
        } else {
            this.mScale = 1.0f;
            invalidateSelf();
        }
        return true;
    }

    public void setIsDisabled(boolean z) {
        if (this.mIsDisabled != z) {
            this.mIsDisabled = z;
            updateFilter();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isDisabled() {
        return this.mIsDisabled;
    }

    public void setBadge(Drawable drawable) {
        Drawable drawable2 = this.mBadge;
        if (drawable2 != null) {
            drawable2.setCallback((Drawable.Callback) null);
        }
        this.mBadge = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        updateBadgeBounds(getBounds());
        updateFilter();
    }

    /* access modifiers changed from: protected */
    public void updateFilter() {
        this.mPaint.setColorFilter(this.mIsDisabled ? getDisabledColorFilter(this.mDisabledAlpha) : this.mColorFilter);
        Drawable drawable = this.mBadge;
        if (drawable != null) {
            drawable.setColorFilter(getColorFilter());
        }
        invalidateSelf();
    }

    /* access modifiers changed from: protected */
    public FastBitmapConstantState newConstantState() {
        return new FastBitmapConstantState(this.mBitmap, this.mIconColor);
    }

    public final Drawable.ConstantState getConstantState() {
        FastBitmapConstantState newConstantState = newConstantState();
        newConstantState.mIsDisabled = this.mIsDisabled;
        Drawable drawable = this.mBadge;
        if (drawable != null) {
            Drawable.ConstantState unused = newConstantState.mBadgeConstantState = drawable.getConstantState();
        }
        return newConstantState;
    }

    public static ColorFilter getDisabledColorFilter() {
        return getDisabledColorFilter(1.0f);
    }

    private static ColorFilter getDisabledColorFilter(float f) {
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorMatrix colorMatrix2 = new ColorMatrix();
        colorMatrix2.setSaturation(0.0f);
        float[] array = colorMatrix.getArray();
        array[0] = 0.5f;
        array[6] = 0.5f;
        array[12] = 0.5f;
        float f2 = (float) WorkQueueKt.MASK;
        array[4] = f2;
        array[9] = f2;
        array[14] = f2;
        array[18] = f;
        colorMatrix2.preConcat(colorMatrix);
        return new ColorMatrixColorFilter(colorMatrix2);
    }

    protected static final int getDisabledColor(int i) {
        int min = Math.min(Math.round((((float) (((Color.red(i) + Color.green(i)) + Color.blue(i)) / 3)) * 0.5f) + ((float) WorkQueueKt.MASK)), 255);
        return Color.rgb(min, min, min);
    }

    public static void setBadgeBounds(Drawable drawable, Rect rect) {
        int badgeSizeForIconSize = BaseIconFactory.getBadgeSizeForIconSize(rect.width());
        drawable.setBounds(rect.right - badgeSizeForIconSize, rect.bottom - badgeSizeForIconSize, rect.right, rect.bottom);
    }

    public void invalidateDrawable(Drawable drawable) {
        if (drawable == this.mBadge) {
            invalidateSelf();
        }
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        if (drawable == this.mBadge) {
            scheduleSelf(runnable, j);
        }
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }

    protected static class FastBitmapConstantState extends Drawable.ConstantState {
        /* access modifiers changed from: private */
        public Drawable.ConstantState mBadgeConstantState;
        protected final Bitmap mBitmap;
        protected final int mIconColor;
        protected boolean mIsDisabled;

        public int getChangingConfigurations() {
            return 0;
        }

        public FastBitmapConstantState(Bitmap bitmap, int i) {
            this.mBitmap = bitmap;
            this.mIconColor = i;
        }

        /* access modifiers changed from: protected */
        public FastBitmapDrawable createDrawable() {
            return new FastBitmapDrawable(this.mBitmap, this.mIconColor);
        }

        public final FastBitmapDrawable newDrawable() {
            FastBitmapDrawable createDrawable = createDrawable();
            createDrawable.setIsDisabled(this.mIsDisabled);
            Drawable.ConstantState constantState = this.mBadgeConstantState;
            if (constantState != null) {
                createDrawable.setBadge(constantState.newDrawable());
            }
            return createDrawable;
        }
    }
}
