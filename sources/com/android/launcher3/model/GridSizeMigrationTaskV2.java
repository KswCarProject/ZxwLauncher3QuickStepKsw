package com.android.launcher3.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.LauncherPreviewRenderer;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class GridSizeMigrationTaskV2 {
    private static final boolean DEBUG = false;
    private static final String TAG = "GridSizeMigrationTaskV2";
    private final Context mContext;
    private final SQLiteDatabase mDb;
    private final int mDestHotseatSize;
    private final DbReader mDestReader;
    private final List<DbEntry> mHotseatDiff;
    private final List<DbEntry> mHotseatItems;
    private final DbReader mSrcReader;
    private final int mTrgX;
    private final int mTrgY;
    private final List<DbEntry> mWorkspaceDiff;
    private final List<DbEntry> mWorkspaceItems;

    protected GridSizeMigrationTaskV2(Context context, SQLiteDatabase sQLiteDatabase, DbReader dbReader, DbReader dbReader2, int i, Point point) {
        this.mContext = context;
        this.mDb = sQLiteDatabase;
        this.mSrcReader = dbReader;
        this.mDestReader = dbReader2;
        ArrayList<DbEntry> loadHotseatEntries = dbReader2.loadHotseatEntries();
        this.mHotseatItems = loadHotseatEntries;
        ArrayList<DbEntry> loadAllWorkspaceEntries = dbReader2.loadAllWorkspaceEntries();
        this.mWorkspaceItems = loadAllWorkspaceEntries;
        this.mHotseatDiff = calcDiff(dbReader.loadHotseatEntries(), loadHotseatEntries);
        this.mWorkspaceDiff = calcDiff(dbReader.loadAllWorkspaceEntries(), loadAllWorkspaceEntries);
        this.mDestHotseatSize = i;
        this.mTrgX = point.x;
        this.mTrgY = point.y;
    }

    public static boolean needsToMigrate(Context context, InvariantDeviceProfile invariantDeviceProfile) {
        return needsToMigrate(new DeviceGridState(context), new DeviceGridState(invariantDeviceProfile));
    }

    private static boolean needsToMigrate(DeviceGridState deviceGridState, DeviceGridState deviceGridState2) {
        boolean z = !deviceGridState2.isCompatible(deviceGridState);
        if (z) {
            Log.d("b/198965093", "Migration is needed. destDeviceState: " + deviceGridState2 + ", srcDeviceState: " + deviceGridState);
        }
        return z;
    }

    public static boolean migrateGridIfNeeded(Context context) {
        if (context instanceof LauncherPreviewRenderer.PreviewContext) {
            return true;
        }
        return migrateGridIfNeeded(context, (InvariantDeviceProfile) null);
    }

    public static boolean migrateGridIfNeeded(Context context, InvariantDeviceProfile invariantDeviceProfile) {
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction;
        Throwable th;
        Context context2 = context;
        boolean z = invariantDeviceProfile != null;
        InvariantDeviceProfile idp = !z ? LauncherAppState.getIDP(context) : invariantDeviceProfile;
        DeviceGridState deviceGridState = new DeviceGridState(context2);
        DeviceGridState deviceGridState2 = new DeviceGridState(idp);
        if (!needsToMigrate(deviceGridState, deviceGridState2)) {
            return true;
        }
        HashSet<String> validPackages = getValidPackages(context);
        if (z) {
            if (!LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_PREP_FOR_PREVIEW, deviceGridState2.getDbFile()).getBoolean("value")) {
                return false;
            }
        } else if (!LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_UPDATE_CURRENT_OPEN_HELPER, deviceGridState2.getDbFile()).getBoolean("value")) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        try {
            sQLiteTransaction = (LauncherDbUtils.SQLiteTransaction) LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_TRANSACTION).getBinder("value");
            SQLiteDatabase db = sQLiteTransaction.getDb();
            String str = LauncherSettings.Favorites.TABLE_NAME;
            DbReader dbReader = new DbReader(db, z ? str : LauncherSettings.Favorites.TMP_TABLE, context2, validPackages);
            SQLiteDatabase db2 = sQLiteTransaction.getDb();
            if (z) {
                str = LauncherSettings.Favorites.PREVIEW_TABLE_NAME;
            }
            DbReader dbReader2 = new DbReader(db2, str, context2, validPackages);
            Point point = new Point(deviceGridState2.getColumns().intValue(), deviceGridState2.getRows().intValue());
            GridSizeMigrationTaskV2 gridSizeMigrationTaskV2 = r1;
            String str2 = LauncherSettings.Favorites.TMP_TABLE;
            GridSizeMigrationTaskV2 gridSizeMigrationTaskV22 = new GridSizeMigrationTaskV2(context, sQLiteTransaction.getDb(), dbReader, dbReader2, deviceGridState2.getNumHotseat(), point);
            gridSizeMigrationTaskV2.migrate(deviceGridState, deviceGridState2);
            if (!z) {
                LauncherDbUtils.dropTable(sQLiteTransaction.getDb(), str2);
            }
            sQLiteTransaction.commit();
            if (sQLiteTransaction != null) {
                sQLiteTransaction.close();
            }
            Log.v(TAG, "Workspace migration completed in " + (System.currentTimeMillis() - currentTimeMillis));
            if (z) {
                return true;
            }
            deviceGridState2.writeToPrefs(context2);
            return true;
        } catch (Exception e) {
            try {
                Log.e(TAG, "Error during grid migration", e);
            } finally {
                Log.v(TAG, "Workspace migration completed in " + (System.currentTimeMillis() - currentTimeMillis));
                if (!z) {
                    deviceGridState2.writeToPrefs(context2);
                }
            }
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
        throw th;
    }

    /* access modifiers changed from: protected */
    public boolean migrate(DeviceGridState deviceGridState, DeviceGridState deviceGridState2) {
        boolean z = false;
        if (this.mHotseatDiff.isEmpty() && this.mWorkspaceDiff.isEmpty()) {
            return false;
        }
        Collections.sort(this.mHotseatDiff);
        Collections.sort(this.mWorkspaceDiff);
        new HotseatPlacementSolution(this.mDb, this.mSrcReader, this.mDestReader, this.mContext, this.mDestHotseatSize, this.mHotseatItems, this.mHotseatDiff).find();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i <= this.mDestReader.mLastScreenId; i++) {
            arrayList.add(Integer.valueOf(i));
        }
        if (arrayList.isEmpty() && FeatureFlags.ENABLE_NEW_MIGRATION_LOGIC.get()) {
            if (deviceGridState2.compareTo(deviceGridState) >= 0 && deviceGridState2.getColumns().intValue() - deviceGridState.getColumns().intValue() <= 2) {
                z = true;
            }
        }
        for (Integer intValue : arrayList) {
            new GridPlacementSolution(this.mDb, this.mSrcReader, this.mDestReader, this.mContext, intValue.intValue(), this.mTrgX, this.mTrgY, this.mWorkspaceDiff, false).find();
            if (this.mWorkspaceDiff.isEmpty()) {
                break;
            }
        }
        int access$000 = this.mDestReader.mLastScreenId;
        while (true) {
            access$000++;
            if (this.mWorkspaceDiff.isEmpty()) {
                return true;
            }
            new GridPlacementSolution(this.mDb, this.mSrcReader, this.mDestReader, this.mContext, access$000, this.mTrgX, this.mTrgY, this.mWorkspaceDiff, z).find();
        }
    }

    private static List<DbEntry> calcDiff(List<DbEntry> list, List<DbEntry> list2) {
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        for (DbEntry next : list2) {
            if (next.itemType == 2) {
                hashSet2.add(getFolderIntents(next));
            } else {
                hashSet.add(next.mIntent);
            }
        }
        ArrayList arrayList = new ArrayList();
        for (DbEntry next2 : list) {
            if (next2.itemType == 2) {
                if (!hashSet2.contains(getFolderIntents(next2))) {
                    arrayList.add(next2);
                }
            } else if (!hashSet.contains(next2.mIntent)) {
                arrayList.add(next2);
            }
        }
        return arrayList;
    }

    private static Map<String, Integer> getFolderIntents(DbEntry dbEntry) {
        HashMap hashMap = new HashMap();
        for (String str : dbEntry.mFolderItems.keySet()) {
            hashMap.put(str, Integer.valueOf(((Set) dbEntry.mFolderItems.get(str)).size()));
        }
        return hashMap;
    }

    /* access modifiers changed from: private */
    public static void insertEntryInDb(SQLiteDatabase sQLiteDatabase, Context context, DbEntry dbEntry, String str, String str2) {
        int copyEntryAndUpdate = copyEntryAndUpdate(sQLiteDatabase, context, dbEntry, str, str2);
        if (dbEntry.itemType == 2) {
            for (Set<Integer> it : dbEntry.mFolderItems.values()) {
                for (Integer intValue : it) {
                    copyEntryAndUpdate(sQLiteDatabase, context, intValue.intValue(), copyEntryAndUpdate, str, str2);
                }
            }
        }
    }

    private static int copyEntryAndUpdate(SQLiteDatabase sQLiteDatabase, Context context, DbEntry dbEntry, String str, String str2) {
        return copyEntryAndUpdate(sQLiteDatabase, context, dbEntry, -1, -1, str, str2);
    }

    private static int copyEntryAndUpdate(SQLiteDatabase sQLiteDatabase, Context context, int i, int i2, String str, String str2) {
        return copyEntryAndUpdate(sQLiteDatabase, context, (DbEntry) null, i, i2, str, str2);
    }

    private static int copyEntryAndUpdate(SQLiteDatabase sQLiteDatabase, Context context, DbEntry dbEntry, int i, int i2, String str, String str2) {
        StringBuilder append = new StringBuilder().append("_id = '");
        if (dbEntry != null) {
            i = dbEntry.id;
        }
        Cursor query = sQLiteDatabase.query(str, (String[]) null, append.append(i).append("'").toString(), (String[]) null, (String) null, (String) null, (String) null);
        int i3 = -1;
        while (query.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorRowToContentValues(query, contentValues);
            if (dbEntry != null) {
                dbEntry.updateContentValues(contentValues);
            } else {
                contentValues.put(LauncherSettings.Favorites.CONTAINER, Integer.valueOf(i2));
            }
            int i4 = LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getInt("value");
            contentValues.put("_id", Integer.valueOf(i4));
            sQLiteDatabase.insert(str2, (String) null, contentValues);
            i3 = i4;
        }
        query.close();
        return i3;
    }

    /* access modifiers changed from: private */
    public static void removeEntryFromDb(SQLiteDatabase sQLiteDatabase, String str, IntArray intArray) {
        sQLiteDatabase.delete(str, Utilities.createDbSelectionQuery("_id", intArray), (String[]) null);
    }

    private static HashSet<String> getValidPackages(Context context) {
        HashSet<String> hashSet = new HashSet<>();
        for (PackageInfo packageInfo : context.getPackageManager().getInstalledPackages(8192)) {
            hashSet.add(packageInfo.packageName);
        }
        InstallSessionHelper.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getActiveSessions().keySet().forEach(new Consumer(hashSet) {
            public final /* synthetic */ HashSet f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                this.f$0.add(((PackageUserKey) obj).mPackageName);
            }
        });
        return hashSet;
    }

    protected static class GridPlacementSolution {
        private final Context mContext;
        private final SQLiteDatabase mDb;
        private final DbReader mDestReader;
        private final boolean mMatchingScreenIdOnly;
        private int mNextStartX = 0;
        private int mNextStartY = 0;
        private final GridOccupancy mOccupied;
        private final int mScreenId;
        private final List<DbEntry> mSortedItemsToPlace;
        private final DbReader mSrcReader;
        private final int mTrgX;
        private final int mTrgY;

        GridPlacementSolution(SQLiteDatabase sQLiteDatabase, DbReader dbReader, DbReader dbReader2, Context context, int i, int i2, int i3, List<DbEntry> list, boolean z) {
            this.mDb = sQLiteDatabase;
            this.mSrcReader = dbReader;
            this.mDestReader = dbReader2;
            this.mContext = context;
            this.mOccupied = new GridOccupancy(i2, i3);
            this.mScreenId = i;
            this.mTrgX = i2;
            this.mTrgY = i3;
            List<DbEntry> list2 = (List) dbReader2.mWorkspaceEntriesByScreenId.get(Integer.valueOf(i));
            if (list2 != null) {
                for (DbEntry markCells : list2) {
                    this.mOccupied.markCells((ItemInfo) markCells, true);
                }
            }
            this.mSortedItemsToPlace = list;
            this.mMatchingScreenIdOnly = z;
        }

        public void find() {
            Iterator<DbEntry> it = this.mSortedItemsToPlace.iterator();
            while (it.hasNext()) {
                DbEntry next = it.next();
                if (!this.mMatchingScreenIdOnly || next.screenId >= this.mScreenId) {
                    if (this.mMatchingScreenIdOnly && next.screenId > this.mScreenId) {
                        return;
                    }
                    if (next.minSpanX > this.mTrgX || next.minSpanY > this.mTrgY) {
                        it.remove();
                    } else if (findPlacement(next)) {
                        GridSizeMigrationTaskV2.insertEntryInDb(this.mDb, this.mContext, next, this.mSrcReader.mTableName, this.mDestReader.mTableName);
                        it.remove();
                    }
                }
            }
        }

        private boolean findPlacement(DbEntry dbEntry) {
            for (int i = this.mNextStartY; i < this.mTrgY; i++) {
                for (int i2 = this.mNextStartX; i2 < this.mTrgX; i2++) {
                    boolean isRegionVacant = this.mOccupied.isRegionVacant(i2, i, dbEntry.spanX, dbEntry.spanY);
                    boolean isRegionVacant2 = this.mOccupied.isRegionVacant(i2, i, dbEntry.minSpanX, dbEntry.minSpanY);
                    if (isRegionVacant2) {
                        dbEntry.spanX = dbEntry.minSpanX;
                        dbEntry.spanY = dbEntry.minSpanY;
                    }
                    if (isRegionVacant || isRegionVacant2) {
                        dbEntry.screenId = this.mScreenId;
                        dbEntry.cellX = i2;
                        dbEntry.cellY = i;
                        this.mOccupied.markCells((ItemInfo) dbEntry, true);
                        this.mNextStartX = i2 + dbEntry.spanX;
                        this.mNextStartY = i;
                        return true;
                    }
                }
                this.mNextStartX = 0;
            }
            return false;
        }
    }

    protected static class HotseatPlacementSolution {
        private final Context mContext;
        private final SQLiteDatabase mDb;
        private final DbReader mDestReader;
        private final List<DbEntry> mItemsToPlace;
        private final HotseatOccupancy mOccupied;
        private final DbReader mSrcReader;

        HotseatPlacementSolution(SQLiteDatabase sQLiteDatabase, DbReader dbReader, DbReader dbReader2, Context context, int i, List<DbEntry> list, List<DbEntry> list2) {
            this.mDb = sQLiteDatabase;
            this.mSrcReader = dbReader;
            this.mDestReader = dbReader2;
            this.mContext = context;
            this.mOccupied = new HotseatOccupancy(i);
            for (DbEntry access$700 : list) {
                this.mOccupied.markCells(access$700, true);
            }
            this.mItemsToPlace = list2;
        }

        public void find() {
            for (int i = 0; i < this.mOccupied.mCells.length; i++) {
                if (!this.mOccupied.mCells[i] && !this.mItemsToPlace.isEmpty()) {
                    DbEntry remove = this.mItemsToPlace.remove(0);
                    remove.screenId = i;
                    remove.cellX = i;
                    remove.cellY = 0;
                    GridSizeMigrationTaskV2.insertEntryInDb(this.mDb, this.mContext, remove, this.mSrcReader.mTableName, this.mDestReader.mTableName);
                    this.mOccupied.markCells(remove, true);
                }
            }
        }

        private class HotseatOccupancy {
            /* access modifiers changed from: private */
            public final boolean[] mCells;

            private HotseatOccupancy(int i) {
                this.mCells = new boolean[i];
            }

            /* access modifiers changed from: private */
            public void markCells(ItemInfo itemInfo, boolean z) {
                this.mCells[itemInfo.screenId] = z;
            }
        }
    }

    protected static class DbReader {
        private final Context mContext;
        private final SQLiteDatabase mDb;
        private final ArrayList<DbEntry> mHotseatEntries = new ArrayList<>();
        /* access modifiers changed from: private */
        public int mLastScreenId = -1;
        /* access modifiers changed from: private */
        public final String mTableName;
        private final Set<String> mValidPackages;
        private final ArrayList<DbEntry> mWorkspaceEntries = new ArrayList<>();
        /* access modifiers changed from: private */
        public final Map<Integer, ArrayList<DbEntry>> mWorkspaceEntriesByScreenId = new ArrayMap();

        DbReader(SQLiteDatabase sQLiteDatabase, String str, Context context, Set<String> set) {
            this.mDb = sQLiteDatabase;
            this.mTableName = str;
            this.mContext = context;
            this.mValidPackages = set;
        }

        /* access modifiers changed from: protected */
        public ArrayList<DbEntry> loadHotseatEntries() {
            Cursor queryWorkspace = queryWorkspace(new String[]{"_id", LauncherSettings.Favorites.ITEM_TYPE, LauncherSettings.Favorites.INTENT, LauncherSettings.Favorites.SCREEN}, "container = -101");
            int columnIndexOrThrow = queryWorkspace.getColumnIndexOrThrow("_id");
            int columnIndexOrThrow2 = queryWorkspace.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
            int columnIndexOrThrow3 = queryWorkspace.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
            int columnIndexOrThrow4 = queryWorkspace.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
            IntArray intArray = new IntArray();
            while (queryWorkspace.moveToNext()) {
                DbEntry dbEntry = new DbEntry();
                dbEntry.id = queryWorkspace.getInt(columnIndexOrThrow);
                dbEntry.itemType = queryWorkspace.getInt(columnIndexOrThrow2);
                dbEntry.screenId = queryWorkspace.getInt(columnIndexOrThrow4);
                try {
                    int i = dbEntry.itemType;
                    if (!(i == 0 || i == 1)) {
                        if (i != 2) {
                            if (i != 6) {
                                throw new Exception("Invalid item type");
                            }
                        } else if (getFolderItemsCount(dbEntry) != 0) {
                            this.mHotseatEntries.add(dbEntry);
                        } else {
                            throw new Exception("Folder is empty");
                        }
                    }
                    String unused = dbEntry.mIntent = queryWorkspace.getString(columnIndexOrThrow3);
                    verifyIntent(queryWorkspace.getString(columnIndexOrThrow3));
                    this.mHotseatEntries.add(dbEntry);
                } catch (Exception unused2) {
                    intArray.add(dbEntry.id);
                }
            }
            GridSizeMigrationTaskV2.removeEntryFromDb(this.mDb, this.mTableName, intArray);
            queryWorkspace.close();
            return this.mHotseatEntries;
        }

        /* access modifiers changed from: protected */
        public ArrayList<DbEntry> loadAllWorkspaceEntries() {
            return loadWorkspaceEntries(queryWorkspace(new String[]{"_id", LauncherSettings.Favorites.ITEM_TYPE, LauncherSettings.Favorites.SCREEN, LauncherSettings.Favorites.CELLX, LauncherSettings.Favorites.CELLY, LauncherSettings.Favorites.SPANX, LauncherSettings.Favorites.SPANY, LauncherSettings.Favorites.INTENT, LauncherSettings.Favorites.APPWIDGET_PROVIDER, LauncherSettings.Favorites.APPWIDGET_ID}, "container = -100"));
        }

        /* JADX WARNING: Removed duplicated region for block: B:40:0x011a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.ArrayList<com.android.launcher3.model.GridSizeMigrationTaskV2.DbEntry> loadWorkspaceEntries(android.database.Cursor r18) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                java.lang.String r2 = "_id"
                int r2 = r1.getColumnIndexOrThrow(r2)
                java.lang.String r3 = "itemType"
                int r3 = r1.getColumnIndexOrThrow(r3)
                java.lang.String r4 = "screen"
                int r4 = r1.getColumnIndexOrThrow(r4)
                java.lang.String r5 = "cellX"
                int r5 = r1.getColumnIndexOrThrow(r5)
                java.lang.String r6 = "cellY"
                int r6 = r1.getColumnIndexOrThrow(r6)
                java.lang.String r7 = "spanX"
                int r7 = r1.getColumnIndexOrThrow(r7)
                java.lang.String r8 = "spanY"
                int r8 = r1.getColumnIndexOrThrow(r8)
                java.lang.String r9 = "intent"
                int r9 = r1.getColumnIndexOrThrow(r9)
                java.lang.String r10 = "appWidgetProvider"
                int r10 = r1.getColumnIndexOrThrow(r10)
                java.lang.String r11 = "appWidgetId"
                int r11 = r1.getColumnIndexOrThrow(r11)
                com.android.launcher3.util.IntArray r12 = new com.android.launcher3.util.IntArray
                r12.<init>()
                com.android.launcher3.widget.WidgetManagerHelper r13 = new com.android.launcher3.widget.WidgetManagerHelper
                android.content.Context r14 = r0.mContext
                r13.<init>(r14)
            L_0x004c:
                boolean r14 = r18.moveToNext()
                if (r14 == 0) goto L_0x0147
                com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry r14 = new com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry
                r14.<init>()
                int r15 = r1.getInt(r2)
                r14.id = r15
                int r15 = r1.getInt(r3)
                r14.itemType = r15
                int r15 = r1.getInt(r4)
                r14.screenId = r15
                int r15 = r0.mLastScreenId
                r16 = r2
                int r2 = r14.screenId
                int r2 = java.lang.Math.max(r15, r2)
                r0.mLastScreenId = r2
                int r2 = r1.getInt(r5)
                r14.cellX = r2
                int r2 = r1.getInt(r6)
                r14.cellY = r2
                int r2 = r1.getInt(r7)
                r14.spanX = r2
                int r2 = r1.getInt(r8)
                r14.spanY = r2
                int r2 = r14.itemType     // Catch:{ Exception -> 0x013c }
                if (r2 == 0) goto L_0x00f9
                r15 = 1
                if (r2 == r15) goto L_0x00f9
                r15 = 2
                if (r2 == r15) goto L_0x00ea
                r15 = 4
                if (r2 == r15) goto L_0x00a6
                r15 = 6
                if (r2 != r15) goto L_0x009e
                goto L_0x00f9
            L_0x009e:
                java.lang.Exception r2 = new java.lang.Exception     // Catch:{ Exception -> 0x013c }
                java.lang.String r15 = "Invalid item type"
                r2.<init>(r15)     // Catch:{ Exception -> 0x013c }
                throw r2     // Catch:{ Exception -> 0x013c }
            L_0x00a6:
                java.lang.String r2 = r1.getString(r10)     // Catch:{ Exception -> 0x013c }
                java.lang.String unused = r14.mProvider = r2     // Catch:{ Exception -> 0x013c }
                java.lang.String r2 = r14.mProvider     // Catch:{ Exception -> 0x013c }
                android.content.ComponentName r2 = android.content.ComponentName.unflattenFromString(r2)     // Catch:{ Exception -> 0x013c }
                java.lang.String r2 = r2.getPackageName()     // Catch:{ Exception -> 0x013c }
                r0.verifyPackage(r2)     // Catch:{ Exception -> 0x013c }
                int r2 = r1.getInt(r11)     // Catch:{ Exception -> 0x013c }
                com.android.launcher3.widget.LauncherAppWidgetProviderInfo r2 = r13.getLauncherAppWidgetInfo(r2)     // Catch:{ Exception -> 0x013c }
                r15 = 0
                if (r2 == 0) goto L_0x00cb
                android.graphics.Point r15 = r2.getMinSpans()     // Catch:{ Exception -> 0x013c }
            L_0x00cb:
                if (r15 == 0) goto L_0x00e4
                int r2 = r15.x     // Catch:{ Exception -> 0x013c }
                if (r2 <= 0) goto L_0x00d4
                int r2 = r15.x     // Catch:{ Exception -> 0x013c }
                goto L_0x00d6
            L_0x00d4:
                int r2 = r14.spanX     // Catch:{ Exception -> 0x013c }
            L_0x00d6:
                r14.minSpanX = r2     // Catch:{ Exception -> 0x013c }
                int r2 = r15.y     // Catch:{ Exception -> 0x013c }
                if (r2 <= 0) goto L_0x00df
                int r2 = r15.y     // Catch:{ Exception -> 0x013c }
                goto L_0x00e1
            L_0x00df:
                int r2 = r14.spanY     // Catch:{ Exception -> 0x013c }
            L_0x00e1:
                r14.minSpanY = r2     // Catch:{ Exception -> 0x013c }
                goto L_0x0107
            L_0x00e4:
                r2 = 2
                r14.minSpanY = r2     // Catch:{ Exception -> 0x013c }
                r14.minSpanX = r2     // Catch:{ Exception -> 0x013c }
                goto L_0x0107
            L_0x00ea:
                int r2 = r0.getFolderItemsCount(r14)     // Catch:{ Exception -> 0x013c }
                if (r2 == 0) goto L_0x00f1
                goto L_0x0107
            L_0x00f1:
                java.lang.Exception r2 = new java.lang.Exception     // Catch:{ Exception -> 0x013c }
                java.lang.String r15 = "Folder is empty"
                r2.<init>(r15)     // Catch:{ Exception -> 0x013c }
                throw r2     // Catch:{ Exception -> 0x013c }
            L_0x00f9:
                java.lang.String r2 = r1.getString(r9)     // Catch:{ Exception -> 0x013c }
                java.lang.String unused = r14.mIntent = r2     // Catch:{ Exception -> 0x013c }
                java.lang.String r2 = r14.mIntent     // Catch:{ Exception -> 0x013c }
                r0.verifyIntent(r2)     // Catch:{ Exception -> 0x013c }
            L_0x0107:
                java.util.ArrayList<com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry> r2 = r0.mWorkspaceEntries
                r2.add(r14)
                java.util.Map<java.lang.Integer, java.util.ArrayList<com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry>> r2 = r0.mWorkspaceEntriesByScreenId
                int r15 = r14.screenId
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                boolean r2 = r2.containsKey(r15)
                if (r2 != 0) goto L_0x012a
                java.util.Map<java.lang.Integer, java.util.ArrayList<com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry>> r2 = r0.mWorkspaceEntriesByScreenId
                int r15 = r14.screenId
                java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                r2.put(r15, r1)
            L_0x012a:
                java.util.Map<java.lang.Integer, java.util.ArrayList<com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry>> r1 = r0.mWorkspaceEntriesByScreenId
                int r2 = r14.screenId
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                java.lang.Object r1 = r1.get(r2)
                java.util.ArrayList r1 = (java.util.ArrayList) r1
                r1.add(r14)
                goto L_0x0141
            L_0x013c:
                int r1 = r14.id
                r12.add(r1)
            L_0x0141:
                r1 = r18
                r2 = r16
                goto L_0x004c
            L_0x0147:
                android.database.sqlite.SQLiteDatabase r1 = r0.mDb
                java.lang.String r2 = r0.mTableName
                com.android.launcher3.model.GridSizeMigrationTaskV2.removeEntryFromDb(r1, r2, r12)
                r18.close()
                java.util.ArrayList<com.android.launcher3.model.GridSizeMigrationTaskV2$DbEntry> r1 = r0.mWorkspaceEntries
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.GridSizeMigrationTaskV2.DbReader.loadWorkspaceEntries(android.database.Cursor):java.util.ArrayList");
        }

        private int getFolderItemsCount(DbEntry dbEntry) {
            Cursor queryWorkspace = queryWorkspace(new String[]{"_id", LauncherSettings.Favorites.INTENT}, "container = " + dbEntry.id);
            int i = 0;
            while (queryWorkspace.moveToNext()) {
                try {
                    int i2 = queryWorkspace.getInt(0);
                    String string = queryWorkspace.getString(1);
                    verifyIntent(string);
                    i++;
                    if (!dbEntry.mFolderItems.containsKey(string)) {
                        dbEntry.mFolderItems.put(string, new HashSet());
                    }
                    ((Set) dbEntry.mFolderItems.get(string)).add(Integer.valueOf(i2));
                } catch (Exception unused) {
                    GridSizeMigrationTaskV2.removeEntryFromDb(this.mDb, this.mTableName, IntArray.wrap(queryWorkspace.getInt(0)));
                }
            }
            queryWorkspace.close();
            return i;
        }

        private Cursor queryWorkspace(String[] strArr, String str) {
            return this.mDb.query(this.mTableName, strArr, str, (String[]) null, (String) null, (String) null, (String) null);
        }

        private void verifyIntent(String str) throws Exception {
            Intent parseUri = Intent.parseUri(str, 0);
            if (parseUri.getComponent() != null) {
                verifyPackage(parseUri.getComponent().getPackageName());
            } else if (parseUri.getPackage() != null) {
                verifyPackage(parseUri.getPackage());
            }
        }

        private void verifyPackage(String str) throws Exception {
            if (!this.mValidPackages.contains(str)) {
                throw new Exception("Package not available");
            }
        }
    }

    protected static class DbEntry extends ItemInfo implements Comparable<DbEntry> {
        /* access modifiers changed from: private */
        public Map<String, Set<Integer>> mFolderItems = new HashMap();
        /* access modifiers changed from: private */
        public String mIntent;
        /* access modifiers changed from: private */
        public String mProvider;

        protected DbEntry() {
        }

        public int compareTo(DbEntry dbEntry) {
            if (this.screenId != dbEntry.screenId) {
                return Integer.compare(this.screenId, dbEntry.screenId);
            }
            if (this.cellY != dbEntry.cellY) {
                return Integer.compare(this.cellY, dbEntry.cellY);
            }
            return Integer.compare(this.cellX, dbEntry.cellX);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return Objects.equals(this.mIntent, ((DbEntry) obj).mIntent);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mIntent});
        }

        public void updateContentValues(ContentValues contentValues) {
            contentValues.put(LauncherSettings.Favorites.SCREEN, Integer.valueOf(this.screenId));
            contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX));
            contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY));
            contentValues.put(LauncherSettings.Favorites.SPANX, Integer.valueOf(this.spanX));
            contentValues.put(LauncherSettings.Favorites.SPANY, Integer.valueOf(this.spanY));
        }
    }
}
