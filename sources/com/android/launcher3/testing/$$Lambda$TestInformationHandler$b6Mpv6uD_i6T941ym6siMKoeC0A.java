package com.android.launcher3.testing;

import android.graphics.Insets;
import android.os.Bundle;
import com.android.launcher3.testing.TestInformationHandler;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A implements TestInformationHandler.BundleSetter {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A INSTANCE = new $$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A();

    private /* synthetic */ $$Lambda$TestInformationHandler$b6Mpv6uD_i6T941ym6siMKoeC0A() {
    }

    public final void set(Bundle bundle, String str, Object obj) {
        bundle.putParcelable(str, (Insets) obj);
    }
}
