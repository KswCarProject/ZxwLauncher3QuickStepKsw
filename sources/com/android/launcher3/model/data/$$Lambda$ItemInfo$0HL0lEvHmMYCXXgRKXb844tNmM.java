package com.android.launcher3.model.data;

import android.content.ComponentName;
import com.android.launcher3.logger.LauncherAtom;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.data.-$$Lambda$ItemInfo$0HL0lEvHmMYCXXgRK-Xb844tNmM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ItemInfo$0HL0lEvHmMYCXXgRKXb844tNmM implements Function {
    public static final /* synthetic */ $$Lambda$ItemInfo$0HL0lEvHmMYCXXgRKXb844tNmM INSTANCE = new $$Lambda$ItemInfo$0HL0lEvHmMYCXXgRKXb844tNmM();

    private /* synthetic */ $$Lambda$ItemInfo$0HL0lEvHmMYCXXgRKXb844tNmM() {
    }

    public final Object apply(Object obj) {
        return LauncherAtom.Application.newBuilder().setComponentName(((ComponentName) obj).flattenToShortString()).setPackageName(((ComponentName) obj).getPackageName());
    }
}
