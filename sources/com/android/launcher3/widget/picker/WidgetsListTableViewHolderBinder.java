package com.android.launcher3.widget.picker;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.android.launcher3.R;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.recyclerview.ViewHolderBinder;
import com.android.launcher3.widget.WidgetCell;
import com.android.launcher3.widget.model.WidgetsListContentEntry;
import com.android.launcher3.widget.util.WidgetsTableUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class WidgetsListTableViewHolderBinder implements ViewHolderBinder<WidgetsListContentEntry, WidgetsRowViewHolder> {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsListRowViewHolderBinder";
    private final View.OnClickListener mIconClickListener;
    private final View.OnLongClickListener mIconLongClickListener;
    private final LayoutInflater mLayoutInflater;
    private final WidgetsListDrawableFactory mListDrawableFactory;

    public WidgetsListTableViewHolderBinder(LayoutInflater layoutInflater, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, WidgetsListDrawableFactory widgetsListDrawableFactory) {
        this.mLayoutInflater = layoutInflater;
        this.mIconClickListener = onClickListener;
        this.mIconLongClickListener = onLongClickListener;
        this.mListDrawableFactory = widgetsListDrawableFactory;
    }

    public WidgetsRowViewHolder newViewHolder(ViewGroup viewGroup) {
        WidgetsRowViewHolder widgetsRowViewHolder = new WidgetsRowViewHolder(this.mLayoutInflater.inflate(R.layout.widgets_table_container, viewGroup, false));
        widgetsRowViewHolder.tableContainer.setBackgroundDrawable(this.mListDrawableFactory.createContentBackgroundDrawable());
        return widgetsRowViewHolder;
    }

    public void bindViewHolder(WidgetsRowViewHolder widgetsRowViewHolder, WidgetsListContentEntry widgetsListContentEntry, int i, List<Object> list) {
        Iterator<Object> it = list.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            widgetsRowViewHolder.previewCache.put((WidgetItem) pair.first, (Bitmap) pair.second);
        }
        WidgetsListTableView widgetsListTableView = widgetsRowViewHolder.tableContainer;
        widgetsListTableView.setListDrawableState((i & 2) != 0 ? WidgetsListDrawableState.LAST : WidgetsListDrawableState.MIDDLE);
        List<ArrayList<WidgetItem>> groupWidgetItemsIntoTableWithReordering = WidgetsTableUtils.groupWidgetItemsIntoTableWithReordering(widgetsListContentEntry.mWidgets, widgetsListContentEntry.getMaxSpanSizeInCells());
        recycleTableBeforeBinding(widgetsListTableView, groupWidgetItemsIntoTableWithReordering);
        for (int i2 = 0; i2 < groupWidgetItemsIntoTableWithReordering.size(); i2++) {
            List list2 = groupWidgetItemsIntoTableWithReordering.get(i2);
            for (int i3 = 0; i3 < list2.size(); i3++) {
                TableRow tableRow = (TableRow) widgetsListTableView.getChildAt(i2);
                tableRow.setVisibility(0);
                WidgetCell widgetCell = (WidgetCell) tableRow.getChildAt(i3);
                widgetCell.clear();
                WidgetItem widgetItem = (WidgetItem) list2.get(i3);
                widgetCell.setVisibility(0);
                widgetCell.applyFromCellItem(widgetItem, 1.0f, new Consumer(widgetItem) {
                    public final /* synthetic */ WidgetItem f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        WidgetsListTableViewHolderBinder.lambda$bindViewHolder$0(WidgetsRowViewHolder.this, this.f$1, (Bitmap) obj);
                    }
                }, widgetsRowViewHolder.previewCache.get(widgetItem));
            }
        }
    }

    static /* synthetic */ void lambda$bindViewHolder$0(WidgetsRowViewHolder widgetsRowViewHolder, WidgetItem widgetItem, Bitmap bitmap) {
        if (widgetsRowViewHolder.getBindingAdapter() != null) {
            widgetsRowViewHolder.getBindingAdapter().notifyItemChanged(widgetsRowViewHolder.getBindingAdapterPosition(), Pair.create(widgetItem, bitmap));
        }
    }

    private void recycleTableBeforeBinding(TableLayout tableLayout, List<ArrayList<WidgetItem>> list) {
        TableRow tableRow;
        for (int size = list.size(); size < tableLayout.getChildCount(); size++) {
            tableLayout.getChildAt(size).setVisibility(8);
        }
        for (int i = 0; i < list.size(); i++) {
            List list2 = list.get(i);
            if (i < tableLayout.getChildCount()) {
                tableRow = (TableRow) tableLayout.getChildAt(i);
            } else {
                tableRow = new TableRow(tableLayout.getContext());
                tableRow.setGravity(48);
                tableLayout.addView(tableRow);
            }
            if (tableRow.getChildCount() > list2.size()) {
                for (int size2 = list2.size(); size2 < tableRow.getChildCount(); size2++) {
                    tableRow.getChildAt(size2).setVisibility(8);
                }
            } else {
                for (int childCount = tableRow.getChildCount(); childCount < list2.size(); childCount++) {
                    WidgetCell widgetCell = (WidgetCell) this.mLayoutInflater.inflate(R.layout.widget_cell, tableRow, false);
                    View findViewById = widgetCell.findViewById(R.id.widget_preview_container);
                    findViewById.setOnClickListener(this.mIconClickListener);
                    findViewById.setOnLongClickListener(this.mIconLongClickListener);
                    widgetCell.setAnimatePreview(false);
                    tableRow.addView(widgetCell);
                }
            }
        }
    }

    public void unbindViewHolder(WidgetsRowViewHolder widgetsRowViewHolder) {
        int childCount = widgetsRowViewHolder.tableContainer.getChildCount();
        widgetsRowViewHolder.previewCache.clear();
        for (int i = 0; i < childCount; i++) {
            TableRow tableRow = (TableRow) widgetsRowViewHolder.tableContainer.getChildAt(i);
            int childCount2 = tableRow.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                ((WidgetCell) tableRow.getChildAt(i2)).clear();
            }
        }
    }
}
