package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import com.android.launcher3.Utilities;

public class IconView extends View {
    private Drawable mDrawable;
    private int mDrawableHeight;
    private int mDrawableWidth;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public IconView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setDrawable(Drawable drawable) {
        Drawable drawable2 = this.mDrawable;
        if (drawable2 != null) {
            drawable2.setCallback((Drawable.Callback) null);
        }
        this.mDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            setDrawableSizeInternal(getWidth(), getHeight());
        }
        invalidate();
    }

    public void setDrawableSize(int i, int i2) {
        this.mDrawableWidth = i;
        this.mDrawableHeight = i2;
        if (this.mDrawable != null) {
            setDrawableSizeInternal(getWidth(), getHeight());
        }
    }

    private void setDrawableSizeInternal(int i, int i2) {
        Rect rect = new Rect(0, 0, i, i2);
        Rect rect2 = new Rect();
        Gravity.apply(17, this.mDrawableWidth, this.mDrawableHeight, rect, rect2);
        this.mDrawable.setBounds(rect2);
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }

    public int getDrawableWidth() {
        return this.mDrawableWidth;
    }

    public int getDrawableHeight() {
        return this.mDrawableHeight;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (this.mDrawable != null) {
            setDrawableSizeInternal(i, i2);
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mDrawable;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mDrawable;
        if (drawable != null && drawable.isStateful() && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void setAlpha(float f) {
        super.setAlpha(f);
        if (f > 0.0f) {
            setVisibility(0);
        } else {
            setVisibility(4);
        }
    }

    public void setIconColorTint(int i, float f) {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            drawable.setColorFilter(Utilities.makeColorTintingColorFilter(i, f));
        }
    }
}
