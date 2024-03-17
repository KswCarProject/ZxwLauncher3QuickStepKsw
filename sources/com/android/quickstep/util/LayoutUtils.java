package com.android.quickstep.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.LauncherActivityInterface;

public class LayoutUtils {
    public static float getDefaultSwipeHeight(Context context, DeviceProfile deviceProfile) {
        float f = ((float) deviceProfile.allAppsCellHeightPx) - deviceProfile.allAppsIconTextSizePx;
        return DisplayController.getNavigationMode(context) == DisplayController.NavigationMode.NO_BUTTON ? f - ((float) deviceProfile.getInsets().bottom) : f;
    }

    public static int getShelfTrackingDistance(Context context, DeviceProfile deviceProfile, PagedOrientationHandler pagedOrientationHandler) {
        Rect rect = new Rect();
        LauncherActivityInterface.INSTANCE.calculateTaskSize(context, deviceProfile, rect);
        return pagedOrientationHandler.getDistanceToBottomOfRect(deviceProfile, rect);
    }

    public static void setViewEnabled(View view, boolean z) {
        view.setEnabled(z);
        if (view instanceof ViewGroup) {
            int i = 0;
            while (true) {
                ViewGroup viewGroup = (ViewGroup) view;
                if (i < viewGroup.getChildCount()) {
                    setViewEnabled(viewGroup.getChildAt(i), z);
                    i++;
                } else {
                    return;
                }
            }
        }
    }
}
