package com.android.launcher3.testing;

import com.android.launcher3.Launcher;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$tp0rZUvbWITqkAs4FytlFb5AJoU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$tp0rZUvbWITqkAs4FytlFb5AJoU implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$tp0rZUvbWITqkAs4FytlFb5AJoU INSTANCE = new $$Lambda$TestInformationHandler$tp0rZUvbWITqkAs4FytlFb5AJoU();

    private /* synthetic */ $$Lambda$TestInformationHandler$tp0rZUvbWITqkAs4FytlFb5AJoU() {
    }

    public final Object apply(Object obj) {
        return ((Launcher) obj).getAppsView().getAppsStore().disableDeferUpdates(2);
    }
}
