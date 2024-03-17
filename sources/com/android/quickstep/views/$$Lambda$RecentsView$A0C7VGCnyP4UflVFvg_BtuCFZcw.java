package com.android.quickstep.views;

import com.android.quickstep.RemoteTargetGluer;
import com.android.quickstep.util.SurfaceTransactionApplier;
import java.util.function.Consumer;

/* renamed from: com.android.quickstep.views.-$$Lambda$RecentsView$A0C7VGCnyP4UflVFvg_BtuCFZcw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RecentsView$A0C7VGCnyP4UflVFvg_BtuCFZcw implements Consumer {
    public static final /* synthetic */ $$Lambda$RecentsView$A0C7VGCnyP4UflVFvg_BtuCFZcw INSTANCE = new $$Lambda$RecentsView$A0C7VGCnyP4UflVFvg_BtuCFZcw();

    private /* synthetic */ $$Lambda$RecentsView$A0C7VGCnyP4UflVFvg_BtuCFZcw() {
    }

    public final void accept(Object obj) {
        ((RemoteTargetGluer.RemoteTargetHandle) obj).getTransformParams().setSyncTransactionApplier((SurfaceTransactionApplier) null);
    }
}
