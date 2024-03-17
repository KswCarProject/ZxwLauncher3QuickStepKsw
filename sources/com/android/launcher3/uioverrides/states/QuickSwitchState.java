package com.android.launcher3.uioverrides.states;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.util.Themes;

public class QuickSwitchState extends BackgroundAppState {
    public float getVerticalProgress(Launcher launcher) {
        return 1.0f;
    }

    public int getVisibleElements(Launcher launcher) {
        return 0;
    }

    public boolean isTaskbarAlignedWithHotseat(Launcher launcher) {
        return false;
    }

    public QuickSwitchState(int i) {
        super(i, 1);
    }

    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(0.9f, 0.0f, (getVerticalProgress(launcher) - NORMAL.getVerticalProgress(launcher)) * launcher.getAllAppsController().getShiftRange());
    }

    public int getWorkspaceScrimColor(Launcher launcher) {
        if (launcher.getDeviceProfile().isTaskbarPresentInApps) {
            return launcher.getColor(R.color.taskbar_background);
        }
        return Themes.getAttrColor(launcher, R.attr.overviewScrimColor);
    }

    public boolean isTaskbarStashed(Launcher launcher) {
        return !launcher.getDeviceProfile().isTaskbarPresentInApps;
    }
}
