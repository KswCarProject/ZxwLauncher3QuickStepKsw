package com.android.launcher3.popup;

import android.content.Context;
import android.view.View;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;

/* renamed from: com.android.launcher3.popup.-$$Lambda$bK5UtxJcJIlcZZiCp6isS3vGef4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$bK5UtxJcJIlcZZiCp6isS3vGef4 implements SystemShortcut.Factory {
    public static final /* synthetic */ $$Lambda$bK5UtxJcJIlcZZiCp6isS3vGef4 INSTANCE = new $$Lambda$bK5UtxJcJIlcZZiCp6isS3vGef4();

    private /* synthetic */ $$Lambda$bK5UtxJcJIlcZZiCp6isS3vGef4() {
    }

    public final SystemShortcut getShortcut(Context context, ItemInfo itemInfo, View view) {
        return new SystemShortcut.AppInfo((BaseDraggingActivity) context, itemInfo, view);
    }
}
