package com.android.launcher3.keyboard;

import android.graphics.Rect;
import android.view.View;
import com.android.launcher3.R;

public abstract class FocusIndicatorHelper extends ItemFocusIndicatorHelper<View> implements View.OnFocusChangeListener {
    public FocusIndicatorHelper(View view) {
        super(view, view.getResources().getColor(R.color.focused_background));
    }

    public void onFocusChange(View view, boolean z) {
        changeFocus(view, z);
    }

    /* access modifiers changed from: protected */
    public boolean shouldDraw(View view) {
        return view.isAttachedToWindow();
    }

    public static class SimpleFocusIndicatorHelper extends FocusIndicatorHelper {
        /* access modifiers changed from: protected */
        public /* bridge */ /* synthetic */ boolean shouldDraw(Object obj) {
            return FocusIndicatorHelper.super.shouldDraw((View) obj);
        }

        public SimpleFocusIndicatorHelper(View view) {
            super(view);
        }

        public void viewToRect(View view, Rect rect) {
            rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        }
    }
}
