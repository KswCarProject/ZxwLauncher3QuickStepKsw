package com.android.quickstep.fallback;

import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.uioverrides.touchcontrollers.TaskViewTouchController;
import com.android.quickstep.RecentsActivity;

public class RecentsTaskController extends TaskViewTouchController<RecentsActivity> {
    /* access modifiers changed from: protected */
    public boolean isRecentsModal() {
        return false;
    }

    public RecentsTaskController(RecentsActivity recentsActivity) {
        super(recentsActivity);
    }

    /* access modifiers changed from: protected */
    public boolean isRecentsInteractive() {
        return ((RecentsActivity) this.mActivity).hasWindowFocus() || (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && ((RecentsActivity) this.mActivity).getStateManager().getState().hasLiveTile());
    }
}
