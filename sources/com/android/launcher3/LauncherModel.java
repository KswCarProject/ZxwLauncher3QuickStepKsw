package com.android.launcher3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInstaller;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.AddWorkspaceItemsTask;
import com.android.launcher3.model.AllAppsList;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.CacheDataUpdatedTask;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.model.ModelDelegate;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.PackageIncrementalDownloadUpdatedTask;
import com.android.launcher3.model.PackageInstallStateChangedTask;
import com.android.launcher3.model.PackageUpdatedTask;
import com.android.launcher3.model.ReloadStringCacheTask;
import com.android.launcher3.model.ShortcutsChangedTask;
import com.android.launcher3.model.UserLockStateChangedTask;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.pm.InstallSessionTracker;
import com.android.launcher3.pm.PackageInstallInfo;
import com.android.launcher3.shortcuts.ShortcutRequest;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntSet;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LooperExecutor;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LauncherModel extends LauncherApps.Callback implements InstallSessionTracker.Callback {
    private static final boolean DEBUG_RECEIVER = false;
    static final String TAG = "Launcher.Model";
    private final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    private final BgDataModel mBgDataModel;
    private final ArrayList<BgDataModel.Callbacks> mCallbacksList = new ArrayList<>(1);
    private final Runnable mDataValidationCheck;
    /* access modifiers changed from: private */
    public boolean mIsLoaderTaskRunning;
    /* access modifiers changed from: private */
    public LoaderTask mLoaderTask;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final ModelDelegate mModelDelegate;
    private boolean mModelDestroyed = false;
    /* access modifiers changed from: private */
    public boolean mModelLoaded;

    public interface CallbackTask {
        void execute(BgDataModel.Callbacks callbacks);
    }

    public interface ModelUpdateTask extends Runnable {
        void init(LauncherAppState launcherAppState, LauncherModel launcherModel, BgDataModel bgDataModel, AllAppsList allAppsList, Executor executor);
    }

    public boolean isModelLoaded() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mModelLoaded && this.mLoaderTask == null && !this.mModelDestroyed;
        }
        return z;
    }

    LauncherModel(Context context, LauncherAppState launcherAppState, IconCache iconCache, AppFilter appFilter, boolean z) {
        BgDataModel bgDataModel = new BgDataModel();
        this.mBgDataModel = bgDataModel;
        this.mDataValidationCheck = new Runnable() {
            public void run() {
                if (LauncherModel.this.mModelLoaded) {
                    LauncherModel.this.mModelDelegate.validateData();
                }
            }
        };
        this.mApp = launcherAppState;
        AllAppsList allAppsList = new AllAppsList(iconCache, appFilter);
        this.mBgAllAppsList = allAppsList;
        this.mModelDelegate = ModelDelegate.newInstance(context, launcherAppState, allAppsList, bgDataModel, z);
    }

    public ModelDelegate getModelDelegate() {
        return this.mModelDelegate;
    }

    public void addAndBindAddedWorkspaceItems(List<Pair<ItemInfo, Object>> list) {
        for (BgDataModel.Callbacks preAddApps : getCallbacks()) {
            preAddApps.preAddApps();
        }
        enqueueModelUpdateTask(new AddWorkspaceItemsTask(list));
    }

    public ModelWriter getWriter(boolean z, boolean z2, BgDataModel.Callbacks callbacks) {
        return new ModelWriter(this.mApp.getContext(), this, this.mBgDataModel, z, z2, callbacks);
    }

    public void onPackageChanged(String str, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(2, userHandle, str));
    }

    public void onPackageRemoved(String str, UserHandle userHandle) {
        onPackagesRemoved(userHandle, str);
    }

    public void onPackagesRemoved(UserHandle userHandle, String... strArr) {
        FileLog.d(TAG, "package removed received " + TextUtils.join(",", strArr));
        enqueueModelUpdateTask(new PackageUpdatedTask(3, userHandle, strArr));
    }

    public void onPackageAdded(String str, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(1, userHandle, str));
    }

    public void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z) {
        enqueueModelUpdateTask(new PackageUpdatedTask(2, userHandle, strArr));
    }

    public void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z) {
        if (!z) {
            enqueueModelUpdateTask(new PackageUpdatedTask(4, userHandle, strArr));
        }
    }

    public void onPackagesSuspended(String[] strArr, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(5, userHandle, strArr));
    }

    public void onPackagesUnsuspended(String[] strArr, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(6, userHandle, strArr));
    }

    public void onPackageLoadingProgressChanged(String str, UserHandle userHandle, float f) {
        if (Utilities.ATLEAST_S) {
            enqueueModelUpdateTask(new PackageIncrementalDownloadUpdatedTask(str, userHandle, f));
        }
    }

    public void onShortcutsChanged(String str, List<ShortcutInfo> list, UserHandle userHandle) {
        enqueueModelUpdateTask(new ShortcutsChangedTask(str, list, userHandle, true));
    }

    public void onAppIconChanged(String str, UserHandle userHandle) {
        Context context = this.mApp.getContext();
        onPackageChanged(str, userHandle);
        ShortcutRequest.QueryResult query = new ShortcutRequest(context, userHandle).forPackage(str).query(2);
        if (!query.isEmpty()) {
            enqueueModelUpdateTask(new ShortcutsChangedTask(str, query, userHandle, false));
        }
    }

    public void onWorkspaceUiChanged() {
        LooperExecutor looperExecutor = Executors.MODEL_EXECUTOR;
        ModelDelegate modelDelegate = this.mModelDelegate;
        Objects.requireNonNull(modelDelegate);
        looperExecutor.execute(new Runnable() {
            public final void run() {
                ModelDelegate.this.workspaceLoadComplete();
            }
        });
    }

    public void destroy() {
        this.mModelDestroyed = true;
        LooperExecutor looperExecutor = Executors.MODEL_EXECUTOR;
        ModelDelegate modelDelegate = this.mModelDelegate;
        Objects.requireNonNull(modelDelegate);
        looperExecutor.execute(new Runnable() {
            public final void run() {
                ModelDelegate.this.destroy();
            }
        });
    }

    public void onBroadcastIntent(Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            forceReload();
        } else if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)) {
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (userHandle != null) {
                if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action)) {
                    enqueueModelUpdateTask(new PackageUpdatedTask(7, userHandle, new String[0]));
                }
                if ("android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)) {
                    enqueueModelUpdateTask(new UserLockStateChangedTask(userHandle, "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)));
                }
            }
        } else if ("android.app.action.DEVICE_POLICY_RESOURCE_UPDATED".equals(action)) {
            enqueueModelUpdateTask(new ReloadStringCacheTask(this.mModelDelegate));
        }
    }

    public void forceReload() {
        synchronized (this.mLock) {
            stopLoader();
            this.mModelLoaded = false;
        }
        if (hasCallbacks()) {
            startLoader();
        }
    }

    public void rebindCallbacks() {
        if (hasCallbacks()) {
            startLoader();
        }
    }

    public void removeCallbacks(BgDataModel.Callbacks callbacks) {
        synchronized (this.mCallbacksList) {
            Preconditions.assertUIThread();
            if (this.mCallbacksList.remove(callbacks) && stopLoader()) {
                startLoader();
            }
        }
    }

    public boolean addCallbacksAndLoad(BgDataModel.Callbacks callbacks) {
        boolean startLoader;
        synchronized (this.mLock) {
            addCallbacks(callbacks);
            startLoader = startLoader(new BgDataModel.Callbacks[]{callbacks});
        }
        return startLoader;
    }

    public void addCallbacks(BgDataModel.Callbacks callbacks) {
        Preconditions.assertUIThread();
        synchronized (this.mCallbacksList) {
            if (TestProtocol.sDebugTracing) {
                Log.d(TestProtocol.NULL_INT_SET, "addCallbacks pointer: " + callbacks + ", name: " + callbacks.getClass().getName(), new Exception());
            }
            this.mCallbacksList.add(callbacks);
        }
    }

    public boolean startLoader() {
        return startLoader(new BgDataModel.Callbacks[0]);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0086, code lost:
        return false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x003a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean startLoader(com.android.launcher3.model.BgDataModel.Callbacks[] r12) {
        /*
            r11 = this;
            com.android.launcher3.util.MainThreadInitializedObject<com.android.launcher3.model.ItemInstallQueue> r0 = com.android.launcher3.model.ItemInstallQueue.INSTANCE
            com.android.launcher3.LauncherAppState r1 = r11.mApp
            android.content.Context r1 = r1.getContext()
            java.lang.Object r0 = r0.lambda$get$1$MainThreadInitializedObject(r1)
            com.android.launcher3.model.ItemInstallQueue r0 = (com.android.launcher3.model.ItemInstallQueue) r0
            r1 = 2
            r0.pauseModelPush(r1)
            java.lang.Object r0 = r11.mLock
            monitor-enter(r0)
            boolean r1 = r11.stopLoader()     // Catch:{ all -> 0x0087 }
            boolean r2 = r11.mModelLoaded     // Catch:{ all -> 0x0087 }
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x0025
            boolean r2 = r11.mIsLoaderTaskRunning     // Catch:{ all -> 0x0087 }
            if (r2 != 0) goto L_0x0025
            r2 = r3
            goto L_0x0026
        L_0x0025:
            r2 = r4
        L_0x0026:
            if (r1 != 0) goto L_0x0030
            if (r2 == 0) goto L_0x0030
            int r1 = r12.length     // Catch:{ all -> 0x0087 }
            if (r1 != 0) goto L_0x002e
            goto L_0x0030
        L_0x002e:
            r1 = r4
            goto L_0x0031
        L_0x0030:
            r1 = r3
        L_0x0031:
            if (r1 == 0) goto L_0x0037
            com.android.launcher3.model.BgDataModel$Callbacks[] r12 = r11.getCallbacks()     // Catch:{ all -> 0x0087 }
        L_0x0037:
            int r5 = r12.length     // Catch:{ all -> 0x0087 }
            if (r5 <= 0) goto L_0x0085
            int r5 = r12.length     // Catch:{ all -> 0x0087 }
            r6 = r4
        L_0x003c:
            if (r6 >= r5) goto L_0x0050
            r7 = r12[r6]     // Catch:{ all -> 0x0087 }
            com.android.launcher3.util.LooperExecutor r8 = com.android.launcher3.util.Executors.MAIN_EXECUTOR     // Catch:{ all -> 0x0087 }
            java.util.Objects.requireNonNull(r7)     // Catch:{ all -> 0x0087 }
            com.android.launcher3.-$$Lambda$SmpP4SueD7qvMcde0R4W0uqNg8I r9 = new com.android.launcher3.-$$Lambda$SmpP4SueD7qvMcde0R4W0uqNg8I     // Catch:{ all -> 0x0087 }
            r9.<init>()     // Catch:{ all -> 0x0087 }
            r8.execute(r9)     // Catch:{ all -> 0x0087 }
            int r6 = r6 + 1
            goto L_0x003c
        L_0x0050:
            com.android.launcher3.model.LoaderResults r10 = new com.android.launcher3.model.LoaderResults     // Catch:{ all -> 0x0087 }
            com.android.launcher3.LauncherAppState r5 = r11.mApp     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.BgDataModel r6 = r11.mBgDataModel     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.AllAppsList r7 = r11.mBgAllAppsList     // Catch:{ all -> 0x0087 }
            r10.<init>(r5, r6, r7, r12)     // Catch:{ all -> 0x0087 }
            if (r2 == 0) goto L_0x006b
            r10.bindWorkspace(r1)     // Catch:{ all -> 0x0087 }
            r10.bindAllApps()     // Catch:{ all -> 0x0087 }
            r10.bindDeepShortcuts()     // Catch:{ all -> 0x0087 }
            r10.bindWidgets()     // Catch:{ all -> 0x0087 }
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            return r3
        L_0x006b:
            r11.stopLoader()     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.LoaderTask r12 = new com.android.launcher3.model.LoaderTask     // Catch:{ all -> 0x0087 }
            com.android.launcher3.LauncherAppState r6 = r11.mApp     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.AllAppsList r7 = r11.mBgAllAppsList     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.BgDataModel r8 = r11.mBgDataModel     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.ModelDelegate r9 = r11.mModelDelegate     // Catch:{ all -> 0x0087 }
            r5 = r12
            r5.<init>(r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0087 }
            r11.mLoaderTask = r12     // Catch:{ all -> 0x0087 }
            com.android.launcher3.util.LooperExecutor r12 = com.android.launcher3.util.Executors.MODEL_EXECUTOR     // Catch:{ all -> 0x0087 }
            com.android.launcher3.model.LoaderTask r1 = r11.mLoaderTask     // Catch:{ all -> 0x0087 }
            r12.post(r1)     // Catch:{ all -> 0x0087 }
        L_0x0085:
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            return r4
        L_0x0087:
            r12 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.startLoader(com.android.launcher3.model.BgDataModel$Callbacks[]):boolean");
    }

    private boolean stopLoader() {
        synchronized (this.mLock) {
            LoaderTask loaderTask = this.mLoaderTask;
            this.mLoaderTask = null;
            if (loaderTask == null) {
                return false;
            }
            loaderTask.stopLocked();
            return true;
        }
    }

    public void loadAsync(Consumer<BgDataModel> consumer) {
        synchronized (this.mLock) {
            if (!this.mModelLoaded && !this.mIsLoaderTaskRunning) {
                startLoader();
            }
        }
        Executors.MODEL_EXECUTOR.post(new Runnable(consumer) {
            public final /* synthetic */ Consumer f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LauncherModel.this.lambda$loadAsync$0$LauncherModel(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$loadAsync$0$LauncherModel(Consumer consumer) {
        consumer.accept(isModelLoaded() ? this.mBgDataModel : null);
    }

    public void onInstallSessionCreated(final PackageInstallInfo packageInstallInfo) {
        if (FeatureFlags.PROMISE_APPS_IN_ALL_APPS.get()) {
            enqueueModelUpdateTask(new BaseModelUpdateTask() {
                public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                    allAppsList.addPromiseApp(launcherAppState.getContext(), packageInstallInfo);
                    bindApplicationsIfNeeded();
                }
            });
        }
    }

    public void onSessionFailure(final String str, final UserHandle userHandle) {
        if (FeatureFlags.PROMISE_APPS_NEW_INSTALLS.get()) {
            enqueueModelUpdateTask(new BaseModelUpdateTask() {
                public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                    IntSet intSet = new IntSet();
                    synchronized (bgDataModel) {
                        Iterator<ItemInfo> it = bgDataModel.itemsIdMap.iterator();
                        while (it.hasNext()) {
                            ItemInfo next = it.next();
                            if ((next instanceof WorkspaceItemInfo) && ((WorkspaceItemInfo) next).hasPromiseIconUi() && userHandle.equals(next.user) && next.getIntent() != null && TextUtils.equals(str, next.getIntent().getPackage())) {
                                intSet.add(next.id);
                            }
                        }
                    }
                    if (!intSet.isEmpty()) {
                        deleteAndBindComponentsRemoved(ItemInfoMatcher.ofItemIds(intSet), "removed because install session failed");
                    }
                }
            });
        }
    }

    public void onPackageStateChanged(PackageInstallInfo packageInstallInfo) {
        enqueueModelUpdateTask(new PackageInstallStateChangedTask(packageInstallInfo));
    }

    public void onUpdateSessionDisplay(PackageUserKey packageUserKey, PackageInstaller.SessionInfo sessionInfo) {
        this.mApp.getIconCache().updateSessionCache(packageUserKey, sessionInfo);
        HashSet hashSet = new HashSet();
        hashSet.add(packageUserKey.mPackageName);
        enqueueModelUpdateTask(new CacheDataUpdatedTask(2, packageUserKey.mUser, hashSet));
    }

    public class LoaderTransaction implements AutoCloseable {
        private final LoaderTask mTask;

        private LoaderTransaction(LoaderTask loaderTask) throws CancellationException {
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == loaderTask) {
                    this.mTask = loaderTask;
                    boolean unused = LauncherModel.this.mIsLoaderTaskRunning = true;
                    boolean unused2 = LauncherModel.this.mModelLoaded = false;
                } else {
                    throw new CancellationException("Loader already stopped");
                }
            }
        }

        public void commit() {
            synchronized (LauncherModel.this.mLock) {
                boolean unused = LauncherModel.this.mModelLoaded = true;
            }
        }

        public void close() {
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == this.mTask) {
                    LoaderTask unused = LauncherModel.this.mLoaderTask = null;
                }
                boolean unused2 = LauncherModel.this.mIsLoaderTaskRunning = false;
            }
        }
    }

    public LoaderTransaction beginLoader(LoaderTask loaderTask) throws CancellationException {
        return new LoaderTransaction(loaderTask);
    }

    public void validateModelDataOnResume() {
        Executors.MODEL_EXECUTOR.getHandler().removeCallbacks(this.mDataValidationCheck);
        Executors.MODEL_EXECUTOR.post(this.mDataValidationCheck);
    }

    public void onPackageIconsUpdated(HashSet<String> hashSet, UserHandle userHandle) {
        enqueueModelUpdateTask(new CacheDataUpdatedTask(1, userHandle, hashSet));
    }

    public void onWidgetLabelsUpdated(final HashSet<String> hashSet, final UserHandle userHandle) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                bgDataModel.widgetsModel.onPackageIconsUpdated(hashSet, userHandle, launcherAppState);
                bindUpdatedWidgets(bgDataModel);
            }
        });
    }

    public void enqueueModelUpdateTask(ModelUpdateTask modelUpdateTask) {
        if (!this.mModelDestroyed) {
            modelUpdateTask.init(this.mApp, this, this.mBgDataModel, this.mBgAllAppsList, Executors.MAIN_EXECUTOR);
            Executors.MODEL_EXECUTOR.execute(modelUpdateTask);
        }
    }

    public void updateAndBindWorkspaceItem(WorkspaceItemInfo workspaceItemInfo, ShortcutInfo shortcutInfo) {
        updateAndBindWorkspaceItem(new Supplier(workspaceItemInfo, shortcutInfo) {
            public final /* synthetic */ WorkspaceItemInfo f$1;
            public final /* synthetic */ ShortcutInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object get() {
                return LauncherModel.this.lambda$updateAndBindWorkspaceItem$1$LauncherModel(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ WorkspaceItemInfo lambda$updateAndBindWorkspaceItem$1$LauncherModel(WorkspaceItemInfo workspaceItemInfo, ShortcutInfo shortcutInfo) {
        workspaceItemInfo.updateFromDeepShortcutInfo(shortcutInfo, this.mApp.getContext());
        this.mApp.getIconCache().getShortcutIcon(workspaceItemInfo, shortcutInfo);
        return workspaceItemInfo;
    }

    public void updateAndBindWorkspaceItem(final Supplier<WorkspaceItemInfo> supplier) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) supplier.get();
                getModelWriter().updateItemInDatabase(workspaceItemInfo);
                ArrayList arrayList = new ArrayList();
                arrayList.add(workspaceItemInfo);
                bindUpdatedWorkspaceItems(arrayList);
            }
        });
    }

    public void refreshAndBindWidgetsAndShortcuts(final PackageUserKey packageUserKey) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                bgDataModel.widgetsModel.update(launcherAppState, packageUserKey);
                bindUpdatedWidgets(bgDataModel);
            }
        });
    }

    public void dumpState(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr.length > 0 && TextUtils.equals(strArr[0], "--all")) {
            printWriter.println(str + "All apps list: size=" + this.mBgAllAppsList.data.size());
            Iterator<AppInfo> it = this.mBgAllAppsList.data.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                printWriter.println(str + "   title=\"" + next.title + "\" bitmapIcon=" + next.bitmap.icon + " componentName=" + next.componentName.getPackageName());
            }
            printWriter.println();
        }
        this.mModelDelegate.dump(str, fileDescriptor, printWriter, strArr);
        this.mBgDataModel.dump(str, fileDescriptor, printWriter, strArr);
    }

    public boolean hasCallbacks() {
        boolean z;
        synchronized (this.mCallbacksList) {
            z = !this.mCallbacksList.isEmpty();
        }
        return z;
    }

    public BgDataModel.Callbacks[] getCallbacks() {
        BgDataModel.Callbacks[] callbacksArr;
        synchronized (this.mCallbacksList) {
            ArrayList<BgDataModel.Callbacks> arrayList = this.mCallbacksList;
            callbacksArr = (BgDataModel.Callbacks[]) arrayList.toArray(new BgDataModel.Callbacks[arrayList.size()]);
        }
        return callbacksArr;
    }
}
