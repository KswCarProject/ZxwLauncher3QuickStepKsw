package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.secondarydisplay.SecondaryDisplayLauncher;

public class SecondaryLauncherAllAppsContainerView extends ActivityAllAppsContainerView<SecondaryDisplayLauncher> {
    /* access modifiers changed from: protected */
    public void updateBackground(DeviceProfile deviceProfile) {
    }

    public SecondaryLauncherAllAppsContainerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SecondaryLauncherAllAppsContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SecondaryLauncherAllAppsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
