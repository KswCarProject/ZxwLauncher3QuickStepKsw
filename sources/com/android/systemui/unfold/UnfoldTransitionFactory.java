package com.android.systemui.unfold;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.unfold.UnfoldSharedComponent;
import com.android.systemui.unfold.config.ResourceUnfoldTransitionConfig;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000B\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001aV\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015¨\u0006\u0016"}, d2 = {"createConfig", "Lcom/android/systemui/unfold/config/UnfoldTransitionConfig;", "context", "Landroid/content/Context;", "createUnfoldTransitionProgressProvider", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "config", "screenStatusProvider", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;", "deviceStateManager", "Landroid/hardware/devicestate/DeviceStateManager;", "activityManager", "Landroid/app/ActivityManager;", "sensorManager", "Landroid/hardware/SensorManager;", "mainHandler", "Landroid/os/Handler;", "mainExecutor", "Ljava/util/concurrent/Executor;", "backgroundExecutor", "tracingTagPrefix", "", "SharedLibWrapper_release"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* compiled from: UnfoldTransitionFactory.kt */
public final class UnfoldTransitionFactory {
    public static final UnfoldTransitionProgressProvider createUnfoldTransitionProgressProvider(Context context, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler handler, Executor executor, Executor executor2, String str) {
        Intrinsics.checkNotNullParameter(context, "context");
        UnfoldTransitionConfig unfoldTransitionConfig2 = unfoldTransitionConfig;
        Intrinsics.checkNotNullParameter(unfoldTransitionConfig2, "config");
        ScreenStatusProvider screenStatusProvider2 = screenStatusProvider;
        Intrinsics.checkNotNullParameter(screenStatusProvider2, "screenStatusProvider");
        DeviceStateManager deviceStateManager2 = deviceStateManager;
        Intrinsics.checkNotNullParameter(deviceStateManager2, "deviceStateManager");
        ActivityManager activityManager2 = activityManager;
        Intrinsics.checkNotNullParameter(activityManager2, "activityManager");
        SensorManager sensorManager2 = sensorManager;
        Intrinsics.checkNotNullParameter(sensorManager2, "sensorManager");
        Handler handler2 = handler;
        Intrinsics.checkNotNullParameter(handler2, "mainHandler");
        Executor executor3 = executor;
        Intrinsics.checkNotNullParameter(executor3, "mainExecutor");
        Executor executor4 = executor2;
        Intrinsics.checkNotNullParameter(executor4, "backgroundExecutor");
        String str2 = str;
        Intrinsics.checkNotNullParameter(str2, "tracingTagPrefix");
        UnfoldSharedComponent.Factory factory = DaggerUnfoldSharedComponent.factory();
        Intrinsics.checkNotNullExpressionValue(factory, "factory()");
        UnfoldTransitionProgressProvider orElse = UnfoldSharedComponent.Factory.DefaultImpls.create$default(factory, context, unfoldTransitionConfig2, screenStatusProvider2, deviceStateManager2, activityManager2, sensorManager2, handler2, executor3, executor4, str2, (ContentResolver) null, 1024, (Object) null).getUnfoldTransitionProvider().orElse((Object) null);
        if (orElse != null) {
            return orElse;
        }
        throw new IllegalStateException("Trying to create UnfoldTransitionProgressProvider when the transition is disabled");
    }

    public static final UnfoldTransitionConfig createConfig(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        return new ResourceUnfoldTransitionConfig(context);
    }
}
