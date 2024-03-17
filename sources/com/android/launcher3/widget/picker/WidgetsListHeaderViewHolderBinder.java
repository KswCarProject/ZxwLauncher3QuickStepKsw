package com.android.launcher3.widget.picker;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.launcher3.R;
import com.android.launcher3.recyclerview.ViewHolderBinder;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.model.WidgetsListHeaderEntry;
import com.android.launcher3.widget.picker.WidgetsListHeader;
import java.util.List;

public final class WidgetsListHeaderViewHolderBinder implements ViewHolderBinder<WidgetsListHeaderEntry, WidgetsListHeaderHolder> {
    private final LayoutInflater mLayoutInflater;
    private final WidgetsListDrawableFactory mListDrawableFactory;
    private final OnHeaderClickListener mOnHeaderClickListener;

    public WidgetsListHeaderViewHolderBinder(LayoutInflater layoutInflater, OnHeaderClickListener onHeaderClickListener, WidgetsListDrawableFactory widgetsListDrawableFactory) {
        this.mLayoutInflater = layoutInflater;
        this.mOnHeaderClickListener = onHeaderClickListener;
        this.mListDrawableFactory = widgetsListDrawableFactory;
    }

    public WidgetsListHeaderHolder newViewHolder(ViewGroup viewGroup) {
        WidgetsListHeader widgetsListHeader = (WidgetsListHeader) this.mLayoutInflater.inflate(R.layout.widgets_list_row_header, viewGroup, false);
        widgetsListHeader.setBackground(this.mListDrawableFactory.createHeaderBackgroundDrawable());
        return new WidgetsListHeaderHolder(widgetsListHeader);
    }

    public void bindViewHolder(WidgetsListHeaderHolder widgetsListHeaderHolder, WidgetsListHeaderEntry widgetsListHeaderEntry, int i, List<Object> list) {
        WidgetsListHeader widgetsListHeader = widgetsListHeaderHolder.mWidgetsListHeader;
        widgetsListHeader.applyFromItemInfoWithIcon(widgetsListHeaderEntry);
        widgetsListHeader.setExpanded(widgetsListHeaderEntry.isWidgetListShown());
        boolean z = false;
        boolean z2 = (i & 1) != 0;
        if ((i & 2) != 0) {
            z = true;
        }
        widgetsListHeader.setListDrawableState(WidgetsListDrawableState.obtain(z2, z, widgetsListHeaderEntry.isWidgetListShown()));
        widgetsListHeader.setOnExpandChangeListener(new WidgetsListHeader.OnExpansionChangeListener(widgetsListHeaderEntry) {
            public final /* synthetic */ WidgetsListHeaderEntry f$1;

            {
                this.f$1 = r2;
            }

            public final void onExpansionChange(boolean z) {
                WidgetsListHeaderViewHolderBinder.this.lambda$bindViewHolder$0$WidgetsListHeaderViewHolderBinder(this.f$1, z);
            }
        });
    }

    public /* synthetic */ void lambda$bindViewHolder$0$WidgetsListHeaderViewHolderBinder(WidgetsListHeaderEntry widgetsListHeaderEntry, boolean z) {
        this.mOnHeaderClickListener.onHeaderClicked(z, PackageUserKey.fromPackageItemInfo(widgetsListHeaderEntry.mPkgItem));
    }
}
