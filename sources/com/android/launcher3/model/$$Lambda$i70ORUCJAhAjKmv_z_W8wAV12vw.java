package com.android.launcher3.model;

import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import java.util.function.Function;

/* renamed from: com.android.launcher3.model.-$$Lambda$i70ORUCJAhAjKmv_z_W8wAV12vw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$i70ORUCJAhAjKmv_z_W8wAV12vw implements Function {
    public static final /* synthetic */ $$Lambda$i70ORUCJAhAjKmv_z_W8wAV12vw INSTANCE = new $$Lambda$i70ORUCJAhAjKmv_z_W8wAV12vw();

    private /* synthetic */ $$Lambda$i70ORUCJAhAjKmv_z_W8wAV12vw() {
    }

    public final Object apply(Object obj) {
        return ShortcutKey.fromItemInfo((WorkspaceItemInfo) obj);
    }
}
