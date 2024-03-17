package com.android.launcher3.allapps;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DeviceProfile.DeviceProfileListenable;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.allapps.BaseAllAppsContainerView;
import com.android.launcher3.allapps.search.SearchAdapterProvider;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.AppLauncher;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ActivityAllAppsContainerView<T extends Context & AppLauncher & DeviceProfile.DeviceProfileListenable> extends BaseAllAppsContainerView<T> {
    private boolean mIsSearching;
    private View mSearchContainer;
    protected SearchUiManager mSearchUiManager;

    public ActivityAllAppsContainerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ActivityAllAppsContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ActivityAllAppsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public SearchUiManager getSearchUiManager() {
        return this.mSearchUiManager;
    }

    public View getSearchView() {
        return this.mSearchContainer;
    }

    public void setLastSearchQuery(String str) {
        $$Lambda$ActivityAllAppsContainerView$D6tmb0nsI2JYtIm_S5hTQPE2u4 r1 = new View.OnClickListener(PackageManagerHelper.getMarketSearchIntent(this.mActivityContext, str)) {
            public final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ActivityAllAppsContainerView.this.lambda$setLastSearchQuery$0$ActivityAllAppsContainerView(this.f$1, view);
            }
        };
        for (int i = 0; i < this.mAH.size(); i++) {
            ((BaseAllAppsContainerView.AdapterHolder) this.mAH.get(i)).mAdapter.setLastSearchQuery(str, r1);
        }
        this.mIsSearching = true;
        rebindAdapters();
        this.mHeader.setCollapsed(true);
    }

    public /* synthetic */ void lambda$setLastSearchQuery$0$ActivityAllAppsContainerView(Intent intent, View view) {
        ((AppLauncher) this.mActivityContext).startActivitySafely(view, intent, (ItemInfo) null);
    }

    public void onClearSearchResult() {
        this.mIsSearching = false;
        this.mHeader.setCollapsed(false);
        rebindAdapters();
        this.mHeader.reset(false);
    }

    public void setSearchResults(ArrayList<BaseAllAppsAdapter.AdapterItem> arrayList) {
        if (getSearchResultList().setSearchResults(arrayList)) {
            for (int i = 0; i < this.mAH.size(); i++) {
                if (((BaseAllAppsContainerView.AdapterHolder) this.mAH.get(i)).mRecyclerView != null) {
                    ((BaseAllAppsContainerView.AdapterHolder) this.mAH.get(i)).mRecyclerView.onSearchResultsChanged();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public final SearchAdapterProvider<?> createMainAdapterProvider() {
        return ((ActivityContext) this.mActivityContext).createSearchAdapterProvider(this);
    }

    public boolean shouldContainerScroll(MotionEvent motionEvent) {
        if (((ActivityContext) this.mActivityContext).getDragLayer().isEventOverView(this.mSearchContainer, motionEvent)) {
            return true;
        }
        return super.shouldContainerScroll(motionEvent);
    }

    public void reset(boolean z) {
        super.reset(z);
        this.mSearchUiManager.resetSearch();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(R.id.search_container_all_apps);
        this.mSearchContainer = findViewById;
        SearchUiManager searchUiManager = (SearchUiManager) findViewById;
        this.mSearchUiManager = searchUiManager;
        searchUiManager.initializeSearch(this);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        this.mSearchUiManager.preDispatchKeyEvent(keyEvent);
        return super.dispatchKeyEvent(keyEvent);
    }

    public String getDescription() {
        if (this.mUsingTabs || !isSearching()) {
            return super.getDescription();
        }
        return getContext().getString(R.string.all_apps_search_results);
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowTabs() {
        return super.shouldShowTabs() && !isSearching();
    }

    public boolean isSearching() {
        return this.mIsSearching;
    }

    /* access modifiers changed from: protected */
    public void rebindAdapters(boolean z) {
        super.rebindAdapters(z);
        if (FeatureFlags.ENABLE_DEVICE_SEARCH.get() && getMainAdapterProvider().getDecorator() != null) {
            this.mAH.stream().map($$Lambda$ActivityAllAppsContainerView$l2C6Y2f8CzVqQIfZIBsMIQdUAzc.INSTANCE).filter($$Lambda$ActivityAllAppsContainerView$0M0on7eczuuRVRGpPIO3mv09sD4.INSTANCE).forEach(new Consumer() {
                public final void accept(Object obj) {
                    ActivityAllAppsContainerView.lambda$rebindAdapters$2(RecyclerView.ItemDecoration.this, (AllAppsRecyclerView) obj);
                }
            });
        }
    }

    static /* synthetic */ void lambda$rebindAdapters$2(RecyclerView.ItemDecoration itemDecoration, AllAppsRecyclerView allAppsRecyclerView) {
        allAppsRecyclerView.removeItemDecoration(itemDecoration);
        allAppsRecyclerView.addItemDecoration(itemDecoration);
    }

    /* access modifiers changed from: protected */
    public View replaceAppsRVContainer(boolean z) {
        View replaceAppsRVContainer = super.replaceAppsRVContainer(z);
        removeCustomRules(replaceAppsRVContainer);
        removeCustomRules(getSearchRecyclerView());
        if (FeatureFlags.ENABLE_FLOATING_SEARCH_BAR.get()) {
            alignParentTop(replaceAppsRVContainer, z);
            alignParentTop(getSearchRecyclerView(), false);
            layoutAboveSearchContainer(replaceAppsRVContainer);
            layoutAboveSearchContainer(getSearchRecyclerView());
        } else {
            layoutBelowSearchContainer(replaceAppsRVContainer, z);
            layoutBelowSearchContainer(getSearchRecyclerView(), false);
        }
        return replaceAppsRVContainer;
    }

    /* access modifiers changed from: package-private */
    public void setupHeader() {
        super.setupHeader();
        removeCustomRules(this.mHeader);
        if (FeatureFlags.ENABLE_FLOATING_SEARCH_BAR.get()) {
            alignParentTop(this.mHeader, false);
        } else {
            layoutBelowSearchContainer(this.mHeader, false);
        }
    }

    /* access modifiers changed from: protected */
    public void updateHeaderScroll(int i) {
        super.updateHeaderScroll(i);
        if (this.mSearchUiManager.getEditText() != null) {
            float f = (float) i;
            float boundToRange = Utilities.boundToRange(f / this.mHeaderThreshold, 0.0f, 1.0f);
            boolean backgroundVisibility = this.mSearchUiManager.getBackgroundVisibility();
            if (i == 0 && !isSearching()) {
                backgroundVisibility = true;
            } else if (f > this.mHeaderThreshold) {
                backgroundVisibility = false;
            }
            this.mSearchUiManager.setBackgroundVisibility(backgroundVisibility, 1.0f - boundToRange);
        }
    }

    /* access modifiers changed from: protected */
    public int getHeaderColor(float f) {
        return ColorUtils.setAlphaComponent(super.getHeaderColor(f), (int) (this.mSearchContainer.getAlpha() * 255.0f));
    }

    public int getHeaderBottom() {
        if (FeatureFlags.ENABLE_FLOATING_SEARCH_BAR.get()) {
            return super.getHeaderBottom();
        }
        return super.getHeaderBottom() + this.mSearchContainer.getBottom();
    }

    private void layoutBelowSearchContainer(View view, boolean z) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.addRule(6, R.id.search_container_all_apps);
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.all_apps_header_top_margin);
            if (z) {
                dimensionPixelSize += getContext().getResources().getDimensionPixelSize(R.dimen.all_apps_header_pill_height);
            }
            layoutParams.topMargin = dimensionPixelSize;
        }
    }

    private void layoutAboveSearchContainer(View view) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) view.getLayoutParams()).addRule(2, R.id.search_container_all_apps);
        }
    }

    private void alignParentTop(View view, boolean z) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.addRule(10);
            layoutParams.topMargin = z ? getContext().getResources().getDimensionPixelSize(R.dimen.all_apps_header_pill_height) : 0;
        }
    }

    private void removeCustomRules(View view) {
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.removeRule(2);
            layoutParams.removeRule(6);
            layoutParams.removeRule(10);
        }
    }

    /* access modifiers changed from: protected */
    public BaseAllAppsAdapter<T> createAdapter(AlphabeticalAppsList<T> alphabeticalAppsList, BaseAdapterProvider[] baseAdapterProviderArr) {
        return new AllAppsGridAdapter(this.mActivityContext, getLayoutInflater(), alphabeticalAppsList, baseAdapterProviderArr);
    }
}
