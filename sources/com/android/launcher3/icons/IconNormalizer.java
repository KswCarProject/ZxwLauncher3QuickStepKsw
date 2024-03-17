package com.android.launcher3.icons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import java.nio.ByteBuffer;
import kotlin.UByte;

public class IconNormalizer {
    private static final float BOUND_RATIO_MARGIN = 0.05f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final boolean DEBUG = false;
    public static final float ICON_VISIBLE_AREA_FACTOR = 0.92f;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final String TAG = "IconNormalizer";
    private final RectF mAdaptiveIconBounds = new RectF();
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds = new Rect();
    private final Canvas mCanvas;
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    IconNormalizer(Context context, int i, boolean z) {
        int i2 = i * 2;
        this.mMaxSize = i2;
        Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Bitmap.Config.ALPHA_8);
        this.mBitmap = createBitmap;
        this.mCanvas = new Canvas(createBitmap);
        this.mPixels = new byte[(i2 * i2)];
        this.mLeftBorder = new float[i2];
        this.mRightBorder = new float[i2];
        Paint paint = new Paint();
        this.mPaintMaskShape = paint;
        paint.setColor(SupportMenu.CATEGORY_MASK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Paint paint2 = new Paint();
        this.mPaintMaskShapeOutline = paint2;
        paint2.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = z;
    }

    private static float getScale(float f, float f2, float f3) {
        float f4 = f / f2;
        float f5 = f4 < CIRCLE_AREA_BY_RECT ? MAX_CIRCLE_AREA_FACTOR : ((1.0f - f4) * LINEAR_SCALE_SLOPE) + MAX_SQUARE_AREA_FACTOR;
        float f6 = f / f3;
        if (f6 > f5) {
            return (float) Math.sqrt((double) (f5 / f6));
        }
        return 1.0f;
    }

    public static float normalizeAdaptiveIcon(Drawable drawable, int i, RectF rectF) {
        Rect rect = new Rect(drawable.getBounds());
        drawable.setBounds(0, 0, i, i);
        Path iconMask = ((AdaptiveIconDrawable) drawable).getIconMask();
        Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, i, i));
        Rect bounds = region.getBounds();
        int area = GraphicsUtils.getArea(region);
        if (rectF != null) {
            float f = (float) i;
            rectF.set(((float) bounds.left) / f, ((float) bounds.top) / f, 1.0f - (((float) bounds.right) / f), 1.0f - (((float) bounds.bottom) / f));
        }
        drawable.setBounds(rect);
        float f2 = (float) area;
        return getScale(f2, f2, (float) (i * i));
    }

    private boolean isShape(Path path) {
        if (Math.abs((((float) this.mBounds.width()) / ((float) this.mBounds.height())) - 1.0f) > BOUND_RATIO_MARGIN) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale((float) this.mBounds.width(), (float) this.mBounds.height());
        this.mMatrix.postTranslate((float) this.mBounds.left, (float) this.mBounds.top);
        path.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
        wrap.rewind();
        this.mBitmap.copyPixelsToBuffer(wrap);
        int i = this.mBounds.top;
        int i2 = this.mMaxSize;
        int i3 = i * i2;
        int i4 = i2 - this.mBounds.right;
        int i5 = 0;
        while (i < this.mBounds.bottom) {
            int i6 = i3 + this.mBounds.left;
            for (int i7 = this.mBounds.left; i7 < this.mBounds.right; i7++) {
                if ((this.mPixels[i6] & UByte.MAX_VALUE) > 40) {
                    i5++;
                }
                i6++;
            }
            i3 = i6 + i4;
            i++;
        }
        if (((float) i5) / ((float) (this.mBounds.width() * this.mBounds.height())) < PIXEL_DIFF_PERCENTAGE_THRESHOLD) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0141, code lost:
        return 1.0f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized float getScale(android.graphics.drawable.Drawable r17, android.graphics.RectF r18, android.graphics.Path r19, boolean[] r20) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            r3 = r20
            monitor-enter(r16)
            boolean r4 = r0 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x0142 }
            r5 = 0
            if (r4 == 0) goto L_0x0029
            float r3 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0142 }
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x001e
            int r3 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
            android.graphics.RectF r4 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x0142 }
            float r0 = normalizeAdaptiveIcon(r0, r3, r4)     // Catch:{ all -> 0x0142 }
            r1.mAdaptiveIconScale = r0     // Catch:{ all -> 0x0142 }
        L_0x001e:
            if (r2 == 0) goto L_0x0025
            android.graphics.RectF r0 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x0142 }
            r2.set(r0)     // Catch:{ all -> 0x0142 }
        L_0x0025:
            float r0 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0142 }
            monitor-exit(r16)
            return r0
        L_0x0029:
            int r4 = r17.getIntrinsicWidth()     // Catch:{ all -> 0x0142 }
            int r6 = r17.getIntrinsicHeight()     // Catch:{ all -> 0x0142 }
            if (r4 <= 0) goto L_0x0048
            if (r6 > 0) goto L_0x0036
            goto L_0x0048
        L_0x0036:
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
            if (r4 > r7) goto L_0x003c
            if (r6 <= r7) goto L_0x0058
        L_0x003c:
            int r7 = java.lang.Math.max(r4, r6)     // Catch:{ all -> 0x0142 }
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
            int r4 = r4 * r8
            int r4 = r4 / r7
            int r8 = r8 * r6
            int r6 = r8 / r7
            goto L_0x0058
        L_0x0048:
            if (r4 <= 0) goto L_0x004e
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
            if (r4 <= r7) goto L_0x0050
        L_0x004e:
            int r4 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
        L_0x0050:
            if (r6 <= 0) goto L_0x0056
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
            if (r6 <= r7) goto L_0x0058
        L_0x0056:
            int r6 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
        L_0x0058:
            android.graphics.Bitmap r7 = r1.mBitmap     // Catch:{ all -> 0x0142 }
            r8 = 0
            r7.eraseColor(r8)     // Catch:{ all -> 0x0142 }
            r0.setBounds(r8, r8, r4, r6)     // Catch:{ all -> 0x0142 }
            android.graphics.Canvas r7 = r1.mCanvas     // Catch:{ all -> 0x0142 }
            r0.draw(r7)     // Catch:{ all -> 0x0142 }
            byte[] r0 = r1.mPixels     // Catch:{ all -> 0x0142 }
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r0)     // Catch:{ all -> 0x0142 }
            r0.rewind()     // Catch:{ all -> 0x0142 }
            android.graphics.Bitmap r7 = r1.mBitmap     // Catch:{ all -> 0x0142 }
            r7.copyPixelsToBuffer(r0)     // Catch:{ all -> 0x0142 }
            int r0 = r1.mMaxSize     // Catch:{ all -> 0x0142 }
            int r7 = r0 + 1
            int r0 = r0 - r4
            r10 = r8
            r14 = r10
            r11 = -1
            r12 = -1
            r13 = -1
        L_0x007e:
            if (r10 >= r6) goto L_0x00bd
            r15 = r8
            r5 = -1
            r8 = -1
        L_0x0083:
            if (r15 >= r4) goto L_0x009b
            byte[] r9 = r1.mPixels     // Catch:{ all -> 0x0142 }
            byte r9 = r9[r14]     // Catch:{ all -> 0x0142 }
            r9 = r9 & 255(0xff, float:3.57E-43)
            r3 = 40
            if (r9 <= r3) goto L_0x0094
            r3 = -1
            if (r5 != r3) goto L_0x0093
            r5 = r15
        L_0x0093:
            r8 = r15
        L_0x0094:
            int r14 = r14 + 1
            int r15 = r15 + 1
            r3 = r20
            goto L_0x0083
        L_0x009b:
            int r14 = r14 + r0
            float[] r3 = r1.mLeftBorder     // Catch:{ all -> 0x0142 }
            float r9 = (float) r5     // Catch:{ all -> 0x0142 }
            r3[r10] = r9     // Catch:{ all -> 0x0142 }
            float[] r3 = r1.mRightBorder     // Catch:{ all -> 0x0142 }
            float r9 = (float) r8     // Catch:{ all -> 0x0142 }
            r3[r10] = r9     // Catch:{ all -> 0x0142 }
            r3 = -1
            if (r5 == r3) goto L_0x00b6
            if (r11 != r3) goto L_0x00ac
            r11 = r10
        L_0x00ac:
            int r3 = java.lang.Math.min(r7, r5)     // Catch:{ all -> 0x0142 }
            int r12 = java.lang.Math.max(r12, r8)     // Catch:{ all -> 0x0142 }
            r7 = r3
            r13 = r10
        L_0x00b6:
            int r10 = r10 + 1
            r3 = r20
            r5 = 0
            r8 = 0
            goto L_0x007e
        L_0x00bd:
            r0 = 1065353216(0x3f800000, float:1.0)
            r3 = -1
            if (r11 == r3) goto L_0x0140
            if (r12 != r3) goto L_0x00c6
            goto L_0x0140
        L_0x00c6:
            float[] r5 = r1.mLeftBorder     // Catch:{ all -> 0x0142 }
            r8 = 1
            convertToConvexArray(r5, r8, r11, r13)     // Catch:{ all -> 0x0142 }
            float[] r5 = r1.mRightBorder     // Catch:{ all -> 0x0142 }
            convertToConvexArray(r5, r3, r11, r13)     // Catch:{ all -> 0x0142 }
            r3 = 0
            r5 = 0
        L_0x00d3:
            if (r3 >= r6) goto L_0x00ec
            float[] r9 = r1.mLeftBorder     // Catch:{ all -> 0x0142 }
            r10 = r9[r3]     // Catch:{ all -> 0x0142 }
            r14 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r10 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r10 > 0) goto L_0x00e0
            goto L_0x00e9
        L_0x00e0:
            float[] r10 = r1.mRightBorder     // Catch:{ all -> 0x0142 }
            r10 = r10[r3]     // Catch:{ all -> 0x0142 }
            r9 = r9[r3]     // Catch:{ all -> 0x0142 }
            float r10 = r10 - r9
            float r10 = r10 + r0
            float r5 = r5 + r10
        L_0x00e9:
            int r3 = r3 + 1
            goto L_0x00d3
        L_0x00ec:
            android.graphics.Rect r3 = r1.mBounds     // Catch:{ all -> 0x0142 }
            r3.left = r7     // Catch:{ all -> 0x0142 }
            android.graphics.Rect r3 = r1.mBounds     // Catch:{ all -> 0x0142 }
            r3.right = r12     // Catch:{ all -> 0x0142 }
            android.graphics.Rect r3 = r1.mBounds     // Catch:{ all -> 0x0142 }
            r3.top = r11     // Catch:{ all -> 0x0142 }
            android.graphics.Rect r3 = r1.mBounds     // Catch:{ all -> 0x0142 }
            r3.bottom = r13     // Catch:{ all -> 0x0142 }
            if (r2 == 0) goto L_0x011e
            android.graphics.Rect r3 = r1.mBounds     // Catch:{ all -> 0x0142 }
            int r3 = r3.left     // Catch:{ all -> 0x0142 }
            float r3 = (float) r3     // Catch:{ all -> 0x0142 }
            float r9 = (float) r4     // Catch:{ all -> 0x0142 }
            float r3 = r3 / r9
            android.graphics.Rect r10 = r1.mBounds     // Catch:{ all -> 0x0142 }
            int r10 = r10.top     // Catch:{ all -> 0x0142 }
            float r10 = (float) r10     // Catch:{ all -> 0x0142 }
            float r14 = (float) r6     // Catch:{ all -> 0x0142 }
            float r10 = r10 / r14
            android.graphics.Rect r15 = r1.mBounds     // Catch:{ all -> 0x0142 }
            int r15 = r15.right     // Catch:{ all -> 0x0142 }
            float r15 = (float) r15     // Catch:{ all -> 0x0142 }
            float r15 = r15 / r9
            float r9 = r0 - r15
            android.graphics.Rect r15 = r1.mBounds     // Catch:{ all -> 0x0142 }
            int r15 = r15.bottom     // Catch:{ all -> 0x0142 }
            float r15 = (float) r15     // Catch:{ all -> 0x0142 }
            float r15 = r15 / r14
            float r0 = r0 - r15
            r2.set(r3, r10, r9, r0)     // Catch:{ all -> 0x0142 }
        L_0x011e:
            r0 = r20
            if (r0 == 0) goto L_0x0132
            boolean r2 = r1.mEnableShapeDetection     // Catch:{ all -> 0x0142 }
            if (r2 == 0) goto L_0x0132
            int r2 = r0.length     // Catch:{ all -> 0x0142 }
            if (r2 <= 0) goto L_0x0132
            r2 = r19
            boolean r2 = r1.isShape(r2)     // Catch:{ all -> 0x0142 }
            r3 = 0
            r0[r3] = r2     // Catch:{ all -> 0x0142 }
        L_0x0132:
            int r13 = r13 + r8
            int r13 = r13 - r11
            int r12 = r12 + r8
            int r12 = r12 - r7
            int r13 = r13 * r12
            float r0 = (float) r13     // Catch:{ all -> 0x0142 }
            int r4 = r4 * r6
            float r2 = (float) r4     // Catch:{ all -> 0x0142 }
            float r0 = getScale(r5, r0, r2)     // Catch:{ all -> 0x0142 }
            monitor-exit(r16)
            return r0
        L_0x0140:
            monitor-exit(r16)
            return r0
        L_0x0142:
            r0 = move-exception
            monitor-exit(r16)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }

    private static void convertToConvexArray(float[] fArr, int i, int i2, int i3) {
        float[] fArr2 = new float[(fArr.length - 1)];
        int i4 = -1;
        float f = Float.MAX_VALUE;
        for (int i5 = i2 + 1; i5 <= i3; i5++) {
            if (fArr[i5] > -1.0f) {
                if (f == Float.MAX_VALUE) {
                    i4 = i2;
                } else {
                    float f2 = ((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - f;
                    float f3 = (float) i;
                    if (f2 * f3 < 0.0f) {
                        while (i4 > i2) {
                            i4--;
                            if ((((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - fArr2[i4]) * f3 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                f = (fArr[i5] - fArr[i4]) / ((float) (i5 - i4));
                for (int i6 = i4; i6 < i5; i6++) {
                    fArr2[i6] = f;
                    fArr[i6] = fArr[i4] + (((float) (i6 - i4)) * f);
                }
                i4 = i5;
            }
        }
    }

    public static int getNormalizedCircleSize(int i) {
        return (int) Math.round(Math.sqrt(((double) ((((float) (i * i)) * MAX_CIRCLE_AREA_FACTOR) * 4.0f)) / 3.141592653589793d));
    }
}
