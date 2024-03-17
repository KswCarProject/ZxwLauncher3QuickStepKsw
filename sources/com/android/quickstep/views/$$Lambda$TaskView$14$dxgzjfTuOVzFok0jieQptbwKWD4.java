package com.android.quickstep.views;

import com.android.quickstep.RemoteTargetGluer;
import java.util.function.Consumer;

/* renamed from: com.android.quickstep.views.-$$Lambda$TaskView$14$dxgzjfTuOVzFok0jieQptbwKWD4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TaskView$14$dxgzjfTuOVzFok0jieQptbwKWD4 implements Consumer {
    public static final /* synthetic */ $$Lambda$TaskView$14$dxgzjfTuOVzFok0jieQptbwKWD4 INSTANCE = new $$Lambda$TaskView$14$dxgzjfTuOVzFok0jieQptbwKWD4();

    private /* synthetic */ $$Lambda$TaskView$14$dxgzjfTuOVzFok0jieQptbwKWD4() {
    }

    public final void accept(Object obj) {
        ((RemoteTargetGluer.RemoteTargetHandle) obj).getTaskViewSimulator().setDrawsBelowRecents(false);
    }
}
