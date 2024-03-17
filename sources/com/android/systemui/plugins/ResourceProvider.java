package com.android.systemui.plugins;

import com.android.systemui.plugins.annotations.ProvidesInterface;

@ProvidesInterface(action = "com.android.launcher3.action.PLUGIN_DYNAMIC_RESOURCE", version = 1)
public interface ResourceProvider extends Plugin {
    public static final String ACTION = "com.android.launcher3.action.PLUGIN_DYNAMIC_RESOURCE";
    public static final int VERSION = 1;

    int getColor(int i);

    float getDimension(int i);

    float getFloat(int i);

    float getFraction(int i);

    int getInt(int i);
}
