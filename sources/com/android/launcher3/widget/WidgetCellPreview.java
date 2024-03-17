package com.android.launcher3.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;

public class WidgetCellPreview extends FrameLayout {
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

    public WidgetCellPreview(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetCellPreview(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetCellPreview(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        super.onInterceptTouchEvent(motionEvent);
        return true;
    }

    public boolean hasPreviewLayout() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof LauncherAppWidgetHostView) {
                return true;
            }
        }
        return false;
    }

    public LauncherAppWidgetHostView getPreviewLayout() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof LauncherAppWidgetHostView) {
                return (LauncherAppWidgetHostView) getChildAt(i);
            }
        }
        return null;
    }
}
