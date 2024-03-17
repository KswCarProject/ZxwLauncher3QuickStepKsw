package com.android.launcher3.icons;

import com.android.launcher3.model.data.IconRequestInfo;
import java.util.function.Function;

/* renamed from: com.android.launcher3.icons.-$$Lambda$IconCache$NU8C6WuM7kSDL9RdDmAP_WJFCkM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$IconCache$NU8C6WuM7kSDL9RdDmAP_WJFCkM implements Function {
    public static final /* synthetic */ $$Lambda$IconCache$NU8C6WuM7kSDL9RdDmAP_WJFCkM INSTANCE = new $$Lambda$IconCache$NU8C6WuM7kSDL9RdDmAP_WJFCkM();

    private /* synthetic */ $$Lambda$IconCache$NU8C6WuM7kSDL9RdDmAP_WJFCkM() {
    }

    public final Object apply(Object obj) {
        return ((IconRequestInfo) obj).itemInfo.getTargetComponent();
    }
}
