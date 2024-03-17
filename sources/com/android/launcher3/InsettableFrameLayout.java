package com.android.launcher3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;

public class InsettableFrameLayout extends FrameLayout implements Insettable {
    @ViewDebug.ExportedProperty(category = "launcher")
    protected Rect mInsets = new Rect();

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    public InsettableFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setFrameLayoutChildInsets(View view, Rect rect, Rect rect2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (view instanceof Insettable) {
            ((Insettable) view).setInsets(rect);
        } else if (!layoutParams.ignoreInsets) {
            layoutParams.topMargin += rect.top - rect2.top;
            layoutParams.leftMargin += rect.left - rect2.left;
            layoutParams.rightMargin += rect.right - rect2.right;
            layoutParams.bottomMargin += rect.bottom - rect2.bottom;
        }
        view.setLayoutParams(layoutParams);
    }

    public void setInsets(Rect rect) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            setFrameLayoutChildInsets(getChildAt(i), rect, this.mInsets);
        }
        this.mInsets.set(rect);
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean ignoreInsets = false;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.InsettableFrameLayout_Layout);
            this.ignoreInsets = obtainStyledAttributes.getBoolean(0, false);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        if (isAttachedToWindow()) {
            setFrameLayoutChildInsets(view, this.mInsets, new Rect());
        }
    }

    public static void dispatchInsets(ViewGroup viewGroup, Rect rect) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof Insettable) {
                ((Insettable) childAt).setInsets(rect);
            }
        }
    }
}
