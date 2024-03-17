package com.android.launcher3.graphics;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.core.view.ViewCompat;
import com.android.launcher3.R;
import com.android.launcher3.icons.BitmapRenderer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ShadowDrawable extends Drawable {
    private final Paint mPaint;
    private final ShadowDrawableState mState;

    public int getOpacity() {
        return -3;
    }

    public ShadowDrawable() {
        this(new ShadowDrawableState());
    }

    private ShadowDrawable(ShadowDrawableState shadowDrawableState) {
        this.mPaint = new Paint(3);
        this.mState = shadowDrawableState;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (!bounds.isEmpty()) {
            if (this.mState.mLastDrawnBitmap == null) {
                regenerateBitmapCache();
            }
            canvas.drawBitmap(this.mState.mLastDrawnBitmap, (Rect) null, bounds, this.mPaint);
        }
    }

    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    public int getIntrinsicHeight() {
        return this.mState.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mState.mIntrinsicWidth;
    }

    public boolean canApplyTheme() {
        return this.mState.canApplyTheme();
    }

    public void applyTheme(Resources.Theme theme) {
        TypedArray obtainStyledAttributes = theme.obtainStyledAttributes(new int[]{R.attr.isWorkspaceDarkText});
        boolean z = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        if (this.mState.mIsDark != z) {
            this.mState.mIsDark = z;
            this.mState.mLastDrawnBitmap = null;
            invalidateSelf();
        }
    }

    private void regenerateBitmapCache() {
        Drawable mutate = this.mState.mChildState.newDrawable().mutate();
        mutate.setBounds(this.mState.mShadowSize, this.mState.mShadowSize, this.mState.mIntrinsicWidth - this.mState.mShadowSize, this.mState.mIntrinsicHeight - this.mState.mShadowSize);
        mutate.setTint(this.mState.mIsDark ? this.mState.mDarkTintColor : -1);
        if (this.mState.mIsDark) {
            ShadowDrawableState shadowDrawableState = this.mState;
            int i = shadowDrawableState.mIntrinsicWidth;
            int i2 = this.mState.mIntrinsicHeight;
            Objects.requireNonNull(mutate);
            shadowDrawableState.mLastDrawnBitmap = BitmapRenderer.createHardwareBitmap(i, i2, new BitmapRenderer(mutate) {
                public final /* synthetic */ Drawable f$0;

                {
                    this.f$0 = r1;
                }

                public final void draw(Canvas canvas) {
                    this.f$0.draw(canvas);
                }
            });
            return;
        }
        Paint paint = new Paint(3);
        paint.setMaskFilter(new BlurMaskFilter((float) this.mState.mShadowSize, BlurMaskFilter.Blur.NORMAL));
        int[] iArr = new int[2];
        int i3 = this.mState.mIntrinsicWidth;
        int i4 = this.mState.mIntrinsicHeight;
        Objects.requireNonNull(mutate);
        Bitmap extractAlpha = BitmapRenderer.createSoftwareBitmap(i3, i4, new BitmapRenderer(mutate) {
            public final /* synthetic */ Drawable f$0;

            {
                this.f$0 = r1;
            }

            public final void draw(Canvas canvas) {
                this.f$0.draw(canvas);
            }
        }).extractAlpha(paint, iArr);
        paint.setMaskFilter((MaskFilter) null);
        paint.setColor(this.mState.mShadowColor);
        ShadowDrawableState shadowDrawableState2 = this.mState;
        shadowDrawableState2.mLastDrawnBitmap = BitmapRenderer.createHardwareBitmap(shadowDrawableState2.mIntrinsicWidth, this.mState.mIntrinsicHeight, new BitmapRenderer(extractAlpha, iArr, paint, mutate) {
            public final /* synthetic */ Bitmap f$0;
            public final /* synthetic */ int[] f$1;
            public final /* synthetic */ Paint f$2;
            public final /* synthetic */ Drawable f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void draw(Canvas canvas) {
                ShadowDrawable.lambda$regenerateBitmapCache$0(this.f$0, this.f$1, this.f$2, this.f$3, canvas);
            }
        });
    }

    static /* synthetic */ void lambda$regenerateBitmapCache$0(Bitmap bitmap, int[] iArr, Paint paint, Drawable drawable, Canvas canvas) {
        canvas.drawBitmap(bitmap, (float) iArr[0], (float) iArr[1], paint);
        drawable.draw(canvas);
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray typedArray;
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (theme == null) {
            typedArray = resources.obtainAttributes(attributeSet, R.styleable.ShadowDrawable);
        } else {
            typedArray = theme.obtainStyledAttributes(attributeSet, R.styleable.ShadowDrawable, 0, 0);
        }
        try {
            Drawable drawable = typedArray.getDrawable(0);
            if (drawable != null) {
                this.mState.mShadowColor = typedArray.getColor(1, ViewCompat.MEASURED_STATE_MASK);
                this.mState.mShadowSize = typedArray.getDimensionPixelSize(2, 0);
                this.mState.mDarkTintColor = typedArray.getColor(3, ViewCompat.MEASURED_STATE_MASK);
                this.mState.mIntrinsicHeight = drawable.getIntrinsicHeight() + (this.mState.mShadowSize * 2);
                this.mState.mIntrinsicWidth = drawable.getIntrinsicWidth() + (this.mState.mShadowSize * 2);
                this.mState.mChangingConfigurations = drawable.getChangingConfigurations();
                this.mState.mChildState = drawable.getConstantState();
                return;
            }
            throw new XmlPullParserException("missing src attribute");
        } finally {
            typedArray.recycle();
        }
    }

    private static class ShadowDrawableState extends Drawable.ConstantState {
        int mChangingConfigurations;
        Drawable.ConstantState mChildState;
        int mDarkTintColor;
        int mIntrinsicHeight;
        int mIntrinsicWidth;
        boolean mIsDark;
        Bitmap mLastDrawnBitmap;
        int mShadowColor;
        int mShadowSize;

        public boolean canApplyTheme() {
            return true;
        }

        private ShadowDrawableState() {
        }

        public Drawable newDrawable() {
            return new ShadowDrawable(this);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }
}
