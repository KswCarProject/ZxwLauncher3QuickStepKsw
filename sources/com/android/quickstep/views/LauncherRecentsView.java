package com.android.quickstep.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseQuickstepLauncher;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.LauncherActivityInterface;
import com.android.quickstep.util.SplitSelectStateController;

public class LauncherRecentsView extends RecentsView<BaseQuickstepLauncher, LauncherState> implements StateManager.StateListener<LauncherState> {
    public LauncherRecentsView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LauncherRecentsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LauncherRecentsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, LauncherActivityInterface.INSTANCE);
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().addStateListener(this);
    }

    public void init(OverviewActionsView overviewActionsView, SplitSelectStateController splitSelectStateController, View view) {
        super.init(overviewActionsView, splitSelectStateController, view);
        Log.d(TestProtocol.BAD_STATE, "LauncherRecentsView init setContentAlpha=0");
        setContentAlpha(0.0f);
    }

    public void startHome() {
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.NORMAL);
        AbstractFloatingView.closeAllOpenViews(this.mActivity, ((BaseQuickstepLauncher) this.mActivity).isStarted());
    }

    /* access modifiers changed from: protected */
    public void onTaskLaunchAnimationEnd(boolean z) {
        if (z) {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().moveToRestState();
        } else {
            ((BaseQuickstepLauncher) this.mActivity).getAllAppsController().setState(((BaseQuickstepLauncher) this.mActivity).getStateManager().getState());
        }
        super.onTaskLaunchAnimationEnd(z);
    }

    public void reset() {
        super.reset();
        setLayoutRotation(0, 0);
    }

    public void onStateTransitionStart(LauncherState launcherState) {
        setOverviewStateEnabled(launcherState.overviewUi);
        setOverviewGridEnabled(launcherState.displayOverviewTasksAsGrid(((BaseQuickstepLauncher) this.mActivity).getDeviceProfile()));
        setOverviewFullscreenEnabled(launcherState.getOverviewFullscreenProgress() == 1.0f);
        if (launcherState == LauncherState.OVERVIEW_MODAL_TASK) {
            setOverviewSelectEnabled(true);
        }
        Log.d(TestProtocol.BAD_STATE, "LRV onStateTransitionStart setFreezeVisibility=true, toState=" + launcherState);
        setFreezeViewVisibility(true);
    }

    public void onStateTransitionComplete(LauncherState launcherState) {
        if (launcherState == LauncherState.NORMAL || launcherState == LauncherState.SPRING_LOADED) {
            reset();
        }
        boolean z = launcherState == LauncherState.OVERVIEW || launcherState == LauncherState.OVERVIEW_MODAL_TASK;
        setOverlayEnabled(z);
        Log.d(TestProtocol.BAD_STATE, "LRV onStateTransitionComplete setFreezeVisibility=false, finalState=" + launcherState);
        setFreezeViewVisibility(false);
        if (launcherState != LauncherState.OVERVIEW_MODAL_TASK) {
            setOverviewSelectEnabled(false);
        }
        if (z) {
            runActionOnRemoteHandles($$Lambda$LauncherRecentsView$IvPxBSc30jFgZEO1ihSvL0rjB8U.INSTANCE);
        }
    }

    public void setOverviewStateEnabled(boolean z) {
        super.setOverviewStateEnabled(z);
        if (z) {
            setDisallowScrollToClearAll(!((((BaseQuickstepLauncher) this.mActivity).getStateManager().getState().getVisibleElements((Launcher) this.mActivity) & 16) != 0));
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent) || ((BaseQuickstepLauncher) this.mActivity).getStateManager().getState().overviewUi;
    }

    /* access modifiers changed from: protected */
    public DepthController getDepthController() {
        return ((BaseQuickstepLauncher) this.mActivity).getDepthController();
    }

    public void setModalStateEnabled(boolean z) {
        super.setModalStateEnabled(z);
        if (z) {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW_MODAL_TASK);
        } else if (((BaseQuickstepLauncher) this.mActivity).isInState(LauncherState.OVERVIEW_MODAL_TASK)) {
            ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW);
            resetModalVisuals();
        }
    }

    /* access modifiers changed from: protected */
    public void onDismissAnimationEnds() {
        super.onDismissAnimationEnds();
        if (((BaseQuickstepLauncher) this.mActivity).isInState(LauncherState.OVERVIEW_SPLIT_SELECT)) {
            setTaskViewsPrimarySplitTranslation(this.mTaskViewsPrimarySplitTranslation);
            setTaskViewsSecondarySplitTranslation(this.mTaskViewsSecondarySplitTranslation);
        }
    }

    public void initiateSplitSelect(TaskView taskView, int i) {
        super.initiateSplitSelect(taskView, i);
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW_SPLIT_SELECT);
    }

    public void initiateSplitSelect(QuickstepSystemShortcut.SplitSelectSource splitSelectSource) {
        super.initiateSplitSelect(splitSelectSource);
        ((BaseQuickstepLauncher) this.mActivity).getStateManager().goToState(LauncherState.OVERVIEW_SPLIT_SELECT);
    }

    /* access modifiers changed from: protected */
    public boolean canLaunchFullscreenTask() {
        return !((BaseQuickstepLauncher) this.mActivity).isInState(LauncherState.OVERVIEW_SPLIT_SELECT);
    }
}
