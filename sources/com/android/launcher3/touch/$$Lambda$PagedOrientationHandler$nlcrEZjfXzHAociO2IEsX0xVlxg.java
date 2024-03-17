package com.android.launcher3.touch;

import android.graphics.Matrix;
import com.android.launcher3.touch.PagedOrientationHandler;

/* renamed from: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg implements PagedOrientationHandler.Float2DAction {
    public static final /* synthetic */ $$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg INSTANCE = new $$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg();

    private /* synthetic */ $$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg() {
    }

    public final void call(Object obj, float f, float f2) {
        boolean unused = ((Matrix) obj).postTranslate(f, f2);
    }
}
