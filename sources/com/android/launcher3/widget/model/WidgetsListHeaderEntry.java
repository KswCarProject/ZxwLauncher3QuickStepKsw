package com.android.launcher3.widget.model;

import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import java.util.List;

public final class WidgetsListHeaderEntry extends WidgetsListBaseEntry implements WidgetsListBaseEntry.Header<WidgetsListHeaderEntry> {
    private final boolean mIsWidgetListShown;
    public final int shortcutsCount;
    public final int widgetsCount;

    public int getRank() {
        return 2;
    }

    public WidgetsListHeaderEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list) {
        this(packageItemInfo, str, list, false);
    }

    private WidgetsListHeaderEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list, boolean z) {
        super(packageItemInfo, str, list);
        int count = (int) list.stream().filter($$Lambda$WidgetsListHeaderEntry$omB_Jqm1m_S8IdHef5CGLgnZ7k.INSTANCE).count();
        this.widgetsCount = count;
        this.shortcutsCount = Math.max(0, list.size() - count);
        this.mIsWidgetListShown = z;
    }

    static /* synthetic */ boolean lambda$new$0(WidgetItem widgetItem) {
        return widgetItem.widgetInfo != null;
    }

    public boolean isWidgetListShown() {
        return this.mIsWidgetListShown;
    }

    public String toString() {
        return "Header:" + this.mPkgItem.packageName + ":" + this.mWidgets.size();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WidgetsListHeaderEntry)) {
            return false;
        }
        WidgetsListHeaderEntry widgetsListHeaderEntry = (WidgetsListHeaderEntry) obj;
        if (!this.mWidgets.equals(widgetsListHeaderEntry.mWidgets) || !this.mPkgItem.equals(widgetsListHeaderEntry.mPkgItem) || !this.mTitleSectionName.equals(widgetsListHeaderEntry.mTitleSectionName) || this.mIsWidgetListShown != widgetsListHeaderEntry.mIsWidgetListShown) {
            return false;
        }
        return true;
    }

    public WidgetsListHeaderEntry withWidgetListShown() {
        if (this.mIsWidgetListShown) {
            return this;
        }
        return new WidgetsListHeaderEntry(this.mPkgItem, this.mTitleSectionName, this.mWidgets, true);
    }
}
