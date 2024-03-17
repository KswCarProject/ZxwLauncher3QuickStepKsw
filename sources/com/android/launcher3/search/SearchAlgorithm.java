package com.android.launcher3.search;

public interface SearchAlgorithm<T> {
    void cancel(boolean z);

    void destroy() {
    }

    void doSearch(String str, SearchCallback<T> searchCallback);

    void doSearch(String str, String[] strArr, SearchCallback<T> searchCallback) {
        doSearch(str, searchCallback);
    }
}
