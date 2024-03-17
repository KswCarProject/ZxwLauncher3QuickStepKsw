package com.android.launcher3.model;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInstaller;
import android.content.pm.ShortcutInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TimingLogger;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.folder.FolderNameInfos;
import com.android.launcher3.folder.FolderNameProvider;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.icons.cache.IconCacheUpdateHandler;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.IconRequestInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.InstallSessionHelper;
import com.android.launcher3.pm.PackageInstallInfo;
import com.android.launcher3.pm.UserCache;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

public class LoaderTask implements Runnable {
    private static final boolean DEBUG = true;
    private static final String TAG = "LoaderTask";
    protected final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    protected final BgDataModel mBgDataModel;
    private String mDbName;
    private FirstScreenBroadcast mFirstScreenBroadcast;
    private final IconCache mIconCache;
    private boolean mItemsDeleted = false;
    private final LauncherApps mLauncherApps;
    private final ModelDelegate mModelDelegate;
    private final Set<PackageUserKey> mPendingPackages = new HashSet();
    private final LoaderResults mResults;
    private final InstallSessionHelper mSessionHelper;
    private boolean mStopped;
    private final UserCache mUserCache;
    private final UserManager mUserManager;
    private final UserManagerState mUserManagerState = new UserManagerState();
    protected final Map<ComponentKey, AppWidgetProviderInfo> mWidgetProvidersMap = new ArrayMap();

    static /* synthetic */ void lambda$run$0(HashSet hashSet, UserHandle userHandle) {
    }

    public LoaderTask(LauncherAppState launcherAppState, AllAppsList allAppsList, BgDataModel bgDataModel, ModelDelegate modelDelegate, LoaderResults loaderResults) {
        this.mApp = launcherAppState;
        this.mBgAllAppsList = allAppsList;
        this.mBgDataModel = bgDataModel;
        this.mModelDelegate = modelDelegate;
        this.mResults = loaderResults;
        this.mLauncherApps = (LauncherApps) launcherAppState.getContext().getSystemService(LauncherApps.class);
        this.mUserManager = (UserManager) launcherAppState.getContext().getSystemService(UserManager.class);
        this.mUserCache = UserCache.INSTANCE.lambda$get$1$MainThreadInitializedObject(launcherAppState.getContext());
        this.mSessionHelper = InstallSessionHelper.INSTANCE.lambda$get$1$MainThreadInitializedObject(launcherAppState.getContext());
        this.mIconCache = launcherAppState.getIconCache();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x0007 A[LOOP:0: B:3:0x0007->B:6:0x0011, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void waitForIdle() {
        /*
            r3 = this;
            monitor-enter(r3)
            com.android.launcher3.model.LoaderResults r0 = r3.mResults     // Catch:{ all -> 0x0016 }
            com.android.launcher3.util.LooperIdleLock r0 = r0.newIdleLock(r3)     // Catch:{ all -> 0x0016 }
        L_0x0007:
            boolean r1 = r3.mStopped     // Catch:{ all -> 0x0016 }
            if (r1 != 0) goto L_0x0014
            r1 = 1000(0x3e8, double:4.94E-321)
            boolean r1 = r0.awaitLocked(r1)     // Catch:{ all -> 0x0016 }
            if (r1 == 0) goto L_0x0014
            goto L_0x0007
        L_0x0014:
            monitor-exit(r3)
            return
        L_0x0016:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.waitForIdle():void");
    }

    private synchronized void verifyNotStopped() throws CancellationException {
        if (this.mStopped) {
            throw new CancellationException("Loader stopped");
        }
    }

    private void sendFirstScreenActiveInstallsBroadcast() {
        ArrayList arrayList = new ArrayList();
        ModelUtils.filterCurrentWorkspaceItems(IntSet.wrap(this.mBgDataModel.collectWorkspaceScreens().get(0)), this.mBgDataModel.getAllWorkspaceItems(), arrayList, new ArrayList());
        this.mFirstScreenBroadcast.sendBroadcasts(this.mApp.getContext(), arrayList);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r4 = new java.util.ArrayList();
        android.os.Trace.beginSection("LoadWorkspace");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        loadWorkspace(r4, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        android.os.Trace.endSection();
        logASplit(r1, "loadWorkspace");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004b, code lost:
        if (r11.mApp.getInvariantDeviceProfile().dbFile.equals(r11.mDbName) == false) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        verifyNotStopped();
        sanitizeData();
        logASplit(r1, "sanitizeData");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0058, code lost:
        verifyNotStopped();
        r11.mResults.bindWorkspace(true);
        logASplit(r1, "bindWorkspace");
        r11.mModelDelegate.workspaceLoadComplete();
        sendFirstScreenActiveInstallsBroadcast();
        logASplit(r1, "sendFirstScreenActiveInstallsBroadcast");
        waitForIdle();
        logASplit(r1, "step 1 complete");
        verifyNotStopped();
        android.os.Trace.beginSection("LoadAllApps");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r5 = loadAllApps();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        android.os.Trace.endSection();
        logASplit(r1, "loadAllApps");
        verifyNotStopped();
        r11.mResults.bindAllApps();
        logASplit(r1, "bindAllApps");
        verifyNotStopped();
        r7 = r11.mIconCache.getUpdateHandler();
        setIgnorePackages(r7);
        r8 = com.android.launcher3.icons.LauncherActivityCachingLogic.newInstance(r11.mApp.getContext());
        r9 = r11.mApp.getModel();
        java.util.Objects.requireNonNull(r9);
        r7.updateIcons(r5, r8, new com.android.launcher3.model.$$Lambda$tFi_8Vq3vx5dtJb4Ge3ehK3lz48(r9));
        logASplit(r1, "update icon cache");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00ce, code lost:
        if (com.android.launcher3.config.FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE.get() == false) goto L_0x00ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00d0, code lost:
        verifyNotStopped();
        logASplit(r1, "save shortcuts in icon cache");
        r5 = new com.android.launcher3.icons.ShortcutCachingLogic();
        r8 = r11.mApp.getModel();
        java.util.Objects.requireNonNull(r8);
        r7.updateIcons(r4, r5, new com.android.launcher3.model.$$Lambda$tFi_8Vq3vx5dtJb4Ge3ehK3lz48(r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00ee, code lost:
        waitForIdle();
        logASplit(r1, "step 2 complete");
        verifyNotStopped();
        r4 = loadDeepShortcuts();
        logASplit(r1, "loadDeepShortcuts");
        verifyNotStopped();
        r11.mResults.bindDeepShortcuts();
        logASplit(r1, "bindDeepShortcuts");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0115, code lost:
        if (com.android.launcher3.config.FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE.get() == false) goto L_0x0129;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0117, code lost:
        verifyNotStopped();
        logASplit(r1, "save deep shortcuts in icon cache");
        r7.updateIcons(r4, new com.android.launcher3.icons.ShortcutCachingLogic(), com.android.launcher3.model.$$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI.INSTANCE);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0129, code lost:
        waitForIdle();
        logASplit(r1, "step 3 complete");
        verifyNotStopped();
        r4 = r11.mBgDataModel.widgetsModel.update(r11.mApp, (com.android.launcher3.util.PackageUserKey) null);
        logASplit(r1, "load widgets");
        verifyNotStopped();
        r11.mResults.bindWidgets();
        logASplit(r1, "bindWidgets");
        verifyNotStopped();
        r5 = new com.android.launcher3.icons.ComponentWithLabelAndIcon.ComponentWithIconCachingLogic(r11.mApp.getContext(), true);
        r6 = r11.mApp.getModel();
        java.util.Objects.requireNonNull(r6);
        r7.updateIcons(r4, r5, new com.android.launcher3.model.$$Lambda$F3aEw1UUOURAf7Y8Xbp13EZ7V1A(r6));
        logASplit(r1, "save widgets in icon cache");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x017b, code lost:
        if (com.android.launcher3.config.FeatureFlags.FOLDER_NAME_SUGGEST.get() == false) goto L_0x0180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x017d, code lost:
        loadFolderNames();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0180, code lost:
        verifyNotStopped();
        r7.finish();
        logASplit(r1, "finish icon update");
        r11.mModelDelegate.modelLoadComplete();
        r3.commit();
        r2.clearLogs();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0196, code lost:
        if (r3 == null) goto L_0x01be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x019c, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        android.os.Trace.endSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x01a0, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x01a1, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x01a2, code lost:
        android.os.Trace.endSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x01a5, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x01a6, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x01a7, code lost:
        if (r3 != null) goto L_0x01a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x01ad, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
        r4.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x01b1, code lost:
        throw r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x01b2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x01b4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        r2.printLogs();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x01b8, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x01b9, code lost:
        logASplit(r1, "Cancelled");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x01c7, code lost:
        r1.dumpToLog();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x01ca, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0008, code lost:
        r0 = com.android.launcher3.util.TraceHelper.INSTANCE.beginSection(TAG);
        r1 = new android.util.TimingLogger(TAG, "run");
        r2 = new com.android.launcher3.model.LoaderMemoryLogger();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
        r3 = r11.mApp.getModel().beginLoader(r11);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r11 = this;
            monitor-enter(r11)
            boolean r0 = r11.mStopped     // Catch:{ all -> 0x01cb }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r11)     // Catch:{ all -> 0x01cb }
            return
        L_0x0007:
            monitor-exit(r11)     // Catch:{ all -> 0x01cb }
            com.android.launcher3.util.TraceHelper r0 = com.android.launcher3.util.TraceHelper.INSTANCE
            java.lang.String r1 = "LoaderTask"
            java.lang.Object r0 = r0.beginSection(r1)
            android.util.TimingLogger r1 = new android.util.TimingLogger
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "run"
            r1.<init>(r2, r3)
            com.android.launcher3.model.LoaderMemoryLogger r2 = new com.android.launcher3.model.LoaderMemoryLogger
            r2.<init>()
            com.android.launcher3.LauncherAppState r3 = r11.mApp     // Catch:{ CancellationException -> 0x01b9, Exception -> 0x01b4 }
            com.android.launcher3.LauncherModel r3 = r3.getModel()     // Catch:{ CancellationException -> 0x01b9, Exception -> 0x01b4 }
            com.android.launcher3.LauncherModel$LoaderTransaction r3 = r3.beginLoader(r11)     // Catch:{ CancellationException -> 0x01b9, Exception -> 0x01b4 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x01a6 }
            r4.<init>()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "LoadWorkspace"
            android.os.Trace.beginSection(r5)     // Catch:{ all -> 0x01a6 }
            r11.loadWorkspace(r4, r2)     // Catch:{ all -> 0x01a1 }
            android.os.Trace.endSection()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "loadWorkspace"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r5 = r11.mApp     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.InvariantDeviceProfile r5 = r5.getInvariantDeviceProfile()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = r5.dbFile     // Catch:{ all -> 0x01a6 }
            java.lang.String r6 = r11.mDbName     // Catch:{ all -> 0x01a6 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x01a6 }
            if (r5 == 0) goto L_0x0058
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            r11.sanitizeData()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "sanitizeData"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
        L_0x0058:
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.LoaderResults r5 = r11.mResults     // Catch:{ all -> 0x01a6 }
            r6 = 1
            r5.bindWorkspace(r6)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "bindWorkspace"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.ModelDelegate r5 = r11.mModelDelegate     // Catch:{ all -> 0x01a6 }
            r5.workspaceLoadComplete()     // Catch:{ all -> 0x01a6 }
            r11.sendFirstScreenActiveInstallsBroadcast()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "sendFirstScreenActiveInstallsBroadcast"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            r11.waitForIdle()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "step 1 complete"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "LoadAllApps"
            android.os.Trace.beginSection(r5)     // Catch:{ all -> 0x01a6 }
            java.util.List r5 = r11.loadAllApps()     // Catch:{ all -> 0x019c }
            android.os.Trace.endSection()     // Catch:{ all -> 0x01a6 }
            java.lang.String r7 = "loadAllApps"
            logASplit(r1, r7)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.LoaderResults r7 = r11.mResults     // Catch:{ all -> 0x01a6 }
            r7.bindAllApps()     // Catch:{ all -> 0x01a6 }
            java.lang.String r7 = "bindAllApps"
            logASplit(r1, r7)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.icons.IconCache r7 = r11.mIconCache     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.icons.cache.IconCacheUpdateHandler r7 = r7.getUpdateHandler()     // Catch:{ all -> 0x01a6 }
            r11.setIgnorePackages(r7)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r8 = r11.mApp     // Catch:{ all -> 0x01a6 }
            android.content.Context r8 = r8.getContext()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.icons.LauncherActivityCachingLogic r8 = com.android.launcher3.icons.LauncherActivityCachingLogic.newInstance(r8)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r9 = r11.mApp     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherModel r9 = r9.getModel()     // Catch:{ all -> 0x01a6 }
            java.util.Objects.requireNonNull(r9)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.-$$Lambda$tFi_8Vq3vx5dtJb4Ge3ehK3lz48 r10 = new com.android.launcher3.model.-$$Lambda$tFi_8Vq3vx5dtJb4Ge3ehK3lz48     // Catch:{ all -> 0x01a6 }
            r10.<init>()     // Catch:{ all -> 0x01a6 }
            r7.updateIcons(r5, r8, r10)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "update icon cache"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.config.FeatureFlags$BooleanFlag r5 = com.android.launcher3.config.FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE     // Catch:{ all -> 0x01a6 }
            boolean r5 = r5.get()     // Catch:{ all -> 0x01a6 }
            if (r5 == 0) goto L_0x00ee
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "save shortcuts in icon cache"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.icons.ShortcutCachingLogic r5 = new com.android.launcher3.icons.ShortcutCachingLogic     // Catch:{ all -> 0x01a6 }
            r5.<init>()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r8 = r11.mApp     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherModel r8 = r8.getModel()     // Catch:{ all -> 0x01a6 }
            java.util.Objects.requireNonNull(r8)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.-$$Lambda$tFi_8Vq3vx5dtJb4Ge3ehK3lz48 r9 = new com.android.launcher3.model.-$$Lambda$tFi_8Vq3vx5dtJb4Ge3ehK3lz48     // Catch:{ all -> 0x01a6 }
            r9.<init>()     // Catch:{ all -> 0x01a6 }
            r7.updateIcons(r4, r5, r9)     // Catch:{ all -> 0x01a6 }
        L_0x00ee:
            r11.waitForIdle()     // Catch:{ all -> 0x01a6 }
            java.lang.String r4 = "step 2 complete"
            logASplit(r1, r4)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            java.util.List r4 = r11.loadDeepShortcuts()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "loadDeepShortcuts"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.LoaderResults r5 = r11.mResults     // Catch:{ all -> 0x01a6 }
            r5.bindDeepShortcuts()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "bindDeepShortcuts"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.config.FeatureFlags$BooleanFlag r5 = com.android.launcher3.config.FeatureFlags.ENABLE_DEEP_SHORTCUT_ICON_CACHE     // Catch:{ all -> 0x01a6 }
            boolean r5 = r5.get()     // Catch:{ all -> 0x01a6 }
            if (r5 == 0) goto L_0x0129
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "save deep shortcuts in icon cache"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.icons.ShortcutCachingLogic r5 = new com.android.launcher3.icons.ShortcutCachingLogic     // Catch:{ all -> 0x01a6 }
            r5.<init>()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.-$$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI r8 = com.android.launcher3.model.$$Lambda$LoaderTask$cAJXD07GePZf3zBbuQLMQDZqXvI.INSTANCE     // Catch:{ all -> 0x01a6 }
            r7.updateIcons(r4, r5, r8)     // Catch:{ all -> 0x01a6 }
        L_0x0129:
            r11.waitForIdle()     // Catch:{ all -> 0x01a6 }
            java.lang.String r4 = "step 3 complete"
            logASplit(r1, r4)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.BgDataModel r4 = r11.mBgDataModel     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.WidgetsModel r4 = r4.widgetsModel     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r5 = r11.mApp     // Catch:{ all -> 0x01a6 }
            r8 = 0
            java.util.List r4 = r4.update(r5, r8)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "load widgets"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.LoaderResults r5 = r11.mResults     // Catch:{ all -> 0x01a6 }
            r5.bindWidgets()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "bindWidgets"
            logASplit(r1, r5)     // Catch:{ all -> 0x01a6 }
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.icons.ComponentWithLabelAndIcon$ComponentWithIconCachingLogic r5 = new com.android.launcher3.icons.ComponentWithLabelAndIcon$ComponentWithIconCachingLogic     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r8 = r11.mApp     // Catch:{ all -> 0x01a6 }
            android.content.Context r8 = r8.getContext()     // Catch:{ all -> 0x01a6 }
            r5.<init>(r8, r6)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherAppState r6 = r11.mApp     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.LauncherModel r6 = r6.getModel()     // Catch:{ all -> 0x01a6 }
            java.util.Objects.requireNonNull(r6)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.-$$Lambda$F3aEw1UUOURAf7Y8Xbp13EZ7V1A r8 = new com.android.launcher3.model.-$$Lambda$F3aEw1UUOURAf7Y8Xbp13EZ7V1A     // Catch:{ all -> 0x01a6 }
            r8.<init>()     // Catch:{ all -> 0x01a6 }
            r7.updateIcons(r4, r5, r8)     // Catch:{ all -> 0x01a6 }
            java.lang.String r4 = "save widgets in icon cache"
            logASplit(r1, r4)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.config.FeatureFlags$BooleanFlag r4 = com.android.launcher3.config.FeatureFlags.FOLDER_NAME_SUGGEST     // Catch:{ all -> 0x01a6 }
            boolean r4 = r4.get()     // Catch:{ all -> 0x01a6 }
            if (r4 == 0) goto L_0x0180
            r11.loadFolderNames()     // Catch:{ all -> 0x01a6 }
        L_0x0180:
            r11.verifyNotStopped()     // Catch:{ all -> 0x01a6 }
            r7.finish()     // Catch:{ all -> 0x01a6 }
            java.lang.String r4 = "finish icon update"
            logASplit(r1, r4)     // Catch:{ all -> 0x01a6 }
            com.android.launcher3.model.ModelDelegate r4 = r11.mModelDelegate     // Catch:{ all -> 0x01a6 }
            r4.modelLoadComplete()     // Catch:{ all -> 0x01a6 }
            r3.commit()     // Catch:{ all -> 0x01a6 }
            r2.clearLogs()     // Catch:{ all -> 0x01a6 }
            if (r3 == 0) goto L_0x01be
            r3.close()     // Catch:{ CancellationException -> 0x01b9, Exception -> 0x01b4 }
            goto L_0x01be
        L_0x019c:
            r4 = move-exception
            android.os.Trace.endSection()     // Catch:{ all -> 0x01a6 }
            throw r4     // Catch:{ all -> 0x01a6 }
        L_0x01a1:
            r4 = move-exception
            android.os.Trace.endSection()     // Catch:{ all -> 0x01a6 }
            throw r4     // Catch:{ all -> 0x01a6 }
        L_0x01a6:
            r4 = move-exception
            if (r3 == 0) goto L_0x01b1
            r3.close()     // Catch:{ all -> 0x01ad }
            goto L_0x01b1
        L_0x01ad:
            r3 = move-exception
            r4.addSuppressed(r3)     // Catch:{ CancellationException -> 0x01b9, Exception -> 0x01b4 }
        L_0x01b1:
            throw r4     // Catch:{ CancellationException -> 0x01b9, Exception -> 0x01b4 }
        L_0x01b2:
            r0 = move-exception
            goto L_0x01c7
        L_0x01b4:
            r0 = move-exception
            r2.printLogs()     // Catch:{ all -> 0x01b2 }
            throw r0     // Catch:{ all -> 0x01b2 }
        L_0x01b9:
            java.lang.String r2 = "Cancelled"
            logASplit(r1, r2)     // Catch:{ all -> 0x01b2 }
        L_0x01be:
            r1.dumpToLog()
            com.android.launcher3.util.TraceHelper r1 = com.android.launcher3.util.TraceHelper.INSTANCE
            r1.endSection(r0)
            return
        L_0x01c7:
            r1.dumpToLog()
            throw r0
        L_0x01cb:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x01cb }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.run():void");
    }

    public synchronized void stopLocked() {
        this.mStopped = true;
        notify();
    }

    private void loadWorkspace(List<ShortcutInfo> list, LoaderMemoryLogger loaderMemoryLogger) {
        loadWorkspace(list, LauncherSettings.Favorites.CONTENT_URI, (String) null, loaderMemoryLogger);
    }

    /* access modifiers changed from: protected */
    public void loadWorkspace(List<ShortcutInfo> list, Uri uri, String str) {
        loadWorkspace(list, uri, str, (LoaderMemoryLogger) null);
    }

    /* JADX INFO: finally extract failed */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v29, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v66, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v68, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v69, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v82, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v87, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v80, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v81, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v85, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v86, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v89, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v91, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v96, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v97, resolved type: android.content.Context} */
    /* JADX WARNING: type inference failed for: r9v0, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r6v2, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r4v6, types: [java.util.Map] */
    /* JADX WARNING: type inference failed for: r3v15 */
    /* JADX WARNING: type inference failed for: r3v16 */
    /* JADX WARNING: type inference failed for: r3v17 */
    /* JADX WARNING: type inference failed for: r3v18 */
    /* JADX WARNING: type inference failed for: r3v19 */
    /* JADX WARNING: type inference failed for: r3v21 */
    /* JADX WARNING: type inference failed for: r3v22 */
    /* JADX WARNING: type inference failed for: r3v26, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r12v13 */
    /* JADX WARNING: type inference failed for: r4v48, types: [com.android.launcher3.model.data.IconRequestInfo, java.lang.Object] */
    /* JADX WARNING: type inference failed for: r12v16 */
    /* JADX WARNING: type inference failed for: r11v38, types: [java.util.Map] */
    /* JADX WARNING: type inference failed for: r3v32 */
    /* JADX WARNING: type inference failed for: r3v33 */
    /* JADX WARNING: type inference failed for: r3v34 */
    /* JADX WARNING: type inference failed for: r8v41, types: [boolean] */
    /* JADX WARNING: type inference failed for: r3v51, types: [java.util.HashMap] */
    /* JADX WARNING: type inference failed for: r3v60 */
    /* JADX WARNING: type inference failed for: r3v61 */
    /* JADX WARNING: type inference failed for: r3v62 */
    /* JADX WARNING: type inference failed for: r3v63 */
    /* JADX WARNING: type inference failed for: r8v54, types: [java.util.Map, java.util.Map<com.android.launcher3.util.ComponentKey, android.appwidget.AppWidgetProviderInfo>] */
    /* JADX WARNING: type inference failed for: r3v64 */
    /* JADX WARNING: type inference failed for: r8v64, types: [java.util.Map] */
    /* JADX WARNING: type inference failed for: r3v74 */
    /* JADX WARNING: type inference failed for: r4v97 */
    /* JADX WARNING: type inference failed for: r4v100 */
    /* JADX WARNING: type inference failed for: r12v74 */
    /* JADX WARNING: type inference failed for: r12v78 */
    /* JADX WARNING: type inference failed for: r12v80 */
    /* JADX WARNING: type inference failed for: r12v82 */
    /* JADX WARNING: type inference failed for: r12v84 */
    /* JADX WARNING: type inference failed for: r3v78 */
    /* JADX WARNING: type inference failed for: r3v82 */
    /* JADX WARNING: type inference failed for: r3v83 */
    /* JADX WARNING: type inference failed for: r3v88 */
    /* JADX WARNING: type inference failed for: r3v93 */
    /* JADX WARNING: type inference failed for: r3v95 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x03ac, code lost:
        if (r4.spanY < r10.minSpanY) goto L_0x03b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x04d7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x04d8, code lost:
        r32 = r3;
        r33 = r8;
        r8 = r21;
        r3 = r25;
        r4 = r37;
        r34 = r10;
        r25 = r13;
        r20 = r18;
        r12 = r32;
        r13 = r36;
        r10 = r39;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x097d, code lost:
        r0 = e;
        r12 = r12;
        r6 = r6;
        r4 = r4;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0420 A[Catch:{ Exception -> 0x0585 }] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0427 A[Catch:{ Exception -> 0x0585 }] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0698 A[Catch:{ Exception -> 0x0740 }] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x06f5 A[Catch:{ Exception -> 0x0740 }] */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x074b  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x074e A[SYNTHETIC, Splitter:B:322:0x074e] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x0757 A[Catch:{ Exception -> 0x0987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x0759 A[Catch:{ Exception -> 0x0987 }] */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x075e A[SYNTHETIC, Splitter:B:331:0x075e] */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x076a A[SYNTHETIC, Splitter:B:334:0x076a] */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0899 A[SYNTHETIC, Splitter:B:424:0x0899] */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x096d A[SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadWorkspace(java.util.List<android.content.pm.ShortcutInfo> r36, android.net.Uri r37, java.lang.String r38, com.android.launcher3.model.LoaderMemoryLogger r39) {
        /*
            r35 = this;
            r1 = r35
            r2 = r39
            com.android.launcher3.LauncherAppState r3 = r1.mApp
            android.content.Context r3 = r3.getContext()
            android.content.ContentResolver r4 = r3.getContentResolver()
            com.android.launcher3.util.PackageManagerHelper r10 = new com.android.launcher3.util.PackageManagerHelper
            r10.<init>(r3)
            boolean r11 = r10.isSafeMode()
            boolean r12 = com.android.launcher3.Utilities.isBootCompleted()
            com.android.launcher3.widget.WidgetManagerHelper r13 = new com.android.launcher3.widget.WidgetManagerHelper
            r13.<init>(r3)
            boolean r5 = com.android.launcher3.model.GridSizeMigrationTaskV2.migrateGridIfNeeded(r3)
            r14 = 1
            r5 = r5 ^ r14
            if (r5 == 0) goto L_0x0034
            java.lang.String r5 = "LoaderTask"
            java.lang.String r6 = "loadWorkspace: resetting launcher database"
            android.util.Log.d(r5, r6)
            java.lang.String r5 = "create_empty_db"
            com.android.launcher3.LauncherSettings.Settings.call(r4, r5)
        L_0x0034:
            java.lang.String r5 = "LoaderTask"
            java.lang.String r6 = "loadWorkspace: loading default favorites"
            android.util.Log.d(r5, r6)
            java.lang.String r5 = "load_default_favorites"
            com.android.launcher3.LauncherSettings.Settings.call(r4, r5)
            com.android.launcher3.model.BgDataModel r15 = r1.mBgDataModel
            monitor-enter(r15)
            com.android.launcher3.model.BgDataModel r5 = r1.mBgDataModel     // Catch:{ all -> 0x0aa4 }
            r5.clear()     // Catch:{ all -> 0x0aa4 }
            java.util.Set<com.android.launcher3.util.PackageUserKey> r5 = r1.mPendingPackages     // Catch:{ all -> 0x0aa4 }
            r5.clear()     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.pm.InstallSessionHelper r5 = r1.mSessionHelper     // Catch:{ all -> 0x0aa4 }
            java.util.HashMap r9 = r5.getActiveSessions()     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.LauncherAppState r5 = r1.mApp     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.icons.IconCache r5 = r5.getIconCache()     // Catch:{ all -> 0x0aa4 }
            java.util.Objects.requireNonNull(r5)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.-$$Lambda$8S7WfzWWJl_esuzA7sm4NY3Gz3w r6 = new com.android.launcher3.model.-$$Lambda$8S7WfzWWJl_esuzA7sm4NY3Gz3w     // Catch:{ all -> 0x0aa4 }
            r6.<init>()     // Catch:{ all -> 0x0aa4 }
            r9.forEach(r6)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.util.PackageUserKey r8 = new com.android.launcher3.util.PackageUserKey     // Catch:{ all -> 0x0aa4 }
            r7 = 0
            r8.<init>((java.lang.String) r7, (android.os.UserHandle) r7)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.FirstScreenBroadcast r5 = new com.android.launcher3.model.FirstScreenBroadcast     // Catch:{ all -> 0x0aa4 }
            r5.<init>(r9)     // Catch:{ all -> 0x0aa4 }
            r1.mFirstScreenBroadcast = r5     // Catch:{ all -> 0x0aa4 }
            java.util.HashMap r6 = new java.util.HashMap     // Catch:{ all -> 0x0aa4 }
            r6.<init>()     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.LoaderCursor r5 = new com.android.launcher3.model.LoaderCursor     // Catch:{ all -> 0x0aa4 }
            r16 = 0
            r17 = 0
            r18 = 0
            r14 = r5
            r5 = r37
            r20 = r6
            r6 = r16
            r16 = r7
            r7 = r38
            r21 = r8
            r8 = r17
            r17 = r12
            r12 = r9
            r9 = r18
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.LauncherAppState r5 = r1.mApp     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.UserManagerState r6 = r1.mUserManagerState     // Catch:{ all -> 0x0aa4 }
            r7 = r37
            r14.<init>(r4, r7, r5, r6)     // Catch:{ all -> 0x0aa4 }
            android.os.Bundle r4 = r14.getExtras()     // Catch:{ all -> 0x0aa4 }
            if (r4 != 0) goto L_0x00a8
            r7 = r16
            goto L_0x00ae
        L_0x00a8:
            java.lang.String r5 = "db_name"
            java.lang.String r7 = r4.getString(r5)     // Catch:{ all -> 0x0aa4 }
        L_0x00ae:
            r1.mDbName = r7     // Catch:{ all -> 0x0aa4 }
            java.lang.String r4 = "appWidgetId"
            int r4 = r14.getColumnIndexOrThrow(r4)     // Catch:{ all -> 0x0a9e }
            java.lang.String r5 = "appWidgetProvider"
            int r5 = r14.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0a9e }
            java.lang.String r6 = "spanX"
            int r6 = r14.getColumnIndexOrThrow(r6)     // Catch:{ all -> 0x0a9e }
            java.lang.String r7 = "spanY"
            int r7 = r14.getColumnIndexOrThrow(r7)     // Catch:{ all -> 0x0a9e }
            java.lang.String r8 = "rank"
            int r8 = r14.getColumnIndexOrThrow(r8)     // Catch:{ all -> 0x0a9e }
            java.lang.String r9 = "options"
            int r9 = r14.getColumnIndexOrThrow(r9)     // Catch:{ all -> 0x0a9e }
            r37 = r8
            java.lang.String r8 = "appWidgetSource"
            int r8 = r14.getColumnIndexOrThrow(r8)     // Catch:{ all -> 0x0a9e }
            r18 = r10
            android.util.LongSparseArray r10 = new android.util.LongSparseArray     // Catch:{ all -> 0x0a9e }
            r10.<init>()     // Catch:{ all -> 0x0a9e }
            com.android.launcher3.model.UserManagerState r2 = r1.mUserManagerState     // Catch:{ all -> 0x0a9e }
            r38 = r8
            com.android.launcher3.pm.UserCache r8 = r1.mUserCache     // Catch:{ all -> 0x0a9e }
            r22 = r7
            android.os.UserManager r7 = r1.mUserManager     // Catch:{ all -> 0x0a9e }
            r2.init(r8, r7)     // Catch:{ all -> 0x0a9e }
            com.android.launcher3.pm.UserCache r2 = r1.mUserCache     // Catch:{ all -> 0x0a9e }
            java.util.List r2 = r2.getUserProfiles()     // Catch:{ all -> 0x0a9e }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0a9e }
        L_0x00fa:
            boolean r7 = r2.hasNext()     // Catch:{ all -> 0x0a9e }
            if (r7 == 0) goto L_0x0169
            java.lang.Object r7 = r2.next()     // Catch:{ all -> 0x0a9e }
            android.os.UserHandle r7 = (android.os.UserHandle) r7     // Catch:{ all -> 0x0a9e }
            com.android.launcher3.pm.UserCache r8 = r1.mUserCache     // Catch:{ all -> 0x0a9e }
            r24 = r11
            r25 = r12
            long r11 = r8.getSerialNumberForUser(r7)     // Catch:{ all -> 0x0a9e }
            android.os.UserManager r8 = r1.mUserManager     // Catch:{ all -> 0x0a9e }
            boolean r8 = r8.isUserUnlocked(r7)     // Catch:{ all -> 0x0a9e }
            if (r8 == 0) goto L_0x0153
            r26 = r2
            com.android.launcher3.shortcuts.ShortcutRequest r2 = new com.android.launcher3.shortcuts.ShortcutRequest     // Catch:{ all -> 0x0a9e }
            r2.<init>(r3, r7)     // Catch:{ all -> 0x0a9e }
            r7 = 2
            com.android.launcher3.shortcuts.ShortcutRequest$QueryResult r2 = r2.query(r7)     // Catch:{ all -> 0x0a9e }
            boolean r7 = r2.wasSuccess()     // Catch:{ all -> 0x0a9e }
            if (r7 == 0) goto L_0x014e
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0a9e }
        L_0x012e:
            boolean r7 = r2.hasNext()     // Catch:{ all -> 0x0a9e }
            if (r7 == 0) goto L_0x0155
            java.lang.Object r7 = r2.next()     // Catch:{ all -> 0x0a9e }
            android.content.pm.ShortcutInfo r7 = (android.content.pm.ShortcutInfo) r7     // Catch:{ all -> 0x0a9e }
            r23 = r2
            com.android.launcher3.shortcuts.ShortcutKey r2 = com.android.launcher3.shortcuts.ShortcutKey.fromInfo(r7)     // Catch:{ all -> 0x0a9e }
            r27 = r8
            r8 = r20
            r8.put(r2, r7)     // Catch:{ all -> 0x0a9e }
            r20 = r8
            r2 = r23
            r8 = r27
            goto L_0x012e
        L_0x014e:
            r8 = r20
            r27 = 0
            goto L_0x0159
        L_0x0153:
            r26 = r2
        L_0x0155:
            r27 = r8
            r8 = r20
        L_0x0159:
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r27)     // Catch:{ all -> 0x0a9e }
            r10.put(r11, r2)     // Catch:{ all -> 0x0a9e }
            r20 = r8
            r11 = r24
            r12 = r25
            r2 = r26
            goto L_0x00fa
        L_0x0169:
            r24 = r11
            r25 = r12
            r8 = r20
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x0a9e }
            r2.<init>()     // Catch:{ all -> 0x0a9e }
        L_0x0174:
            boolean r7 = r1.mStopped     // Catch:{ all -> 0x0a9e }
            if (r7 != 0) goto L_0x09d1
            boolean r7 = r14.moveToNext()     // Catch:{ all -> 0x0a9e }
            if (r7 == 0) goto L_0x09d1
            android.os.UserHandle r7 = r14.user     // Catch:{ Exception -> 0x09aa }
            if (r7 != 0) goto L_0x0188
            java.lang.String r7 = "User has been deleted"
            r14.markDeleted(r7)     // Catch:{ Exception -> 0x09aa }
            goto L_0x0174
        L_0x0188:
            int r7 = r14.itemType     // Catch:{ Exception -> 0x09aa }
            r11 = 4
            if (r7 == 0) goto L_0x01b9
            r12 = 1
            if (r7 == r12) goto L_0x01b9
            r12 = 2
            if (r7 == r12) goto L_0x0529
            r12 = 5
            if (r7 == r11) goto L_0x01cd
            if (r7 == r12) goto L_0x01cd
            r11 = 6
            if (r7 == r11) goto L_0x01b9
            r12 = r3
            r29 = r4
            r30 = r5
            r33 = r8
            r34 = r10
            r20 = r18
            r8 = r21
            r3 = r25
            r7 = 1
            r11 = 2
            r4 = r37
            r10 = r39
            r18 = r6
            r25 = r13
            r13 = r36
            r6 = r2
            goto L_0x0932
        L_0x01b9:
            r31 = r2
            r32 = r3
            r29 = r4
            r30 = r5
            r33 = r8
            r34 = r10
            r8 = r21
            r3 = r25
            r5 = r39
            goto L_0x059b
        L_0x01cd:
            int r7 = r14.itemType     // Catch:{ Exception -> 0x0508 }
            if (r7 != r12) goto L_0x01d3
            r7 = 1
            goto L_0x01d4
        L_0x01d3:
            r7 = 0
        L_0x01d4:
            int r11 = r14.getInt(r4)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r12 = r14.getString(r5)     // Catch:{ Exception -> 0x0508 }
            int r28 = r14.getInt(r9)     // Catch:{ Exception -> 0x0508 }
            r19 = 1
            r28 = r28 & 1
            if (r28 == 0) goto L_0x01e9
            r28 = 1
            goto L_0x01eb
        L_0x01e9:
            r28 = 0
        L_0x01eb:
            if (r28 == 0) goto L_0x01fa
            android.content.ComponentName r28 = com.android.launcher3.qsb.QsbContainerView.getSearchComponentName(r3)     // Catch:{ Exception -> 0x09aa }
            if (r28 != 0) goto L_0x01fe
            java.lang.String r7 = "Discarding SearchWidget without packagename "
            r14.markDeleted(r7)     // Catch:{ Exception -> 0x09aa }
            goto L_0x0174
        L_0x01fa:
            android.content.ComponentName r28 = android.content.ComponentName.unflattenFromString(r12)     // Catch:{ Exception -> 0x0508 }
        L_0x01fe:
            r29 = r4
            r30 = r5
            r4 = r28
            r5 = 1
            boolean r28 = r14.hasRestoreFlag(r5)     // Catch:{ Exception -> 0x04f3 }
            r31 = r2
            r2 = 2
            if (r28 != 0) goto L_0x0210
            r5 = 1
            goto L_0x0211
        L_0x0210:
            r5 = 0
        L_0x0211:
            boolean r28 = r14.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x04d7 }
            r32 = r3
            if (r28 != 0) goto L_0x021b
            r2 = 1
            goto L_0x021c
        L_0x021b:
            r2 = 0
        L_0x021c:
            com.android.launcher3.util.ComponentKey r3 = new com.android.launcher3.util.ComponentKey     // Catch:{ Exception -> 0x04bd }
            r33 = r8
            android.os.UserHandle r8 = r14.user     // Catch:{ Exception -> 0x04bb }
            r3.<init>(r4, r8)     // Catch:{ Exception -> 0x04bb }
            java.util.Map<com.android.launcher3.util.ComponentKey, android.appwidget.AppWidgetProviderInfo> r8 = r1.mWidgetProvidersMap     // Catch:{ Exception -> 0x04bb }
            boolean r8 = r8.containsKey(r3)     // Catch:{ Exception -> 0x04bb }
            if (r8 != 0) goto L_0x0268
            java.util.Map<com.android.launcher3.util.ComponentKey, android.appwidget.AppWidgetProviderInfo> r8 = r1.mWidgetProvidersMap     // Catch:{ Exception -> 0x024c }
            r34 = r10
            android.os.UserHandle r10 = r14.user     // Catch:{ Exception -> 0x023b }
            com.android.launcher3.widget.LauncherAppWidgetProviderInfo r10 = r13.findProvider(r4, r10)     // Catch:{ Exception -> 0x023b }
            r8.put(r3, r10)     // Catch:{ Exception -> 0x023b }
            goto L_0x026a
        L_0x023b:
            r0 = move-exception
            r4 = r37
            r10 = r39
            r2 = r0
            r20 = r18
            r8 = r21
            r3 = r25
            r12 = r32
            r7 = 1
            r11 = 2
            goto L_0x025e
        L_0x024c:
            r0 = move-exception
            r4 = r37
            r2 = r0
            r34 = r10
            r20 = r18
            r8 = r21
            r3 = r25
            r12 = r32
            r7 = 1
            r11 = 2
            r10 = r39
        L_0x025e:
            r18 = r6
            r25 = r13
            r6 = r31
            r13 = r36
            goto L_0x09c8
        L_0x0268:
            r34 = r10
        L_0x026a:
            java.util.Map<com.android.launcher3.util.ComponentKey, android.appwidget.AppWidgetProviderInfo> r8 = r1.mWidgetProvidersMap     // Catch:{ Exception -> 0x04b4 }
            java.lang.Object r3 = r8.get(r3)     // Catch:{ Exception -> 0x04b4 }
            android.appwidget.AppWidgetProviderInfo r3 = (android.appwidget.AppWidgetProviderInfo) r3     // Catch:{ Exception -> 0x04b4 }
            boolean r8 = isValidProvider(r3)     // Catch:{ Exception -> 0x04b4 }
            if (r24 != 0) goto L_0x02ac
            if (r7 != 0) goto L_0x02ac
            if (r2 == 0) goto L_0x02ac
            if (r8 != 0) goto L_0x02ac
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023b }
            r2.<init>()     // Catch:{ Exception -> 0x023b }
            java.lang.String r4 = "Deleting widget that isn't installed anymore: "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ Exception -> 0x023b }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ Exception -> 0x023b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x023b }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x023b }
            r4 = r37
            r10 = r39
            r20 = r18
            r8 = r21
            r3 = r25
            r12 = r32
            r7 = 1
            r11 = 2
            r18 = r6
            r25 = r13
            r6 = r31
            r13 = r36
            goto L_0x0932
        L_0x02ac:
            if (r8 == 0) goto L_0x02cb
            com.android.launcher3.model.data.LauncherAppWidgetInfo r4 = new com.android.launcher3.model.data.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x023b }
            android.content.ComponentName r3 = r3.provider     // Catch:{ Exception -> 0x023b }
            r4.<init>(r11, r3)     // Catch:{ Exception -> 0x023b }
            int r3 = r14.restoreFlag     // Catch:{ Exception -> 0x023b }
            r3 = r3 & -9
            r3 = r3 & -3
            if (r2 != 0) goto L_0x02c1
            if (r5 == 0) goto L_0x02c1
            r3 = r3 | 4
        L_0x02c1:
            r4.restoreStatus = r3     // Catch:{ Exception -> 0x023b }
            r8 = r21
            r3 = r25
        L_0x02c7:
            r2 = 32
            goto L_0x0361
        L_0x02cb:
            java.lang.String r2 = "LoaderTask"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04b4 }
            r3.<init>()     // Catch:{ Exception -> 0x04b4 }
            java.lang.String r5 = "Widget restore pending id="
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Exception -> 0x04b4 }
            int r5 = r14.id     // Catch:{ Exception -> 0x04b4 }
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Exception -> 0x04b4 }
            java.lang.String r5 = " appWidgetId="
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Exception -> 0x04b4 }
            java.lang.StringBuilder r3 = r3.append(r11)     // Catch:{ Exception -> 0x04b4 }
            java.lang.String r5 = " status ="
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Exception -> 0x04b4 }
            int r5 = r14.restoreFlag     // Catch:{ Exception -> 0x04b4 }
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ Exception -> 0x04b4 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x04b4 }
            android.util.Log.v(r2, r3)     // Catch:{ Exception -> 0x04b4 }
            com.android.launcher3.model.data.LauncherAppWidgetInfo r2 = new com.android.launcher3.model.data.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x04b4 }
            r2.<init>(r11, r4)     // Catch:{ Exception -> 0x04b4 }
            int r3 = r14.restoreFlag     // Catch:{ Exception -> 0x04b4 }
            r2.restoreStatus = r3     // Catch:{ Exception -> 0x04b4 }
            java.lang.String r3 = r4.getPackageName()     // Catch:{ Exception -> 0x04b4 }
            android.os.UserHandle r5 = r14.user     // Catch:{ Exception -> 0x04b4 }
            r8 = r21
            r8.update(r3, r5)     // Catch:{ Exception -> 0x04b2 }
            r3 = r25
            java.lang.Object r5 = r3.get(r8)     // Catch:{ Exception -> 0x0585 }
            android.content.pm.PackageInstaller$SessionInfo r5 = (android.content.pm.PackageInstaller.SessionInfo) r5     // Catch:{ Exception -> 0x0585 }
            if (r5 != 0) goto L_0x031e
            r5 = r16
        L_0x031b:
            r10 = 8
            goto L_0x032b
        L_0x031e:
            float r5 = r5.getProgress()     // Catch:{ Exception -> 0x0585 }
            r10 = 1120403456(0x42c80000, float:100.0)
            float r5 = r5 * r10
            int r5 = (int) r5     // Catch:{ Exception -> 0x0585 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0585 }
            goto L_0x031b
        L_0x032b:
            boolean r21 = r14.hasRestoreFlag(r10)     // Catch:{ Exception -> 0x0585 }
            if (r21 == 0) goto L_0x0332
            goto L_0x0354
        L_0x0332:
            if (r5 == 0) goto L_0x033a
            int r4 = r2.restoreStatus     // Catch:{ Exception -> 0x0585 }
            r4 = r4 | r10
            r2.restoreStatus = r4     // Catch:{ Exception -> 0x0585 }
            goto L_0x0354
        L_0x033a:
            if (r24 != 0) goto L_0x0354
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0585 }
            r2.<init>()     // Catch:{ Exception -> 0x0585 }
            java.lang.String r5 = "Unrestored widget removed: "
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0585 }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0585 }
            goto L_0x05a6
        L_0x0354:
            if (r5 != 0) goto L_0x0358
            r4 = 0
            goto L_0x035c
        L_0x0358:
            int r4 = r5.intValue()     // Catch:{ Exception -> 0x0585 }
        L_0x035c:
            r2.installProgress = r4     // Catch:{ Exception -> 0x0585 }
            r4 = r2
            goto L_0x02c7
        L_0x0361:
            boolean r2 = r4.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x0585 }
            if (r2 == 0) goto L_0x036d
            android.content.Intent r2 = r14.parseIntent()     // Catch:{ Exception -> 0x0585 }
            r4.bindOptions = r2     // Catch:{ Exception -> 0x0585 }
        L_0x036d:
            r14.applyCommonProperties(r4)     // Catch:{ Exception -> 0x0585 }
            int r2 = r14.getInt(r6)     // Catch:{ Exception -> 0x0585 }
            r4.spanX = r2     // Catch:{ Exception -> 0x0585 }
            r2 = r22
            int r5 = r14.getInt(r2)     // Catch:{ Exception -> 0x04ad }
            r4.spanY = r5     // Catch:{ Exception -> 0x04ad }
            int r5 = r14.getInt(r9)     // Catch:{ Exception -> 0x04ad }
            r4.options = r5     // Catch:{ Exception -> 0x04ad }
            android.os.UserHandle r5 = r14.user     // Catch:{ Exception -> 0x04ad }
            r4.user = r5     // Catch:{ Exception -> 0x04ad }
            r5 = r38
            int r10 = r14.getInt(r5)     // Catch:{ Exception -> 0x04a6 }
            r4.sourceContainer = r10     // Catch:{ Exception -> 0x04a6 }
            int r10 = r4.spanX     // Catch:{ Exception -> 0x04a6 }
            if (r10 <= 0) goto L_0x047b
            int r10 = r4.spanY     // Catch:{ Exception -> 0x04a6 }
            if (r10 > 0) goto L_0x039a
            goto L_0x047b
        L_0x039a:
            com.android.launcher3.widget.LauncherAppWidgetProviderInfo r10 = r13.getLauncherAppWidgetInfo(r11)     // Catch:{ Exception -> 0x04a6 }
            if (r10 == 0) goto L_0x0416
            int r11 = r4.spanX     // Catch:{ Exception -> 0x04a6 }
            r22 = r2
            int r2 = r10.minSpanX     // Catch:{ Exception -> 0x0413 }
            if (r11 < r2) goto L_0x03b9
            int r2 = r4.spanY     // Catch:{ Exception -> 0x03af }
            int r11 = r10.minSpanY     // Catch:{ Exception -> 0x03af }
            if (r2 >= r11) goto L_0x0418
            goto L_0x03b9
        L_0x03af:
            r0 = move-exception
            r4 = r37
            r10 = r39
            r2 = r0
            r38 = r5
            goto L_0x058b
        L_0x03b9:
            java.lang.String r2 = "LoaderTask"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0413 }
            r11.<init>()     // Catch:{ Exception -> 0x0413 }
            r38 = r5
            java.lang.String r5 = "Widget "
            java.lang.StringBuilder r5 = r11.append(r5)     // Catch:{ Exception -> 0x0585 }
            android.content.ComponentName r11 = r10.getComponent()     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r11 = " minSizes not meet: span="
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            int r11 = r4.spanX     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r11 = "x"
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            int r11 = r4.spanY     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r11 = " minSpan="
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            int r11 = r10.minSpanX     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r11 = "x"
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            int r11 = r10.minSpanY     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r5 = r5.append(r11)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.logging.FileLog.d(r2, r5)     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.LauncherAppState r2 = r1.mApp     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.InvariantDeviceProfile r2 = r2.getInvariantDeviceProfile()     // Catch:{ Exception -> 0x0585 }
            logWidgetInfo(r2, r10)     // Catch:{ Exception -> 0x0585 }
            goto L_0x041a
        L_0x0413:
            r0 = move-exception
            goto L_0x04a9
        L_0x0416:
            r22 = r2
        L_0x0418:
            r38 = r5
        L_0x041a:
            boolean r2 = r14.isOnWorkspaceOrHotseat()     // Catch:{ Exception -> 0x0585 }
            if (r2 != 0) goto L_0x0427
            java.lang.String r2 = "Widget found where container != CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0585 }
            goto L_0x05a6
        L_0x0427:
            if (r7 != 0) goto L_0x0454
            android.content.ComponentName r2 = r4.providerName     // Catch:{ Exception -> 0x0585 }
            java.lang.String r2 = r2.flattenToString()     // Catch:{ Exception -> 0x0585 }
            boolean r5 = r2.equals(r12)     // Catch:{ Exception -> 0x0585 }
            if (r5 == 0) goto L_0x043b
            int r5 = r4.restoreStatus     // Catch:{ Exception -> 0x0585 }
            int r7 = r14.restoreFlag     // Catch:{ Exception -> 0x0585 }
            if (r5 == r7) goto L_0x0454
        L_0x043b:
            com.android.launcher3.util.ContentWriter r5 = r14.updater()     // Catch:{ Exception -> 0x0585 }
            java.lang.String r7 = "appWidgetProvider"
            com.android.launcher3.util.ContentWriter r2 = r5.put((java.lang.String) r7, (java.lang.String) r2)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r5 = "restored"
            int r7 = r4.restoreStatus     // Catch:{ Exception -> 0x0585 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.util.ContentWriter r2 = r2.put((java.lang.String) r5, (java.lang.Integer) r7)     // Catch:{ Exception -> 0x0585 }
            r2.commit()     // Catch:{ Exception -> 0x0585 }
        L_0x0454:
            int r2 = r4.restoreStatus     // Catch:{ Exception -> 0x0585 }
            if (r2 == 0) goto L_0x0470
            com.android.launcher3.LauncherAppState r2 = r1.mApp     // Catch:{ Exception -> 0x0585 }
            android.content.Context r2 = r2.getContext()     // Catch:{ Exception -> 0x0585 }
            android.content.ComponentName r5 = r4.providerName     // Catch:{ Exception -> 0x0585 }
            android.os.UserHandle r7 = r4.user     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.model.data.PackageItemInfo r2 = com.android.launcher3.model.WidgetsModel.newPendingItemInfo(r2, r5, r7)     // Catch:{ Exception -> 0x0585 }
            r4.pendingItemInfo = r2     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.icons.IconCache r2 = r1.mIconCache     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.model.data.PackageItemInfo r5 = r4.pendingItemInfo     // Catch:{ Exception -> 0x0585 }
            r7 = 0
            r2.getTitleAndIconForApp(r5, r7)     // Catch:{ Exception -> 0x0585 }
        L_0x0470:
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ Exception -> 0x0585 }
            r14.checkAndAddItem(r4, r2)     // Catch:{ Exception -> 0x0585 }
            r4 = r37
            r10 = r39
            goto L_0x0564
        L_0x047b:
            r22 = r2
            r38 = r5
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0585 }
            r2.<init>()     // Catch:{ Exception -> 0x0585 }
            java.lang.String r5 = "Widget has invalid size: "
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x0585 }
            int r5 = r4.spanX     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r5 = "x"
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x0585 }
            int r4 = r4.spanY     // Catch:{ Exception -> 0x0585 }
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ Exception -> 0x0585 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0585 }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0585 }
            goto L_0x05a6
        L_0x04a6:
            r0 = move-exception
            r22 = r2
        L_0x04a9:
            r38 = r5
            goto L_0x0586
        L_0x04ad:
            r0 = move-exception
            r22 = r2
            goto L_0x0586
        L_0x04b2:
            r0 = move-exception
            goto L_0x04b7
        L_0x04b4:
            r0 = move-exception
            r8 = r21
        L_0x04b7:
            r3 = r25
            goto L_0x0586
        L_0x04bb:
            r0 = move-exception
            goto L_0x04c0
        L_0x04bd:
            r0 = move-exception
            r33 = r8
        L_0x04c0:
            r8 = r21
            r3 = r25
            r4 = r37
            r2 = r0
            r34 = r10
            r25 = r13
            r20 = r18
            r12 = r32
            r7 = 1
            r11 = 2
            r13 = r36
            r10 = r39
            goto L_0x0595
        L_0x04d7:
            r0 = move-exception
            r32 = r3
            r33 = r8
            r8 = r21
            r3 = r25
            r4 = r37
            r11 = r2
            r34 = r10
            r25 = r13
            r20 = r18
            r12 = r32
            r7 = 1
            r13 = r36
            r10 = r39
            r2 = r0
            goto L_0x0595
        L_0x04f3:
            r0 = move-exception
            r32 = r3
            r33 = r8
            r8 = r21
            r3 = r25
            r4 = r37
            r7 = r5
            r34 = r10
            r25 = r13
            r20 = r18
            r12 = r32
            goto L_0x0520
        L_0x0508:
            r0 = move-exception
            r32 = r3
            r29 = r4
            r30 = r5
            r33 = r8
            r8 = r21
            r3 = r25
            r4 = r37
            r34 = r10
            r25 = r13
            r20 = r18
            r12 = r32
            r7 = 1
        L_0x0520:
            r11 = 2
            r13 = r36
            r10 = r39
            r18 = r6
            goto L_0x09c6
        L_0x0529:
            r31 = r2
            r32 = r3
            r29 = r4
            r30 = r5
            r33 = r8
            r34 = r10
            r8 = r21
            r3 = r25
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ Exception -> 0x0585 }
            int r4 = r14.id     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.model.data.FolderInfo r2 = r2.findOrMakeFolder(r4)     // Catch:{ Exception -> 0x0585 }
            r14.applyCommonProperties(r2)     // Catch:{ Exception -> 0x0585 }
            int r4 = r14.titleIndex     // Catch:{ Exception -> 0x0585 }
            java.lang.String r4 = r14.getString(r4)     // Catch:{ Exception -> 0x0585 }
            r2.title = r4     // Catch:{ Exception -> 0x0585 }
            r4 = 1
            r2.spanX = r4     // Catch:{ Exception -> 0x0574 }
            r2.spanY = r4     // Catch:{ Exception -> 0x0574 }
            int r4 = r14.getInt(r9)     // Catch:{ Exception -> 0x0585 }
            r2.options = r4     // Catch:{ Exception -> 0x0585 }
            r14.markRestored()     // Catch:{ Exception -> 0x0585 }
            com.android.launcher3.model.BgDataModel r4 = r1.mBgDataModel     // Catch:{ Exception -> 0x0585 }
            r5 = r39
            r14.checkAndAddItem(r2, r4, r5)     // Catch:{ Exception -> 0x05b8 }
            r4 = r37
            r10 = r5
        L_0x0564:
            r25 = r13
            r20 = r18
            r12 = r32
            r7 = 1
            r11 = 2
            r13 = r36
            r18 = r6
            r6 = r31
            goto L_0x0932
        L_0x0574:
            r0 = move-exception
            r10 = r39
            r2 = r0
            r7 = r4
            r25 = r13
            r20 = r18
            r12 = r32
            r11 = 2
            r13 = r36
            r4 = r37
            goto L_0x0595
        L_0x0585:
            r0 = move-exception
        L_0x0586:
            r4 = r37
            r10 = r39
            r2 = r0
        L_0x058b:
            r25 = r13
            r20 = r18
            r12 = r32
            r7 = 1
            r11 = 2
            r13 = r36
        L_0x0595:
            r18 = r6
            r6 = r31
            goto L_0x09c8
        L_0x059b:
            android.content.Intent r2 = r14.parseIntent()     // Catch:{ Exception -> 0x0997 }
            if (r2 != 0) goto L_0x05be
            java.lang.String r2 = "Invalid or null intent"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x05b8 }
        L_0x05a6:
            r25 = r3
            r21 = r8
            r4 = r29
            r5 = r30
            r2 = r31
            r3 = r32
            r8 = r33
            r10 = r34
            goto L_0x0174
        L_0x05b8:
            r0 = move-exception
            r4 = r37
            r2 = r0
            r10 = r5
            goto L_0x058b
        L_0x05be:
            com.android.launcher3.model.UserManagerState r4 = r1.mUserManagerState     // Catch:{ Exception -> 0x0997 }
            long r10 = r14.serialNumber     // Catch:{ Exception -> 0x0997 }
            boolean r4 = r4.isUserQuiet((long) r10)     // Catch:{ Exception -> 0x0997 }
            if (r4 == 0) goto L_0x05cb
            r10 = 8
            goto L_0x05cc
        L_0x05cb:
            r10 = 0
        L_0x05cc:
            android.content.ComponentName r4 = r2.getComponent()     // Catch:{ Exception -> 0x0997 }
            if (r4 != 0) goto L_0x05d7
            java.lang.String r7 = r2.getPackage()     // Catch:{ Exception -> 0x05b8 }
            goto L_0x05db
        L_0x05d7:
            java.lang.String r7 = r4.getPackageName()     // Catch:{ Exception -> 0x0997 }
        L_0x05db:
            boolean r11 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x0997 }
            if (r11 == 0) goto L_0x05ec
            int r11 = r14.itemType     // Catch:{ Exception -> 0x05b8 }
            r12 = 1
            if (r11 == r12) goto L_0x05ec
            java.lang.String r2 = "Only legacy shortcuts can have null package"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x05b8 }
            goto L_0x05a6
        L_0x05ec:
            boolean r11 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x0997 }
            if (r11 != 0) goto L_0x05ff
            android.content.pm.LauncherApps r11 = r1.mLauncherApps     // Catch:{ Exception -> 0x05b8 }
            android.os.UserHandle r12 = r14.user     // Catch:{ Exception -> 0x05b8 }
            boolean r11 = r11.isPackageEnabled(r7, r12)     // Catch:{ Exception -> 0x05b8 }
            if (r11 == 0) goto L_0x05fd
            goto L_0x05ff
        L_0x05fd:
            r11 = 0
            goto L_0x0600
        L_0x05ff:
            r11 = 1
        L_0x0600:
            if (r4 == 0) goto L_0x0682
            if (r11 == 0) goto L_0x0682
            int r12 = r14.itemType     // Catch:{ Exception -> 0x066a }
            r21 = r2
            r2 = 6
            if (r12 == r2) goto L_0x0684
            android.content.pm.LauncherApps r2 = r1.mLauncherApps     // Catch:{ Exception -> 0x066a }
            android.os.UserHandle r12 = r14.user     // Catch:{ Exception -> 0x066a }
            boolean r2 = r2.isActivityEnabled(r4, r12)     // Catch:{ Exception -> 0x066a }
            if (r2 == 0) goto L_0x061a
            r14.markRestored()     // Catch:{ Exception -> 0x05b8 }
            goto L_0x0684
        L_0x061a:
            android.os.UserHandle r2 = r14.user     // Catch:{ Exception -> 0x066a }
            r4 = r18
            android.content.Intent r2 = r4.getAppLaunchIntent(r7, r2)     // Catch:{ Exception -> 0x0664 }
            if (r2 == 0) goto L_0x0643
            r12 = 0
            r14.restoreFlag = r12     // Catch:{ Exception -> 0x0664 }
            com.android.launcher3.util.ContentWriter r12 = r14.updater()     // Catch:{ Exception -> 0x0664 }
            r18 = r6
            java.lang.String r6 = "intent"
            r25 = r13
            r13 = 0
            java.lang.String r5 = r2.toUri(r13)     // Catch:{ Exception -> 0x0740 }
            com.android.launcher3.util.ContentWriter r5 = r12.put((java.lang.String) r6, (java.lang.String) r5)     // Catch:{ Exception -> 0x0740 }
            r5.commit()     // Catch:{ Exception -> 0x0740 }
            r2.getComponent()     // Catch:{ Exception -> 0x0740 }
            goto L_0x068c
        L_0x0641:
            r0 = move-exception
            goto L_0x0667
        L_0x0643:
            r18 = r6
            r25 = r13
            java.lang.String r2 = "Unable to find a launch target"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0740 }
        L_0x064c:
            r21 = r8
            r6 = r18
            r13 = r25
            r5 = r30
            r2 = r31
            r8 = r33
            r10 = r34
        L_0x065a:
            r25 = r3
            r18 = r4
            r4 = r29
            r3 = r32
            goto L_0x0174
        L_0x0664:
            r0 = move-exception
            r18 = r6
        L_0x0667:
            r25 = r13
            goto L_0x0671
        L_0x066a:
            r0 = move-exception
            r25 = r13
            r4 = r18
            r18 = r6
        L_0x0671:
            r13 = r36
            r10 = r39
            r2 = r0
            r20 = r4
            r6 = r31
            r12 = r32
        L_0x067c:
            r7 = 1
            r11 = 2
        L_0x067e:
            r4 = r37
            goto L_0x09c8
        L_0x0682:
            r21 = r2
        L_0x0684:
            r25 = r13
            r4 = r18
            r18 = r6
            r2 = r21
        L_0x068c:
            boolean r5 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x0987 }
            if (r5 != 0) goto L_0x0743
            if (r11 != 0) goto L_0x0743
            int r5 = r14.restoreFlag     // Catch:{ Exception -> 0x0740 }
            if (r5 == 0) goto L_0x06f5
            java.lang.String r5 = "LoaderTask"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0740 }
            r6.<init>()     // Catch:{ Exception -> 0x0740 }
            java.lang.String r12 = "package not yet restored: "
            java.lang.StringBuilder r6 = r6.append(r12)     // Catch:{ Exception -> 0x0740 }
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x0740 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0740 }
            com.android.launcher3.logging.FileLog.d(r5, r6)     // Catch:{ Exception -> 0x0740 }
            android.os.UserHandle r5 = r14.user     // Catch:{ Exception -> 0x0740 }
            r8.update(r7, r5)     // Catch:{ Exception -> 0x0740 }
            r5 = 4
            boolean r6 = r14.hasRestoreFlag(r5)     // Catch:{ Exception -> 0x0740 }
            if (r6 == 0) goto L_0x06be
            goto L_0x0743
        L_0x06be:
            boolean r6 = r3.containsKey(r8)     // Catch:{ Exception -> 0x0740 }
            if (r6 == 0) goto L_0x06dd
            int r6 = r14.restoreFlag     // Catch:{ Exception -> 0x0740 }
            r6 = r6 | r5
            r14.restoreFlag = r6     // Catch:{ Exception -> 0x0740 }
            com.android.launcher3.util.ContentWriter r5 = r14.updater()     // Catch:{ Exception -> 0x0740 }
            java.lang.String r6 = "restored"
            int r12 = r14.restoreFlag     // Catch:{ Exception -> 0x0740 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x0740 }
            com.android.launcher3.util.ContentWriter r5 = r5.put((java.lang.String) r6, (java.lang.Integer) r12)     // Catch:{ Exception -> 0x0740 }
            r5.commit()     // Catch:{ Exception -> 0x0740 }
            goto L_0x0743
        L_0x06dd:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0740 }
            r2.<init>()     // Catch:{ Exception -> 0x0740 }
            java.lang.String r5 = "Unrestored app removed: "
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x0740 }
            java.lang.StringBuilder r2 = r2.append(r7)     // Catch:{ Exception -> 0x0740 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0740 }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0740 }
            goto L_0x064c
        L_0x06f5:
            android.os.UserHandle r5 = r14.user     // Catch:{ Exception -> 0x0740 }
            boolean r5 = r4.isAppOnSdcard(r7, r5)     // Catch:{ Exception -> 0x0740 }
            if (r5 == 0) goto L_0x0701
            r10 = r10 | 2
        L_0x06ff:
            r5 = 1
            goto L_0x0744
        L_0x0701:
            if (r17 != 0) goto L_0x0728
            java.lang.String r5 = "LoaderTask"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0740 }
            r6.<init>()     // Catch:{ Exception -> 0x0740 }
            java.lang.String r12 = "Missing pkg, will check later: "
            java.lang.StringBuilder r6 = r6.append(r12)     // Catch:{ Exception -> 0x0740 }
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x0740 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0740 }
            android.util.Log.d(r5, r6)     // Catch:{ Exception -> 0x0740 }
            java.util.Set<com.android.launcher3.util.PackageUserKey> r5 = r1.mPendingPackages     // Catch:{ Exception -> 0x0740 }
            com.android.launcher3.util.PackageUserKey r6 = new com.android.launcher3.util.PackageUserKey     // Catch:{ Exception -> 0x0740 }
            android.os.UserHandle r12 = r14.user     // Catch:{ Exception -> 0x0740 }
            r6.<init>((java.lang.String) r7, (android.os.UserHandle) r12)     // Catch:{ Exception -> 0x0740 }
            r5.add(r6)     // Catch:{ Exception -> 0x0740 }
            goto L_0x06ff
        L_0x0728:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0740 }
            r2.<init>()     // Catch:{ Exception -> 0x0740 }
            java.lang.String r5 = "Invalid package removed: "
            java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ Exception -> 0x0740 }
            java.lang.StringBuilder r2 = r2.append(r7)     // Catch:{ Exception -> 0x0740 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0740 }
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x0740 }
            goto L_0x064c
        L_0x0740:
            r0 = move-exception
            goto L_0x0671
        L_0x0743:
            r5 = 0
        L_0x0744:
            int r6 = r14.restoreFlag     // Catch:{ Exception -> 0x0987 }
            r12 = 8
            r6 = r6 & r12
            if (r6 == 0) goto L_0x074c
            r11 = 0
        L_0x074c:
            if (r11 == 0) goto L_0x0751
            r14.markRestored()     // Catch:{ Exception -> 0x0740 }
        L_0x0751:
            boolean r6 = r14.isOnWorkspaceOrHotseat()     // Catch:{ Exception -> 0x0987 }
            if (r6 != 0) goto L_0x0759
            r6 = 1
            goto L_0x075a
        L_0x0759:
            r6 = 0
        L_0x075a:
            int r11 = r14.restoreFlag     // Catch:{ Exception -> 0x0987 }
            if (r11 == 0) goto L_0x076a
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = r14.getRestoredItemInfo(r2)     // Catch:{ Exception -> 0x0740 }
        L_0x0762:
            r13 = r36
            r20 = r4
            r12 = r32
            goto L_0x0897
        L_0x076a:
            int r11 = r14.itemType     // Catch:{ Exception -> 0x0987 }
            if (r11 != 0) goto L_0x077e
            com.android.launcher3.config.FeatureFlags$BooleanFlag r11 = com.android.launcher3.config.FeatureFlags.ENABLE_BULK_WORKSPACE_ICON_LOADING     // Catch:{ Exception -> 0x0740 }
            boolean r11 = r11.get()     // Catch:{ Exception -> 0x0740 }
            if (r11 != 0) goto L_0x0778
            r11 = 1
            goto L_0x0779
        L_0x0778:
            r11 = 0
        L_0x0779:
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = r14.getAppShortcutInfo(r2, r5, r6, r11)     // Catch:{ Exception -> 0x0740 }
            goto L_0x0762
        L_0x077e:
            int r5 = r14.itemType     // Catch:{ Exception -> 0x0987 }
            r11 = 6
            if (r5 != r11) goto L_0x083d
            android.os.UserHandle r5 = r14.user     // Catch:{ Exception -> 0x082f }
            com.android.launcher3.shortcuts.ShortcutKey r5 = com.android.launcher3.shortcuts.ShortcutKey.fromIntent(r2, r5)     // Catch:{ Exception -> 0x082f }
            long r11 = r14.serialNumber     // Catch:{ Exception -> 0x082f }
            r13 = r34
            java.lang.Object r11 = r13.get(r11)     // Catch:{ Exception -> 0x0827 }
            java.lang.Boolean r11 = (java.lang.Boolean) r11     // Catch:{ Exception -> 0x0827 }
            boolean r11 = r11.booleanValue()     // Catch:{ Exception -> 0x0827 }
            if (r11 == 0) goto L_0x0811
            r11 = r33
            java.lang.Object r2 = r11.get(r5)     // Catch:{ Exception -> 0x080d }
            android.content.pm.ShortcutInfo r2 = (android.content.pm.ShortcutInfo) r2     // Catch:{ Exception -> 0x080d }
            if (r2 != 0) goto L_0x07ca
            java.lang.String r2 = "Pinned shortcut not found"
            r14.markDeleted(r2)     // Catch:{ Exception -> 0x07b6 }
            r21 = r8
            r8 = r11
            r10 = r13
            r6 = r18
            r13 = r25
            r5 = r30
            r2 = r31
            goto L_0x065a
        L_0x07b6:
            r0 = move-exception
            r10 = r39
            r2 = r0
            r20 = r4
            r33 = r11
            r34 = r13
            r6 = r31
            r12 = r32
            r7 = 1
            r11 = 2
            r13 = r36
            goto L_0x067e
        L_0x07ca:
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = new com.android.launcher3.model.data.WorkspaceItemInfo     // Catch:{ Exception -> 0x080d }
            r12 = r32
            r5.<init>(r2, r12)     // Catch:{ Exception -> 0x0807 }
            r34 = r13
            com.android.launcher3.icons.IconCache r13 = r1.mIconCache     // Catch:{ Exception -> 0x0801 }
            java.util.Objects.requireNonNull(r14)     // Catch:{ Exception -> 0x0801 }
            r33 = r11
            com.android.launcher3.model.-$$Lambda$Akbdy6VsenKPNEuvVYIHwGY0-jA r11 = new com.android.launcher3.model.-$$Lambda$Akbdy6VsenKPNEuvVYIHwGY0-jA     // Catch:{ Exception -> 0x07ff }
            r11.<init>()     // Catch:{ Exception -> 0x07ff }
            r13.getShortcutIcon(r5, r2, r11)     // Catch:{ Exception -> 0x07ff }
            java.lang.String r11 = r2.getPackage()     // Catch:{ Exception -> 0x07ff }
            android.os.UserHandle r13 = r5.user     // Catch:{ Exception -> 0x07ff }
            boolean r11 = r4.isAppSuspended(r11, r13)     // Catch:{ Exception -> 0x07ff }
            if (r11 == 0) goto L_0x07f4
            int r11 = r5.runtimeStatusFlags     // Catch:{ Exception -> 0x07ff }
            r13 = 4
            r11 = r11 | r13
            r5.runtimeStatusFlags = r11     // Catch:{ Exception -> 0x07ff }
        L_0x07f4:
            android.content.Intent r11 = r5.getIntent()     // Catch:{ Exception -> 0x07ff }
            r13 = r36
            r13.add(r2)     // Catch:{ Exception -> 0x0856 }
            r2 = r11
            goto L_0x0823
        L_0x07ff:
            r0 = move-exception
            goto L_0x082c
        L_0x0801:
            r0 = move-exception
            r13 = r36
            r33 = r11
            goto L_0x0834
        L_0x0807:
            r0 = move-exception
            r33 = r11
            r34 = r13
            goto L_0x082c
        L_0x080d:
            r0 = move-exception
            r33 = r11
            goto L_0x0828
        L_0x0811:
            r34 = r13
            r12 = r32
            r13 = r36
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = r14.loadSimpleWorkspaceItem()     // Catch:{ Exception -> 0x0856 }
            int r11 = r5.runtimeStatusFlags     // Catch:{ Exception -> 0x0856 }
            r20 = 32
            r11 = r11 | 32
            r5.runtimeStatusFlags = r11     // Catch:{ Exception -> 0x0856 }
        L_0x0823:
            r20 = r4
            goto L_0x0897
        L_0x0827:
            r0 = move-exception
        L_0x0828:
            r34 = r13
            r12 = r32
        L_0x082c:
            r13 = r36
            goto L_0x0834
        L_0x082f:
            r0 = move-exception
            r13 = r36
            r12 = r32
        L_0x0834:
            r10 = r39
            r2 = r0
            r20 = r4
            r6 = r31
            goto L_0x067c
        L_0x083d:
            r13 = r36
            r12 = r32
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = r14.loadSimpleWorkspaceItem()     // Catch:{ Exception -> 0x097f }
            boolean r11 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x097f }
            if (r11 != 0) goto L_0x0858
            android.os.UserHandle r11 = r14.user     // Catch:{ Exception -> 0x0856 }
            boolean r11 = r4.isAppSuspended(r7, r11)     // Catch:{ Exception -> 0x0856 }
            if (r11 == 0) goto L_0x0858
            r10 = r10 | 4
            goto L_0x0858
        L_0x0856:
            r0 = move-exception
            goto L_0x0834
        L_0x0858:
            int r11 = r14.getInt(r9)     // Catch:{ Exception -> 0x097f }
            r5.options = r11     // Catch:{ Exception -> 0x097f }
            java.lang.String r11 = r2.getAction()     // Catch:{ Exception -> 0x097f }
            if (r11 == 0) goto L_0x0823
            java.util.Set r11 = r2.getCategories()     // Catch:{ Exception -> 0x088c }
            if (r11 == 0) goto L_0x0823
            java.lang.String r11 = r2.getAction()     // Catch:{ Exception -> 0x088c }
            r20 = r4
            java.lang.String r4 = "android.intent.action.MAIN"
            boolean r4 = r11.equals(r4)     // Catch:{ Exception -> 0x088a }
            if (r4 == 0) goto L_0x0897
            java.util.Set r4 = r2.getCategories()     // Catch:{ Exception -> 0x088a }
            java.lang.String r11 = "android.intent.category.LAUNCHER"
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x088a }
            if (r4 == 0) goto L_0x0897
            r4 = 270532608(0x10200000, float:3.1554436E-29)
            r2.addFlags(r4)     // Catch:{ Exception -> 0x088a }
            goto L_0x0897
        L_0x088a:
            r0 = move-exception
            goto L_0x088f
        L_0x088c:
            r0 = move-exception
            r20 = r4
        L_0x088f:
            r4 = r37
            r10 = r39
            r2 = r0
            r6 = r31
            goto L_0x08b2
        L_0x0897:
            if (r5 == 0) goto L_0x096d
            int r4 = r5.itemType     // Catch:{ Exception -> 0x0962 }
            r11 = 6
            if (r4 == r11) goto L_0x08b6
            com.android.launcher3.model.data.IconRequestInfo r4 = r14.createIconRequestInfo(r5, r6)     // Catch:{ Exception -> 0x08aa }
            r6 = r31
            r6.add(r4)     // Catch:{ Exception -> 0x08a8 }
            goto L_0x08b8
        L_0x08a8:
            r0 = move-exception
            goto L_0x08ad
        L_0x08aa:
            r0 = move-exception
            r6 = r31
        L_0x08ad:
            r4 = r37
        L_0x08af:
            r10 = r39
            r2 = r0
        L_0x08b2:
            r7 = 1
            r11 = 2
            goto L_0x09c8
        L_0x08b6:
            r6 = r31
        L_0x08b8:
            r14.applyCommonProperties(r5)     // Catch:{ Exception -> 0x095c }
            r5.intent = r2     // Catch:{ Exception -> 0x095c }
            r4 = r37
            int r11 = r14.getInt(r4)     // Catch:{ Exception -> 0x095a }
            r5.rank = r11     // Catch:{ Exception -> 0x095a }
            r11 = 1
            r5.spanX = r11     // Catch:{ Exception -> 0x0955 }
            r5.spanY = r11     // Catch:{ Exception -> 0x0955 }
            int r11 = r5.runtimeStatusFlags     // Catch:{ Exception -> 0x095a }
            r10 = r10 | r11
            r5.runtimeStatusFlags = r10     // Catch:{ Exception -> 0x095a }
            if (r24 == 0) goto L_0x08e0
            boolean r2 = com.android.launcher3.util.PackageManagerHelper.isSystemApp(r12, r2)     // Catch:{ Exception -> 0x08de }
            if (r2 != 0) goto L_0x08e0
            int r2 = r5.runtimeStatusFlags     // Catch:{ Exception -> 0x08de }
            r10 = 1
            r2 = r2 | r10
            r5.runtimeStatusFlags = r2     // Catch:{ Exception -> 0x08de }
            goto L_0x08e0
        L_0x08de:
            r0 = move-exception
            goto L_0x08af
        L_0x08e0:
            android.content.pm.LauncherActivityInfo r2 = r14.getLauncherActivityInfo()     // Catch:{ Exception -> 0x095a }
            if (r2 == 0) goto L_0x08f9
            int r10 = com.android.launcher3.util.PackageManagerHelper.getLoadingProgress(r2)     // Catch:{ Exception -> 0x08f1 }
            r11 = 2
            r5.setProgressLevel(r10, r11)     // Catch:{ Exception -> 0x08ef }
            goto L_0x08fa
        L_0x08ef:
            r0 = move-exception
            goto L_0x08f3
        L_0x08f1:
            r0 = move-exception
            r11 = 2
        L_0x08f3:
            r10 = r39
            r2 = r0
            r7 = 1
            goto L_0x09c8
        L_0x08f9:
            r11 = 2
        L_0x08fa:
            int r10 = r14.restoreFlag     // Catch:{ Exception -> 0x094f }
            if (r10 == 0) goto L_0x092a
            boolean r10 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x0927 }
            if (r10 != 0) goto L_0x092a
            android.os.UserHandle r10 = r14.user     // Catch:{ Exception -> 0x0927 }
            r8.update(r7, r10)     // Catch:{ Exception -> 0x0927 }
            java.lang.Object r7 = r3.get(r8)     // Catch:{ Exception -> 0x0927 }
            android.content.pm.PackageInstaller$SessionInfo r7 = (android.content.pm.PackageInstaller.SessionInfo) r7     // Catch:{ Exception -> 0x0927 }
            if (r7 != 0) goto L_0x0918
            int r2 = r5.runtimeStatusFlags     // Catch:{ Exception -> 0x08ef }
            r2 = r2 & -1025(0xfffffffffffffbff, float:NaN)
            r5.runtimeStatusFlags = r2     // Catch:{ Exception -> 0x08ef }
            goto L_0x092a
        L_0x0918:
            if (r2 != 0) goto L_0x092a
            float r2 = r7.getProgress()     // Catch:{ Exception -> 0x0927 }
            r7 = 1120403456(0x42c80000, float:100.0)
            float r2 = r2 * r7
            int r2 = (int) r2
            r7 = 1
            r5.setProgressLevel(r2, r7)     // Catch:{ Exception -> 0x094a }
            goto L_0x092b
        L_0x0927:
            r0 = move-exception
            r7 = 1
            goto L_0x094b
        L_0x092a:
            r7 = 1
        L_0x092b:
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ Exception -> 0x094a }
            r10 = r39
            r14.checkAndAddItem(r5, r2, r10)     // Catch:{ Exception -> 0x097d }
        L_0x0932:
            r37 = r4
            r2 = r6
            r21 = r8
            r6 = r18
            r18 = r20
            r13 = r25
            r4 = r29
            r5 = r30
            r8 = r33
            r10 = r34
            r25 = r3
            r3 = r12
            goto L_0x0174
        L_0x094a:
            r0 = move-exception
        L_0x094b:
            r10 = r39
            goto L_0x09c7
        L_0x094f:
            r0 = move-exception
            r10 = r39
            r7 = 1
            goto L_0x09c7
        L_0x0955:
            r0 = move-exception
            r10 = r39
            r7 = r11
            goto L_0x096a
        L_0x095a:
            r0 = move-exception
            goto L_0x095f
        L_0x095c:
            r0 = move-exception
            r4 = r37
        L_0x095f:
            r10 = r39
            goto L_0x0969
        L_0x0962:
            r0 = move-exception
            r4 = r37
            r10 = r39
            r6 = r31
        L_0x0969:
            r7 = 1
        L_0x096a:
            r11 = 2
            goto L_0x09c7
        L_0x096d:
            r4 = r37
            r10 = r39
            r6 = r31
            r7 = 1
            r11 = 2
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x097d }
            java.lang.String r5 = "Unexpected null WorkspaceItemInfo"
            r2.<init>(r5)     // Catch:{ Exception -> 0x097d }
            throw r2     // Catch:{ Exception -> 0x097d }
        L_0x097d:
            r0 = move-exception
            goto L_0x09c7
        L_0x097f:
            r0 = move-exception
            r10 = r39
            r20 = r4
            r6 = r31
            goto L_0x0992
        L_0x0987:
            r0 = move-exception
            r13 = r36
            r10 = r39
            r20 = r4
            r6 = r31
            r12 = r32
        L_0x0992:
            r7 = 1
            r11 = 2
            r4 = r37
            goto L_0x09c7
        L_0x0997:
            r0 = move-exception
            r4 = r37
            r10 = r5
            r25 = r13
            r20 = r18
            r12 = r32
            r7 = 1
            r11 = 2
            r13 = r36
            r18 = r6
            r6 = r31
            goto L_0x09c7
        L_0x09aa:
            r0 = move-exception
            r12 = r3
            r29 = r4
            r30 = r5
            r33 = r8
            r34 = r10
            r20 = r18
            r8 = r21
            r3 = r25
            r7 = 1
            r11 = 2
            r4 = r37
            r10 = r39
            r18 = r6
            r25 = r13
            r13 = r36
        L_0x09c6:
            r6 = r2
        L_0x09c7:
            r2 = r0
        L_0x09c8:
            java.lang.String r5 = "LoaderTask"
            java.lang.String r7 = "Desktop items loading interrupted"
            android.util.Log.e(r5, r7, r2)     // Catch:{ all -> 0x0a9e }
            goto L_0x0932
        L_0x09d1:
            r6 = r2
            r33 = r8
            com.android.launcher3.config.FeatureFlags$BooleanFlag r2 = com.android.launcher3.config.FeatureFlags.ENABLE_BULK_WORKSPACE_ICON_LOADING     // Catch:{ all -> 0x0a9e }
            boolean r2 = r2.get()     // Catch:{ all -> 0x0a9e }
            if (r2 == 0) goto L_0x0a1a
            java.lang.String r2 = "LoadWorkspaceIconsInBulk"
            android.os.Trace.beginSection(r2)     // Catch:{ all -> 0x0a9e }
            com.android.launcher3.icons.IconCache r2 = r1.mIconCache     // Catch:{ all -> 0x0a14 }
            r2.getTitlesAndIconsInBulk(r6)     // Catch:{ all -> 0x0a14 }
            java.util.Iterator r2 = r6.iterator()     // Catch:{ all -> 0x0a14 }
        L_0x09ea:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x0a14 }
            if (r3 == 0) goto L_0x0a10
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x0a14 }
            com.android.launcher3.model.data.IconRequestInfo r3 = (com.android.launcher3.model.data.IconRequestInfo) r3     // Catch:{ all -> 0x0a14 }
            T r4 = r3.itemInfo     // Catch:{ all -> 0x0a14 }
            com.android.launcher3.model.data.WorkspaceItemInfo r4 = (com.android.launcher3.model.data.WorkspaceItemInfo) r4     // Catch:{ all -> 0x0a14 }
            com.android.launcher3.icons.IconCache r5 = r1.mIconCache     // Catch:{ all -> 0x0a14 }
            com.android.launcher3.icons.BitmapInfo r6 = r4.bitmap     // Catch:{ all -> 0x0a14 }
            android.os.UserHandle r4 = r4.user     // Catch:{ all -> 0x0a14 }
            boolean r4 = r5.isDefaultIcon(r6, r4)     // Catch:{ all -> 0x0a14 }
            if (r4 == 0) goto L_0x09ea
            com.android.launcher3.LauncherAppState r4 = r1.mApp     // Catch:{ all -> 0x0a14 }
            android.content.Context r4 = r4.getContext()     // Catch:{ all -> 0x0a14 }
            r3.loadWorkspaceIcon(r4)     // Catch:{ all -> 0x0a14 }
            goto L_0x09ea
        L_0x0a10:
            android.os.Trace.endSection()     // Catch:{ all -> 0x0a9e }
            goto L_0x0a1a
        L_0x0a14:
            r0 = move-exception
            r2 = r0
            android.os.Trace.endSection()     // Catch:{ all -> 0x0a9e }
            throw r2     // Catch:{ all -> 0x0a9e }
        L_0x0a1a:
            com.android.launcher3.util.IOUtils.closeSilently(r14)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.ModelDelegate r2 = r1.mModelDelegate     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.UserManagerState r3 = r1.mUserManagerState     // Catch:{ all -> 0x0aa4 }
            r4 = r33
            r2.loadItems(r3, r4)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.ModelDelegate r2 = r1.mModelDelegate     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.StringCache r3 = r3.stringCache     // Catch:{ all -> 0x0aa4 }
            r2.loadStringCache(r3)     // Catch:{ all -> 0x0aa4 }
            boolean r2 = r1.mStopped     // Catch:{ all -> 0x0aa4 }
            if (r2 == 0) goto L_0x0a3a
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ all -> 0x0aa4 }
            r2.clear()     // Catch:{ all -> 0x0aa4 }
            monitor-exit(r15)     // Catch:{ all -> 0x0aa4 }
            return
        L_0x0a3a:
            boolean r2 = r14.commitDeleted()     // Catch:{ all -> 0x0aa4 }
            r1.mItemsDeleted = r2     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.folder.FolderGridOrganizer r2 = new com.android.launcher3.folder.FolderGridOrganizer     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.LauncherAppState r3 = r1.mApp     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.InvariantDeviceProfile r3 = r3.getInvariantDeviceProfile()     // Catch:{ all -> 0x0aa4 }
            r2.<init>(r3)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.util.IntSparseArrayMap<com.android.launcher3.model.data.FolderInfo> r3 = r3.folders     // Catch:{ all -> 0x0aa4 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0aa4 }
        L_0x0a53:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0aa4 }
            if (r4 == 0) goto L_0x0a99
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.data.FolderInfo r4 = (com.android.launcher3.model.data.FolderInfo) r4     // Catch:{ all -> 0x0aa4 }
            java.util.ArrayList<com.android.launcher3.model.data.WorkspaceItemInfo> r5 = r4.contents     // Catch:{ all -> 0x0aa4 }
            java.util.Comparator<com.android.launcher3.model.data.ItemInfo> r6 = com.android.launcher3.folder.Folder.ITEM_POS_COMPARATOR     // Catch:{ all -> 0x0aa4 }
            java.util.Collections.sort(r5, r6)     // Catch:{ all -> 0x0aa4 }
            r2.setFolderInfo(r4)     // Catch:{ all -> 0x0aa4 }
            java.util.ArrayList<com.android.launcher3.model.data.WorkspaceItemInfo> r5 = r4.contents     // Catch:{ all -> 0x0aa4 }
            int r5 = r5.size()     // Catch:{ all -> 0x0aa4 }
            r7 = 0
        L_0x0a70:
            if (r7 >= r5) goto L_0x0a53
            java.util.ArrayList<com.android.launcher3.model.data.WorkspaceItemInfo> r6 = r4.contents     // Catch:{ all -> 0x0aa4 }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x0aa4 }
            com.android.launcher3.model.data.WorkspaceItemInfo r6 = (com.android.launcher3.model.data.WorkspaceItemInfo) r6     // Catch:{ all -> 0x0aa4 }
            r6.rank = r7     // Catch:{ all -> 0x0aa4 }
            boolean r8 = r6.usingLowResIcon()     // Catch:{ all -> 0x0aa4 }
            if (r8 == 0) goto L_0x0a95
            int r8 = r6.itemType     // Catch:{ all -> 0x0aa4 }
            if (r8 != 0) goto L_0x0a95
            int r8 = r6.rank     // Catch:{ all -> 0x0aa4 }
            boolean r8 = r2.isItemInPreview(r8)     // Catch:{ all -> 0x0aa4 }
            if (r8 == 0) goto L_0x0a95
            com.android.launcher3.icons.IconCache r8 = r1.mIconCache     // Catch:{ all -> 0x0aa4 }
            r9 = 0
            r8.getTitleAndIcon(r6, r9)     // Catch:{ all -> 0x0aa4 }
            goto L_0x0a96
        L_0x0a95:
            r9 = 0
        L_0x0a96:
            int r7 = r7 + 1
            goto L_0x0a70
        L_0x0a99:
            r14.commitRestoredItems()     // Catch:{ all -> 0x0aa4 }
            monitor-exit(r15)     // Catch:{ all -> 0x0aa4 }
            return
        L_0x0a9e:
            r0 = move-exception
            r2 = r0
            com.android.launcher3.util.IOUtils.closeSilently(r14)     // Catch:{ all -> 0x0aa4 }
            throw r2     // Catch:{ all -> 0x0aa4 }
        L_0x0aa4:
            r0 = move-exception
            r2 = r0
            monitor-exit(r15)     // Catch:{ all -> 0x0aa4 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.loadWorkspace(java.util.List, android.net.Uri, java.lang.String, com.android.launcher3.model.LoaderMemoryLogger):void");
    }

    private void setIgnorePackages(IconCacheUpdateHandler iconCacheUpdateHandler) {
        synchronized (this.mBgDataModel) {
            Iterator<ItemInfo> it = this.mBgDataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo next = it.next();
                if (next instanceof WorkspaceItemInfo) {
                    WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) next;
                    if (workspaceItemInfo.isPromise() && workspaceItemInfo.getTargetComponent() != null) {
                        iconCacheUpdateHandler.addPackagesToIgnore(workspaceItemInfo.user, workspaceItemInfo.getTargetComponent().getPackageName());
                    }
                } else if (next instanceof LauncherAppWidgetInfo) {
                    LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) next;
                    if (launcherAppWidgetInfo.hasRestoreFlag(2)) {
                        iconCacheUpdateHandler.addPackagesToIgnore(launcherAppWidgetInfo.user, launcherAppWidgetInfo.providerName.getPackageName());
                    }
                }
            }
        }
    }

    private void sanitizeData() {
        Context context = this.mApp.getContext();
        ContentResolver contentResolver = context.getContentResolver();
        if (this.mItemsDeleted) {
            int[] intArray = LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_DELETE_EMPTY_FOLDERS).getIntArray("value");
            synchronized (this.mBgDataModel) {
                for (int i : intArray) {
                    this.mBgDataModel.workspaceItems.remove(this.mBgDataModel.folders.get(i));
                    this.mBgDataModel.folders.remove(i);
                    this.mBgDataModel.itemsIdMap.remove(i);
                }
            }
        }
        LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_REMOVE_GHOST_WIDGETS);
        this.mBgDataModel.updateShortcutPinnedState(context);
        if (!Utilities.isBootCompleted() && !this.mPendingPackages.isEmpty()) {
            context.registerReceiver(new SdCardAvailableReceiver(this.mApp, this.mPendingPackages), new IntentFilter("android.intent.action.BOOT_COMPLETED"), (String) null, Executors.MODEL_EXECUTOR.getHandler());
        }
    }

    private List<LauncherActivityInfo> loadAllApps() {
        List<UserHandle> userProfiles = this.mUserCache.getUserProfiles();
        ArrayList arrayList = new ArrayList();
        this.mBgAllAppsList.clear();
        ArrayList arrayList2 = new ArrayList();
        Iterator<UserHandle> it = userProfiles.iterator();
        while (true) {
            boolean z = false;
            if (it.hasNext()) {
                UserHandle next = it.next();
                List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList((String) null, next);
                if (activityList == null || activityList.isEmpty()) {
                    return arrayList;
                }
                boolean isUserQuiet = this.mUserManagerState.isUserQuiet(next);
                for (int i = 0; i < activityList.size(); i++) {
                    LauncherActivityInfo launcherActivityInfo = activityList.get(i);
                    AppInfo appInfo = new AppInfo(launcherActivityInfo, next, isUserQuiet);
                    arrayList2.add(new IconRequestInfo(appInfo, launcherActivityInfo, false));
                    this.mBgAllAppsList.add(appInfo, launcherActivityInfo, !FeatureFlags.ENABLE_BULK_ALL_APPS_ICON_LOADING.get());
                }
                arrayList.addAll(activityList);
            } else {
                if (FeatureFlags.PROMISE_APPS_IN_ALL_APPS.get()) {
                    for (PackageInstaller.SessionInfo fromInstallingState : this.mSessionHelper.getAllVerifiedSessions()) {
                        AppInfo addPromiseApp = this.mBgAllAppsList.addPromiseApp(this.mApp.getContext(), PackageInstallInfo.fromInstallingState(fromInstallingState), !FeatureFlags.ENABLE_BULK_ALL_APPS_ICON_LOADING.get());
                        if (addPromiseApp != null) {
                            arrayList2.add(new IconRequestInfo(addPromiseApp, (LauncherActivityInfo) null, addPromiseApp.usingLowResIcon()));
                        }
                    }
                }
                if (FeatureFlags.ENABLE_BULK_ALL_APPS_ICON_LOADING.get()) {
                    Trace.beginSection("LoadAllAppsIconsInBulk");
                    try {
                        this.mIconCache.getTitlesAndIconsInBulk(arrayList2);
                        arrayList2.forEach(new Consumer() {
                            public final void accept(Object obj) {
                                LoaderTask.this.lambda$loadAllApps$1$LoaderTask((IconRequestInfo) obj);
                            }
                        });
                    } finally {
                        Trace.endSection();
                    }
                }
                this.mBgAllAppsList.setFlags(2, this.mUserManagerState.isAnyProfileQuietModeEnabled());
                this.mBgAllAppsList.setFlags(1, PackageManagerHelper.hasShortcutsPermission(this.mApp.getContext()));
                AllAppsList allAppsList = this.mBgAllAppsList;
                if (this.mApp.getContext().checkSelfPermission("android.permission.MODIFY_QUIET_MODE") == 0) {
                    z = true;
                }
                allAppsList.setFlags(4, z);
                this.mBgAllAppsList.getAndResetChangeFlag();
                return arrayList;
            }
        }
        return arrayList;
    }

    public /* synthetic */ void lambda$loadAllApps$1$LoaderTask(IconRequestInfo iconRequestInfo) {
        this.mBgAllAppsList.updateSectionName((AppInfo) iconRequestInfo.itemInfo);
    }

    private List<ShortcutInfo> loadDeepShortcuts() {
        ArrayList arrayList = new ArrayList();
        this.mBgDataModel.deepShortcutMap.clear();
        if (this.mBgAllAppsList.hasShortcutHostPermission()) {
            for (UserHandle next : this.mUserCache.getUserProfiles()) {
                if (this.mUserManager.isUserUnlocked(next)) {
                    ShortcutRequest.QueryResult query = new ShortcutRequest(this.mApp.getContext(), next).query(11);
                    arrayList.addAll(query);
                    this.mBgDataModel.updateDeepShortcutCounts((String) null, next, query);
                }
            }
        }
        return arrayList;
    }

    private void loadFolderNames() {
        FolderNameProvider newInstance = FolderNameProvider.newInstance(this.mApp.getContext(), this.mBgAllAppsList.data, this.mBgDataModel.folders);
        synchronized (this.mBgDataModel) {
            for (int i = 0; i < this.mBgDataModel.folders.size(); i++) {
                FolderNameInfos folderNameInfos = new FolderNameInfos();
                FolderInfo folderInfo = (FolderInfo) this.mBgDataModel.folders.valueAt(i);
                if (folderInfo.suggestedFolderNames == null) {
                    newInstance.getSuggestedFolderName(this.mApp.getContext(), folderInfo.contents, folderNameInfos);
                    folderInfo.suggestedFolderNames = folderNameInfos;
                }
            }
        }
    }

    public static boolean isValidProvider(AppWidgetProviderInfo appWidgetProviderInfo) {
        return (appWidgetProviderInfo == null || appWidgetProviderInfo.provider == null || appWidgetProviderInfo.provider.getPackageName() == null) ? false : true;
    }

    private static void logWidgetInfo(InvariantDeviceProfile invariantDeviceProfile, LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo) {
        Point point = new Point();
        for (DeviceProfile next : invariantDeviceProfile.supportedProfiles) {
            next.getCellSize(point);
            FileLog.d(TAG, "DeviceProfile available width: " + next.availableWidthPx + ", available height: " + next.availableHeightPx + ", cellLayoutBorderSpacePx Horizontal: " + next.cellLayoutBorderSpacePx.x + ", cellLayoutBorderSpacePx Vertical: " + next.cellLayoutBorderSpacePx.y + ", cellSize: " + point);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Widget dimensions:\n").append("minResizeWidth: ").append(launcherAppWidgetProviderInfo.minResizeWidth).append("\n").append("minResizeHeight: ").append(launcherAppWidgetProviderInfo.minResizeHeight).append("\n").append("defaultWidth: ").append(launcherAppWidgetProviderInfo.minWidth).append("\n").append("defaultHeight: ").append(launcherAppWidgetProviderInfo.minHeight).append("\n");
        if (Utilities.ATLEAST_S) {
            sb.append("targetCellWidth: ").append(launcherAppWidgetProviderInfo.targetCellWidth).append("\n").append("targetCellHeight: ").append(launcherAppWidgetProviderInfo.targetCellHeight).append("\n").append("maxResizeWidth: ").append(launcherAppWidgetProviderInfo.maxResizeWidth).append("\n").append("maxResizeHeight: ").append(launcherAppWidgetProviderInfo.maxResizeHeight).append("\n");
        }
        FileLog.d(TAG, sb.toString());
    }

    private static void logASplit(TimingLogger timingLogger, String str) {
        timingLogger.addSplit(str);
        Log.d(TAG, str);
    }
}
