package com.android.launcher3.taskbar;

import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.quickstep.SystemUiProxy;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.StringJoiner;

public class TaskbarAutohideSuspendController implements TaskbarControllers.LoggableTaskbarController {
    public static final int FLAG_AUTOHIDE_SUSPEND_DRAGGING = 2;
    public static final int FLAG_AUTOHIDE_SUSPEND_FULLSCREEN = 1;
    private int mAutohideSuspendFlags = 0;
    private final SystemUiProxy mSystemUiProxy;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AutohideSuspendFlag {
    }

    public TaskbarAutohideSuspendController(TaskbarActivityContext taskbarActivityContext) {
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(taskbarActivityContext);
    }

    public void onDestroy() {
        this.mSystemUiProxy.notifyTaskbarAutohideSuspend(false);
    }

    public void updateFlag(int i, boolean z) {
        if (z) {
            this.mAutohideSuspendFlags = i | this.mAutohideSuspendFlags;
        } else {
            this.mAutohideSuspendFlags = (~i) & this.mAutohideSuspendFlags;
        }
        this.mSystemUiProxy.notifyTaskbarAutohideSuspend(this.mAutohideSuspendFlags != 0);
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarAutohideSuspendController:");
        printWriter.println(String.format("%s\tmAutohideSuspendFlags=%s", new Object[]{str, getStateString(this.mAutohideSuspendFlags)}));
    }

    private static String getStateString(int i) {
        StringJoiner stringJoiner = new StringJoiner("|");
        Utilities.appendFlag(stringJoiner, i, 1, "FLAG_AUTOHIDE_SUSPEND_FULLSCREEN");
        Utilities.appendFlag(stringJoiner, i, 2, "FLAG_AUTOHIDE_SUSPEND_DRAGGING");
        return stringJoiner.toString();
    }
}
