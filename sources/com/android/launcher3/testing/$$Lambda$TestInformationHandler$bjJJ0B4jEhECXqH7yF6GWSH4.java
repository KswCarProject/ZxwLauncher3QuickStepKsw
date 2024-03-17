package com.android.launcher3.testing;

import com.android.launcher3.Launcher;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$bjJJ0B4jE-hECXqH-7yF6GWSH-4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$bjJJ0B4jEhECXqH7yF6GWSH4 implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$bjJJ0B4jEhECXqH7yF6GWSH4 INSTANCE = new $$Lambda$TestInformationHandler$bjJJ0B4jEhECXqH7yF6GWSH4();

    private /* synthetic */ $$Lambda$TestInformationHandler$bjJJ0B4jEhECXqH7yF6GWSH4() {
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(((Launcher) obj).getAppsView().getActiveRecyclerView().getCurrentScrollY());
    }
}
