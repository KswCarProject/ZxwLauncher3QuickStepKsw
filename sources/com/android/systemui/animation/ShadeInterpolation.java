package com.android.systemui.animation;

import android.util.MathUtils;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;

@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0007J\u0010\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0007J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0002¨\u0006\b"}, d2 = {"Lcom/android/systemui/animation/ShadeInterpolation;", "", "()V", "getContentAlpha", "", "fraction", "getNotificationScrimAlpha", "interpolateEaseInOut", "SharedLibWrapper_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
/* compiled from: ShadeInterpolation.kt */
public final class ShadeInterpolation {
    public static final ShadeInterpolation INSTANCE = new ShadeInterpolation();

    private ShadeInterpolation() {
    }

    @JvmStatic
    public static final float getNotificationScrimAlpha(float f) {
        return INSTANCE.interpolateEaseInOut(MathUtils.constrainedMap(0.0f, 1.0f, 0.0f, 0.5f, f));
    }

    @JvmStatic
    public static final float getContentAlpha(float f) {
        return INSTANCE.interpolateEaseInOut(MathUtils.constrainedMap(0.0f, 1.0f, 0.3f, 1.0f, f));
    }

    private final float interpolateEaseInOut(float f) {
        float f2 = (f * 1.2f) - 0.2f;
        if (f2 <= 0.0f) {
            return 0.0f;
        }
        float f3 = 1.0f - f2;
        double d = (double) 1.0f;
        return (float) (d - (((double) 0.5f) * (d - Math.cos((double) ((3.14159f * f3) * f3)))));
    }
}
