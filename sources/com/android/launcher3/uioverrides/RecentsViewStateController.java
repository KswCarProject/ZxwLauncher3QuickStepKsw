package com.android.launcher3.uioverrides;

import android.util.FloatProperty;
import android.util.Pair;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.quickstep.views.ClearAllButton;
import com.android.quickstep.views.LauncherRecentsView;
import com.android.quickstep.views.RecentsView;
import java.util.Objects;

public final class RecentsViewStateController extends BaseRecentsViewStateController<LauncherRecentsView> {
    public RecentsViewStateController(BaseQuickstepLauncher baseQuickstepLauncher) {
        super(baseQuickstepLauncher);
    }

    public void setState(LauncherState launcherState) {
        super.setState(launcherState);
        if (launcherState.overviewUi) {
            ((LauncherRecentsView) this.mRecentsView).updateEmptyMessage();
            ((LauncherRecentsView) this.mRecentsView).resetTaskVisuals();
        }
        setAlphas(PropertySetter.NO_ANIM_PROPERTY_SETTER, new StateAnimationConfig(), launcherState);
        ((LauncherRecentsView) this.mRecentsView).setFullscreenProgress(launcherState.getOverviewFullscreenProgress());
        this.mLauncher.getDepthController().setHasContentBehindLauncher(launcherState.overviewUi);
        handleSplitSelectionState(launcherState, (PendingAnimation) null);
    }

    /* access modifiers changed from: package-private */
    public void setStateWithAnimationInternal(LauncherState launcherState, StateAnimationConfig stateAnimationConfig, PendingAnimation pendingAnimation) {
        super.setStateWithAnimationInternal(launcherState, stateAnimationConfig, pendingAnimation);
        if (launcherState.overviewUi) {
            pendingAnimation.addOnFrameCallback(new Runnable() {
                public final void run() {
                    RecentsViewStateController.this.lambda$setStateWithAnimationInternal$0$RecentsViewStateController();
                }
            });
            ((LauncherRecentsView) this.mRecentsView).updateEmptyMessage();
        } else {
            LauncherRecentsView launcherRecentsView = (LauncherRecentsView) this.mRecentsView;
            Objects.requireNonNull(launcherRecentsView);
            pendingAnimation.addListener(AnimatorListeners.forSuccessCallback(new Runnable() {
                public final void run() {
                    LauncherRecentsView.this.resetTaskVisuals();
                }
            }));
        }
        pendingAnimation.addListener(AnimatorListeners.forSuccessCallback(new Runnable(launcherState) {
            public final /* synthetic */ LauncherState f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RecentsViewStateController.this.lambda$setStateWithAnimationInternal$1$RecentsViewStateController(this.f$1);
            }
        }));
        handleSplitSelectionState(launcherState, pendingAnimation);
        setAlphas(pendingAnimation, stateAnimationConfig, launcherState);
        pendingAnimation.setFloat(this.mRecentsView, RecentsView.FULLSCREEN_PROGRESS, launcherState.getOverviewFullscreenProgress(), Interpolators.LINEAR);
    }

    public /* synthetic */ void lambda$setStateWithAnimationInternal$0$RecentsViewStateController() {
        ((LauncherRecentsView) this.mRecentsView).loadVisibleTaskData(3);
    }

    public /* synthetic */ void lambda$setStateWithAnimationInternal$1$RecentsViewStateController(LauncherState launcherState) {
        this.mLauncher.getDepthController().setHasContentBehindLauncher(launcherState.overviewUi);
    }

    private void handleSplitSelectionState(LauncherState launcherState, PendingAnimation pendingAnimation) {
        boolean z = pendingAnimation != null;
        Pair<FloatProperty, FloatProperty> splitSelectTaskOffset = ((RecentsView) this.mLauncher.getOverviewPanel()).getPagedOrientationHandler().getSplitSelectTaskOffset(RecentsView.TASK_PRIMARY_SPLIT_TRANSLATION, RecentsView.TASK_SECONDARY_SPLIT_TRANSLATION, this.mLauncher.getDeviceProfile());
        if (launcherState == LauncherState.OVERVIEW_SPLIT_SELECT) {
            PendingAnimation createSplitSelectInitAnimation = ((LauncherRecentsView) this.mRecentsView).createSplitSelectInitAnimation(launcherState.getTransitionDuration(this.mLauncher, true));
            createSplitSelectInitAnimation.setFloat((LauncherRecentsView) this.mRecentsView, (FloatProperty) splitSelectTaskOffset.first, launcherState.getSplitSelectTranslation(this.mLauncher), Interpolators.LINEAR);
            createSplitSelectInitAnimation.setFloat((LauncherRecentsView) this.mRecentsView, (FloatProperty) splitSelectTaskOffset.second, 0.0f, Interpolators.LINEAR);
            if (!z) {
                createSplitSelectInitAnimation.buildAnim().start();
            } else {
                pendingAnimation.add(createSplitSelectInitAnimation.buildAnim());
            }
            ((LauncherRecentsView) this.mRecentsView).applySplitPrimaryScrollOffset();
            return;
        }
        ((LauncherRecentsView) this.mRecentsView).resetSplitPrimaryScrollOffset();
    }

    private void setAlphas(PropertySetter propertySetter, StateAnimationConfig stateAnimationConfig, LauncherState launcherState) {
        float f = 1.0f;
        propertySetter.setFloat(((LauncherRecentsView) this.mRecentsView).getClearAllButton(), ClearAllButton.VISIBILITY_ALPHA, launcherState.areElementsVisible(this.mLauncher, 16) ? 1.0f : 0.0f, Interpolators.LINEAR);
        if (!launcherState.areElementsVisible(this.mLauncher, 8)) {
            f = 0.0f;
        }
        propertySetter.setFloat(this.mLauncher.getActionsView().getVisibilityAlpha(), MultiValueAlpha.VALUE, f, stateAnimationConfig.getInterpolator(14, Interpolators.LINEAR));
    }

    /* access modifiers changed from: package-private */
    public FloatProperty<RecentsView> getTaskModalnessProperty() {
        return RecentsView.TASK_MODALNESS;
    }

    /* access modifiers changed from: package-private */
    public FloatProperty<RecentsView> getContentAlphaProperty() {
        return RecentsView.CONTENT_ALPHA;
    }
}
