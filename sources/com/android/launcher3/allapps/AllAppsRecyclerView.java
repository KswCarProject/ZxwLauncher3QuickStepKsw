package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.FastScrollRecyclerView;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.util.LogConfig;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.RecyclerViewFastScroller;
import java.util.List;

public class AllAppsRecyclerView extends FastScrollRecyclerView {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LATENCY = Utilities.isPropertyEnabled(LogConfig.SEARCH_LOGGING);
    protected static final String TAG = "AllAppsRecyclerView";
    protected AlphabeticalAppsList<?> mApps;
    /* access modifiers changed from: private */
    public final SparseIntArray mCachedScrollPositions;
    protected AllAppsBackgroundDrawable mEmptySearchBackground;
    protected int mEmptySearchBackgroundTopOffset;
    private final AllAppsFastScrollHelper mFastScrollHelper;
    protected final int mNumAppsPerRow;
    private final RecyclerView.AdapterDataObserver mObserver;
    private final SparseIntArray mViewHeights;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isPaddingOffsetRequired() {
        return true;
    }

    public AllAppsRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mViewHeights = new SparseIntArray();
        this.mCachedScrollPositions = new SparseIntArray();
        this.mObserver = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                AllAppsRecyclerView.this.mCachedScrollPositions.clear();
            }

            public void onItemRangeChanged(int i, int i2) {
                onChanged();
            }

            public void onItemRangeInserted(int i, int i2) {
                onChanged();
            }

            public void onItemRangeRemoved(int i, int i2) {
                onChanged();
            }

            public void onItemRangeMoved(int i, int i2, int i3) {
                onChanged();
            }
        };
        this.mEmptySearchBackgroundTopOffset = getResources().getDimensionPixelSize(R.dimen.all_apps_empty_search_bg_top_offset);
        this.mNumAppsPerRow = LauncherAppState.getIDP(context).numColumns;
        this.mFastScrollHelper = new AllAppsFastScrollHelper(this);
    }

    public void setApps(AlphabeticalAppsList<?> alphabeticalAppsList) {
        this.mApps = alphabeticalAppsList;
    }

    public AlphabeticalAppsList<?> getApps() {
        return this.mApps;
    }

    /* access modifiers changed from: protected */
    public void updatePoolSize() {
        DeviceProfile deviceProfile = ((ActivityContext) ActivityContext.lookupContext(getContext())).getDeviceProfile();
        RecyclerView.RecycledViewPool recycledViewPool = getRecycledViewPool();
        recycledViewPool.setMaxRecycledViews(4, 1);
        recycledViewPool.setMaxRecycledViews(16, 1);
        recycledViewPool.setMaxRecycledViews(8, 1);
        recycledViewPool.setMaxRecycledViews(2, ((int) Math.ceil((double) (deviceProfile.availableHeightPx / deviceProfile.allAppsIconSizePx))) * (this.mNumAppsPerRow + 1));
        this.mViewHeights.clear();
        this.mViewHeights.put(2, deviceProfile.allAppsCellHeightPx);
    }

    public void onDraw(Canvas canvas) {
        AllAppsBackgroundDrawable allAppsBackgroundDrawable = this.mEmptySearchBackground;
        if (allAppsBackgroundDrawable != null && allAppsBackgroundDrawable.getAlpha() > 0) {
            this.mEmptySearchBackground.draw(canvas);
        }
        if (DEBUG_LATENCY) {
            Log.d(LogConfig.SEARCH_LOGGING, getClass().getSimpleName() + " onDraw; time stamp = " + System.currentTimeMillis());
        }
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mEmptySearchBackground || super.verifyDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        updateEmptySearchBackgroundBounds();
        updatePoolSize();
    }

    public void onSearchResultsChanged() {
        scrollToTop();
        if (!this.mApps.hasNoFilteredResults() || FeatureFlags.ENABLE_DEVICE_SEARCH.get()) {
            AllAppsBackgroundDrawable allAppsBackgroundDrawable = this.mEmptySearchBackground;
            if (allAppsBackgroundDrawable != null) {
                allAppsBackgroundDrawable.setBgAlpha(0.0f);
                return;
            }
            return;
        }
        if (this.mEmptySearchBackground == null) {
            AllAppsBackgroundDrawable allAppsBackgroundDrawable2 = new AllAppsBackgroundDrawable(getContext());
            this.mEmptySearchBackground = allAppsBackgroundDrawable2;
            allAppsBackgroundDrawable2.setAlpha(0);
            this.mEmptySearchBackground.setCallback(this);
            updateEmptySearchBackgroundBounds();
        }
        this.mEmptySearchBackground.animateBgAlpha(1.0f, DragView.VIEW_ZOOM_DURATION);
    }

    public void onScrollStateChanged(int i) {
        super.onScrollStateChanged(i);
        StatsLogManager statsLogManager = ((ActivityContext) ActivityContext.lookupContext(getContext())).getStatsLogManager();
        if (i == 0) {
            statsLogManager.logger().sendToInteractionJankMonitor(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_VERTICAL_SWIPE_END, this);
        } else if (i == 1) {
            statsLogManager.logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_SCROLLED);
            requestFocus();
            statsLogManager.logger().sendToInteractionJankMonitor(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_VERTICAL_SWIPE_BEGIN, this);
            UiThreadHelper.hideKeyboardAsync((ActivityContext) ActivityContext.lookupContext(getContext()), getApplicationWindowToken());
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        AllAppsBackgroundDrawable allAppsBackgroundDrawable;
        boolean onInterceptTouchEvent = super.onInterceptTouchEvent(motionEvent);
        if (!onInterceptTouchEvent && motionEvent.getAction() == 0 && (allAppsBackgroundDrawable = this.mEmptySearchBackground) != null && allAppsBackgroundDrawable.getAlpha() > 0) {
            this.mEmptySearchBackground.setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return onInterceptTouchEvent;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000b, code lost:
        r0 = r3.mApps.getFastScrollerSections();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String scrollToPositionAtProgress(float r4) {
        /*
            r3 = this;
            com.android.launcher3.allapps.AlphabeticalAppsList<?> r0 = r3.mApps
            int r0 = r0.getNumAppRows()
            java.lang.String r1 = ""
            if (r0 != 0) goto L_0x000b
            return r1
        L_0x000b:
            com.android.launcher3.allapps.AlphabeticalAppsList<?> r0 = r3.mApps
            java.util.List r0 = r0.getFastScrollerSections()
            int r2 = r0.size()
            if (r2 != 0) goto L_0x0018
            return r1
        L_0x0018:
            float r1 = (float) r2
            float r4 = r4 * r1
            int r4 = (int) r4
            r1 = 0
            int r2 = r2 + -1
            int r4 = com.android.launcher3.Utilities.boundToRange((int) r4, (int) r1, (int) r2)
            java.lang.Object r4 = r0.get(r4)
            com.android.launcher3.allapps.AlphabeticalAppsList$FastScrollSectionInfo r4 = (com.android.launcher3.allapps.AlphabeticalAppsList.FastScrollSectionInfo) r4
            com.android.launcher3.allapps.AllAppsFastScrollHelper r0 = r3.mFastScrollHelper
            r0.smoothScrollToSection(r4)
            java.lang.String r4 = r4.sectionName
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsRecyclerView.scrollToPositionAtProgress(float):java.lang.String");
    }

    public void onFastScrollCompleted() {
        super.onFastScrollCompleted();
        this.mFastScrollHelper.onFastScrollCompleted();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(this.mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
        }
    }

    /* access modifiers changed from: protected */
    public int getTopPaddingOffset() {
        return -getPaddingTop();
    }

    public void onUpdateScrollbar(int i) {
        int i2;
        AlphabeticalAppsList<?> alphabeticalAppsList = this.mApps;
        if (alphabeticalAppsList != null) {
            if (alphabeticalAppsList.getAdapterItems().isEmpty() || this.mNumAppsPerRow == 0) {
                this.mScrollbar.setThumbOffsetY(-1);
                return;
            }
            int currentScrollY = getCurrentScrollY();
            if (currentScrollY < 0) {
                this.mScrollbar.setThumbOffsetY(-1);
                return;
            }
            int availableScrollBarHeight = getAvailableScrollBarHeight();
            int availableScrollHeight = getAvailableScrollHeight();
            if (availableScrollHeight <= 0) {
                this.mScrollbar.setThumbOffsetY(-1);
            } else if (!this.mScrollbar.isThumbDetached()) {
                synchronizeScrollBarThumbOffsetToViewScroll(currentScrollY, availableScrollHeight);
            } else if (!this.mScrollbar.isDraggingThumb()) {
                int i3 = (int) ((((float) currentScrollY) / ((float) availableScrollHeight)) * ((float) availableScrollBarHeight));
                int thumbOffsetY = this.mScrollbar.getThumbOffsetY();
                int i4 = i3 - thumbOffsetY;
                if (((float) (i4 * i)) > 0.0f) {
                    if (i < 0) {
                        i2 = Math.max((int) (((float) (i * thumbOffsetY)) / ((float) i3)), i4);
                    } else {
                        i2 = Math.min((int) (((float) (i * (availableScrollBarHeight - thumbOffsetY))) / ((float) (availableScrollBarHeight - i3))), i4);
                    }
                    int max = Math.max(0, Math.min(availableScrollBarHeight, thumbOffsetY + i2));
                    this.mScrollbar.setThumbOffsetY(max);
                    if (i3 == max) {
                        this.mScrollbar.reattachThumbToScroll();
                        return;
                    }
                    return;
                }
                this.mScrollbar.setThumbOffsetY(thumbOffsetY);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        r0 = getChildAt(0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCurrentScrollY() {
        /*
            r4 = this;
            com.android.launcher3.allapps.AlphabeticalAppsList<?> r0 = r4.mApps
            java.util.List r0 = r0.getAdapterItems()
            boolean r0 = r0.isEmpty()
            r1 = -1
            if (r0 != 0) goto L_0x0035
            int r0 = r4.mNumAppsPerRow
            if (r0 == 0) goto L_0x0035
            int r0 = r4.getChildCount()
            if (r0 != 0) goto L_0x0018
            goto L_0x0035
        L_0x0018:
            r0 = 0
            android.view.View r0 = r4.getChildAt(r0)
            int r2 = r4.getChildAdapterPosition(r0)
            if (r2 != r1) goto L_0x0024
            return r1
        L_0x0024:
            int r1 = r4.getPaddingTop()
            androidx.recyclerview.widget.RecyclerView$LayoutManager r3 = r4.getLayoutManager()
            int r0 = r3.getDecoratedTop(r0)
            int r0 = r4.getCurrentScrollY(r2, r0)
            int r1 = r1 + r0
        L_0x0035:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.AllAppsRecyclerView.getCurrentScrollY():int");
    }

    public int getCurrentScrollY(int i, int i2) {
        List<BaseAllAppsAdapter.AdapterItem> adapterItems = this.mApps.getAdapterItems();
        BaseAllAppsAdapter.AdapterItem adapterItem = i < adapterItems.size() ? adapterItems.get(i) : null;
        int i3 = this.mCachedScrollPositions.get(i, -1);
        if (i3 < 0) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                BaseAllAppsAdapter.AdapterItem adapterItem2 = adapterItems.get(i5);
                if (AllAppsGridAdapter.isIconViewType(adapterItem2.viewType)) {
                    if (adapterItem != null && adapterItem.viewType == adapterItem2.viewType && adapterItem.rowIndex == adapterItem2.rowIndex) {
                        break;
                    } else if (adapterItem2.rowAppIndex == 0) {
                        i4 += this.mViewHeights.get(adapterItem2.viewType, 0);
                    }
                } else {
                    int i6 = this.mViewHeights.get(adapterItem2.viewType);
                    if (i6 == 0) {
                        RecyclerView.ViewHolder findViewHolderForAdapterPosition = findViewHolderForAdapterPosition(i5);
                        if (findViewHolderForAdapterPosition == null) {
                            RecyclerView.ViewHolder createViewHolder = getAdapter().createViewHolder(this, adapterItem2.viewType);
                            getAdapter().onBindViewHolder(createViewHolder, i5);
                            createViewHolder.itemView.measure(0, 0);
                            i6 = createViewHolder.itemView.getMeasuredHeight();
                            getRecycledViewPool().putRecycledView(createViewHolder);
                        } else {
                            i6 = findViewHolderForAdapterPosition.itemView.getMeasuredHeight();
                        }
                    }
                    i4 += i6;
                }
            }
            this.mCachedScrollPositions.put(i, i4);
            i3 = i4;
        }
        return i3 - i2;
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollHeight() {
        return ((getPaddingTop() + getCurrentScrollY(getAdapter().getItemCount(), 0)) - getHeight()) + getPaddingBottom();
    }

    public int getScrollBarTop() {
        return getResources().getDimensionPixelOffset(R.dimen.all_apps_header_top_padding);
    }

    public RecyclerViewFastScroller getScrollbar() {
        return this.mScrollbar;
    }

    private void updateEmptySearchBackgroundBounds() {
        if (this.mEmptySearchBackground != null) {
            int measuredWidth = (getMeasuredWidth() - this.mEmptySearchBackground.getIntrinsicWidth()) / 2;
            int i = this.mEmptySearchBackgroundTopOffset;
            AllAppsBackgroundDrawable allAppsBackgroundDrawable = this.mEmptySearchBackground;
            allAppsBackgroundDrawable.setBounds(measuredWidth, i, allAppsBackgroundDrawable.getIntrinsicWidth() + measuredWidth, this.mEmptySearchBackground.getIntrinsicHeight() + i);
        }
    }
}
