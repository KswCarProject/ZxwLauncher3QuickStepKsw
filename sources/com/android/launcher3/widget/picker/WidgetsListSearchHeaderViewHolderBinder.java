package com.android.launcher3.widget.picker;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.launcher3.R;
import com.android.launcher3.recyclerview.ViewHolderBinder;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.model.WidgetsListSearchHeaderEntry;
import com.android.launcher3.widget.picker.WidgetsListHeader;
import java.util.List;

public final class WidgetsListSearchHeaderViewHolderBinder implements ViewHolderBinder<WidgetsListSearchHeaderEntry, WidgetsListSearchHeaderHolder> {
    private final LayoutInflater mLayoutInflater;
    private final WidgetsListDrawableFactory mListDrawableFactory;
    private final OnHeaderClickListener mOnHeaderClickListener;

    public WidgetsListSearchHeaderViewHolderBinder(LayoutInflater layoutInflater, OnHeaderClickListener onHeaderClickListener, WidgetsListDrawableFactory widgetsListDrawableFactory) {
        this.mLayoutInflater = layoutInflater;
        this.mOnHeaderClickListener = onHeaderClickListener;
        this.mListDrawableFactory = widgetsListDrawableFactory;
    }

    public WidgetsListSearchHeaderHolder newViewHolder(ViewGroup viewGroup) {
        WidgetsListHeader widgetsListHeader = (WidgetsListHeader) this.mLayoutInflater.inflate(R.layout.widgets_list_row_header, viewGroup, false);
        widgetsListHeader.setBackground(this.mListDrawableFactory.createHeaderBackgroundDrawable());
        return new WidgetsListSearchHeaderHolder(widgetsListHeader);
    }

    public void bindViewHolder(WidgetsListSearchHeaderHolder widgetsListSearchHeaderHolder, WidgetsListSearchHeaderEntry widgetsListSearchHeaderEntry, int i, List<Object> list) {
        WidgetsListHeader widgetsListHeader = widgetsListSearchHeaderHolder.mWidgetsListHeader;
        widgetsListHeader.applyFromItemInfoWithIcon(widgetsListSearchHeaderEntry);
        widgetsListHeader.setExpanded(widgetsListSearchHeaderEntry.isWidgetListShown());
        boolean z = false;
        boolean z2 = (i & 1) != 0;
        if ((i & 2) != 0) {
            z = true;
        }
        widgetsListHeader.setListDrawableState(WidgetsListDrawableState.obtain(z2, z, widgetsListSearchHeaderEntry.isWidgetListShown()));
        widgetsListHeader.setOnExpandChangeListener(new WidgetsListHeader.OnExpansionChangeListener(widgetsListSearchHeaderEntry) {
            public final /* synthetic */ WidgetsListSearchHeaderEntry f$1;

            {
                this.f$1 = r2;
            }

            public final void onExpansionChange(boolean z) {
                WidgetsListSearchHeaderViewHolderBinder.this.lambda$bindViewHolder$0$WidgetsListSearchHeaderViewHolderBinder(this.f$1, z);
            }
        });
    }

    public /* synthetic */ void lambda$bindViewHolder$0$WidgetsListSearchHeaderViewHolderBinder(WidgetsListSearchHeaderEntry widgetsListSearchHeaderEntry, boolean z) {
        this.mOnHeaderClickListener.onHeaderClicked(z, PackageUserKey.fromPackageItemInfo(widgetsListSearchHeaderEntry.mPkgItem));
    }
}
