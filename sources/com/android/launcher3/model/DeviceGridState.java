package com.android.launcher3.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.StatsLogManager;
import java.util.Locale;
import java.util.Objects;

public class DeviceGridState implements Comparable<DeviceGridState> {
    public static final String KEY_DB_FILE = "migration_src_db_file";
    public static final String KEY_DEVICE_TYPE = "migration_src_device_type";
    public static final String KEY_HOTSEAT_COUNT = "migration_src_hotseat_count";
    public static final String KEY_WORKSPACE_SIZE = "migration_src_workspace_size";
    private final String mDbFile;
    private final int mDeviceType;
    private final String mGridSizeString;
    private final int mNumHotseat;

    public DeviceGridState(InvariantDeviceProfile invariantDeviceProfile) {
        this.mGridSizeString = String.format(Locale.ENGLISH, "%d,%d", new Object[]{Integer.valueOf(invariantDeviceProfile.numColumns), Integer.valueOf(invariantDeviceProfile.numRows)});
        this.mNumHotseat = invariantDeviceProfile.numDatabaseHotseatIcons;
        this.mDeviceType = invariantDeviceProfile.deviceType;
        this.mDbFile = invariantDeviceProfile.dbFile;
    }

    public DeviceGridState(Context context) {
        SharedPreferences prefs = Utilities.getPrefs(context);
        this.mGridSizeString = prefs.getString(KEY_WORKSPACE_SIZE, "");
        this.mNumHotseat = prefs.getInt(KEY_HOTSEAT_COUNT, -1);
        this.mDeviceType = prefs.getInt(KEY_DEVICE_TYPE, 0);
        this.mDbFile = prefs.getString(KEY_DB_FILE, "");
    }

    public int getDeviceType() {
        return this.mDeviceType;
    }

    public String getDbFile() {
        return this.mDbFile;
    }

    public int getNumHotseat() {
        return this.mNumHotseat;
    }

    public void writeToPrefs(Context context) {
        Utilities.getPrefs(context).edit().putString(KEY_WORKSPACE_SIZE, this.mGridSizeString).putInt(KEY_HOTSEAT_COUNT, this.mNumHotseat).putInt(KEY_DEVICE_TYPE, this.mDeviceType).putString(KEY_DB_FILE, this.mDbFile).apply();
    }

    public StatsLogManager.LauncherEvent getWorkspaceSizeEvent() {
        if (TextUtils.isEmpty(this.mGridSizeString)) {
            return null;
        }
        int intValue = getColumns().intValue();
        if (intValue == 2) {
            return StatsLogManager.LauncherEvent.LAUNCHER_GRID_SIZE_2;
        }
        if (intValue == 3) {
            return StatsLogManager.LauncherEvent.LAUNCHER_GRID_SIZE_3;
        }
        if (intValue == 4) {
            return StatsLogManager.LauncherEvent.LAUNCHER_GRID_SIZE_4;
        }
        if (intValue == 5) {
            return StatsLogManager.LauncherEvent.LAUNCHER_GRID_SIZE_5;
        }
        if (intValue != 6) {
            return null;
        }
        return StatsLogManager.LauncherEvent.LAUNCHER_GRID_SIZE_6;
    }

    public String toString() {
        return "DeviceGridState{mGridSizeString='" + this.mGridSizeString + '\'' + ", mNumHotseat=" + this.mNumHotseat + ", mDeviceType=" + this.mDeviceType + ", mDbFile=" + this.mDbFile + '}';
    }

    public boolean isCompatible(DeviceGridState deviceGridState) {
        if (this == deviceGridState) {
            return true;
        }
        if (deviceGridState == null) {
            return false;
        }
        if (this.mNumHotseat != deviceGridState.mNumHotseat || !Objects.equals(this.mGridSizeString, deviceGridState.mGridSizeString)) {
            return false;
        }
        return true;
    }

    public Integer getColumns() {
        return Integer.valueOf(Integer.parseInt(String.valueOf(this.mGridSizeString.charAt(0))));
    }

    public Integer getRows() {
        return Integer.valueOf(Integer.parseInt(String.valueOf(this.mGridSizeString.charAt(2))));
    }

    public int compareTo(DeviceGridState deviceGridState) {
        return Integer.valueOf(getColumns().intValue() * getRows().intValue()).compareTo(Integer.valueOf(deviceGridState.getColumns().intValue() * deviceGridState.getRows().intValue()));
    }
}
