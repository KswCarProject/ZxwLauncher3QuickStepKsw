package com.android.systemui.shared.pip;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Choreographer;
import android.view.SurfaceControl;
import android.window.PictureInPictureSurfaceTransaction;

public class PipSurfaceTransactionHelper {
    private final int mCornerRadius;
    private final int mShadowRadius;
    private final Rect mTmpDestinationRect = new Rect();
    private final RectF mTmpDestinationRectF = new RectF();
    private final float[] mTmpFloat9 = new float[9];
    private final RectF mTmpSourceRectF = new RectF();
    private final Matrix mTmpTransform = new Matrix();

    public PipSurfaceTransactionHelper(int i, int i2) {
        this.mCornerRadius = i;
        this.mShadowRadius = i2;
    }

    public PictureInPictureSurfaceTransaction scale(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2) {
        float f = (float) rect2.left;
        float f2 = (float) rect2.top;
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRectF.set(rect2);
        this.mTmpDestinationRectF.offsetTo(0.0f, 0.0f);
        this.mTmpTransform.setRectToRect(this.mTmpSourceRectF, this.mTmpDestinationRectF, Matrix.ScaleToFit.FILL);
        float scaledCornerRadius = getScaledCornerRadius(rect, rect2);
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setPosition(surfaceControl, f, f2).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, (float) this.mShadowRadius);
        return newPipSurfaceTransaction(f, f2, this.mTmpFloat9, 0.0f, scaledCornerRadius, (float) this.mShadowRadius, rect);
    }

    public PictureInPictureSurfaceTransaction scale(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, float f, float f2, float f3) {
        SurfaceControl surfaceControl2 = surfaceControl;
        Rect rect3 = rect2;
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRectF.set(rect2);
        this.mTmpDestinationRectF.offsetTo(0.0f, 0.0f);
        this.mTmpTransform.setRectToRect(this.mTmpSourceRectF, this.mTmpDestinationRectF, Matrix.ScaleToFit.FILL);
        float f4 = f;
        this.mTmpTransform.postRotate(f, 0.0f, 0.0f);
        float scaledCornerRadius = getScaledCornerRadius(rect, rect2);
        SurfaceControl.Transaction transaction2 = transaction;
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setPosition(surfaceControl, f2, f3).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, (float) this.mShadowRadius);
        float[] fArr = this.mTmpFloat9;
        return newPipSurfaceTransaction(f2, f3, fArr, f, scaledCornerRadius, (float) this.mShadowRadius, rect);
    }

    public PictureInPictureSurfaceTransaction scaleAndCrop(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, Rect rect3, Rect rect4) {
        float f;
        float f2;
        int i;
        int i2;
        float f3;
        this.mTmpSourceRectF.set(rect2);
        this.mTmpDestinationRect.set(rect2);
        this.mTmpDestinationRect.inset(rect4);
        if (rect.isEmpty() || rect.width() == rect2.width()) {
            if (rect2.width() <= rect2.height()) {
                f2 = (float) rect3.width();
                i = rect2.width();
            } else {
                f2 = (float) rect3.height();
                i = rect2.height();
            }
            f = f2 / ((float) i);
        } else {
            if (rect.width() <= rect.height()) {
                f3 = (float) rect3.width();
                i2 = rect.width();
            } else {
                f3 = (float) rect3.height();
                i2 = rect.height();
            }
            f = f3 / ((float) i2);
        }
        float f4 = ((float) rect3.left) - (((float) (rect4.left + rect2.left)) * f);
        float f5 = ((float) rect3.top) - (((float) (rect4.top + rect2.top)) * f);
        this.mTmpTransform.setScale(f, f);
        float scaledCornerRadius = getScaledCornerRadius(this.mTmpDestinationRect, rect3);
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setCrop(surfaceControl, this.mTmpDestinationRect).setPosition(surfaceControl, f4, f5).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, (float) this.mShadowRadius);
        return newPipSurfaceTransaction(f4, f5, this.mTmpFloat9, 0.0f, scaledCornerRadius, (float) this.mShadowRadius, this.mTmpDestinationRect);
    }

    public PictureInPictureSurfaceTransaction scaleAndRotate(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, Rect rect, Rect rect2, Rect rect3, float f, float f2, float f3) {
        float f4;
        int i;
        float f5;
        float f6;
        SurfaceControl surfaceControl2 = surfaceControl;
        Rect rect4 = rect;
        Rect rect5 = rect3;
        float f7 = f;
        this.mTmpSourceRectF.set(rect);
        this.mTmpDestinationRect.set(rect);
        this.mTmpDestinationRect.inset(rect3);
        if (rect.width() <= rect.height()) {
            f4 = (float) rect2.width();
            i = rect.width();
        } else {
            f4 = (float) rect2.height();
            i = rect.height();
        }
        float f8 = f4 / ((float) i);
        this.mTmpTransform.setRotate(f, 0.0f, 0.0f);
        this.mTmpTransform.postScale(f8, f8);
        Rect rect6 = rect2;
        float scaledCornerRadius = getScaledCornerRadius(this.mTmpDestinationRect, rect2);
        if (f7 < 0.0f) {
            f6 = f2 + (((float) rect5.top) * f8);
            f5 = f3 + (((float) rect5.left) * f8);
        } else {
            f6 = f2 - (((float) rect5.top) * f8);
            f5 = f3 - (((float) rect5.left) * f8);
        }
        SurfaceControl.Transaction transaction2 = transaction;
        transaction.setMatrix(surfaceControl, this.mTmpTransform, this.mTmpFloat9).setCrop(surfaceControl, this.mTmpDestinationRect).setPosition(surfaceControl, f6, f5).setCornerRadius(surfaceControl, scaledCornerRadius).setShadowRadius(surfaceControl, (float) this.mShadowRadius);
        return newPipSurfaceTransaction(f6, f5, this.mTmpFloat9, f, scaledCornerRadius, (float) this.mShadowRadius, this.mTmpDestinationRect);
    }

    private float getScaledCornerRadius(Rect rect, Rect rect2) {
        return ((float) this.mCornerRadius) * ((float) (Math.hypot((double) rect.width(), (double) rect.height()) / Math.hypot((double) rect2.width(), (double) rect2.height())));
    }

    private static PictureInPictureSurfaceTransaction newPipSurfaceTransaction(float f, float f2, float[] fArr, float f3, float f4, float f5, Rect rect) {
        return new PictureInPictureSurfaceTransaction.Builder().setPosition(f, f2).setTransform(fArr, f3).setCornerRadius(f4).setShadowRadius(f5).setWindowCrop(rect).build();
    }

    public static SurfaceControl.Transaction newSurfaceControlTransaction() {
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.setFrameTimelineVsync(Choreographer.getSfInstance().getVsyncId());
        return transaction;
    }
}
