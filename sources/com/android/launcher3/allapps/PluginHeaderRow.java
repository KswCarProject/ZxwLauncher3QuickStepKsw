package com.android.launcher3.allapps;

import android.graphics.Rect;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.systemui.plugins.AllAppsRow;

public class PluginHeaderRow implements FloatingHeaderRow {
    private final AllAppsRow mPlugin;
    final View mView;

    public View getFocusedChild() {
        return null;
    }

    public boolean hasVisibleContent() {
        return true;
    }

    public void setInsets(Rect rect, DeviceProfile deviceProfile) {
    }

    public void setup(FloatingHeaderView floatingHeaderView, FloatingHeaderRow[] floatingHeaderRowArr, boolean z) {
    }

    public boolean shouldDraw() {
        return true;
    }

    PluginHeaderRow(AllAppsRow allAppsRow, FloatingHeaderView floatingHeaderView) {
        this.mPlugin = allAppsRow;
        this.mView = allAppsRow.setup(floatingHeaderView);
    }

    public int getExpectedHeight() {
        return this.mPlugin.getExpectedHeight();
    }

    public void setVerticalScroll(int i, boolean z) {
        this.mView.setVisibility(z ? 4 : 0);
        if (!z) {
            this.mView.setTranslationY((float) i);
        }
    }

    public Class<PluginHeaderRow> getTypeClass() {
        return PluginHeaderRow.class;
    }
}
