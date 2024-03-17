package com.android.systemui.plugins;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_ALL_APPS_ACTIONS", version = 1)
public interface AllAppsRow extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_ALL_APPS_ACTIONS";
    public static final int VERSION = 1;

    public interface OnHeightUpdatedListener {
        void onHeightUpdated();
    }

    int getExpectedHeight();

    void setOnHeightUpdatedListener(OnHeightUpdatedListener onHeightUpdatedListener);

    View setup(ViewGroup viewGroup);
}
