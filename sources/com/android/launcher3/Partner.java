package com.android.launcher3;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.util.PackageManagerHelper;
import java.io.File;

public class Partner {
    private static final String ACTION_PARTNER_CUSTOMIZATION = "com.android.launcher3.action.PARTNER_CUSTOMIZATION";
    public static final String RES_DEFAULT_LAYOUT = "partner_default_layout";
    public static final String RES_DEFAULT_WALLPAPER_HIDDEN = "default_wallpapper_hidden";
    public static final String RES_FOLDER = "partner_folder";
    public static final String RES_GRID_ICON_SIZE_DP = "grid_icon_size_dp";
    public static final String RES_GRID_NUM_COLUMNS = "grid_num_columns";
    public static final String RES_GRID_NUM_ROWS = "grid_num_rows";
    public static final String RES_REQUIRE_FIRST_RUN_FLOW = "requires_first_run_flow";
    public static final String RES_SYSTEM_WALLPAPER_DIR = "system_wallpaper_directory";
    public static final String RES_WALLPAPERS = "partner_wallpapers";
    static final String TAG = "Launcher.Partner";
    private final String mPackageName;
    private final Resources mResources;

    public static synchronized Partner get(PackageManager packageManager) {
        Partner partner;
        synchronized (Partner.class) {
            Pair<String, Resources> findSystemApk = PackageManagerHelper.findSystemApk(ACTION_PARTNER_CUSTOMIZATION, packageManager);
            partner = findSystemApk != null ? new Partner((String) findSystemApk.first, (Resources) findSystemApk.second) : null;
        }
        return partner;
    }

    private Partner(String str, Resources resources) {
        this.mPackageName = str;
        this.mResources = resources;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public Resources getResources() {
        return this.mResources;
    }

    public boolean hasDefaultLayout() {
        return getResources().getIdentifier(RES_DEFAULT_LAYOUT, "xml", getPackageName()) != 0;
    }

    public boolean hasFolder() {
        return getResources().getIdentifier(RES_FOLDER, "xml", getPackageName()) != 0;
    }

    public boolean hideDefaultWallpaper() {
        int identifier = getResources().getIdentifier(RES_DEFAULT_WALLPAPER_HIDDEN, "bool", getPackageName());
        return identifier != 0 && getResources().getBoolean(identifier);
    }

    public File getWallpaperDirectory() {
        int identifier = getResources().getIdentifier(RES_SYSTEM_WALLPAPER_DIR, "string", getPackageName());
        if (identifier != 0) {
            return new File(getResources().getString(identifier));
        }
        return null;
    }

    public boolean requiresFirstRunFlow() {
        int identifier = getResources().getIdentifier(RES_REQUIRE_FIRST_RUN_FLOW, "bool", getPackageName());
        return identifier != 0 && getResources().getBoolean(identifier);
    }

    public void applyInvariantDeviceProfileOverrides(InvariantDeviceProfile invariantDeviceProfile, DisplayMetrics displayMetrics) {
        try {
            int identifier = getResources().getIdentifier(RES_GRID_NUM_ROWS, "integer", getPackageName());
            int i = -1;
            int integer = identifier > 0 ? getResources().getInteger(identifier) : -1;
            int identifier2 = getResources().getIdentifier(RES_GRID_NUM_COLUMNS, "integer", getPackageName());
            if (identifier2 > 0) {
                i = getResources().getInteger(identifier2);
            }
            int identifier3 = getResources().getIdentifier(RES_GRID_ICON_SIZE_DP, "dimen", getPackageName());
            float dpiFromPx = identifier3 > 0 ? Utilities.dpiFromPx((float) getResources().getDimensionPixelSize(identifier3), displayMetrics.densityDpi) : -1.0f;
            if (integer > 0 && i > 0) {
                invariantDeviceProfile.numRows = integer;
                invariantDeviceProfile.numColumns = i;
            }
            if (dpiFromPx > 0.0f) {
                invariantDeviceProfile.iconSize[0] = dpiFromPx;
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Invalid Partner grid resource!", e);
        }
    }
}
