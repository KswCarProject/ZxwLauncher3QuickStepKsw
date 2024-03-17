package com.android.quickstep;

import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.model.WellbeingModel;
import com.android.launcher3.popup.SystemShortcut;
import com.android.quickstep.views.TaskView;

/* renamed from: com.android.quickstep.-$$Lambda$TaskShortcutFactory$dbnp3P629cNDOg0riBlvYCie_Yk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TaskShortcutFactory$dbnp3P629cNDOg0riBlvYCie_Yk implements TaskShortcutFactory {
    public static final /* synthetic */ $$Lambda$TaskShortcutFactory$dbnp3P629cNDOg0riBlvYCie_Yk INSTANCE = new $$Lambda$TaskShortcutFactory$dbnp3P629cNDOg0riBlvYCie_Yk();

    private /* synthetic */ $$Lambda$TaskShortcutFactory$dbnp3P629cNDOg0riBlvYCie_Yk() {
    }

    public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        return WellbeingModel.SHORTCUT_FACTORY.getShortcut(baseDraggingActivity, taskIdAttributeContainer.getItemInfo(), taskIdAttributeContainer.getTaskView());
    }
}
