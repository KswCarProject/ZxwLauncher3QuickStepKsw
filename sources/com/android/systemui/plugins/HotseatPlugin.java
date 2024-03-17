package com.android.systemui.plugins;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.systemui.action.PLUGIN_HOTSEAT", version = 1)
public interface HotseatPlugin extends Plugin {
    public static final String ACTION = "com.android.systemui.action.PLUGIN_HOTSEAT";
    public static final int VERSION = 1;

    View createView(ViewGroup viewGroup);
}
