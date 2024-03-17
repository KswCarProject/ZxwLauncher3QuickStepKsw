package com.android.launcher3;

import android.app.backup.BackupManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.icons.cache.BaseIconCache;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.DbDowngradeHelper;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.IOUtils;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.NoLocaleSQLiteHelper;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.widget.LauncherAppWidgetHost;
import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.xmlpull.v1.XmlPullParser;

public class LauncherProvider extends ContentProvider {
    public static final String AUTHORITY = "com.android.launcher3.settings";
    private static final String DOWNGRADE_SCHEMA_FILE = "downgrade_schema.json";
    static final String EMPTY_DATABASE_CREATED = "EMPTY_DATABASE_CREATED";
    public static final String KEY_LAYOUT_PROVIDER_AUTHORITY = "KEY_LAYOUT_PROVIDER_AUTHORITY";
    private static final boolean LOGD = false;
    private static final long RESTORE_BACKUP_TABLE_DELAY = TimeUnit.SECONDS.toMillis(30);
    public static final int SCHEMA_VERSION = 30;
    private static final String TAG = "LauncherProvider";
    private static final int TEST_WORKSPACE_LAYOUT_RES_XML = 2131951617;
    private long mLastRestoreTimestamp = 0;
    protected DatabaseHelper mOpenHelper;
    protected String mProviderAuthority;
    private boolean mUseTestWorkspaceLayout;

    static /* synthetic */ XmlPullParser lambda$createWorkspaceLoaderFromAppRestriction$4(XmlPullParser xmlPullParser) {
        return xmlPullParser;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null && instanceNoCreate.getModel().isModelLoaded()) {
            instanceNoCreate.getModel().dumpState("", fileDescriptor, printWriter, strArr);
        }
    }

    public boolean onCreate() {
        MainProcessInitializer.initialize(getContext().getApplicationContext());
        return true;
    }

    public String getType(Uri uri) {
        SqlArguments sqlArguments = new SqlArguments(uri, (String) null, (String[]) null);
        if (TextUtils.isEmpty(sqlArguments.where)) {
            return "vnd.android.cursor.dir/" + sqlArguments.table;
        }
        return "vnd.android.cursor.item/" + sqlArguments.table;
    }

    /* access modifiers changed from: protected */
    public synchronized void createDbIfNotExists() {
        if (this.mOpenHelper == null) {
            this.mOpenHelper = DatabaseHelper.createDatabaseHelper(getContext(), false);
            RestoreDbTask.restoreIfNeeded(getContext(), this.mOpenHelper);
        }
    }

    private synchronized boolean prepForMigration(String str, String str2, Supplier<DatabaseHelper> supplier, Supplier<DatabaseHelper> supplier2) {
        if (TextUtils.equals(str, this.mOpenHelper.getDatabaseName())) {
            Log.e("b/198965093", "prepForMigration - target db is same as current: " + str);
            return false;
        }
        DatabaseHelper databaseHelper = supplier.get();
        this.mOpenHelper = supplier2.get();
        LauncherDbUtils.copyTable(databaseHelper.getReadableDatabase(), LauncherSettings.Favorites.TABLE_NAME, this.mOpenHelper.getWritableDatabase(), str2, getContext());
        databaseHelper.close();
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri, str, strArr2);
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(sqlArguments.table);
        Cursor query = sQLiteQueryBuilder.query(this.mOpenHelper.getWritableDatabase(), strArr, sqlArguments.where, sqlArguments.args, (String) null, (String) null, str2);
        Bundle bundle = new Bundle();
        bundle.putString(LauncherSettings.Settings.EXTRA_DB_NAME, this.mOpenHelper.getDatabaseName());
        query.setExtras(bundle);
        query.setNotificationUri(getContext().getContentResolver(), uri);
        return query;
    }

    static int dbInsertAndCheck(DatabaseHelper databaseHelper, SQLiteDatabase sQLiteDatabase, String str, String str2, ContentValues contentValues) {
        if (contentValues == null) {
            throw new RuntimeException("Error: attempting to insert null values");
        } else if (contentValues.containsKey("_id")) {
            databaseHelper.checkId(contentValues);
            return (int) sQLiteDatabase.insert(str, str2, contentValues);
        } else {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
    }

    private void reloadLauncherIfExternal() {
        LauncherAppState instanceNoCreate;
        if (Binder.getCallingPid() != Process.myPid() && (instanceNoCreate = LauncherAppState.getInstanceNoCreate()) != null) {
            instanceNoCreate.getModel().forceReload();
        }
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri);
        if (Binder.getCallingPid() != Process.myPid() && !initializeExternalAdd(contentValues)) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        addModifiedTime(contentValues);
        int dbInsertAndCheck = dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, (String) null, contentValues);
        if (dbInsertAndCheck < 0) {
            return null;
        }
        onAddOrDeleteOp(writableDatabase);
        Uri withAppendedId = ContentUris.withAppendedId(uri, (long) dbInsertAndCheck);
        reloadLauncherIfExternal();
        return withAppendedId;
    }

    private boolean initializeExternalAdd(ContentValues contentValues) {
        contentValues.put("_id", Integer.valueOf(this.mOpenHelper.generateNewItemId()));
        Integer asInteger = contentValues.getAsInteger(LauncherSettings.Favorites.ITEM_TYPE);
        if (asInteger == null || asInteger.intValue() != 4 || contentValues.containsKey(LauncherSettings.Favorites.APPWIDGET_ID)) {
            return true;
        }
        AppWidgetManager instance = AppWidgetManager.getInstance(getContext());
        ComponentName unflattenFromString = ComponentName.unflattenFromString(contentValues.getAsString(LauncherSettings.Favorites.APPWIDGET_PROVIDER));
        if (unflattenFromString != null) {
            try {
                AppWidgetHost newLauncherWidgetHost = this.mOpenHelper.newLauncherWidgetHost();
                int allocateAppWidgetId = newLauncherWidgetHost.allocateAppWidgetId();
                contentValues.put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(allocateAppWidgetId));
                if (instance.bindAppWidgetIdIfAllowed(allocateAppWidgetId, unflattenFromString)) {
                    return true;
                }
                newLauncherWidgetHost.deleteAppWidgetId(allocateAppWidgetId);
                return false;
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to initialize external widget", e);
            }
        }
        return false;
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(writableDatabase);
        try {
            int length = contentValuesArr.length;
            for (int i = 0; i < length; i++) {
                addModifiedTime(contentValuesArr[i]);
                if (dbInsertAndCheck(this.mOpenHelper, writableDatabase, sqlArguments.table, (String) null, contentValuesArr[i]) < 0) {
                    sQLiteTransaction.close();
                    return 0;
                }
            }
            onAddOrDeleteOp(writableDatabase);
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            reloadLauncherIfExternal();
            return contentValuesArr.length;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
        createDbIfNotExists();
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(this.mOpenHelper.getWritableDatabase());
        try {
            int size = arrayList.size();
            ContentProviderResult[] contentProviderResultArr = new ContentProviderResult[size];
            boolean z = false;
            for (int i = 0; i < size; i++) {
                ContentProviderOperation contentProviderOperation = arrayList.get(i);
                contentProviderResultArr[i] = contentProviderOperation.apply(this, contentProviderResultArr, i);
                z |= (contentProviderOperation.isInsert() || contentProviderOperation.isDelete()) && contentProviderResultArr[i].count != null && contentProviderResultArr[i].count.intValue() > 0;
            }
            if (z) {
                onAddOrDeleteOp(sQLiteTransaction.getDb());
            }
            sQLiteTransaction.commit();
            reloadLauncherIfExternal();
            sQLiteTransaction.close();
            return contentProviderResultArr;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri, str, strArr);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (Binder.getCallingPid() != Process.myPid() && LauncherSettings.Favorites.TABLE_NAME.equalsIgnoreCase(sqlArguments.table)) {
            DatabaseHelper databaseHelper = this.mOpenHelper;
            databaseHelper.removeGhostWidgets(databaseHelper.getWritableDatabase());
        }
        int delete = writableDatabase.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
        if (delete > 0) {
            onAddOrDeleteOp(writableDatabase);
            reloadLauncherIfExternal();
        }
        return delete;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        createDbIfNotExists();
        SqlArguments sqlArguments = new SqlArguments(uri, str, strArr);
        addModifiedTime(contentValues);
        int update = this.mOpenHelper.getWritableDatabase().update(sqlArguments.table, contentValues, sqlArguments.where, sqlArguments.args);
        reloadLauncherIfExternal();
        return update;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (Binder.getCallingUid() != Process.myUid()) {
            return null;
        }
        createDbIfNotExists();
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1999597249:
                if (str.equals(LauncherSettings.Settings.METHOD_DELETE_EMPTY_FOLDERS)) {
                    c = 0;
                    break;
                }
                break;
            case -1565944700:
                if (str.equals(LauncherSettings.Settings.METHOD_REMOVE_GHOST_WIDGETS)) {
                    c = 1;
                    break;
                }
                break;
            case -1428559582:
                if (str.equals(LauncherSettings.Settings.METHOD_RESTORE_BACKUP_TABLE)) {
                    c = 2;
                    break;
                }
                break;
            case -1107339682:
                if (str.equals(LauncherSettings.Settings.METHOD_NEW_ITEM_ID)) {
                    c = 3;
                    break;
                }
                break;
            case -1029923675:
                if (str.equals(LauncherSettings.Settings.METHOD_NEW_SCREEN_ID)) {
                    c = 4;
                    break;
                }
                break;
            case -1018207424:
                if (str.equals(LauncherSettings.Settings.METHOD_PREP_FOR_PREVIEW)) {
                    c = 5;
                    break;
                }
                break;
            case -1008511191:
                if (str.equals(LauncherSettings.Settings.METHOD_CLEAR_EMPTY_DB_FLAG)) {
                    c = 6;
                    break;
                }
                break;
            case -298097114:
                if (str.equals(LauncherSettings.Settings.METHOD_SWITCH_DATABASE)) {
                    c = 7;
                    break;
                }
                break;
            case 306676016:
                if (str.equals(LauncherSettings.Settings.METHOD_REFRESH_HOTSEAT_RESTORE_TABLE)) {
                    c = 8;
                    break;
                }
                break;
            case 476749504:
                if (str.equals(LauncherSettings.Settings.METHOD_LOAD_DEFAULT_FAVORITES)) {
                    c = 9;
                    break;
                }
                break;
            case 684076146:
                if (str.equals(LauncherSettings.Settings.METHOD_WAS_EMPTY_DB_CREATED)) {
                    c = 10;
                    break;
                }
                break;
            case 870601991:
                if (str.equals(LauncherSettings.Settings.METHOD_UPDATE_CURRENT_OPEN_HELPER)) {
                    c = 11;
                    break;
                }
                break;
            case 878603444:
                if (str.equals(LauncherSettings.Settings.METHOD_CLEAR_USE_TEST_WORKSPACE_LAYOUT_FLAG)) {
                    c = 12;
                    break;
                }
                break;
            case 1006035967:
                if (str.equals(LauncherSettings.Settings.METHOD_SET_USE_TEST_WORKSPACE_LAYOUT_FLAG)) {
                    c = 13;
                    break;
                }
                break;
            case 1038077429:
                if (str.equals(LauncherSettings.Settings.METHOD_REFRESH_BACKUP_TABLE)) {
                    c = 14;
                    break;
                }
                break;
            case 1615249692:
                if (str.equals(LauncherSettings.Settings.METHOD_NEW_TRANSACTION)) {
                    c = 15;
                    break;
                }
                break;
            case 2117515411:
                if (str.equals(LauncherSettings.Settings.METHOD_CREATE_EMPTY_DB)) {
                    c = 16;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                Bundle bundle2 = new Bundle();
                bundle2.putIntArray("value", deleteEmptyFolders().toArray());
                return bundle2;
            case 1:
                DatabaseHelper databaseHelper = this.mOpenHelper;
                databaseHelper.removeGhostWidgets(databaseHelper.getWritableDatabase());
                return null;
            case 2:
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - this.mLastRestoreTimestamp > RESTORE_BACKUP_TABLE_DELAY) {
                    this.mLastRestoreTimestamp = currentTimeMillis;
                    RestoreDbTask.restoreIfPossible(getContext(), this.mOpenHelper, new BackupManager(getContext()));
                }
                return null;
            case 3:
                Bundle bundle3 = new Bundle();
                bundle3.putInt("value", this.mOpenHelper.generateNewItemId());
                return bundle3;
            case 4:
                Bundle bundle4 = new Bundle();
                bundle4.putInt("value", this.mOpenHelper.getNewScreenId());
                return bundle4;
            case 5:
                Bundle bundle5 = new Bundle();
                bundle5.putBoolean("value", prepForMigration(str2, LauncherSettings.Favorites.PREVIEW_TABLE_NAME, new Supplier(str2) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final Object get() {
                        return LauncherProvider.this.lambda$call$2$LauncherProvider(this.f$1);
                    }
                }, new Supplier() {
                    public final Object get() {
                        return LauncherProvider.this.lambda$call$3$LauncherProvider();
                    }
                }));
                return bundle5;
            case 6:
                clearFlagEmptyDbCreated();
                return null;
            case 7:
                if (TextUtils.equals(str2, this.mOpenHelper.getDatabaseName())) {
                    return null;
                }
                DatabaseHelper databaseHelper2 = this.mOpenHelper;
                if (bundle == null || !bundle.containsKey(KEY_LAYOUT_PROVIDER_AUTHORITY)) {
                    this.mProviderAuthority = null;
                } else {
                    this.mProviderAuthority = bundle.getString(KEY_LAYOUT_PROVIDER_AUTHORITY);
                }
                this.mOpenHelper = DatabaseHelper.createDatabaseHelper(getContext(), str2, false);
                databaseHelper2.close();
                LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
                if (instanceNoCreate == null) {
                    return null;
                }
                instanceNoCreate.getModel().forceReload();
                return null;
            case 8:
                DatabaseHelper databaseHelper3 = this.mOpenHelper;
                boolean unused = databaseHelper3.mHotseatRestoreTableExists = LauncherDbUtils.tableExists(databaseHelper3.getReadableDatabase(), LauncherSettings.Favorites.HYBRID_HOTSEAT_BACKUP_TABLE);
                return null;
            case 9:
                loadDefaultFavoritesIfNecessary();
                return null;
            case 10:
                Bundle bundle6 = new Bundle();
                bundle6.putBoolean("value", Utilities.getPrefs(getContext()).getBoolean(this.mOpenHelper.getKey(EMPTY_DATABASE_CREATED), false));
                return bundle6;
            case 11:
                Bundle bundle7 = new Bundle();
                bundle7.putBoolean("value", prepForMigration(str2, LauncherSettings.Favorites.TMP_TABLE, new Supplier() {
                    public final Object get() {
                        return LauncherProvider.this.lambda$call$0$LauncherProvider();
                    }
                }, new Supplier() {
                    public final Object get() {
                        return LauncherProvider.this.lambda$call$1$LauncherProvider();
                    }
                }));
                return bundle7;
            case 12:
                this.mUseTestWorkspaceLayout = false;
                return null;
            case 13:
                this.mUseTestWorkspaceLayout = true;
                return null;
            case 14:
                DatabaseHelper databaseHelper4 = this.mOpenHelper;
                boolean unused2 = databaseHelper4.mBackupTableExists = LauncherDbUtils.tableExists(databaseHelper4.getReadableDatabase(), LauncherSettings.Favorites.BACKUP_TABLE_NAME);
                return null;
            case 15:
                Bundle bundle8 = new Bundle();
                bundle8.putBinder("value", new LauncherDbUtils.SQLiteTransaction(this.mOpenHelper.getWritableDatabase()));
                return bundle8;
            case 16:
                DatabaseHelper databaseHelper5 = this.mOpenHelper;
                databaseHelper5.createEmptyDB(databaseHelper5.getWritableDatabase());
                return null;
            default:
                return null;
        }
    }

    public /* synthetic */ DatabaseHelper lambda$call$0$LauncherProvider() {
        return this.mOpenHelper;
    }

    public /* synthetic */ DatabaseHelper lambda$call$1$LauncherProvider() {
        return DatabaseHelper.createDatabaseHelper(getContext(), true);
    }

    public /* synthetic */ DatabaseHelper lambda$call$2$LauncherProvider(String str) {
        return DatabaseHelper.createDatabaseHelper(getContext(), str, true);
    }

    public /* synthetic */ DatabaseHelper lambda$call$3$LauncherProvider() {
        return this.mOpenHelper;
    }

    private void onAddOrDeleteOp(SQLiteDatabase sQLiteDatabase) {
        this.mOpenHelper.onAddOrDeleteOp(sQLiteDatabase);
    }

    private IntArray deleteEmptyFolders() {
        LauncherDbUtils.SQLiteTransaction sQLiteTransaction;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        try {
            sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(writableDatabase);
            IntArray queryIntArray = LauncherDbUtils.queryIntArray(false, writableDatabase, LauncherSettings.Favorites.TABLE_NAME, "_id", "itemType = 2 AND _id NOT IN (SELECT container FROM favorites)", (String) null, (String) null);
            if (!queryIntArray.isEmpty()) {
                writableDatabase.delete(LauncherSettings.Favorites.TABLE_NAME, Utilities.createDbSelectionQuery("_id", queryIntArray), (String[]) null);
            }
            sQLiteTransaction.commit();
            sQLiteTransaction.close();
            return queryIntArray;
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return new IntArray();
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    static void addModifiedTime(ContentValues contentValues) {
        contentValues.put(LauncherSettings.Favorites.MODIFIED, Long.valueOf(System.currentTimeMillis()));
    }

    private void clearFlagEmptyDbCreated() {
        Utilities.getPrefs(getContext()).edit().remove(this.mOpenHelper.getKey(EMPTY_DATABASE_CREATED)).commit();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: com.android.launcher3.AutoInstallsLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: com.android.launcher3.AutoInstallsLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: com.android.launcher3.DefaultLayoutParser} */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004b, code lost:
        r7 = r3.getResources();
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void loadDefaultFavoritesIfNecessary() {
        /*
            r9 = this;
            monitor-enter(r9)
            android.content.Context r0 = r9.getContext()     // Catch:{ all -> 0x00a6 }
            android.content.SharedPreferences r0 = com.android.launcher3.Utilities.getPrefs(r0)     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            java.lang.String r2 = "EMPTY_DATABASE_CREATED"
            java.lang.String r1 = r1.getKey(r2)     // Catch:{ all -> 0x00a6 }
            r2 = 0
            boolean r0 = r0.getBoolean(r1, r2)     // Catch:{ all -> 0x00a6 }
            if (r0 == 0) goto L_0x00a4
            java.lang.String r0 = "LauncherProvider"
            java.lang.String r1 = "loading default workspace"
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r0 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            android.appwidget.AppWidgetHost r0 = r0.newLauncherWidgetHost()     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.AutoInstallsLayout r1 = r9.createWorkspaceLoaderFromAppRestriction(r0)     // Catch:{ all -> 0x00a6 }
            if (r1 != 0) goto L_0x0035
            android.content.Context r1 = r9.getContext()     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.AutoInstallsLayout r1 = com.android.launcher3.AutoInstallsLayout.get(r1, r0, r3)     // Catch:{ all -> 0x00a6 }
        L_0x0035:
            if (r1 != 0) goto L_0x006b
            android.content.Context r3 = r9.getContext()     // Catch:{ all -> 0x00a6 }
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.Partner r3 = com.android.launcher3.Partner.get(r3)     // Catch:{ all -> 0x00a6 }
            if (r3 == 0) goto L_0x006b
            boolean r4 = r3.hasDefaultLayout()     // Catch:{ all -> 0x00a6 }
            if (r4 == 0) goto L_0x006b
            android.content.res.Resources r7 = r3.getResources()     // Catch:{ all -> 0x00a6 }
            java.lang.String r4 = "partner_default_layout"
            java.lang.String r5 = "xml"
            java.lang.String r3 = r3.getPackageName()     // Catch:{ all -> 0x00a6 }
            int r8 = r7.getIdentifier(r4, r5, r3)     // Catch:{ all -> 0x00a6 }
            if (r8 == 0) goto L_0x006b
            com.android.launcher3.DefaultLayoutParser r1 = new com.android.launcher3.DefaultLayoutParser     // Catch:{ all -> 0x00a6 }
            android.content.Context r4 = r9.getContext()     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r6 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            r3 = r1
            r5 = r0
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00a6 }
        L_0x006b:
            if (r1 == 0) goto L_0x006e
            r2 = 1
        L_0x006e:
            if (r1 != 0) goto L_0x0074
            com.android.launcher3.DefaultLayoutParser r1 = r9.getDefaultLayoutParser(r0)     // Catch:{ all -> 0x00a6 }
        L_0x0074:
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            android.database.sqlite.SQLiteDatabase r4 = r3.getWritableDatabase()     // Catch:{ all -> 0x00a6 }
            r3.createEmptyDB(r4)     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            android.database.sqlite.SQLiteDatabase r4 = r3.getWritableDatabase()     // Catch:{ all -> 0x00a6 }
            int r1 = r3.loadFavorites(r4, r1)     // Catch:{ all -> 0x00a6 }
            if (r1 > 0) goto L_0x00a1
            if (r2 == 0) goto L_0x00a1
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            android.database.sqlite.SQLiteDatabase r2 = r1.getWritableDatabase()     // Catch:{ all -> 0x00a6 }
            r1.createEmptyDB(r2)     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r9.mOpenHelper     // Catch:{ all -> 0x00a6 }
            android.database.sqlite.SQLiteDatabase r2 = r1.getWritableDatabase()     // Catch:{ all -> 0x00a6 }
            com.android.launcher3.DefaultLayoutParser r0 = r9.getDefaultLayoutParser(r0)     // Catch:{ all -> 0x00a6 }
            r1.loadFavorites(r2, r0)     // Catch:{ all -> 0x00a6 }
        L_0x00a1:
            r9.clearFlagEmptyDbCreated()     // Catch:{ all -> 0x00a6 }
        L_0x00a4:
            monitor-exit(r9)
            return
        L_0x00a6:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.loadDefaultFavoritesIfNecessary():void");
    }

    private AutoInstallsLayout createWorkspaceLoaderFromAppRestriction(AppWidgetHost appWidgetHost) {
        String str;
        InputStream openInputStream;
        Context context = getContext();
        if (!TextUtils.isEmpty(this.mProviderAuthority)) {
            str = this.mProviderAuthority;
        } else {
            str = Settings.Secure.getString(context.getContentResolver(), "launcher3.layout.provider");
        }
        String str2 = str;
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        ProviderInfo resolveContentProvider = context.getPackageManager().resolveContentProvider(str2, 0);
        if (resolveContentProvider == null) {
            Log.e(TAG, "No provider found for authority " + str2);
            return null;
        }
        try {
            openInputStream = context.getContentResolver().openInputStream(getLayoutUri(str2, context));
            String str3 = new String(IOUtils.toByteArray(openInputStream));
            XmlPullParser newPullParser = Xml.newPullParser();
            newPullParser.setInput(new StringReader(str3));
            Log.d(TAG, "Loading layout from " + str2);
            AutoInstallsLayout autoInstallsLayout = new AutoInstallsLayout(context, appWidgetHost, (AutoInstallsLayout.LayoutParserCallback) this.mOpenHelper, context.getPackageManager().getResourcesForApplication(resolveContentProvider.applicationInfo), (Supplier<XmlPullParser>) new Supplier(newPullParser) {
                public final /* synthetic */ XmlPullParser f$0;

                {
                    this.f$0 = r1;
                }

                public final Object get() {
                    return LauncherProvider.lambda$createWorkspaceLoaderFromAppRestriction$4(this.f$0);
                }
            }, AutoInstallsLayout.TAG_WORKSPACE);
            if (openInputStream != null) {
                openInputStream.close();
            }
            return autoInstallsLayout;
        } catch (Exception e) {
            Log.e(TAG, "Error getting layout stream from: " + str2, e);
            return null;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    public static Uri getLayoutUri(String str, Context context) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        return new Uri.Builder().scheme("content").authority(str).path("launcher_layout").appendQueryParameter(BaseIconCache.IconDB.COLUMN_VERSION, "1").appendQueryParameter("gridWidth", Integer.toString(idp.numColumns)).appendQueryParameter("gridHeight", Integer.toString(idp.numRows)).appendQueryParameter("hotseatSize", Integer.toString(idp.numDatabaseHotseatIcons)).build();
    }

    private DefaultLayoutParser getDefaultLayoutParser(AppWidgetHost appWidgetHost) {
        int i;
        InvariantDeviceProfile idp = LauncherAppState.getIDP(getContext());
        if (this.mUseTestWorkspaceLayout) {
            i = R.xml.default_test_workspace;
        } else {
            i = idp.defaultLayoutId;
        }
        if (((UserManager) getContext().getSystemService(UserManager.class)).isDemoUser() && idp.demoModeLayoutId != 0) {
            i = idp.demoModeLayoutId;
        }
        return new DefaultLayoutParser(getContext(), appWidgetHost, this.mOpenHelper, getContext().getResources(), i);
    }

    public static class DatabaseHelper extends NoLocaleSQLiteHelper implements AutoInstallsLayout.LayoutParserCallback {
        /* access modifiers changed from: private */
        public boolean mBackupTableExists;
        private final Context mContext;
        private final boolean mForMigration;
        /* access modifiers changed from: private */
        public boolean mHotseatRestoreTableExists;
        private int mMaxItemId = -1;

        static DatabaseHelper createDatabaseHelper(Context context, boolean z) {
            return createDatabaseHelper(context, (String) null, z);
        }

        static DatabaseHelper createDatabaseHelper(Context context, String str, boolean z) {
            if (str == null) {
                str = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).dbFile;
            }
            DatabaseHelper databaseHelper = new DatabaseHelper(context, str, z);
            if (!LauncherDbUtils.tableExists(databaseHelper.getReadableDatabase(), LauncherSettings.Favorites.TABLE_NAME)) {
                Log.e(LauncherProvider.TAG, "Tables are missing after onCreate has been called. Trying to recreate");
                databaseHelper.addFavoritesTable(databaseHelper.getWritableDatabase(), true);
            }
            databaseHelper.mHotseatRestoreTableExists = LauncherDbUtils.tableExists(databaseHelper.getReadableDatabase(), LauncherSettings.Favorites.HYBRID_HOTSEAT_BACKUP_TABLE);
            databaseHelper.initIds();
            return databaseHelper;
        }

        public DatabaseHelper(Context context, String str, boolean z) {
            super(context, str, 30);
            this.mContext = context;
            this.mForMigration = z;
        }

        /* access modifiers changed from: protected */
        public void initIds() {
            if (this.mMaxItemId == -1) {
                this.mMaxItemId = initializeMaxItemId(getWritableDatabase());
            }
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            this.mMaxItemId = 1;
            addFavoritesTable(sQLiteDatabase, false);
            this.mMaxItemId = initializeMaxItemId(sQLiteDatabase);
            if (!this.mForMigration) {
                onEmptyDbCreated();
            }
        }

        /* access modifiers changed from: protected */
        public void onAddOrDeleteOp(SQLiteDatabase sQLiteDatabase) {
            if (this.mBackupTableExists) {
                LauncherDbUtils.dropTable(sQLiteDatabase, LauncherSettings.Favorites.BACKUP_TABLE_NAME);
                this.mBackupTableExists = false;
            }
            if (this.mHotseatRestoreTableExists) {
                LauncherDbUtils.dropTable(sQLiteDatabase, LauncherSettings.Favorites.HYBRID_HOTSEAT_BACKUP_TABLE);
                this.mHotseatRestoreTableExists = false;
            }
        }

        /* access modifiers changed from: package-private */
        public String getKey(String str) {
            if (TextUtils.equals(getDatabaseName(), LauncherFiles.LAUNCHER_DB)) {
                return str;
            }
            return str + "@" + getDatabaseName();
        }

        /* access modifiers changed from: protected */
        public void onEmptyDbCreated() {
            Utilities.getPrefs(this.mContext).edit().putBoolean(getKey(LauncherProvider.EMPTY_DATABASE_CREATED), true).commit();
        }

        public long getSerialNumberForUser(UserHandle userHandle) {
            return UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getSerialNumberForUser(userHandle);
        }

        public long getDefaultUserSerial() {
            return getSerialNumberForUser(Process.myUserHandle());
        }

        private void addFavoritesTable(SQLiteDatabase sQLiteDatabase, boolean z) {
            LauncherSettings.Favorites.addTableToDb(sQLiteDatabase, getDefaultUserSerial(), z);
        }

        public void onOpen(SQLiteDatabase sQLiteDatabase) {
            super.onOpen(sQLiteDatabase);
            File fileStreamPath = this.mContext.getFileStreamPath(LauncherProvider.DOWNGRADE_SCHEMA_FILE);
            if (!fileStreamPath.exists()) {
                handleOneTimeDataUpgrade(sQLiteDatabase);
            }
            DbDowngradeHelper.updateSchemaFile(fileStreamPath, 30, this.mContext);
        }

        /* access modifiers changed from: protected */
        public void handleOneTimeDataUpgrade(SQLiteDatabase sQLiteDatabase) {
            UserCache userCache = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext);
            for (UserHandle serialNumberForUser : userCache.getUserProfiles()) {
                sQLiteDatabase.execSQL("update favorites set intent = replace(intent, ';l.profile=" + userCache.getSerialNumberForUser(serialNumberForUser) + ";', ';') where itemType = 0;");
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0028, code lost:
            if (addIntegerColumn(r9, com.android.launcher3.LauncherSettings.Favorites.MODIFIED, 0) == false) goto L_0x010e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
            if (addIntegerColumn(r9, com.android.launcher3.LauncherSettings.Favorites.RESTORED, 0) == false) goto L_0x010e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0040, code lost:
            if (addIntegerColumn(r9, "profileId", getDefaultUserSerial()) == false) goto L_0x010e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0048, code lost:
            if (updateFolderItemsRank(r9, true) == false) goto L_0x010e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0052, code lost:
            if (addIntegerColumn(r9, com.android.launcher3.LauncherSettings.Favorites.OPTIONS, 0) == false) goto L_0x010e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0056, code lost:
            convertShortcutsToLauncherActivities(r16);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0059, code lost:
            r2 = com.android.launcher3.provider.LauncherDbUtils.queryIntArray(false, r16, "workspaceScreens", "_id", (java.lang.String) null, (java.lang.String) null, "screenRank");
            r3 = r2.toArray();
            java.util.Arrays.sort(r3);
            r5 = "";
            r6 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0075, code lost:
            if (r6 >= r3.length) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x007d, code lost:
            if (r2.get(r6) == r3[r6]) goto L_0x00ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x007f, code lost:
            r5 = r5 + java.lang.String.format(java.util.Locale.ENGLISH, " WHEN %1$s=%2$d THEN %3$d", new java.lang.Object[]{com.android.launcher3.LauncherSettings.Favorites.SCREEN, java.lang.Integer.valueOf(r2.get(r6)), java.lang.Integer.valueOf(r3[r6])});
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ae, code lost:
            r6 = r6 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00b5, code lost:
            if (android.text.TextUtils.isEmpty(r5) != false) goto L_0x00d8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x00b7, code lost:
            r9.execSQL(java.lang.String.format(java.util.Locale.ENGLISH, "UPDATE %1$s SET %2$s=CASE %3$s ELSE %2$s END WHERE %4$s = %5$d", new java.lang.Object[]{com.android.launcher3.LauncherSettings.Favorites.TABLE_NAME, com.android.launcher3.LauncherSettings.Favorites.SCREEN, r5, com.android.launcher3.LauncherSettings.Favorites.CONTAINER, -100}));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x00d8, code lost:
            com.android.launcher3.provider.LauncherDbUtils.dropTable(r9, "workspaceScreens");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00e5, code lost:
            if (addIntegerColumn(r9, com.android.launcher3.LauncherSettings.Favorites.APPWIDGET_SOURCE, -1) != false) goto L_0x00e8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00e8, code lost:
            r9.delete(com.android.launcher3.LauncherSettings.Favorites.TABLE_NAME, com.android.launcher3.Utilities.createDbSelectionQuery(com.android.launcher3.LauncherSettings.Favorites.SCREEN, com.android.launcher3.util.IntArray.wrap(-777, -778)), (java.lang.String[]) null);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onUpgrade(android.database.sqlite.SQLiteDatabase r16, int r17, int r18) {
            /*
                r15 = this;
                r1 = r15
                r9 = r16
                java.lang.String r0 = "favorites"
                java.lang.String r10 = "LauncherProvider"
                r11 = 2
                java.lang.String r12 = "screen"
                r2 = 0
                r13 = 1
                switch(r17) {
                    case 12: goto L_0x0012;
                    case 13: goto L_0x0012;
                    case 14: goto L_0x0022;
                    case 15: goto L_0x002c;
                    case 16: goto L_0x0036;
                    case 17: goto L_0x0036;
                    case 18: goto L_0x0036;
                    case 19: goto L_0x0036;
                    case 20: goto L_0x0044;
                    case 21: goto L_0x004c;
                    case 22: goto L_0x004c;
                    case 23: goto L_0x0056;
                    case 24: goto L_0x0056;
                    case 25: goto L_0x0056;
                    case 26: goto L_0x0059;
                    case 27: goto L_0x0059;
                    case 28: goto L_0x00dd;
                    case 29: goto L_0x00e8;
                    case 30: goto L_0x00f9;
                    default: goto L_0x0010;
                }
            L_0x0010:
                goto L_0x010e
            L_0x0012:
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r4 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x0106 }
                r4.<init>(r9)     // Catch:{ SQLException -> 0x0106 }
                java.lang.String r5 = "ALTER TABLE favorites ADD COLUMN appWidgetProvider TEXT;"
                r9.execSQL(r5)     // Catch:{ all -> 0x00fa }
                r4.commit()     // Catch:{ all -> 0x00fa }
                r4.close()     // Catch:{ SQLException -> 0x0106 }
            L_0x0022:
                java.lang.String r4 = "modified"
                boolean r4 = r15.addIntegerColumn(r9, r4, r2)
                if (r4 != 0) goto L_0x002c
                goto L_0x010e
            L_0x002c:
                java.lang.String r4 = "restored"
                boolean r4 = r15.addIntegerColumn(r9, r4, r2)
                if (r4 != 0) goto L_0x0036
                goto L_0x010e
            L_0x0036:
                long r4 = r15.getDefaultUserSerial()
                java.lang.String r6 = "profileId"
                boolean r4 = r15.addIntegerColumn(r9, r6, r4)
                if (r4 != 0) goto L_0x0044
                goto L_0x010e
            L_0x0044:
                boolean r4 = r15.updateFolderItemsRank(r9, r13)
                if (r4 != 0) goto L_0x004c
                goto L_0x010e
            L_0x004c:
                java.lang.String r4 = "options"
                boolean r2 = r15.addIntegerColumn(r9, r4, r2)
                if (r2 != 0) goto L_0x0056
                goto L_0x010e
            L_0x0056:
                r15.convertShortcutsToLauncherActivities(r16)
            L_0x0059:
                r2 = 0
                r6 = 0
                r7 = 0
                java.lang.String r4 = "workspaceScreens"
                java.lang.String r5 = "_id"
                java.lang.String r8 = "screenRank"
                r3 = r16
                com.android.launcher3.util.IntArray r2 = com.android.launcher3.provider.LauncherDbUtils.queryIntArray(r2, r3, r4, r5, r6, r7, r8)
                int[] r3 = r2.toArray()
                java.util.Arrays.sort(r3)
                r4 = 0
                java.lang.String r5 = ""
                r6 = r4
            L_0x0073:
                int r7 = r3.length
                r8 = 3
                if (r6 >= r7) goto L_0x00b1
                int r7 = r2.get(r6)
                r14 = r3[r6]
                if (r7 == r14) goto L_0x00ae
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                java.lang.StringBuilder r5 = r7.append(r5)
                java.util.Locale r7 = java.util.Locale.ENGLISH
                java.lang.Object[] r8 = new java.lang.Object[r8]
                r8[r4] = r12
                int r14 = r2.get(r6)
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                r8[r13] = r14
                r14 = r3[r6]
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                r8[r11] = r14
                java.lang.String r14 = " WHEN %1$s=%2$d THEN %3$d"
                java.lang.String r7 = java.lang.String.format(r7, r14, r8)
                java.lang.StringBuilder r5 = r5.append(r7)
                java.lang.String r5 = r5.toString()
            L_0x00ae:
                int r6 = r6 + 1
                goto L_0x0073
            L_0x00b1:
                boolean r2 = android.text.TextUtils.isEmpty(r5)
                if (r2 != 0) goto L_0x00d8
                java.util.Locale r2 = java.util.Locale.ENGLISH
                r3 = 5
                java.lang.Object[] r3 = new java.lang.Object[r3]
                r3[r4] = r0
                r3[r13] = r12
                r3[r11] = r5
                java.lang.String r4 = "container"
                r3[r8] = r4
                r4 = 4
                r5 = -100
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r3[r4] = r5
                java.lang.String r4 = "UPDATE %1$s SET %2$s=CASE %3$s ELSE %2$s END WHERE %4$s = %5$d"
                java.lang.String r2 = java.lang.String.format(r2, r4, r3)
                r9.execSQL(r2)
            L_0x00d8:
                java.lang.String r2 = "workspaceScreens"
                com.android.launcher3.provider.LauncherDbUtils.dropTable(r9, r2)
            L_0x00dd:
                r2 = -1
                java.lang.String r4 = "appWidgetSource"
                boolean r2 = r15.addIntegerColumn(r9, r4, r2)
                if (r2 != 0) goto L_0x00e8
                goto L_0x010e
            L_0x00e8:
                int[] r2 = new int[r11]
                r2 = {-777, -778} // fill-array
                com.android.launcher3.util.IntArray r2 = com.android.launcher3.util.IntArray.wrap(r2)
                java.lang.String r2 = com.android.launcher3.Utilities.createDbSelectionQuery(r12, r2)
                r3 = 0
                r9.delete(r0, r2, r3)
            L_0x00f9:
                return
            L_0x00fa:
                r0 = move-exception
                r2 = r0
                r4.close()     // Catch:{ all -> 0x0100 }
                goto L_0x0105
            L_0x0100:
                r0 = move-exception
                r3 = r0
                r2.addSuppressed(r3)     // Catch:{ SQLException -> 0x0106 }
            L_0x0105:
                throw r2     // Catch:{ SQLException -> 0x0106 }
            L_0x0106:
                r0 = move-exception
                java.lang.String r2 = r0.getMessage()
                android.util.Log.e(r10, r2, r0)
            L_0x010e:
                java.lang.String r0 = "Destroying all old data."
                android.util.Log.w(r10, r0)
                r15.createEmptyDB(r16)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase, int, int):void");
        }

        public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            try {
                DbDowngradeHelper.parse(this.mContext.getFileStreamPath(LauncherProvider.DOWNGRADE_SCHEMA_FILE)).onDowngrade(sQLiteDatabase, i, i2);
            } catch (Exception e) {
                Log.d(LauncherProvider.TAG, "Unable to downgrade from: " + i + " to " + i2 + ". Wiping databse.", e);
                createEmptyDB(sQLiteDatabase);
            }
        }

        public void createEmptyDB(SQLiteDatabase sQLiteDatabase) {
            LauncherDbUtils.SQLiteTransaction sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(sQLiteDatabase);
            try {
                LauncherDbUtils.dropTable(sQLiteDatabase, LauncherSettings.Favorites.TABLE_NAME);
                LauncherDbUtils.dropTable(sQLiteDatabase, "workspaceScreens");
                onCreate(sQLiteDatabase);
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }

        public void removeGhostWidgets(SQLiteDatabase sQLiteDatabase) {
            AppWidgetHost newLauncherWidgetHost = newLauncherWidgetHost();
            try {
                int[] appWidgetIds = newLauncherWidgetHost.getAppWidgetIds();
                IntSet wrap = IntSet.wrap(LauncherDbUtils.queryIntArray(false, sQLiteDatabase, LauncherSettings.Favorites.TABLE_NAME, LauncherSettings.Favorites.APPWIDGET_ID, "itemType=4", (String) null, (String) null));
                for (int i : appWidgetIds) {
                    if (!wrap.contains(i)) {
                        try {
                            FileLog.d(LauncherProvider.TAG, "Deleting invalid widget " + i);
                            newLauncherWidgetHost.deleteAppWidgetId(i);
                        } catch (RuntimeException unused) {
                        }
                    }
                }
            } catch (IncompatibleClassChangeError e) {
                Log.e(LauncherProvider.TAG, "getAppWidgetIds not supported", e);
            }
        }

        /* access modifiers changed from: package-private */
        public void convertShortcutsToLauncherActivities(SQLiteDatabase sQLiteDatabase) {
            try {
                LauncherDbUtils.SQLiteTransaction sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(sQLiteDatabase);
                try {
                    Cursor query = sQLiteDatabase.query(LauncherSettings.Favorites.TABLE_NAME, new String[]{"_id", LauncherSettings.Favorites.INTENT}, "itemType=1 AND profileId=" + getDefaultUserSerial(), (String[]) null, (String) null, (String) null, (String) null);
                    try {
                        SQLiteStatement compileStatement = sQLiteDatabase.compileStatement("UPDATE favorites SET itemType=0 WHERE _id=?");
                        try {
                            int columnIndexOrThrow = query.getColumnIndexOrThrow("_id");
                            int columnIndexOrThrow2 = query.getColumnIndexOrThrow(LauncherSettings.Favorites.INTENT);
                            while (query.moveToNext()) {
                                if (PackageManagerHelper.isLauncherAppTarget(Intent.parseUri(query.getString(columnIndexOrThrow2), 0))) {
                                    compileStatement.bindLong(1, (long) query.getInt(columnIndexOrThrow));
                                    compileStatement.executeUpdateDelete();
                                }
                            }
                            sQLiteTransaction.commit();
                            if (compileStatement != null) {
                                compileStatement.close();
                            }
                            if (query != null) {
                                query.close();
                            }
                            sQLiteTransaction.close();
                        } catch (URISyntaxException e) {
                            Log.e(LauncherProvider.TAG, "Unable to parse intent", e);
                        } catch (Throwable th) {
                            if (compileStatement != null) {
                                compileStatement.close();
                            }
                            throw th;
                        }
                    } catch (Throwable th2) {
                        if (query != null) {
                            query.close();
                        }
                        throw th2;
                    }
                } catch (Throwable th3) {
                    sQLiteTransaction.close();
                    throw th3;
                }
            } catch (SQLException e2) {
                Log.w(LauncherProvider.TAG, "Error deduping shortcuts", e2);
            } catch (Throwable th4) {
                th3.addSuppressed(th4);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean updateFolderItemsRank(SQLiteDatabase sQLiteDatabase, boolean z) {
            LauncherDbUtils.SQLiteTransaction sQLiteTransaction;
            try {
                sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(sQLiteDatabase);
                if (z) {
                    sQLiteDatabase.execSQL("ALTER TABLE favorites ADD COLUMN rank INTEGER NOT NULL DEFAULT 0;");
                }
                Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT container, MAX(cellX) FROM favorites WHERE container IN (SELECT _id FROM favorites WHERE itemType = ?) GROUP BY container;", new String[]{Integer.toString(2)});
                while (rawQuery.moveToNext()) {
                    sQLiteDatabase.execSQL("UPDATE favorites SET rank=cellX+(cellY*?) WHERE container=? AND cellX IS NOT NULL AND cellY IS NOT NULL;", new Object[]{Long.valueOf(rawQuery.getLong(1) + 1), Long.valueOf(rawQuery.getLong(0))});
                }
                rawQuery.close();
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return true;
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
                return false;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }

        private boolean addIntegerColumn(SQLiteDatabase sQLiteDatabase, String str, long j) {
            LauncherDbUtils.SQLiteTransaction sQLiteTransaction;
            try {
                sQLiteTransaction = new LauncherDbUtils.SQLiteTransaction(sQLiteDatabase);
                sQLiteDatabase.execSQL("ALTER TABLE favorites ADD COLUMN " + str + " INTEGER NOT NULL DEFAULT " + j + ";");
                sQLiteTransaction.commit();
                sQLiteTransaction.close();
                return true;
            } catch (SQLException e) {
                Log.e(LauncherProvider.TAG, e.getMessage(), e);
                return false;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }

        public int generateNewItemId() {
            int i = this.mMaxItemId;
            if (i >= 0) {
                int i2 = i + 1;
                this.mMaxItemId = i2;
                return i2;
            }
            throw new RuntimeException("Error: max item id was not initialized");
        }

        public AppWidgetHost newLauncherWidgetHost() {
            return new LauncherAppWidgetHost(this.mContext);
        }

        public int insertAndCheck(SQLiteDatabase sQLiteDatabase, ContentValues contentValues) {
            return LauncherProvider.dbInsertAndCheck(this, sQLiteDatabase, LauncherSettings.Favorites.TABLE_NAME, (String) null, contentValues);
        }

        public void checkId(ContentValues contentValues) {
            this.mMaxItemId = Math.max(contentValues.getAsInteger("_id").intValue(), this.mMaxItemId);
        }

        private int initializeMaxItemId(SQLiteDatabase sQLiteDatabase) {
            return LauncherProvider.getMaxId(sQLiteDatabase, "SELECT MAX(%1$s) FROM %2$s", "_id", LauncherSettings.Favorites.TABLE_NAME);
        }

        /* access modifiers changed from: private */
        public int getNewScreenId() {
            return LauncherProvider.getMaxId(getWritableDatabase(), "SELECT MAX(%1$s) FROM %2$s WHERE %3$s = %4$d AND %1$s >= 0", LauncherSettings.Favorites.SCREEN, LauncherSettings.Favorites.TABLE_NAME, LauncherSettings.Favorites.CONTAINER, -100) + 1;
        }

        /* access modifiers changed from: package-private */
        public int loadFavorites(SQLiteDatabase sQLiteDatabase, AutoInstallsLayout autoInstallsLayout) {
            int loadLayout = autoInstallsLayout.loadLayout(sQLiteDatabase, new IntArray());
            this.mMaxItemId = initializeMaxItemId(sQLiteDatabase);
            return loadLayout;
        }
    }

    static int getMaxId(SQLiteDatabase sQLiteDatabase, String str, Object... objArr) {
        SQLiteStatement compileStatement;
        int i = 0;
        try {
            compileStatement = sQLiteDatabase.compileStatement(String.format(Locale.ENGLISH, str, objArr));
            i = (int) DatabaseUtils.longForQuery(compileStatement, (String[]) null);
            if (i >= 0) {
                if (compileStatement != null) {
                    compileStatement.close();
                }
                return i;
            }
            throw new RuntimeException("Error: could not query max id");
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (!message.contains("re-open") || !message.contains("already-closed")) {
                throw e;
            }
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    static class SqlArguments {
        public final String[] args;
        public final String table;
        public final String where;

        SqlArguments(Uri uri, String str, String[] strArr) {
            if (uri.getPathSegments().size() == 1) {
                this.table = uri.getPathSegments().get(0);
                this.where = str;
                this.args = strArr;
            } else if (uri.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + uri);
            } else if (TextUtils.isEmpty(str)) {
                this.table = uri.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(uri);
                this.args = null;
            } else {
                throw new UnsupportedOperationException("WHERE clause not supported: " + uri);
            }
        }

        SqlArguments(Uri uri) {
            if (uri.getPathSegments().size() == 1) {
                this.table = uri.getPathSegments().get(0);
                this.where = null;
                this.args = null;
                return;
            }
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
    }
}
