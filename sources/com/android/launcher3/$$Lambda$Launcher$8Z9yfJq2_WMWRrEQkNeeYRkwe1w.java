package com.android.launcher3;

import android.view.View;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.LauncherBindableItemsContainer;

/* renamed from: com.android.launcher3.-$$Lambda$Launcher$8Z9yfJq2_WMWRrEQkNeeYRkwe1w  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Launcher$8Z9yfJq2_WMWRrEQkNeeYRkwe1w implements LauncherBindableItemsContainer.ItemOperator {
    public static final /* synthetic */ $$Lambda$Launcher$8Z9yfJq2_WMWRrEQkNeeYRkwe1w INSTANCE = new $$Lambda$Launcher$8Z9yfJq2_WMWRrEQkNeeYRkwe1w();

    private /* synthetic */ $$Lambda$Launcher$8Z9yfJq2_WMWRrEQkNeeYRkwe1w() {
    }

    public final boolean evaluate(ItemInfo itemInfo, View view) {
        return Launcher.lambda$pauseExpensiveViewUpdates$18(itemInfo, view);
    }
}
