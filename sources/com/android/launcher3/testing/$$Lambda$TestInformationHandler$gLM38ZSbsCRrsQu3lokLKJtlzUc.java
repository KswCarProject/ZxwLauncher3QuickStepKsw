package com.android.launcher3.testing;

import com.android.launcher3.Launcher;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$gLM38ZSbsCRrsQu3lokLKJtlzUc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$gLM38ZSbsCRrsQu3lokLKJtlzUc implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$gLM38ZSbsCRrsQu3lokLKJtlzUc INSTANCE = new $$Lambda$TestInformationHandler$gLM38ZSbsCRrsQu3lokLKJtlzUc();

    private /* synthetic */ $$Lambda$TestInformationHandler$gLM38ZSbsCRrsQu3lokLKJtlzUc() {
    }

    public final Object apply(Object obj) {
        return ((Launcher) obj).getAppsView().getAppsStore().enableDeferUpdates(2);
    }
}
