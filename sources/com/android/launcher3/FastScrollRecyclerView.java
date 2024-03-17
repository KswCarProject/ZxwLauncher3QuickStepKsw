package com.android.launcher3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.views.RecyclerViewFastScroller;

public abstract class FastScrollRecyclerView extends RecyclerView {
    protected RecyclerViewFastScroller mScrollbar;

    /* access modifiers changed from: protected */
    public abstract int getAvailableScrollHeight();

    public abstract int getCurrentScrollY();

    public void onFastScrollCompleted() {
    }

    public abstract void onUpdateScrollbar(int i);

    public abstract String scrollToPositionAtProgress(float f);

    public boolean supportsFastScrolling() {
        return true;
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public FastScrollRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FastScrollRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FastScrollRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        bindFastScrollbar();
    }

    public void bindFastScrollbar() {
        ViewGroup viewGroup = (ViewGroup) getParent().getParent();
        RecyclerViewFastScroller recyclerViewFastScroller = (RecyclerViewFastScroller) viewGroup.findViewById(R.id.fast_scroller);
        this.mScrollbar = recyclerViewFastScroller;
        recyclerViewFastScroller.setRecyclerView(this, (TextView) viewGroup.findViewById(R.id.fast_scroller_popup));
        onUpdateScrollbar(0);
    }

    public RecyclerViewFastScroller getScrollbar() {
        return this.mScrollbar;
    }

    public int getScrollBarTop() {
        return getPaddingTop();
    }

    public int getScrollbarTrackHeight() {
        return (this.mScrollbar.getHeight() - getScrollBarTop()) - getPaddingBottom();
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollBarHeight() {
        return getScrollbarTrackHeight() - this.mScrollbar.getThumbHeight();
    }

    /* access modifiers changed from: protected */
    public void synchronizeScrollBarThumbOffsetToViewScroll(int i, int i2) {
        if (i2 <= 0) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        this.mScrollbar.setThumbOffsetY((int) ((((float) i) / ((float) i2)) * ((float) getAvailableScrollBarHeight())));
    }

    public boolean shouldContainerScroll(MotionEvent motionEvent, View view) {
        float[] fArr = {motionEvent.getX(), motionEvent.getY()};
        Utilities.mapCoordInSelfToDescendant(this.mScrollbar, view, fArr);
        if (this.mScrollbar.shouldBlockIntercept((int) fArr[0], (int) fArr[1])) {
            return false;
        }
        if (getCurrentScrollY() == 0) {
            return true;
        }
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            return true;
        }
        return false;
    }

    public void onScrollStateChanged(int i) {
        super.onScrollStateChanged(i);
        if (i == 0) {
            AccessibilityManagerCompat.sendScrollFinishedEventToTest(getContext());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (isLayoutSuppressed()) {
            accessibilityNodeInfo.setScrollable(false);
        }
    }

    public void scrollToTop() {
        RecyclerViewFastScroller recyclerViewFastScroller = this.mScrollbar;
        if (recyclerViewFastScroller != null) {
            recyclerViewFastScroller.reattachThumbToScroll();
        }
        scrollToPosition(0);
    }
}
