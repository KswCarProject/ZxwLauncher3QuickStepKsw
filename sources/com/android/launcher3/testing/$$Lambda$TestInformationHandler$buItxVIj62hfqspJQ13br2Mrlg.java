package com.android.launcher3.testing;

import android.app.Activity;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$buItxV-Ij62hfqspJQ13br2Mrlg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$buItxVIj62hfqspJQ13br2Mrlg implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$buItxVIj62hfqspJQ13br2Mrlg INSTANCE = new $$Lambda$TestInformationHandler$buItxVIj62hfqspJQ13br2Mrlg();

    private /* synthetic */ $$Lambda$TestInformationHandler$buItxVIj62hfqspJQ13br2Mrlg() {
    }

    public final Object apply(Object obj) {
        return ((Activity) obj).getWindow().getDecorView().getRootWindowInsets();
    }
}
