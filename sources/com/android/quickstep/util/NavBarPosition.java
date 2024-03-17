package com.android.quickstep.util;

import com.android.launcher3.util.DisplayController;

public class NavBarPosition {
    private final int mDisplayRotation;
    private final DisplayController.NavigationMode mMode;

    public NavBarPosition(DisplayController.NavigationMode navigationMode, DisplayController.Info info) {
        this.mMode = navigationMode;
        this.mDisplayRotation = info.rotation;
    }

    public NavBarPosition(DisplayController.NavigationMode navigationMode, int i) {
        this.mMode = navigationMode;
        this.mDisplayRotation = i;
    }

    public boolean isRightEdge() {
        return this.mMode != DisplayController.NavigationMode.NO_BUTTON && this.mDisplayRotation == 1;
    }

    public boolean isLeftEdge() {
        return this.mMode != DisplayController.NavigationMode.NO_BUTTON && this.mDisplayRotation == 3;
    }

    public float getRotation() {
        if (isLeftEdge()) {
            return 90.0f;
        }
        return (float) (isRightEdge() ? -90 : 0);
    }
}
