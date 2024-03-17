package com.android.launcher3.allapps.search;

import android.net.Uri;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.allapps.BaseAdapterProvider;
import com.android.launcher3.views.ActivityContext;

public abstract class SearchAdapterProvider<T extends ActivityContext> extends BaseAdapterProvider {
    protected final T mLauncher;

    public abstract RecyclerView.ItemDecoration getDecorator();

    public abstract View getHighlightedItem();

    public abstract boolean launchHighlightedItem();

    public void onSliceStatusUpdate(Uri uri) {
    }

    public SearchAdapterProvider(T t) {
        this.mLauncher = t;
    }
}
