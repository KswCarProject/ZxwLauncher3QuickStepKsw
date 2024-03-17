package com.android.launcher3.testing;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import java.util.function.Function;

/* renamed from: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$ykHx1fS8Ino8ygoFttBzzt0vYAk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TestInformationHandler$ykHx1fS8Ino8ygoFttBzzt0vYAk implements Function {
    public static final /* synthetic */ $$Lambda$TestInformationHandler$ykHx1fS8Ino8ygoFttBzzt0vYAk INSTANCE = new $$Lambda$TestInformationHandler$ykHx1fS8Ino8ygoFttBzzt0vYAk();

    private /* synthetic */ $$Lambda$TestInformationHandler$ykHx1fS8Ino8ygoFttBzzt0vYAk() {
    }

    public final Object apply(Object obj) {
        return Integer.valueOf((int) (((Launcher) obj).getAllAppsController().getShiftRange() * (LauncherState.NORMAL.getVerticalProgress((Launcher) obj) - LauncherState.ALL_APPS.getVerticalProgress((Launcher) obj))));
    }
}
