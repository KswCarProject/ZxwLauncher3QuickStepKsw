package com.android.launcher3.qsb;

import android.os.Bundle;
import android.view.View;
import com.android.launcher3.Launcher;

/* renamed from: com.android.launcher3.qsb.-$$Lambda$QsbWidgetHostView$UOz71DN3v7EvlljLFBdDT0She50  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$QsbWidgetHostView$UOz71DN3v7EvlljLFBdDT0She50 implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$QsbWidgetHostView$UOz71DN3v7EvlljLFBdDT0She50 INSTANCE = new $$Lambda$QsbWidgetHostView$UOz71DN3v7EvlljLFBdDT0She50();

    private /* synthetic */ $$Lambda$QsbWidgetHostView$UOz71DN3v7EvlljLFBdDT0She50() {
    }

    public final void onClick(View view) {
        Launcher.getLauncher(view.getContext()).startSearch("", false, (Bundle) null, true);
    }
}
