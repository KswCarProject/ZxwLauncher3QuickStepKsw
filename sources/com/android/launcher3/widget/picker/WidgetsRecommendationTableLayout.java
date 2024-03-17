package com.android.launcher3.widget.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.widget.WidgetCell;
import com.android.launcher3.widget.util.WidgetSizes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WidgetsRecommendationTableLayout extends TableLayout {
    private static final float DOWN_SCALE_RATIO = 0.9f;
    private static final float MAX_DOWN_SCALE_RATIO = 0.5f;
    private static final String TAG = "WidgetsRecommendationTableLayout";
    private float mRecommendationTableMaxHeight;
    private View.OnClickListener mWidgetCellOnClickListener;
    private View.OnLongClickListener mWidgetCellOnLongClickListener;
    private final float mWidgetCellTextViewsHeight;
    private final float mWidgetCellVerticalPadding;
    private final float mWidgetsRecommendationTableVerticalPadding;

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public WidgetsRecommendationTableLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetsRecommendationTableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRecommendationTableMaxHeight = Float.MAX_VALUE;
        this.mWidgetsRecommendationTableVerticalPadding = (float) (getResources().getDimensionPixelSize(R.dimen.recommended_widgets_table_vertical_padding) * 2);
        this.mWidgetCellVerticalPadding = (float) (getResources().getDimensionPixelSize(R.dimen.widget_cell_vertical_padding) * 2);
        this.mWidgetCellTextViewsHeight = getResources().getDimension(R.dimen.widget_cell_font_size) * 4.0f;
    }

    public void setWidgetCellLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.mWidgetCellOnLongClickListener = onLongClickListener;
    }

    public void setWidgetCellOnClickListener(View.OnClickListener onClickListener) {
        this.mWidgetCellOnClickListener = onClickListener;
    }

    public void setRecommendedWidgets(List<ArrayList<WidgetItem>> list, float f) {
        this.mRecommendationTableMaxHeight = f;
        bindData(fitRecommendedWidgetsToTableSpace(1.0f, list));
    }

    private void bindData(RecommendationTableData recommendationTableData) {
        if (recommendationTableData.mRecommendationTable.size() == 0) {
            setVisibility(8);
            return;
        }
        removeAllViews();
        for (int i = 0; i < recommendationTableData.mRecommendationTable.size(); i++) {
            TableRow tableRow = new TableRow(getContext());
            tableRow.setGravity(48);
            for (WidgetItem applyFromCellItem : (List) recommendationTableData.mRecommendationTable.get(i)) {
                WidgetCell addItemCell = addItemCell(tableRow);
                addItemCell.applyFromCellItem(applyFromCellItem, recommendationTableData.mPreviewScale);
                addItemCell.showBadge();
            }
            addView(tableRow);
        }
        setVisibility(0);
    }

    private WidgetCell addItemCell(ViewGroup viewGroup) {
        WidgetCell widgetCell = (WidgetCell) LayoutInflater.from(getContext()).inflate(R.layout.widget_cell, viewGroup, false);
        View findViewById = widgetCell.findViewById(R.id.widget_preview_container);
        findViewById.setOnClickListener(this.mWidgetCellOnClickListener);
        findViewById.setOnLongClickListener(this.mWidgetCellOnLongClickListener);
        widgetCell.setAnimatePreview(false);
        widgetCell.setSourceContainer(LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION);
        viewGroup.addView(widgetCell);
        return widgetCell;
    }

    private RecommendationTableData fitRecommendedWidgetsToTableSpace(float f, List<ArrayList<WidgetItem>> list) {
        if (f < 0.5f) {
            Log.w(TAG, "Hide recommended widgets. Can't down scale previews to " + f);
            return new RecommendationTableData(Collections.emptyList(), f);
        }
        float f2 = this.mWidgetsRecommendationTableVerticalPadding;
        DeviceProfile deviceProfile = Launcher.getLauncher(getContext()).getDeviceProfile();
        for (int i = 0; i < list.size(); i++) {
            List list2 = list.get(i);
            float f3 = 0.0f;
            for (int i2 = 0; i2 < list2.size(); i2++) {
                f3 = Math.max(f3, (((float) WidgetSizes.getWidgetItemSizePx(getContext(), deviceProfile, (WidgetItem) list2.get(i2)).getHeight()) * f) + this.mWidgetCellTextViewsHeight + this.mWidgetCellVerticalPadding);
            }
            f2 += f3;
        }
        if (f2 < this.mRecommendationTableMaxHeight) {
            return new RecommendationTableData(list, f);
        }
        if (list.size() > 1) {
            return fitRecommendedWidgetsToTableSpace(f, list.subList(0, list.size() - 1));
        }
        return fitRecommendedWidgetsToTableSpace(f * DOWN_SCALE_RATIO, list);
    }

    private class RecommendationTableData {
        /* access modifiers changed from: private */
        public final float mPreviewScale;
        /* access modifiers changed from: private */
        public final List<ArrayList<WidgetItem>> mRecommendationTable;

        RecommendationTableData(List<ArrayList<WidgetItem>> list, float f) {
            this.mRecommendationTable = list;
            this.mPreviewScale = f;
        }
    }
}
