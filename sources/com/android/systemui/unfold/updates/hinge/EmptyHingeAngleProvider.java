package com.android.systemui.unfold.updates.hinge;

import androidx.core.util.Consumer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016J\u0016\u0010\b\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0016J\b\u0010\t\u001a\u00020\u0004H\u0016J\b\u0010\n\u001a\u00020\u0004H\u0016¨\u0006\u000b"}, d2 = {"Lcom/android/systemui/unfold/updates/hinge/EmptyHingeAngleProvider;", "Lcom/android/systemui/unfold/updates/hinge/HingeAngleProvider;", "()V", "addCallback", "", "listener", "Landroidx/core/util/Consumer;", "", "removeCallback", "start", "stop", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: EmptyHingeAngleProvider.kt */
public final class EmptyHingeAngleProvider implements HingeAngleProvider {
    public static final EmptyHingeAngleProvider INSTANCE = new EmptyHingeAngleProvider();

    public void addCallback(Consumer<Float> consumer) {
        Intrinsics.checkNotNullParameter(consumer, "listener");
    }

    public void removeCallback(Consumer<Float> consumer) {
        Intrinsics.checkNotNullParameter(consumer, "listener");
    }

    public void start() {
    }

    public void stop() {
    }

    private EmptyHingeAngleProvider() {
    }
}
