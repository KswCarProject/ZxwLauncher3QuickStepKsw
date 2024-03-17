package com.android.launcher3.allapps.search;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.SuggestionSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.search.SearchAlgorithm;
import com.android.launcher3.search.SearchCallback;
import com.android.launcher3.views.ActivityContext;

public class AllAppsSearchBarController implements TextWatcher, TextView.OnEditorActionListener, ExtendedEditText.OnBackKeyListener, View.OnFocusChangeListener {
    protected SearchCallback<BaseAllAppsAdapter.AdapterItem> mCallback;
    protected ExtendedEditText mInput;
    protected ActivityContext mLauncher;
    protected String mQuery;
    protected SearchAlgorithm<BaseAllAppsAdapter.AdapterItem> mSearchAlgorithm;
    private String[] mTextConversions;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void setVisibility(int i) {
        this.mInput.setVisibility(i);
    }

    public final void initialize(SearchAlgorithm<BaseAllAppsAdapter.AdapterItem> searchAlgorithm, ExtendedEditText extendedEditText, ActivityContext activityContext, SearchCallback<BaseAllAppsAdapter.AdapterItem> searchCallback) {
        this.mCallback = searchCallback;
        this.mLauncher = activityContext;
        this.mInput = extendedEditText;
        extendedEditText.addTextChangedListener(this);
        this.mInput.setOnEditorActionListener(this);
        this.mInput.setOnBackKeyListener(this);
        this.mInput.setOnFocusChangeListener(this);
        this.mSearchAlgorithm = searchAlgorithm;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        this.mTextConversions = extractTextConversions(charSequence);
    }

    private static String[] extractTextConversions(CharSequence charSequence) {
        if (!(charSequence instanceof SpannableStringBuilder)) {
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
        SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) spannableStringBuilder.getSpans(0, charSequence.length(), SuggestionSpan.class);
        if (suggestionSpanArr == null || suggestionSpanArr.length <= 0) {
            return null;
        }
        spannableStringBuilder.removeSpan(suggestionSpanArr[0]);
        return suggestionSpanArr[0].getSuggestions();
    }

    public void afterTextChanged(Editable editable) {
        String obj = editable.toString();
        this.mQuery = obj;
        if (obj.isEmpty()) {
            this.mSearchAlgorithm.cancel(true);
            this.mCallback.clearSearchResult();
            return;
        }
        this.mSearchAlgorithm.cancel(false);
        this.mSearchAlgorithm.doSearch(this.mQuery, this.mTextConversions, this.mCallback);
    }

    public void refreshSearchResult() {
        if (!TextUtils.isEmpty(this.mQuery)) {
            this.mSearchAlgorithm.cancel(false);
            this.mSearchAlgorithm.doSearch(this.mQuery, this.mCallback);
        }
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (i != 3 && i != 2) {
            return false;
        }
        StatsLogManager.StatsLogger logger = this.mLauncher.getStatsLogManager().logger();
        if (i == 3) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_QUICK_SEARCH_WITH_IME;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_FOCUSED_ITEM_SELECTED_WITH_IME;
        }
        logger.log(launcherEvent);
        return this.mLauncher.getAppsView().getMainAdapterProvider().launchHighlightedItem();
    }

    public boolean onBackKey() {
        if (!Utilities.trim(this.mInput.getEditableText().toString()).isEmpty()) {
            return false;
        }
        reset();
        return true;
    }

    public void onFocusChange(View view, boolean z) {
        if (!z && !FeatureFlags.ENABLE_DEVICE_SEARCH.get()) {
            this.mInput.hideKeyboard();
        }
    }

    public void reset() {
        this.mCallback.clearSearchResult();
        this.mInput.reset();
        this.mQuery = null;
    }

    public void focusSearchField() {
        this.mInput.showKeyboard();
    }

    public boolean isSearchFieldFocused() {
        return this.mInput.isFocused();
    }
}
