package com.android.launcher3.testing;

import android.app.Activity;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$MedHHcZ7L2adMYy65cJFiELHmPM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$MedHHcZ7L2adMYy65cJFiELHmPM implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$MedHHcZ7L2adMYy65cJFiELHmPM INSTANCE = new $$Lambda$TestInformationHandler$MedHHcZ7L2adMYy65cJFiELHmPM();

    private /* synthetic */ $$Lambda$TestInformationHandler$MedHHcZ7L2adMYy65cJFiELHmPM() {
    }

    public final Object apply(Object obj) {
        return ((Activity) obj).getWindow().getDecorView().getRootWindowInsets().getSystemWindowInsets();
    }
}
