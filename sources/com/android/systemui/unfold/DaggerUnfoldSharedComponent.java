package com.android.systemui.unfold;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.unfold.UnfoldSharedComponent;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.updates.DeviceFoldStateProvider;
import com.android.systemui.unfold.updates.FoldStateProvider;
import com.android.systemui.unfold.updates.hinge.HingeAngleProvider;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import com.android.systemui.unfold.util.ATraceLoggerTransitionProgressListener;
import com.android.systemui.unfold.util.ScaleAwareTransitionProgressProvider;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.SingleCheck;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class DaggerUnfoldSharedComponent implements UnfoldSharedComponent {
    private final ActivityManager activityManager;
    private final Executor backgroundExecutor;
    /* access modifiers changed from: private */
    public final UnfoldTransitionConfig config;
    /* access modifiers changed from: private */
    public final ContentResolver contentResolver;
    private final Context context;
    private final DeviceStateManager deviceStateManager;
    private final Executor executor;
    /* access modifiers changed from: private */
    public Provider<ScaleAwareTransitionProgressProvider.Factory> factoryProvider;
    private final Handler handler;
    /* access modifiers changed from: private */
    public Provider<FoldStateProvider> provideFoldStateProvider;
    private final ScreenStatusProvider screenStatusProvider;
    private final SensorManager sensorManager;
    private final String tracingTagPrefix;
    private final DaggerUnfoldSharedComponent unfoldSharedComponent;
    /* access modifiers changed from: private */
    public final UnfoldSharedModule unfoldSharedModule;
    private Provider<Optional<UnfoldTransitionProgressProvider>> unfoldTransitionProgressProvider;

    private DaggerUnfoldSharedComponent(UnfoldSharedModule unfoldSharedModule2, Context context2, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider2, DeviceStateManager deviceStateManager2, ActivityManager activityManager2, SensorManager sensorManager2, Handler handler2, Executor executor2, Executor executor3, String str, ContentResolver contentResolver2) {
        this.unfoldSharedComponent = this;
        this.unfoldSharedModule = unfoldSharedModule2;
        this.config = unfoldTransitionConfig;
        this.contentResolver = contentResolver2;
        this.tracingTagPrefix = str;
        this.context = context2;
        this.sensorManager = sensorManager2;
        this.backgroundExecutor = executor3;
        this.screenStatusProvider = screenStatusProvider2;
        this.deviceStateManager = deviceStateManager2;
        this.activityManager = activityManager2;
        this.executor = executor2;
        this.handler = handler2;
        initialize(unfoldSharedModule2, context2, unfoldTransitionConfig, screenStatusProvider2, deviceStateManager2, activityManager2, sensorManager2, handler2, executor2, executor3, str, contentResolver2);
    }

    public static UnfoldSharedComponent.Factory factory() {
        return new Factory();
    }

    /* access modifiers changed from: private */
    public ATraceLoggerTransitionProgressListener aTraceLoggerTransitionProgressListener() {
        return new ATraceLoggerTransitionProgressListener(this.tracingTagPrefix);
    }

    private HingeAngleProvider hingeAngleProvider() {
        return UnfoldSharedModule_HingeAngleProviderFactory.hingeAngleProvider(this.unfoldSharedModule, this.config, this.sensorManager, this.backgroundExecutor);
    }

    /* access modifiers changed from: private */
    public DeviceFoldStateProvider deviceFoldStateProvider() {
        return new DeviceFoldStateProvider(this.context, hingeAngleProvider(), this.screenStatusProvider, this.deviceStateManager, this.activityManager, this.executor, this.handler);
    }

    private void initialize(UnfoldSharedModule unfoldSharedModule2, Context context2, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider2, DeviceStateManager deviceStateManager2, ActivityManager activityManager2, SensorManager sensorManager2, Handler handler2, Executor executor2, Executor executor3, String str, ContentResolver contentResolver2) {
        this.factoryProvider = SingleCheck.provider(new SwitchingProvider(this.unfoldSharedComponent, 1));
        this.provideFoldStateProvider = DoubleCheck.provider(new SwitchingProvider(this.unfoldSharedComponent, 2));
        this.unfoldTransitionProgressProvider = DoubleCheck.provider(new SwitchingProvider(this.unfoldSharedComponent, 0));
    }

    public Optional<UnfoldTransitionProgressProvider> getUnfoldTransitionProvider() {
        return this.unfoldTransitionProgressProvider.get();
    }

    private static final class Factory implements UnfoldSharedComponent.Factory {
        private Factory() {
        }

        public UnfoldSharedComponent create(Context context, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler handler, Executor executor, Executor executor2, String str, ContentResolver contentResolver) {
            Preconditions.checkNotNull(context);
            Preconditions.checkNotNull(unfoldTransitionConfig);
            Preconditions.checkNotNull(screenStatusProvider);
            Preconditions.checkNotNull(deviceStateManager);
            Preconditions.checkNotNull(activityManager);
            Preconditions.checkNotNull(sensorManager);
            Preconditions.checkNotNull(handler);
            Preconditions.checkNotNull(executor);
            Preconditions.checkNotNull(executor2);
            Preconditions.checkNotNull(str);
            Preconditions.checkNotNull(contentResolver);
            return new DaggerUnfoldSharedComponent(new UnfoldSharedModule(), context, unfoldTransitionConfig, screenStatusProvider, deviceStateManager, activityManager, sensorManager, handler, executor, executor2, str, contentResolver);
        }
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
        private final int id;
        /* access modifiers changed from: private */
        public final DaggerUnfoldSharedComponent unfoldSharedComponent;

        SwitchingProvider(DaggerUnfoldSharedComponent daggerUnfoldSharedComponent, int i) {
            this.unfoldSharedComponent = daggerUnfoldSharedComponent;
            this.id = i;
        }

        public T get() {
            int i = this.id;
            if (i == 0) {
                return UnfoldSharedModule_UnfoldTransitionProgressProviderFactory.unfoldTransitionProgressProvider(this.unfoldSharedComponent.unfoldSharedModule, this.unfoldSharedComponent.config, (ScaleAwareTransitionProgressProvider.Factory) this.unfoldSharedComponent.factoryProvider.get(), this.unfoldSharedComponent.aTraceLoggerTransitionProgressListener(), (FoldStateProvider) this.unfoldSharedComponent.provideFoldStateProvider.get());
            }
            if (i == 1) {
                return new ScaleAwareTransitionProgressProvider.Factory() {
                    public ScaleAwareTransitionProgressProvider wrap(UnfoldTransitionProgressProvider unfoldTransitionProgressProvider) {
                        return new ScaleAwareTransitionProgressProvider(unfoldTransitionProgressProvider, SwitchingProvider.this.unfoldSharedComponent.contentResolver);
                    }
                };
            }
            if (i == 2) {
                return UnfoldSharedModule_ProvideFoldStateProviderFactory.provideFoldStateProvider(this.unfoldSharedComponent.unfoldSharedModule, this.unfoldSharedComponent.deviceFoldStateProvider());
            }
            throw new AssertionError(this.id);
        }
    }
}
