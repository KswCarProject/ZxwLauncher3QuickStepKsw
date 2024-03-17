package com.android.launcher3.uioverrides;

import android.util.FloatProperty;
import android.util.Log;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.views.RecentsView;

public abstract class BaseRecentsViewStateController<T extends RecentsView> implements StateManager.StateHandler<LauncherState> {
    protected final BaseQuickstepLauncher mLauncher;
    protected final T mRecentsView;

    /* access modifiers changed from: package-private */
    public abstract FloatProperty getContentAlphaProperty();

    /* access modifiers changed from: package-private */
    public abstract FloatProperty getTaskModalnessProperty();

    public BaseRecentsViewStateController(BaseQuickstepLauncher baseQuickstepLauncher) {
        this.mLauncher = baseQuickstepLauncher;
        this.mRecentsView = (RecentsView) baseQuickstepLauncher.getOverviewPanel();
    }

    public void setState(LauncherState launcherState) {
        float[] overviewScaleAndOffset = launcherState.getOverviewScaleAndOffset(this.mLauncher);
        RecentsView.RECENTS_SCALE_PROPERTY.set(this.mRecentsView, Float.valueOf(overviewScaleAndOffset[0]));
        RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET.set(this.mRecentsView, Float.valueOf(overviewScaleAndOffset[1]));
        float f = 0.0f;
        RecentsView.TASK_SECONDARY_TRANSLATION.set(this.mRecentsView, Float.valueOf(0.0f));
        float f2 = launcherState.overviewUi ? 1.0f : 0.0f;
        Log.d(TestProtocol.BAD_STATE, "BaseRecentsViewStateController setState state=" + launcherState + ", alpha=" + f2);
        getContentAlphaProperty().set(this.mRecentsView, Float.valueOf(f2));
        getTaskModalnessProperty().set(this.mRecentsView, Float.valueOf(launcherState.getOverviewModalness()));
        FloatProperty<RecentsView> floatProperty = RecentsView.RECENTS_GRID_PROGRESS;
        T t = this.mRecentsView;
        if (launcherState.displayOverviewTasksAsGrid(this.mLauncher.getDeviceProfile())) {
            f = 1.0f;
        }
        floatProperty.set(t, Float.valueOf(f));
    }

    public void setStateWithAnimation(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        Log.d(TestProtocol.BAD_STATE, "BaseRecentsViewStateController setStateWithAnimation state=" + launcherState + ", config.skipOverview=" + stateAnimationConfig.hasAnimationFlag(2));
        if (!stateAnimationConfig.hasAnimationFlag(2)) {
            setStateWithAnimationInternal(launcherState, stateAnimationConfig, pendingAnimation);
        }
    }

    /* access modifiers changed from: package-private */
    public void setStateWithAnimationInternal(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        float[] overviewScaleAndOffset = launcherState.getOverviewScaleAndOffset(this.mLauncher);
        pendingAnimation.setFloat(this.mRecentsView, RecentsView.RECENTS_SCALE_PROPERTY, overviewScaleAndOffset[0], stateAnimationConfig.getInterpolator(6, Interpolators.LINEAR));
        pendingAnimation.setFloat(this.mRecentsView, RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET, overviewScaleAndOffset[1], stateAnimationConfig.getInterpolator(7, Interpolators.LINEAR));
        float f = 0.0f;
        pendingAnimation.setFloat(this.mRecentsView, RecentsView.TASK_SECONDARY_TRANSLATION, 0.0f, stateAnimationConfig.getInterpolator(8, Interpolators.LINEAR));
        float f2 = launcherState.overviewUi ? 1.0f : 0.0f;
        Log.d(TestProtocol.BAD_STATE, "BaseRecentsViewStateController setStateWithAnimationInternal toState=" + launcherState + ", alpha=" + f2);
        pendingAnimation.setFloat(this.mRecentsView, getContentAlphaProperty(), f2, stateAnimationConfig.getInterpolator(9, Interpolators.AGGRESSIVE_EASE_IN_OUT));
        pendingAnimation.setFloat(this.mRecentsView, getTaskModalnessProperty(), launcherState.getOverviewModalness(), stateAnimationConfig.getInterpolator(12, Interpolators.LINEAR));
        boolean displayOverviewTasksAsGrid = launcherState.displayOverviewTasksAsGrid(this.mLauncher.getDeviceProfile());
        T t = this.mRecentsView;
        FloatProperty<RecentsView> floatProperty = RecentsView.RECENTS_GRID_PROGRESS;
        if (displayOverviewTasksAsGrid) {
            f = 1.0f;
        }
        pendingAnimation.setFloat(t, floatProperty, f, displayOverviewTasksAsGrid ? Interpolators.INSTANT : Interpolators.FINAL_FRAME);
    }
}
