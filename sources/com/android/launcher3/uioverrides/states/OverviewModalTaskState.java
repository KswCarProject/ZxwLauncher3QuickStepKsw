package com.android.launcher3.uioverrides.states;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.quickstep.views.RecentsView;

public class OverviewModalTaskState extends OverviewState {
    private static final int STATE_FLAGS = ((FLAG_OVERVIEW_UI | 2) | FLAG_WORKSPACE_INACCESSIBLE);

    public float getOverviewModalness() {
        return 1.0f;
    }

    public int getTransitionDuration(Context context, boolean z) {
        return 300;
    }

    public int getVisibleElements(Launcher launcher) {
        return 24;
    }

    public OverviewModalTaskState(int i) {
        super(i, 3, STATE_FLAGS);
    }

    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return getOverviewScaleAndOffsetForModalState(launcher);
    }

    public void onBackPressed(Launcher launcher) {
        launcher.getStateManager().goToState(LauncherState.OVERVIEW);
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        if (recentsView != null) {
            recentsView.resetModalVisuals();
        } else {
            super.onBackPressed(launcher);
        }
    }

    public static float[] getOverviewScaleAndOffsetForModalState(BaseDraggingActivity baseDraggingActivity) {
        Point selectedTaskSize = ((RecentsView) baseDraggingActivity.getOverviewPanel()).getSelectedTaskSize();
        Rect rect = new Rect();
        ((RecentsView) baseDraggingActivity.getOverviewPanel()).getModalTaskSize(rect);
        return new float[]{Math.min(((float) rect.height()) / ((float) selectedTaskSize.y), ((float) rect.width()) / ((float) selectedTaskSize.x)), 0.0f};
    }
}
