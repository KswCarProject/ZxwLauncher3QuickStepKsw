package com.android.systemui.shared.rotation;

import com.android.systemui.shared.system.ActivityManagerWrapper;
import java.util.function.Function;

/* renamed from: com.android.systemui.shared.rotation.-$$Lambda$6C9JU6u-zYg5T9BlQa8suAkJ9sY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$6C9JU6uzYg5T9BlQa8suAkJ9sY implements Function {
    public static final /* synthetic */ $$Lambda$6C9JU6uzYg5T9BlQa8suAkJ9sY INSTANCE = new $$Lambda$6C9JU6uzYg5T9BlQa8suAkJ9sY();

    private /* synthetic */ $$Lambda$6C9JU6uzYg5T9BlQa8suAkJ9sY() {
    }

    public final Object apply(Object obj) {
        return ((ActivityManagerWrapper) obj).getRunningTask();
    }
}
