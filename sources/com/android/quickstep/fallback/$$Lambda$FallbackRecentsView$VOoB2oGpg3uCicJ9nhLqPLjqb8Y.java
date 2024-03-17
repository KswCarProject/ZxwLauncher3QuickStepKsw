package com.android.quickstep.fallback;

import com.android.quickstep.RemoteTargetGluer;
import java.util.function.Consumer;

/* renamed from: com.android.quickstep.fallback.-$$Lambda$FallbackRecentsView$VOoB2oGpg3uCicJ9nhLqPLjqb8Y  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FallbackRecentsView$VOoB2oGpg3uCicJ9nhLqPLjqb8Y implements Consumer {
    public static final /* synthetic */ $$Lambda$FallbackRecentsView$VOoB2oGpg3uCicJ9nhLqPLjqb8Y INSTANCE = new $$Lambda$FallbackRecentsView$VOoB2oGpg3uCicJ9nhLqPLjqb8Y();

    private /* synthetic */ $$Lambda$FallbackRecentsView$VOoB2oGpg3uCicJ9nhLqPLjqb8Y() {
    }

    public final void accept(Object obj) {
        ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().setDrawsBelowRecents(true);
    }
}
