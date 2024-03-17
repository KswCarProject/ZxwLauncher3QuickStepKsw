package com.android.launcher3;

import android.content.Context;
import android.view.animation.Interpolator;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.states.HintState;
import com.android.launcher3.states.SpringLoadedState;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.uioverrides.states.AllAppsState;
import com.android.launcher3.uioverrides.states.OverviewState;
import java.util.Arrays;

public abstract class LauncherState implements BaseState<LauncherState> {
    public static final LauncherState ALL_APPS = new AllAppsState(5);
    public static final int ALL_APPS_CONTENT = 2;
    public static final LauncherState BACKGROUND_APP = OverviewState.newBackgroundState(6);
    public static final int CLEAR_ALL_BUTTON = 16;
    protected static final PageAlphaProvider DEFAULT_ALPHA_PROVIDER = new PageAlphaProvider(Interpolators.ACCEL_2) {
        public float getPageAlpha(int i) {
            return 1.0f;
        }
    };
    protected static final PageTranslationProvider DEFAULT_PAGE_TRANSLATION_PROVIDER = new PageTranslationProvider(Interpolators.DEACCEL_2) {
        public float getPageTranslation(int i) {
            return 0.0f;
        }
    };
    public static final int FLAG_CLOSE_POPUPS = BaseState.getFlag(6);
    public static final int FLAG_HAS_SYS_UI_SCRIM;
    public static final int FLAG_HIDE_BACK_BUTTON;
    public static final int FLAG_HOTSEAT_INACCESSIBLE = BaseState.getFlag(8);
    public static final int FLAG_MULTI_PAGE = BaseState.getFlag(0);
    public static final int FLAG_OVERVIEW_UI = BaseState.getFlag(7);
    public static final int FLAG_WORKSPACE_HAS_BACKGROUNDS = BaseState.getFlag(3);
    public static final int FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED;
    public static final int FLAG_WORKSPACE_INACCESSIBLE = BaseState.getFlag(1);
    public static final LauncherState HINT_STATE = new HintState(7);
    public static final LauncherState HINT_STATE_TWO_BUTTON = new HintState(8, 3);
    public static final int HOTSEAT_ICONS = 1;
    public static final int NONE = 0;
    public static final LauncherState NORMAL;
    public static final float NO_OFFSET = 0.0f;
    public static final float NO_SCALE = 1.0f;
    public static final LauncherState OVERVIEW = new OverviewState(2);
    public static final int OVERVIEW_ACTIONS = 8;
    public static final LauncherState OVERVIEW_MODAL_TASK = OverviewState.newModalTaskState(3);
    public static final LauncherState OVERVIEW_SPLIT_SELECT = OverviewState.newSplitSelectState(9);
    public static final LauncherState QUICK_SWITCH = OverviewState.newSwitchState(4);
    public static final int SPLIT_PLACHOLDER_VIEW = 64;
    public static final LauncherState SPRING_LOADED = new SpringLoadedState(1);
    public static final int VERTICAL_SWIPE_INDICATOR = 4;
    public static final int WORKSPACE_PAGE_INDICATOR = 32;
    private static final LauncherState[] sAllStates = new LauncherState[10];
    private final int mFlags;
    public final int ordinal;
    public final boolean overviewUi;
    public final int statsLogOrdinal;

    /* access modifiers changed from: protected */
    public float getDepthUnchecked(Context context) {
        return 0.0f;
    }

    public float getOverviewFullscreenProgress() {
        return 0.0f;
    }

    public float getOverviewModalness() {
        return 0.0f;
    }

    public float getSplitSelectTranslation(Launcher launcher) {
        return 0.0f;
    }

    public float getVerticalProgress(Launcher launcher) {
        return 1.0f;
    }

    public int getVisibleElements(Launcher launcher) {
        return 37;
    }

    public float getWorkspaceBackgroundAlpha(Launcher launcher) {
        return 0.0f;
    }

    public int getWorkspaceScrimColor(Launcher launcher) {
        return 0;
    }

    public boolean isTaskbarStashed(Launcher launcher) {
        return false;
    }

    static {
        int flag = BaseState.getFlag(2);
        FLAG_WORKSPACE_ICONS_CAN_BE_DRAGGED = flag;
        int flag2 = BaseState.getFlag(4);
        FLAG_HIDE_BACK_BUTTON = flag2;
        int flag3 = BaseState.getFlag(5);
        FLAG_HAS_SYS_UI_SCRIM = flag3;
        NORMAL = new LauncherState(0, 2, flag | 2 | flag2 | flag3) {
            public int getTransitionDuration(Context context, boolean z) {
                return 0;
            }

            public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState baseState) {
                return LauncherState.super.getHistoryForState((LauncherState) baseState);
            }
        };
    }

    public LauncherState(int i, int i2, int i3) {
        this.statsLogOrdinal = i2;
        this.mFlags = i3;
        this.overviewUi = (FLAG_OVERVIEW_UI & i3) != 0;
        this.ordinal = i;
        sAllStates[i] = this;
    }

    public final boolean hasFlag(int i) {
        return (i & this.mFlags) != 0;
    }

    public static LauncherState[] values() {
        LauncherState[] launcherStateArr = sAllStates;
        return (LauncherState[]) Arrays.copyOf(launcherStateArr, launcherStateArr.length);
    }

    public ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new ScaleAndTranslation(1.0f, 0.0f, 0.0f);
    }

    public ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        return getWorkspaceScaleAndTranslation(launcher);
    }

    public float[] getOverviewScaleAndOffset(Launcher launcher) {
        return launcher.getNormalOverviewScaleAndOffset();
    }

    public boolean areElementsVisible(Launcher launcher, int i) {
        return (getVisibleElements(launcher) & i) == i;
    }

    public boolean isTaskbarAlignedWithHotseat(Launcher launcher) {
        return !isTaskbarStashed(launcher);
    }

    public final float getDepth(Context context) {
        return getDepth(context, BaseDraggingActivity.fromContext(context).getDeviceProfile().isMultiWindowMode);
    }

    public final float getDepth(Context context, boolean z) {
        if (z) {
            return 0.0f;
        }
        return getDepthUnchecked(context);
    }

    public String getDescription(Launcher launcher) {
        return launcher.getWorkspace().getCurrentPageDescription();
    }

    public PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        if ((this != NORMAL && this != HINT_STATE) || !launcher.getDeviceProfile().shouldFadeAdjacentWorkspaceScreens()) {
            return DEFAULT_ALPHA_PROVIDER;
        }
        final int nextPage = launcher.getWorkspace().getNextPage();
        return new PageAlphaProvider(Interpolators.ACCEL_2) {
            public float getPageAlpha(int i) {
                return i != nextPage ? 0.0f : 1.0f;
            }
        };
    }

    public PageTranslationProvider getWorkspacePageTranslationProvider(final Launcher launcher) {
        if (this != SPRING_LOADED || !launcher.getDeviceProfile().isTwoPanels) {
            return DEFAULT_PAGE_TRANSLATION_PROVIDER;
        }
        final float pageSpacing = ((float) launcher.getWorkspace().getPageSpacing()) / 4.0f;
        return new PageTranslationProvider(Interpolators.DEACCEL_2) {
            public float getPageTranslation(int i) {
                boolean z = launcher.getWorkspace().mIsRtl;
                boolean z2 = i % 2 == 0;
                if ((!z2 || z) && (z2 || !z)) {
                    return pageSpacing;
                }
                return -pageSpacing;
            }
        };
    }

    public LauncherState getHistoryForState(LauncherState launcherState) {
        return NORMAL;
    }

    public String toString() {
        return TestProtocol.stateOrdinalToString(this.ordinal);
    }

    public void onBackPressed(Launcher launcher) {
        if (this != NORMAL) {
            StateManager<LauncherState> stateManager = launcher.getStateManager();
            stateManager.goToState(stateManager.getLastState());
        }
    }

    public static abstract class PageAlphaProvider {
        public final Interpolator interpolator;

        public abstract float getPageAlpha(int i);

        public PageAlphaProvider(Interpolator interpolator2) {
            this.interpolator = interpolator2;
        }
    }

    public static abstract class PageTranslationProvider {
        public final Interpolator interpolator;

        public abstract float getPageTranslation(int i);

        public PageTranslationProvider(Interpolator interpolator2) {
            this.interpolator = interpolator2;
        }
    }

    public static class ScaleAndTranslation {
        public float scale;
        public float translationX;
        public float translationY;

        public ScaleAndTranslation(float f, float f2, float f3) {
            this.scale = f;
            this.translationX = f2;
            this.translationY = f3;
        }
    }
}
