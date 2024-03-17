package com.android.launcher3.popup;

import android.content.Context;
import android.view.View;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;

/* renamed from: com.android.launcher3.popup.-$$Lambda$SystemShortcut$r7uy1cifcM4uPvUlcixhJHkL_34  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SystemShortcut$r7uy1cifcM4uPvUlcixhJHkL_34 implements SystemShortcut.Factory {
    public static final /* synthetic */ $$Lambda$SystemShortcut$r7uy1cifcM4uPvUlcixhJHkL_34 INSTANCE = new $$Lambda$SystemShortcut$r7uy1cifcM4uPvUlcixhJHkL_34();

    private /* synthetic */ $$Lambda$SystemShortcut$r7uy1cifcM4uPvUlcixhJHkL_34() {
    }

    public final SystemShortcut getShortcut(Context context, ItemInfo itemInfo, View view) {
        return SystemShortcut.lambda$static$1((BaseDraggingActivity) context, itemInfo, view);
    }
}
