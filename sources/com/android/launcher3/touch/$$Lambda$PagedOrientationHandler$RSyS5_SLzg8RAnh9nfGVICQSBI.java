package com.android.launcher3.touch;

import android.view.View;
import com.android.launcher3.touch.PagedOrientationHandler;

/* renamed from: com.android.launcher3.touch.-$$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVI-CQSBI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVICQSBI implements PagedOrientationHandler.Int2DAction {
    public static final /* synthetic */ $$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVICQSBI INSTANCE = new $$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVICQSBI();

    private /* synthetic */ $$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVICQSBI() {
    }

    public final void call(Object obj, int i, int i2) {
        ((View) obj).scrollTo(i, i2);
    }
}
