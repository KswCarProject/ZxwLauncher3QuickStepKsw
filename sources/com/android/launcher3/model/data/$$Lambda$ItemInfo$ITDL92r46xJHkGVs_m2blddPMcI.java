package com.android.launcher3.model.data;

import android.content.ComponentName;
import com.android.launcher3.logger.LauncherAtom;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.data.-$$Lambda$ItemInfo$ITDL92r46xJHkGVs_m2blddPMcI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ItemInfo$ITDL92r46xJHkGVs_m2blddPMcI implements Function {
    public static final /* synthetic */ $$Lambda$ItemInfo$ITDL92r46xJHkGVs_m2blddPMcI INSTANCE = new $$Lambda$ItemInfo$ITDL92r46xJHkGVs_m2blddPMcI();

    private /* synthetic */ $$Lambda$ItemInfo$ITDL92r46xJHkGVs_m2blddPMcI() {
    }

    public final Object apply(Object obj) {
        return LauncherAtom.Shortcut.newBuilder().setShortcutName(((ComponentName) obj).flattenToShortString());
    }
}
