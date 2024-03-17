package com.android.quickstep.util;

import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.function.Consumer;

/* renamed from: com.android.quickstep.util.-$$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4 implements Consumer {
    public static final /* synthetic */ $$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4 INSTANCE = new $$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4();

    private /* synthetic */ $$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4() {
    }

    public final void accept(Object obj) {
        ((ScreenStatusProvider.ScreenListener) obj).onScreenTurnedOn();
    }
}
