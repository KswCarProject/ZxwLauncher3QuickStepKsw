package com.android.launcher3.uioverrides.states;

import android.view.animation.Interpolator;
import com.android.launcher3.anim.Interpolators;

/* renamed from: com.android.launcher3.uioverrides.states.-$$Lambda$QuickstepAtomicAnimationFactory$ohSF7dIy6KDYmDrCqxuPpm4zo7Y  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$QuickstepAtomicAnimationFactory$ohSF7dIy6KDYmDrCqxuPpm4zo7Y implements Interpolator {
    public static final /* synthetic */ $$Lambda$QuickstepAtomicAnimationFactory$ohSF7dIy6KDYmDrCqxuPpm4zo7Y INSTANCE = new $$Lambda$QuickstepAtomicAnimationFactory$ohSF7dIy6KDYmDrCqxuPpm4zo7Y();

    private /* synthetic */ $$Lambda$QuickstepAtomicAnimationFactory$ohSF7dIy6KDYmDrCqxuPpm4zo7Y() {
    }

    public final float getInterpolation(float f) {
        return Math.min(1.0f, Interpolators.OVERSHOOT_1_2.getInterpolation(f));
    }
}
