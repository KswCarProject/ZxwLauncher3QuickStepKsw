package com.android.launcher3;

import android.view.View;
import com.android.launcher3.DropTarget;

public interface DragSource {
    void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z);
}
