package com.android.launcher3;

import android.animation.TimeInterpolator;

public class LogDecelerateInterpolator implements TimeInterpolator {
    int mBase;
    int mDrift;
    final float mLogScale;

    public LogDecelerateInterpolator(int i, int i2) {
        this.mBase = i;
        this.mDrift = i2;
        this.mLogScale = 1.0f / computeLog(1.0f, i, i2);
    }

    static float computeLog(float f, int i, int i2) {
        return ((float) (-Math.pow((double) i, (double) (-f)))) + 1.0f + (((float) i2) * f);
    }

    public float getInterpolation(float f) {
        if (Float.compare(f, 1.0f) == 0) {
            return 1.0f;
        }
        return this.mLogScale * computeLog(f, this.mBase, this.mDrift);
    }
}
