package com.android.quickstep;

import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.function.Supplier;

/* renamed from: com.android.quickstep.-$$Lambda$TopTaskTracker$z5NnLW2Rfhj-zLdBEqnd9OJIpl8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TopTaskTracker$z5NnLW2RfhjzLdBEqnd9OJIpl8 implements Supplier {
    public static final /* synthetic */ $$Lambda$TopTaskTracker$z5NnLW2RfhjzLdBEqnd9OJIpl8 INSTANCE = new $$Lambda$TopTaskTracker$z5NnLW2RfhjzLdBEqnd9OJIpl8();

    private /* synthetic */ $$Lambda$TopTaskTracker$z5NnLW2RfhjzLdBEqnd9OJIpl8() {
    }

    public final Object get() {
        return ActivityManagerWrapper.getInstance().getRunningTasks(false);
    }
}
