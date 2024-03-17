package com.android.launcher3.model.data;

import android.content.ComponentName;
import com.android.launcher3.logger.LauncherAtom;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.data.-$$Lambda$ItemInfo$BNXK1GCjh5afrzKvd2Boil2I8Zs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ItemInfo$BNXK1GCjh5afrzKvd2Boil2I8Zs implements Function {
    public static final /* synthetic */ $$Lambda$ItemInfo$BNXK1GCjh5afrzKvd2Boil2I8Zs INSTANCE = new $$Lambda$ItemInfo$BNXK1GCjh5afrzKvd2Boil2I8Zs();

    private /* synthetic */ $$Lambda$ItemInfo$BNXK1GCjh5afrzKvd2Boil2I8Zs() {
    }

    public final Object apply(Object obj) {
        return LauncherAtom.Widget.newBuilder().setComponentName(((ComponentName) obj).flattenToShortString()).setPackageName(((ComponentName) obj).getPackageName());
    }
}
