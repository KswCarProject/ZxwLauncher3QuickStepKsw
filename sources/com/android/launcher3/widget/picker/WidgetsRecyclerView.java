package com.android.launcher3.widget.picker;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.FastScrollRecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.model.WidgetListSpaceEntry;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import com.android.launcher3.widget.model.WidgetsListSearchHeaderEntry;
import com.android.launcher3.widget.picker.WidgetsSpaceViewHolderBinder;

public class WidgetsRecyclerView extends FastScrollRecyclerView implements RecyclerView.OnItemTouchListener {
    private WidgetsListAdapter mAdapter;
    private final Point mFastScrollerOffset;
    private HeaderViewDimensionsProvider mHeaderViewDimensionsProvider;
    private int mLastVisibleWidgetContentTableHeight;
    private final int mScrollbarTop;
    private final int mSpacingBetweenEntries;
    private boolean mTouchDownOnScroller;
    private int mWidgetEmptySpaceHeight;
    private int mWidgetHeaderHeight;

    public interface HeaderViewDimensionsProvider {
        int getHeaderViewHeight();
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public WidgetsRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFastScrollerOffset = new Point();
        this.mLastVisibleWidgetContentTableHeight = 0;
        this.mWidgetHeaderHeight = 0;
        this.mWidgetEmptySpaceHeight = 0;
        this.mScrollbarTop = getResources().getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin);
        addOnItemTouchListener(this);
        ((ActivityContext) ActivityContext.lookupContext(getContext())).getDeviceProfile();
        this.mSpacingBetweenEntries = getResources().getDimensionPixelSize(R.dimen.widget_list_entry_spacing);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = (WidgetsListAdapter) adapter;
    }

    public String scrollToPositionAtProgress(float f) {
        if (isModelNotReady()) {
            return "";
        }
        stopScroll();
        float itemCount = ((float) this.mAdapter.getItemCount()) * f;
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0, (int) (-(((float) getAvailableScrollHeight()) * f)));
        if (f == 1.0f) {
            itemCount -= 1.0f;
        }
        return this.mAdapter.getSectionName((int) itemCount);
    }

    public void onUpdateScrollbar(int i) {
        if (isModelNotReady()) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        int currentScrollY = getCurrentScrollY();
        if (currentScrollY < 0) {
            this.mScrollbar.setThumbOffsetY(-1);
        } else {
            synchronizeScrollBarThumbOffsetToViewScroll(currentScrollY, getAvailableScrollHeight());
        }
    }

    public int getCurrentScrollY() {
        int i = -1;
        if (isModelNotReady() || getChildCount() == 0) {
            return -1;
        }
        View view = null;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            i = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            view = layoutManager.findViewByPosition(i);
        }
        if (view == null) {
            view = getChildAt(0);
            i = getChildPosition(view);
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof TableLayout) {
                this.mLastVisibleWidgetContentTableHeight = childAt.getMeasuredHeight();
            } else if ((childAt instanceof WidgetsListHeader) && this.mWidgetHeaderHeight == 0 && childAt.getMeasuredHeight() > 0) {
                this.mWidgetHeaderHeight = childAt.getMeasuredHeight();
            } else if ((childAt instanceof WidgetsSpaceViewHolderBinder.EmptySpaceView) && childAt.getMeasuredHeight() > 0) {
                this.mWidgetEmptySpaceHeight = childAt.getMeasuredHeight();
            }
        }
        int itemsHeight = getItemsHeight(i);
        return (getPaddingTop() + itemsHeight) - getLayoutManager().getDecoratedTop(view);
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollHeight() {
        return Math.max(0, getItemsHeight(this.mAdapter.getItemCount()) - ((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()));
    }

    private boolean isModelNotReady() {
        return this.mAdapter.getItemCount() == 0;
    }

    public int getScrollBarTop() {
        HeaderViewDimensionsProvider headerViewDimensionsProvider = this.mHeaderViewDimensionsProvider;
        if (headerViewDimensionsProvider == null) {
            return this.mScrollbarTop;
        }
        return headerViewDimensionsProvider.getHeaderViewHeight() + this.mScrollbarTop;
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mTouchDownOnScroller = this.mScrollbar.isHitInParent(motionEvent.getX(), motionEvent.getY(), this.mFastScrollerOffset);
        }
        if (this.mTouchDownOnScroller) {
            return this.mScrollbar.handleTouchEvent(motionEvent, this.mFastScrollerOffset);
        }
        return false;
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (this.mTouchDownOnScroller) {
            this.mScrollbar.handleTouchEvent(motionEvent, this.mFastScrollerOffset);
        }
    }

    public void setHeaderViewDimensionsProvider(HeaderViewDimensionsProvider headerViewDimensionsProvider) {
        this.mHeaderViewDimensionsProvider = headerViewDimensionsProvider;
    }

    private int getItemsHeight(int i) {
        int i2;
        if (i > this.mAdapter.getItems().size()) {
            i = this.mAdapter.getItems().size();
        }
        int i3 = 0;
        for (int i4 = 0; i4 < i; i4++) {
            WidgetsListBaseEntry widgetsListBaseEntry = this.mAdapter.getItems().get(i4);
            if ((widgetsListBaseEntry instanceof WidgetsListHeaderEntry) || (widgetsListBaseEntry instanceof WidgetsListSearchHeaderEntry)) {
                i3 += this.mWidgetHeaderHeight;
                if (i4 > 0) {
                    i2 = this.mSpacingBetweenEntries;
                }
            } else if (widgetsListBaseEntry instanceof WidgetsListContentEntry) {
                i2 = this.mLastVisibleWidgetContentTableHeight;
            } else if (widgetsListBaseEntry instanceof WidgetListSpaceEntry) {
                i2 = this.mWidgetEmptySpaceHeight;
            } else {
                throw new UnsupportedOperationException("Can't estimate height for " + widgetsListBaseEntry);
            }
            i3 += i2;
        }
        return i3;
    }
}
