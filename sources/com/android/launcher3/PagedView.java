package com.android.launcher3;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.OverScroller;
import android.widget.ScrollView;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.EdgeEffectCompat;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class PagedView<T extends View & PageIndicator> extends ViewGroup {
    public static final int ACTION_MOVE_ALLOW_EASY_FLING = 254;
    private static final boolean DEBUG = false;
    public static final boolean DEBUG_FAILED_QUICKSWITCH = false;
    public static final int INVALID_PAGE = -1;
    protected static final int INVALID_POINTER = -1;
    private static final float MAX_SCROLL_PROGRESS = 1.0f;
    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;
    protected static final ComputePageScrollsLogic SIMPLE_SCROLL_LOGIC = $$Lambda$PagedView$bFsGWHKJCiyiqldga8RW5Ge_gk.INSTANCE;
    private static final String TAG = "PagedView";
    protected int mActivePointerId;
    private boolean mAllowEasyFling;
    protected boolean mAllowOverScroll;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected int mCurrentPage;
    protected int mCurrentPageScrollDiff;
    protected int mCurrentScrollOverPage;
    private float mDownMotionPrimary;
    private float mDownMotionX;
    private float mDownMotionY;
    private int mEasyFlingThresholdVelocity;
    protected EdgeEffectCompat mEdgeGlowLeft;
    protected EdgeEffectCompat mEdgeGlowRight;
    protected boolean mFirstLayout;
    private int mFlingThresholdVelocity;
    private boolean mFreeScroll;
    protected final Rect mInsets;
    private boolean mIsBeingDragged;
    protected boolean mIsLayoutValid;
    protected boolean mIsPageInTransition;
    protected boolean mIsRtl;
    private float mLastMotion;
    private float mLastMotionRemainder;
    protected int mMaxScroll;
    private int mMaximumVelocity;
    private int mMinFlingVelocity;
    protected int mMinScroll;
    private int mMinSnapVelocity;
    @ViewDebug.ExportedProperty(category = "launcher")
    protected int mNextPage;
    private final ArrayList<Runnable> mOnPageScrollsInitializedCallbacks;
    private Runnable mOnPageTransitionEndCallback;
    protected PagedOrientationHandler mOrientationHandler;
    protected T mPageIndicator;
    int mPageIndicatorViewId;
    protected int[] mPageScrolls;
    protected int mPageSlop;
    private int mPageSnapAnimationDuration;
    protected int mPageSpacing;
    protected OverScroller mScroller;
    private int[] mTmpIntPair;
    private float mTotalMotion;
    protected int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    protected interface ComputePageScrollsLogic {
        boolean shouldIncludeView(View view);
    }

    /* access modifiers changed from: protected */
    public boolean canAnnouncePageDescription() {
        return true;
    }

    /* access modifiers changed from: protected */
    public int computeMinScroll() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getChildGap(int i, int i2) {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getPanelCount() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public boolean isPageOrderFlipped() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isSignificantMove(float f, int i) {
        return f > ((float) i) * 0.4f;
    }

    /* access modifiers changed from: protected */
    public void onEdgeAbsorbingScroll() {
    }

    /* access modifiers changed from: protected */
    public void onNotSnappingToPageInFreeScroll() {
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
    }

    /* access modifiers changed from: protected */
    public void onScrollOverPageChanged() {
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    static /* synthetic */ boolean lambda$static$0(View view) {
        return view.getVisibility() != 8;
    }

    public PagedView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PagedView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFreeScroll = false;
        this.mFirstLayout = true;
        this.mNextPage = -1;
        this.mPageSpacing = 0;
        this.mOrientationHandler = PagedOrientationHandler.PORTRAIT;
        this.mOnPageScrollsInitializedCallbacks = new ArrayList<>();
        this.mPageScrolls = null;
        this.mAllowOverScroll = true;
        this.mActivePointerId = -1;
        this.mIsPageInTransition = false;
        this.mInsets = new Rect();
        this.mTmpIntPair = new int[2];
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PagedView, i, 0);
        this.mPageIndicatorViewId = obtainStyledAttributes.getResourceId(0, -1);
        obtainStyledAttributes.recycle();
        setHapticFeedbackEnabled(false);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mScroller = new OverScroller(context, Interpolators.SCROLL);
        this.mCurrentPage = 0;
        this.mCurrentScrollOverPage = 0;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mPageSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        updateVelocityValues();
        initEdgeEffect();
        setDefaultFocusHighlightEnabled(false);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void initEdgeEffect() {
        this.mEdgeGlowLeft = new EdgeEffectCompat(getContext());
        this.mEdgeGlowRight = new EdgeEffectCompat(getContext());
    }

    public void initParentViews(View view) {
        int i = this.mPageIndicatorViewId;
        if (i > -1) {
            T findViewById = view.findViewById(i);
            this.mPageIndicator = findViewById;
            ((PageIndicator) findViewById).setMarkersCount(getChildCount() / getPanelCount());
        }
    }

    public T getPageIndicator() {
        return this.mPageIndicator;
    }

    public int getCurrentPage() {
        return this.mCurrentPage;
    }

    public int getNextPage() {
        int i = this.mNextPage;
        return i != -1 ? i : this.mCurrentPage;
    }

    public int getPageCount() {
        return getChildCount();
    }

    public View getPageAt(int i) {
        return getChildAt(i);
    }

    /* access modifiers changed from: protected */
    public void updateCurrentPageScroll() {
        int i = this.mCurrentPage;
        int scrollForPage = (i < 0 || i >= getPageCount()) ? 0 : getScrollForPage(this.mCurrentPage) + this.mCurrentPageScrollDiff;
        this.mOrientationHandler.setPrimary(this, PagedOrientationHandler.VIEW_SCROLL_TO, scrollForPage);
        OverScroller overScroller = this.mScroller;
        overScroller.startScroll(overScroller.getCurrX(), 0, scrollForPage - this.mScroller.getCurrX(), 0);
        forceFinishScroller();
    }

    public void abortScrollerAnimation() {
        this.mEdgeGlowLeft.finish();
        this.mEdgeGlowRight.finish();
        abortScrollerAnimation(true);
    }

    private void abortScrollerAnimation(boolean z) {
        this.mScroller.abortAnimation();
        if (z) {
            this.mNextPage = -1;
            pageEndTransition();
        }
    }

    public void forceFinishScroller() {
        this.mScroller.forceFinished(true);
        this.mNextPage = -1;
        pageEndTransition();
    }

    private int validateNewPage(int i) {
        int boundToRange = Utilities.boundToRange(ensureWithinScrollBounds(i), 0, getPageCount() - 1);
        return getPanelCount() > 1 ? getLeftmostVisiblePageForIndex(boundToRange) : boundToRange;
    }

    public int getLeftmostVisiblePageForIndex(int i) {
        return i - (i % getPanelCount());
    }

    public IntSet getVisiblePageIndices() {
        return getPageIndices(this.mCurrentPage);
    }

    private IntSet getPageIndices(int i) {
        int leftmostVisiblePageForIndex = getLeftmostVisiblePageForIndex(i);
        IntSet intSet = new IntSet();
        int panelCount = getPanelCount();
        int pageCount = getPageCount();
        int i2 = leftmostVisiblePageForIndex;
        while (i2 < leftmostVisiblePageForIndex + panelCount && i2 < pageCount) {
            intSet.add(i2);
            i2++;
        }
        return intSet;
    }

    private IntSet getNeighbourPageIndices(int i) {
        int i2;
        int panelCount = getPanelCount();
        int nextPage = getNextPage();
        if (i == 17) {
            i2 = nextPage - panelCount;
        } else if (i != 66) {
            return new IntSet();
        } else {
            i2 = nextPage + panelCount;
        }
        int validateNewPage = validateNewPage(i2);
        if (validateNewPage == nextPage) {
            return new IntSet();
        }
        return getPageIndices(validateNewPage);
    }

    public void forEachVisiblePage(Consumer<View> consumer) {
        getVisiblePageIndices().forEach(new Consumer(consumer) {
            public final /* synthetic */ Consumer f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                PagedView.this.lambda$forEachVisiblePage$1$PagedView(this.f$1, (Integer) obj);
            }
        });
    }

    public /* synthetic */ void lambda$forEachVisiblePage$1$PagedView(Consumer consumer, Integer num) {
        View pageAt = getPageAt(num.intValue());
        if (pageAt != null) {
            consumer.accept(pageAt);
        }
    }

    public boolean isVisible(View view) {
        return isVisible(indexOfChild(view));
    }

    private boolean isVisible(int i) {
        return getLeftmostVisiblePageForIndex(i) == this.mCurrentPage;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0024  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int ensureWithinScrollBounds(int r5) {
        /*
            r4 = this;
            boolean r0 = r4.mIsRtl
            if (r0 != 0) goto L_0x0006
            r0 = 1
            goto L_0x0007
        L_0x0006:
            r0 = -1
        L_0x0007:
            int r1 = r4.getScrollForPage(r5)
        L_0x000b:
            int r2 = r4.mMinScroll
            java.lang.String r3 = "PagedView"
            if (r1 >= r2) goto L_0x0020
            int r5 = r5 + r0
            int r2 = r4.getScrollForPage(r5)
            if (r2 > r1) goto L_0x001e
            java.lang.String r1 = "validateNewPage: failed to find a page > mMinScrollX"
            android.util.Log.e(r3, r1)
            goto L_0x0031
        L_0x001e:
            r1 = r2
            goto L_0x000b
        L_0x0020:
            int r2 = r4.mMaxScroll
            if (r1 <= r2) goto L_0x0033
            int r5 = r5 - r0
            int r2 = r4.getScrollForPage(r5)
            if (r2 < r1) goto L_0x0031
            java.lang.String r0 = "validateNewPage: failed to find a page < mMaxScrollX"
            android.util.Log.e(r3, r0)
            goto L_0x0033
        L_0x0031:
            r1 = r2
            goto L_0x0020
        L_0x0033:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.PagedView.ensureWithinScrollBounds(int):int");
    }

    public void setCurrentPage(int i) {
        setCurrentPage(i, -1);
    }

    public void setCurrentPage(int i, int i2) {
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(true);
        }
        if (getChildCount() != 0) {
            if (i2 == -1) {
                i2 = this.mCurrentPage;
            }
            int validateNewPage = validateNewPage(i);
            this.mCurrentPage = validateNewPage;
            this.mCurrentScrollOverPage = validateNewPage;
            updateCurrentPageScroll();
            notifyPageSwitchListener(i2);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        T t = this.mPageIndicator;
        if (t != null) {
            ((PageIndicator) t).setActiveMarker(getNextPage());
        }
    }

    /* access modifiers changed from: protected */
    public void pageBeginTransition() {
        if (!this.mIsPageInTransition) {
            this.mIsPageInTransition = true;
            onPageBeginTransition();
        }
    }

    /* access modifiers changed from: protected */
    public void pageEndTransition() {
        if (this.mIsPageInTransition && !this.mIsBeingDragged && this.mScroller.isFinished()) {
            if (!isShown() || (this.mEdgeGlowLeft.isFinished() && this.mEdgeGlowRight.isFinished())) {
                this.mIsPageInTransition = false;
                onPageEndTransition();
            }
        }
    }

    public void onVisibilityAggregated(boolean z) {
        pageEndTransition();
        super.onVisibilityAggregated(z);
    }

    /* access modifiers changed from: protected */
    public boolean isPageInTransition() {
        return this.mIsPageInTransition;
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        this.mCurrentPageScrollDiff = 0;
        AccessibilityManagerCompat.sendScrollFinishedEventToTest(getContext());
        AccessibilityManagerCompat.sendCustomAccessibilityEvent(getPageAt(this.mCurrentPage), 8, (String) null);
        Runnable runnable = this.mOnPageTransitionEndCallback;
        if (runnable != null) {
            runnable.run();
            this.mOnPageTransitionEndCallback = null;
        }
    }

    public void setOnPageTransitionEndCallback(Runnable runnable) {
        if (this.mIsPageInTransition || runnable == null) {
            this.mOnPageTransitionEndCallback = runnable;
        } else {
            runnable.run();
        }
    }

    public void scrollTo(int i, int i2) {
        super.scrollTo(Utilities.boundToRange(i, this.mOrientationHandler.getPrimaryValue(this.mMinScroll, 0), this.mMaxScroll), Utilities.boundToRange(i2, this.mOrientationHandler.getPrimaryValue(0, this.mMinScroll), this.mMaxScroll));
    }

    private void sendScrollAccessibilityEvent() {
        if (AccessibilityManagerCompat.isObservedEventType(getContext(), 4096) && this.mCurrentPage != getNextPage()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(4096);
            obtain.setScrollable(true);
            obtain.setScrollX(getScrollX());
            obtain.setScrollY(getScrollY());
            this.mOrientationHandler.setMaxScroll(obtain, this.mMaxScroll);
            sendAccessibilityEventUnchecked(obtain);
        }
    }

    /* access modifiers changed from: protected */
    public void announcePageForAccessibility() {
        if (AccessibilityManagerCompat.isAccessibilityEnabled(getContext())) {
            announceForAccessibility(getCurrentPageDescription());
        }
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper() {
        if (this.mScroller.computeScrollOffset()) {
            int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
            int currX = this.mScroller.getCurrX();
            if (primaryScroll != currX) {
                this.mOrientationHandler.setPrimary(this, PagedOrientationHandler.VIEW_SCROLL_TO, this.mScroller.getCurrX());
            }
            if (this.mAllowOverScroll) {
                int i = this.mMinScroll;
                if (currX >= i || primaryScroll < i) {
                    int i2 = this.mMaxScroll;
                    if (currX > i2 && primaryScroll <= i2) {
                        this.mEdgeGlowRight.onAbsorb((int) this.mScroller.getCurrVelocity());
                        this.mScroller.abortAnimation();
                        onEdgeAbsorbingScroll();
                    }
                } else {
                    this.mEdgeGlowLeft.onAbsorb((int) this.mScroller.getCurrVelocity());
                    this.mScroller.abortAnimation();
                    onEdgeAbsorbingScroll();
                }
            }
            if (currX == this.mOrientationHandler.getPrimaryValue(this.mScroller.getFinalX(), this.mScroller.getFinalY()) && this.mEdgeGlowLeft.isFinished() && this.mEdgeGlowRight.isFinished()) {
                this.mScroller.abortAnimation();
            }
            invalidate();
            return true;
        } else if (this.mNextPage == -1) {
            return false;
        } else {
            sendScrollAccessibilityEvent();
            int i3 = this.mCurrentPage;
            int validateNewPage = validateNewPage(this.mNextPage);
            this.mCurrentPage = validateNewPage;
            this.mCurrentScrollOverPage = validateNewPage;
            this.mNextPage = -1;
            notifyPageSwitchListener(i3);
            if (!this.mIsBeingDragged) {
                pageEndTransition();
            }
            if (!canAnnouncePageDescription()) {
                return false;
            }
            announcePageForAccessibility();
            return false;
        }
    }

    public void computeScroll() {
        computeScrollHelper();
    }

    public int getExpectedHeight() {
        return getMeasuredHeight();
    }

    public int getNormalChildHeight() {
        return (((getExpectedHeight() - getPaddingTop()) - getPaddingBottom()) - this.mInsets.top) - this.mInsets.bottom;
    }

    public int getExpectedWidth() {
        return getMeasuredWidth();
    }

    public int getNormalChildWidth() {
        return (((getExpectedWidth() - getPaddingLeft()) - getPaddingRight()) - this.mInsets.left) - this.mInsets.right;
    }

    private void updateVelocityValues() {
        Resources resources = getResources();
        this.mFlingThresholdVelocity = resources.getDimensionPixelSize(R.dimen.fling_threshold_velocity);
        this.mEasyFlingThresholdVelocity = resources.getDimensionPixelSize(R.dimen.easy_fling_threshold_velocity);
        this.mMinFlingVelocity = resources.getDimensionPixelSize(R.dimen.min_fling_velocity);
        this.mMinSnapVelocity = resources.getDimensionPixelSize(R.dimen.min_page_snap_velocity);
        this.mPageSnapAnimationDuration = resources.getInteger(R.integer.config_pageSnapAnimationDuration);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateVelocityValues();
    }

    public void requestLayout() {
        this.mIsLayoutValid = false;
        super.requestLayout();
    }

    public void forceLayout() {
        this.mIsLayoutValid = false;
        super.forceLayout();
    }

    private int getPageWidthSize(int i) {
        return (((((i - this.mInsets.left) - this.mInsets.right) - getPaddingLeft()) - getPaddingRight()) / getPanelCount()) + getPaddingLeft() + getPaddingRight();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (getChildCount() == 0) {
            super.onMeasure(i, i2);
            return;
        }
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 0 || mode2 == 0) {
            super.onMeasure(i, i2);
        } else if (size <= 0 || size2 <= 0) {
            super.onMeasure(i, i2);
        } else {
            measureChildren(View.MeasureSpec.makeMeasureSpec(getPageWidthSize(size), BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec((size2 - this.mInsets.top) - this.mInsets.bottom, BasicMeasure.EXACTLY));
            setMeasuredDimension(size, size2);
        }
    }

    /* access modifiers changed from: protected */
    public boolean pageScrollsInitialized() {
        int[] iArr = this.mPageScrolls;
        return iArr != null && iArr.length == getChildCount();
    }

    public void runOnPageScrollsInitialized(Runnable runnable) {
        this.mOnPageScrollsInitializedCallbacks.add(runnable);
        if (pageScrollsInitialized()) {
            onPageScrollsInitialized();
        }
    }

    private void onPageScrollsInitialized() {
        Iterator<Runnable> it = this.mOnPageScrollsInitializedCallbacks.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        this.mOnPageScrollsInitializedCallbacks.clear();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        boolean z2;
        int i5;
        this.mIsLayoutValid = true;
        int childCount = getChildCount();
        int[] iArr = this.mPageScrolls;
        if (!pageScrollsInitialized()) {
            iArr = new int[childCount];
            z2 = true;
        } else {
            z2 = false;
        }
        boolean pageScrolls = getPageScrolls(iArr, true, SIMPLE_SCROLL_LOGIC) | z2;
        this.mPageScrolls = iArr;
        if (childCount == 0) {
            onPageScrollsInitialized();
            return;
        }
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition == null || !layoutTransition.isRunning()) {
            updateMinAndMaxScrollX();
        } else {
            layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
                public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                }

                public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                    if (!layoutTransition.isRunning()) {
                        layoutTransition.removeTransitionListener(this);
                        PagedView.this.updateMinAndMaxScrollX();
                    }
                }
            });
        }
        if (this.mFirstLayout && (i5 = this.mCurrentPage) >= 0 && i5 < childCount) {
            updateCurrentPageScroll();
            this.mFirstLayout = false;
        }
        if (this.mScroller.isFinished() && pageScrolls) {
            setCurrentPage(getNextPage());
        }
        onPageScrollsInitialized();
    }

    /* access modifiers changed from: protected */
    public boolean getPageScrolls(int[] iArr, boolean z, ComputePageScrollsLogic computePageScrollsLogic) {
        int childCount = getChildCount();
        boolean z2 = this.mIsRtl;
        int i = -1;
        int i2 = z2 ? -1 : childCount;
        if (!z2) {
            i = 1;
        }
        int centerForPage = this.mOrientationHandler.getCenterForPage(this, this.mInsets);
        int scrollOffsetStart = this.mOrientationHandler.getScrollOffsetStart(this, this.mInsets);
        int scrollOffsetEnd = this.mOrientationHandler.getScrollOffsetEnd(this, this.mInsets);
        int panelCount = getPanelCount();
        int i3 = scrollOffsetStart;
        boolean z3 = false;
        for (int i4 = z2 ? childCount - 1 : 0; i4 != i2; i4 += i) {
            View pageAt = getPageAt(i4);
            if (computePageScrollsLogic.shouldIncludeView(pageAt)) {
                PagedOrientationHandler.ChildBounds childBounds = this.mOrientationHandler.getChildBounds(pageAt, i3, centerForPage, z);
                int i5 = childBounds.primaryDimension;
                int i6 = this.mIsRtl ? childBounds.childPrimaryEnd - scrollOffsetEnd : i3 - scrollOffsetStart;
                if (iArr[i4] != i6) {
                    iArr[i4] = i6;
                    z3 = true;
                }
                i3 += i5 + getChildGap(i4, i4 + i);
                if (i4 % panelCount == (this.mIsRtl ? 0 : panelCount - 1)) {
                    i3 += this.mPageSpacing;
                }
            } else {
                boolean z4 = z;
            }
        }
        if (panelCount > 1) {
            for (int i7 = 0; i7 < childCount; i7++) {
                int i8 = iArr[getLeftmostVisiblePageForIndex(i7)];
                if (iArr[i7] != i8) {
                    iArr[i7] = i8;
                    z3 = true;
                }
            }
        }
        return z3;
    }

    /* access modifiers changed from: protected */
    public void updateMinAndMaxScrollX() {
        this.mMinScroll = computeMinScroll();
        this.mMaxScroll = computeMaxScroll();
    }

    /* access modifiers changed from: protected */
    public int computeMaxScroll() {
        int childCount = getChildCount();
        int i = 0;
        if (childCount <= 0) {
            return 0;
        }
        if (!this.mIsRtl) {
            i = childCount - 1;
        }
        return getScrollForPage(i);
    }

    public void setPageSpacing(int i) {
        this.mPageSpacing = i;
        requestLayout();
    }

    public int getPageSpacing() {
        return this.mPageSpacing;
    }

    private void dispatchPageCountChanged() {
        T t = this.mPageIndicator;
        if (t != null) {
            ((PageIndicator) t).setMarkersCount(getChildCount() / getPanelCount());
        }
        invalidate();
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        dispatchPageCountChanged();
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        runOnPageScrollsInitialized(new Runnable() {
            public final void run() {
                PagedView.this.lambda$onViewRemoved$2$PagedView();
            }
        });
        dispatchPageCountChanged();
    }

    public /* synthetic */ void lambda$onViewRemoved$2$PagedView() {
        int validateNewPage = validateNewPage(this.mCurrentPage);
        this.mCurrentPage = validateNewPage;
        this.mCurrentScrollOverPage = validateNewPage;
    }

    /* access modifiers changed from: protected */
    public int getChildOffset(int i) {
        if (i < 0 || i > getChildCount() - 1) {
            return 0;
        }
        return this.mOrientationHandler.getChildStart(getPageAt(i));
    }

    /* access modifiers changed from: protected */
    public int getChildVisibleSize(int i) {
        return this.mOrientationHandler.getMeasuredSize(getPageAt(i));
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        int indexOfChild = indexOfChild(view);
        if (isVisible(indexOfChild) && this.mScroller.isFinished()) {
            return false;
        }
        if (z) {
            setCurrentPage(indexOfChild);
            return true;
        }
        snapToPage(indexOfChild);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        int i2 = this.mNextPage;
        if (i2 == -1) {
            i2 = this.mCurrentPage;
        }
        View pageAt = getPageAt(i2);
        if (pageAt != null) {
            return pageAt.requestFocus(i, rect);
        }
        return false;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        if (super.dispatchUnhandledMove(view, i)) {
            return true;
        }
        if (this.mIsRtl) {
            if (i == 17) {
                i = 66;
            } else if (i == 66) {
                i = 17;
            }
        }
        int nextPage = getNextPage();
        int i2 = Integer.MAX_VALUE;
        Iterator<Integer> it = getNeighbourPageIndices(i).iterator();
        int i3 = -1;
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            int abs = Math.abs(intValue - nextPage);
            if (i2 > abs) {
                i3 = intValue;
                i2 = abs;
            }
        }
        if (i3 == -1) {
            return false;
        }
        View pageAt = getPageAt(i3);
        snapToPage(i3);
        pageAt.requestFocus(i);
        return true;
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (getDescendantFocusability() != 393216) {
            getPageIndices(getNextPage()).addAll(getNeighbourPageIndices(i)).forEach(new Consumer(arrayList, i, i2) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void accept(Object obj) {
                    PagedView.this.lambda$addFocusables$3$PagedView(this.f$1, this.f$2, this.f$3, (Integer) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addFocusables$3$PagedView(ArrayList arrayList, int i, int i2, Integer num) {
        getPageAt(num.intValue()).addFocusables(arrayList, i, i2);
    }

    public void focusableViewAvailable(View view) {
        View pageAt = getPageAt(this.mCurrentPage);
        View view2 = view;
        while (view2 != pageAt) {
            if (view2 != this && (view2.getParent() instanceof View)) {
                view2 = (View) view2.getParent();
            } else {
                return;
            }
        }
        super.focusableViewAvailable(view);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (z) {
            cancelCurrentPageLongPress();
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getChildCount() <= 0) {
            return false;
        }
        acquireVelocityTrackerAndAddMovement(motionEvent);
        int action = motionEvent.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        int i = action & 255;
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i == 6) {
                            onSecondaryPointerUp(motionEvent);
                            releaseVelocityTracker();
                        }
                    }
                } else if (this.mActivePointerId != -1) {
                    determineScrollingStart(motionEvent);
                }
            }
            resetTouchState();
        } else {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            this.mDownMotionX = x;
            this.mDownMotionY = y;
            float primaryDirection = this.mOrientationHandler.getPrimaryDirection(motionEvent, 0);
            this.mLastMotion = primaryDirection;
            this.mDownMotionPrimary = primaryDirection;
            this.mLastMotionRemainder = 0.0f;
            this.mTotalMotion = 0.0f;
            this.mAllowEasyFling = false;
            this.mActivePointerId = motionEvent.getPointerId(0);
            updateIsBeingDraggedOnTouchDown(motionEvent);
        }
        return this.mIsBeingDragged;
    }

    /* access modifiers changed from: protected */
    public void updateIsBeingDraggedOnTouchDown(MotionEvent motionEvent) {
        boolean z = false;
        if (this.mScroller.isFinished() || Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) < this.mPageSlop / 3) {
            this.mIsBeingDragged = false;
            if (!this.mScroller.isFinished() && !this.mFreeScroll) {
                setCurrentPage(getNextPage());
                pageEndTransition();
            }
            if (!this.mEdgeGlowLeft.isFinished() || !this.mEdgeGlowRight.isFinished()) {
                z = true;
            }
            this.mIsBeingDragged = z;
        } else {
            this.mIsBeingDragged = true;
        }
        float secondaryValue = this.mOrientationHandler.getSecondaryValue(motionEvent.getX(), motionEvent.getY()) / ((float) this.mOrientationHandler.getSecondaryValue(getWidth(), getHeight()));
        if (!this.mEdgeGlowLeft.isFinished()) {
            this.mEdgeGlowLeft.onPullDistance(0.0f, 1.0f - secondaryValue);
        }
        if (!this.mEdgeGlowRight.isFinished()) {
            this.mEdgeGlowRight.onPullDistance(0.0f, secondaryValue);
        }
    }

    public boolean isHandlingTouch() {
        return this.mIsBeingDragged;
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent) {
        determineScrollingStart(motionEvent, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent, float f) {
        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
        if (findPointerIndex != -1) {
            float primaryDirection = this.mOrientationHandler.getPrimaryDirection(motionEvent, findPointerIndex);
            if (((int) Math.abs(primaryDirection - this.mLastMotion)) > Math.round(f * ((float) this.mTouchSlop)) || motionEvent.getAction() == 254) {
                this.mIsBeingDragged = true;
                this.mTotalMotion += Math.abs(this.mLastMotion - primaryDirection);
                this.mLastMotion = primaryDirection;
                this.mLastMotionRemainder = 0.0f;
                pageBeginTransition();
                requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCurrentPageLongPress() {
        forEachVisiblePage($$Lambda$PagedView$2CBM4xFhjXDSRmQKyIvUNkjhg0.INSTANCE);
    }

    /* access modifiers changed from: protected */
    public float getScrollProgress(int i, View view, int i2) {
        int i3;
        int scrollForPage = i - (getScrollForPage(i2) + (getMeasuredWidth() / 2));
        int panelCount = getPanelCount();
        int childCount = getChildCount();
        int i4 = i2 + panelCount;
        if ((scrollForPage < 0 && !this.mIsRtl) || (scrollForPage > 0 && this.mIsRtl)) {
            i4 = i2 - panelCount;
        }
        if (i4 < 0 || i4 > childCount - 1) {
            i3 = (view.getMeasuredWidth() + this.mPageSpacing) * panelCount;
        } else {
            i3 = Math.abs(getScrollForPage(i4) - getScrollForPage(i2));
        }
        return Math.max(Math.min(((float) scrollForPage) / (((float) i3) * 1.0f), 1.0f), -1.0f);
    }

    public int getScrollForPage(int i) {
        int[] iArr = this.mPageScrolls;
        if (iArr == null || i >= iArr.length || i < 0) {
            return 0;
        }
        return iArr[i];
    }

    public int getLayoutTransitionOffsetForPage(int i) {
        if (!pageScrollsInitialized() || i >= this.mPageScrolls.length || i < 0) {
            return 0;
        }
        return (int) (getChildAt(i).getX() - ((float) (this.mPageScrolls[i] + (this.mIsRtl ? getPaddingRight() : getPaddingLeft()))));
    }

    public void setEnableFreeScroll(boolean z) {
        boolean z2 = this.mFreeScroll;
        if (z2 != z) {
            this.mFreeScroll = z;
            if (z) {
                setCurrentPage(getNextPage());
            } else if (z2 && getScrollForPage(getNextPage()) != getScrollX()) {
                snapToPage(getNextPage());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setEnableOverscroll(boolean z) {
        this.mAllowOverScroll = z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        float f;
        float f2;
        float onPullDistance;
        MotionEvent motionEvent2 = motionEvent;
        boolean z = false;
        if (getChildCount() <= 0) {
            return false;
        }
        acquireVelocityTrackerAndAddMovement(motionEvent);
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            updateIsBeingDraggedOnTouchDown(motionEvent);
            if (!this.mScroller.isFinished()) {
                abortScrollerAnimation(false);
            }
            this.mDownMotionX = motionEvent.getX();
            this.mDownMotionY = motionEvent.getY();
            float primaryDirection = this.mOrientationHandler.getPrimaryDirection(motionEvent2, 0);
            this.mLastMotion = primaryDirection;
            this.mDownMotionPrimary = primaryDirection;
            this.mLastMotionRemainder = 0.0f;
            this.mTotalMotion = 0.0f;
            this.mAllowEasyFling = false;
            this.mActivePointerId = motionEvent2.getPointerId(0);
            if (this.mIsBeingDragged) {
                pageBeginTransition();
            }
        } else if (action == 1) {
            if (this.mIsBeingDragged) {
                int findPointerIndex = motionEvent2.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex == -1) {
                    return true;
                }
                float primaryDirection2 = this.mOrientationHandler.getPrimaryDirection(motionEvent2, findPointerIndex);
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                int primaryVelocity = (int) this.mOrientationHandler.getPrimaryVelocity(velocityTracker, this.mActivePointerId);
                float f3 = primaryDirection2 - this.mDownMotionPrimary;
                int measuredSize = (int) (((float) this.mOrientationHandler.getMeasuredSize(getPageAt(this.mCurrentPage))) * this.mOrientationHandler.getPrimaryScale(this));
                boolean isSignificantMove = isSignificantMove(Math.abs(f3), measuredSize);
                float abs = this.mTotalMotion + Math.abs((this.mLastMotion + this.mLastMotionRemainder) - primaryDirection2);
                this.mTotalMotion = abs;
                boolean z2 = (this.mAllowEasyFling || (abs > ((float) this.mPageSlop) ? 1 : (abs == ((float) this.mPageSlop) ? 0 : -1)) > 0) && shouldFlingForVelocity(primaryVelocity);
                boolean z3 = this.mIsRtl;
                boolean z4 = !z3 ? f3 < 0.0f : f3 > 0.0f;
                boolean z5 = !z3 ? primaryVelocity < 0 : primaryVelocity > 0;
                if (!this.mFreeScroll) {
                    if (Math.abs(f3) > ((float) measuredSize) * RETURN_TO_ORIGINAL_PAGE_THRESHOLD && Math.signum((float) primaryVelocity) != Math.signum(f3) && z2) {
                        z = true;
                    }
                    if (((isSignificantMove && !z4 && !z2) || (z2 && !z5)) && (i = this.mCurrentPage) > 0) {
                        if (!z) {
                            i -= getPanelCount();
                        }
                        snapToPageWithVelocity(i, primaryVelocity);
                    } else if (((!isSignificantMove || !z4 || z2) && (!z2 || !z5)) || this.mCurrentPage >= getChildCount() - 1) {
                        snapToDestination();
                    } else {
                        snapToPageWithVelocity(z ? this.mCurrentPage : this.mCurrentPage + getPanelCount(), primaryVelocity);
                    }
                } else {
                    if (!this.mScroller.isFinished()) {
                        abortScrollerAnimation(true);
                    }
                    int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
                    int i2 = this.mMaxScroll;
                    int i3 = this.mMinScroll;
                    if ((primaryScroll < i2 || (!z5 && z2)) && (primaryScroll > i3 || (z5 && z2))) {
                        this.mScroller.fling(primaryScroll, 0, -primaryVelocity, 0, i3, i2, 0, 0, Math.round(((float) getWidth()) * 0.5f * 0.07f), 0);
                        this.mNextPage = getDestinationPage(this.mScroller.getFinalX());
                        onNotSnappingToPageInFreeScroll();
                    } else {
                        this.mScroller.springBack(primaryScroll, 0, i3, i2, 0, 0);
                        this.mNextPage = getDestinationPage();
                    }
                    invalidate();
                }
            }
            this.mEdgeGlowLeft.onRelease();
            this.mEdgeGlowRight.onRelease();
            resetTouchState();
        } else if (action != 2) {
            if (action == 3) {
                if (this.mIsBeingDragged) {
                    snapToDestination();
                }
                this.mEdgeGlowLeft.onRelease();
                this.mEdgeGlowRight.onRelease();
                resetTouchState();
            } else if (action == 6) {
                onSecondaryPointerUp(motionEvent);
                releaseVelocityTracker();
            } else if (action == 254) {
                determineScrollingStart(motionEvent);
                this.mAllowEasyFling = true;
            }
        } else if (this.mIsBeingDragged) {
            int findPointerIndex2 = motionEvent2.findPointerIndex(this.mActivePointerId);
            if (findPointerIndex2 == -1) {
                return true;
            }
            float primaryScroll2 = (float) this.mOrientationHandler.getPrimaryScroll(this);
            float x = motionEvent2.getX(findPointerIndex2);
            float y = motionEvent2.getY(findPointerIndex2);
            float primaryValue = this.mOrientationHandler.getPrimaryValue(x, y);
            float f4 = (this.mLastMotion + this.mLastMotionRemainder) - primaryValue;
            int width = getWidth();
            int height = getHeight();
            int primaryValue2 = this.mOrientationHandler.getPrimaryValue(width, height);
            float secondaryValue = this.mOrientationHandler.getSecondaryValue(x, y) / ((float) this.mOrientationHandler.getSecondaryValue(width, height));
            this.mTotalMotion += Math.abs(f4);
            if (this.mAllowOverScroll) {
                if (f4 < 0.0f && this.mEdgeGlowRight.getDistance() != 0.0f) {
                    f2 = (float) primaryValue2;
                    onPullDistance = this.mEdgeGlowRight.onPullDistance(f4 / f2, secondaryValue);
                } else if (f4 <= 0.0f || this.mEdgeGlowLeft.getDistance() == 0.0f) {
                    f = 0.0f;
                    f4 -= f;
                } else {
                    f2 = (float) (-primaryValue2);
                    onPullDistance = this.mEdgeGlowLeft.onPullDistance((-f4) / ((float) primaryValue2), 1.0f - secondaryValue);
                }
                f = f2 * onPullDistance;
                f4 -= f;
            }
            float primaryScale = f4 / this.mOrientationHandler.getPrimaryScale(this);
            this.mLastMotion = primaryValue;
            int i4 = (int) primaryScale;
            this.mLastMotionRemainder = primaryScale - ((float) i4);
            if (primaryScale != 0.0f) {
                this.mOrientationHandler.setPrimary(this, PagedOrientationHandler.VIEW_SCROLL_BY, i4);
                if (this.mAllowOverScroll) {
                    float f5 = primaryScroll2 + primaryScale;
                    if (f5 < ((float) this.mMinScroll)) {
                        this.mEdgeGlowLeft.onPullDistance((-primaryScale) / ((float) primaryValue2), 1.0f - secondaryValue);
                        if (!this.mEdgeGlowRight.isFinished()) {
                            this.mEdgeGlowRight.onRelease();
                        }
                    } else if (f5 > ((float) this.mMaxScroll)) {
                        this.mEdgeGlowRight.onPullDistance(primaryScale / ((float) primaryValue2), secondaryValue);
                        if (!this.mEdgeGlowLeft.isFinished()) {
                            this.mEdgeGlowLeft.onRelease();
                        }
                    }
                    if (!this.mEdgeGlowLeft.isFinished() || !this.mEdgeGlowRight.isFinished()) {
                        postInvalidateOnAnimation();
                    }
                }
            } else {
                awakenScrollBars();
            }
        } else {
            determineScrollingStart(motionEvent);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldFlingForVelocity(int i) {
        return ((float) Math.abs(i)) > ((float) (this.mAllowEasyFling ? this.mEasyFlingThresholdVelocity : this.mFlingThresholdVelocity));
    }

    /* access modifiers changed from: protected */
    public void resetTouchState() {
        releaseVelocityTracker();
        this.mIsBeingDragged = false;
        this.mActivePointerId = -1;
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        float f;
        float f2;
        if ((motionEvent.getSource() & 2) != 0 && motionEvent.getAction() == 8) {
            if ((motionEvent.getMetaState() & 1) != 0) {
                f = motionEvent.getAxisValue(9);
                f2 = 0.0f;
            } else {
                f2 = -motionEvent.getAxisValue(9);
                f = motionEvent.getAxisValue(10);
            }
            boolean z = false;
            if (!canScroll(Math.abs(f2), Math.abs(f))) {
                return false;
            }
            int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
            if (!(i == 0 && f2 == 0.0f)) {
                if (!this.mIsRtl ? i > 0 || f2 > 0.0f : f < 0.0f || f2 < 0.0f) {
                    z = true;
                }
                if (z) {
                    scrollRight();
                } else {
                    scrollLeft();
                }
                return true;
            }
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public boolean canScroll(float f, float f2) {
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(getContext());
        return activityContext == null || AbstractFloatingView.getTopOpenView(activityContext) == null;
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
    }

    private void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            int i = actionIndex == 0 ? 1 : 0;
            float primaryDirection = this.mOrientationHandler.getPrimaryDirection(motionEvent, i);
            this.mDownMotionPrimary = primaryDirection;
            this.mLastMotion = primaryDirection;
            this.mLastMotionRemainder = 0.0f;
            this.mActivePointerId = motionEvent.getPointerId(i);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        int nextPage = getNextPage();
        if (nextPage != this.mCurrentPage) {
            setCurrentPage(nextPage);
        }
        int indexOfChild = indexOfChild(view);
        if (indexOfChild >= 0 && !isVisible(indexOfChild) && !isInTouchMode()) {
            snapToPage(indexOfChild);
        }
    }

    public int getDestinationPage() {
        return getDestinationPage(this.mOrientationHandler.getPrimaryScroll(this));
    }

    /* access modifiers changed from: protected */
    public int getDestinationPage(int i) {
        return getPageNearestToCenterOfScreen(i);
    }

    public int getPageNearestToCenterOfScreen() {
        return getPageNearestToCenterOfScreen(this.mOrientationHandler.getPrimaryScroll(this));
    }

    private int getPageNearestToCenterOfScreen(int i) {
        int screenCenter = getScreenCenter(i);
        int childCount = getChildCount();
        int i2 = Integer.MAX_VALUE;
        int i3 = -1;
        for (int i4 = 0; i4 < childCount; i4++) {
            int abs = Math.abs(getDisplacementFromScreenCenter(i4, screenCenter));
            if (abs < i2) {
                i3 = i4;
                i2 = abs;
            }
        }
        return i3;
    }

    private int getDisplacementFromScreenCenter(int i, int i2) {
        return (getChildOffset(i) + (Math.round((float) getChildVisibleSize(i)) / 2)) - i2;
    }

    /* access modifiers changed from: protected */
    public int getDisplacementFromScreenCenter(int i) {
        return getDisplacementFromScreenCenter(i, getScreenCenter(this.mOrientationHandler.getPrimaryScroll(this)));
    }

    /* access modifiers changed from: protected */
    public int getScreenCenter(int i) {
        float primaryScale = this.mOrientationHandler.getPrimaryScale(this);
        float primaryValue = this.mOrientationHandler.getPrimaryValue(getPivotX(), getPivotY());
        return Math.round(((float) i) + (((((float) this.mOrientationHandler.getMeasuredSize(this)) / 2.0f) - primaryValue) / primaryScale) + primaryValue);
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        snapToPage(getDestinationPage(), this.mPageSnapAnimationDuration);
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
    }

    /* access modifiers changed from: protected */
    public boolean snapToPageWithVelocity(int i, int i2) {
        int validateNewPage = validateNewPage(i);
        int measuredSize = this.mOrientationHandler.getMeasuredSize(this) / 2;
        int scrollForPage = getScrollForPage(validateNewPage) - this.mOrientationHandler.getPrimaryScroll(this);
        if (Math.abs(i2) < this.mMinFlingVelocity) {
            return snapToPage(validateNewPage, this.mPageSnapAnimationDuration);
        }
        float f = (float) measuredSize;
        return snapToPage(validateNewPage, scrollForPage, Math.round(Math.abs((f + (distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) Math.abs(scrollForPage)) * 1.0f) / ((float) (measuredSize * 2)))) * f)) / ((float) Math.max(this.mMinSnapVelocity, Math.abs(i2)))) * 1000.0f) * 4);
    }

    public boolean snapToPage(int i) {
        return snapToPage(i, this.mPageSnapAnimationDuration);
    }

    public boolean snapToPageImmediately(int i) {
        return snapToPage(i, this.mPageSnapAnimationDuration, true);
    }

    public boolean snapToPage(int i, int i2) {
        return snapToPage(i, i2, false);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPage(int i, int i2, boolean z) {
        int validateNewPage = validateNewPage(i);
        return snapToPage(validateNewPage, getScrollForPage(validateNewPage) - this.mOrientationHandler.getPrimaryScroll(this), i2, z);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPage(int i, int i2, int i3) {
        return snapToPage(i, i2, i3, false);
    }

    /* access modifiers changed from: protected */
    public boolean snapToPage(int i, int i2, int i3, boolean z) {
        int i4;
        if (this.mFirstLayout) {
            setCurrentPage(i);
            return false;
        }
        this.mNextPage = validateNewPage(i);
        awakenScrollBars(i3);
        if (z) {
            i4 = 0;
        } else {
            if (i3 == 0) {
                i3 = Math.abs(i2);
            }
            i4 = i3;
        }
        if (i4 != 0) {
            pageBeginTransition();
        }
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }
        this.mScroller.startScroll(this.mOrientationHandler.getPrimaryScroll(this), 0, i2, 0, i4);
        updatePageIndicator();
        if (z) {
            computeScroll();
            pageEndTransition();
        }
        invalidate();
        if (Math.abs(i2) > 0) {
            return true;
        }
        return false;
    }

    public boolean scrollLeft() {
        if (getNextPage() <= 0) {
            return this.mAllowOverScroll;
        }
        snapToPage(getNextPage() - getPanelCount());
        return true;
    }

    public boolean scrollRight() {
        if (getNextPage() >= getChildCount() - 1) {
            return this.mAllowOverScroll;
        }
        snapToPage(getNextPage() + getPanelCount());
        return true;
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        int destinationPage;
        if (!this.mScroller.isFinished() && (destinationPage = getDestinationPage()) >= 0 && destinationPage != this.mCurrentScrollOverPage) {
            this.mCurrentScrollOverPage = destinationPage;
            onScrollOverPageChanged();
        }
    }

    public CharSequence getAccessibilityClassName() {
        return ScrollView.class.getName();
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction2;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction3;
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction4;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        boolean isPageOrderFlipped = isPageOrderFlipped();
        accessibilityNodeInfo.setScrollable(getPageCount() > 0);
        int primaryScroll = this.mOrientationHandler.getPrimaryScroll(this);
        if (getCurrentPage() < getPageCount() - getPanelCount() || (getCurrentPage() == getPageCount() - getPanelCount() && primaryScroll != getScrollForPage(getPageCount() - getPanelCount()))) {
            if (isPageOrderFlipped) {
                accessibilityAction3 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD;
            } else {
                accessibilityAction3 = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD;
            }
            accessibilityNodeInfo.addAction(accessibilityAction3);
            if (this.mIsRtl) {
                accessibilityAction4 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_LEFT;
            } else {
                accessibilityAction4 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_RIGHT;
            }
            accessibilityNodeInfo.addAction(accessibilityAction4);
        }
        if (getCurrentPage() > 0 || (getCurrentPage() == 0 && primaryScroll != getScrollForPage(0))) {
            if (isPageOrderFlipped) {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD;
            } else {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD;
            }
            accessibilityNodeInfo.addAction(accessibilityAction);
            if (this.mIsRtl) {
                accessibilityAction2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_RIGHT;
            } else {
                accessibilityAction2 = AccessibilityNodeInfo.AccessibilityAction.ACTION_PAGE_LEFT;
            }
            accessibilityNodeInfo.addAction(accessibilityAction2);
        }
        accessibilityNodeInfo.setLongClickable(false);
        accessibilityNodeInfo.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
    }

    public void sendAccessibilityEvent(int i) {
        if (i != 4096) {
            super.sendAccessibilityEvent(i);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        boolean z = true;
        if (!this.mAllowOverScroll && getPageCount() <= 1) {
            z = false;
        }
        accessibilityEvent.setScrollable(z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0043 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0053 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAccessibilityAction(int r3, android.os.Bundle r4) {
        /*
            r2 = this;
            boolean r4 = super.performAccessibilityAction(r3, r4)
            r0 = 1
            if (r4 == 0) goto L_0x0008
            return r0
        L_0x0008:
            boolean r4 = r2.isPageOrderFlipped()
            r1 = 4096(0x1000, float:5.74E-42)
            if (r3 == r1) goto L_0x0044
            r1 = 8192(0x2000, float:1.14794E-41)
            if (r3 == r1) goto L_0x0034
            switch(r3) {
                case 16908360: goto L_0x0026;
                case 16908361: goto L_0x0018;
                default: goto L_0x0017;
            }
        L_0x0017:
            goto L_0x0054
        L_0x0018:
            boolean r3 = r2.mIsRtl
            if (r3 != 0) goto L_0x0021
            boolean r3 = r2.scrollRight()
            return r3
        L_0x0021:
            boolean r3 = r2.scrollLeft()
            return r3
        L_0x0026:
            boolean r3 = r2.mIsRtl
            if (r3 != 0) goto L_0x002f
            boolean r3 = r2.scrollLeft()
            return r3
        L_0x002f:
            boolean r3 = r2.scrollRight()
            return r3
        L_0x0034:
            if (r4 == 0) goto L_0x003d
            boolean r3 = r2.scrollRight()
            if (r3 == 0) goto L_0x0054
            goto L_0x0043
        L_0x003d:
            boolean r3 = r2.scrollLeft()
            if (r3 == 0) goto L_0x0054
        L_0x0043:
            return r0
        L_0x0044:
            if (r4 == 0) goto L_0x004d
            boolean r3 = r2.scrollLeft()
            if (r3 == 0) goto L_0x0054
            goto L_0x0053
        L_0x004d:
            boolean r3 = r2.scrollRight()
            if (r3 == 0) goto L_0x0054
        L_0x0053:
            return r0
        L_0x0054:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.PagedView.performAccessibilityAction(int, android.os.Bundle):boolean");
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return getContext().getString(R.string.default_scroll_format, new Object[]{Integer.valueOf(getNextPage() + 1), Integer.valueOf(getChildCount())});
    }

    /* access modifiers changed from: protected */
    public float getDownMotionX() {
        return this.mDownMotionX;
    }

    /* access modifiers changed from: protected */
    public float getDownMotionY() {
        return this.mDownMotionY;
    }

    public int[] getVisibleChildrenRange() {
        float f = 0.0f;
        float measuredWidth = ((float) getMeasuredWidth()) + 0.0f;
        float scaleX = getScaleX();
        if (scaleX < 1.0f && scaleX > 0.0f) {
            float measuredWidth2 = (float) (getMeasuredWidth() / 2);
            f = measuredWidth2 - ((measuredWidth2 - 0.0f) / scaleX);
            measuredWidth = ((measuredWidth - measuredWidth2) / scaleX) + measuredWidth2;
        }
        int childCount = getChildCount();
        int i = -1;
        int i2 = -1;
        for (int i3 = 0; i3 < childCount; i3++) {
            View pageAt = getPageAt(i3);
            float left = (((float) pageAt.getLeft()) + pageAt.getTranslationX()) - ((float) getScrollX());
            if (left <= measuredWidth && left + ((float) pageAt.getMeasuredWidth()) >= f) {
                if (i == -1) {
                    i = i3;
                }
                i2 = i3;
            }
        }
        int[] iArr = this.mTmpIntPair;
        iArr[0] = i;
        iArr[1] = i2;
        return iArr;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawEdgeEffect(canvas);
        pageEndTransition();
    }

    /* access modifiers changed from: protected */
    public void drawEdgeEffect(Canvas canvas) {
        if (!this.mAllowOverScroll) {
            return;
        }
        if (!this.mEdgeGlowRight.isFinished() || !this.mEdgeGlowLeft.isFinished()) {
            int width = getWidth();
            int height = getHeight();
            if (!this.mEdgeGlowLeft.isFinished()) {
                int save = canvas.save();
                canvas.rotate(-90.0f);
                canvas.translate((float) (-height), (float) Math.min(this.mMinScroll, getScrollX()));
                this.mEdgeGlowLeft.setSize(height, width);
                if (this.mEdgeGlowLeft.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(save);
            }
            if (!this.mEdgeGlowRight.isFinished()) {
                int save2 = canvas.save();
                float f = (float) width;
                canvas.rotate(90.0f, f, 0.0f);
                canvas.translate(f, (float) (-Math.max(this.mMaxScroll, getScrollX())));
                this.mEdgeGlowRight.setSize(height, width);
                if (this.mEdgeGlowRight.draw(canvas)) {
                    postInvalidateOnAnimation();
                }
                canvas.restoreToCount(save2);
            }
        }
    }
}
