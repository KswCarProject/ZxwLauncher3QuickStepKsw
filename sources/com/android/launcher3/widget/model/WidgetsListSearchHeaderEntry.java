package com.android.launcher3.widget.model;

import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.widget.model.WidgetsListBaseEntry;
import java.util.List;

public final class WidgetsListSearchHeaderEntry extends WidgetsListBaseEntry implements WidgetsListBaseEntry.Header<WidgetsListSearchHeaderEntry> {
    private final boolean mIsWidgetListShown;

    public int getRank() {
        return 3;
    }

    public WidgetsListSearchHeaderEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list) {
        this(packageItemInfo, str, list, false);
    }

    private WidgetsListSearchHeaderEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list, boolean z) {
        super(packageItemInfo, str, list);
        this.mIsWidgetListShown = z;
    }

    public boolean isWidgetListShown() {
        return this.mIsWidgetListShown;
    }

    public String toString() {
        return "SearchHeader:" + this.mPkgItem.packageName + ":" + this.mWidgets.size();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WidgetsListSearchHeaderEntry)) {
            return false;
        }
        WidgetsListSearchHeaderEntry widgetsListSearchHeaderEntry = (WidgetsListSearchHeaderEntry) obj;
        if (!this.mWidgets.equals(widgetsListSearchHeaderEntry.mWidgets) || !this.mPkgItem.equals(widgetsListSearchHeaderEntry.mPkgItem) || !this.mTitleSectionName.equals(widgetsListSearchHeaderEntry.mTitleSectionName) || this.mIsWidgetListShown != widgetsListSearchHeaderEntry.mIsWidgetListShown) {
            return false;
        }
        return true;
    }

    public WidgetsListSearchHeaderEntry withWidgetListShown() {
        if (this.mIsWidgetListShown) {
            return this;
        }
        return new WidgetsListSearchHeaderEntry(this.mPkgItem, this.mTitleSectionName, this.mWidgets, true);
    }
}
