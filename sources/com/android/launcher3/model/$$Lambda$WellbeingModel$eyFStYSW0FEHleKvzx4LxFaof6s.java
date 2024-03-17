package com.android.launcher3.model;

import android.content.Context;
import android.view.View;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;

/* renamed from: com.android.launcher3.model.-$$Lambda$WellbeingModel$eyFStYSW0FEHleKvzx4LxFaof6s  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WellbeingModel$eyFStYSW0FEHleKvzx4LxFaof6s implements SystemShortcut.Factory {
    public static final /* synthetic */ $$Lambda$WellbeingModel$eyFStYSW0FEHleKvzx4LxFaof6s INSTANCE = new $$Lambda$WellbeingModel$eyFStYSW0FEHleKvzx4LxFaof6s();

    private /* synthetic */ $$Lambda$WellbeingModel$eyFStYSW0FEHleKvzx4LxFaof6s() {
    }

    public final SystemShortcut getShortcut(Context context, ItemInfo itemInfo, View view) {
        return WellbeingModel.lambda$static$4((BaseDraggingActivity) context, itemInfo, view);
    }
}
