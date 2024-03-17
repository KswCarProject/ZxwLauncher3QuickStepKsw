package com.android.launcher3.testing;

import com.android.launcher3.Launcher;
import com.android.launcher3.widget.picker.WidgetsFullSheet;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$7HszJHUR_yImtj1-7H6DnPC-eP0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$7HszJHUR_yImtj17H6DnPCeP0 implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$7HszJHUR_yImtj17H6DnPCeP0 INSTANCE = new $$Lambda$TestInformationHandler$7HszJHUR_yImtj17H6DnPCeP0();

    private /* synthetic */ $$Lambda$TestInformationHandler$7HszJHUR_yImtj17H6DnPCeP0() {
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(WidgetsFullSheet.getWidgetsView((Launcher) obj).getCurrentScrollY());
    }
}
