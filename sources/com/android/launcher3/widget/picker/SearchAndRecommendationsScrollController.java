package com.android.launcher3.widget.picker;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.widget.picker.WidgetsSpaceViewHolderBinder;
import com.android.launcher3.widget.picker.search.WidgetsSearchBar;

final class SearchAndRecommendationsScrollController implements RecyclerView.OnChildAttachStateChangeListener {
    private static final MotionEventProxyMethod INTERCEPT_PROXY = $$Lambda$SearchAndRecommendationsScrollController$mTv8BJ5LYS3dqX5PEeTERLatUI.INSTANCE;
    private static final FloatProperty<SearchAndRecommendationsScrollController> SCROLL_OFFSET = new FloatProperty<SearchAndRecommendationsScrollController>("scrollAnimOffset") {
        public /* bridge */ /* synthetic */ void set(Object obj, Object obj2) {
            super.set(obj, (Float) obj2);
        }

        public void setValue(SearchAndRecommendationsScrollController searchAndRecommendationsScrollController, float f) {
            float unused = searchAndRecommendationsScrollController.mScrollOffset = f;
            searchAndRecommendationsScrollController.updateHeaderScroll();
        }

        public Float get(SearchAndRecommendationsScrollController searchAndRecommendationsScrollController) {
            return Float.valueOf(searchAndRecommendationsScrollController.mScrollOffset);
        }
    };
    private static final MotionEventProxyMethod TOUCH_PROXY = $$Lambda$SearchAndRecommendationsScrollController$YZcYjcoyq6jX8j1j09EPrKUlgAo.INSTANCE;
    final SearchAndRecommendationsView mContainer;
    private WidgetsSpaceViewHolderBinder.EmptySpaceView mCurrentEmptySpaceView;
    private WidgetsRecyclerView mCurrentRecyclerView;
    private int mHeaderHeight;
    final TextView mHeaderTitle;
    private float mLastScroll = 0.0f;
    private Animator mOffsetAnimator;
    final WidgetsRecommendationTableLayout mRecommendedWidgetsTable;
    /* access modifiers changed from: private */
    public float mScrollOffset = 0.0f;
    final WidgetsSearchBar mSearchBar;
    final View mSearchBarContainer;
    private boolean mShouldForwardToRecyclerView = false;
    final View mTabBar;

    private interface MotionEventProxyMethod {
        boolean proxyEvent(ViewGroup viewGroup, MotionEvent motionEvent);
    }

    SearchAndRecommendationsScrollController(SearchAndRecommendationsView searchAndRecommendationsView) {
        this.mContainer = searchAndRecommendationsView;
        this.mSearchBarContainer = searchAndRecommendationsView.findViewById(R.id.search_bar_container);
        this.mSearchBar = (WidgetsSearchBar) searchAndRecommendationsView.findViewById(R.id.widgets_search_bar);
        this.mHeaderTitle = (TextView) searchAndRecommendationsView.findViewById(R.id.title);
        this.mRecommendedWidgetsTable = (WidgetsRecommendationTableLayout) searchAndRecommendationsView.findViewById(R.id.recommended_widget_table);
        this.mTabBar = searchAndRecommendationsView.findViewById(R.id.tabs);
        searchAndRecommendationsView.setSearchAndRecommendationScrollController(this);
    }

    public void setCurrentRecyclerView(WidgetsRecyclerView widgetsRecyclerView) {
        WidgetsRecyclerView widgetsRecyclerView2 = this.mCurrentRecyclerView;
        boolean z = widgetsRecyclerView2 != null;
        if (widgetsRecyclerView2 != null) {
            widgetsRecyclerView2.removeOnChildAttachStateChangeListener(this);
        }
        this.mCurrentRecyclerView = widgetsRecyclerView;
        widgetsRecyclerView.addOnChildAttachStateChangeListener(this);
        findCurrentEmptyView();
        reset(z);
    }

    public int getHeaderHeight() {
        return this.mHeaderHeight;
    }

    /* access modifiers changed from: private */
    public void updateHeaderScroll() {
        float currentScroll = getCurrentScroll();
        this.mLastScroll = currentScroll;
        this.mHeaderTitle.setTranslationY(currentScroll);
        this.mRecommendedWidgetsTable.setTranslationY(this.mLastScroll);
        this.mSearchBarContainer.setTranslationY(Math.max(this.mLastScroll, (float) (-this.mSearchBarContainer.getTop())));
        View view = this.mTabBar;
        if (view != null) {
            this.mTabBar.setTranslationY(Math.max(this.mLastScroll, (float) ((-view.getTop()) + this.mSearchBarContainer.getHeight())));
        }
    }

    private float getCurrentScroll() {
        float f = this.mScrollOffset;
        WidgetsSpaceViewHolderBinder.EmptySpaceView emptySpaceView = this.mCurrentEmptySpaceView;
        return f + (emptySpaceView == null ? 0.0f : emptySpaceView.getY());
    }

    public boolean updateHeaderHeight() {
        boolean z;
        int measuredHeight = this.mContainer.getMeasuredHeight();
        if (measuredHeight != this.mHeaderHeight) {
            this.mHeaderHeight = measuredHeight;
            z = true;
        } else {
            z = false;
        }
        WidgetsSpaceViewHolderBinder.EmptySpaceView emptySpaceView = this.mCurrentEmptySpaceView;
        if (emptySpaceView == null || !emptySpaceView.setFixedHeight(this.mHeaderHeight)) {
            return z;
        }
        return true;
    }

    public void reset(boolean z) {
        Animator animator = this.mOffsetAnimator;
        if (animator != null) {
            animator.cancel();
            this.mOffsetAnimator = null;
        }
        this.mScrollOffset = 0.0f;
        if (!z) {
            updateHeaderScroll();
            return;
        }
        float currentScroll = this.mLastScroll - getCurrentScroll();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, SCROLL_OFFSET, new float[]{currentScroll, 0.0f});
        this.mOffsetAnimator = ofFloat;
        ofFloat.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
            public final void run() {
                SearchAndRecommendationsScrollController.this.lambda$reset$0$SearchAndRecommendationsScrollController();
            }
        }));
        this.mOffsetAnimator.start();
    }

    public /* synthetic */ void lambda$reset$0$SearchAndRecommendationsScrollController() {
        this.mOffsetAnimator = null;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean proxyMotionEvent = proxyMotionEvent(motionEvent, INTERCEPT_PROXY);
        this.mShouldForwardToRecyclerView = proxyMotionEvent;
        return proxyMotionEvent;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mShouldForwardToRecyclerView && proxyMotionEvent(motionEvent, TOUCH_PROXY);
    }

    private boolean proxyMotionEvent(MotionEvent motionEvent, MotionEventProxyMethod motionEventProxyMethod) {
        float left = (float) (this.mCurrentRecyclerView.getLeft() - this.mContainer.getLeft());
        float top = (float) (this.mCurrentRecyclerView.getTop() - this.mContainer.getTop());
        motionEvent.offsetLocation(left, top);
        try {
            return motionEventProxyMethod.proxyEvent(this.mCurrentRecyclerView, motionEvent);
        } finally {
            motionEvent.offsetLocation(-left, -top);
        }
    }

    public void onChildViewAttachedToWindow(View view) {
        if (view instanceof WidgetsSpaceViewHolderBinder.EmptySpaceView) {
            findCurrentEmptyView();
        }
    }

    public void onChildViewDetachedFromWindow(View view) {
        if (view == this.mCurrentEmptySpaceView) {
            findCurrentEmptyView();
        }
    }

    private void findCurrentEmptyView() {
        WidgetsSpaceViewHolderBinder.EmptySpaceView emptySpaceView = this.mCurrentEmptySpaceView;
        if (emptySpaceView != null) {
            emptySpaceView.setOnYChangeCallback((Runnable) null);
            this.mCurrentEmptySpaceView = null;
        }
        int childCount = this.mCurrentRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mCurrentRecyclerView.getChildAt(i);
            if (childAt instanceof WidgetsSpaceViewHolderBinder.EmptySpaceView) {
                WidgetsSpaceViewHolderBinder.EmptySpaceView emptySpaceView2 = (WidgetsSpaceViewHolderBinder.EmptySpaceView) childAt;
                this.mCurrentEmptySpaceView = emptySpaceView2;
                emptySpaceView2.setFixedHeight(getHeaderHeight());
                this.mCurrentEmptySpaceView.setOnYChangeCallback(new Runnable() {
                    public final void run() {
                        SearchAndRecommendationsScrollController.this.updateHeaderScroll();
                    }
                });
                return;
            }
        }
    }
}
