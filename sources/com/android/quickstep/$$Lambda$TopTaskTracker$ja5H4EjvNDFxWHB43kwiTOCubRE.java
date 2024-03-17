package com.android.quickstep;

import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.function.Supplier;

/* renamed from: com.android.quickstep.-$$Lambda$TopTaskTracker$ja5H4EjvNDFxWHB43kwiTOCubRE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TopTaskTracker$ja5H4EjvNDFxWHB43kwiTOCubRE implements Supplier {
    public static final /* synthetic */ $$Lambda$TopTaskTracker$ja5H4EjvNDFxWHB43kwiTOCubRE INSTANCE = new $$Lambda$TopTaskTracker$ja5H4EjvNDFxWHB43kwiTOCubRE();

    private /* synthetic */ $$Lambda$TopTaskTracker$ja5H4EjvNDFxWHB43kwiTOCubRE() {
    }

    public final Object get() {
        return ActivityManagerWrapper.getInstance().getRunningTasks(true);
    }
}
