package com.android.launcher3.model;

import com.android.launcher3.model.data.ItemInfo;
import java.util.Comparator;

/* renamed from: com.android.launcher3.model.-$$Lambda$ModelUtils$b6rGSqQCp07jIFgI4tObSfJrI6M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ModelUtils$b6rGSqQCp07jIFgI4tObSfJrI6M implements Comparator {
    public static final /* synthetic */ $$Lambda$ModelUtils$b6rGSqQCp07jIFgI4tObSfJrI6M INSTANCE = new $$Lambda$ModelUtils$b6rGSqQCp07jIFgI4tObSfJrI6M();

    private /* synthetic */ $$Lambda$ModelUtils$b6rGSqQCp07jIFgI4tObSfJrI6M() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((ItemInfo) obj).container, ((ItemInfo) obj2).container);
    }
}
