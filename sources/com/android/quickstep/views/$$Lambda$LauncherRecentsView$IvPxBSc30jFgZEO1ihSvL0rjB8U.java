package com.android.quickstep.views;

import com.android.quickstep.RemoteTargetGluer;
import java.util.function.Consumer;

/* renamed from: com.android.quickstep.views.-$$Lambda$LauncherRecentsView$IvPxBSc30jFgZEO1ihSvL0rjB8U  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LauncherRecentsView$IvPxBSc30jFgZEO1ihSvL0rjB8U implements Consumer {
    public static final /* synthetic */ $$Lambda$LauncherRecentsView$IvPxBSc30jFgZEO1ihSvL0rjB8U INSTANCE = new $$Lambda$LauncherRecentsView$IvPxBSc30jFgZEO1ihSvL0rjB8U();

    private /* synthetic */ $$Lambda$LauncherRecentsView$IvPxBSc30jFgZEO1ihSvL0rjB8U() {
    }

    public final void accept(Object obj) {
        ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().setDrawsBelowRecents(true);
    }
}
