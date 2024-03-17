package com.android.launcher3.config;

import android.content.Context;
import com.android.launcher3.Utilities;
import com.android.launcher3.uioverrides.DeviceFlag;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class FeatureFlags {
    public static final BooleanFlag ALWAYS_USE_HARDWARE_OPTIMIZATION_FOR_FOLDER_ANIMATIONS = getDebugFlag("ALWAYS_USE_HARDWARE_OPTIMIZATION_FOR_FOLDER_ANIMATIONS", false, "Always use hardware optimization for folder animations.");
    public static final BooleanFlag ASSISTANT_GIVES_LAUNCHER_FOCUS = getDebugFlag("ASSISTANT_GIVES_LAUNCHER_FOCUS", false, "Allow Launcher to handle nav bar gestures while Assistant is running over it");
    public static final BooleanFlag COLLECT_SEARCH_HISTORY = new DeviceFlag("COLLECT_SEARCH_HISTORY", false, "Allow launcher to collect search history for log");
    public static final BooleanFlag ENABLE_ALL_APPS_BUTTON_IN_HOTSEAT = getDebugFlag("ENABLE_ALL_APPS_BUTTON_IN_HOTSEAT", false, "Enables displaying the all apps button in the hotseat.");
    public static final BooleanFlag ENABLE_ALL_APPS_EDU = getDebugFlag("ENABLE_ALL_APPS_EDU", true, "Shows user a tutorial on how to get to All Apps after X amount of attempts.");
    public static final BooleanFlag ENABLE_ALL_APPS_IN_TASKBAR = getDebugFlag("ENABLE_ALL_APPS_IN_TASKBAR", true, "Enables accessing All Apps from the system Taskbar.");
    public static final BooleanFlag ENABLE_ALL_APPS_ONE_SEARCH_IN_TASKBAR = getDebugFlag("ENABLE_ALL_APPS_ONE_SEARCH_IN_TASKBAR", false, "Enables One Search box in Taskbar All Apps.");
    public static final BooleanFlag ENABLE_APP_PREDICTIONS_WHILE_VISIBLE = new DeviceFlag("ENABLE_APP_PREDICTIONS_WHILE_VISIBLE", true, "Allows app predictions to be updated while they are visible to the user.");
    public static final BooleanFlag ENABLE_BACK_SWIPE_HOME_ANIMATION = getDebugFlag("ENABLE_BACK_SWIPE_HOME_ANIMATION", true, "Enables home animation to icon when user swipes back.");
    public static final BooleanFlag ENABLE_BULK_ALL_APPS_ICON_LOADING = getDebugFlag("ENABLE_BULK_ALL_APPS_ICON_LOADING", true, "Enable loading all apps icons in bulk.");
    public static final BooleanFlag ENABLE_BULK_WORKSPACE_ICON_LOADING = getDebugFlag("ENABLE_BULK_WORKSPACE_ICON_LOADING", true, "Enable loading workspace icons in bulk.");
    public static final BooleanFlag ENABLE_DATABASE_RESTORE = getDebugFlag("ENABLE_DATABASE_RESTORE", false, "Enable database restore when new restore session is created");
    public static final BooleanFlag ENABLE_DEEP_SHORTCUT_ICON_CACHE = getDebugFlag("ENABLE_DEEP_SHORTCUT_ICON_CACHE", true, "R/W deep shortcut in IconCache");
    public static final BooleanFlag ENABLE_DEVICE_SEARCH = new DeviceFlag("ENABLE_DEVICE_SEARCH", true, "Allows on device search in all apps");
    public static final BooleanFlag ENABLE_DEVICE_SEARCH_PERFORMANCE_LOGGING = new DeviceFlag("ENABLE_DEVICE_SEARCH_PERFORMANCE_LOGGING", false, "Allows on device search in all apps logging");
    public static final BooleanFlag ENABLE_DISMISS_PREDICTION_UNDO = getDebugFlag("ENABLE_DISMISS_PREDICTION_UNDO", false, "Show an 'Undo' snackbar when users dismiss a predicted hotseat item");
    public static final BooleanFlag ENABLE_ENFORCED_ROUNDED_CORNERS = new DeviceFlag("ENABLE_ENFORCED_ROUNDED_CORNERS", true, "Enforce rounded corners on all App Widgets");
    public static final BooleanFlag ENABLE_FLOATING_SEARCH_BAR = getDebugFlag("ENABLE_FLOATING_SEARCH_BAR", false, "Keep All Apps search bar at the bottom (but above keyboard if open)");
    public static final BooleanFlag ENABLE_ICON_LABEL_AUTO_SCALING = getDebugFlag("ENABLE_ICON_LABEL_AUTO_SCALING", true, "Enables scaling/spacing for icon labels to make more characters visible");
    public static final BooleanFlag ENABLE_LAUNCHER_ACTIVITY_THEME_CROSSFADE = new DeviceFlag("ENABLE_LAUNCHER_ACTIVITY_THEME_CROSSFADE", false, "Enables a crossfade animation when the system these changes.");
    public static final BooleanFlag ENABLE_LOCAL_COLOR_POPUPS = getDebugFlag("ENABLE_LOCAL_COLOR_POPUPS", false, "Enable local color extraction for popups.");
    public static final BooleanFlag ENABLE_LOCAL_RECOMMENDED_WIDGETS_FILTER = new DeviceFlag("ENABLE_LOCAL_RECOMMENDED_WIDGETS_FILTER", true, "Enables a local filter for recommended widgets.");
    public static final BooleanFlag ENABLE_MINIMAL_DEVICE = getDebugFlag("ENABLE_MINIMAL_DEVICE", false, "Allow user to toggle minimal device mode in launcher.");
    public static final BooleanFlag ENABLE_NEW_MIGRATION_LOGIC = getDebugFlag("ENABLE_NEW_MIGRATION_LOGIC", true, "Enable the new grid migration logic, keeping pages when src < dest");
    public static final BooleanFlag ENABLE_ONE_SEARCH_MOTION = new DeviceFlag("ENABLE_ONE_SEARCH_MOTION", true, "Enables animations in OneSearch.");
    public static final BooleanFlag ENABLE_OVERVIEW_SELECTIONS = new DeviceFlag("ENABLE_OVERVIEW_SELECTIONS", true, "Show Select Mode button in Overview Actions");
    public static final BooleanFlag ENABLE_OVERVIEW_SHARING_TO_PEOPLE = getDebugFlag("ENABLE_OVERVIEW_SHARING_TO_PEOPLE", true, "Show indicators for content on Overview to share with top people. ");
    public static final BooleanFlag ENABLE_PEOPLE_TILE_PREVIEW = getDebugFlag("ENABLE_PEOPLE_TILE_PREVIEW", false, "Experimental: Shows conversation shortcuts on home screen as search results");
    public static final BooleanFlag ENABLE_PREDICTION_DISMISS = getDebugFlag("ENABLE_PREDICTION_DISMISS", true, "Allow option to dimiss apps from predicted list");
    public static final BooleanFlag ENABLE_QUICKSTEP_LIVE_TILE = getDebugFlag("ENABLE_QUICKSTEP_LIVE_TILE", true, "Enable live tile in Quickstep overview");
    public static final BooleanFlag ENABLE_QUICKSTEP_WIDGET_APP_START = getDebugFlag("ENABLE_QUICKSTEP_WIDGET_APP_START", true, "Enable Quickstep animation when launching activities from an app widget");
    public static final BooleanFlag ENABLE_QUICK_SEARCH = new DeviceFlag("ENABLE_QUICK_SEARCH", true, "Use quick search behavior.");
    public static final BooleanFlag ENABLE_SCRIM_FOR_APP_LAUNCH = getDebugFlag("ENABLE_SCRIM_FOR_APP_LAUNCH", false, "Enables scrim during app launch animation.");
    public static final BooleanFlag ENABLE_SHOW_KEYBOARD_OPTION_IN_ALL_APPS = new DeviceFlag("ENABLE_SHOW_KEYBOARD_OPTION_IN_ALL_APPS", true, "Enable option to show keyboard when going to all-apps");
    public static final BooleanFlag ENABLE_SMARTSPACE_DISMISS = getDebugFlag("ENABLE_SMARTSPACE_DISMISS", true, "Adds a menu option to dismiss the current Enhanced Smartspace card.");
    public static final BooleanFlag ENABLE_SPLIT_FROM_WORKSPACE = getDebugFlag("ENABLE_SPLIT_FROM_WORKSPACE", true, "Enable initiating split screen from workspace.");
    public static final BooleanFlag ENABLE_SPLIT_SELECT = getDebugFlag("ENABLE_SPLIT_SELECT", true, "Uses new split screen selection overview UI");
    public static final BooleanFlag ENABLE_TASKBAR_POPUP_MENU = getDebugFlag("ENABLE_TASKBAR_POPUP_MENU", true, "Enables long pressing taskbar icons to show the popup menu.");
    public static final BooleanFlag ENABLE_THEMED_ICONS = getDebugFlag("ENABLE_THEMED_ICONS", true, "Enable themed icons on workspace");
    public static final BooleanFlag ENABLE_TWOLINE_ALLAPPS = getDebugFlag("ENABLE_TWOLINE_ALLAPPS", false, "Enables two line label inside all apps.");
    public static final BooleanFlag ENABLE_TWO_PANEL_HOME = getDebugFlag("ENABLE_TWO_PANEL_HOME", true, "Uses two panel on home screen. Only applicable on large screen devices.");
    public static final BooleanFlag ENABLE_WALLPAPER_SCRIM = getDebugFlag("ENABLE_WALLPAPER_SCRIM", false, "Enables scrim over wallpaper for text protection.");
    public static final BooleanFlag ENABLE_WIDGETS_PICKER_AIAI_SEARCH = new DeviceFlag("ENABLE_WIDGETS_PICKER_AIAI_SEARCH", true, "Enable AiAi search in the widgets picker");
    public static final BooleanFlag EXPANDED_SMARTSPACE = new DeviceFlag("EXPANDED_SMARTSPACE", false, "Expands smartspace height to two rows. Any apps occupying the first row will be removed from workspace.");
    public static final String FLAGS_PREF_NAME = "featureFlags";
    public static final BooleanFlag FOLDER_NAME_MAJORITY_RANKING = getDebugFlag("FOLDER_NAME_MAJORITY_RANKING", true, "Suggests folder names based on majority based ranking.");
    public static final BooleanFlag FOLDER_NAME_SUGGEST = new DeviceFlag("FOLDER_NAME_SUGGEST", true, "Suggests folder names instead of blank text.");
    public static final BooleanFlag HOTSEAT_MIGRATE_TO_FOLDER = getDebugFlag("HOTSEAT_MIGRATE_TO_FOLDER", false, "Should move hotseat items into a folder");
    public static final BooleanFlag IME_STICKY_SNACKBAR_EDU = getDebugFlag("IME_STICKY_SNACKBAR_EDU", true, "Show sticky IME edu in AllApps");
    public static final boolean IS_STUDIO_BUILD = false;
    public static final BooleanFlag KEYGUARD_ANIMATION = getDebugFlag("KEYGUARD_ANIMATION", false, "Enable animation for keyguard going away on wallpaper");
    public static final BooleanFlag NOTIFY_CRASHES = getDebugFlag("NOTIFY_CRASHES", false, "Sends a notification whenever launcher encounters an uncaught exception.");
    public static final BooleanFlag PROMISE_APPS_IN_ALL_APPS = getDebugFlag("PROMISE_APPS_IN_ALL_APPS", false, "Add promise icon in all-apps");
    public static final BooleanFlag PROMISE_APPS_NEW_INSTALLS = getDebugFlag("PROMISE_APPS_NEW_INSTALLS", true, "Adds a promise icon to the home screen for new install sessions.");
    public static final boolean QSB_ON_FIRST_SCREEN = false;
    public static final BooleanFlag QUICK_WALLPAPER_PICKER = getDebugFlag("QUICK_WALLPAPER_PICKER", true, "Shows quick wallpaper picker in long-press menu");
    public static final BooleanFlag SEPARATE_RECENTS_ACTIVITY = getDebugFlag("SEPARATE_RECENTS_ACTIVITY", false, "Uses a separate recents activity instead of using the integrated recents+Launcher UI");
    public static final BooleanFlag USE_LOCAL_ICON_OVERRIDES = getDebugFlag("USE_LOCAL_ICON_OVERRIDES", true, "Use inbuilt monochrome icons if app doesn't provide one");
    public static final BooleanFlag WIDGETS_IN_LAUNCHER_PREVIEW = getDebugFlag("WIDGETS_IN_LAUNCHER_PREVIEW", true, "Enables widgets in Launcher preview for the Wallpaper app.");
    /* access modifiers changed from: private */
    public static final List<DebugFlag> sDebugFlags = new ArrayList();

    private FeatureFlags() {
    }

    public static boolean showFlagTogglerUi(Context context) {
        return Utilities.IS_DEBUG_DEVICE && Utilities.isDevelopersOptionsEnabled(context);
    }

    public static void initialize(Context context) {
        List<DebugFlag> list = sDebugFlags;
        synchronized (list) {
            for (DebugFlag initialize : list) {
                initialize.initialize(context);
            }
            sDebugFlags.sort($$Lambda$FeatureFlags$6bcXSl1sS2HEXuuZllPO0H4vKew.INSTANCE);
        }
    }

    static List<DebugFlag> getDebugFlags() {
        ArrayList arrayList;
        List<DebugFlag> list = sDebugFlags;
        synchronized (list) {
            arrayList = new ArrayList(list);
        }
        return arrayList;
    }

    public static void dump(PrintWriter printWriter) {
        printWriter.println("DeviceFlags:");
        List<DebugFlag> list = sDebugFlags;
        synchronized (list) {
            for (DebugFlag next : list) {
                if (next instanceof DeviceFlag) {
                    printWriter.println("  " + next.toString());
                }
            }
        }
        printWriter.println("DebugFlags:");
        List<DebugFlag> list2 = sDebugFlags;
        synchronized (list2) {
            for (DebugFlag next2 : list2) {
                if (!(next2 instanceof DeviceFlag)) {
                    printWriter.println("  " + next2.toString());
                }
            }
        }
    }

    public static class BooleanFlag {
        public final boolean defaultValue;
        public final String key;

        public BooleanFlag(String str, boolean z) {
            this.key = str;
            this.defaultValue = z;
        }

        public boolean get() {
            return this.defaultValue;
        }

        public String toString() {
            return appendProps(new StringBuilder()).toString();
        }

        /* access modifiers changed from: protected */
        public StringBuilder appendProps(StringBuilder sb) {
            return sb.append(this.key).append(", defaultValue=").append(this.defaultValue);
        }
    }

    public static class DebugFlag extends BooleanFlag {
        public final String description;
        protected boolean mCurrentValue = this.defaultValue;

        public DebugFlag(String str, boolean z, String str2) {
            super(str, z);
            this.description = str2;
            synchronized (FeatureFlags.sDebugFlags) {
                FeatureFlags.sDebugFlags.add(this);
            }
        }

        public boolean get() {
            return this.mCurrentValue;
        }

        public void initialize(Context context) {
            this.mCurrentValue = context.getSharedPreferences(FeatureFlags.FLAGS_PREF_NAME, 0).getBoolean(this.key, this.defaultValue);
        }

        /* access modifiers changed from: protected */
        public StringBuilder appendProps(StringBuilder sb) {
            return super.appendProps(sb).append(", mCurrentValue=").append(this.mCurrentValue);
        }
    }

    private static BooleanFlag getDebugFlag(String str, boolean z, String str2) {
        if (Utilities.IS_DEBUG_DEVICE) {
            return new DebugFlag(str, z, str2);
        }
        return new BooleanFlag(str, z);
    }
}
