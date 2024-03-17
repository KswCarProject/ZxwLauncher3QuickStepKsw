package com.android.launcher3.allapps;

import android.content.Context;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.BaseAllAppsAdapter;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.LabelComparator;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlphabeticalAppsList<T extends Context & ActivityContext> implements AllAppsStore.OnUpdateListener {
    public static final String TAG = "AlphabeticalAppsList";
    private int mAccessibilityResultsCount = 0;
    private final T mActivityContext;
    private BaseAllAppsAdapter<T> mAdapter;
    private final ArrayList<BaseAllAppsAdapter.AdapterItem> mAdapterItems = new ArrayList<>();
    private final AllAppsStore mAllAppsStore;
    private AppInfoComparator mAppNameComparator;
    private final List<AppInfo> mApps = new ArrayList();
    private final List<FastScrollSectionInfo> mFastScrollerSections = new ArrayList();
    private Predicate<ItemInfo> mItemFilter;
    private int mNumAppRowsInAdapter;
    private final int mNumAppsPerRowAllApps;
    private final ArrayList<BaseAllAppsAdapter.AdapterItem> mSearchResults = new ArrayList<>();
    private final WorkAdapterProvider mWorkAdapterProvider;

    public static /* synthetic */ ArrayList lambda$E2McIAGPWBO62AkYPcfatUmDGDA() {
        return new ArrayList();
    }

    public static class FastScrollSectionInfo {
        public final int position;
        public final String sectionName;

        public FastScrollSectionInfo(String str, int i) {
            this.sectionName = str;
            this.position = i;
        }
    }

    public AlphabeticalAppsList(Context context, AllAppsStore allAppsStore, WorkAdapterProvider workAdapterProvider) {
        this.mAllAppsStore = allAppsStore;
        T lookupContext = ActivityContext.lookupContext(context);
        this.mActivityContext = lookupContext;
        this.mAppNameComparator = new AppInfoComparator(context);
        this.mWorkAdapterProvider = workAdapterProvider;
        this.mNumAppsPerRowAllApps = ((ActivityContext) lookupContext).getDeviceProfile().inv.numAllAppsColumns;
        if (allAppsStore != null) {
            allAppsStore.addUpdateListener(this);
        }
    }

    public void updateItemFilter(Predicate<ItemInfo> predicate) {
        this.mItemFilter = predicate;
        onAppsUpdated();
    }

    public void setAdapter(BaseAllAppsAdapter<T> baseAllAppsAdapter) {
        this.mAdapter = baseAllAppsAdapter;
    }

    public List<FastScrollSectionInfo> getFastScrollerSections() {
        return this.mFastScrollerSections;
    }

    public List<BaseAllAppsAdapter.AdapterItem> getAdapterItems() {
        return this.mAdapterItems;
    }

    public BaseAllAppsAdapter.AdapterItem getFocusedChild() {
        if (this.mAdapterItems.size() == 0 || getFocusedChildIndex() == -1) {
            return null;
        }
        return this.mAdapterItems.get(getFocusedChildIndex());
    }

    public int getFocusedChildIndex() {
        Iterator<BaseAllAppsAdapter.AdapterItem> it = this.mAdapterItems.iterator();
        while (it.hasNext()) {
            BaseAllAppsAdapter.AdapterItem next = it.next();
            if (next.isCountedForAccessibility()) {
                return this.mAdapterItems.indexOf(next);
            }
        }
        return -1;
    }

    public int getNumAppRows() {
        return this.mNumAppRowsInAdapter;
    }

    public int getNumFilteredApps() {
        return this.mAccessibilityResultsCount;
    }

    public boolean hasSearchResults() {
        return !this.mSearchResults.isEmpty();
    }

    public boolean hasNoFilteredResults() {
        return hasSearchResults() && this.mAccessibilityResultsCount == 0;
    }

    public boolean setSearchResults(ArrayList<BaseAllAppsAdapter.AdapterItem> arrayList) {
        if (Objects.equals(arrayList, this.mSearchResults)) {
            return false;
        }
        this.mSearchResults.clear();
        if (arrayList != null) {
            this.mSearchResults.addAll(arrayList);
        }
        updateAdapterItems();
        return true;
    }

    public void onAppsUpdated() {
        Predicate<ItemInfo> predicate;
        if (this.mAllAppsStore != null) {
            this.mApps.clear();
            Stream<ItemInfo> of = Stream.of(this.mAllAppsStore.getApps());
            if (!hasSearchResults() && (predicate = this.mItemFilter) != null) {
                of = of.filter(predicate);
            }
            Stream<ItemInfo> sorted = of.sorted(this.mAppNameComparator);
            if (this.mActivityContext.getResources().getConfiguration().locale.equals(Locale.SIMPLIFIED_CHINESE)) {
                sorted = ((TreeMap) sorted.collect(Collectors.groupingBy($$Lambda$AlphabeticalAppsList$mqjLuKyzokJV4qBHJdMr8DC0zAE.INSTANCE, $$Lambda$AlphabeticalAppsList$j12bJBxRfTBxbRnau7zVmzhZnsc.INSTANCE, Collectors.toCollection($$Lambda$AlphabeticalAppsList$E2McIAGPWBO62AkYPcfatUmDGDA.INSTANCE)))).values().stream().flatMap($$Lambda$q5oOTh_6hhooXPNjCvXOC_mcV3w.INSTANCE);
            }
            List<AppInfo> list = this.mApps;
            Objects.requireNonNull(list);
            sorted.forEachOrdered(new Consumer(list) {
                public final /* synthetic */ List f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    this.f$0.add((AppInfo) obj);
                }
            });
            if (this.mSearchResults.isEmpty()) {
                updateAdapterItems();
            }
        }
    }

    static /* synthetic */ TreeMap lambda$onAppsUpdated$1() {
        return new TreeMap(new LabelComparator());
    }

    public void updateAdapterItems() {
        int i;
        ArrayList arrayList = new ArrayList(this.mAdapterItems);
        this.mFastScrollerSections.clear();
        this.mAdapterItems.clear();
        this.mAccessibilityResultsCount = 0;
        if (hasSearchResults()) {
            this.mAdapterItems.addAll(this.mSearchResults);
            if (!FeatureFlags.ENABLE_DEVICE_SEARCH.get()) {
                if (hasNoFilteredResults()) {
                    this.mAdapterItems.add(new BaseAllAppsAdapter.AdapterItem(4));
                } else {
                    this.mAdapterItems.add(new BaseAllAppsAdapter.AdapterItem(16));
                }
                this.mAdapterItems.add(new BaseAllAppsAdapter.AdapterItem(8));
            }
        } else {
            WorkAdapterProvider workAdapterProvider = this.mWorkAdapterProvider;
            if (workAdapterProvider != null) {
                i = workAdapterProvider.addWorkItems(this.mAdapterItems) + 0;
                if (!this.mWorkAdapterProvider.shouldShowWorkApps()) {
                    return;
                }
            } else {
                i = 0;
            }
            String str = null;
            for (AppInfo next : this.mApps) {
                this.mAdapterItems.add(BaseAllAppsAdapter.AdapterItem.asApp(next));
                String str2 = next.sectionName;
                if (!str2.equals(str)) {
                    this.mFastScrollerSections.add(new FastScrollSectionInfo(str2, i));
                    str = str2;
                }
                i++;
            }
        }
        this.mAccessibilityResultsCount = (int) this.mAdapterItems.stream().filter($$Lambda$pXIiW0nt8H_wcXa7zeiDgGgHpM.INSTANCE).count();
        if (this.mNumAppsPerRowAllApps != 0) {
            int i2 = -1;
            Iterator<BaseAllAppsAdapter.AdapterItem> it = this.mAdapterItems.iterator();
            int i3 = 0;
            int i4 = 0;
            while (it.hasNext()) {
                BaseAllAppsAdapter.AdapterItem next2 = it.next();
                next2.rowIndex = 0;
                if (BaseAllAppsAdapter.isDividerViewType(next2.viewType)) {
                    i3 = 0;
                } else if (BaseAllAppsAdapter.isIconViewType(next2.viewType)) {
                    if (i3 % this.mNumAppsPerRowAllApps == 0) {
                        i2++;
                        i4 = 0;
                    }
                    next2.rowIndex = i2;
                    next2.rowAppIndex = i4;
                    i3++;
                    i4++;
                }
            }
            this.mNumAppRowsInAdapter = i2 + 1;
        }
        if (this.mAdapter != null) {
            DiffUtil.calculateDiff(new MyDiffCallback(arrayList, this.mAdapterItems), false).dispatchUpdatesTo((RecyclerView.Adapter) this.mAdapter);
        }
    }

    private static class MyDiffCallback extends DiffUtil.Callback {
        private final List<BaseAllAppsAdapter.AdapterItem> mNewList;
        private final List<BaseAllAppsAdapter.AdapterItem> mOldList;

        MyDiffCallback(List<BaseAllAppsAdapter.AdapterItem> list, List<BaseAllAppsAdapter.AdapterItem> list2) {
            this.mOldList = list;
            this.mNewList = list2;
        }

        public int getOldListSize() {
            return this.mOldList.size();
        }

        public int getNewListSize() {
            return this.mNewList.size();
        }

        public boolean areItemsTheSame(int i, int i2) {
            return this.mOldList.get(i).isSameAs(this.mNewList.get(i2));
        }

        public boolean areContentsTheSame(int i, int i2) {
            return this.mOldList.get(i).isContentSame(this.mNewList.get(i2));
        }
    }
}
