package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.R;

public class WidgetImageView extends View {
    private final int mBadgeMargin;
    private Drawable mDrawable;
    private final RectF mDstRectF;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public WidgetImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDstRectF = new RectF();
        this.mBadgeMargin = context.getResources().getDimensionPixelSize(R.dimen.profile_badge_margin);
    }

    public void setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        invalidate();
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mDrawable != null) {
            updateDstRectF();
            this.mDrawable.setBounds(getBitmapBounds());
            this.mDrawable.draw(canvas);
        }
    }

    private void updateDstRectF() {
        float width = (float) getWidth();
        float height = (float) getHeight();
        float intrinsicWidth = (float) this.mDrawable.getIntrinsicWidth();
        float f = intrinsicWidth > width ? width / intrinsicWidth : 1.0f;
        float f2 = intrinsicWidth * f;
        float intrinsicHeight = ((float) this.mDrawable.getIntrinsicHeight()) * f;
        this.mDstRectF.left = (width - f2) / 2.0f;
        this.mDstRectF.right = (width + f2) / 2.0f;
        if (intrinsicHeight > height) {
            this.mDstRectF.top = 0.0f;
            this.mDstRectF.bottom = intrinsicHeight;
            return;
        }
        this.mDstRectF.top = (height - intrinsicHeight) / 2.0f;
        this.mDstRectF.bottom = (height + intrinsicHeight) / 2.0f;
    }

    public Rect getBitmapBounds() {
        updateDstRectF();
        Rect rect = new Rect();
        this.mDstRectF.round(rect);
        return rect;
    }
}
