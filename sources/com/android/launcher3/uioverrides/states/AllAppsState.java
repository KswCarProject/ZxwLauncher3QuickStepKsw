package com.android.launcher3.uioverrides.states;

import android.content.Context;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.window.WindowManagerProxy;

public class AllAppsState extends LauncherState {
    private static final int STATE_FLAGS = ((FLAG_WORKSPACE_INACCESSIBLE | FLAG_CLOSE_POPUPS) | FLAG_HOTSEAT_INACCESSIBLE);
    private static final float WORKSPACE_SCALE_FACTOR = 0.97f;

    /* access modifiers changed from: protected */
    public float getDepthUnchecked(Context context) {
        return 2.0f;
    }

    public float getVerticalProgress(Launcher launcher) {
        return 0.0f;
    }

    public AllAppsState(int i) {
        super(i, 4, STATE_FLAGS);
    }

    public <DEVICE_PROFILE_CONTEXT extends Context & DeviceProfile.DeviceProfileListenable> int getTransitionDuration(DEVICE_PROFILE_CONTEXT device_profile_context, boolean z) {
        if (((DeviceProfile.DeviceProfileListenable) device_profile_context).getDeviceProfile().isTablet || !z) {
            return z ? 500 : 300;
        }
        return WindowManagerProxy.MIN_TABLET_WIDTH;
    }

    public String getDescription(Launcher launcher) {
        return launcher.getAppsView().getDescription();
    }

    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        return new LauncherState.ScaleAndTranslation(WORKSPACE_SCALE_FACTOR, 0.0f, 0.0f);
    }

    public LauncherState.ScaleAndTranslation getHotseatScaleAndTranslation(Launcher launcher) {
        if (launcher.getDeviceProfile().isTablet) {
            return getWorkspaceScaleAndTranslation(launcher);
        }
        LauncherState.ScaleAndTranslation workspaceScaleAndTranslation = LauncherState.OVERVIEW.getWorkspaceScaleAndTranslation(launcher);
        return new LauncherState.ScaleAndTranslation(WORKSPACE_SCALE_FACTOR, workspaceScaleAndTranslation.translationX, workspaceScaleAndTranslation.translationY);
    }

    public LauncherState.PageAlphaProvider getWorkspacePageAlphaProvider(final Launcher launcher) {
        final LauncherState.PageAlphaProvider workspacePageAlphaProvider = super.getWorkspacePageAlphaProvider(launcher);
        return new LauncherState.PageAlphaProvider(Interpolators.DEACCEL_2) {
            public float getPageAlpha(int i) {
                if (launcher.getDeviceProfile().isTablet) {
                    return workspacePageAlphaProvider.getPageAlpha(i);
                }
                return 0.0f;
            }
        };
    }

    public int getVisibleElements(Launcher launcher) {
        return launcher.getDeviceProfile().isTablet ? 3 : 2;
    }

    public LauncherState getHistoryForState(LauncherState launcherState) {
        return launcherState == OVERVIEW ? OVERVIEW : NORMAL;
    }

    public int getWorkspaceScrimColor(Launcher launcher) {
        if (launcher.getDeviceProfile().isTablet) {
            return launcher.getResources().getColor(R.color.widgets_picker_scrim);
        }
        return Themes.getAttrColor(launcher, R.attr.allAppsScrimColor);
    }
}
