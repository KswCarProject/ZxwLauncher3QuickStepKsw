package com.android.launcher3.util;

import android.view.Window;
import java.util.Arrays;

public class SystemUiController {
    public static final int FLAG_DARK_NAV = 2;
    public static final int FLAG_DARK_STATUS = 8;
    public static final int FLAG_LIGHT_NAV = 1;
    public static final int FLAG_LIGHT_STATUS = 4;
    public static final int UI_STATE_ALLAPPS = 4;
    public static final int UI_STATE_BASE_WINDOW = 0;
    public static final int UI_STATE_FULLSCREEN_TASK = 3;
    public static final int UI_STATE_SCRIM_VIEW = 1;
    public static final int UI_STATE_WIDGET_BOTTOM_SHEET = 2;
    private final int[] mStates = new int[5];
    private final Window mWindow;

    private int getSysUiVisibilityFlags(int i, int i2) {
        if ((i & 1) != 0) {
            i2 |= 16;
        } else if ((i & 2) != 0) {
            i2 &= -17;
        }
        return (i & 4) != 0 ? i2 | 8192 : (i & 8) != 0 ? i2 & -8193 : i2;
    }

    public SystemUiController(Window window) {
        this.mWindow = window;
    }

    public void updateUiState(int i, boolean z) {
        updateUiState(i, z ? 5 : 10);
    }

    public void updateUiState(int i, int i2) {
        int[] iArr = this.mStates;
        if (iArr[i] != i2) {
            iArr[i] = i2;
            int systemUiVisibility = this.mWindow.getDecorView().getSystemUiVisibility();
            int i3 = systemUiVisibility;
            for (int sysUiVisibilityFlags : this.mStates) {
                i3 = getSysUiVisibilityFlags(sysUiVisibilityFlags, i3);
            }
            if (i3 != systemUiVisibility) {
                this.mWindow.getDecorView().setSystemUiVisibility(i3);
            }
        }
    }

    public int getBaseSysuiVisibility() {
        return getSysUiVisibilityFlags(this.mStates[0], this.mWindow.getDecorView().getSystemUiVisibility());
    }

    public String toString() {
        return "mStates=" + Arrays.toString(this.mStates);
    }
}
