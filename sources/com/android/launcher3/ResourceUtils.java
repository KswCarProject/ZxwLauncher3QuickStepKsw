package com.android.launcher3;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ResourceUtils {
    public static final int DEFAULT_NAVBAR_VALUE = 48;
    public static final int INVALID_RESOURCE_HANDLE = -1;
    public static final String NAVBAR_BOTTOM_GESTURE_LARGER_SIZE = "navigation_bar_gesture_larger_height";
    public static final String NAVBAR_BOTTOM_GESTURE_SIZE = "navigation_bar_gesture_height";
    public static final String NAVBAR_HEIGHT = "navigation_bar_height";
    public static final String NAVBAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    public static final String NAVBAR_LANDSCAPE_LEFT_RIGHT_SIZE = "navigation_bar_width";
    public static final String STATUS_BAR_HEIGHT = "status_bar_height";
    public static final String STATUS_BAR_HEIGHT_LANDSCAPE = "status_bar_height_landscape";
    public static final String STATUS_BAR_HEIGHT_PORTRAIT = "status_bar_height_portrait";

    public static int getNavbarSize(String str, Resources resources) {
        return getDimenByName(str, resources, 48);
    }

    public static int getDimenByName(String str, Resources resources, int i) {
        int identifier = resources.getIdentifier(str, "dimen", "android");
        if (identifier != 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return pxFromDp((float) i, resources.getDisplayMetrics());
    }

    public static boolean getBoolByName(String str, Resources resources, boolean z) {
        int identifier = resources.getIdentifier(str, "bool", "android");
        return identifier != 0 ? resources.getBoolean(identifier) : z;
    }

    public static int getIntegerByName(String str, Resources resources, int i) {
        int identifier = resources.getIdentifier(str, "integer", "android");
        return identifier != 0 ? resources.getInteger(identifier) : i;
    }

    public static int pxFromDp(float f, DisplayMetrics displayMetrics) {
        return pxFromDp(f, displayMetrics, 1.0f);
    }

    public static int pxFromDp(float f, DisplayMetrics displayMetrics, float f2) {
        if (f < 0.0f) {
            return -1;
        }
        return Math.round(f2 * TypedValue.applyDimension(1, f, displayMetrics));
    }
}
