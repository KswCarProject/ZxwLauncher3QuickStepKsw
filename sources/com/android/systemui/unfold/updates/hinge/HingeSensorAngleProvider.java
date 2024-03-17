package com.android.systemui.unfold.updates.hinge;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Trace;
import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001:\u0001\u0013B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0016\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016J\u0016\u0010\u0010\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016J\b\u0010\u0011\u001a\u00020\u000eH\u0016J\b\u0010\u0012\u001a\u00020\u000eH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bX\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u000b\u001a\u00060\fR\u00020\u0000X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider;", "Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "sensorManager", "Landroid/hardware/SensorManager;", "executor", "Ljava/util/concurrent/Executor;", "(Landroid/hardware/SensorManager;Ljava/util/concurrent/Executor;)V", "listeners", "", "Landroidx/core/util/Consumer;", "", "sensorListener", "Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider$HingeAngleSensorListener;", "addCallback", "", "listener", "removeCallback", "start", "stop", "HingeAngleSensorListener", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: HingeSensorAngleProvider.kt */
public final class HingeSensorAngleProvider implements HingeAngleProvider {
    private final Executor executor;
    /* access modifiers changed from: private */
    public final List<Consumer<Float>> listeners = new ArrayList();
    private final HingeAngleSensorListener sensorListener = new HingeAngleSensorListener(this);
    private final SensorManager sensorManager;

    public HingeSensorAngleProvider(SensorManager sensorManager2, Executor executor2) {
        Intrinsics.checkNotNullParameter(sensorManager2, "sensorManager");
        Intrinsics.checkNotNullParameter(executor2, "executor");
        this.sensorManager = sensorManager2;
        this.executor = executor2;
    }

    public void start() {
        this.executor.execute(new Runnable() {
            public final void run() {
                HingeSensorAngleProvider.m126start$lambda0(HingeSensorAngleProvider.this);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: start$lambda-0  reason: not valid java name */
    public static final void m126start$lambda0(HingeSensorAngleProvider hingeSensorAngleProvider) {
        Intrinsics.checkNotNullParameter(hingeSensorAngleProvider, "this$0");
        Trace.beginSection("HingeSensorAngleProvider#start");
        hingeSensorAngleProvider.sensorManager.registerListener(hingeSensorAngleProvider.sensorListener, hingeSensorAngleProvider.sensorManager.getDefaultSensor(36), 0);
        Trace.endSection();
    }

    public void stop() {
        this.executor.execute(new Runnable() {
            public final void run() {
                HingeSensorAngleProvider.m127stop$lambda1(HingeSensorAngleProvider.this);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: stop$lambda-1  reason: not valid java name */
    public static final void m127stop$lambda1(HingeSensorAngleProvider hingeSensorAngleProvider) {
        Intrinsics.checkNotNullParameter(hingeSensorAngleProvider, "this$0");
        hingeSensorAngleProvider.sensorManager.unregisterListener(hingeSensorAngleProvider.sensorListener);
    }

    public void removeCallback(Consumer<Float> consumer) {
        Intrinsics.checkNotNullParameter(consumer, "listener");
        this.listeners.remove(consumer);
    }

    public void addCallback(Consumer<Float> consumer) {
        Intrinsics.checkNotNullParameter(consumer, "listener");
        this.listeners.add(consumer);
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0004\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000bH\u0016¨\u0006\f"}, d2 = {"Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider$HingeAngleSensorListener;", "Landroid/hardware/SensorEventListener;", "(Lcom/android/systemui/unfold/updates/hinge/HingeSensorAngleProvider;)V", "onAccuracyChanged", "", "sensor", "Landroid/hardware/Sensor;", "accuracy", "", "onSensorChanged", "event", "Landroid/hardware/SensorEvent;", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    /* compiled from: HingeSensorAngleProvider.kt */
    private final class HingeAngleSensorListener implements SensorEventListener {
        final /* synthetic */ HingeSensorAngleProvider this$0;

        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public HingeAngleSensorListener(HingeSensorAngleProvider hingeSensorAngleProvider) {
            Intrinsics.checkNotNullParameter(hingeSensorAngleProvider, "this$0");
            this.this$0 = hingeSensorAngleProvider;
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            Intrinsics.checkNotNullParameter(sensorEvent, NotificationCompat.CATEGORY_EVENT);
            for (Consumer accept : this.this$0.listeners) {
                accept.accept(Float.valueOf(sensorEvent.values[0]));
            }
        }
    }
}
