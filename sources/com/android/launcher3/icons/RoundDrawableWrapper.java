package com.android.launcher3.icons;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;

public class RoundDrawableWrapper extends DrawableWrapper {
    private final Path mClipPath = new Path();
    private final float mRoundedCornersRadius;
    private final RectF mTempRect = new RectF();

    public RoundDrawableWrapper(Drawable drawable, float f) {
        super(drawable);
        this.mRoundedCornersRadius = f;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        this.mTempRect.set(getBounds());
        this.mClipPath.reset();
        Path path = this.mClipPath;
        RectF rectF = this.mTempRect;
        float f = this.mRoundedCornersRadius;
        path.addRoundRect(rectF, f, f, Path.Direction.CCW);
        super.onBoundsChange(rect);
    }

    public final void draw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(this.mClipPath);
        super.draw(canvas);
        canvas.restoreToCount(save);
    }
}
