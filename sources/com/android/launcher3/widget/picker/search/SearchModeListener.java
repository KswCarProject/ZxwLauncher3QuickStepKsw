package com.android.launcher3.widget.picker.search;

import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import java.util.List;

public interface SearchModeListener {
    void enterSearchMode();

    void exitSearchMode();

    void onSearchResults(List<WidgetsListBaseEntry> list);
}
