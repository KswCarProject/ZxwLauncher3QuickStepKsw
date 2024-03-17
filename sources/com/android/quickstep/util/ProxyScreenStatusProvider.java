package com.android.quickstep.util;

import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.ArrayList;
import java.util.List;

public class ProxyScreenStatusProvider implements ScreenStatusProvider {
    public static final ProxyScreenStatusProvider INSTANCE = new ProxyScreenStatusProvider();
    private final List<ScreenStatusProvider.ScreenListener> mListeners = new ArrayList();

    public void onScreenTurnedOn() {
        this.mListeners.forEach($$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4.INSTANCE);
    }

    public void addCallback(ScreenStatusProvider.ScreenListener screenListener) {
        this.mListeners.add(screenListener);
    }

    public void removeCallback(ScreenStatusProvider.ScreenListener screenListener) {
        this.mListeners.remove(screenListener);
    }
}
