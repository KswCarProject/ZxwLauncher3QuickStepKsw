package com.android.launcher3.uioverrides.states;

import android.content.Context;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.RecentsView;

public class BackgroundAppState extends OverviewState {
    private static final int STATE_FLAGS = ((((FLAG_OVERVIEW_UI | 2) | FLAG_WORKSPACE_INACCESSIBLE) | 1) | FLAG_CLOSE_POPUPS);

    public boolean displayOverviewTasksAsGrid(DeviceProfile deviceProfile) {
        return false;
    }

    /* access modifiers changed from: protected */
    public float getDepthUnchecked(Context context) {
        return 1.0f;
    }

    public float getOverviewFullscreenProgress() {
        return 1.0f;
    }

    public BackgroundAppState(int i) {
        this(i, 1);
    }

    protected BackgroundAppState(int i, int i2) {
        super(i, i2, STATE_FLAGS);
    }

    public float getVerticalProgress(Launcher launcher) {
        if (launcher.getDeviceProfile().isVerticalBarLayout()) {
            return super.getVerticalProgress(launcher);
        }
        float shelfTrackingDistance = (float) LayoutUtils.getShelfTrackingDistance(launcher, launcher.getDeviceProfile(), ((RecentsView) launcher.getOverviewPanel()).getPagedOrientationHandler());
        return super.getVerticalProgress(launcher) + (shelfTrackingDistance / Math.max(launcher.getAllAppsController().getShiftRange(), 1.0f));
    }

    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return getOverviewScaleAndOffsetForBackgroundState(launcher);
    }

    public int getVisibleElements(Launcher launcher) {
        return super.getVisibleElements(launcher) & -9 & -17 & -5;
    }

    public int getWorkspaceScrimColor(Launcher launcher) {
        if (launcher.getDeviceProfile().isTaskbarPresentInApps) {
            return launcher.getColor(R.color.taskbar_background);
        }
        return 0;
    }

    public static float[] getOverviewScaleAndOffsetForBackgroundState(BaseDraggingActivity baseDraggingActivity) {
        return new float[]{((RecentsView) baseDraggingActivity.getOverviewPanel()).getMaxScaleForFullScreen(), 0.0f};
    }
}
