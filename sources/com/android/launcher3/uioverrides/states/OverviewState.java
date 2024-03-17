package com.android.launcher3.uioverrides.states;

import android.content.Context;
import android.graphics.Rect;
import android.os.SystemProperties;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.taskbar.LauncherTaskbarUIController;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.Themes;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;

public class OverviewState extends LauncherState {
    private static final int STATE_FLAGS = ((((FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED | 2) | FLAG_OVERVIEW_UI) | FLAG_WORKSPACE_INACCESSIBLE) | FLAG_CLOSE_POPUPS);
    protected static final Rect sTempRect = new Rect();

    public int getVisibleElements(Launcher launcher) {
        return 24;
    }

    public OverviewState(int i) {
        this(i, STATE_FLAGS);
    }

    protected OverviewState(int i, int i2) {
        this(i, 3, i2);
    }

    protected OverviewState(int i, int i2, int i3) {
        super(i, i2, i3);
    }

    public int getTransitionDuration(Context context, boolean z) {
        return DisplayController.getNavigationMode(context).hasGestures ? 380 : 250;
    }

    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        Rect rect = sTempRect;
        ((RecentsView) launcher.getOverviewPanel()).getTaskSize(rect);
        return new LauncherState.ScaleAndTranslation(((float) rect.height()) / ((float) launcher.getDeviceProfile().getCellLayoutHeight()), 0.0f, (-getDefaultSwipeHeight(launcher)) * 0.5f);
    }

    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return new float[]{1.0f, 0.0f};
    }

    public LauncherState.PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        return new LauncherState.PageAlphaProvider(Interpolators.DEACCEL_2) {
            public float getPageAlpha(int i) {
                return 0.0f;
            }
        };
    }

    public boolean isTaskbarStashed(Launcher launcher) {
        if (!(launcher instanceof BaseQuickstepLauncher)) {
            return super.isTaskbarStashed(launcher);
        }
        LauncherTaskbarUIController taskbarUIController = ((BaseQuickstepLauncher) launcher).getTaskbarUIController();
        return taskbarUIController != null && taskbarUIController.supportsVisualStashing();
    }

    public int getWorkspaceScrimColor(Launcher launcher) {
        return Themes.getAttrColor(launcher, R.attr.overviewScrimColor);
    }

    public boolean displayOverviewTasksAsGrid(DeviceProfile deviceProfile) {
        return deviceProfile.isTablet;
    }

    public String getDescription(Launcher launcher) {
        return launcher.getString(R.string.accessibility_recent_apps);
    }

    public static float getDefaultSwipeHeight(Launcher launcher) {
        return LayoutUtils.getDefaultSwipeHeight(launcher, launcher.getDeviceProfile());
    }

    /* access modifiers changed from: protected */
    public float getDepthUnchecked(Context context) {
        return SystemProperties.getBoolean("ro.launcher.depth.overview", true) ? 1.0f : 0.0f;
    }

    public void onBackPressed(Launcher launcher) {
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        TaskView runningTaskView = recentsView.getRunningTaskView();
        if (runningTaskView == null) {
            super.onBackPressed(launcher);
        } else if (recentsView.isTaskViewFullyVisible(runningTaskView)) {
            runningTaskView.launchTasks();
        } else {
            recentsView.snapToPage(recentsView.indexOfChild(runningTaskView));
        }
    }

    public static OverviewState newBackgroundState(int i) {
        return new BackgroundAppState(i);
    }

    public static OverviewState newSwitchState(int i) {
        return new QuickSwitchState(i);
    }

    public static OverviewState newModalTaskState(int i) {
        return new OverviewModalTaskState(i);
    }

    public static OverviewState newSplitSelectState(int i) {
        return new SplitScreenSelectState(i);
    }
}
