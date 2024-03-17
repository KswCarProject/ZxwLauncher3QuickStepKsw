package com.android.quickstep;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.MotionEvent;
import com.android.launcher3.states.RotationHelper;
import com.android.quickstep.util.RecentsOrientedState;

public class OrientationRectF extends RectF {
    private static final boolean DEBUG = false;
    private static final String TAG = "OrientationRectF";
    private final float mHeight;
    private final int mRotation;
    private final Matrix mTmpMatrix = new Matrix();
    private final float[] mTmpPoint = new float[2];
    private final float mWidth;

    public OrientationRectF(float f, float f2, float f3, float f4, int i) {
        super(f, f2, f3, f4);
        this.mRotation = i;
        this.mHeight = f4;
        this.mWidth = f3;
    }

    public String toString() {
        return super.toString() + " rotation: " + this.mRotation;
    }

    public boolean contains(float f, float f2) {
        return this.left < this.right && this.top < this.bottom && f >= this.left && f <= this.right && f2 >= this.top && f2 <= this.bottom;
    }

    public boolean applyTransformFromRotation(MotionEvent motionEvent, int i, boolean z) {
        return applyTransform(motionEvent, RotationHelper.deltaRotation(i, this.mRotation), z);
    }

    public boolean applyTransformToRotation(MotionEvent motionEvent, int i, boolean z) {
        return applyTransform(motionEvent, RotationHelper.deltaRotation(this.mRotation, i), z);
    }

    public boolean applyTransform(MotionEvent motionEvent, int i, boolean z) {
        this.mTmpMatrix.reset();
        RecentsOrientedState.postDisplayRotation(i, this.mHeight, this.mWidth, this.mTmpMatrix);
        if (z) {
            motionEvent.applyTransform(this.mTmpMatrix);
            return true;
        }
        this.mTmpPoint[0] = motionEvent.getX();
        this.mTmpPoint[1] = motionEvent.getY();
        this.mTmpMatrix.mapPoints(this.mTmpPoint);
        float[] fArr = this.mTmpPoint;
        if (!contains(fArr[0], fArr[1])) {
            return false;
        }
        motionEvent.applyTransform(this.mTmpMatrix);
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getRotation() {
        return this.mRotation;
    }
}
