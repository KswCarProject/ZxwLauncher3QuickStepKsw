package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import androidx.core.view.ViewCompat;
import com.android.launcher3.icons.ShadowGenerator;

public class ShadowGenerator {
    private static final int AMBIENT_SHADOW_ALPHA = 25;
    public static final float BLUR_FACTOR = 0.035f;
    public static final boolean ENABLE_SHADOWS = true;
    private static final float HALF_DISTANCE = 0.5f;
    private static final int KEY_SHADOW_ALPHA = 7;
    public static final float KEY_SHADOW_DISTANCE = 0.020833334f;
    private final Paint mBlurPaint = new Paint(3);
    private final BlurMaskFilter mDefaultBlurMaskFilter;
    private final Paint mDrawPaint = new Paint(3);
    private final int mIconSize;

    public ShadowGenerator(int i) {
        this.mIconSize = i;
        this.mDefaultBlurMaskFilter = new BlurMaskFilter(((float) i) * 0.035f, BlurMaskFilter.Blur.NORMAL);
    }

    public synchronized void recreateIcon(Bitmap bitmap, Canvas canvas) {
        recreateIcon(bitmap, this.mDefaultBlurMaskFilter, 25, 7, canvas);
    }

    public synchronized void recreateIcon(Bitmap bitmap, BlurMaskFilter blurMaskFilter, int i, int i2, Canvas canvas) {
        int[] iArr = new int[2];
        this.mBlurPaint.setMaskFilter(blurMaskFilter);
        Bitmap extractAlpha = bitmap.extractAlpha(this.mBlurPaint, iArr);
        this.mDrawPaint.setAlpha(i);
        canvas.drawBitmap(extractAlpha, (float) iArr[0], (float) iArr[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(i2);
        canvas.drawBitmap(extractAlpha, (float) iArr[0], ((float) iArr[1]) + (((float) this.mIconSize) * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mDrawPaint);
    }

    public static float getScaleForBounds(RectF rectF) {
        float min = Math.min(Math.min(rectF.left, rectF.right), rectF.top);
        float f = min < 0.035f ? 0.465f / (0.5f - min) : 1.0f;
        return rectF.bottom < 0.055833332f ? Math.min(f, 0.44416666f / (0.5f - rectF.bottom)) : f;
    }

    public static class Builder {
        public int ambientShadowAlpha = 25;
        public final RectF bounds = new RectF();
        public final int color;
        public int keyShadowAlpha = 7;
        public float keyShadowDistance;
        public float radius;
        public float shadowBlur;

        public Builder(int i) {
            this.color = i;
        }

        public Builder setupBlurForSize(int i) {
            float f = ((float) i) * 1.0f;
            this.shadowBlur = f / 24.0f;
            this.keyShadowDistance = f / 16.0f;
            return this;
        }

        public Bitmap createPill(int i, int i2) {
            return createPill(i, i2, ((float) i2) / 2.0f);
        }

        public Bitmap createPill(int i, int i2, float f) {
            this.radius = f;
            float f2 = (float) i;
            float f3 = f2 / 2.0f;
            int max = Math.max(Math.round(this.shadowBlur + f3), Math.round(this.radius + this.shadowBlur + this.keyShadowDistance));
            float f4 = (float) i2;
            this.bounds.set(0.0f, 0.0f, f2, f4);
            float f5 = (float) max;
            this.bounds.offsetTo(f5 - f3, f5 - (f4 / 2.0f));
            int i3 = max * 2;
            return BitmapRenderer.createHardwareBitmap(i3, i3, new BitmapRenderer() {
                public final void draw(Canvas canvas) {
                    ShadowGenerator.Builder.this.drawShadow(canvas);
                }
            });
        }

        public void drawShadow(Canvas canvas) {
            Paint paint = new Paint(3);
            paint.setColor(this.color);
            paint.setShadowLayer(this.shadowBlur, 0.0f, this.keyShadowDistance, GraphicsUtils.setColorAlphaBound(ViewCompat.MEASURED_STATE_MASK, this.keyShadowAlpha));
            RectF rectF = this.bounds;
            float f = this.radius;
            canvas.drawRoundRect(rectF, f, f, paint);
            paint.setShadowLayer(this.shadowBlur, 0.0f, 0.0f, GraphicsUtils.setColorAlphaBound(ViewCompat.MEASURED_STATE_MASK, this.ambientShadowAlpha));
            RectF rectF2 = this.bounds;
            float f2 = this.radius;
            canvas.drawRoundRect(rectF2, f2, f2, paint);
            if (Color.alpha(this.color) < 255) {
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                paint.clearShadowLayer();
                paint.setColor(ViewCompat.MEASURED_STATE_MASK);
                RectF rectF3 = this.bounds;
                float f3 = this.radius;
                canvas.drawRoundRect(rectF3, f3, f3, paint);
                paint.setXfermode((Xfermode) null);
                paint.setColor(this.color);
                RectF rectF4 = this.bounds;
                float f4 = this.radius;
                canvas.drawRoundRect(rectF4, f4, f4, paint);
            }
        }
    }
}
