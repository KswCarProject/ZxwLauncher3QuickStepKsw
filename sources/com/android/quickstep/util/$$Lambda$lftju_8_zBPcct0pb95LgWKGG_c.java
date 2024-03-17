package com.android.quickstep.util;

import android.content.Context;
import com.android.launcher3.util.MainThreadInitializedObject;

/* renamed from: com.android.quickstep.util.-$$Lambda$lftju_8_zBPcct0pb95LgWKGG_c  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$lftju_8_zBPcct0pb95LgWKGG_c implements MainThreadInitializedObject.ObjectProvider {
    public static final /* synthetic */ $$Lambda$lftju_8_zBPcct0pb95LgWKGG_c INSTANCE = new $$Lambda$lftju_8_zBPcct0pb95LgWKGG_c();

    private /* synthetic */ $$Lambda$lftju_8_zBPcct0pb95LgWKGG_c() {
    }

    public final Object get(Context context) {
        return new VibratorWrapper(context);
    }
}
