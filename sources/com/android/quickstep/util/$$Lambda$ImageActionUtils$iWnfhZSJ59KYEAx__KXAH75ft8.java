package com.android.quickstep.util;

import android.content.Intent;
import android.net.Uri;
import java.util.function.BiFunction;

/* renamed from: com.android.quickstep.util.-$$Lambda$ImageActionUtils$iWnfhZSJ59KYE-Ax__KXAH75ft8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8 implements BiFunction {
    public static final /* synthetic */ $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8 INSTANCE = new $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8();

    private /* synthetic */ $$Lambda$ImageActionUtils$iWnfhZSJ59KYEAx__KXAH75ft8() {
    }

    public final Object apply(Object obj, Object obj2) {
        return ImageActionUtils.getShareIntentForImageUri((Uri) obj, (Intent) obj2);
    }
}
