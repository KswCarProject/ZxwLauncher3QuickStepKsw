package com.android.launcher3.allapps.search;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.views.AppLauncher;

public class DefaultSearchAdapterProvider extends SearchAdapterProvider<AppLauncher> {
    private final RecyclerView.ItemDecoration mDecoration = new RecyclerView.ItemDecoration() {
        public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            super.onDraw(canvas, recyclerView, state);
        }
    };
    private View mHighlightedView;

    public boolean isViewSupported(int i) {
        return false;
    }

    public BaseAllAppsAdapter.ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, int i) {
        return null;
    }

    public DefaultSearchAdapterProvider(AppLauncher appLauncher) {
        super(appLauncher);
    }

    public void onBindView(BaseAllAppsAdapter.ViewHolder viewHolder, int i) {
        if (i == 0) {
            this.mHighlightedView = viewHolder.itemView;
        }
    }

    public boolean launchHighlightedItem() {
        View view = this.mHighlightedView;
        if (!(view instanceof BubbleTextView) || !(view.getTag() instanceof ItemInfo)) {
            return false;
        }
        ItemInfo itemInfo = (ItemInfo) this.mHighlightedView.getTag();
        return ((AppLauncher) this.mLauncher).startActivitySafely(this.mHighlightedView, itemInfo.getIntent(), itemInfo);
    }

    public View getHighlightedItem() {
        return this.mHighlightedView;
    }

    public RecyclerView.ItemDecoration getDecorator() {
        return this.mDecoration;
    }
}
