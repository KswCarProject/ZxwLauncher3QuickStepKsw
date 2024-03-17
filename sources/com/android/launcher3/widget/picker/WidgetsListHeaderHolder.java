package com.android.launcher3.widget.picker;

import androidx.recyclerview.widget.RecyclerView;

public final class WidgetsListHeaderHolder extends RecyclerView.ViewHolder {
    final WidgetsListHeader mWidgetsListHeader;

    public WidgetsListHeaderHolder(WidgetsListHeader widgetsListHeader) {
        super(widgetsListHeader);
        this.mWidgetsListHeader = widgetsListHeader;
    }
}
