package com.android.launcher3.testing;

import android.graphics.Point;
import android.os.Bundle;
import com.android.launcher3.testing.TestInformationHandler;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$I-emUce8XIRkt6hZ8spoFTyo3lo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$IemUce8XIRkt6hZ8spoFTyo3lo implements TestInformationHandler.BundleSetter {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$IemUce8XIRkt6hZ8spoFTyo3lo INSTANCE = new $$Lambda$TestInformationHandler$IemUce8XIRkt6hZ8spoFTyo3lo();

    private /* synthetic */ $$Lambda$TestInformationHandler$IemUce8XIRkt6hZ8spoFTyo3lo() {
    }

    public final void set(Bundle bundle, String str, Object obj) {
        bundle.putParcelable(str, (Point) obj);
    }
}
