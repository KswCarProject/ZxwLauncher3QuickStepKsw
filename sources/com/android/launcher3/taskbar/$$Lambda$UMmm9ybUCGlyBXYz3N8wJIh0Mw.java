package com.android.launcher3.taskbar;

import android.content.Context;
import android.view.View;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;

/* renamed from: com.android.launcher3.taskbar.-$$Lambda$UMmm9ybUCGlyBX-Yz3N8wJIh0Mw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$UMmm9ybUCGlyBXYz3N8wJIh0Mw implements SystemShortcut.Factory {
    public static final /* synthetic */ $$Lambda$UMmm9ybUCGlyBXYz3N8wJIh0Mw INSTANCE = new $$Lambda$UMmm9ybUCGlyBXYz3N8wJIh0Mw();

    private /* synthetic */ $$Lambda$UMmm9ybUCGlyBXYz3N8wJIh0Mw() {
    }

    public final SystemShortcut getShortcut(Context context, ItemInfo itemInfo, View view) {
        return new SystemShortcut.AppInfo((BaseTaskbarContext) context, itemInfo, view);
    }
}
