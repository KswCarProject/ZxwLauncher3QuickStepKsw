package com.android.quickstep.fallback;

import android.content.Context;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.uioverrides.states.OverviewModalTaskState;
import com.android.launcher3.util.Themes;
import com.android.quickstep.RecentsActivity;

public class RecentsState implements BaseState<RecentsState> {
    public static final RecentsState BACKGROUND_APP;
    public static final RecentsState BG_LAUNCHER = new LauncherState(4, 0);
    public static final RecentsState DEFAULT;
    private static final int FLAG_CLEAR_ALL_BUTTON;
    private static final int FLAG_FULL_SCREEN;
    private static final int FLAG_LIVE_TILE;
    private static final int FLAG_MODAL;
    private static final int FLAG_OVERVIEW_ACTIONS;
    private static final int FLAG_OVERVIEW_UI;
    private static final int FLAG_SCRIM;
    private static final int FLAG_SHOW_AS_GRID;
    public static final RecentsState HOME = new RecentsState(3, 0);
    public static final RecentsState MODAL_TASK;
    private static final float NO_OFFSET = 0.0f;
    private static final float NO_SCALE = 1.0f;
    public static final RecentsState OVERVIEW_SPLIT_SELECT;
    private final int mFlags;
    public final int ordinal;

    public int getTransitionDuration(Context context, boolean z) {
        return 250;
    }

    static {
        int flag = BaseState.getFlag(0);
        FLAG_MODAL = flag;
        int flag2 = BaseState.getFlag(1);
        FLAG_CLEAR_ALL_BUTTON = flag2;
        int flag3 = BaseState.getFlag(2);
        FLAG_FULL_SCREEN = flag3;
        int flag4 = BaseState.getFlag(3);
        FLAG_OVERVIEW_ACTIONS = flag4;
        int flag5 = BaseState.getFlag(4);
        FLAG_SHOW_AS_GRID = flag5;
        int flag6 = BaseState.getFlag(5);
        FLAG_SCRIM = flag6;
        int flag7 = BaseState.getFlag(6);
        FLAG_LIVE_TILE = flag7;
        int flag8 = BaseState.getFlag(7);
        FLAG_OVERVIEW_UI = flag8;
        DEFAULT = new RecentsState(0, flag2 | 2 | flag4 | flag5 | flag6 | flag7 | flag8);
        MODAL_TASK = new ModalState(1, flag | flag2 | 2 | flag4 | flag5 | flag6 | flag7 | flag8);
        BACKGROUND_APP = new BackgroundAppState(2, flag3 | 3 | flag8);
        OVERVIEW_SPLIT_SELECT = new RecentsState(5, flag5 | flag6 | flag8);
    }

    public RecentsState(int i, int i2) {
        this.ordinal = i;
        this.mFlags = i2;
    }

    public String toString() {
        return "Ordinal-" + this.ordinal;
    }

    public final boolean hasFlag(int i) {
        return (i & this.mFlags) != 0;
    }

    public RecentsState getHistoryForState(RecentsState recentsState) {
        return DEFAULT;
    }

    public float getOverviewModalness() {
        return hasFlag(FLAG_MODAL) ? 1.0f : 0.0f;
    }

    public boolean isFullScreen() {
        return hasFlag(FLAG_FULL_SCREEN);
    }

    public boolean hasClearAllButton() {
        return hasFlag(FLAG_CLEAR_ALL_BUTTON);
    }

    public boolean hasOverviewActions() {
        return hasFlag(FLAG_OVERVIEW_ACTIONS);
    }

    public boolean hasLiveTile() {
        return hasFlag(FLAG_LIVE_TILE);
    }

    public int getScrimColor(RecentsActivity recentsActivity) {
        if (hasFlag(FLAG_SCRIM)) {
            return Themes.getAttrColor(recentsActivity, R.attr.overviewScrimColor);
        }
        return 0;
    }

    public float[] getOverviewScaleAndOffset(RecentsActivity recentsActivity) {
        return new float[]{1.0f, 0.0f};
    }

    public boolean displayOverviewTasksAsGrid(DeviceProfile deviceProfile) {
        return hasFlag(FLAG_SHOW_AS_GRID) && deviceProfile.isTablet;
    }

    public boolean overviewUi() {
        return hasFlag(FLAG_OVERVIEW_UI);
    }

    private static class ModalState extends RecentsState {
        public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState baseState) {
            return RecentsState.super.getHistoryForState((RecentsState) baseState);
        }

        public ModalState(int i, int i2) {
            super(i, i2);
        }

        public float[] getOverviewScaleAndOffset(RecentsActivity recentsActivity) {
            return OverviewModalTaskState.getOverviewScaleAndOffsetForModalState(recentsActivity);
        }
    }

    private static class BackgroundAppState extends RecentsState {
        public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState baseState) {
            return RecentsState.super.getHistoryForState((RecentsState) baseState);
        }

        public BackgroundAppState(int i, int i2) {
            super(i, i2);
        }

        public float[] getOverviewScaleAndOffset(RecentsActivity recentsActivity) {
            return com.android.launcher3.uioverrides.states.BackgroundAppState.getOverviewScaleAndOffsetForBackgroundState(recentsActivity);
        }
    }

    private static class LauncherState extends RecentsState {
        public /* bridge */ /* synthetic */ BaseState getHistoryForState(BaseState baseState) {
            return RecentsState.super.getHistoryForState((RecentsState) baseState);
        }

        LauncherState(int i, int i2) {
            super(i, i2);
        }

        public float[] getOverviewScaleAndOffset(RecentsActivity recentsActivity) {
            return new float[]{1.0f, 1.0f};
        }
    }
}
