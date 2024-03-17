package com.android.launcher3.model;

import android.app.StatsManager;
import android.app.prediction.AppPredictionContext;
import android.app.prediction.AppPredictionManager;
import android.app.prediction.AppPredictor;
import android.app.prediction.AppTarget;
import android.app.prediction.AppTargetEvent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.util.LongSparseArray;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.hybridhotseat.HotseatPredictionModel;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.QuickstepModelDelegate;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.IntSparseArrayMap;
import com.android.launcher3.util.PersistedItemArray;
import com.android.quickstep.logging.SettingsChangeLogger;
import com.android.quickstep.logging.StatsLogCompatManager;
import com.android.systemui.shared.system.SysUiStatsLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.LongFunction;
import java.util.function.ObjIntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuickstepModelDelegate extends ModelDelegate {
    private static final String BUNDLE_KEY_ADDED_APP_WIDGETS = "added_app_widgets";
    private static final boolean IS_DEBUG = false;
    public static final String LAST_PREDICTION_ENABLED_STATE = "last_prediction_enabled_state";
    private static final String LAST_SNAPSHOT_TIME_MILLIS = "LAST_SNAPSHOT_TIME_MILLIS";
    private static final int NUM_OF_RECOMMENDED_WIDGETS_PREDICATION = 20;
    private static final String TAG = "QuickstepModelDelegate";
    protected boolean mActive = false;
    private final PredictorState mAllAppsState = new PredictorState(LauncherSettings.Favorites.CONTAINER_PREDICTION, "all_apps_predictions");
    private final AppEventProducer mAppEventProducer;
    private final Context mContext;
    private final PredictorState mHotseatState = new PredictorState(LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION, "hotseat_predictions");
    private final InvariantDeviceProfile mIDP;
    private final StatsManager mStatsManager;
    private final PredictorState mWidgetsRecommendationState = new PredictorState(LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION, "widgets_prediction");

    public static /* synthetic */ ArrayList lambda$E2McIAGPWBO62AkYPcfatUmDGDA() {
        return new ArrayList();
    }

    /* access modifiers changed from: protected */
    public void additionalSnapshotEvents(InstanceId instanceId) {
    }

    public QuickstepModelDelegate(Context context) {
        this.mContext = context;
        AppEventProducer appEventProducer = new AppEventProducer(context, new ObjIntConsumer() {
            public final void accept(Object obj, int i) {
                QuickstepModelDelegate.this.onAppTargetEvent((AppTargetEvent) obj, i);
            }
        });
        this.mAppEventProducer = appEventProducer;
        this.mIDP = InvariantDeviceProfile.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        StatsLogCompatManager.LOGS_CONSUMER.add(appEventProducer);
        this.mStatsManager = (StatsManager) context.getSystemService(StatsManager.class);
    }

    public void loadItems(UserManagerState userManagerState, Map<ShortcutKey, ShortcutInfo> map) {
        super.loadItems(userManagerState, map);
        WorkspaceItemFactory workspaceItemFactory = new WorkspaceItemFactory(this.mApp, userManagerState, map, this.mIDP.numDatabaseAllAppsColumns);
        int i = this.mAllAppsState.containerId;
        PersistedItemArray<ItemInfo> persistedItemArray = this.mAllAppsState.storage;
        Context context = this.mApp.getContext();
        LongSparseArray<UserHandle> longSparseArray = userManagerState.allUsers;
        Objects.requireNonNull(longSparseArray);
        this.mDataModel.extraItems.put(this.mAllAppsState.containerId, new BgDataModel.FixedContainerItems(i, persistedItemArray.read(context, workspaceItemFactory, new LongFunction(longSparseArray) {
            public final /* synthetic */ LongSparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(long j) {
                return (UserHandle) this.f$0.get(j);
            }
        })));
        WorkspaceItemFactory workspaceItemFactory2 = new WorkspaceItemFactory(this.mApp, userManagerState, map, this.mIDP.numDatabaseHotseatIcons);
        int i2 = this.mHotseatState.containerId;
        PersistedItemArray<ItemInfo> persistedItemArray2 = this.mHotseatState.storage;
        Context context2 = this.mApp.getContext();
        LongSparseArray<UserHandle> longSparseArray2 = userManagerState.allUsers;
        Objects.requireNonNull(longSparseArray2);
        this.mDataModel.extraItems.put(this.mHotseatState.containerId, new BgDataModel.FixedContainerItems(i2, persistedItemArray2.read(context2, workspaceItemFactory2, new LongFunction(longSparseArray2) {
            public final /* synthetic */ LongSparseArray f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(long j) {
                return (UserHandle) this.f$0.get(j);
            }
        })));
        this.mDataModel.extraItems.put(this.mWidgetsRecommendationState.containerId, new BgDataModel.FixedContainerItems(this.mWidgetsRecommendationState.containerId));
        this.mActive = true;
    }

    public void workspaceLoadComplete() {
        super.workspaceLoadComplete();
        recreatePredictors();
    }

    public void modelLoadComplete() {
        IntSparseArrayMap clone;
        super.modelLoadComplete();
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(this.mApp.getContext());
        long j = devicePrefs.getLong(LAST_SNAPSHOT_TIME_MILLIS, 0);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - j >= 86400000) {
            synchronized (this.mDataModel) {
                clone = this.mDataModel.itemsIdMap.clone();
            }
            InstanceId newInstanceId = new InstanceIdSequence().newInstanceId();
            Iterator it = clone.iterator();
            while (it.hasNext()) {
                ItemInfo itemInfo = (ItemInfo) it.next();
                StatsLogCompatManager.writeSnapshot(itemInfo.buildProto(getContainer(itemInfo, clone)), newInstanceId);
            }
            additionalSnapshotEvents(newInstanceId);
            devicePrefs.edit().putLong(LAST_SNAPSHOT_TIME_MILLIS, currentTimeMillis).apply();
        }
        if (this.mIsPrimaryInstance) {
            registerSnapshotLoggingCallback();
        }
    }

    /* access modifiers changed from: protected */
    public void registerSnapshotLoggingCallback() {
        if (this.mStatsManager == null) {
            Log.d(TAG, "Failed to get StatsManager");
        }
        try {
            this.mStatsManager.setPullAtomCallback(SysUiStatsLog.LAUNCHER_LAYOUT_SNAPSHOT, (StatsManager.PullAtomMetadata) null, Executors.MODEL_EXECUTOR, new StatsManager.StatsPullAtomCallback() {
                public final int onPullAtom(int i, List list) {
                    return QuickstepModelDelegate.this.lambda$registerSnapshotLoggingCallback$0$QuickstepModelDelegate(i, list);
                }
            });
            Log.d(TAG, "Successfully registered for launcher snapshot logging!");
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to register launcher snapshot logging callback with StatsManager", e);
        }
    }

    public /* synthetic */ int lambda$registerSnapshotLoggingCallback$0$QuickstepModelDelegate(int i, List list) {
        IntSparseArrayMap clone;
        InstanceId newInstanceId = new InstanceIdSequence().newInstanceId();
        synchronized (this.mDataModel) {
            clone = this.mDataModel.itemsIdMap.clone();
        }
        Iterator it = clone.iterator();
        while (it.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it.next();
            LauncherAtom.ItemInfo buildProto = itemInfo.buildProto(getContainer(itemInfo, clone));
            Log.d(TAG, buildProto.toString());
            list.add(StatsLogCompatManager.buildStatsEvent(buildProto, newInstanceId));
        }
        Log.d(TAG, String.format("Successfully logged %d workspace items with instanceId=%d", new Object[]{Integer.valueOf(clone.size()), Integer.valueOf(newInstanceId.getId())}));
        additionalSnapshotEvents(newInstanceId);
        SettingsChangeLogger.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).logSnapshot(newInstanceId);
        return 0;
    }

    private static FolderInfo getContainer(ItemInfo itemInfo, IntSparseArrayMap<ItemInfo> intSparseArrayMap) {
        if (itemInfo.container <= 0) {
            return null;
        }
        ItemInfo itemInfo2 = (ItemInfo) intSparseArrayMap.get(itemInfo.container);
        if (!(itemInfo2 instanceof FolderInfo)) {
            Log.e(TAG, String.format("Item info: %s found with invalid container: %s", new Object[]{itemInfo, itemInfo2}));
        }
        return (FolderInfo) itemInfo2;
    }

    public void validateData() {
        super.validateData();
        if (this.mAllAppsState.predictor != null) {
            this.mAllAppsState.predictor.requestPredictionUpdate();
        }
        if (this.mWidgetsRecommendationState.predictor != null) {
            this.mWidgetsRecommendationState.predictor.requestPredictionUpdate();
        }
    }

    public void destroy() {
        super.destroy();
        this.mActive = false;
        StatsLogCompatManager.LOGS_CONSUMER.remove(this.mAppEventProducer);
        if (this.mIsPrimaryInstance) {
            this.mStatsManager.clearPullAtomCallback(SysUiStatsLog.LAUNCHER_LAYOUT_SNAPSHOT);
        }
        destroyPredictors();
    }

    private void destroyPredictors() {
        this.mAllAppsState.destroyPredictor();
        this.mHotseatState.destroyPredictor();
        this.mWidgetsRecommendationState.destroyPredictor();
    }

    private void recreatePredictors() {
        destroyPredictors();
        if (this.mActive) {
            Context context = this.mApp.getContext();
            AppPredictionManager appPredictionManager = (AppPredictionManager) context.getSystemService(AppPredictionManager.class);
            if (appPredictionManager != null) {
                registerPredictor(this.mAllAppsState, appPredictionManager.createAppPredictionSession(new AppPredictionContext.Builder(context).setUiSurface("home").setPredictedTargetCount(this.mIDP.numDatabaseAllAppsColumns).build()));
                registerPredictor(this.mHotseatState, appPredictionManager.createAppPredictionSession(new AppPredictionContext.Builder(context).setUiSurface("hotseat").setPredictedTargetCount(this.mIDP.numDatabaseHotseatIcons).setExtras(HotseatPredictionModel.convertDataModelToAppTargetBundle(context, this.mDataModel)).build()));
                registerWidgetsPredictor(appPredictionManager.createAppPredictionSession(new AppPredictionContext.Builder(context).setUiSurface("widgets").setExtras(getBundleForWidgetsOnWorkspace(context, this.mDataModel)).setPredictedTargetCount(20).build()));
            }
        }
    }

    private void registerPredictor(PredictorState predictorState, AppPredictor appPredictor) {
        predictorState.predictor = appPredictor;
        predictorState.predictor.registerPredictionUpdates(Executors.MODEL_EXECUTOR, new AppPredictor.Callback(predictorState) {
            public final /* synthetic */ QuickstepModelDelegate.PredictorState f$1;

            {
                this.f$1 = r2;
            }

            public final void onTargetsAvailable(List list) {
                QuickstepModelDelegate.this.lambda$registerPredictor$1$QuickstepModelDelegate(this.f$1, list);
            }
        });
        predictorState.predictor.requestPredictionUpdate();
    }

    /* access modifiers changed from: private */
    /* renamed from: handleUpdate */
    public void lambda$registerPredictor$1$QuickstepModelDelegate(PredictorState predictorState, List<AppTarget> list) {
        if (!predictorState.setTargets(list)) {
            this.mApp.getModel().enqueueModelUpdateTask(new PredictionUpdateTask(predictorState, list));
        }
    }

    private void registerWidgetsPredictor(AppPredictor appPredictor) {
        this.mWidgetsRecommendationState.predictor = appPredictor;
        this.mWidgetsRecommendationState.predictor.registerPredictionUpdates(Executors.MODEL_EXECUTOR, new AppPredictor.Callback() {
            public final void onTargetsAvailable(List list) {
                QuickstepModelDelegate.this.lambda$registerWidgetsPredictor$2$QuickstepModelDelegate(list);
            }
        });
        this.mWidgetsRecommendationState.predictor.requestPredictionUpdate();
    }

    public /* synthetic */ void lambda$registerWidgetsPredictor$2$QuickstepModelDelegate(List list) {
        if (!this.mWidgetsRecommendationState.setTargets(list)) {
            this.mApp.getModel().enqueueModelUpdateTask(new WidgetsPredictionUpdateTask(this.mWidgetsRecommendationState, list));
        }
    }

    /* access modifiers changed from: private */
    public void onAppTargetEvent(AppTargetEvent appTargetEvent, int i) {
        PredictorState predictorState;
        if (i == -111) {
            predictorState = this.mWidgetsRecommendationState;
        } else if (i != -102) {
            predictorState = this.mHotseatState;
        } else {
            predictorState = this.mAllAppsState;
        }
        if (predictorState.predictor != null) {
            predictorState.predictor.notifyAppTargetEvent(appTargetEvent);
            Log.d(TAG, "notifyAppTargetEvent action=" + appTargetEvent.getAction() + " launchLocation=" + appTargetEvent.getLaunchLocation());
        }
    }

    private Bundle getBundleForWidgetsOnWorkspace(Context context, BgDataModel bgDataModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BUNDLE_KEY_ADDED_APP_WIDGETS, (ArrayList) bgDataModel.getAllWorkspaceItems().stream().filter($$Lambda$3OuuS3VRjeQqVClpMT_zkjgRPDU.INSTANCE).map(new Function(context) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return QuickstepModelDelegate.lambda$getBundleForWidgetsOnWorkspace$3(this.f$0, (ItemInfo) obj);
            }
        }).filter($$Lambda$QuickstepModelDelegate$CIC8enwnqbCz_sS3RxmPtNbyUms.INSTANCE).collect(Collectors.toCollection($$Lambda$QuickstepModelDelegate$E2McIAGPWBO62AkYPcfatUmDGDA.INSTANCE)));
        return bundle;
    }

    static /* synthetic */ AppTargetEvent lambda$getBundleForWidgetsOnWorkspace$3(Context context, ItemInfo itemInfo) {
        AppTarget appTargetFromItemInfo = PredictionHelper.getAppTargetFromItemInfo(context, itemInfo);
        if (appTargetFromItemInfo == null) {
            return null;
        }
        return PredictionHelper.wrapAppTargetWithItemLocation(appTargetFromItemInfo, 3, itemInfo);
    }

    static class PredictorState {
        public final int containerId;
        private List<AppTarget> mLastTargets = Collections.emptyList();
        public AppPredictor predictor;
        public final PersistedItemArray<ItemInfo> storage;

        PredictorState(int i, String str) {
            this.containerId = i;
            this.storage = new PersistedItemArray<>(str);
        }

        public void destroyPredictor() {
            AppPredictor appPredictor = this.predictor;
            if (appPredictor != null) {
                appPredictor.destroy();
                this.predictor = null;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean setTargets(List<AppTarget> list) {
            List<AppTarget> list2 = this.mLastTargets;
            this.mLastTargets = list;
            int size = list2.size();
            if (size != list.size() || !IntStream.range(0, size).allMatch(new IntPredicate(list2, list) {
                public final /* synthetic */ List f$0;
                public final /* synthetic */ List f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final boolean test(int i) {
                    return QuickstepModelDelegate.areAppTargetsSame((AppTarget) this.f$0.get(i), (AppTarget) this.f$1.get(i));
                }
            })) {
                return false;
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public static boolean areAppTargetsSame(AppTarget appTarget, AppTarget appTarget2) {
        if (!Objects.equals(appTarget.getPackageName(), appTarget2.getPackageName()) || !Objects.equals(appTarget.getUser(), appTarget2.getUser()) || !Objects.equals(appTarget.getClassName(), appTarget2.getClassName())) {
            return false;
        }
        ShortcutInfo shortcutInfo = appTarget.getShortcutInfo();
        ShortcutInfo shortcutInfo2 = appTarget2.getShortcutInfo();
        if (shortcutInfo != null) {
            if (shortcutInfo2 == null || !Objects.equals(shortcutInfo.getId(), shortcutInfo2.getId())) {
                return false;
            }
            return true;
        } else if (shortcutInfo2 != null) {
            return false;
        } else {
            return true;
        }
    }

    private static class WorkspaceItemFactory implements PersistedItemArray.ItemFactory<ItemInfo> {
        private final LauncherAppState mAppState;
        private final int mMaxCount;
        private final Map<ShortcutKey, ShortcutInfo> mPinnedShortcuts;
        private int mReadCount = 0;
        private final UserManagerState mUMS;

        protected WorkspaceItemFactory(LauncherAppState launcherAppState, UserManagerState userManagerState, Map<ShortcutKey, ShortcutInfo> map, int i) {
            this.mAppState = launcherAppState;
            this.mUMS = userManagerState;
            this.mPinnedShortcuts = map;
            this.mMaxCount = i;
        }

        public ItemInfo createInfo(int i, UserHandle userHandle, Intent intent) {
            ShortcutKey fromIntent;
            ShortcutInfo shortcutInfo;
            if (this.mReadCount >= this.mMaxCount) {
                return null;
            }
            if (i == 0) {
                LauncherActivityInfo resolveActivity = ((LauncherApps) this.mAppState.getContext().getSystemService(LauncherApps.class)).resolveActivity(intent, userHandle);
                if (resolveActivity == null) {
                    return null;
                }
                AppInfo appInfo = new AppInfo(resolveActivity, userHandle, this.mUMS.isUserQuiet(userHandle));
                this.mAppState.getIconCache().getTitleAndIcon(appInfo, resolveActivity, false);
                this.mReadCount++;
                return appInfo.makeWorkspaceItem(this.mAppState.getContext());
            } else if (i != 6 || (fromIntent = ShortcutKey.fromIntent(intent, userHandle)) == null || (shortcutInfo = this.mPinnedShortcuts.get(fromIntent)) == null) {
                return null;
            } else {
                WorkspaceItemInfo workspaceItemInfo = new WorkspaceItemInfo(shortcutInfo, this.mAppState.getContext());
                this.mAppState.getIconCache().getShortcutIcon(workspaceItemInfo, shortcutInfo);
                this.mReadCount++;
                return workspaceItemInfo;
            }
        }
    }
}
