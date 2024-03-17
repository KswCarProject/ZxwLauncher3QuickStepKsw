package com.android.quickstep;

import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.quickstep.util.TaskViewSimulator;

/* renamed from: com.android.quickstep.-$$Lambda$wF--NSFuZVSurigPf-4I9Zk2Qj8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$wFNSFuZVSurigPf4I9Zk2Qj8 implements PagedOrientationHandler.Int2DAction {
    public static final /* synthetic */ $$Lambda$wFNSFuZVSurigPf4I9Zk2Qj8 INSTANCE = new $$Lambda$wFNSFuZVSurigPf4I9Zk2Qj8();

    private /* synthetic */ $$Lambda$wFNSFuZVSurigPf4I9Zk2Qj8() {
    }

    public final void call(Object obj, int i, int i2) {
        ((TaskViewSimulator) obj).setTaskRectTranslation(i, i2);
    }
}
