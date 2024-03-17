package com.android.launcher3.anim;

import android.view.animation.Interpolator;

/* renamed from: com.android.launcher3.anim.-$$Lambda$Interpolators$EOVZRYL3WTZQ1fJuYRDvkggS3PY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Interpolators$EOVZRYL3WTZQ1fJuYRDvkggS3PY implements Interpolator {
    public static final /* synthetic */ $$Lambda$Interpolators$EOVZRYL3WTZQ1fJuYRDvkggS3PY INSTANCE = new $$Lambda$Interpolators$EOVZRYL3WTZQ1fJuYRDvkggS3PY();

    private /* synthetic */ $$Lambda$Interpolators$EOVZRYL3WTZQ1fJuYRDvkggS3PY() {
    }

    public final float getInterpolation(float f) {
        return Interpolators.ACCEL_DEACCEL.getInterpolation(Interpolators.TOUCH_RESPONSE_INTERPOLATOR.getInterpolation(f));
    }
}
