package com.android.quickstep.inputconsumers;

import com.android.systemui.shared.system.ActivityManagerWrapper;

/* renamed from: com.android.quickstep.inputconsumers.-$$Lambda$OtherActivityInputConsumer$EjYF-k6GXX3Ti0a0CqE4Q7WLJNI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OtherActivityInputConsumer$EjYFk6GXX3Ti0a0CqE4Q7WLJNI implements Runnable {
    public static final /* synthetic */ $$Lambda$OtherActivityInputConsumer$EjYFk6GXX3Ti0a0CqE4Q7WLJNI INSTANCE = new $$Lambda$OtherActivityInputConsumer$EjYFk6GXX3Ti0a0CqE4Q7WLJNI();

    private /* synthetic */ $$Lambda$OtherActivityInputConsumer$EjYFk6GXX3Ti0a0CqE4Q7WLJNI() {
    }

    public final void run() {
        ActivityManagerWrapper.getInstance().cancelRecentsAnimation(true);
    }
}
