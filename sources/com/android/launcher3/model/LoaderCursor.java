package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.IconRequestInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSparseArrayMap;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;

public class LoaderCursor extends CursorWrapper {
    private static final String TAG = "LoaderCursor";
    private final LongSparseArray<UserHandle> allUsers;
    private final int cellXIndex;
    private final int cellYIndex;
    public int container;
    private final int containerIndex;
    private final int iconIndex;
    private final int iconPackageIndex;
    private final int iconResourceIndex;
    public int id;
    private final int idIndex;
    private final int intentIndex;
    public int itemType;
    private final int itemTypeIndex;
    private final IntArray itemsToRemove = new IntArray();
    private LauncherActivityInfo mActivityInfo;
    private final Uri mContentUri;
    private final Context mContext;
    private final InvariantDeviceProfile mIDP;
    private final IconCache mIconCache;
    private final PackageManager mPM;
    private final IntSparseArrayMap<GridOccupancy> occupied = new IntSparseArrayMap<>();
    private final int profileIdIndex;
    public int restoreFlag;
    private final int restoredIndex;
    private final IntArray restoredRows = new IntArray();
    private final int screenIndex;
    public long serialNumber;
    public final int titleIndex;
    public UserHandle user;

    public LoaderCursor(Cursor cursor, Uri uri, LauncherAppState launcherAppState, UserManagerState userManagerState) {
        super(cursor);
        this.allUsers = userManagerState.allUsers;
        this.mContentUri = uri;
        Context context = launcherAppState.getContext();
        this.mContext = context;
        this.mIconCache = launcherAppState.getIconCache();
        this.mIDP = launcherAppState.getInvariantDeviceProfile();
        this.mPM = context.getPackageManager();
        this.iconIndex = getColumnIndexOrThrow("icon");
        this.iconPackageIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_PACKAGE);
        this.iconResourceIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.ICON_RESOURCE);
        this.titleIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
        this.idIndex = getColumnIndexOrThrow("_id");
        this.containerIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
        this.itemTypeIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
        this.screenIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
        this.cellXIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
        this.cellYIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
        this.profileIdIndex = getColumnIndexOrThrow("profileId");
        this.restoredIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.RESTORED);
        this.intentIndex = getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
    }

    public boolean moveToNext() {
        boolean moveToNext = super.moveToNext();
        if (moveToNext) {
            this.mActivityInfo = null;
            this.itemType = getInt(this.itemTypeIndex);
            this.container = getInt(this.containerIndex);
            this.id = getInt(this.idIndex);
            long j = (long) getInt(this.profileIdIndex);
            this.serialNumber = j;
            this.user = this.allUsers.get(j);
            this.restoreFlag = getInt(this.restoredIndex);
        }
        return moveToNext;
    }

    public Intent parseIntent() {
        String string = getString(this.intentIndex);
        try {
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            return Intent.parseUri(string, 0);
        } catch (URISyntaxException unused) {
            Log.e(TAG, "Error parsing Intent");
            return null;
        }
    }

    public WorkspaceItemInfo loadSimpleWorkspaceItem() {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.intent = new Intent();
        workspaceItemInfo.user = this.user;
        workspaceItemInfo.itemType = this.itemType;
        workspaceItemInfo.title = getTitle();
        if (!loadIcon(workspaceItemInfo)) {
            workspaceItemInfo.bitmap = this.mIconCache.getDefaultIcon(workspaceItemInfo.user);
        }
        return workspaceItemInfo;
    }

    /* access modifiers changed from: protected */
    public boolean loadIcon(WorkspaceItemInfo workspaceItemInfo) {
        return createIconRequestInfo(workspaceItemInfo, false).loadWorkspaceIcon(this.mContext);
    }

    public IconRequestInfo<WorkspaceItemInfo> createIconRequestInfo(WorkspaceItemInfo workspaceItemInfo, boolean z) {
        byte[] bArr = null;
        String string = this.itemType == 1 ? getString(this.iconPackageIndex) : null;
        String string2 = this.itemType == 1 ? getString(this.iconResourceIndex) : null;
        int i = this.itemType;
        if (i == 1 || i == 6 || this.restoreFlag != 0) {
            bArr = getBlob(this.iconIndex);
        }
        return new IconRequestInfo(workspaceItemInfo, this.mActivityInfo, string, string2, bArr, z);
    }

    private String getTitle() {
        return Utilities.trim(getString(this.titleIndex));
    }

    public WorkspaceItemInfo getRestoredItemInfo(Intent intent) {
        WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
        workspaceItemInfo.user = this.user;
        workspaceItemInfo.intent = intent;
        if (!loadIcon(workspaceItemInfo)) {
            this.mIconCache.getTitleAndIcon(workspaceItemInfo, false);
        }
        if (hasRestoreFlag(1)) {
            String title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                workspaceItemInfo.title = Utilities.trim(title);
            }
        } else if (!hasRestoreFlag(2)) {
            throw new InvalidParameterException("Invalid restoreType " + this.restoreFlag);
        } else if (TextUtils.isEmpty(workspaceItemInfo.title)) {
            workspaceItemInfo.title = getTitle();
        }
        workspaceItemInfo.contentDescription = this.mPM.getUserBadgedLabel(workspaceItemInfo.title, workspaceItemInfo.user);
        workspaceItemInfo.itemType = this.itemType;
        workspaceItemInfo.status = this.restoreFlag;
        return workspaceItemInfo;
    }

    public LauncherActivityInfo getLauncherActivityInfo() {
        return this.mActivityInfo;
    }

    public WorkspaceItemInfo getAppShortcutInfo(Intent intent, boolean z, boolean z2) {
        return getAppShortcutInfo(intent, z, z2, true);
    }

    public WorkspaceItemInfo getAppShortcutInfo(Intent intent, boolean z, boolean z2, boolean z3) {
        if (this.user == null) {
            Log.d(TAG, "Null user found in getShortcutInfo");
            return null;
        }
        ComponentName component = intent.getComponent();
        if (component == null) {
            Log.d(TAG, "Missing component found in getShortcutInfo");
            return null;
        }
        Intent intent2 = new Intent("android.intent.action.MAIN", (Uri) null);
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setComponent(component);
        LauncherActivityInfo resolveActivity = ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).resolveActivity(intent2, this.user);
        this.mActivityInfo = resolveActivity;
        if (resolveActivity != null || z) {
            WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo();
            workspaceItemInfo.itemType = 0;
            workspaceItemInfo.user = this.user;
            workspaceItemInfo.intent = intent2;
            if (z3) {
                this.mIconCache.getTitleAndIcon(workspaceItemInfo, this.mActivityInfo, z2);
                if (this.mIconCache.isDefaultIcon(workspaceItemInfo.bitmap, this.user)) {
                    loadIcon(workspaceItemInfo);
                }
            }
            LauncherActivityInfo launcherActivityInfo = this.mActivityInfo;
            if (launcherActivityInfo != null) {
                AppInfo.updateRuntimeFlagsForActivityTarget(workspaceItemInfo, launcherActivityInfo);
            }
            if (TextUtils.isEmpty(workspaceItemInfo.title)) {
                workspaceItemInfo.title = getTitle();
            }
            if (workspaceItemInfo.title == null) {
                workspaceItemInfo.title = component.getClassName();
            }
            workspaceItemInfo.contentDescription = this.mPM.getUserBadgedLabel(workspaceItemInfo.title, workspaceItemInfo.user);
            return workspaceItemInfo;
        }
        Log.d(TAG, "Missing activity found in getShortcutInfo: " + component);
        return null;
    }

    public ContentWriter updater() {
        return new ContentWriter(this.mContext, new ContentWriter.CommitParams("_id= ?", new String[]{Integer.toString(this.id)}));
    }

    public void markDeleted(String str) {
        FileLog.e(TAG, str);
        this.itemsToRemove.add(this.id);
    }

    public boolean commitDeleted() {
        if (this.itemsToRemove.size() <= 0) {
            return false;
        }
        this.mContext.getContentResolver().delete(this.mContentUri, Utilities.createDbSelectionQuery("_id", this.itemsToRemove), (String[]) null);
        return true;
    }

    public void markRestored() {
        if (this.restoreFlag != 0) {
            this.restoredRows.add(this.id);
            this.restoreFlag = 0;
        }
    }

    public boolean hasRestoreFlag(int i) {
        return (i & this.restoreFlag) != 0;
    }

    public void commitRestoredItems() {
        if (this.restoredRows.size() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(LauncherSettings.Favorites.RESTORED, 0);
            this.mContext.getContentResolver().update(this.mContentUri, contentValues, Utilities.createDbSelectionQuery("_id", this.restoredRows), (String[]) null);
        }
    }

    public boolean isOnWorkspaceOrHotseat() {
        int i = this.container;
        return i == -100 || i == -101;
    }

    public void applyCommonProperties(ItemInfo itemInfo) {
        itemInfo.id = this.id;
        itemInfo.container = this.container;
        itemInfo.screenId = getInt(this.screenIndex);
        itemInfo.cellX = getInt(this.cellXIndex);
        itemInfo.cellY = getInt(this.cellYIndex);
    }

    public void checkAndAddItem(ItemInfo itemInfo, BgDataModel bgDataModel) {
        checkAndAddItem(itemInfo, bgDataModel, (LoaderMemoryLogger) null);
    }

    public void checkAndAddItem(ItemInfo itemInfo, BgDataModel bgDataModel, LoaderMemoryLogger loaderMemoryLogger) {
        if (itemInfo.itemType == 6) {
            ShortcutKey.fromItemInfo(itemInfo);
        }
        if (checkItemPlacement(itemInfo)) {
            bgDataModel.addItem(this.mContext, itemInfo, false, loaderMemoryLogger);
        } else {
            markDeleted("Item position overlap");
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkItemPlacement(ItemInfo itemInfo) {
        ItemInfo itemInfo2 = itemInfo;
        int i = itemInfo2.screenId;
        if (itemInfo2.container == -101) {
            GridOccupancy gridOccupancy = (GridOccupancy) this.occupied.get(LauncherSettings.Favorites.CONTAINER_HOTSEAT);
            if (itemInfo2.screenId >= this.mIDP.numDatabaseHotseatIcons) {
                Log.e(TAG, "Error loading shortcut " + itemInfo2 + " into hotseat position " + itemInfo2.screenId + ", position out of bounds: (0 to " + (this.mIDP.numDatabaseHotseatIcons - 1) + ")");
                return false;
            } else if (gridOccupancy == null) {
                GridOccupancy gridOccupancy2 = new GridOccupancy(this.mIDP.numDatabaseHotseatIcons, 1);
                gridOccupancy2.cells[itemInfo2.screenId][0] = true;
                this.occupied.put(LauncherSettings.Favorites.CONTAINER_HOTSEAT, gridOccupancy2);
                return true;
            } else if (gridOccupancy.cells[itemInfo2.screenId][0]) {
                Log.e(TAG, "Error loading shortcut into hotseat " + itemInfo2 + " into position (" + itemInfo2.screenId + ":" + itemInfo2.cellX + "," + itemInfo2.cellY + ") already occupied");
                return false;
            } else {
                gridOccupancy.cells[itemInfo2.screenId][0] = true;
                return true;
            }
        } else if (itemInfo2.container != -100) {
            return true;
        } else {
            int i2 = this.mIDP.numColumns;
            int i3 = this.mIDP.numRows;
            if ((itemInfo2.container != -100 || itemInfo2.cellX >= 0) && itemInfo2.cellY >= 0 && itemInfo2.cellX + itemInfo2.spanX <= i2 && itemInfo2.cellY + itemInfo2.spanY <= i3) {
                if (!this.occupied.containsKey(itemInfo2.screenId)) {
                    int i4 = i2 + 1;
                    GridOccupancy gridOccupancy3 = new GridOccupancy(i4, i3 + 1);
                    if (itemInfo2.screenId == 0) {
                        gridOccupancy3.markCells(0, 0, i4, FeatureFlags.EXPANDED_SMARTSPACE.get() ? 2 : 1, false);
                    }
                    this.occupied.put(itemInfo2.screenId, gridOccupancy3);
                }
                GridOccupancy gridOccupancy4 = (GridOccupancy) this.occupied.get(itemInfo2.screenId);
                if (gridOccupancy4.isRegionVacant(itemInfo2.cellX, itemInfo2.cellY, itemInfo2.spanX, itemInfo2.spanY)) {
                    gridOccupancy4.markCells(itemInfo2, true);
                    return true;
                }
                Log.e(TAG, "Error loading shortcut " + itemInfo2 + " into cell (" + i + "-" + itemInfo2.screenId + ":" + itemInfo2.cellX + "," + itemInfo2.cellX + "," + itemInfo2.spanX + "," + itemInfo2.spanY + ") already occupied");
                return false;
            }
            Log.e(TAG, "Error loading shortcut " + itemInfo2 + " into cell (" + i + "-" + itemInfo2.screenId + ":" + itemInfo2.cellX + "," + itemInfo2.cellY + ") out of screen bounds ( " + i2 + "x" + i3 + ")");
            return false;
        }
    }
}
