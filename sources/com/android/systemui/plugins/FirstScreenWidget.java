package com.android.systemui.plugins;

import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_FIRST_SCREEN_WIDGET", version = 1)
public interface FirstScreenWidget extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_FIRST_SCREEN_WIDGET";
    public static final int VERSION = 1;

    void onWidgetUpdated(ViewGroup viewGroup);
}
