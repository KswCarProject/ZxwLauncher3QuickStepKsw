package com.android.systemui.shared.system;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.SurfaceControl;

public class TransactionCompat {
    final float[] mTmpValues = new float[9];
    final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();

    public void apply() {
        this.mTransaction.apply();
    }

    public TransactionCompat show(SurfaceControl surfaceControl) {
        this.mTransaction.show(surfaceControl);
        return this;
    }

    public TransactionCompat hide(SurfaceControl surfaceControl) {
        this.mTransaction.hide(surfaceControl);
        return this;
    }

    public TransactionCompat setPosition(SurfaceControl surfaceControl, float f, float f2) {
        this.mTransaction.setPosition(surfaceControl, f, f2);
        return this;
    }

    public TransactionCompat setSize(SurfaceControl surfaceControl, int i, int i2) {
        this.mTransaction.setBufferSize(surfaceControl, i, i2);
        return this;
    }

    public TransactionCompat setLayer(SurfaceControl surfaceControl, int i) {
        this.mTransaction.setLayer(surfaceControl, i);
        return this;
    }

    public TransactionCompat setAlpha(SurfaceControl surfaceControl, float f) {
        this.mTransaction.setAlpha(surfaceControl, f);
        return this;
    }

    public TransactionCompat setOpaque(SurfaceControl surfaceControl, boolean z) {
        this.mTransaction.setOpaque(surfaceControl, z);
        return this;
    }

    public TransactionCompat setMatrix(SurfaceControl surfaceControl, float f, float f2, float f3, float f4) {
        this.mTransaction.setMatrix(surfaceControl, f, f2, f3, f4);
        return this;
    }

    public TransactionCompat setMatrix(SurfaceControl surfaceControl, Matrix matrix) {
        this.mTransaction.setMatrix(surfaceControl, matrix, this.mTmpValues);
        return this;
    }

    public TransactionCompat setWindowCrop(SurfaceControl surfaceControl, Rect rect) {
        this.mTransaction.setWindowCrop(surfaceControl, rect);
        return this;
    }

    public TransactionCompat setCornerRadius(SurfaceControl surfaceControl, float f) {
        this.mTransaction.setCornerRadius(surfaceControl, f);
        return this;
    }

    public TransactionCompat setBackgroundBlurRadius(SurfaceControl surfaceControl, int i) {
        this.mTransaction.setBackgroundBlurRadius(surfaceControl, i);
        return this;
    }

    public TransactionCompat setColor(SurfaceControl surfaceControl, float[] fArr) {
        this.mTransaction.setColor(surfaceControl, fArr);
        return this;
    }

    public static void setRelativeLayer(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, SurfaceControl surfaceControl2, int i) {
        transaction.setRelativeLayer(surfaceControl, surfaceControl2, i);
    }
}
