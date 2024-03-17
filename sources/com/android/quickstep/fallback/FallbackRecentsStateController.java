package com.android.quickstep.fallback;

import android.util.FloatProperty;
import android.util.Pair;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.views.ClearAllButton;
import com.android.quickstep.views.RecentsView;

public class FallbackRecentsStateController implements StateManager.StateHandler<RecentsState> {
    private final RecentsActivity mActivity;
    private final StateAnimationConfig mNoConfig = new StateAnimationConfig();
    private final FallbackRecentsView mRecentsView;

    public FallbackRecentsStateController(RecentsActivity recentsActivity) {
        this.mActivity = recentsActivity;
        this.mRecentsView = (FallbackRecentsView) recentsActivity.getOverviewPanel();
    }

    public void setState(RecentsState recentsState) {
        this.mRecentsView.updateEmptyMessage();
        this.mRecentsView.resetTaskVisuals();
        setProperties(recentsState, this.mNoConfig, PropertySetter.NO_ANIM_PROPERTY_SETTER);
    }

    public void setStateWithAnimation(RecentsState recentsState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        if (!stateAnimationConfig.hasAnimationFlag(2)) {
            pendingAnimation.addOnFrameCallback(new Runnable() {
                public final void run() {
                    FallbackRecentsStateController.this.lambda$setStateWithAnimation$0$FallbackRecentsStateController();
                }
            });
            this.mRecentsView.updateEmptyMessage();
            setProperties(recentsState, stateAnimationConfig, pendingAnimation);
        }
    }

    public /* synthetic */ void lambda$setStateWithAnimation$0$FallbackRecentsStateController() {
        this.mRecentsView.loadVisibleTaskData(3);
    }

    private void setProperties(RecentsState recentsState, StateAnimationConfig stateAnimationConfig, PropertySetter propertySetter) {
        float f = 1.0f;
        propertySetter.setFloat(this.mRecentsView.getClearAllButton(), ClearAllButton.VISIBILITY_ALPHA, recentsState.hasClearAllButton() ? 1.0f : 0.0f, Interpolators.LINEAR);
        propertySetter.setFloat(this.mActivity.getActionsView().getVisibilityAlpha(), MultiValueAlpha.VALUE, recentsState.hasOverviewActions() ? 1.0f : 0.0f, Interpolators.LINEAR);
        float[] overviewScaleAndOffset = recentsState.getOverviewScaleAndOffset(this.mActivity);
        propertySetter.setFloat(this.mRecentsView, RecentsView.RECENTS_SCALE_PROPERTY, overviewScaleAndOffset[0], stateAnimationConfig.getInterpolator(6, Interpolators.LINEAR));
        propertySetter.setFloat(this.mRecentsView, RecentsView.ADJACENT_PAGE_HORIZONTAL_OFFSET, overviewScaleAndOffset[1], stateAnimationConfig.getInterpolator(7, Interpolators.LINEAR));
        propertySetter.setFloat(this.mRecentsView, RecentsView.TASK_SECONDARY_TRANSLATION, 0.0f, stateAnimationConfig.getInterpolator(8, Interpolators.LINEAR));
        propertySetter.setFloat(this.mRecentsView, RecentsView.TASK_MODALNESS, recentsState.getOverviewModalness(), stateAnimationConfig.getInterpolator(12, Interpolators.LINEAR));
        propertySetter.setFloat(this.mRecentsView, RecentsView.FULLSCREEN_PROGRESS, recentsState.isFullScreen() ? 1.0f : 0.0f, Interpolators.LINEAR);
        boolean displayOverviewTasksAsGrid = recentsState.displayOverviewTasksAsGrid(this.mActivity.getDeviceProfile());
        FallbackRecentsView fallbackRecentsView = this.mRecentsView;
        FloatProperty<RecentsView> floatProperty = RecentsView.RECENTS_GRID_PROGRESS;
        if (!displayOverviewTasksAsGrid) {
            f = 0.0f;
        }
        propertySetter.setFloat(fallbackRecentsView, floatProperty, f, displayOverviewTasksAsGrid ? Interpolators.INSTANT : Interpolators.FINAL_FRAME);
        propertySetter.setViewBackgroundColor(this.mActivity.getScrimView(), recentsState.getScrimColor(this.mActivity), stateAnimationConfig.getInterpolator(11, Interpolators.LINEAR));
        RecentsState state = this.mActivity.getStateManager().getState();
        if (isSplitSelectionState(recentsState) && !isSplitSelectionState(state)) {
            propertySetter.add(this.mRecentsView.createSplitSelectInitAnimation(recentsState.getTransitionDuration(this.mActivity, true)).buildAnim());
        }
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = this.mRecentsView.getPagedOrientationHandler().getSplitSelectTaskOffset(RecentsView.TASK_PRIMARY_SPLIT_TRANSLATION, RecentsView.TASK_SECONDARY_SPLIT_TRANSLATION, this.mActivity.getDeviceProfile());
        propertySetter.setFloat(this.mRecentsView, (FloatProperty) splitSelectTaskOffset.second, 0.0f, Interpolators.LINEAR);
        if (isSplitSelectionState(recentsState)) {
            this.mRecentsView.applySplitPrimaryScrollOffset();
            propertySetter.setFloat(this.mRecentsView, (FloatProperty) splitSelectTaskOffset.first, this.mRecentsView.getSplitSelectTranslation(), Interpolators.LINEAR);
            return;
        }
        this.mRecentsView.resetSplitPrimaryScrollOffset();
        propertySetter.setFloat(this.mRecentsView, (FloatProperty) splitSelectTaskOffset.first, 0.0f, Interpolators.LINEAR);
    }

    private boolean isSplitSelectionState(RecentsState recentsState) {
        return recentsState == RecentsState.OVERVIEW_SPLIT_SELECT;
    }
}
