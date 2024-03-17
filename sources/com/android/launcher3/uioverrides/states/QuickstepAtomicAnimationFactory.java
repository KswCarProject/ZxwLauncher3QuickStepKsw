package com.android.launcher3.uioverrides.states;

import com.android.launcher3.CellLayout;
import com.android.launcher3.Hotseat;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.WorkspaceStateTransitionAnimation;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.states.StateAnimationConfig;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.util.RecentsAtomicAnimationFactory;
import com.android.quickstep.views.RecentsView;

public class QuickstepAtomicAnimationFactory extends RecentsAtomicAnimationFactory<QuickstepLauncher, LauncherState> {
    private static final int DEFAULT_PAGE = 0;
    private static final int MAX_PAGE_SCROLL_DURATION = 750;
    private static final int PER_PAGE_SCROLL_DURATION = 150;
    private static final float RECENTS_PREPARE_SCALE = 1.33f;
    private static final float WORKSPACE_PREPARE_SCALE = 0.92f;
    private int mHintToNormalDuration = -1;

    public QuickstepAtomicAnimationFactory(QuickstepLauncher quickstepLauncher) {
        super(quickstepLauncher);
    }

    public void prepareForAtomicAnimation(LauncherState launcherState, LauncherState launcherState2, StateAnimationConfig stateAnimationConfig) {
        LauncherState launcherState3 = launcherState;
        LauncherState launcherState4 = launcherState2;
        StateAnimationConfig stateAnimationConfig2 = stateAnimationConfig;
        RecentsView recentsView = (RecentsView) ((QuickstepLauncher) this.mActivity).getOverviewPanel();
        boolean z = true;
        if (launcherState4 == LauncherState.NORMAL && launcherState3 == LauncherState.OVERVIEW) {
            stateAnimationConfig2.setInterpolator(14, Interpolators.clampToProgress(Interpolators.LINEAR, 0.0f, 0.25f));
            stateAnimationConfig2.setInterpolator(11, Interpolators.LINEAR);
            stateAnimationConfig2.setInterpolator(1, Interpolators.DEACCEL);
            stateAnimationConfig2.setInterpolator(3, Interpolators.ACCEL);
            if (!DisplayController.getNavigationMode(this.mActivity).hasGestures || recentsView.getTaskViewCount() <= 0) {
                stateAnimationConfig2.setInterpolator(7, Interpolators.ACCEL_DEACCEL);
                stateAnimationConfig2.setInterpolator(6, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.9f));
                stateAnimationConfig2.setInterpolator(9, Interpolators.DEACCEL_1_7);
            } else {
                stateAnimationConfig2.setInterpolator(6, Interpolators.FINAL_FRAME);
                stateAnimationConfig2.setInterpolator(9, Interpolators.FINAL_FRAME);
                stateAnimationConfig2.setInterpolator(7, Interpolators.clampToProgress(Interpolators.FAST_OUT_SLOW_IN, 0.0f, 0.75f));
                stateAnimationConfig2.setInterpolator(8, Interpolators.FINAL_FRAME);
            }
            stateAnimationConfig2.duration = Math.max(stateAnimationConfig2.duration, (long) Math.min(MAX_PAGE_SCROLL_DURATION, (recentsView.getNextPage() - 0) * 150));
            recentsView.snapToPage(0, Math.toIntExact(stateAnimationConfig2.duration));
            Workspace<?> workspace = ((QuickstepLauncher) this.mActivity).getWorkspace();
            boolean z2 = workspace.getVisibility() == 0;
            if (z2) {
                CellLayout cellLayout = (CellLayout) workspace.getChildAt(workspace.getCurrentPage());
                z2 = cellLayout.getVisibility() == 0 && cellLayout.getShortcutsAndWidgets().getAlpha() > 0.0f;
            }
            if (!z2) {
                workspace.setScaleX(0.92f);
                workspace.setScaleY(0.92f);
            }
            Hotseat hotseat = ((QuickstepLauncher) this.mActivity).getHotseat();
            if (hotseat.getVisibility() != 0 || hotseat.getAlpha() <= 0.0f) {
                z = false;
            }
            if (!z) {
                hotseat.setScaleX(0.92f);
                hotseat.setScaleY(0.92f);
            }
        } else if ((launcherState3 == LauncherState.NORMAL || launcherState3 == LauncherState.HINT_STATE || launcherState3 == LauncherState.HINT_STATE_TWO_BUTTON) && launcherState4 == LauncherState.OVERVIEW) {
            if (DisplayController.getNavigationMode(this.mActivity).hasGestures) {
                stateAnimationConfig2.setInterpolator(1, launcherState3 == LauncherState.NORMAL ? Interpolators.ACCEL : Interpolators.OVERSHOOT_1_2);
                stateAnimationConfig2.setInterpolator(2, Interpolators.ACCEL);
                if (recentsView.getTaskViewCount() > 0) {
                    stateAnimationConfig2.setInterpolator(9, Interpolators.INSTANT);
                } else {
                    stateAnimationConfig2.setInterpolator(9, Interpolators.OVERSHOOT_1_2);
                }
            } else {
                stateAnimationConfig2.setInterpolator(1, Interpolators.OVERSHOOT_1_2);
                stateAnimationConfig2.setInterpolator(9, Interpolators.OVERSHOOT_1_2);
                if (recentsView.getVisibility() != 0 || recentsView.getContentAlpha() == 0.0f) {
                    RecentsView.RECENTS_SCALE_PROPERTY.set(recentsView, Float.valueOf(RECENTS_PREPARE_SCALE));
                }
            }
            stateAnimationConfig2.setInterpolator(3, Interpolators.OVERSHOOT_1_2);
            stateAnimationConfig2.setInterpolator(10, Interpolators.OVERSHOOT_1_2);
            stateAnimationConfig2.setInterpolator(6, Interpolators.OVERSHOOT_1_2);
            stateAnimationConfig2.setInterpolator(13, Interpolators.OVERSHOOT_1_2);
            stateAnimationConfig2.setInterpolator(11, $$Lambda$QuickstepAtomicAnimationFactory$ohSF7dIy6KDYmDrCqxuPpm4zo7Y.INSTANCE);
            stateAnimationConfig2.setInterpolator(7, Interpolators.OVERSHOOT_1_2);
            stateAnimationConfig2.setInterpolator(8, Interpolators.OVERSHOOT_1_2);
        } else if (launcherState3 == LauncherState.HINT_STATE && launcherState4 == LauncherState.NORMAL) {
            stateAnimationConfig2.setInterpolator(13, Interpolators.DEACCEL_3);
            if (this.mHintToNormalDuration == -1) {
                this.mHintToNormalDuration = (int) WorkspaceStateTransitionAnimation.getWorkspaceSpringScaleAnimator((Launcher) this.mActivity, ((QuickstepLauncher) this.mActivity).getWorkspace(), launcherState4.getWorkspaceScaleAndTranslation((Launcher) this.mActivity).scale).getDuration();
            }
            stateAnimationConfig2.duration = Math.max(stateAnimationConfig2.duration, (long) this.mHintToNormalDuration);
        } else if (launcherState3 == LauncherState.ALL_APPS && launcherState4 == LauncherState.NORMAL) {
            boolean z3 = ((QuickstepLauncher) this.mActivity).getDeviceProfile().isTablet;
            stateAnimationConfig2.setInterpolator(10, z3 ? Interpolators.FINAL_FRAME : Interpolators.clampToProgress(Interpolators.LINEAR, 0.19999999f, 0.5f));
            stateAnimationConfig2.setInterpolator(11, Interpolators.clampToProgress(Interpolators.LINEAR, 0.5f, 0.9f));
            stateAnimationConfig2.setInterpolator(0, com.android.systemui.animation.Interpolators.EMPHASIZED_ACCELERATE);
            if (!z3) {
                stateAnimationConfig2.setInterpolator(3, Interpolators.INSTANT);
            }
        } else if (launcherState3 == LauncherState.NORMAL && launcherState4 == LauncherState.ALL_APPS && ((QuickstepLauncher) this.mActivity).getDeviceProfile().isTablet) {
            stateAnimationConfig2.setInterpolator(0, com.android.systemui.animation.Interpolators.EMPHASIZED_DECELERATE);
        }
    }
}
