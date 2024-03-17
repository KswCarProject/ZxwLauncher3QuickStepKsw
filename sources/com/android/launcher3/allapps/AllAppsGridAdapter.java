package com.android.launcher3.allapps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityRecordCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.views.ActivityContext;
import java.util.List;

public class AllAppsGridAdapter<T extends Context & ActivityContext> extends BaseAllAppsAdapter<T> {
    public static final String TAG = "AppsGridAdapter";
    /* access modifiers changed from: private */
    public final GridLayoutManager mGridLayoutMgr;
    private final AllAppsGridAdapter<T>.GridSpanSizer mGridSizer;

    public AllAppsGridAdapter(T t, LayoutInflater layoutInflater, AlphabeticalAppsList alphabeticalAppsList, BaseAdapterProvider[] baseAdapterProviderArr) {
        super(t, layoutInflater, alphabeticalAppsList, baseAdapterProviderArr);
        AllAppsGridAdapter<T>.GridSpanSizer gridSpanSizer = new GridSpanSizer();
        this.mGridSizer = gridSpanSizer;
        AppsGridLayoutManager appsGridLayoutManager = new AppsGridLayoutManager(this.mActivityContext);
        this.mGridLayoutMgr = appsGridLayoutManager;
        appsGridLayoutManager.setSpanSizeLookup(gridSpanSizer);
        setAppsPerRow(((ActivityContext) t).getDeviceProfile().numShownAllAppsColumns);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return this.mGridLayoutMgr;
    }

    public class AppsGridLayoutManager extends GridLayoutManager {
        public AppsGridLayoutManager(Context context) {
            super(context, 1, 1, false);
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            AccessibilityRecordCompat asRecord = AccessibilityEventCompat.asRecord(accessibilityEvent);
            asRecord.setItemCount(AllAppsGridAdapter.this.mApps.getNumFilteredApps());
            asRecord.setFromIndex(Math.max(0, asRecord.getFromIndex() - getRowsNotForAccessibility(asRecord.getFromIndex())));
            asRecord.setToIndex(Math.max(0, asRecord.getToIndex() - getRowsNotForAccessibility(asRecord.getToIndex())));
        }

        public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
            return super.getRowCountForAccessibility(recycler, state) - getRowsNotForAccessibility(AllAppsGridAdapter.this.mApps.getAdapterItems().size() - 1);
        }

        public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfoForItem(recycler, state, view, accessibilityNodeInfoCompat);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            AccessibilityNodeInfoCompat.CollectionItemInfoCompat collectionItemInfo = accessibilityNodeInfoCompat.getCollectionItemInfo();
            if ((layoutParams instanceof GridLayoutManager.LayoutParams) && collectionItemInfo != null) {
                accessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(collectionItemInfo.getRowIndex() - getRowsNotForAccessibility(((GridLayoutManager.LayoutParams) layoutParams).getViewAdapterPosition()), collectionItemInfo.getRowSpan(), collectionItemInfo.getColumnIndex(), collectionItemInfo.getColumnSpan(), collectionItemInfo.isHeading(), collectionItemInfo.isSelected()));
            }
        }

        private int getRowsNotForAccessibility(int i) {
            List<BaseAllAppsAdapter.AdapterItem> adapterItems = AllAppsGridAdapter.this.mApps.getAdapterItems();
            int max = Math.max(i, adapterItems.size() - 1);
            int i2 = 0;
            int i3 = 0;
            while (i2 <= max && i2 < adapterItems.size()) {
                if (!BaseAllAppsAdapter.isViewType(adapterItems.get(i2).viewType, 2)) {
                    i3++;
                }
                i2++;
            }
            return i3;
        }
    }

    public void setAppsPerRow(int i) {
        this.mAppsPerRow = i;
        int i2 = this.mAppsPerRow;
        for (BaseAdapterProvider supportedItemsPerRowArray : this.mAdapterProviders) {
            for (int i3 : supportedItemsPerRowArray.getSupportedItemsPerRowArray()) {
                if (i2 % i3 != 0) {
                    i2 *= i3;
                }
            }
        }
        this.mGridLayoutMgr.setSpanCount(i2);
    }

    public class GridSpanSizer extends GridLayoutManager.SpanSizeLookup {
        public GridSpanSizer() {
            setSpanIndexCacheEnabled(true);
        }

        public int getSpanSize(int i) {
            int i2 = AllAppsGridAdapter.this.mApps.getAdapterItems().get(i).viewType;
            int spanCount = AllAppsGridAdapter.this.mGridLayoutMgr.getSpanCount();
            if (BaseAllAppsAdapter.isIconViewType(i2)) {
                return spanCount / AllAppsGridAdapter.this.mAppsPerRow;
            }
            BaseAdapterProvider adapterProvider = AllAppsGridAdapter.this.getAdapterProvider(i2);
            return adapterProvider != null ? spanCount / adapterProvider.getItemsPerRow(i2, AllAppsGridAdapter.this.mAppsPerRow) : spanCount;
        }
    }
}
