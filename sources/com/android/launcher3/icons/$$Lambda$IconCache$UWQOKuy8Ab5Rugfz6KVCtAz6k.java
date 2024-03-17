package com.android.launcher3.icons;

import com.android.launcher3.model.data.IconRequestInfo;
import java.util.function.Function;

/* renamed from: com.android.launcher3.icons.-$$Lambda$IconCache$UWQO-Kuy8A-b5Rugfz6KVCtAz6k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$IconCache$UWQOKuy8Ab5Rugfz6KVCtAz6k implements Function {
    public static final /* synthetic */ $$Lambda$IconCache$UWQOKuy8Ab5Rugfz6KVCtAz6k INSTANCE = new $$Lambda$IconCache$UWQOKuy8Ab5Rugfz6KVCtAz6k();

    private /* synthetic */ $$Lambda$IconCache$UWQOKuy8Ab5Rugfz6KVCtAz6k() {
    }

    public final Object apply(Object obj) {
        return ((IconRequestInfo) obj).itemInfo.getTargetComponent();
    }
}
