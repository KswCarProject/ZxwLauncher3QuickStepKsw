package com.android.systemui.unfold.updates;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class DeviceFoldStateProvider_Factory implements Factory<DeviceFoldStateProvider> {
    private final Provider<ActivityManager> activityManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceStateManager> deviceStateManagerProvider;
    private final Provider<Handler> handlerProvider;
    private final Provider<HingeAngleProvider> hingeAngleProvider;
    private final Provider<Executor> mainExecutorProvider;
    private final Provider<ScreenStatusProvider> screenStatusProvider;

    public DeviceFoldStateProvider_Factory(Provider<Context> provider, Provider<HingeAngleProvider> provider2, Provider<ScreenStatusProvider> provider3, Provider<DeviceStateManager> provider4, Provider<ActivityManager> provider5, Provider<Executor> provider6, Provider<Handler> provider7) {
        this.contextProvider = provider;
        this.hingeAngleProvider = provider2;
        this.screenStatusProvider = provider3;
        this.deviceStateManagerProvider = provider4;
        this.activityManagerProvider = provider5;
        this.mainExecutorProvider = provider6;
        this.handlerProvider = provider7;
    }

    public DeviceFoldStateProvider get() {
        return newInstance(this.contextProvider.get(), this.hingeAngleProvider.get(), this.screenStatusProvider.get(), this.deviceStateManagerProvider.get(), this.activityManagerProvider.get(), this.mainExecutorProvider.get(), this.handlerProvider.get());
    }

    public static DeviceFoldStateProvider_Factory create(Provider<Context> provider, Provider<HingeAngleProvider> provider2, Provider<ScreenStatusProvider> provider3, Provider<DeviceStateManager> provider4, Provider<ActivityManager> provider5, Provider<Executor> provider6, Provider<Handler> provider7) {
        return new DeviceFoldStateProvider_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
    }

    public static DeviceFoldStateProvider newInstance(Context context, HingeAngleProvider hingeAngleProvider2, ScreenStatusProvider screenStatusProvider2, DeviceStateManager deviceStateManager, ActivityManager activityManager, Executor executor, Handler handler) {
        return new DeviceFoldStateProvider(context, hingeAngleProvider2, screenStatusProvider2, deviceStateManager, activityManager, executor, handler);
    }
}
