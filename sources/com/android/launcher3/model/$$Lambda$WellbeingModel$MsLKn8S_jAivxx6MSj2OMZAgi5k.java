package com.android.launcher3.model;

import android.content.pm.LauncherActivityInfo;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.-$$Lambda$WellbeingModel$MsLKn8S_jAivxx6MSj2OMZAgi5k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WellbeingModel$MsLKn8S_jAivxx6MSj2OMZAgi5k implements Function {
    public static final /* synthetic */ $$Lambda$WellbeingModel$MsLKn8S_jAivxx6MSj2OMZAgi5k INSTANCE = new $$Lambda$WellbeingModel$MsLKn8S_jAivxx6MSj2OMZAgi5k();

    private /* synthetic */ $$Lambda$WellbeingModel$MsLKn8S_jAivxx6MSj2OMZAgi5k() {
    }

    public final Object apply(Object obj) {
        return ((LauncherActivityInfo) obj).getApplicationInfo().packageName;
    }
}
