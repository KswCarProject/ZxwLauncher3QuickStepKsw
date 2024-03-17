package com.android.launcher3.widget.picker;

import com.android.launcher3.model.data.PackageItemInfo;
import com.android.launcher3.util.PackageUserKey;
import java.util.function.Function;

/* renamed from: com.android.launcher3.widget.picker.-$$Lambda$WidgetsListAdapter$c7ABGsgjgfw-l5yWNIyfH_07N54  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WidgetsListAdapter$c7ABGsgjgfwl5yWNIyfH_07N54 implements Function {
    public static final /* synthetic */ $$Lambda$WidgetsListAdapter$c7ABGsgjgfwl5yWNIyfH_07N54 INSTANCE = new $$Lambda$WidgetsListAdapter$c7ABGsgjgfwl5yWNIyfH_07N54();

    private /* synthetic */ $$Lambda$WidgetsListAdapter$c7ABGsgjgfwl5yWNIyfH_07N54() {
    }

    public final Object apply(Object obj) {
        return PackageUserKey.fromPackageItemInfo((PackageItemInfo) obj);
    }
}
