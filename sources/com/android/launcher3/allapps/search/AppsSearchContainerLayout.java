package com.android.launcher3.allapps.search;

import android.content.Context;
import android.graphics.Rect;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.Insettable;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.ActivityAllAppsContainerView;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.allapps.SearchUiManager;
import com.android.launcher3.search.SearchCallback;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;

public class AppsSearchContainerLayout extends ExtendedEditText implements SearchUiManager, SearchCallback<BaseAllAppsAdapter.AdapterItem>, AllAppsStore.OnUpdateListener, Insettable {
    private ActivityAllAppsContainerView<?> mAppsView;
    private final int mContentOverlap;
    private final ActivityContext mLauncher;
    private final AllAppsSearchBarController mSearchBarController;
    private final SpannableStringBuilder mSearchQueryBuilder;

    public ExtendedEditText getEditText() {
        return this;
    }

    public AppsSearchContainerLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppsSearchContainerLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppsSearchContainerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLauncher = (ActivityContext) ActivityContext.lookupContext(context);
        this.mSearchBarController = new AllAppsSearchBarController();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        this.mSearchQueryBuilder = spannableStringBuilder;
        Selection.setSelection(spannableStringBuilder, 0);
        setHint(Utilities.prefixTextWithIcon(getContext(), R.drawable.ic_allapps_search, getHint()));
        this.mContentOverlap = getResources().getDimensionPixelSize(R.dimen.all_apps_search_bar_content_overlap);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAppsView.getAppsStore().addUpdateListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAppsView.getAppsStore().removeUpdateListener(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int size = (View.MeasureSpec.getSize(i) - this.mAppsView.getActiveRecyclerView().getPaddingLeft()) - this.mAppsView.getActiveRecyclerView().getPaddingRight();
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((size - (DeviceProfile.calculateCellWidth(size, deviceProfile.cellLayoutBorderSpacePx.x, deviceProfile.numShownHotseatIcons) - Math.round(((float) deviceProfile.iconSizePx) * 0.92f))) + getPaddingLeft() + getPaddingRight(), BasicMeasure.EXACTLY), i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        View view = (View) getParent();
        setTranslationX((float) ((view.getPaddingLeft() + ((((view.getWidth() - view.getPaddingLeft()) - view.getPaddingRight()) - (i3 - i)) / 2)) - i));
        offsetTopAndBottom(this.mContentOverlap);
    }

    public void initializeSearch(ActivityAllAppsContainerView<?> activityAllAppsContainerView) {
        this.mAppsView = activityAllAppsContainerView;
        this.mSearchBarController.initialize(new DefaultAppSearchAlgorithm(getContext()), this, this.mLauncher, this);
    }

    public void onAppsUpdated() {
        this.mSearchBarController.refreshSearchResult();
    }

    public void resetSearch() {
        this.mSearchBarController.reset();
    }

    public void preDispatchKeyEvent(KeyEvent keyEvent) {
        if (!this.mSearchBarController.isSearchFieldFocused() && keyEvent.getAction() == 0) {
            int unicodeChar = keyEvent.getUnicodeChar();
            if ((unicodeChar > 0 && !Character.isWhitespace(unicodeChar) && !Character.isSpaceChar(unicodeChar)) && TextKeyListener.getInstance().onKeyDown(this, this.mSearchQueryBuilder, keyEvent.getKeyCode(), keyEvent) && this.mSearchQueryBuilder.length() > 0) {
                this.mSearchBarController.focusSearchField();
            }
        }
    }

    public void onSearchResult(String str, ArrayList<BaseAllAppsAdapter.AdapterItem> arrayList) {
        if (arrayList != null) {
            this.mAppsView.setSearchResults(arrayList);
            this.mAppsView.setLastSearchQuery(str);
        }
    }

    public void clearSearchResult() {
        this.mAppsView.setSearchResults((ArrayList<BaseAllAppsAdapter.AdapterItem>) null);
        this.mSearchQueryBuilder.clear();
        this.mSearchQueryBuilder.clearSpans();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
        this.mAppsView.onClearSearchResult();
    }

    public void setInsets(Rect rect) {
        ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = rect.top;
        requestLayout();
    }
}
