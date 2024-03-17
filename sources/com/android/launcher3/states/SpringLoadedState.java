package com.android.launcher3.states;

import android.content.Context;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragView;

public class SpringLoadedState extends LauncherState {
    private static final int STATE_FLAGS = (((((FLAG_MULTI_PAGE | FLAG_WORKSPACE_INACCESSIBLE) | 2) | FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED) | FLAG_WORKSPACE_HAS_BACKGROUNDS) | FLAG_HIDE_BACK_BUTTON);

    /* access modifiers changed from: protected */
    public float getDepthUnchecked(Context context) {
        return 0.5f;
    }

    public int getTransitionDuration(Context context, boolean z) {
        return DragView.VIEW_ZOOM_DURATION;
    }

    public float getWorkspaceBackgroundAlpha(Launcher launcher) {
        return 0.2f;
    }

    public SpringLoadedState(int i) {
        super(i, 2, STATE_FLAGS);
    }

    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        Workspace<?> workspace = launcher.getWorkspace();
        if (workspace.getChildCount() == 0) {
            return super.getWorkspaceScaleAndTranslation(launcher);
        }
        float cellLayoutSpringLoadShrunkTop = deviceProfile.getCellLayoutSpringLoadShrunkTop();
        float workspaceSpringLoadScale = deviceProfile.getWorkspaceSpringLoadScale();
        float height = (float) (workspace.getHeight() / 2);
        return new LauncherState.ScaleAndTranslation(workspaceSpringLoadScale, 0.0f, (cellLayoutSpringLoadShrunkTop - ((((float) workspace.getTop()) + height) - ((height - ((float) workspace.getChildAt(0).getTop())) * workspaceSpringLoadScale))) / workspaceSpringLoadScale);
    }

    public LauncherState.ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(1.0f, 0.0f, 0.0f);
    }
}
