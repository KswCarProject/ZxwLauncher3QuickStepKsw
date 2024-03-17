package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.util.Property;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.views.ActivityContext;
import com.android.systemui.shared.system.SysUiStatsLog;

public class PreviewBackground extends CellLayout.DelegatedCellDrawing {
    private static final float ACCEPT_SCALE_FACTOR = 1.2f;
    private static final int BG_OPACITY = 255;
    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    private static final boolean DRAW_SHADOW = false;
    private static final boolean DRAW_STROKE = false;
    private static final int MAX_BG_OPACITY = 255;
    private static final Property<PreviewBackground, Integer> SHADOW_ALPHA = new Property<PreviewBackground, Integer>(Integer.class, "shadowAlpha") {
        public Integer get(PreviewBackground previewBackground) {
            return Integer.valueOf(previewBackground.mShadowAlpha);
        }

        public void set(PreviewBackground previewBackground, Integer num) {
            int unused = previewBackground.mShadowAlpha = num.intValue();
            previewBackground.invalidate();
        }
    };
    private static final int SHADOW_OPACITY = 40;
    private static final Property<PreviewBackground, Integer> STROKE_ALPHA = new Property<PreviewBackground, Integer>(Integer.class, "strokeAlpha") {
        public Integer get(PreviewBackground previewBackground) {
            return Integer.valueOf(previewBackground.mStrokeAlpha);
        }

        public void set(PreviewBackground previewBackground, Integer num) {
            int unused = previewBackground.mStrokeAlpha = num.intValue();
            previewBackground.invalidate();
        }
    };
    int basePreviewOffsetX;
    int basePreviewOffsetY;
    public boolean isClipping = true;
    private int mBgColor;
    private int mDotColor;
    private CellLayout mDrawingDelegate;
    private View mInvalidateDelegate;
    private final Paint mPaint = new Paint(1);
    private final Path mPath = new Path();
    float mScale = 1.0f;
    /* access modifiers changed from: private */
    public ValueAnimator mScaleAnimator;
    private final Matrix mShaderMatrix = new Matrix();
    /* access modifiers changed from: private */
    public int mShadowAlpha = 255;
    /* access modifiers changed from: private */
    public ObjectAnimator mShadowAnimator;
    private final PorterDuffXfermode mShadowPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private RadialGradient mShadowShader = null;
    /* access modifiers changed from: private */
    public int mStrokeAlpha = 255;
    /* access modifiers changed from: private */
    public ObjectAnimator mStrokeAlphaAnimator;
    private int mStrokeColor;
    private float mStrokeWidth;
    int previewSize;

    public void animateBackgroundStroke() {
    }

    public void drawBackgroundStroke(Canvas canvas) {
    }

    public void drawShadow(Canvas canvas) {
    }

    public void fadeInBackgroundShadow() {
    }

    public void drawUnderItem(Canvas canvas) {
        drawBackground(canvas);
        if (!this.isClipping) {
            drawBackgroundStroke(canvas);
        }
    }

    public void drawOverItem(Canvas canvas) {
        if (this.isClipping) {
            drawBackgroundStroke(canvas);
        }
    }

    public void setup(Context context, ActivityContext activityContext, View view, int i, int i2) {
        this.mInvalidateDelegate = view;
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(R.styleable.FolderIconPreview);
        this.mDotColor = obtainStyledAttributes.getColor(0, 0);
        this.mStrokeColor = obtainStyledAttributes.getColor(1, 0);
        this.mBgColor = obtainStyledAttributes.getColor(2, 0);
        obtainStyledAttributes.recycle();
        DeviceProfile deviceProfile = activityContext.getDeviceProfile();
        int i3 = deviceProfile.folderIconSizePx;
        this.previewSize = i3;
        this.basePreviewOffsetX = (i - i3) / 2;
        this.basePreviewOffsetY = i2 + deviceProfile.folderIconOffsetYPx;
        this.mStrokeWidth = context.getResources().getDisplayMetrics().density;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void getBounds(Rect rect) {
        int i = this.basePreviewOffsetY;
        int i2 = this.basePreviewOffsetX;
        int i3 = this.previewSize;
        rect.set(i2, i, i2 + i3, i3 + i);
    }

    public int getRadius() {
        return this.previewSize / 2;
    }

    /* access modifiers changed from: package-private */
    public int getScaledRadius() {
        return (int) (this.mScale * ((float) getRadius()));
    }

    /* access modifiers changed from: package-private */
    public int getOffsetX() {
        return this.basePreviewOffsetX - (getScaledRadius() - getRadius());
    }

    /* access modifiers changed from: package-private */
    public int getOffsetY() {
        return this.basePreviewOffsetY - (getScaledRadius() - getRadius());
    }

    /* access modifiers changed from: package-private */
    public float getScaleProgress() {
        return (this.mScale - 1.0f) / 0.20000005f;
    }

    /* access modifiers changed from: package-private */
    public void invalidate() {
        View view = this.mInvalidateDelegate;
        if (view != null) {
            view.invalidate();
        }
        CellLayout cellLayout = this.mDrawingDelegate;
        if (cellLayout != null) {
            cellLayout.invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public void setInvalidateDelegate(View view) {
        this.mInvalidateDelegate = view;
        invalidate();
    }

    public int getBgColor() {
        return this.mBgColor;
    }

    public int getDotColor() {
        return this.mDotColor;
    }

    public void drawBackground(Canvas canvas) {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(getBgColor());
        IconShape.getShape().drawShape(canvas, (float) getOffsetX(), (float) getOffsetY(), (float) getScaledRadius(), this.mPaint);
        drawShadow(canvas);
    }

    public void drawLeaveBehind(Canvas canvas) {
        float f = this.mScale;
        this.mScale = 0.5f;
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(Color.argb(160, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED, SysUiStatsLog.NOTIFICATION_PANEL_REPORTED));
        IconShape.getShape().drawShape(canvas, (float) getOffsetX(), (float) getOffsetY(), (float) getScaledRadius(), this.mPaint);
        this.mScale = f;
    }

    public Path getClipPath() {
        this.mPath.reset();
        float scaledRadius = ((float) getScaledRadius()) * 1.125f;
        float radius = scaledRadius - ((float) getRadius());
        IconShape.getShape().addToPath(this.mPath, ((float) this.basePreviewOffsetX) - radius, ((float) this.basePreviewOffsetY) - radius, scaledRadius);
        return this.mPath;
    }

    /* access modifiers changed from: private */
    /* renamed from: delegateDrawing */
    public void lambda$animateToRest$1$PreviewBackground(CellLayout cellLayout, int i, int i2) {
        if (this.mDrawingDelegate != cellLayout) {
            cellLayout.addDelegatedCellDrawing(this);
        }
        this.mDrawingDelegate = cellLayout;
        this.mDelegateCellX = i;
        this.mDelegateCellY = i2;
        invalidate();
    }

    /* access modifiers changed from: private */
    public void clearDrawingDelegate() {
        CellLayout cellLayout = this.mDrawingDelegate;
        if (cellLayout != null) {
            cellLayout.removeDelegatedCellDrawing(this);
        }
        this.mDrawingDelegate = null;
        this.isClipping = false;
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public boolean drawingDelegated() {
        return this.mDrawingDelegate != null;
    }

    private void animateScale(final float f, final Runnable runnable, final Runnable runnable2) {
        final float f2 = this.mScale;
        ValueAnimator valueAnimator = this.mScaleAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mScaleAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                PreviewBackground.this.mScale = (f * animatedFraction) + ((1.0f - animatedFraction) * f2);
                PreviewBackground.this.invalidate();
            }
        });
        this.mScaleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
            }

            public void onAnimationEnd(Animator animator) {
                Runnable runnable = runnable2;
                if (runnable != null) {
                    runnable.run();
                }
                ValueAnimator unused = PreviewBackground.this.mScaleAnimator = null;
            }
        });
        this.mScaleAnimator.setDuration(100);
        this.mScaleAnimator.start();
    }

    public void animateToAccept(CellLayout cellLayout, int i, int i2) {
        animateScale(ACCEPT_SCALE_FACTOR, new Runnable(cellLayout, i, i2) {
            public final /* synthetic */ CellLayout f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PreviewBackground.this.lambda$animateToAccept$0$PreviewBackground(this.f$1, this.f$2, this.f$3);
            }
        }, (Runnable) null);
    }

    public void animateToRest() {
        animateScale(1.0f, new Runnable(this.mDrawingDelegate, this.mDelegateCellX, this.mDelegateCellY) {
            public final /* synthetic */ CellLayout f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PreviewBackground.this.lambda$animateToRest$1$PreviewBackground(this.f$1, this.f$2, this.f$3);
            }
        }, new Runnable() {
            public final void run() {
                PreviewBackground.this.clearDrawingDelegate();
            }
        });
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }
}
