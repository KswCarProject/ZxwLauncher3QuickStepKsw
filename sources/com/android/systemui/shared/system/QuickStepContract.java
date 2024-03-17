package com.android.systemui.shared.system;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewConfiguration;
import com.android.internal.policy.ScreenDecorationsUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.StringJoiner;

public class QuickStepContract {
    public static final String KEY_EXTRA_RECENT_TASKS = "recent_tasks";
    public static final String KEY_EXTRA_SHELL_BACK_ANIMATION = "extra_shell_back_animation";
    public static final String KEY_EXTRA_SHELL_ONE_HANDED = "extra_shell_one_handed";
    public static final String KEY_EXTRA_SHELL_PIP = "extra_shell_pip";
    public static final String KEY_EXTRA_SHELL_SHELL_TRANSITIONS = "extra_shell_shell_transitions";
    public static final String KEY_EXTRA_SHELL_SPLIT_SCREEN = "extra_shell_split_screen";
    public static final String KEY_EXTRA_SHELL_STARTING_WINDOW = "extra_shell_starting_window";
    public static final String KEY_EXTRA_SUPPORTS_WINDOW_CORNERS = "extra_supports_window_corners";
    public static final String KEY_EXTRA_SYSUI_PROXY = "extra_sysui_proxy";
    public static final String KEY_EXTRA_UNLOCK_ANIMATION_CONTROLLER = "unlock_animation";
    public static final String KEY_EXTRA_WINDOW_CORNER_RADIUS = "extra_window_corner_radius";
    public static final String LAUNCHER_ACTIVITY_CLASS_NAME = "com.google.android.apps.nexuslauncher.NexusLauncherActivity";
    public static final String NAV_BAR_MODE_3BUTTON_OVERLAY = "com.android.internal.systemui.navbar.threebutton";
    public static final String NAV_BAR_MODE_GESTURAL_OVERLAY = "com.android.internal.systemui.navbar.gestural";
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO = 3.0f;
    public static final int SYSUI_STATE_A11Y_BUTTON_CLICKABLE = 16;
    public static final int SYSUI_STATE_A11Y_BUTTON_LONG_CLICKABLE = 32;
    public static final int SYSUI_STATE_ALLOW_GESTURE_IGNORING_BAR_VISIBILITY = 131072;
    public static final int SYSUI_STATE_ASSIST_GESTURE_CONSTRAINED = 8192;
    public static final int SYSUI_STATE_BACK_DISABLED = 4194304;
    public static final int SYSUI_STATE_BOUNCER_SHOWING = 8;
    public static final int SYSUI_STATE_BUBBLES_EXPANDED = 16384;
    public static final int SYSUI_STATE_BUBBLES_MANAGE_MENU_EXPANDED = 8388608;
    public static final int SYSUI_STATE_DEVICE_DOZING = 2097152;
    public static final int SYSUI_STATE_DIALOG_SHOWING = 32768;
    public static final int SYSUI_STATE_HOME_DISABLED = 256;
    public static final int SYSUI_STATE_IME_SHOWING = 262144;
    public static final int SYSUI_STATE_IME_SWITCHER_SHOWING = 1048576;
    public static final int SYSUI_STATE_IMMERSIVE_MODE = 16777216;
    public static final int SYSUI_STATE_MAGNIFICATION_OVERLAP = 524288;
    public static final int SYSUI_STATE_NAV_BAR_HIDDEN = 2;
    public static final int SYSUI_STATE_NOTIFICATION_PANEL_EXPANDED = 4;
    public static final int SYSUI_STATE_ONE_HANDED_ACTIVE = 65536;
    public static final int SYSUI_STATE_OVERVIEW_DISABLED = 128;
    public static final int SYSUI_STATE_QUICK_SETTINGS_EXPANDED = 2048;
    public static final int SYSUI_STATE_SCREEN_PINNING = 1;
    public static final int SYSUI_STATE_SEARCH_DISABLED = 1024;
    public static final int SYSUI_STATE_STATUS_BAR_KEYGUARD_SHOWING = 64;
    public static final int SYSUI_STATE_STATUS_BAR_KEYGUARD_SHOWING_OCCLUDED = 512;
    public static final int SYSUI_STATE_TRACING_ENABLED = 4096;
    public static final int SYSUI_STATE_VOICE_INTERACTION_WINDOW_SHOWING = 33554432;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SystemUiStateFlags {
    }

    public static boolean isAssistantGestureDisabled(int i) {
        if ((131072 & i) != 0) {
            i &= -3;
        }
        if ((i & 3083) != 0) {
            return true;
        }
        return (i & 4) != 0 && (i & 64) == 0;
    }

    public static boolean isBackGestureDisabled(int i) {
        if ((i & 8) != 0 || (32768 & i) != 0 || (33554432 & i) != 0) {
            return false;
        }
        if ((131072 & i) != 0) {
            i &= -3;
        }
        return (i & 70) != 0;
    }

    public static boolean isGesturalMode(int i) {
        return i == 2;
    }

    public static boolean isLegacyMode(int i) {
        return i == 0;
    }

    public static boolean isSwipeUpMode(int i) {
        return i == 1;
    }

    public static String getSystemUiStateString(int i) {
        StringJoiner stringJoiner = new StringJoiner("|");
        String str = "";
        stringJoiner.add((i & 1) != 0 ? "screen_pinned" : str);
        stringJoiner.add((i & 128) != 0 ? "overview_disabled" : str);
        stringJoiner.add((i & 256) != 0 ? "home_disabled" : str);
        stringJoiner.add((i & 1024) != 0 ? "search_disabled" : str);
        stringJoiner.add((i & 2) != 0 ? "navbar_hidden" : str);
        stringJoiner.add((i & 4) != 0 ? "notif_visible" : str);
        stringJoiner.add((i & 2048) != 0 ? "qs_visible" : str);
        stringJoiner.add((i & 64) != 0 ? "keygrd_visible" : str);
        stringJoiner.add((i & 512) != 0 ? "keygrd_occluded" : str);
        stringJoiner.add((i & 8) != 0 ? "bouncer_visible" : str);
        stringJoiner.add((32768 & i) != 0 ? "dialog_showing" : str);
        stringJoiner.add((i & 16) != 0 ? "a11y_click" : str);
        stringJoiner.add((i & 32) != 0 ? "a11y_long_click" : str);
        stringJoiner.add((i & 4096) != 0 ? "tracing" : str);
        stringJoiner.add((i & 8192) != 0 ? "asst_gesture_constrain" : str);
        stringJoiner.add((i & 16384) != 0 ? "bubbles_expanded" : str);
        stringJoiner.add((65536 & i) != 0 ? "one_handed_active" : str);
        stringJoiner.add((131072 & i) != 0 ? "allow_gesture" : str);
        stringJoiner.add((262144 & i) != 0 ? "ime_visible" : str);
        stringJoiner.add((524288 & i) != 0 ? "magnification_overlap" : str);
        stringJoiner.add((1048576 & i) != 0 ? "ime_switcher_showing" : str);
        stringJoiner.add((2097152 & i) != 0 ? "device_dozing" : str);
        stringJoiner.add((4194304 & i) != 0 ? "back_disabled" : str);
        stringJoiner.add((8388608 & i) != 0 ? "bubbles_mange_menu_expanded" : str);
        stringJoiner.add((16777216 & i) != 0 ? "immersive_mode" : str);
        if ((i & SYSUI_STATE_VOICE_INTERACTION_WINDOW_SHOWING) != 0) {
            str = "vis_win_showing";
        }
        stringJoiner.add(str);
        return stringJoiner.toString();
    }

    public static final float getQuickStepTouchSlopPx(Context context) {
        return ((float) ViewConfiguration.get(context).getScaledTouchSlop()) * 3.0f;
    }

    public static int getQuickStepDragSlopPx() {
        return convertDpToPixel(10.0f);
    }

    public static int getQuickStepTouchSlopPx() {
        return convertDpToPixel(24.0f);
    }

    public static int getQuickScrubTouchSlopPx() {
        return convertDpToPixel(24.0f);
    }

    private static int convertDpToPixel(float f) {
        return (int) (f * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float getWindowCornerRadius(Context context) {
        return ScreenDecorationsUtils.getWindowCornerRadius(context);
    }

    public static boolean supportsRoundedCornersOnWindows(Resources resources) {
        return ScreenDecorationsUtils.supportsRoundedCornersOnWindows(resources);
    }
}
