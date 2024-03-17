package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;

public class SplitPlaceholderView extends FrameLayout {
    public static final FloatProperty<SplitPlaceholderView> ALPHA_FLOAT = new FloatProperty<SplitPlaceholderView>("SplitViewAlpha") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(SplitPlaceholderView splitPlaceholderView, float f) {
            splitPlaceholderView.setVisibility(f != 0.0f ? 0 : 8);
            splitPlaceholderView.setAlpha(f);
        }

        public Float get(SplitPlaceholderView splitPlaceholderView) {
            return Float.valueOf(splitPlaceholderView.getAlpha());
        }
    };
    private IconView mIconView;
    private final Paint mPaint;
    private final Rect mTempRect = new Rect();

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public SplitPlaceholderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        paint.setColor(getThemeBackgroundColor(context));
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        drawBackground(canvas);
        super.dispatchDraw(canvas);
        if (this.mIconView != null) {
            getLocalVisibleRect(this.mTempRect);
            ((FloatingTaskView) getParent()).centerIconView(this.mIconView, (float) this.mTempRect.centerX(), (float) this.mTempRect.centerY());
        }
    }

    public IconView getIconView() {
        return this.mIconView;
    }

    public void setIcon(Drawable drawable, int i) {
        if (this.mIconView == null) {
            IconView iconView = new IconView(getContext());
            this.mIconView = iconView;
            addView(iconView);
        }
        this.mIconView.setDrawable(drawable);
        this.mIconView.setDrawableSize(i, i);
        this.mIconView.setLayoutParams(new FrameLayout.LayoutParams(i, i));
    }

    private void drawBackground(Canvas canvas) {
        ((FloatingTaskView) getParent()).drawRoundedRect(canvas, this.mPaint);
    }

    private static int getThemeBackgroundColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16842801, typedValue, true);
        return typedValue.data;
    }
}
