package com.android.quickstep;

import android.content.Context;
import com.android.launcher3.util.MainThreadInitializedObject;

/* renamed from: com.android.quickstep.-$$Lambda$BFHk0_dcwlOR5ZBx4PMAIQQs2Jk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$BFHk0_dcwlOR5ZBx4PMAIQQs2Jk implements MainThreadInitializedObject.ObjectProvider {
    public static final /* synthetic */ $$Lambda$BFHk0_dcwlOR5ZBx4PMAIQQs2Jk INSTANCE = new $$Lambda$BFHk0_dcwlOR5ZBx4PMAIQQs2Jk();

    private /* synthetic */ $$Lambda$BFHk0_dcwlOR5ZBx4PMAIQQs2Jk() {
    }

    public final Object get(Context context) {
        return new SimpleOrientationTouchTransformer(context);
    }
}
