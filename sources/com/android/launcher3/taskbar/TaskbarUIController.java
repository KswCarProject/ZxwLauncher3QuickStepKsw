package com.android.launcher3.taskbar;

import android.view.View;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import java.io.PrintWriter;
import java.util.stream.Stream;

public class TaskbarUIController {
    public static final TaskbarUIController DEFAULT = new TaskbarUIController();
    protected TaskbarControllers mControllers;

    /* access modifiers changed from: protected */
    public boolean isTaskbarTouchable() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onStashedInAppChanged() {
    }

    public void onTaskbarIconLaunched(ItemInfo itemInfo) {
    }

    /* access modifiers changed from: protected */
    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mControllers = null;
    }

    public boolean supportsVisualStashing() {
        TaskbarControllers taskbarControllers = this.mControllers;
        if (taskbarControllers == null) {
            return false;
        }
        return !taskbarControllers.taskbarActivityContext.isThreeButtonNav();
    }

    public Stream<ItemInfoWithIcon> getAppIconsForEdu() {
        return Stream.empty();
    }

    public View getRootView() {
        return this.mControllers.taskbarActivityContext.getDragLayer();
    }

    public void setSystemGestureInProgress(boolean z) {
        this.mControllers.taskbarStashController.setSystemGestureInProgress(z);
    }

    public void hideAllApps() {
        this.mControllers.taskbarAllAppsController.hide();
    }

    public void onExpandPip() {
        TaskbarControllers taskbarControllers = this.mControllers;
        if (taskbarControllers != null) {
            TaskbarStashController taskbarStashController = taskbarControllers.taskbarStashController;
            taskbarStashController.updateStateForFlag(1, true);
            taskbarStashController.applyState();
        }
    }

    /* access modifiers changed from: protected */
    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(String.format("%sTaskbarUIController: using an instance of %s", new Object[]{str, getClass().getSimpleName()}));
    }
}
