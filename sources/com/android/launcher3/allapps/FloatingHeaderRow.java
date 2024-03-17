package com.android.launcher3.allapps;

import android.graphics.Rect;
import android.view.View;
import com.android.launcher3.DeviceProfile;

public interface FloatingHeaderRow {
    public static final FloatingHeaderRow[] NO_ROWS = new FloatingHeaderRow[0];

    int getExpectedHeight();

    View getFocusedChild();

    Class<? extends FloatingHeaderRow> getTypeClass();

    boolean hasVisibleContent();

    void setInsets(Rect rect, DeviceProfile deviceProfile);

    void setVerticalScroll(int i, boolean z);

    void setup(FloatingHeaderView floatingHeaderView, FloatingHeaderRow[] floatingHeaderRowArr, boolean z);

    boolean shouldDraw();

    boolean isVisible() {
        return shouldDraw();
    }
}
