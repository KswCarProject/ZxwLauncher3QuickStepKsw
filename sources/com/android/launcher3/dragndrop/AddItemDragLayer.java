package com.android.launcher3.dragndrop;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.BaseDragLayer;

public class AddItemDragLayer extends BaseDragLayer<AddItemActivity> {
    public AddItemDragLayer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 1);
    }

    public AddItemDragLayer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void recreateControllers() {
        this.mControllers = new TouchController[0];
    }
}
