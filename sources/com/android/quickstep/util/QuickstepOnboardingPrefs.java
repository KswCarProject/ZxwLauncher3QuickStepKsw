package com.android.quickstep.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.appprediction.AppsDividerView;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.hybridhotseat.HotseatPredictionController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.quickstep.views.AllAppsEduView;

public class QuickstepOnboardingPrefs extends OnboardingPrefs<QuickstepLauncher> {
    public QuickstepOnboardingPrefs(final QuickstepLauncher quickstepLauncher, SharedPreferences sharedPreferences) {
        super(quickstepLauncher, sharedPreferences);
        final StateManager<LauncherState> stateManager = quickstepLauncher.getStateManager();
        if (!getBoolean(OnboardingPrefs.HOME_BOUNCE_SEEN)) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() {
                public void onStateTransitionComplete(LauncherState launcherState) {
                    boolean z = DisplayController.getNavigationMode((Context) QuickstepOnboardingPrefs.this.mLauncher).hasGestures;
                    LauncherState launcherState2 = (LauncherState) stateManager.getLastState();
                    if ((z && launcherState == LauncherState.OVERVIEW) || ((!z && launcherState == LauncherState.ALL_APPS && launcherState2 == LauncherState.NORMAL) || QuickstepOnboardingPrefs.this.hasReachedMaxCount(OnboardingPrefs.HOME_BOUNCE_COUNT))) {
                        QuickstepOnboardingPrefs.this.mSharedPrefs.edit().putBoolean(OnboardingPrefs.HOME_BOUNCE_SEEN, true).apply();
                        stateManager.removeStateListener(this);
                    }
                }
            });
        }
        if (!Utilities.IS_RUNNING_IN_TEST_HARNESS && !hasReachedMaxCount(OnboardingPrefs.HOTSEAT_DISCOVERY_TIP_COUNT)) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() {
                boolean mFromAllApps = false;

                public void onStateTransitionStart(LauncherState launcherState) {
                    this.mFromAllApps = ((QuickstepLauncher) QuickstepOnboardingPrefs.this.mLauncher).getStateManager().getCurrentStableState() == LauncherState.ALL_APPS;
                }

                public void onStateTransitionComplete(LauncherState launcherState) {
                    HotseatPredictionController hotseatPredictionController = ((QuickstepLauncher) QuickstepOnboardingPrefs.this.mLauncher).getHotseatPredictionController();
                    if (this.mFromAllApps && launcherState == LauncherState.NORMAL && hotseatPredictionController.hasPredictions() && QuickstepOnboardingPrefs.this.incrementEventCount(OnboardingPrefs.HOTSEAT_DISCOVERY_TIP_COUNT)) {
                        hotseatPredictionController.showEdu();
                        stateManager.removeStateListener(this);
                    }
                }
            });
        }
        if (DisplayController.getNavigationMode(quickstepLauncher) == DisplayController.NavigationMode.NO_BUTTON && FeatureFlags.ENABLE_ALL_APPS_EDU.get()) {
            stateManager.addStateListener(new StateManager.StateListener<LauncherState>() {
                private static final int MAX_NUM_SWIPES_TO_TRIGGER_EDU = 3;
                private int mCount = 0;
                private boolean mShouldIncreaseCount;

                public void onStateTransitionStart(LauncherState launcherState) {
                    if (launcherState != LauncherState.NORMAL) {
                        this.mShouldIncreaseCount = launcherState == LauncherState.HINT_STATE && quickstepLauncher.getWorkspace().getNextPage() == 0;
                    }
                }

                public void onStateTransitionComplete(LauncherState launcherState) {
                    AllAppsEduView allAppsEduView;
                    if (launcherState != LauncherState.NORMAL) {
                        if (!this.mShouldIncreaseCount || launcherState != LauncherState.HINT_STATE) {
                            this.mCount = 0;
                        } else {
                            this.mCount++;
                        }
                        if (launcherState == LauncherState.ALL_APPS && (allAppsEduView = (AllAppsEduView) AbstractFloatingView.getOpenView(QuickstepOnboardingPrefs.this.mLauncher, 512)) != null) {
                            allAppsEduView.close(false);
                        }
                    } else if (this.mCount >= 3) {
                        if (AbstractFloatingView.getOpenView(QuickstepOnboardingPrefs.this.mLauncher, 512) == null) {
                            AllAppsEduView.show(quickstepLauncher);
                        }
                        this.mCount = 0;
                    }
                }
            });
        }
        if (!hasReachedMaxCount(OnboardingPrefs.ALL_APPS_VISITED_COUNT)) {
            ((QuickstepLauncher) this.mLauncher).getStateManager().addStateListener(new StateManager.StateListener<LauncherState>() {
                public void onStateTransitionComplete(LauncherState launcherState) {
                    if (launcherState == LauncherState.ALL_APPS) {
                        QuickstepOnboardingPrefs.this.incrementEventCount(OnboardingPrefs.ALL_APPS_VISITED_COUNT);
                        return;
                    }
                    boolean hasReachedMaxCount = QuickstepOnboardingPrefs.this.hasReachedMaxCount(OnboardingPrefs.ALL_APPS_VISITED_COUNT);
                    ((AppsDividerView) ((QuickstepLauncher) QuickstepOnboardingPrefs.this.mLauncher).getAppsView().getFloatingHeaderView().findFixedRowByType(AppsDividerView.class)).setShowAllAppsLabel(!hasReachedMaxCount);
                    if (hasReachedMaxCount) {
                        ((QuickstepLauncher) QuickstepOnboardingPrefs.this.mLauncher).getStateManager().removeStateListener(this);
                    }
                }
            });
        }
    }
}
