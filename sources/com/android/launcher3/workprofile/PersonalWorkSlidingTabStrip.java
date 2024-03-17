package com.android.launcher3.workprofile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.launcher3.pageindicators.PageIndicator;

public class PersonalWorkSlidingTabStrip extends LinearLayout implements PageIndicator {
    private int mLastActivePage = 0;
    private OnActivePageChangedListener mOnActivePageChangedListener;

    public interface OnActivePageChangedListener {
        void onActivePageChanged(int i);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setMarkersCount(int i) {
    }

    public void setScroll(int i, int i2) {
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public PersonalWorkSlidingTabStrip(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void updateTabTextColor(int i) {
        int i2 = 0;
        while (i2 < getChildCount()) {
            ((Button) getChildAt(i2)).setSelected(i2 == i);
            i2++;
        }
    }

    public void setActiveMarker(int i) {
        updateTabTextColor(i);
        OnActivePageChangedListener onActivePageChangedListener = this.mOnActivePageChangedListener;
        if (!(onActivePageChangedListener == null || this.mLastActivePage == i)) {
            onActivePageChangedListener.onActivePageChanged(i);
        }
        this.mLastActivePage = i;
    }

    public void setOnActivePageChangedListener(OnActivePageChangedListener onActivePageChangedListener) {
        this.mOnActivePageChangedListener = onActivePageChangedListener;
    }
}
