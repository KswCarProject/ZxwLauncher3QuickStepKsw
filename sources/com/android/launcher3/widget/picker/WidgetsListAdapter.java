package com.android.launcher3.widget.picker;

import android.content.Context;
import android.graphics.Rect;
import android.os.Process;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.recyclerview.ViewHolderBinder;
import com.android.launcher3.util.LabelComparator;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.model.WidgetListSpaceEntry;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import com.android.launcher3.widget.model.WidgetsListSearchHeaderEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WidgetsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnHeaderClickListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsListAdapter";
    private static final int VIEW_TYPE_WIDGETS_HEADER = 2131296957;
    private static final int VIEW_TYPE_WIDGETS_LIST = 2131296958;
    private static final int VIEW_TYPE_WIDGETS_SEARCH_HEADER = 2131296959;
    private static final int VIEW_TYPE_WIDGETS_SPACE = 2131296960;
    private final List<WidgetsListBaseEntry> mAllEntries = new ArrayList();
    private final Context mContext;
    private final WidgetsDiffReporter mDiffReporter;
    private Predicate<WidgetsListBaseEntry> mFilter = null;
    private Predicate<WidgetsListBaseEntry> mHeaderAndSelectedContentFilter = new Predicate() {
        public final boolean test(Object obj) {
            return WidgetsListAdapter.this.lambda$new$0$WidgetsListAdapter((WidgetsListBaseEntry) obj);
        }
    };
    private int mMaxSpanSize = 4;
    private PackageUserKey mPendingClickHeader;
    private RecyclerView mRecyclerView;
    private final WidgetListBaseRowEntryComparator mRowComparator = new WidgetListBaseRowEntryComparator();
    /* access modifiers changed from: private */
    public final int mSpacingBetweenEntries;
    private final SparseArray<ViewHolderBinder> mViewHolderBinders;
    private ArrayList<WidgetsListBaseEntry> mVisibleEntries = new ArrayList<>();
    private PackageUserKey mWidgetsContentVisiblePackageUserKey = null;

    static /* synthetic */ PackageItemInfo lambda$shouldClearVisibleEntries$7(PackageItemInfo packageItemInfo) {
        return packageItemInfo;
    }

    public boolean onFailedToRecycleView(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    public /* synthetic */ boolean lambda$new$0$WidgetsListAdapter(WidgetsListBaseEntry widgetsListBaseEntry) {
        return (widgetsListBaseEntry instanceof WidgetsListHeaderEntry) || (widgetsListBaseEntry instanceof WidgetsListSearchHeaderEntry) || PackageUserKey.fromPackageItemInfo(widgetsListBaseEntry.mPkgItem).equals(this.mWidgetsContentVisiblePackageUserKey);
    }

    public WidgetsListAdapter(Context context, LayoutInflater layoutInflater, IconCache iconCache, IntSupplier intSupplier, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        SparseArray<ViewHolderBinder> sparseArray = new SparseArray<>();
        this.mViewHolderBinders = sparseArray;
        this.mContext = context;
        this.mDiffReporter = new WidgetsDiffReporter(iconCache, this);
        WidgetsListDrawableFactory widgetsListDrawableFactory = new WidgetsListDrawableFactory(context);
        sparseArray.put(R.id.view_type_widgets_list, new WidgetsListTableViewHolderBinder(layoutInflater, onClickListener, onLongClickListener, widgetsListDrawableFactory));
        sparseArray.put(R.id.view_type_widgets_header, new WidgetsListHeaderViewHolderBinder(layoutInflater, this, widgetsListDrawableFactory));
        sparseArray.put(R.id.view_type_widgets_search_header, new WidgetsListSearchHeaderViewHolderBinder(layoutInflater, this, widgetsListDrawableFactory));
        sparseArray.put(R.id.view_type_widgets_space, new WidgetsSpaceViewHolderBinder(intSupplier));
        this.mSpacingBetweenEntries = context.getResources().getDimensionPixelSize(R.dimen.widget_list_entry_spacing);
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view, recyclerView, state);
                rect.top += (((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition() <= 0 || !(view.getTag(R.id.tag_widget_entry) instanceof WidgetsListBaseEntry.Header)) ? 0 : WidgetsListAdapter.this.mSpacingBetweenEntries;
            }
        });
    }

    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = null;
    }

    public void setFilter(Predicate<WidgetsListBaseEntry> predicate) {
        this.mFilter = predicate;
    }

    public int getItemCount() {
        return this.mVisibleEntries.size();
    }

    public boolean hasVisibleEntries() {
        return getItemCount() > 1;
    }

    public List<WidgetsListBaseEntry> getItems() {
        return this.mVisibleEntries;
    }

    public String getSectionName(int i) {
        return this.mVisibleEntries.get(i).mTitleSectionName;
    }

    public void setWidgets(List<WidgetsListBaseEntry> list) {
        this.mAllEntries.clear();
        this.mAllEntries.add(new WidgetListSpaceEntry());
        Stream sorted = list.stream().sorted(this.mRowComparator);
        List<WidgetsListBaseEntry> list2 = this.mAllEntries;
        Objects.requireNonNull(list2);
        sorted.forEach(new Consumer(list2) {
            public final /* synthetic */ List f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.add((WidgetsListBaseEntry) obj);
            }
        });
        if (shouldClearVisibleEntries()) {
            this.mVisibleEntries.clear();
        }
        updateVisibleEntries();
    }

    public void setWidgetsOnSearch(List<WidgetsListBaseEntry> list) {
        this.mWidgetsContentVisiblePackageUserKey = null;
        setWidgets(list);
    }

    private void updateVisibleEntries() {
        OptionalInt offsetForPosition = getOffsetForPosition(getPositionForPackageUserKey(this.mPendingClickHeader));
        this.mDiffReporter.process(this.mVisibleEntries, (List) this.mAllEntries.stream().filter(new Predicate() {
            public final boolean test(Object obj) {
                return WidgetsListAdapter.this.lambda$updateVisibleEntries$1$WidgetsListAdapter((WidgetsListBaseEntry) obj);
            }
        }).map(new Function() {
            public final Object apply(Object obj) {
                return WidgetsListAdapter.this.lambda$updateVisibleEntries$2$WidgetsListAdapter((WidgetsListBaseEntry) obj);
            }
        }).collect(Collectors.toList()), this.mRowComparator);
        PackageUserKey packageUserKey = this.mPendingClickHeader;
        if (packageUserKey != null) {
            scrollToPositionAndMaintainOffset(getPositionForPackageUserKey(packageUserKey), offsetForPosition);
            this.mPendingClickHeader = null;
        }
    }

    public /* synthetic */ boolean lambda$updateVisibleEntries$1$WidgetsListAdapter(WidgetsListBaseEntry widgetsListBaseEntry) {
        Predicate<WidgetsListBaseEntry> predicate = this.mFilter;
        return ((predicate == null || predicate.test(widgetsListBaseEntry)) && this.mHeaderAndSelectedContentFilter.test(widgetsListBaseEntry)) || (widgetsListBaseEntry instanceof WidgetListSpaceEntry);
    }

    public /* synthetic */ WidgetsListBaseEntry lambda$updateVisibleEntries$2$WidgetsListAdapter(WidgetsListBaseEntry widgetsListBaseEntry) {
        if (!(widgetsListBaseEntry instanceof WidgetsListBaseEntry.Header) || !matchesKey(widgetsListBaseEntry, this.mWidgetsContentVisiblePackageUserKey)) {
            return widgetsListBaseEntry instanceof WidgetsListContentEntry ? ((WidgetsListContentEntry) widgetsListBaseEntry).withMaxSpanSize(this.mMaxSpanSize) : widgetsListBaseEntry;
        }
        return ((WidgetsListBaseEntry.Header) widgetsListBaseEntry).withWidgetListShown();
    }

    private static boolean isHeaderForPackageUserKey(WidgetsListBaseEntry widgetsListBaseEntry, PackageUserKey packageUserKey) {
        return (widgetsListBaseEntry instanceof WidgetsListBaseEntry.Header) && matchesKey(widgetsListBaseEntry, packageUserKey);
    }

    private static boolean matchesKey(WidgetsListBaseEntry widgetsListBaseEntry, PackageUserKey packageUserKey) {
        if (packageUserKey != null && widgetsListBaseEntry.mPkgItem.packageName.equals(packageUserKey.mPackageName) && widgetsListBaseEntry.mPkgItem.widgetCategory == packageUserKey.mWidgetCategory && widgetsListBaseEntry.mPkgItem.user.equals(packageUserKey.mUser)) {
            return true;
        }
        return false;
    }

    public void resetExpandedHeader() {
        if (this.mWidgetsContentVisiblePackageUserKey != null) {
            this.mWidgetsContentVisiblePackageUserKey = null;
            updateVisibleEntries();
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        onBindViewHolder(viewHolder, i, Collections.EMPTY_LIST);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List<Object> list) {
        ViewHolderBinder viewHolderBinder = this.mViewHolderBinders.get(getItemViewType(i));
        WidgetsListBaseEntry widgetsListBaseEntry = this.mVisibleEntries.get(i);
        int i2 = i > 1 ? 0 : 1;
        if (i == getItemCount() - 1) {
            i2 |= 2;
        }
        viewHolderBinder.bindViewHolder(viewHolder, this.mVisibleEntries.get(i), i2, list);
        viewHolder.itemView.setTag(R.id.tag_widget_entry, widgetsListBaseEntry);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return this.mViewHolderBinders.get(i).newViewHolder(viewGroup);
    }

    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        this.mViewHolderBinders.get(viewHolder.getItemViewType()).unbindViewHolder(viewHolder);
    }

    public long getItemId(int i) {
        return (long) Arrays.hashCode(new Object[]{Integer.valueOf(this.mVisibleEntries.get(i).mPkgItem.hashCode()), Integer.valueOf(getItemViewType(i))});
    }

    public int getItemViewType(int i) {
        WidgetsListBaseEntry widgetsListBaseEntry = this.mVisibleEntries.get(i);
        if (widgetsListBaseEntry instanceof WidgetsListContentEntry) {
            return R.id.view_type_widgets_list;
        }
        if (widgetsListBaseEntry instanceof WidgetsListHeaderEntry) {
            return R.id.view_type_widgets_header;
        }
        if (widgetsListBaseEntry instanceof WidgetsListSearchHeaderEntry) {
            return R.id.view_type_widgets_search_header;
        }
        if (widgetsListBaseEntry instanceof WidgetListSpaceEntry) {
            return R.id.view_type_widgets_space;
        }
        throw new UnsupportedOperationException("ViewHolderBinder not found for " + widgetsListBaseEntry);
    }

    public void onHeaderClicked(boolean z, PackageUserKey packageUserKey) {
        if (z || packageUserKey.equals(this.mWidgetsContentVisiblePackageUserKey)) {
            if (z) {
                this.mWidgetsContentVisiblePackageUserKey = packageUserKey;
                ((ActivityContext) ActivityContext.lookupContext(this.mContext)).getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_WIDGETSTRAY_APP_EXPANDED);
            } else {
                this.mWidgetsContentVisiblePackageUserKey = null;
            }
            this.mPendingClickHeader = packageUserKey;
            updateVisibleEntries();
        }
    }

    private OptionalInt getPositionForPackageUserKey(PackageUserKey packageUserKey) {
        return IntStream.range(0, this.mVisibleEntries.size()).filter(new IntPredicate(packageUserKey) {
            public final /* synthetic */ PackageUserKey f$1;

            {
                this.f$1 = r2;
            }

            public final boolean test(int i) {
                return WidgetsListAdapter.this.lambda$getPositionForPackageUserKey$3$WidgetsListAdapter(this.f$1, i);
            }
        }).findFirst();
    }

    public /* synthetic */ boolean lambda$getPositionForPackageUserKey$3$WidgetsListAdapter(PackageUserKey packageUserKey, int i) {
        return isHeaderForPackageUserKey(this.mVisibleEntries.get(i), packageUserKey);
    }

    private OptionalInt getOffsetForPosition(OptionalInt optionalInt) {
        RecyclerView recyclerView;
        if (!optionalInt.isPresent() || (recyclerView = this.mRecyclerView) == null) {
            return OptionalInt.empty();
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return OptionalInt.empty();
        }
        View findViewByPosition = layoutManager.findViewByPosition(optionalInt.getAsInt());
        if (findViewByPosition == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(layoutManager.getDecoratedTop(findViewByPosition));
    }

    private void scrollToPositionAndMaintainOffset(OptionalInt optionalInt, OptionalInt optionalInt2) {
        if (optionalInt.isPresent() && this.mRecyclerView != null) {
            int asInt = optionalInt.getAsInt();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
            if (linearLayoutManager != null) {
                if (asInt == this.mVisibleEntries.size() - 2) {
                    ArrayList<WidgetsListBaseEntry> arrayList = this.mVisibleEntries;
                    if (arrayList.get(arrayList.size() - 1) instanceof WidgetsListContentEntry) {
                        linearLayoutManager.scrollToPosition(this.mVisibleEntries.size() - 1);
                        return;
                    }
                }
                linearLayoutManager.scrollToPositionWithOffset(asInt, optionalInt2.orElse(0) - this.mRecyclerView.getPaddingTop());
            }
        }
    }

    public void setMaxHorizontalSpansPerRow(int i) {
        this.mMaxSpanSize = i;
        updateVisibleEntries();
    }

    private boolean shouldClearVisibleEntries() {
        Map map = (Map) this.mAllEntries.stream().filter($$Lambda$WidgetsListAdapter$RG47HiZaN0opApGYHyltYjj7iR8.INSTANCE).map($$Lambda$WidgetsListAdapter$FdTWrmzVJBBkN6bhf3SxUT7F8rc.INSTANCE).collect(Collectors.toMap($$Lambda$WidgetsListAdapter$c7ABGsgjgfwl5yWNIyfH_07N54.INSTANCE, $$Lambda$WidgetsListAdapter$bbGjJFhWfvBR6jxYWH5PqrmNHDE.INSTANCE));
        Iterator<WidgetsListBaseEntry> it = this.mVisibleEntries.iterator();
        while (it.hasNext()) {
            WidgetsListBaseEntry next = it.next();
            PackageItemInfo packageItemInfo = (PackageItemInfo) map.get(PackageUserKey.fromPackageItemInfo(next.mPkgItem));
            if (packageItemInfo != null && !next.mPkgItem.title.equals(packageItemInfo.title)) {
                return true;
            }
        }
        return false;
    }

    static /* synthetic */ boolean lambda$shouldClearVisibleEntries$4(WidgetsListBaseEntry widgetsListBaseEntry) {
        return widgetsListBaseEntry instanceof WidgetsListHeaderEntry;
    }

    public static class WidgetListBaseRowEntryComparator implements Comparator<WidgetsListBaseEntry> {
        private final LabelComparator mComparator = new LabelComparator();

        public int compare(WidgetsListBaseEntry widgetsListBaseEntry, WidgetsListBaseEntry widgetsListBaseEntry2) {
            int compare = this.mComparator.compare(widgetsListBaseEntry.mPkgItem.title.toString(), widgetsListBaseEntry2.mPkgItem.title.toString());
            if (compare != 0) {
                return compare;
            }
            if (widgetsListBaseEntry.mPkgItem.user.equals(widgetsListBaseEntry2.mPkgItem.user)) {
                return 0;
            }
            return widgetsListBaseEntry.mPkgItem.user.equals(Process.myUserHandle()) ? -1 : 1;
        }
    }
}
