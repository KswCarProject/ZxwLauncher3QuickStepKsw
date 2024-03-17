package com.android.launcher3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LauncherFiles {
    public static final List<String> ALL_FILES = Collections.unmodifiableList(new ArrayList<String>() {
        {
            addAll(LauncherFiles.GRID_DB_FILES);
            addAll(LauncherFiles.OTHER_FILES);
        }
    });
    public static final String APP_ICONS_DB = "app_icons.db";
    public static final String BACKUP_DB = "backup.db";
    public static final String DEVICE_PREFERENCES_KEY = "com.android.launcher3.device.prefs";
    public static final List<String> GRID_DB_FILES = Collections.unmodifiableList(Arrays.asList(new String[]{LAUNCHER_DB, LAUNCHER_6_BY_5_DB, LAUNCHER_4_BY_5_DB, LAUNCHER_4_BY_4_DB, LAUNCHER_3_BY_3_DB, LAUNCHER_2_BY_2_DB}));
    public static final String LAUNCHER_2_BY_2_DB = "launcher_2_by_2.db";
    public static final String LAUNCHER_3_BY_3_DB = "launcher_3_by_3.db";
    public static final String LAUNCHER_4_BY_4_DB = "launcher_4_by_4.db";
    public static final String LAUNCHER_4_BY_5_DB = "launcher_4_by_5.db";
    public static final String LAUNCHER_6_BY_5_DB = "launcher_6_by_5.db";
    public static final String LAUNCHER_DB = "launcher.db";
    public static final String MANAGED_USER_PREFERENCES_KEY = "com.android.launcher3.managedusers.prefs";
    public static final List<String> OTHER_FILES = Collections.unmodifiableList(Arrays.asList(new String[]{BACKUP_DB, "com.android.launcher3.prefs.xml", WIDGET_PREVIEWS_DB, "com.android.launcher3.managedusers.prefs.xml", "com.android.launcher3.device.prefs.xml", APP_ICONS_DB}));
    public static final String SHARED_PREFERENCES_KEY = "com.android.launcher3.prefs";
    public static final String WIDGET_PREVIEWS_DB = "widgetpreviews.db";
    private static final String XML = ".xml";
}
