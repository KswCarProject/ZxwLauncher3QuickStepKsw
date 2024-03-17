package com.android.launcher3.widget.picker;

import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import com.android.launcher3.widget.model.WidgetsListSearchHeaderEntry;
import com.android.launcher3.widget.picker.WidgetsListAdapter;

public class WidgetsDiffReporter {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsDiffReporter";
    private final IconCache mIconCache;
    private final RecyclerView.Adapter mListener;

    public WidgetsDiffReporter(IconCache iconCache, RecyclerView.Adapter adapter) {
        this.mIconCache = iconCache;
        this.mListener = adapter;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: com.android.launcher3.widget.model.WidgetsListBaseEntry} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void process(java.util.ArrayList<com.android.launcher3.widget.model.WidgetsListBaseEntry> r7, java.util.List<com.android.launcher3.widget.model.WidgetsListBaseEntry> r8, com.android.launcher3.widget.picker.WidgetsListAdapter.WidgetListBaseRowEntryComparator r9) {
        /*
            r6 = this;
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x00b1
            boolean r0 = r8.isEmpty()
            if (r0 == 0) goto L_0x000e
            goto L_0x00b1
        L_0x000e:
            java.lang.Object r0 = r7.clone()
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            java.util.Iterator r0 = r0.iterator()
            java.util.Iterator r1 = r8.iterator()
            java.lang.Object r8 = r0.next()
            com.android.launcher3.widget.model.WidgetsListBaseEntry r8 = (com.android.launcher3.widget.model.WidgetsListBaseEntry) r8
            java.lang.Object r2 = r1.next()
            com.android.launcher3.widget.model.WidgetsListBaseEntry r2 = (com.android.launcher3.widget.model.WidgetsListBaseEntry) r2
        L_0x0028:
            int r3 = r6.compareAppNameAndType(r8, r2, r9)
            r4 = 0
            if (r3 >= 0) goto L_0x004a
            int r8 = r7.indexOf(r8)
            androidx.recyclerview.widget.RecyclerView$Adapter r3 = r6.mListener
            r3.notifyItemRemoved(r8)
            r7.remove(r8)
            boolean r8 = r0.hasNext()
            if (r8 == 0) goto L_0x0048
            java.lang.Object r8 = r0.next()
            r4 = r8
            com.android.launcher3.widget.model.WidgetsListBaseEntry r4 = (com.android.launcher3.widget.model.WidgetsListBaseEntry) r4
        L_0x0048:
            r8 = r4
            goto L_0x00ac
        L_0x004a:
            if (r3 <= 0) goto L_0x006e
            if (r8 == 0) goto L_0x0053
            int r3 = r7.indexOf(r8)
            goto L_0x0057
        L_0x0053:
            int r3 = r7.size()
        L_0x0057:
            r7.add(r3, r2)
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0067
            java.lang.Object r2 = r1.next()
            r4 = r2
            com.android.launcher3.widget.model.WidgetsListBaseEntry r4 = (com.android.launcher3.widget.model.WidgetsListBaseEntry) r4
        L_0x0067:
            androidx.recyclerview.widget.RecyclerView$Adapter r2 = r6.mListener
            r2.notifyItemInserted(r3)
        L_0x006c:
            r2 = r4
            goto L_0x00ac
        L_0x006e:
            com.android.launcher3.model.data.PackageItemInfo r3 = r8.mPkgItem
            com.android.launcher3.model.data.PackageItemInfo r5 = r2.mPkgItem
            boolean r3 = r6.isSamePackageItemInfo(r3, r5)
            if (r3 == 0) goto L_0x0084
            boolean r3 = r6.hasHeaderUpdated(r8, r2)
            if (r3 != 0) goto L_0x0084
            boolean r3 = r6.hasWidgetsListContentChanged(r8, r2)
            if (r3 == 0) goto L_0x0090
        L_0x0084:
            int r8 = r7.indexOf(r8)
            r7.set(r8, r2)
            androidx.recyclerview.widget.RecyclerView$Adapter r2 = r6.mListener
            r2.notifyItemChanged(r8)
        L_0x0090:
            boolean r8 = r0.hasNext()
            if (r8 == 0) goto L_0x009d
            java.lang.Object r8 = r0.next()
            com.android.launcher3.widget.model.WidgetsListBaseEntry r8 = (com.android.launcher3.widget.model.WidgetsListBaseEntry) r8
            goto L_0x009e
        L_0x009d:
            r8 = r4
        L_0x009e:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x006c
            java.lang.Object r2 = r1.next()
            r4 = r2
            com.android.launcher3.widget.model.WidgetsListBaseEntry r4 = (com.android.launcher3.widget.model.WidgetsListBaseEntry) r4
            goto L_0x006c
        L_0x00ac:
            if (r8 != 0) goto L_0x0028
            if (r2 != 0) goto L_0x0028
            return
        L_0x00b1:
            int r9 = r7.size()
            int r0 = r8.size()
            if (r9 == r0) goto L_0x00c6
            r7.clear()
            r7.addAll(r8)
            androidx.recyclerview.widget.RecyclerView$Adapter r7 = r6.mListener
            r7.notifyDataSetChanged()
        L_0x00c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.widget.picker.WidgetsDiffReporter.process(java.util.ArrayList, java.util.List, com.android.launcher3.widget.picker.WidgetsListAdapter$WidgetListBaseRowEntryComparator):void");
    }

    private int compareAppNameAndType(WidgetsListBaseEntry widgetsListBaseEntry, WidgetsListBaseEntry widgetsListBaseEntry2, WidgetsListAdapter.WidgetListBaseRowEntryComparator widgetListBaseRowEntryComparator) {
        if (widgetsListBaseEntry == null && widgetsListBaseEntry2 == null) {
            throw new IllegalStateException("Cannot compare PackageItemInfo if both rows are null.");
        } else if (widgetsListBaseEntry == null && widgetsListBaseEntry2 != null) {
            return 1;
        } else {
            if (widgetsListBaseEntry != null && widgetsListBaseEntry2 == null) {
                return -1;
            }
            int compare = widgetListBaseRowEntryComparator.compare(widgetsListBaseEntry, widgetsListBaseEntry2);
            return compare == 0 ? widgetsListBaseEntry2.getRank() - widgetsListBaseEntry.getRank() : compare;
        }
    }

    private boolean hasWidgetsListContentChanged(WidgetsListBaseEntry widgetsListBaseEntry, WidgetsListBaseEntry widgetsListBaseEntry2) {
        if (!(widgetsListBaseEntry instanceof WidgetsListContentEntry) || !(widgetsListBaseEntry2 instanceof WidgetsListContentEntry)) {
            return false;
        }
        return !widgetsListBaseEntry.equals(widgetsListBaseEntry2);
    }

    private boolean hasHeaderUpdated(WidgetsListBaseEntry widgetsListBaseEntry, WidgetsListBaseEntry widgetsListBaseEntry2) {
        if ((widgetsListBaseEntry2 instanceof WidgetsListHeaderEntry) && (widgetsListBaseEntry instanceof WidgetsListHeaderEntry)) {
            return !widgetsListBaseEntry.equals(widgetsListBaseEntry2);
        }
        if (!(widgetsListBaseEntry2 instanceof WidgetsListSearchHeaderEntry) || !(widgetsListBaseEntry instanceof WidgetsListSearchHeaderEntry)) {
            return false;
        }
        return true;
    }

    private boolean isSamePackageItemInfo(PackageItemInfo packageItemInfo, PackageItemInfo packageItemInfo2) {
        return packageItemInfo.bitmap.icon.equals(packageItemInfo2.bitmap.icon) && !this.mIconCache.isDefaultIcon(packageItemInfo.bitmap, packageItemInfo.user);
    }
}
