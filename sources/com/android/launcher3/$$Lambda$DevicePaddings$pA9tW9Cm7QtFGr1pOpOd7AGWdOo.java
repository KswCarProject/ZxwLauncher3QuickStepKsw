package com.android.launcher3;

import com.android.launcher3.DevicePaddings;
import java.util.Comparator;

/* renamed from: com.android.launcher3.-$$Lambda$DevicePaddings$pA9tW9Cm7QtFGr1pOpOd7AGWdOo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DevicePaddings$pA9tW9Cm7QtFGr1pOpOd7AGWdOo implements Comparator {
    public static final /* synthetic */ $$Lambda$DevicePaddings$pA9tW9Cm7QtFGr1pOpOd7AGWdOo INSTANCE = new $$Lambda$DevicePaddings$pA9tW9Cm7QtFGr1pOpOd7AGWdOo();

    private /* synthetic */ $$Lambda$DevicePaddings$pA9tW9Cm7QtFGr1pOpOd7AGWdOo() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((DevicePaddings.DevicePadding) obj).maxEmptySpacePx, ((DevicePaddings.DevicePadding) obj2).maxEmptySpacePx);
    }
}
