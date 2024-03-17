package com.android.systemui.unfold;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.os.Handler;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.dagger.qualifiers.UiBackground;
import com.android.systemui.unfold.config.UnfoldTransitionConfig;
import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import com.android.systemui.unfold.util.UnfoldTransitionATracePrefix;
import dagger.BindsInstance;
import dagger.Component;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Singleton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Component(modules = {UnfoldSharedModule.class})
@Singleton
@Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\ba\u0018\u00002\u00020\u0001:\u0001\u0007R\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006¨\u0006\b"}, d2 = {"Lcom/android/systemui/unfold/UnfoldSharedComponent;", "", "unfoldTransitionProvider", "Ljava/util/Optional;", "Lcom/android/systemui/unfold/UnfoldTransitionProgressProvider;", "getUnfoldTransitionProvider", "()Ljava/util/Optional;", "Factory", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: UnfoldSharedComponent.kt */
public interface UnfoldSharedComponent {
    Optional<UnfoldTransitionProgressProvider> getUnfoldTransitionProvider();

    @Component.Factory
    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001Jv\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\b\u0001\u0010\b\u001a\u00020\t2\b\b\u0001\u0010\n\u001a\u00020\u000b2\b\b\u0001\u0010\f\u001a\u00020\r2\b\b\u0001\u0010\u000e\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\u00112\b\b\u0001\u0010\u0012\u001a\u00020\u00132\b\b\u0001\u0010\u0014\u001a\u00020\u00132\b\b\u0001\u0010\u0015\u001a\u00020\u00162\b\b\u0003\u0010\u0017\u001a\u00020\u0018H&¨\u0006\u0019"}, d2 = {"Lcom/android/systemui/unfold/UnfoldSharedComponent$Factory;", "", "create", "Lcom/android/systemui/unfold/UnfoldSharedComponent;", "context", "Landroid/content/Context;", "config", "Lcom/android/systemui/unfold/config/UnfoldTransitionConfig;", "screenStatusProvider", "Lcom/android/systemui/unfold/updates/screen/ScreenStatusProvider;", "deviceStateManager", "Landroid/hardware/devicestate/DeviceStateManager;", "activityManager", "Landroid/app/ActivityManager;", "sensorManager", "Landroid/hardware/SensorManager;", "handler", "Landroid/os/Handler;", "executor", "Ljava/util/concurrent/Executor;", "backgroundExecutor", "tracingTagPrefix", "", "contentResolver", "Landroid/content/ContentResolver;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: UnfoldSharedComponent.kt */
    public interface Factory {
        UnfoldSharedComponent create(@BindsInstance Context context, @BindsInstance UnfoldTransitionConfig unfoldTransitionConfig, @BindsInstance ScreenStatusProvider screenStatusProvider, @BindsInstance DeviceStateManager deviceStateManager, @BindsInstance ActivityManager activityManager, @BindsInstance SensorManager sensorManager, @Main @BindsInstance Handler handler, @Main @BindsInstance Executor executor, @BindsInstance @UiBackground Executor executor2, @UnfoldTransitionATracePrefix @BindsInstance String str, @BindsInstance ContentResolver contentResolver);

        @Metadata(k = 3, mv = {1, 6, 0}, xi = 48)
        /* compiled from: UnfoldSharedComponent.kt */
        public static final class DefaultImpls {
            public static /* synthetic */ UnfoldSharedComponent create$default(Factory factory, Context context, UnfoldTransitionConfig unfoldTransitionConfig, ScreenStatusProvider screenStatusProvider, DeviceStateManager deviceStateManager, ActivityManager activityManager, SensorManager sensorManager, Handler handler, Executor executor, Executor executor2, String str, ContentResolver contentResolver, int i, Object obj) {
                ContentResolver contentResolver2;
                if (obj == null) {
                    if ((i & 1024) != 0) {
                        ContentResolver contentResolver3 = context.getContentResolver();
                        Intrinsics.checkNotNullExpressionValue(contentResolver3, "fun create(\n            … ): UnfoldSharedComponent");
                        contentResolver2 = contentResolver3;
                    } else {
                        contentResolver2 = contentResolver;
                    }
                    return factory.create(context, unfoldTransitionConfig, screenStatusProvider, deviceStateManager, activityManager, sensorManager, handler, executor, executor2, str, contentResolver2);
                }
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: create");
            }
        }
    }
}
