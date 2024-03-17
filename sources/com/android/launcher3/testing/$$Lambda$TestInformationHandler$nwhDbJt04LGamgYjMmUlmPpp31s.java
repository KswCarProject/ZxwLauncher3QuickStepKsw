package com.android.launcher3.testing;

import android.os.Bundle;
import com.android.launcher3.testing.TestInformationHandler;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s implements TestInformationHandler.BundleSetter {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s INSTANCE = new $$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s();

    private /* synthetic */ $$Lambda$TestInformationHandler$nwhDbJt04LGamgYjMmUlmPpp31s() {
    }

    public final void set(Bundle bundle, String str, Object obj) {
        bundle.putBoolean(str, ((Boolean) obj).booleanValue());
    }
}
