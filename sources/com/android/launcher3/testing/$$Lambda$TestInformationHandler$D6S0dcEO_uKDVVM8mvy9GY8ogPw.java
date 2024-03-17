package com.android.launcher3.testing;

import android.os.Bundle;
import com.android.launcher3.testing.TestInformationHandler;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw implements TestInformationHandler.BundleSetter {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw INSTANCE = new $$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw();

    private /* synthetic */ $$Lambda$TestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw() {
    }

    public final void set(Bundle bundle, String str, Object obj) {
        bundle.putInt(str, ((Integer) obj).intValue());
    }
}
