package com.android.launcher3.allapps;

import android.view.KeyEvent;
import com.android.launcher3.ExtendedEditText;

public interface SearchUiManager {
    boolean getBackgroundVisibility() {
        return false;
    }

    ExtendedEditText getEditText();

    boolean inZeroState() {
        return false;
    }

    void initializeSearch(ActivityAllAppsContainerView<?> activityAllAppsContainerView);

    void preDispatchKeyEvent(KeyEvent keyEvent) {
    }

    void refreshResults() {
    }

    void resetSearch();

    void setBackgroundVisibility(boolean z, float f) {
    }

    void setFocusedResultTitle(CharSequence charSequence) {
    }
}
