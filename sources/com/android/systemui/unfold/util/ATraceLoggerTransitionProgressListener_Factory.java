package com.android.systemui.unfold.util;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class ATraceLoggerTransitionProgressListener_Factory implements Factory<ATraceLoggerTransitionProgressListener> {
    private final Provider<String> tracePrefixProvider;

    public ATraceLoggerTransitionProgressListener_Factory(Provider<String> provider) {
        this.tracePrefixProvider = provider;
    }

    public ATraceLoggerTransitionProgressListener get() {
        return newInstance(this.tracePrefixProvider.get());
    }

    public static ATraceLoggerTransitionProgressListener_Factory create(Provider<String> provider) {
        return new ATraceLoggerTransitionProgressListener_Factory(provider);
    }

    public static ATraceLoggerTransitionProgressListener newInstance(String str) {
        return new ATraceLoggerTransitionProgressListener(str);
    }
}
