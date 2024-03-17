package com.android.launcher3.taskbar;

import android.view.View;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;

/* renamed from: com.android.launcher3.taskbar.-$$Lambda$TaskbarDragController$GAZ5HKHQSsfvE2nE9EcXp5bRtSo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TaskbarDragController$GAZ5HKHQSsfvE2nE9EcXp5bRtSo implements DragSource {
    public static final /* synthetic */ $$Lambda$TaskbarDragController$GAZ5HKHQSsfvE2nE9EcXp5bRtSo INSTANCE = new $$Lambda$TaskbarDragController$GAZ5HKHQSsfvE2nE9EcXp5bRtSo();

    private /* synthetic */ $$Lambda$TaskbarDragController$GAZ5HKHQSsfvE2nE9EcXp5bRtSo() {
    }

    public final void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
        TaskbarDragController.lambda$startInternalDrag$1(view, dragObject, z);
    }
}
