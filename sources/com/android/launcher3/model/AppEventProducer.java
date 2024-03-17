package com.android.launcher3.model;

import android.app.prediction.AppTarget;
import android.app.prediction.AppTargetEvent;
import android.app.prediction.AppTargetId;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logger.LauncherAtomExtensions;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.util.Executors;
import com.android.quickstep.logging.StatsLogCompatManager;
import java.util.Locale;
import java.util.function.ObjIntConsumer;

public class AppEventProducer implements StatsLogCompatManager.StatsLogConsumer {
    private static final int MSG_LAUNCH = 0;
    private final ObjIntConsumer<AppTargetEvent> mCallback;
    private final Context mContext;
    private LauncherAtom.ItemInfo mLastDragItem;
    private final Handler mMessageHandler = new Handler(Executors.MODEL_EXECUTOR.getLooper(), new Handler.Callback() {
        public final boolean handleMessage(Message message) {
            return AppEventProducer.this.handleMessage(message);
        }
    });

    public AppEventProducer(Context context, ObjIntConsumer<AppTargetEvent> objIntConsumer) {
        this.mContext = context;
        this.mCallback = objIntConsumer;
    }

    /* access modifiers changed from: private */
    public boolean handleMessage(Message message) {
        if (message.what != 0) {
            return false;
        }
        this.mCallback.accept((AppTargetEvent) message.obj, message.arg1);
        return true;
    }

    private void sendEvent(LauncherAtom.ItemInfo itemInfo, int i, int i2) {
        sendEvent(toAppTarget(itemInfo), itemInfo, i, i2);
    }

    private void sendEvent(AppTarget appTarget, LauncherAtom.ItemInfo itemInfo, int i, int i2) {
        if (appTarget != null && !Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            this.mMessageHandler.obtainMessage(0, i2, 0, new AppTargetEvent.Builder(appTarget, i).setLaunchLocation(getContainer(itemInfo)).build()).sendToTarget();
        }
    }

    public void consume(StatsLogManager.EventEnum eventEnum, LauncherAtom.ItemInfo itemInfo) {
        if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP || eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_TASK_LAUNCH_SWIPE_DOWN || eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_TASK_LAUNCH_TAP || eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_RIGHT || eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_QUICKSWITCH_LEFT) {
            sendEvent(itemInfo, 1, LauncherSettings.Favorites.CONTAINER_PREDICTION);
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROPPED_ON_DONT_SUGGEST) {
            sendEvent(itemInfo, 2, LauncherSettings.Favorites.CONTAINER_PREDICTION);
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DRAG_STARTED) {
            this.mLastDragItem = itemInfo;
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_COMPLETED) {
            LauncherAtom.ItemInfo itemInfo2 = this.mLastDragItem;
            if (itemInfo2 != null) {
                if (PredictionHelper.isTrackedForHotseatPrediction(itemInfo2)) {
                    sendEvent(this.mLastDragItem, 4, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
                }
                if (PredictionHelper.isTrackedForHotseatPrediction(itemInfo)) {
                    sendEvent(itemInfo, 3, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
                }
                if (PredictionHelper.isTrackedForWidgetPrediction(itemInfo)) {
                    sendEvent(itemInfo, 3, LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION);
                }
                this.mLastDragItem = null;
            }
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROP_FOLDER_CREATED) {
            if (PredictionHelper.isTrackedForHotseatPrediction(itemInfo)) {
                sendEvent(createTempFolderTarget(), itemInfo, 3, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
                sendEvent(itemInfo, 4, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
            }
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_CONVERTED_TO_ICON) {
            if (PredictionHelper.isTrackedForHotseatPrediction(itemInfo)) {
                sendEvent(createTempFolderTarget(), itemInfo, 4, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
                sendEvent(itemInfo, 3, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
            }
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROPPED_ON_REMOVE) {
            LauncherAtom.ItemInfo itemInfo3 = this.mLastDragItem;
            if (itemInfo3 != null && PredictionHelper.isTrackedForHotseatPrediction(itemInfo3)) {
                sendEvent(this.mLastDragItem, 4, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
            }
            LauncherAtom.ItemInfo itemInfo4 = this.mLastDragItem;
            if (itemInfo4 != null && PredictionHelper.isTrackedForWidgetPrediction(itemInfo4)) {
                sendEvent(this.mLastDragItem, 4, LauncherSettings.Favorites.CONTAINER_WIDGETS_PREDICTION);
            }
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_PREDICTION_PINNED) {
            if (PredictionHelper.isTrackedForHotseatPrediction(itemInfo)) {
                sendEvent(itemInfo, 3, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
            }
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_ONRESUME) {
            sendEvent(new AppTarget.Builder(new AppTargetId("launcher:launcher"), this.mContext.getPackageName(), Process.myUserHandle()).build(), itemInfo, 1, LauncherSettings.Favorites.CONTAINER_PREDICTION);
        } else if (eventEnum == StatsLogManager.LauncherEvent.LAUNCHER_DISMISS_PREDICTION_UNDO) {
            sendEvent(itemInfo, 5, LauncherSettings.Favorites.CONTAINER_HOTSEAT_PREDICTION);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: android.content.pm.ShortcutInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: android.content.pm.ShortcutInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: android.content.pm.ShortcutInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x013f A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.app.prediction.AppTarget toAppTarget(com.android.launcher3.logger.LauncherAtom.ItemInfo r10) {
        /*
            r9 = this;
            android.os.UserHandle r0 = android.os.Process.myUserHandle()
            boolean r1 = r10.getIsWork()
            r2 = 0
            if (r1 == 0) goto L_0x0037
            com.android.launcher3.util.MainThreadInitializedObject<com.android.launcher3.pm.UserCache> r1 = com.android.launcher3.pm.UserCache.INSTANCE
            android.content.Context r3 = r9.mContext
            java.lang.Object r1 = r1.lambda$get$1$MainThreadInitializedObject(r3)
            com.android.launcher3.pm.UserCache r1 = (com.android.launcher3.pm.UserCache) r1
            java.util.List r1 = r1.getUserProfiles()
            java.util.stream.Stream r1 = r1.stream()
            java.util.Objects.requireNonNull(r0)
            com.android.launcher3.model.-$$Lambda$AppEventProducer$_3RCGM2TKNdFDE_746YCnWJDUmU r3 = new com.android.launcher3.model.-$$Lambda$AppEventProducer$_3RCGM2TKNdFDE_746YCnWJDUmU
            r3.<init>(r0)
            java.util.function.Predicate r0 = r3.negate()
            java.util.stream.Stream r0 = r1.filter(r0)
            java.util.Optional r0 = r0.findAny()
            java.lang.Object r0 = r0.orElse(r2)
            android.os.UserHandle r0 = (android.os.UserHandle) r0
        L_0x0037:
            if (r0 != 0) goto L_0x003a
            return r2
        L_0x003a:
            int[] r1 = com.android.launcher3.model.AppEventProducer.AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase
            com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r3 = r10.getItemCase()
            int r3 = r3.ordinal()
            r1 = r1[r3]
            java.lang.String r3 = "app:"
            r4 = 1
            if (r1 == r4) goto L_0x0116
            r5 = 2
            if (r1 == r5) goto L_0x00aa
            r4 = 3
            if (r1 == r4) goto L_0x0083
            r4 = 4
            if (r1 == r4) goto L_0x005e
            r10 = 5
            if (r1 == r10) goto L_0x0059
            goto L_0x0112
        L_0x0059:
            android.app.prediction.AppTarget r10 = r9.createTempFolderTarget()
            return r10
        L_0x005e:
            com.android.launcher3.logger.LauncherAtom$Task r10 = r10.getTask()
            java.lang.String r10 = r10.getComponentName()
            android.content.ComponentName r10 = parseNullable(r10)
            if (r10 == 0) goto L_0x013b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r3 = r10.getPackageName()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            goto L_0x0139
        L_0x0083:
            com.android.launcher3.logger.LauncherAtom$Widget r10 = r10.getWidget()
            java.lang.String r10 = r10.getComponentName()
            android.content.ComponentName r10 = parseNullable(r10)
            if (r10 == 0) goto L_0x013b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "widget:"
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r3 = r10.getPackageName()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            goto L_0x0139
        L_0x00aa:
            com.android.launcher3.logger.LauncherAtom$Shortcut r10 = r10.getShortcut()
            java.lang.String r1 = r10.getShortcutId()
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0112
            java.lang.String r1 = r10.getShortcutName()
            android.content.ComponentName r1 = parseNullable(r1)
            if (r1 == 0) goto L_0x0110
            com.android.launcher3.shortcuts.ShortcutRequest r3 = new com.android.launcher3.shortcuts.ShortcutRequest
            android.content.Context r5 = r9.mContext
            r3.<init>(r5, r0)
            java.lang.String r5 = r1.getPackageName()
            java.lang.String[] r4 = new java.lang.String[r4]
            r6 = 0
            java.lang.String r7 = r10.getShortcutId()
            r4[r6] = r7
            com.android.launcher3.shortcuts.ShortcutRequest r3 = r3.forPackage((java.lang.String) r5, (java.lang.String[]) r4)
            r4 = 11
            com.android.launcher3.shortcuts.ShortcutRequest$QueryResult r3 = r3.query(r4)
            java.util.stream.Stream r3 = r3.stream()
            java.util.Optional r3 = r3.findFirst()
            boolean r4 = r3.isPresent()
            if (r4 == 0) goto L_0x010f
            java.lang.Object r3 = r3.get()
            android.content.pm.ShortcutInfo r3 = (android.content.pm.ShortcutInfo) r3
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "shortcut:"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r10 = r10.getShortcutId()
            java.lang.StringBuilder r10 = r4.append(r10)
            java.lang.String r10 = r10.toString()
            r8 = r1
            r1 = r10
            r10 = r8
            goto L_0x013d
        L_0x010f:
            return r2
        L_0x0110:
            r10 = r1
            goto L_0x013b
        L_0x0112:
            r10 = r2
            r1 = r10
        L_0x0114:
            r3 = r1
            goto L_0x013d
        L_0x0116:
            com.android.launcher3.logger.LauncherAtom$Application r10 = r10.getApplication()
            java.lang.String r10 = r10.getComponentName()
            android.content.ComponentName r10 = parseNullable(r10)
            if (r10 == 0) goto L_0x013b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r3 = r10.getPackageName()
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
        L_0x0139:
            r3 = r2
            goto L_0x013d
        L_0x013b:
            r1 = r2
            goto L_0x0114
        L_0x013d:
            if (r1 == 0) goto L_0x016d
            if (r10 == 0) goto L_0x016d
            if (r3 == 0) goto L_0x0152
            android.app.prediction.AppTarget$Builder r10 = new android.app.prediction.AppTarget$Builder
            android.app.prediction.AppTargetId r0 = new android.app.prediction.AppTargetId
            r0.<init>(r1)
            r10.<init>(r0, r3)
            android.app.prediction.AppTarget r10 = r10.build()
            return r10
        L_0x0152:
            android.app.prediction.AppTarget$Builder r2 = new android.app.prediction.AppTarget$Builder
            android.app.prediction.AppTargetId r3 = new android.app.prediction.AppTargetId
            r3.<init>(r1)
            java.lang.String r1 = r10.getPackageName()
            r2.<init>(r3, r1, r0)
            java.lang.String r10 = r10.getClassName()
            android.app.prediction.AppTarget$Builder r10 = r2.setClassName(r10)
            android.app.prediction.AppTarget r10 = r10.build()
            return r10
        L_0x016d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AppEventProducer.toAppTarget(com.android.launcher3.logger.LauncherAtom$ItemInfo):android.app.prediction.AppTarget");
    }

    private AppTarget createTempFolderTarget() {
        return new AppTarget.Builder(new AppTargetId("folder:" + SystemClock.uptimeMillis()), this.mContext.getPackageName(), Process.myUserHandle()).build();
    }

    /* renamed from: com.android.launcher3.model.AppEventProducer$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase;
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase;

        /* JADX WARNING: Can't wrap try/catch for region: R(38:0|(2:1|2)|3|(2:5|6)|7|9|10|11|(2:13|14)|15|17|18|19|20|21|22|23|24|25|26|(2:27|28)|29|31|32|33|34|35|37|38|39|40|41|42|43|44|45|46|48) */
        /* JADX WARNING: Can't wrap try/catch for region: R(39:0|(2:1|2)|3|5|6|7|9|10|11|(2:13|14)|15|17|18|19|20|21|22|23|24|25|26|(2:27|28)|29|31|32|33|34|35|37|38|39|40|41|42|43|44|45|46|48) */
        /* JADX WARNING: Can't wrap try/catch for region: R(41:0|1|2|3|5|6|7|9|10|11|(2:13|14)|15|17|18|19|20|21|22|23|24|25|26|27|28|29|31|32|33|34|35|37|38|39|40|41|42|43|44|45|46|48) */
        /* JADX WARNING: Can't wrap try/catch for region: R(42:0|1|2|3|5|6|7|9|10|11|13|14|15|17|18|19|20|21|22|23|24|25|26|27|28|29|31|32|33|34|35|37|38|39|40|41|42|43|44|45|46|48) */
        /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:19:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x0060 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x006c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x0089 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:39:0x00a4 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00ae */
        /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x00b8 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x00c2 */
        static {
            /*
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase[] r0 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase = r0
                r1 = 1
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r2 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.WORKSPACE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                r0 = 2
                int[] r2 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r3 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.HOTSEAT     // Catch:{ NoSuchFieldError -> 0x001d }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                r2 = 3
                int[] r3 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r4 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.TASK_SWITCHER_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r4 = r4.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r3[r4] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                r3 = 4
                int[] r4 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r5 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.ALL_APPS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r4[r5] = r3     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                r4 = 5
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.PREDICTED_HOTSEAT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x003e }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r5[r6] = r4     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.PREDICTION_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r7 = 6
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.SHORTCUTS_CONTAINER     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r7 = 7
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.FOLDER     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r7 = 8
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x006c }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.SEARCH_RESULT_CONTAINER     // Catch:{ NoSuchFieldError -> 0x006c }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x006c }
                r7 = 9
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x006c }
            L_0x006c:
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase     // Catch:{ NoSuchFieldError -> 0x0078 }
                com.android.launcher3.logger.LauncherAtom$ContainerInfo$ContainerCase r6 = com.android.launcher3.logger.LauncherAtom.ContainerInfo.ContainerCase.EXTENDED_CONTAINERS     // Catch:{ NoSuchFieldError -> 0x0078 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0078 }
                r7 = 10
                r5[r6] = r7     // Catch:{ NoSuchFieldError -> 0x0078 }
            L_0x0078:
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase[] r5 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.values()
                int r5 = r5.length
                int[] r5 = new int[r5]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase = r5
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase r6 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.WORKSPACE     // Catch:{ NoSuchFieldError -> 0x0089 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0089 }
                r5[r6] = r1     // Catch:{ NoSuchFieldError -> 0x0089 }
            L_0x0089:
                int[] r5 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase     // Catch:{ NoSuchFieldError -> 0x0093 }
                com.android.launcher3.logger.LauncherAtom$FolderContainer$ParentContainerCase r6 = com.android.launcher3.logger.LauncherAtom.FolderContainer.ParentContainerCase.HOTSEAT     // Catch:{ NoSuchFieldError -> 0x0093 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x0093 }
                r5[r6] = r0     // Catch:{ NoSuchFieldError -> 0x0093 }
            L_0x0093:
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase[] r5 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.values()
                int r5 = r5.length
                int[] r5 = new int[r5]
                $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase = r5
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r6 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.APPLICATION     // Catch:{ NoSuchFieldError -> 0x00a4 }
                int r6 = r6.ordinal()     // Catch:{ NoSuchFieldError -> 0x00a4 }
                r5[r6] = r1     // Catch:{ NoSuchFieldError -> 0x00a4 }
            L_0x00a4:
                int[] r1 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x00ae }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r5 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.SHORTCUT     // Catch:{ NoSuchFieldError -> 0x00ae }
                int r5 = r5.ordinal()     // Catch:{ NoSuchFieldError -> 0x00ae }
                r1[r5] = r0     // Catch:{ NoSuchFieldError -> 0x00ae }
            L_0x00ae:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x00b8 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.WIDGET     // Catch:{ NoSuchFieldError -> 0x00b8 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00b8 }
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x00b8 }
            L_0x00b8:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x00c2 }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.TASK     // Catch:{ NoSuchFieldError -> 0x00c2 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00c2 }
                r0[r1] = r3     // Catch:{ NoSuchFieldError -> 0x00c2 }
            L_0x00c2:
                int[] r0 = $SwitchMap$com$android$launcher3$logger$LauncherAtom$ItemInfo$ItemCase     // Catch:{ NoSuchFieldError -> 0x00cc }
                com.android.launcher3.logger.LauncherAtom$ItemInfo$ItemCase r1 = com.android.launcher3.logger.LauncherAtom.ItemInfo.ItemCase.FOLDER_ICON     // Catch:{ NoSuchFieldError -> 0x00cc }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x00cc }
                r0[r1] = r4     // Catch:{ NoSuchFieldError -> 0x00cc }
            L_0x00cc:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AppEventProducer.AnonymousClass1.<clinit>():void");
        }
    }

    private String getContainer(LauncherAtom.ItemInfo itemInfo) {
        LauncherAtom.ContainerInfo containerInfo = itemInfo.getContainerInfo();
        switch (AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$ContainerInfo$ContainerCase[containerInfo.getContainerCase().ordinal()]) {
            case 1:
                return getWorkspaceContainerString(containerInfo.getWorkspace(), itemInfo.getWidget().getSpanX(), itemInfo.getWidget().getSpanY());
            case 2:
                return getHotseatContainerString(containerInfo.getHotseat());
            case 3:
                return "task-switcher";
            case 4:
                return "all-apps";
            case 5:
                return "predictions/hotseat";
            case 6:
                return "predictions";
            case 7:
                return "deep-shortcuts";
            case 8:
                LauncherAtom.FolderContainer folder = containerInfo.getFolder();
                int i = AnonymousClass1.$SwitchMap$com$android$launcher3$logger$LauncherAtom$FolderContainer$ParentContainerCase[folder.getParentContainerCase().ordinal()];
                if (i == 1) {
                    return "folder/" + getWorkspaceContainerString(folder.getWorkspace(), 1, 1);
                }
                if (i != 2) {
                    return "folder";
                }
                return "folder/" + getHotseatContainerString(folder.getHotseat());
            case 9:
                break;
            case 10:
                if (containerInfo.getExtendedContainers().getContainerCase() == LauncherAtomExtensions.ExtendedContainers.ContainerCase.DEVICE_SEARCH_RESULT_CONTAINER) {
                    return "search-results";
                }
                return "";
            default:
                return "";
        }
        return "search-results";
    }

    private static String getWorkspaceContainerString(LauncherAtom.WorkspaceContainer workspaceContainer, int i, int i2) {
        return String.format(Locale.ENGLISH, "workspace/%d/[%d,%d]/[%d,%d]", new Object[]{Integer.valueOf(workspaceContainer.getPageIndex()), Integer.valueOf(workspaceContainer.getGridX()), Integer.valueOf(workspaceContainer.getGridY()), Integer.valueOf(i), Integer.valueOf(i2)});
    }

    private static String getHotseatContainerString(LauncherAtom.HotseatContainer hotseatContainer) {
        return String.format(Locale.ENGLISH, "hotseat/%1$d/[%1$d,0]/[1,1]", new Object[]{Integer.valueOf(hotseatContainer.getIndex())});
    }

    private static ComponentName parseNullable(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return ComponentName.unflattenFromString(str);
    }
}
