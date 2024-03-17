package com.android.launcher3.widget.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.LinearLayout;

public class SearchAndRecommendationsView extends LinearLayout {
    private SearchAndRecommendationsScrollController mController;

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

    public SearchAndRecommendationsView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SearchAndRecommendationsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SearchAndRecommendationsView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SearchAndRecommendationsView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setSearchAndRecommendationScrollController(SearchAndRecommendationsScrollController searchAndRecommendationsScrollController) {
        this.mController = searchAndRecommendationsScrollController;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mController.onInterceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mController.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }
}
