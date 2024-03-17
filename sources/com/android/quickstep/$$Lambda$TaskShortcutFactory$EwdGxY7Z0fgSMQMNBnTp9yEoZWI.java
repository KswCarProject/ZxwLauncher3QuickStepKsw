package com.android.quickstep;

import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.popup.SystemShortcut;
import com.android.quickstep.views.TaskView;

/* renamed from: com.android.quickstep.-$$Lambda$TaskShortcutFactory$EwdGxY7Z0fgSMQMNBnTp9yEoZWI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TaskShortcutFactory$EwdGxY7Z0fgSMQMNBnTp9yEoZWI implements TaskShortcutFactory {
    public static final /* synthetic */ $$Lambda$TaskShortcutFactory$EwdGxY7Z0fgSMQMNBnTp9yEoZWI INSTANCE = new $$Lambda$TaskShortcutFactory$EwdGxY7Z0fgSMQMNBnTp9yEoZWI();

    private /* synthetic */ $$Lambda$TaskShortcutFactory$EwdGxY7Z0fgSMQMNBnTp9yEoZWI() {
    }

    public final SystemShortcut getShortcut(BaseDraggingActivity baseDraggingActivity, TaskView.TaskIdAttributeContainer taskIdAttributeContainer) {
        return taskIdAttributeContainer.getThumbnailView().getTaskOverlay().getScreenshotShortcut(baseDraggingActivity, taskIdAttributeContainer.getItemInfo(), taskIdAttributeContainer.getTaskView());
    }
}
