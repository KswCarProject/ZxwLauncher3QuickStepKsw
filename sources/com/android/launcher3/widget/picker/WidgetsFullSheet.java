package com.android.launcher3.widget.picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.UserManagerState;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.AbstractSlideInView;
import com.android.launcher3.views.ArrowTipView;
import com.android.launcher3.views.RecyclerViewFastScroller;
import com.android.launcher3.views.SpringRelativeLayout;
import com.android.launcher3.views.WidgetsEduView;
import com.android.launcher3.widget.BaseWidgetSheet;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.picker.WidgetsFullSheet;
import com.android.launcher3.widget.picker.WidgetsRecyclerView;
import com.android.launcher3.widget.picker.search.SearchModeListener;
import com.android.launcher3.widget.util.WidgetsTableUtils;
import com.android.launcher3.workprofile.PersonalWorkPagedView;
import com.android.launcher3.workprofile.PersonalWorkSlidingTabStrip;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class WidgetsFullSheet extends BaseWidgetSheet implements LauncherAppWidgetHost.ProviderChangedListener, PersonalWorkSlidingTabStrip.OnActivePageChangedListener, WidgetsRecyclerView.HeaderViewDimensionsProvider, SearchModeListener {
    private static final long DEFAULT_OPEN_DURATION = 267;
    private static final long EDUCATION_DIALOG_DELAY_MS = 500;
    private static final long EDUCATION_TIP_DELAY_MS = 200;
    private static final long FADE_IN_DURATION = 150;
    private static final String KEY_WIDGETS_EDUCATION_DIALOG_SEEN = "launcher.widgets_education_dialog_seen";
    private static final float RECOMMENDATION_TABLE_HEIGHT_RATIO = 0.75f;
    private static final float VERTICAL_START_POSITION = 0.3f;
    /* access modifiers changed from: private */
    public final SparseArray<AdapterHolder> mAdapters;
    /* access modifiers changed from: private */
    public final View.OnAttachStateChangeListener mBindScrollbarInSearchMode;
    private final UserHandle mCurrentUser;
    private WidgetsRecyclerView mCurrentWidgetsRecyclerView;
    private final boolean mHasWorkProfile;
    /* access modifiers changed from: private */
    public boolean mIsInSearchMode;
    private boolean mIsNoWidgetsViewNeeded;
    private ArrowTipView mLatestEducationalTip;
    private final View.OnLayoutChangeListener mLayoutChangeListenerToShowTips;
    /* access modifiers changed from: private */
    public int mMaxSpansPerRow;
    private TextView mNoWidgetsView;
    /* access modifiers changed from: private */
    public final Predicate<WidgetsListBaseEntry> mPrimaryWidgetsFilter;
    /* access modifiers changed from: private */
    public SearchAndRecommendationsScrollController mSearchScrollController;
    /* access modifiers changed from: private */
    public final Runnable mShowEducationTipTask;
    private final int mTabsHeight;
    private final UserManagerState mUserManagerState;
    private PersonalWorkPagedView mViewPager;
    private final int mWidgetSheetContentHorizontalPadding;
    /* access modifiers changed from: private */
    public final Predicate<WidgetsListBaseEntry> mWorkWidgetsFilter;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 16) != 0;
    }

    public /* synthetic */ boolean lambda$new$0$WidgetsFullSheet(WidgetsListBaseEntry widgetsListBaseEntry) {
        return this.mCurrentUser.equals(widgetsListBaseEntry.mPkgItem.user);
    }

    public /* synthetic */ boolean lambda$new$1$WidgetsFullSheet(WidgetsListBaseEntry widgetsListBaseEntry) {
        return !this.mCurrentUser.equals(widgetsListBaseEntry.mPkgItem.user) && !this.mUserManagerState.isUserQuiet(widgetsListBaseEntry.mPkgItem.user);
    }

    public /* synthetic */ void lambda$new$2$WidgetsFullSheet() {
        if (hasSeenEducationTip()) {
            removeOnLayoutChangeListener(this.mLayoutChangeListenerToShowTips);
            return;
        }
        ArrowTipView showEducationTipOnViewIfPossible = showEducationTipOnViewIfPossible(getViewToShowEducationTip());
        this.mLatestEducationalTip = showEducationTipOnViewIfPossible;
        if (showEducationTipOnViewIfPossible != null) {
            removeOnLayoutChangeListener(this.mLayoutChangeListenerToShowTips);
        }
    }

    public WidgetsFullSheet(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        UserManagerState userManagerState = new UserManagerState();
        this.mUserManagerState = userManagerState;
        SparseArray<AdapterHolder> sparseArray = new SparseArray<>();
        this.mAdapters = sparseArray;
        this.mCurrentUser = Process.myUserHandle();
        this.mPrimaryWidgetsFilter = new Predicate() {
            public final boolean test(Object obj) {
                return WidgetsFullSheet.this.lambda$new$0$WidgetsFullSheet((WidgetsListBaseEntry) obj);
            }
        };
        this.mWorkWidgetsFilter = new Predicate() {
            public final boolean test(Object obj) {
                return WidgetsFullSheet.this.lambda$new$1$WidgetsFullSheet((WidgetsListBaseEntry) obj);
            }
        };
        this.mLayoutChangeListenerToShowTips = new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                if (WidgetsFullSheet.this.hasSeenEducationTip()) {
                    WidgetsFullSheet.this.removeOnLayoutChangeListener(this);
                    return;
                }
                WidgetsFullSheet widgetsFullSheet = WidgetsFullSheet.this;
                widgetsFullSheet.removeCallbacks(widgetsFullSheet.mShowEducationTipTask);
                WidgetsFullSheet widgetsFullSheet2 = WidgetsFullSheet.this;
                widgetsFullSheet2.postDelayed(widgetsFullSheet2.mShowEducationTipTask, WidgetsFullSheet.EDUCATION_TIP_DELAY_MS);
            }
        };
        this.mShowEducationTipTask = new Runnable() {
            public final void run() {
                WidgetsFullSheet.this.lambda$new$2$WidgetsFullSheet();
            }
        };
        this.mBindScrollbarInSearchMode = new View.OnAttachStateChangeListener() {
            public void onViewDetachedFromWindow(View view) {
            }

            public void onViewAttachedToWindow(View view) {
                WidgetsRecyclerView access$300 = ((AdapterHolder) WidgetsFullSheet.this.mAdapters.get(2)).mWidgetsRecyclerView;
                if (WidgetsFullSheet.this.mIsInSearchMode && access$300 != null) {
                    access$300.bindFastScrollbar();
                }
            }
        };
        this.mMaxSpansPerRow = 4;
        int i2 = 0;
        boolean z = ((LauncherApps) context.getSystemService(LauncherApps.class)).getProfiles().size() > 1;
        this.mHasWorkProfile = z;
        sparseArray.put(0, new AdapterHolder(0));
        sparseArray.put(1, new AdapterHolder(1));
        sparseArray.put(2, new AdapterHolder(2));
        Resources resources = getResources();
        this.mTabsHeight = z ? resources.getDimensionPixelSize(R.dimen.all_apps_header_pill_height) : i2;
        this.mWidgetSheetContentHorizontalPadding = resources.getDimensionPixelSize(R.dimen.widget_cell_horizontal_padding) * 2;
        userManagerState.init(UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(context), (UserManager) context.getSystemService(UserManager.class));
    }

    public WidgetsFullSheet(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (ViewGroup) findViewById(R.id.container);
        LayoutInflater.from(getContext()).inflate(this.mHasWorkProfile ? R.layout.widgets_full_sheet_paged_view : R.layout.widgets_full_sheet_recyclerview, this.mContent, true);
        RecyclerViewFastScroller recyclerViewFastScroller = (RecyclerViewFastScroller) findViewById(R.id.fast_scroller);
        this.mAdapters.get(0).setup((WidgetsRecyclerView) findViewById(R.id.primary_widgets_list_view));
        this.mAdapters.get(2).setup((WidgetsRecyclerView) findViewById(R.id.search_widgets_list_view));
        if (this.mHasWorkProfile) {
            PersonalWorkPagedView personalWorkPagedView = (PersonalWorkPagedView) findViewById(R.id.widgets_view_pager);
            this.mViewPager = personalWorkPagedView;
            personalWorkPagedView.initParentViews(this);
            ((PersonalWorkSlidingTabStrip) this.mViewPager.getPageIndicator()).setOnActivePageChangedListener(this);
            ((PersonalWorkSlidingTabStrip) this.mViewPager.getPageIndicator()).setActiveMarker(0);
            findViewById(R.id.tab_personal).setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WidgetsFullSheet.this.lambda$onFinishInflate$3$WidgetsFullSheet(view);
                }
            });
            findViewById(R.id.tab_work).setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    WidgetsFullSheet.this.lambda$onFinishInflate$4$WidgetsFullSheet(view);
                }
            });
            this.mAdapters.get(1).setup((WidgetsRecyclerView) findViewById(R.id.work_widgets_list_view));
            setDeviceManagementResources();
        } else {
            this.mViewPager = null;
        }
        this.mNoWidgetsView = (TextView) findViewById(R.id.no_widgets_text);
        SearchAndRecommendationsScrollController searchAndRecommendationsScrollController = new SearchAndRecommendationsScrollController((SearchAndRecommendationsView) findViewById(R.id.search_and_recommendations_container));
        this.mSearchScrollController = searchAndRecommendationsScrollController;
        searchAndRecommendationsScrollController.setCurrentRecyclerView((WidgetsRecyclerView) findViewById(R.id.primary_widgets_list_view));
        this.mSearchScrollController.mRecommendedWidgetsTable.setWidgetCellLongClickListener(this);
        this.mSearchScrollController.mRecommendedWidgetsTable.setWidgetCellOnClickListener(this);
        onRecommendedWidgetsBound();
        onWidgetsBound();
        this.mSearchScrollController.mSearchBar.initialize(((Launcher) this.mActivityContext).getPopupDataProvider(), this);
        setUpEducationViewsIfNeeded();
    }

    public /* synthetic */ void lambda$onFinishInflate$3$WidgetsFullSheet(View view) {
        this.mViewPager.snapToPage(0);
    }

    public /* synthetic */ void lambda$onFinishInflate$4$WidgetsFullSheet(View view) {
        this.mViewPager.snapToPage(1);
    }

    private void setDeviceManagementResources() {
        if (((Launcher) this.mActivityContext).getStringCache() != null) {
            ((Button) findViewById(R.id.tab_personal)).setText(((Launcher) this.mActivityContext).getStringCache().widgetsPersonalTab);
            ((Button) findViewById(R.id.tab_work)).setText(((Launcher) this.mActivityContext).getStringCache().widgetsWorkTab);
        }
    }

    public void onActivePageChanged(int i) {
        WidgetsRecyclerView access$300 = this.mAdapters.get(i).mWidgetsRecyclerView;
        updateRecyclerViewVisibility(this.mAdapters.get(i));
        attachScrollbarToRecyclerView(access$300);
    }

    private void attachScrollbarToRecyclerView(WidgetsRecyclerView widgetsRecyclerView) {
        widgetsRecyclerView.bindFastScrollbar();
        if (this.mCurrentWidgetsRecyclerView != widgetsRecyclerView) {
            reset();
            resetExpandedHeaders();
            this.mCurrentWidgetsRecyclerView = widgetsRecyclerView;
            this.mSearchScrollController.setCurrentRecyclerView(widgetsRecyclerView);
        }
    }

    private void updateRecyclerViewVisibility(AdapterHolder adapterHolder) {
        boolean hasVisibleEntries = adapterHolder.mWidgetsListAdapter.hasVisibleEntries();
        int i = 0;
        adapterHolder.mWidgetsRecyclerView.setVisibility(hasVisibleEntries ? 0 : 8);
        if (adapterHolder.mAdapterType == 2) {
            this.mNoWidgetsView.setText(R.string.no_search_results);
        } else if (adapterHolder.mAdapterType != 1 || !this.mUserManagerState.isAnyProfileQuietModeEnabled() || ((Launcher) this.mActivityContext).getStringCache() == null) {
            this.mNoWidgetsView.setText(R.string.no_widgets_available);
        } else {
            this.mNoWidgetsView.setText(((Launcher) this.mActivityContext).getStringCache().workProfilePausedTitle);
        }
        TextView textView = this.mNoWidgetsView;
        if (hasVisibleEntries) {
            i = 8;
        }
        textView.setVisibility(i);
    }

    private void reset() {
        this.mAdapters.get(0).mWidgetsRecyclerView.scrollToTop();
        if (this.mHasWorkProfile) {
            this.mAdapters.get(1).mWidgetsRecyclerView.scrollToTop();
        }
        this.mAdapters.get(2).mWidgetsRecyclerView.scrollToTop();
        this.mSearchScrollController.reset(true);
    }

    public WidgetsRecyclerView getRecyclerView() {
        if (this.mIsInSearchMode) {
            return this.mAdapters.get(2).mWidgetsRecyclerView;
        }
        if (!this.mHasWorkProfile || this.mViewPager.getCurrentPage() == 0) {
            return this.mAdapters.get(0).mWidgetsRecyclerView;
        }
        return this.mAdapters.get(1).mWidgetsRecyclerView;
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return Pair.create(getRecyclerView(), getContext().getString(this.mIsOpen ? R.string.widgets_list : R.string.widgets_list_closed));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((Launcher) this.mActivityContext).getAppWidgetHost().addProviderChangeListener(this);
        notifyWidgetProvidersChanged();
        onRecommendedWidgetsBound();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((Launcher) this.mActivityContext).getAppWidgetHost().removeProviderChangeListener(this);
        this.mAdapters.get(0).mWidgetsRecyclerView.removeOnAttachStateChangeListener(this.mBindScrollbarInSearchMode);
        if (this.mHasWorkProfile) {
            this.mAdapters.get(1).mWidgetsRecyclerView.removeOnAttachStateChangeListener(this.mBindScrollbarInSearchMode);
        }
    }

    public void setInsets(Rect rect) {
        super.setInsets(rect);
        int max = Math.max(rect.bottom, this.mNavBarScrimHeight);
        setBottomPadding(this.mAdapters.get(0).mWidgetsRecyclerView, max);
        setBottomPadding(this.mAdapters.get(2).mWidgetsRecyclerView, max);
        if (this.mHasWorkProfile) {
            setBottomPadding(this.mAdapters.get(1).mWidgetsRecyclerView, max);
        }
        ((ViewGroup.MarginLayoutParams) this.mNoWidgetsView.getLayoutParams()).bottomMargin = max;
        if (max > 0) {
            setupNavBarColor();
        } else {
            clearNavBarColor();
        }
        requestLayout();
    }

    private void setBottomPadding(RecyclerView recyclerView, int i) {
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), i);
    }

    /* access modifiers changed from: protected */
    public void onContentHorizontalMarginChanged(int i) {
        setContentViewChildHorizontalMargin(this.mSearchScrollController.mContainer, i);
        if (this.mViewPager == null) {
            setContentViewChildHorizontalPadding(this.mAdapters.get(0).mWidgetsRecyclerView, i);
        } else {
            setContentViewChildHorizontalPadding(this.mAdapters.get(0).mWidgetsRecyclerView, i);
            setContentViewChildHorizontalPadding(this.mAdapters.get(1).mWidgetsRecyclerView, i);
        }
        setContentViewChildHorizontalPadding(this.mAdapters.get(2).mWidgetsRecyclerView, i);
    }

    private static void setContentViewChildHorizontalMargin(View view, int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMarginStart(i);
        marginLayoutParams.setMarginEnd(i);
    }

    private static void setContentViewChildHorizontalPadding(View view, int i) {
        view.setPadding(i, view.getPaddingTop(), i, view.getPaddingBottom());
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        doMeasure(i, i2);
        if (this.mSearchScrollController.updateHeaderHeight()) {
            doMeasure(i, i2);
        }
        if (updateMaxSpansPerRow()) {
            doMeasure(i, i2);
            if (this.mSearchScrollController.updateHeaderHeight()) {
                doMeasure(i, i2);
            }
        }
    }

    private boolean updateMaxSpansPerRow() {
        View view;
        boolean z = false;
        if (getMeasuredWidth() == 0) {
            return false;
        }
        if (this.mHasWorkProfile) {
            view = this.mViewPager;
        } else {
            view = this.mAdapters.get(0).mWidgetsRecyclerView;
        }
        int computeMaxHorizontalSpans = computeMaxHorizontalSpans(view, this.mWidgetSheetContentHorizontalPadding);
        if (this.mMaxSpansPerRow != computeMaxHorizontalSpans) {
            this.mMaxSpansPerRow = computeMaxHorizontalSpans;
            this.mAdapters.get(0).mWidgetsListAdapter.setMaxHorizontalSpansPerRow(this.mMaxSpansPerRow);
            this.mAdapters.get(2).mWidgetsListAdapter.setMaxHorizontalSpansPerRow(this.mMaxSpansPerRow);
            z = true;
            if (this.mHasWorkProfile) {
                this.mAdapters.get(1).mWidgetsListAdapter.setMaxHorizontalSpansPerRow(this.mMaxSpansPerRow);
            }
            onRecommendedWidgetsBound();
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int measuredWidth = this.mContent.getMeasuredWidth();
        int i6 = (((((i3 - i) - measuredWidth) - this.mInsets.left) - this.mInsets.right) / 2) + this.mInsets.left;
        this.mContent.layout(i6, i5 - this.mContent.getMeasuredHeight(), measuredWidth + i6, i5);
        setTranslationShift(this.mTranslationShift);
    }

    public void notifyWidgetProvidersChanged() {
        ((Launcher) this.mActivityContext).refreshAndBindWidgetsForPackageUser((PackageUserKey) null);
    }

    public void onWidgetsBound() {
        if (!this.mIsInSearchMode) {
            List<WidgetsListBaseEntry> allWidgets = ((Launcher) this.mActivityContext).getPopupDataProvider().getAllWidgets();
            boolean z = false;
            AdapterHolder adapterHolder = this.mAdapters.get(0);
            adapterHolder.mWidgetsListAdapter.setWidgets(allWidgets);
            if (this.mHasWorkProfile) {
                this.mViewPager.setVisibility(0);
                this.mSearchScrollController.mTabBar.setVisibility(0);
                this.mAdapters.get(1).mWidgetsListAdapter.setWidgets(allWidgets);
                onActivePageChanged(this.mViewPager.getCurrentPage());
            } else {
                updateRecyclerViewVisibility(adapterHolder);
            }
            if (!this.mAdapters.get(0).mWidgetsListAdapter.hasVisibleEntries() || (this.mHasWorkProfile && this.mAdapters.get(1).mWidgetsListAdapter.hasVisibleEntries())) {
                z = true;
            }
            if (this.mIsNoWidgetsViewNeeded != z) {
                this.mIsNoWidgetsViewNeeded = z;
                onRecommendedWidgetsBound();
            }
        }
    }

    public void enterSearchMode() {
        if (!this.mIsInSearchMode) {
            setViewVisibilityBasedOnSearch(true);
            attachScrollbarToRecyclerView(this.mAdapters.get(2).mWidgetsRecyclerView);
            ((Launcher) this.mActivityContext).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_WIDGETSTRAY_SEARCHED);
        }
    }

    public void exitSearchMode() {
        if (this.mIsInSearchMode) {
            onSearchResults(new ArrayList());
            setViewVisibilityBasedOnSearch(false);
            if (this.mHasWorkProfile) {
                this.mViewPager.snapToPage(0);
            }
            attachScrollbarToRecyclerView(this.mAdapters.get(0).mWidgetsRecyclerView);
        }
    }

    public void onSearchResults(List<WidgetsListBaseEntry> list) {
        this.mAdapters.get(2).mWidgetsListAdapter.setWidgetsOnSearch(list);
        updateRecyclerViewVisibility(this.mAdapters.get(2));
        this.mAdapters.get(2).mWidgetsRecyclerView.scrollToTop();
    }

    private void setViewVisibilityBasedOnSearch(boolean z) {
        this.mIsInSearchMode = z;
        if (z) {
            this.mSearchScrollController.mRecommendedWidgetsTable.setVisibility(8);
            if (this.mHasWorkProfile) {
                this.mViewPager.setVisibility(8);
                this.mSearchScrollController.mTabBar.setVisibility(8);
            } else {
                this.mAdapters.get(0).mWidgetsRecyclerView.setVisibility(8);
            }
            updateRecyclerViewVisibility(this.mAdapters.get(2));
            this.mNoWidgetsView.setVisibility(8);
            return;
        }
        this.mAdapters.get(2).mWidgetsRecyclerView.setVisibility(8);
        onRecommendedWidgetsBound();
        onWidgetsBound();
    }

    private void resetExpandedHeaders() {
        this.mAdapters.get(0).mWidgetsListAdapter.resetExpandedHeader();
        this.mAdapters.get(1).mWidgetsListAdapter.resetExpandedHeader();
    }

    public void onRecommendedWidgetsBound() {
        if (!this.mIsInSearchMode) {
            List<WidgetItem> recommendedWidgets = ((Launcher) this.mActivityContext).getPopupDataProvider().getRecommendedWidgets();
            WidgetsRecommendationTableLayout widgetsRecommendationTableLayout = this.mSearchScrollController.mRecommendedWidgetsTable;
            if (recommendedWidgets.size() > 0) {
                float f = 0.0f;
                if (this.mIsNoWidgetsViewNeeded) {
                    Rect rect = new Rect();
                    this.mNoWidgetsView.getPaint().getTextBounds(this.mNoWidgetsView.getText().toString(), 0, this.mNoWidgetsView.getText().length(), rect);
                    f = (float) rect.height();
                }
                doMeasure(View.MeasureSpec.makeMeasureSpec(((Launcher) this.mActivityContext).getDeviceProfile().availableWidthPx, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(((Launcher) this.mActivityContext).getDeviceProfile().availableHeightPx, BasicMeasure.EXACTLY));
                widgetsRecommendationTableLayout.setRecommendedWidgets(WidgetsTableUtils.groupWidgetItemsIntoTableWithoutReordering(recommendedWidgets, this.mMaxSpansPerRow), (((float) ((this.mContent.getMeasuredHeight() - this.mTabsHeight) - getHeaderViewHeight())) - f) * 0.75f);
                return;
            }
            widgetsRecommendationTableLayout.setVisibility(8);
        }
    }

    private void open(boolean z) {
        if (z) {
            if (getPopupContainer().getInsets().bottom > 0) {
                this.mContent.setAlpha(0.0f);
                setTranslationShift(0.3f);
            }
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setDuration(DEFAULT_OPEN_DURATION).setInterpolator(AnimationUtils.loadInterpolator(getContext(), AndroidResources.LINEAR_OUT_SLOW_IN));
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    WidgetsFullSheet.this.mOpenCloseAnimator.removeListener(this);
                }
            });
            post(new Runnable() {
                public final void run() {
                    WidgetsFullSheet.this.lambda$open$5$WidgetsFullSheet();
                }
            });
            return;
        }
        setTranslationShift(0.0f);
        post(new Runnable() {
            public final void run() {
                WidgetsFullSheet.this.announceAccessibilityChanges();
            }
        });
    }

    public /* synthetic */ void lambda$open$5$WidgetsFullSheet() {
        this.mOpenCloseAnimator.start();
        this.mContent.animate().alpha(1.0f).setDuration(150);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        handleClose(z, DEFAULT_OPEN_DURATION);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mNoIntercept = false;
            RecyclerViewFastScroller scrollbar = getRecyclerView().getScrollbar();
            if (scrollbar.getThumbOffsetY() >= 0 && getPopupContainer().isEventOverView(scrollbar, motionEvent)) {
                this.mNoIntercept = true;
            } else if (getPopupContainer().isEventOverView(this.mContent, motionEvent)) {
                this.mNoIntercept = !getRecyclerView().shouldContainerScroll(motionEvent, getPopupContainer());
            }
            if (this.mSearchScrollController.mSearchBar.isSearchBarFocused() && !getPopupContainer().isEventOverView(this.mSearchScrollController.mSearchBarContainer, motionEvent)) {
                this.mSearchScrollController.mSearchBar.clearSearchBarFocus();
            }
        }
        return super.onControllerInterceptTouchEvent(motionEvent);
    }

    public static WidgetsFullSheet show(Launcher launcher, boolean z) {
        WidgetsFullSheet widgetsFullSheet = (WidgetsFullSheet) launcher.getLayoutInflater().inflate(R.layout.widgets_full_sheet, launcher.getDragLayer(), false);
        widgetsFullSheet.attachToContainer();
        widgetsFullSheet.mIsOpen = true;
        widgetsFullSheet.open(z);
        return widgetsFullSheet;
    }

    public static WidgetsRecyclerView getWidgetsView(Launcher launcher) {
        return (WidgetsRecyclerView) launcher.findViewById(R.id.primary_widgets_list_view);
    }

    public void addHintCloseAnim(float f, Interpolator interpolator, PendingAnimation pendingAnimation) {
        pendingAnimation.setFloat(getRecyclerView(), LauncherAnimUtils.VIEW_TRANSLATE_Y, -f, interpolator);
        pendingAnimation.setViewAlpha(getRecyclerView(), 0.5f, interpolator);
    }

    /* access modifiers changed from: protected */
    public void onCloseComplete() {
        super.onCloseComplete();
        removeCallbacks(this.mShowEducationTipTask);
        ArrowTipView arrowTipView = this.mLatestEducationalTip;
        if (arrowTipView != null) {
            arrowTipView.close(false);
        }
        AccessibilityManagerCompat.sendStateEventToTest(getContext(), 0);
    }

    public int getHeaderViewHeight() {
        return measureHeightWithVerticalMargins(this.mSearchScrollController.mHeaderTitle) + measureHeightWithVerticalMargins(this.mSearchScrollController.mSearchBarContainer);
    }

    private static int measureHeightWithVerticalMargins(View view) {
        if (view.getVisibility() != 0) {
            return 0;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return view.getMeasuredHeight() + marginLayoutParams.bottomMargin + marginLayoutParams.topMargin;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mIsInSearchMode) {
            this.mSearchScrollController.mSearchBar.reset();
        }
    }

    public boolean onBackPressed() {
        if (!this.mIsInSearchMode) {
            return super.onBackPressed();
        }
        this.mSearchScrollController.mSearchBar.reset();
        return true;
    }

    public void onDragStart(boolean z, float f) {
        super.onDragStart(z, f);
        getWindowInsetsController().hide(WindowInsets.Type.ime());
    }

    private View getViewToShowEducationTip() {
        int i;
        if (this.mSearchScrollController.mRecommendedWidgetsTable.getVisibility() == 0 && this.mSearchScrollController.mRecommendedWidgetsTable.getChildCount() > 0) {
            return ((ViewGroup) this.mSearchScrollController.mRecommendedWidgetsTable.getChildAt(0)).getChildAt(0);
        }
        SparseArray<AdapterHolder> sparseArray = this.mAdapters;
        if (this.mIsInSearchMode) {
            i = 2;
        } else {
            PersonalWorkPagedView personalWorkPagedView = this.mViewPager;
            if (personalWorkPagedView == null) {
                i = 0;
            } else {
                i = personalWorkPagedView.getCurrentPage();
            }
        }
        AdapterHolder adapterHolder = sparseArray.get(i);
        IntStream range = IntStream.range(0, adapterHolder.mWidgetsListAdapter.getItemCount());
        WidgetsRecyclerView access$300 = adapterHolder.mWidgetsRecyclerView;
        Objects.requireNonNull(access$300);
        WidgetsRowViewHolder widgetsRowViewHolder = (WidgetsRowViewHolder) range.mapToObj(new IntFunction() {
            public final Object apply(int i) {
                return WidgetsRecyclerView.this.findViewHolderForAdapterPosition(i);
            }
        }).filter($$Lambda$WidgetsFullSheet$_siPoT3akOB4LPX_GICj1Ab37Ak.INSTANCE).findFirst().orElse((Object) null);
        if (widgetsRowViewHolder != null) {
            return ((ViewGroup) widgetsRowViewHolder.tableContainer.getChildAt(0)).getChildAt(0);
        }
        return null;
    }

    static /* synthetic */ boolean lambda$getViewToShowEducationTip$7(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof WidgetsRowViewHolder;
    }

    private WidgetsEduView showEducationDialog() {
        ((Launcher) this.mActivityContext).getSharedPrefs().edit().putBoolean(KEY_WIDGETS_EDUCATION_DIALOG_SEEN, true).apply();
        return WidgetsEduView.showEducationDialog((Launcher) this.mActivityContext);
    }

    /* access modifiers changed from: protected */
    public boolean hasSeenEducationDialog() {
        if (((Launcher) this.mActivityContext).getSharedPrefs().getBoolean(KEY_WIDGETS_EDUCATION_DIALOG_SEEN, false) || Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return true;
        }
        return false;
    }

    private void setUpEducationViewsIfNeeded() {
        if (!hasSeenEducationDialog()) {
            postDelayed(new Runnable() {
                public final void run() {
                    WidgetsFullSheet.this.lambda$setUpEducationViewsIfNeeded$9$WidgetsFullSheet();
                }
            }, EDUCATION_DIALOG_DELAY_MS);
        } else if (!hasSeenEducationTip()) {
            addOnLayoutChangeListener(this.mLayoutChangeListenerToShowTips);
        }
    }

    public /* synthetic */ void lambda$setUpEducationViewsIfNeeded$9$WidgetsFullSheet() {
        showEducationDialog().addOnCloseListener(new AbstractSlideInView.OnCloseListener() {
            public final void onSlideInViewClosed() {
                WidgetsFullSheet.this.lambda$setUpEducationViewsIfNeeded$8$WidgetsFullSheet();
            }
        });
    }

    public /* synthetic */ void lambda$setUpEducationViewsIfNeeded$8$WidgetsFullSheet() {
        if (!hasSeenEducationTip()) {
            addOnLayoutChangeListener(this.mLayoutChangeListenerToShowTips);
            requestLayout();
        }
    }

    private final class AdapterHolder {
        static final int PRIMARY = 0;
        static final int SEARCH = 2;
        static final int WORK = 1;
        /* access modifiers changed from: private */
        public final int mAdapterType;
        /* access modifiers changed from: private */
        public final WidgetsListAdapter mWidgetsListAdapter;
        private final DefaultItemAnimator mWidgetsListItemAnimator;
        /* access modifiers changed from: private */
        public WidgetsRecyclerView mWidgetsRecyclerView;

        AdapterHolder(int i) {
            this.mAdapterType = i;
            Context context = WidgetsFullSheet.this.getContext();
            WidgetsListAdapter widgetsListAdapter = new WidgetsListAdapter(context, LayoutInflater.from(context), LauncherAppState.getInstance(context).getIconCache(), new IntSupplier() {
                public final int getAsInt() {
                    return WidgetsFullSheet.AdapterHolder.this.getEmptySpaceHeight();
                }
            }, WidgetsFullSheet.this, WidgetsFullSheet.this);
            this.mWidgetsListAdapter = widgetsListAdapter;
            widgetsListAdapter.setHasStableIds(true);
            if (i == 0) {
                widgetsListAdapter.setFilter(WidgetsFullSheet.this.mPrimaryWidgetsFilter);
            } else if (i == 1) {
                widgetsListAdapter.setFilter(WidgetsFullSheet.this.mWorkWidgetsFilter);
            }
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            this.mWidgetsListItemAnimator = defaultItemAnimator;
            defaultItemAnimator.setSupportsChangeAnimations(false);
        }

        /* access modifiers changed from: private */
        public int getEmptySpaceHeight() {
            return WidgetsFullSheet.this.mSearchScrollController.getHeaderHeight();
        }

        /* access modifiers changed from: package-private */
        public void setup(WidgetsRecyclerView widgetsRecyclerView) {
            this.mWidgetsRecyclerView = widgetsRecyclerView;
            widgetsRecyclerView.setAdapter(this.mWidgetsListAdapter);
            this.mWidgetsRecyclerView.setItemAnimator(this.mWidgetsListItemAnimator);
            this.mWidgetsRecyclerView.setHeaderViewDimensionsProvider(WidgetsFullSheet.this);
            this.mWidgetsRecyclerView.setEdgeEffectFactory(((SpringRelativeLayout) WidgetsFullSheet.this.mContent).createEdgeEffectFactory());
            int i = this.mAdapterType;
            if (i == 0 || i == 1) {
                this.mWidgetsRecyclerView.addOnAttachStateChangeListener(WidgetsFullSheet.this.mBindScrollbarInSearchMode);
            }
            this.mWidgetsListAdapter.setMaxHorizontalSpansPerRow(WidgetsFullSheet.this.mMaxSpansPerRow);
        }
    }
}
