package com.android.launcher3.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.R;

public class ViewCache {
    protected final SparseArray<CacheEntry> mCache = new SparseArray<>();

    public void setCacheSize(int i, int i2) {
        this.mCache.put(i, new CacheEntry(i2));
    }

    public <T extends View> T getView(int i, Context context, ViewGroup viewGroup) {
        CacheEntry cacheEntry = this.mCache.get(i);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry(1);
            this.mCache.put(i, cacheEntry);
        }
        if (cacheEntry.mCurrentSize > 0) {
            cacheEntry.mCurrentSize--;
            T t = cacheEntry.mViews[cacheEntry.mCurrentSize];
            cacheEntry.mViews[cacheEntry.mCurrentSize] = null;
            return t;
        }
        T inflate = LayoutInflater.from(context).inflate(i, viewGroup, false);
        inflate.setTag(R.id.cache_entry_tag_id, cacheEntry);
        return inflate;
    }

    public void recycleView(int i, View view) {
        CacheEntry cacheEntry = this.mCache.get(i);
        if (cacheEntry == view.getTag(R.id.cache_entry_tag_id) && cacheEntry != null && cacheEntry.mCurrentSize < cacheEntry.mMaxSize) {
            cacheEntry.mViews[cacheEntry.mCurrentSize] = view;
            cacheEntry.mCurrentSize++;
        }
    }

    private static class CacheEntry {
        int mCurrentSize = 0;
        final int mMaxSize;
        final View[] mViews;

        public CacheEntry(int i) {
            this.mMaxSize = i;
            this.mViews = new View[i];
        }
    }
}
