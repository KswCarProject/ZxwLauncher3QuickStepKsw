package com.android.launcher3.model;

import android.content.pm.PackageInstaller;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.-$$Lambda$FirstScreenBroadcast$yRJMSjoVbjNnjnD_Bz-Gj4-xecE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FirstScreenBroadcast$yRJMSjoVbjNnjnD_BzGj4xecE implements Function {
    public static final /* synthetic */ $$Lambda$FirstScreenBroadcast$yRJMSjoVbjNnjnD_BzGj4xecE INSTANCE = new $$Lambda$FirstScreenBroadcast$yRJMSjoVbjNnjnD_BzGj4xecE();

    private /* synthetic */ $$Lambda$FirstScreenBroadcast$yRJMSjoVbjNnjnD_BzGj4xecE() {
    }

    public final Object apply(Object obj) {
        return ((PackageInstaller.SessionInfo) obj).getInstallerPackageName();
    }
}
