package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.views.RecyclerViewFastScroller;

public class SearchRecyclerView extends AllAppsRecyclerView {
    private static final String TAG = "SearchRecyclerView";

    public RecyclerViewFastScroller getScrollbar() {
        return null;
    }

    public boolean supportsFastScrolling() {
        return false;
    }

    public SearchRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SearchRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SearchRecyclerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SearchRecyclerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    /* access modifiers changed from: protected */
    public void updatePoolSize() {
        getRecycledViewPool().setMaxRecycledViews(2, this.mNumAppsPerRow);
    }
}
