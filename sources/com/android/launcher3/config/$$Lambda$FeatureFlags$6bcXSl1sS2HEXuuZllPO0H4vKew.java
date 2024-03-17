package com.android.launcher3.config;

import com.android.launcher3.config.FeatureFlags;
import java.util.Comparator;

/* renamed from: com.android.launcher3.config.-$$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew implements Comparator {
    public static final /* synthetic */ $$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew INSTANCE = new $$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew();

    private /* synthetic */ $$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((FeatureFlags.DebugFlag) obj).key.compareToIgnoreCase(((FeatureFlags.DebugFlag) obj2).key);
    }
}
