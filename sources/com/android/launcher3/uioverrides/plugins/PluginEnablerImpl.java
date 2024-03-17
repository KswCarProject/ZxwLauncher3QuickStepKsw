package com.android.launcher3.uioverrides.plugins;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceDataStore;
import com.android.launcher3.Utilities;
import com.android.systemui.shared.plugins.PluginEnabler;

public class PluginEnablerImpl extends PreferenceDataStore implements PluginEnabler {
    private static final String PREFIX_PLUGIN_ENABLED = "PLUGIN_ENABLED_";
    private final SharedPreferences mSharedPrefs;

    public PluginEnablerImpl(Context context) {
        this.mSharedPrefs = Utilities.getDevicePrefs(context);
    }

    public void setEnabled(ComponentName componentName) {
        setState(componentName, true);
    }

    public void setDisabled(ComponentName componentName, int i) {
        setState(componentName, i == 0);
    }

    private void setState(ComponentName componentName, boolean z) {
        putBoolean(pluginEnabledKey(componentName), z);
    }

    public boolean isEnabled(ComponentName componentName) {
        return getBoolean(pluginEnabledKey(componentName), true);
    }

    public int getDisableReason(ComponentName componentName) {
        return isEnabled(componentName) ^ true ? 1 : 0;
    }

    public void putBoolean(String str, boolean z) {
        this.mSharedPrefs.edit().putBoolean(str, z).apply();
    }

    public boolean getBoolean(String str, boolean z) {
        return this.mSharedPrefs.getBoolean(str, z);
    }

    static String pluginEnabledKey(ComponentName componentName) {
        return PREFIX_PLUGIN_ENABLED + componentName.flattenToString();
    }
}
