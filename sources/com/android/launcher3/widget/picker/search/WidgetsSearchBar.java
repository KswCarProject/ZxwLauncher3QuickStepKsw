package com.android.launcher3.widget.picker.search;

import com.android.launcher3.popup.PopupDataProvider;

public interface WidgetsSearchBar {
    void clearSearchBarFocus();

    void initialize(PopupDataProvider popupDataProvider, SearchModeListener searchModeListener);

    boolean isSearchBarFocused();

    void reset();

    void setTranslationY(float f);
}
