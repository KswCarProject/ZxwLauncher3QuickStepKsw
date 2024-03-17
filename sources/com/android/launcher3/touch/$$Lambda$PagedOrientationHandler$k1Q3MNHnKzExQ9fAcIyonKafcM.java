package com.android.launcher3.touch;

import android.view.View;
import com.android.launcher3.touch.PagedOrientationHandler;

/* renamed from: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9f-AcIyonKafcM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9fAcIyonKafcM implements PagedOrientationHandler.Int2DAction {
    public static final /* synthetic */ $$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9fAcIyonKafcM INSTANCE = new $$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9fAcIyonKafcM();

    private /* synthetic */ $$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9fAcIyonKafcM() {
    }

    public final void call(Object obj, int i, int i2) {
        ((View) obj).scrollBy(i, i2);
    }
}
