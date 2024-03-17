package com.android.launcher3.widget.picker;

import android.graphics.Bitmap;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.R;
import com.android.launcher3.model.WidgetItem;
import java.util.HashMap;
import java.util.Map;

public final class WidgetsRowViewHolder extends RecyclerView.ViewHolder {
    public final Map<WidgetItem, Bitmap> previewCache = new HashMap();
    public final WidgetsListTableView tableContainer;

    public WidgetsRowViewHolder(View view) {
        super(view);
        this.tableContainer = (WidgetsListTableView) view.findViewById(R.id.widgets_table);
    }
}
