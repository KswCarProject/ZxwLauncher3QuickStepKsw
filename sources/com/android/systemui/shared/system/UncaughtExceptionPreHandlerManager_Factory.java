package com.android.systemui.shared.system;

import dagger.internal.Factory;

public final class UncaughtExceptionPreHandlerManager_Factory implements Factory<UncaughtExceptionPreHandlerManager> {
    public UncaughtExceptionPreHandlerManager get() {
        return newInstance();
    }

    public static UncaughtExceptionPreHandlerManager_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static UncaughtExceptionPreHandlerManager newInstance() {
        return new UncaughtExceptionPreHandlerManager();
    }

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final UncaughtExceptionPreHandlerManager_Factory INSTANCE = new UncaughtExceptionPreHandlerManager_Factory();

        private InstanceHolder() {
        }
    }
}
