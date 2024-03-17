package com.android.launcher3.anim;

import android.graphics.Rect;

public class RoundedRectRevealOutlineProvider extends RevealOutlineAnimation {
    private final float mEndRadius;
    private final Rect mEndRect;
    private final float mStartRadius;
    private final Rect mStartRect;

    public boolean shouldRemoveElevationDuringAnimation() {
        return false;
    }

    public RoundedRectRevealOutlineProvider(float f, float f2, Rect rect, Rect rect2) {
        this.mStartRadius = f;
        this.mEndRadius = f2;
        this.mStartRect = rect;
        this.mEndRect = rect2;
    }

    public void setProgress(float f) {
        float f2 = 1.0f - f;
        this.mOutlineRadius = (this.mStartRadius * f2) + (this.mEndRadius * f);
        this.mOutline.left = (int) ((((float) this.mStartRect.left) * f2) + (((float) this.mEndRect.left) * f));
        this.mOutline.top = (int) ((((float) this.mStartRect.top) * f2) + (((float) this.mEndRect.top) * f));
        this.mOutline.right = (int) ((((float) this.mStartRect.right) * f2) + (((float) this.mEndRect.right) * f));
        this.mOutline.bottom = (int) ((f2 * ((float) this.mStartRect.bottom)) + (f * ((float) this.mEndRect.bottom)));
    }
}
