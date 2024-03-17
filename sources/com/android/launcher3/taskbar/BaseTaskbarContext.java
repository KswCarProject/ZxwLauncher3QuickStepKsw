package com.android.launcher3.taskbar;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.AppLauncher;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTaskbarContext extends ContextThemeWrapper implements AppLauncher, DeviceProfile.DeviceProfileListenable {
    private final List<DeviceProfile.OnDeviceProfileChangeListener> mDPChangeListeners = new ArrayList();
    protected final LayoutInflater mLayoutInflater = LayoutInflater.from(this).cloneInContext(this);

    public abstract void onDragEnd();

    public abstract void onDragStart();

    public abstract void onPopupVisibilityChanged(boolean z);

    public BaseTaskbarContext(Context context) {
        super(context, Themes.getActivityThemeRes(context));
    }

    public final LayoutInflater getLayoutInflater() {
        return this.mLayoutInflater;
    }

    public final List<DeviceProfile.OnDeviceProfileChangeListener> getOnDeviceProfileChangeListeners() {
        return this.mDPChangeListeners;
    }
}
