package com.android.launcher3.widget.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.TableLayout;

public class WidgetsListTableView extends TableLayout {
    private WidgetsListDrawableState mListDrawableState;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public WidgetsListTableView(Context context) {
        super(context);
    }

    public WidgetsListTableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setListDrawableState(WidgetsListDrawableState widgetsListDrawableState) {
        if (widgetsListDrawableState != this.mListDrawableState) {
            this.mListDrawableState = widgetsListDrawableState;
            refreshDrawableState();
        }
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        WidgetsListDrawableState widgetsListDrawableState = this.mListDrawableState;
        if (widgetsListDrawableState == null) {
            return super.onCreateDrawableState(i);
        }
        int[] onCreateDrawableState = super.onCreateDrawableState(i + widgetsListDrawableState.mStateSet.length);
        mergeDrawableStates(onCreateDrawableState, this.mListDrawableState.mStateSet);
        return onCreateDrawableState;
    }
}
