package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DeviceProfile.DeviceProfileListenable;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Insettable;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.BaseAllAppsContainerView;
import com.android.launcher3.allapps.search.SearchAdapterProvider;
import com.android.launcher3.keyboard.FocusedItemDecorator;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.StringCache;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.views.RecyclerViewFastScroller;
import com.android.launcher3.views.ScrimView;
import com.android.launcher3.views.SpringRelativeLayout;
import com.android.launcher3.workprofile.PersonalWorkSlidingTabStrip;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class BaseAllAppsContainerView<T extends Context & ActivityContext & DeviceProfile.DeviceProfileListenable> extends SpringRelativeLayout implements DragSource, Insettable, DeviceProfile.OnDeviceProfileChangeListener, PersonalWorkSlidingTabStrip.OnActivePageChangedListener, ScrimView.ScrimDrawingController {
    protected static final String BUNDLE_KEY_CURRENT_PAGE = "launcher.allapps.current_page";
    public static final float FLING_VELOCITY_MULTIPLIER = 1200.0f;
    public static final float PULL_MULTIPLIER = 0.02f;
    protected final List<BaseAllAppsContainerView<T>.AdapterHolder> mAH;
    protected final T mActivityContext;
    /* access modifiers changed from: private */
    public final AllAppsStore mAllAppsStore;
    private View mBottomSheetBackground;
    private View mBottomSheetHandleArea;
    protected final Point mFastScrollerOffset;
    private boolean mHasWorkApps;
    protected FloatingHeaderView mHeader;
    private int mHeaderBottomAdjustment;
    private int mHeaderColor;
    private final Paint mHeaderPaint = new Paint(1);
    private final int mHeaderProtectionColor;
    protected final float mHeaderThreshold;
    /* access modifiers changed from: private */
    public final Rect mInsets = new Rect();
    /* access modifiers changed from: private */
    public final SearchAdapterProvider<?> mMainAdapterProvider;
    private int mNavBarScrimHeight;
    private final Paint mNavBarScrimPaint;
    protected final Predicate<ItemInfo> mPersonalMatcher = ItemInfoMatcher.ofUser(Process.myUserHandle());
    private final int mScrimColor;
    private ScrimView mScrimView;
    /* access modifiers changed from: private */
    public final RecyclerView.OnScrollListener mScrollListener;
    private SearchRecyclerView mSearchRecyclerView;
    private int mTabsProtectionAlpha;
    protected RecyclerViewFastScroller mTouchHandler;
    protected boolean mUsingTabs;
    private AllAppsPagedView mViewPager;
    /* access modifiers changed from: private */
    public final WorkProfileManager mWorkManager;

    static /* synthetic */ boolean lambda$onFinishInflate$1(ItemInfo itemInfo) {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract BaseAllAppsAdapter<T> createAdapter(AlphabeticalAppsList<T> alphabeticalAppsList, BaseAdapterProvider[] baseAdapterProviderArr);

    /* access modifiers changed from: protected */
    public abstract SearchAdapterProvider<?> createMainAdapterProvider();

    /* access modifiers changed from: protected */
    public int getNavBarScrimHeight(WindowInsets windowInsets) {
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean isSearching() {
        return false;
    }

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
    }

    protected BaseAllAppsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        AllAppsStore allAppsStore = new AllAppsStore();
        this.mAllAppsStore = allAppsStore;
        this.mScrollListener = new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                BaseAllAppsContainerView.this.updateHeaderScroll(((AllAppsRecyclerView) recyclerView).getCurrentScrollY());
            }
        };
        this.mNavBarScrimHeight = 0;
        this.mFastScrollerOffset = new Point();
        T lookupContext = ActivityContext.lookupContext(context);
        this.mActivityContext = lookupContext;
        this.mMainAdapterProvider = createMainAdapterProvider();
        this.mScrimColor = Themes.getAttrColor(context, R.attr.allAppsScrimColor);
        this.mHeaderThreshold = (float) getResources().getDimensionPixelSize(R.dimen.dynamic_grid_cell_border_spacing);
        this.mHeaderBottomAdjustment = getResources().getDimensionPixelSize(R.dimen.all_apps_header_bottom_adjustment);
        this.mHeaderProtectionColor = Themes.getAttrColor(context, R.attr.allappsHeaderProtectionColor);
        this.mWorkManager = new WorkProfileManager((UserManager) lookupContext.getSystemService(UserManager.class), this, Utilities.getPrefs(lookupContext), ((DeviceProfile.DeviceProfileListenable) lookupContext).getDeviceProfile());
        List<BaseAllAppsContainerView<T>.AdapterHolder> asList = Arrays.asList(new AdapterHolder[]{null, null, null});
        this.mAH = asList;
        asList.set(0, new AdapterHolder(0));
        asList.set(1, new AdapterHolder(1));
        asList.set(2, new AdapterHolder(2));
        Paint paint = new Paint();
        this.mNavBarScrimPaint = paint;
        paint.setColor(Themes.getAttrColor(context, R.attr.allAppsNavBarScrimColor));
        allAppsStore.addUpdateListener(new AllAppsStore.OnUpdateListener() {
            public final void onAppsUpdated() {
                BaseAllAppsContainerView.this.onAppsUpdated();
            }
        });
        ((DeviceProfile.DeviceProfileListenable) lookupContext).addOnDeviceProfileChangeListener(this);
    }

    public final SearchAdapterProvider<?> getMainAdapterProvider() {
        return this.mMainAdapterProvider;
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        AllAppsPagedView allAppsPagedView;
        try {
            super.dispatchRestoreInstanceState(sparseArray);
        } catch (Exception e) {
            Log.e("AllAppsContainerView", "restoreInstanceState viewId = 0", e);
        }
        Bundle bundle = (Bundle) sparseArray.get(R.id.work_tab_state_id, (Object) null);
        if (bundle != null) {
            int i = bundle.getInt(BUNDLE_KEY_CURRENT_PAGE, 0);
            if (i != 1 || (allAppsPagedView = this.mViewPager) == null) {
                reset(true);
                return;
            }
            allAppsPagedView.setCurrentPage(i);
            rebindAdapters();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        super.dispatchSaveInstanceState(sparseArray);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_CURRENT_PAGE, getCurrentPage());
        sparseArray.put(R.id.work_tab_state_id, bundle);
    }

    public void setOnIconLongClickListener(View.OnLongClickListener onLongClickListener) {
        for (BaseAllAppsContainerView<T>.AdapterHolder adapterHolder : this.mAH) {
            adapterHolder.mAdapter.setOnIconLongClickListener(onLongClickListener);
        }
    }

    public AllAppsStore getAppsStore() {
        return this.mAllAppsStore;
    }

    public WorkProfileManager getWorkManager() {
        return this.mWorkManager;
    }

    public void onDeviceProfileChanged(DeviceProfile deviceProfile) {
        for (AdapterHolder next : this.mAH) {
            next.mAdapter.setAppsPerRow(deviceProfile.numShownAllAppsColumns);
            if (next.mRecyclerView != null) {
                next.mRecyclerView.swapAdapter(next.mRecyclerView.getAdapter(), true);
                next.mRecyclerView.getRecycledViewPool().clear();
            }
        }
        updateBackground(deviceProfile);
    }

    /* access modifiers changed from: protected */
    public void updateBackground(DeviceProfile deviceProfile) {
        this.mBottomSheetBackground.setVisibility(deviceProfile.isTablet ? 0 : 8);
    }

    /* access modifiers changed from: private */
    public void onAppsUpdated() {
        this.mHasWorkApps = Stream.of(this.mAllAppsStore.getApps()).anyMatch(this.mWorkManager.getMatcher());
        if (!isSearching()) {
            rebindAdapters();
            if (this.mHasWorkApps) {
                this.mWorkManager.reset();
            }
        }
    }

    public boolean shouldContainerScroll(MotionEvent motionEvent) {
        AllAppsRecyclerView activeRecyclerView;
        BaseDragLayer dragLayer = ((ActivityContext) this.mActivityContext).getDragLayer();
        if (!dragLayer.isEventOverView(this, motionEvent) || dragLayer.isEventOverView(this.mBottomSheetHandleArea, motionEvent) || (activeRecyclerView = getActiveRecyclerView()) == null) {
            return true;
        }
        if (activeRecyclerView.getScrollbar() == null || activeRecyclerView.getScrollbar().getThumbOffsetY() < 0 || !dragLayer.isEventOverView(activeRecyclerView.getScrollbar(), motionEvent)) {
            return activeRecyclerView.shouldContainerScroll(motionEvent, dragLayer);
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            AllAppsRecyclerView activeRecyclerView = getActiveRecyclerView();
            if (activeRecyclerView == null || activeRecyclerView.getScrollbar() == null || !activeRecyclerView.getScrollbar().isHitInParent(motionEvent.getX(), motionEvent.getY(), this.mFastScrollerOffset)) {
                this.mTouchHandler = null;
            } else {
                this.mTouchHandler = activeRecyclerView.getScrollbar();
            }
        }
        RecyclerViewFastScroller recyclerViewFastScroller = this.mTouchHandler;
        if (recyclerViewFastScroller != null) {
            return recyclerViewFastScroller.handleTouchEvent(motionEvent, this.mFastScrollerOffset);
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            AllAppsRecyclerView activeRecyclerView = getActiveRecyclerView();
            if (activeRecyclerView == null || activeRecyclerView.getScrollbar() == null || !activeRecyclerView.getScrollbar().isHitInParent(motionEvent.getX(), motionEvent.getY(), this.mFastScrollerOffset)) {
                this.mTouchHandler = null;
            } else {
                this.mTouchHandler = activeRecyclerView.getScrollbar();
            }
        }
        RecyclerViewFastScroller recyclerViewFastScroller = this.mTouchHandler;
        if (recyclerViewFastScroller != null) {
            recyclerViewFastScroller.handleTouchEvent(motionEvent, this.mFastScrollerOffset);
            return true;
        } else if (isSearching()) {
            return true;
        } else {
            return false;
        }
    }

    public String getDescription() {
        StringCache stringCache = ((ActivityContext) this.mActivityContext).getStringCache();
        if (!this.mUsingTabs) {
            return getContext().getString(R.string.all_apps_button_label);
        }
        if (stringCache != null) {
            if (isPersonalTab()) {
                return stringCache.allAppsPersonalTabAccessibility;
            }
            return stringCache.allAppsWorkTabAccessibility;
        } else if (isPersonalTab()) {
            return getContext().getString(R.string.all_apps_button_personal_label);
        } else {
            return getContext().getString(R.string.all_apps_button_work_label);
        }
    }

    public AllAppsRecyclerView getActiveRecyclerView() {
        if (isSearching()) {
            return getSearchRecyclerView();
        }
        return getActiveAppsRecyclerView();
    }

    private AllAppsRecyclerView getActiveAppsRecyclerView() {
        if (!this.mUsingTabs || isPersonalTab()) {
            return this.mAH.get(0).mRecyclerView;
        }
        return this.mAH.get(1).mRecyclerView;
    }

    private View getAppsRecyclerViewContainer() {
        AllAppsPagedView allAppsPagedView = this.mViewPager;
        return allAppsPagedView != null ? allAppsPagedView : findViewById(R.id.apps_list_view);
    }

    public SearchRecyclerView getSearchRecyclerView() {
        return this.mSearchRecyclerView;
    }

    /* access modifiers changed from: protected */
    public boolean isPersonalTab() {
        AllAppsPagedView allAppsPagedView = this.mViewPager;
        return allAppsPagedView == null || allAppsPagedView.getNextPage() == 0;
    }

    public void switchToTab(int i) {
        if (this.mUsingTabs) {
            this.mViewPager.setCurrentPage(i);
        }
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getContext());
    }

    public void reset(boolean z) {
        for (int i = 0; i < this.mAH.size(); i++) {
            if (this.mAH.get(i).mRecyclerView != null) {
                this.mAH.get(i).mRecyclerView.scrollToTop();
            }
        }
        if (isHeaderVisible()) {
            this.mHeader.reset(z);
        }
        updateHeaderScroll(0);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public final void onFocusChange(View view, boolean z) {
                BaseAllAppsContainerView.this.lambda$onFinishInflate$0$BaseAllAppsContainerView(view, z);
            }
        });
        this.mHeader = (FloatingHeaderView) findViewById(R.id.all_apps_header);
        this.mSearchRecyclerView = (SearchRecyclerView) findViewById(R.id.search_results_list_view);
        this.mAH.get(2).setup(this.mSearchRecyclerView, $$Lambda$BaseAllAppsContainerView$9NrVnz8qD1QBOskdYZxDbRoDzJs.INSTANCE);
        rebindAdapters(true);
        this.mBottomSheetBackground = findViewById(R.id.bottom_sheet_background);
        updateBackground(((DeviceProfile.DeviceProfileListenable) this.mActivityContext).getDeviceProfile());
        this.mBottomSheetHandleArea = findViewById(R.id.bottom_sheet_handle_area);
    }

    public /* synthetic */ void lambda$onFinishInflate$0$BaseAllAppsContainerView(View view, boolean z) {
        if (z && getActiveRecyclerView() != null) {
            getActiveRecyclerView().requestFocus();
        }
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        DeviceProfile deviceProfile = ((DeviceProfile.DeviceProfileListenable) this.mActivityContext).getDeviceProfile();
        applyAdapterSideAndBottomPaddings(deviceProfile);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        marginLayoutParams.leftMargin = rect.left;
        marginLayoutParams.rightMargin = rect.right;
        setLayoutParams(marginLayoutParams);
        if (deviceProfile.isVerticalBarLayout()) {
            setPadding(deviceProfile.workspacePadding.left, 0, deviceProfile.workspacePadding.right, 0);
        } else {
            setPadding(deviceProfile.allAppsLeftRightMargin, deviceProfile.allAppsTopPadding, deviceProfile.allAppsLeftRightMargin, 0);
        }
        InsettableFrameLayout.dispatchInsets(this, rect);
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets windowInsets) {
        this.mNavBarScrimHeight = getNavBarScrimHeight(windowInsets);
        applyAdapterSideAndBottomPaddings(((DeviceProfile.DeviceProfileListenable) this.mActivityContext).getDeviceProfile());
        return super.dispatchApplyWindowInsets(windowInsets);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mNavBarScrimHeight > 0) {
            canvas.drawRect(0.0f, (float) (getHeight() - this.mNavBarScrimHeight), (float) getWidth(), (float) getHeight(), this.mNavBarScrimPaint);
        }
    }

    /* access modifiers changed from: protected */
    public void rebindAdapters() {
        rebindAdapters(false);
    }

    /* access modifiers changed from: protected */
    public void rebindAdapters(boolean z) {
        updateSearchResultsVisibility();
        boolean shouldShowTabs = shouldShowTabs();
        if (shouldShowTabs == this.mUsingTabs && !z) {
            return;
        }
        if (isSearching()) {
            this.mUsingTabs = shouldShowTabs;
            this.mWorkManager.detachWorkModeSwitch();
            return;
        }
        replaceAppsRVContainer(shouldShowTabs);
        this.mUsingTabs = shouldShowTabs;
        this.mAllAppsStore.unregisterIconContainer(this.mAH.get(0).mRecyclerView);
        this.mAllAppsStore.unregisterIconContainer(this.mAH.get(1).mRecyclerView);
        if (this.mUsingTabs) {
            this.mAH.get(0).setup(this.mViewPager.getChildAt(0), this.mPersonalMatcher);
            this.mAH.get(1).setup(this.mViewPager.getChildAt(1), this.mWorkManager.getMatcher());
            this.mAH.get(1).mRecyclerView.setId(R.id.apps_list_view_work);
            ((PersonalWorkSlidingTabStrip) this.mViewPager.getPageIndicator()).setActiveMarker(0);
            findViewById(R.id.tab_personal).setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    BaseAllAppsContainerView.this.lambda$rebindAdapters$2$BaseAllAppsContainerView(view);
                }
            });
            findViewById(R.id.tab_work).setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    BaseAllAppsContainerView.this.lambda$rebindAdapters$3$BaseAllAppsContainerView(view);
                }
            });
            setDeviceManagementResources();
            onActivePageChanged(this.mViewPager.getNextPage());
        } else {
            this.mAH.get(0).setup(findViewById(R.id.apps_list_view), (Predicate<ItemInfo>) null);
            this.mAH.get(1).mRecyclerView = null;
        }
        setupHeader();
        this.mAllAppsStore.registerIconContainer(this.mAH.get(0).mRecyclerView);
        this.mAllAppsStore.registerIconContainer(this.mAH.get(1).mRecyclerView);
    }

    public /* synthetic */ void lambda$rebindAdapters$2$BaseAllAppsContainerView(View view) {
        if (this.mViewPager.snapToPage(0)) {
            ((ActivityContext) this.mActivityContext).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_TAP_ON_PERSONAL_TAB);
        }
        UiThreadHelper.hideKeyboardAsync((ActivityContext) ActivityContext.lookupContext(getContext()), getApplicationWindowToken());
    }

    public /* synthetic */ void lambda$rebindAdapters$3$BaseAllAppsContainerView(View view) {
        if (this.mViewPager.snapToPage(1)) {
            ((ActivityContext) this.mActivityContext).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_TAP_ON_WORK_TAB);
        }
        UiThreadHelper.hideKeyboardAsync((ActivityContext) ActivityContext.lookupContext(getContext()), getApplicationWindowToken());
    }

    private void updateSearchResultsVisibility() {
        if (isSearching()) {
            getSearchRecyclerView().setVisibility(0);
            getAppsRecyclerViewContainer().setVisibility(8);
        } else {
            getSearchRecyclerView().setVisibility(8);
            getAppsRecyclerViewContainer().setVisibility(0);
        }
        if (this.mHeader.isSetUp()) {
            this.mHeader.setActiveRV(getCurrentPage());
        }
    }

    private void applyAdapterSideAndBottomPaddings(DeviceProfile deviceProfile) {
        this.mAH.forEach(new Consumer(Math.max(this.mInsets.bottom, this.mNavBarScrimHeight), deviceProfile) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ DeviceProfile f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                BaseAllAppsContainerView.lambda$applyAdapterSideAndBottomPaddings$4(this.f$0, this.f$1, (BaseAllAppsContainerView.AdapterHolder) obj);
            }
        });
    }

    static /* synthetic */ void lambda$applyAdapterSideAndBottomPaddings$4(int i, DeviceProfile deviceProfile, AdapterHolder adapterHolder) {
        adapterHolder.mPadding.bottom = i;
        Rect rect = adapterHolder.mPadding;
        Rect rect2 = adapterHolder.mPadding;
        int i2 = deviceProfile.allAppsLeftRightPadding;
        rect2.right = i2;
        rect.left = i2;
        adapterHolder.applyPadding();
    }

    private void setDeviceManagementResources() {
        if (((ActivityContext) this.mActivityContext).getStringCache() != null) {
            ((Button) findViewById(R.id.tab_personal)).setText(((ActivityContext) this.mActivityContext).getStringCache().allAppsPersonalTab);
            ((Button) findViewById(R.id.tab_work)).setText(((ActivityContext) this.mActivityContext).getStringCache().allAppsWorkTab);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowTabs() {
        return this.mHasWorkApps;
    }

    /* access modifiers changed from: protected */
    public View replaceAppsRVContainer(boolean z) {
        for (int i = 0; i <= 1; i++) {
            AdapterHolder adapterHolder = this.mAH.get(i);
            if (adapterHolder.mRecyclerView != null) {
                adapterHolder.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager) null);
                adapterHolder.mRecyclerView.setAdapter((RecyclerView.Adapter) null);
            }
        }
        View appsRecyclerViewContainer = getAppsRecyclerViewContainer();
        int indexOfChild = indexOfChild(appsRecyclerViewContainer);
        removeView(appsRecyclerViewContainer);
        View inflate = getLayoutInflater().inflate(z ? R.layout.all_apps_tabs : R.layout.all_apps_rv_layout, this, false);
        addView(inflate, indexOfChild);
        if (z) {
            AllAppsPagedView allAppsPagedView = (AllAppsPagedView) inflate;
            this.mViewPager = allAppsPagedView;
            allAppsPagedView.initParentViews(this);
            ((PersonalWorkSlidingTabStrip) this.mViewPager.getPageIndicator()).setOnActivePageChangedListener(this);
            if (this.mWorkManager.attachWorkModeSwitch()) {
                this.mWorkManager.getWorkModeSwitch().post(new Runnable() {
                    public final void run() {
                        BaseAllAppsContainerView.this.lambda$replaceAppsRVContainer$5$BaseAllAppsContainerView();
                    }
                });
            }
        } else {
            this.mWorkManager.detachWorkModeSwitch();
            this.mViewPager = null;
        }
        return inflate;
    }

    public /* synthetic */ void lambda$replaceAppsRVContainer$5$BaseAllAppsContainerView() {
        this.mAH.get(1).applyPadding();
    }

    public void onActivePageChanged(int i) {
        if (this.mAH.get(i).mRecyclerView != null) {
            this.mAH.get(i).mRecyclerView.bindFastScrollbar();
        }
        reset(true);
        this.mWorkManager.onActivePageChanged(i);
    }

    private boolean isDescendantViewVisible(int i) {
        View findViewById = findViewById(i);
        if (findViewById != null && findViewById.isShown()) {
            return findViewById.getGlobalVisibleRect(new Rect());
        }
        return false;
    }

    public boolean isPersonalTabVisible() {
        return isDescendantViewVisible(R.id.tab_personal);
    }

    public boolean isWorkTabVisible() {
        return isDescendantViewVisible(R.id.tab_work);
    }

    public AlphabeticalAppsList<T> getSearchResultList() {
        return this.mAH.get(2).mAppsList;
    }

    public FloatingHeaderView getFloatingHeaderView() {
        return this.mHeader;
    }

    public View getContentView() {
        return isSearching() ? getSearchRecyclerView() : getAppsRecyclerViewContainer();
    }

    public int getCurrentPage() {
        if (isSearching()) {
            return 2;
        }
        AllAppsPagedView allAppsPagedView = this.mViewPager;
        if (allAppsPagedView == null) {
            return 0;
        }
        return allAppsPagedView.getNextPage();
    }

    public RecyclerViewFastScroller getScrollBar() {
        AllAppsRecyclerView activeAppsRecyclerView = getActiveAppsRecyclerView();
        if (activeAppsRecyclerView == null) {
            return null;
        }
        return activeAppsRecyclerView.getScrollbar();
    }

    /* access modifiers changed from: package-private */
    public void setupHeader() {
        this.mHeader.setVisibility(0);
        this.mHeader.setup(this.mAH.get(0).mRecyclerView, this.mAH.get(1).mRecyclerView, (SearchRecyclerView) this.mAH.get(2).mRecyclerView, getCurrentPage(), !this.mUsingTabs);
        this.mAH.forEach(new Consumer(this.mHeader.getMaxTranslation()) {
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                BaseAllAppsContainerView.lambda$setupHeader$6(this.f$0, (BaseAllAppsContainerView.AdapterHolder) obj);
            }
        });
    }

    static /* synthetic */ void lambda$setupHeader$6(int i, AdapterHolder adapterHolder) {
        adapterHolder.mPadding.top = i;
        adapterHolder.applyPadding();
        if (adapterHolder.mRecyclerView != null) {
            adapterHolder.mRecyclerView.scrollToTop();
        }
    }

    public boolean isHeaderVisible() {
        FloatingHeaderView floatingHeaderView = this.mHeader;
        return floatingHeaderView != null && floatingHeaderView.getVisibility() == 0;
    }

    public void addSpringFromFlingUpdateListener(ValueAnimator valueAnimator, final float f, final float f2) {
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                BaseAllAppsContainerView.this.absorbSwipeUpVelocity(Math.max(1000, Math.abs(Math.round(Math.min(0.0f, (((1.0f - f2) * ((float) BaseAllAppsContainerView.this.getHeight())) / (((float) animator.getDuration()) * 1.7f)) + f) * 1200.0f))));
            }
        });
    }

    public void onPull(float f, float f2) {
        absorbPullDeltaDistance(f * 0.02f, f2 * 0.02f);
    }

    public void getDrawingRect(Rect rect) {
        super.getDrawingRect(rect);
        rect.offset(0, (int) getTranslationY());
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        invalidateHeader();
    }

    public void setScrimView(ScrimView scrimView) {
        this.mScrimView = scrimView;
    }

    public void drawOnScrim(Canvas canvas) {
        if (this.mHeader.isHeaderProtectionSupported()) {
            this.mHeaderPaint.setColor(this.mHeaderColor);
            this.mHeaderPaint.setAlpha((int) (getAlpha() * ((float) Color.alpha(this.mHeaderColor))));
            if (this.mHeaderPaint.getColor() != this.mScrimColor && this.mHeaderPaint.getColor() != 0) {
                int headerBottom = getHeaderBottom();
                if (!this.mUsingTabs) {
                    headerBottom += getFloatingHeaderView().getPaddingBottom() - this.mHeaderBottomAdjustment;
                }
                float f = (float) headerBottom;
                canvas.drawRect(0.0f, 0.0f, (float) canvas.getWidth(), f, this.mHeaderPaint);
                int peripheralProtectionHeight = getFloatingHeaderView().getPeripheralProtectionHeight();
                if (this.mTabsProtectionAlpha > 0 && peripheralProtectionHeight != 0) {
                    this.mHeaderPaint.setAlpha((int) (getAlpha() * ((float) this.mTabsProtectionAlpha)));
                    canvas.drawRect(0.0f, f, (float) canvas.getWidth(), (float) (headerBottom + peripheralProtectionHeight), this.mHeaderPaint);
                }
            }
        }
    }

    public void invalidateHeader() {
        if (this.mScrimView != null && this.mHeader.isHeaderProtectionSupported()) {
            this.mScrimView.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void updateHeaderScroll(int i) {
        int i2;
        int headerColor = getHeaderColor(Utilities.boundToRange(((float) i) / this.mHeaderThreshold, 0.0f, 1.0f));
        if (this.mHeader.getPeripheralProtectionHeight() == 0) {
            i2 = 0;
        } else {
            i2 = (int) (Utilities.boundToRange(((float) (i + this.mHeader.mSnappedScrolledY)) / this.mHeaderThreshold, 0.0f, 1.0f) * 255.0f);
        }
        if (headerColor != this.mHeaderColor || this.mTabsProtectionAlpha != i2) {
            this.mHeaderColor = headerColor;
            this.mTabsProtectionAlpha = i2;
            invalidateHeader();
        }
    }

    /* access modifiers changed from: protected */
    public int getHeaderColor(float f) {
        return ColorUtils.blendARGB(this.mScrimColor, this.mHeaderProtectionColor, f);
    }

    public int getHeaderBottom() {
        return (int) getTranslationY();
    }

    public View getVisibleContainerView() {
        return ((DeviceProfile.DeviceProfileListenable) this.mActivityContext).getDeviceProfile().isTablet ? this.mBottomSheetBackground : this;
    }

    public class AdapterHolder {
        public static final int MAIN = 0;
        public static final int SEARCH = 2;
        public static final int WORK = 1;
        public final BaseAllAppsAdapter<T> mAdapter;
        final AlphabeticalAppsList<T> mAppsList;
        final RecyclerView.LayoutManager mLayoutManager;
        final Rect mPadding = new Rect();
        /* access modifiers changed from: package-private */
        public AllAppsRecyclerView mRecyclerView;
        private final int mType;

        AdapterHolder(int i) {
            this.mType = i;
            AlphabeticalAppsList<T> alphabeticalAppsList = new AlphabeticalAppsList<>(BaseAllAppsContainerView.this.mActivityContext, isSearch() ? null : BaseAllAppsContainerView.this.mAllAppsStore, isWork() ? BaseAllAppsContainerView.this.mWorkManager.getAdapterProvider() : null);
            this.mAppsList = alphabeticalAppsList;
            BaseAllAppsAdapter<T> createAdapter = BaseAllAppsContainerView.this.createAdapter(alphabeticalAppsList, isWork() ? new BaseAdapterProvider[]{BaseAllAppsContainerView.this.mMainAdapterProvider, BaseAllAppsContainerView.this.mWorkManager.getAdapterProvider()} : new BaseAdapterProvider[]{BaseAllAppsContainerView.this.mMainAdapterProvider});
            this.mAdapter = createAdapter;
            alphabeticalAppsList.setAdapter(createAdapter);
            this.mLayoutManager = createAdapter.getLayoutManager();
        }

        /* access modifiers changed from: package-private */
        public void setup(View view, Predicate<ItemInfo> predicate) {
            this.mAppsList.updateItemFilter(predicate);
            AllAppsRecyclerView allAppsRecyclerView = (AllAppsRecyclerView) view;
            this.mRecyclerView = allAppsRecyclerView;
            allAppsRecyclerView.setEdgeEffectFactory(BaseAllAppsContainerView.this.createEdgeEffectFactory());
            this.mRecyclerView.setApps(this.mAppsList);
            this.mRecyclerView.setLayoutManager(this.mLayoutManager);
            this.mRecyclerView.setAdapter(this.mAdapter);
            this.mRecyclerView.setHasFixedSize(true);
            this.mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.mRecyclerView.addOnScrollListener(BaseAllAppsContainerView.this.mScrollListener);
            FocusedItemDecorator focusedItemDecorator = new FocusedItemDecorator(this.mRecyclerView);
            this.mRecyclerView.addItemDecoration(focusedItemDecorator);
            this.mAdapter.setIconFocusListener(focusedItemDecorator.getFocusListener());
            applyPadding();
        }

        /* access modifiers changed from: package-private */
        public void applyPadding() {
            if (this.mRecyclerView != null) {
                int i = 0;
                if (isWork() && BaseAllAppsContainerView.this.mWorkManager.getWorkModeSwitch() != null) {
                    i = BaseAllAppsContainerView.this.mInsets.bottom + BaseAllAppsContainerView.this.mWorkManager.getWorkModeSwitch().getHeight();
                }
                this.mRecyclerView.setPadding(this.mPadding.left, this.mPadding.top, this.mPadding.right, this.mPadding.bottom + i);
            }
        }

        private boolean isWork() {
            return this.mType == 1;
        }

        private boolean isSearch() {
            return this.mType == 2;
        }
    }
}
