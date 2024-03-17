package com.android.launcher3.statemanager;

import android.content.Context;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.statemanager.BaseState;

public interface BaseState<T extends BaseState> {
    public static final int FLAG_DISABLE_RESTORE = 2;
    public static final int FLAG_NON_INTERACTIVE = 1;

    static int getFlag(int i) {
        return 1 << (i + 2);
    }

    boolean displayOverviewTasksAsGrid(DeviceProfile deviceProfile) {
        return false;
    }

    T getHistoryForState(T t);

    <DEVICE_PROFILE_CONTEXT extends Context & DeviceProfile.DeviceProfileListenable> int getTransitionDuration(DEVICE_PROFILE_CONTEXT device_profile_context, boolean z);

    boolean hasFlag(int i);

    boolean shouldDisableRestore() {
        return hasFlag(2);
    }
}
