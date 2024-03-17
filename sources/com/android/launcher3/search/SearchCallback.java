package com.android.launcher3.search;

import java.util.ArrayList;

public interface SearchCallback<T> {
    void clearSearchResult();

    void onSearchResult(String str, ArrayList<T> arrayList);
}
