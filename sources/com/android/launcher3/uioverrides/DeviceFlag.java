package com.android.launcher3.uioverrides;

import android.provider.DeviceConfig;
import com.android.launcher3.config.FeatureFlags;

public class DeviceFlag extends FeatureFlags.DebugFlag {
    public static final String NAMESPACE_LAUNCHER = "launcher";
    private final boolean mDefaultValueInCode;

    public DeviceFlag(String str, boolean z, String str2) {
        super(str, getDeviceValue(str, z), str2);
        this.mDefaultValueInCode = z;
    }

    /* access modifiers changed from: protected */
    public StringBuilder appendProps(StringBuilder sb) {
        return super.appendProps(sb).append(", mDefaultValueInCode=").append(this.mDefaultValueInCode);
    }

    public boolean get() {
        return super.get();
    }

    protected static boolean getDeviceValue(String str, boolean z) {
        return DeviceConfig.getBoolean(NAMESPACE_LAUNCHER, str, z);
    }
}
