package com.android.launcher3.widget.model;

import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.PackageItemInfo;
import java.util.List;

public final class WidgetsListContentEntry extends WidgetsListBaseEntry {
    private final int mMaxSpanSizeInCells;

    public int getRank() {
        return 4;
    }

    public WidgetsListContentEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list) {
        this(packageItemInfo, str, list, 0);
    }

    public WidgetsListContentEntry(PackageItemInfo packageItemInfo, String str, List<WidgetItem> list, int i) {
        super(packageItemInfo, str, list);
        this.mMaxSpanSizeInCells = i;
    }

    public String toString() {
        return "Content:" + this.mPkgItem.packageName + ":" + this.mWidgets.size() + " maxSpanSizeInCells: " + this.mMaxSpanSizeInCells;
    }

    public WidgetsListContentEntry withMaxSpanSize(int i) {
        if (this.mMaxSpanSizeInCells == i) {
            return this;
        }
        return new WidgetsListContentEntry(this.mPkgItem, this.mTitleSectionName, this.mWidgets, i);
    }

    public int getMaxSpanSizeInCells() {
        return this.mMaxSpanSizeInCells;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WidgetsListContentEntry)) {
            return false;
        }
        WidgetsListContentEntry widgetsListContentEntry = (WidgetsListContentEntry) obj;
        if (!this.mWidgets.equals(widgetsListContentEntry.mWidgets) || !this.mPkgItem.equals(widgetsListContentEntry.mPkgItem) || !this.mTitleSectionName.equals(widgetsListContentEntry.mTitleSectionName) || this.mMaxSpanSizeInCells != widgetsListContentEntry.mMaxSpanSizeInCells) {
            return false;
        }
        return true;
    }
}
