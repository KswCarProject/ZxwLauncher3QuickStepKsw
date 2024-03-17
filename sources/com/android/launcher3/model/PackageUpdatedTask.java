package com.android.launcher3.model;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.PackageManagerHelper;

public class PackageUpdatedTask extends BaseModelUpdateTask {
    private static final boolean DEBUG = false;
    public static final int OP_ADD = 1;
    public static final int OP_NONE = 0;
    public static final int OP_REMOVE = 3;
    public static final int OP_SUSPEND = 5;
    public static final int OP_UNAVAILABLE = 4;
    public static final int OP_UNSUSPEND = 6;
    public static final int OP_UPDATE = 2;
    public static final int OP_USER_AVAILABILITY_CHANGE = 7;
    private static final String TAG = "PackageUpdatedTask";
    private final int mOp;
    private final String[] mPackages;
    private final UserHandle mUser;

    public PackageUpdatedTask(int i, UserHandle userHandle, String... strArr) {
        this.mOp = i;
        this.mUser = userHandle;
        this.mPackages = strArr;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0231, code lost:
        r3.bindUpdatedWorkspaceItems(r20);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x023a, code lost:
        if (r21.isEmpty() != false) goto L_0x0245;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x023c, code lost:
        r3.deleteAndBindComponentsRemoved(com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r21), "removed because the target component is invalid");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0249, code lost:
        if (r5.isEmpty() != false) goto L_0x0253;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x024b, code lost:
        r3.scheduleCallbackTask(new com.android.launcher3.model.$$Lambda$PackageUpdatedTask$EOjf9d9GXSQJa14OpORjl_i5GnE(r5));
     */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02ca  */
    /* JADX WARNING: Removed duplicated region for block: B:138:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(com.android.launcher3.LauncherAppState r28, com.android.launcher3.model.BgDataModel r29, com.android.launcher3.model.AllAppsList r30) {
        /*
            r27 = this;
            r14 = r27
            r15 = r29
            r0 = r30
            android.content.Context r13 = r28.getContext()
            com.android.launcher3.icons.IconCache r11 = r28.getIconCache()
            java.lang.String[] r12 = r14.mPackages
            int r10 = r12.length
            java.util.HashSet r9 = new java.util.HashSet
            java.util.List r1 = java.util.Arrays.asList(r12)
            r9.<init>(r1)
            int r1 = r14.mOp
            r2 = 7
            if (r1 != r2) goto L_0x0026
            android.os.UserHandle r1 = r14.mUser
            java.util.function.Predicate r1 = com.android.launcher3.util.ItemInfoMatcher.ofUser(r1)
            goto L_0x002c
        L_0x0026:
            android.os.UserHandle r1 = r14.mUser
            java.util.function.Predicate r1 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r9, r1)
        L_0x002c:
            r5 = r1
            java.util.HashSet r8 = new java.util.HashSet
            r8.<init>()
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            int r1 = r14.mOp
            r4 = 2
            r16 = 0
            switch(r1) {
                case 1: goto L_0x012f;
                case 2: goto L_0x00d0;
                case 3: goto L_0x0091;
                case 4: goto L_0x008f;
                case 5: goto L_0x007a;
                case 6: goto L_0x007a;
                case 7: goto L_0x0045;
                default: goto L_0x003f;
            }
        L_0x003f:
            r20 = r8
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
            goto L_0x0162
        L_0x0045:
            com.android.launcher3.model.UserManagerState r1 = new com.android.launcher3.model.UserManagerState
            r1.<init>()
            com.android.launcher3.util.MainThreadInitializedObject<com.android.launcher3.pm.UserCache> r2 = com.android.launcher3.pm.UserCache.INSTANCE
            java.lang.Object r2 = r2.lambda$get$1$MainThreadInitializedObject(r13)
            com.android.launcher3.pm.UserCache r2 = (com.android.launcher3.pm.UserCache) r2
            java.lang.Class<android.os.UserManager> r3 = android.os.UserManager.class
            java.lang.Object r3 = r13.getSystemService(r3)
            android.os.UserManager r3 = (android.os.UserManager) r3
            r1.init(r2, r3)
            com.android.launcher3.util.FlagOp r2 = com.android.launcher3.util.FlagOp.NO_OP
            r3 = 8
            android.os.UserHandle r6 = r14.mUser
            boolean r6 = r1.isUserQuiet((android.os.UserHandle) r6)
            com.android.launcher3.util.FlagOp r2 = r2.setFlag(r3, r6)
            r0.updateDisabledFlags(r5, r2)
            boolean r1 = r1.isAnyProfileQuietModeEnabled()
            r0.setFlags(r4, r1)
            r0 = r2
        L_0x0076:
            r20 = r8
            goto L_0x0162
        L_0x007a:
            com.android.launcher3.util.FlagOp r1 = com.android.launcher3.util.FlagOp.NO_OP
            int r2 = r14.mOp
            r3 = 5
            if (r2 != r3) goto L_0x0083
            r2 = 1
            goto L_0x0085
        L_0x0083:
            r2 = r16
        L_0x0085:
            r6 = 4
            com.android.launcher3.util.FlagOp r1 = r1.setFlag(r6, r2)
            r0.updateDisabledFlags(r5, r1)
            r0 = r1
            goto L_0x0076
        L_0x008f:
            r6 = 4
            goto L_0x00bb
        L_0x0091:
            r6 = 4
            r1 = r16
        L_0x0094:
            if (r1 >= r10) goto L_0x00bb
            java.lang.String r2 = "PackageUpdatedTask"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "Removing app icon"
            java.lang.StringBuilder r3 = r3.append(r6)
            r6 = r12[r1]
            java.lang.StringBuilder r3 = r3.append(r6)
            java.lang.String r3 = r3.toString()
            com.android.launcher3.logging.FileLog.d(r2, r3)
            r2 = r12[r1]
            android.os.UserHandle r3 = r14.mUser
            r11.removeIconsForPkg(r2, r3)
            int r1 = r1 + 1
            r6 = 4
            goto L_0x0094
        L_0x00bb:
            r1 = r16
        L_0x00bd:
            if (r1 >= r10) goto L_0x00c9
            r2 = r12[r1]
            android.os.UserHandle r3 = r14.mUser
            r0.removePackage(r2, r3)
            int r1 = r1 + 1
            goto L_0x00bd
        L_0x00c9:
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
            com.android.launcher3.util.FlagOp r0 = r0.addFlag(r4)
            goto L_0x0076
        L_0x00d0:
            com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$Qp4OHahTn1dopGaSFhSfqtK11FQ r1 = new com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$Qp4OHahTn1dopGaSFhSfqtK11FQ
            r1.<init>(r8)
            com.android.launcher3.util.SafeCloseable r1 = r0.trackRemoves(r1)
            r2 = r16
        L_0x00db:
            if (r2 >= r10) goto L_0x0120
            r3 = r12[r2]     // Catch:{ all -> 0x0112 }
            android.os.UserHandle r6 = r14.mUser     // Catch:{ all -> 0x0112 }
            r11.updateIconsForPkg(r3, r6)     // Catch:{ all -> 0x0112 }
            r3 = r12[r2]     // Catch:{ all -> 0x0112 }
            r6 = r12[r2]     // Catch:{ all -> 0x0112 }
            android.os.UserHandle r4 = r14.mUser     // Catch:{ all -> 0x0112 }
            java.util.List r4 = r0.updatePackage(r13, r6, r4)     // Catch:{ all -> 0x0112 }
            r7.put(r3, r4)     // Catch:{ all -> 0x0112 }
            com.android.launcher3.util.ActivityTracker<com.android.launcher3.Launcher> r3 = com.android.launcher3.Launcher.ACTIVITY_TRACKER     // Catch:{ all -> 0x0112 }
            com.android.launcher3.BaseActivity r3 = r3.getCreatedActivity()     // Catch:{ all -> 0x0112 }
            com.android.launcher3.Launcher r3 = (com.android.launcher3.Launcher) r3     // Catch:{ all -> 0x0112 }
            if (r3 == 0) goto L_0x010a
            com.android.launcher3.util.PackageUserKey r4 = new com.android.launcher3.util.PackageUserKey     // Catch:{ all -> 0x0112 }
            r6 = r12[r2]     // Catch:{ all -> 0x0112 }
            r20 = r8
            android.os.UserHandle r8 = r14.mUser     // Catch:{ all -> 0x0112 }
            r4.<init>((java.lang.String) r6, (android.os.UserHandle) r8)     // Catch:{ all -> 0x0112 }
            r3.refreshAndBindWidgetsForPackageUser(r4)     // Catch:{ all -> 0x0112 }
            goto L_0x010c
        L_0x010a:
            r20 = r8
        L_0x010c:
            int r2 = r2 + 1
            r8 = r20
            r4 = 2
            goto L_0x00db
        L_0x0112:
            r0 = move-exception
            r2 = r0
            if (r1 == 0) goto L_0x011f
            r1.close()     // Catch:{ all -> 0x011a }
            goto L_0x011f
        L_0x011a:
            r0 = move-exception
            r1 = r0
            r2.addSuppressed(r1)
        L_0x011f:
            throw r2
        L_0x0120:
            r20 = r8
            if (r1 == 0) goto L_0x0127
            r1.close()
        L_0x0127:
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
            r1 = 2
            com.android.launcher3.util.FlagOp r0 = r0.removeFlag(r1)
            goto L_0x0162
        L_0x012f:
            r20 = r8
            r1 = r16
        L_0x0133:
            if (r1 >= r10) goto L_0x015b
            r2 = r12[r1]
            android.os.UserHandle r3 = r14.mUser
            r11.updateIconsForPkg(r2, r3)
            com.android.launcher3.config.FeatureFlags$BooleanFlag r2 = com.android.launcher3.config.FeatureFlags.PROMISE_APPS_IN_ALL_APPS
            boolean r2 = r2.get()
            if (r2 == 0) goto L_0x014b
            r2 = r12[r1]
            android.os.UserHandle r3 = r14.mUser
            r0.removePackage(r2, r3)
        L_0x014b:
            r2 = r12[r1]
            r3 = r12[r1]
            android.os.UserHandle r4 = r14.mUser
            java.util.List r3 = r0.addPackage(r13, r3, r4)
            r7.put(r2, r3)
            int r1 = r1 + 1
            goto L_0x0133
        L_0x015b:
            com.android.launcher3.util.FlagOp r0 = com.android.launcher3.util.FlagOp.NO_OP
            r1 = 2
            com.android.launcher3.util.FlagOp r0 = r0.removeFlag(r1)
        L_0x0162:
            r27.bindApplicationsIfNeeded()
            com.android.launcher3.util.IntSet r21 = new com.android.launcher3.util.IntSet
            r21.<init>()
            com.android.launcher3.util.IntSet r22 = new com.android.launcher3.util.IntSet
            r22.<init>()
            int r1 = r14.mOp
            r3 = 1
            if (r1 == r3) goto L_0x0184
            com.android.launcher3.util.FlagOp r1 = com.android.launcher3.util.FlagOp.NO_OP
            if (r0 == r1) goto L_0x0179
            goto L_0x0184
        L_0x0179:
            r25 = r10
            r26 = r12
            r0 = r13
            r3 = r14
            r17 = r20
            r4 = 2
            goto L_0x0253
        L_0x0184:
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            int r1 = r14.mOp
            r4 = 2
            if (r1 == r3) goto L_0x0199
            if (r1 != r4) goto L_0x0196
            goto L_0x0199
        L_0x0196:
            r17 = r16
            goto L_0x019b
        L_0x0199:
            r17 = r3
        L_0x019b:
            monitor-enter(r29)
            android.os.UserHandle r2 = r14.mUser     // Catch:{ all -> 0x02e9 }
            com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$UnGlBmBYwzUcpym2fgK8fhLFSTo r1 = new com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$UnGlBmBYwzUcpym2fgK8fhLFSTo     // Catch:{ all -> 0x02e9 }
            r30 = r1
            r1 = r30
            r14 = r2
            r2 = r27
            r3 = r9
            r4 = r13
            r23 = r6
            r18 = 4
            r6 = r22
            r19 = r7
            r7 = r17
            r17 = r20
            r20 = r8
            r8 = r21
            r24 = r9
            r9 = r17
            r25 = r10
            r10 = r19
            r26 = r12
            r12 = r0
            r0 = r13
            r13 = r20
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ all -> 0x02e5 }
            r15.forAllWorkspaceItemInfos(r14, r1)     // Catch:{ all -> 0x02e5 }
            java.util.ArrayList<com.android.launcher3.model.data.LauncherAppWidgetInfo> r1 = r15.appWidgets     // Catch:{ all -> 0x02e5 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x02e5 }
        L_0x01d3:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x02e5 }
            if (r2 == 0) goto L_0x022b
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x02e5 }
            com.android.launcher3.model.data.LauncherAppWidgetInfo r2 = (com.android.launcher3.model.data.LauncherAppWidgetInfo) r2     // Catch:{ all -> 0x02e5 }
            r3 = r27
            android.os.UserHandle r4 = r3.mUser     // Catch:{ all -> 0x02ed }
            android.os.UserHandle r5 = r2.user     // Catch:{ all -> 0x02ed }
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x02ed }
            if (r4 == 0) goto L_0x0221
            r4 = 2
            boolean r5 = r2.hasRestoreFlag(r4)     // Catch:{ all -> 0x02ed }
            if (r5 == 0) goto L_0x021c
            android.content.ComponentName r5 = r2.providerName     // Catch:{ all -> 0x02ed }
            java.lang.String r5 = r5.getPackageName()     // Catch:{ all -> 0x02ed }
            r6 = r24
            boolean r5 = r6.contains(r5)     // Catch:{ all -> 0x02ed }
            if (r5 == 0) goto L_0x0219
            int r5 = r2.restoreStatus     // Catch:{ all -> 0x02ed }
            r5 = r5 & -11
            r2.restoreStatus = r5     // Catch:{ all -> 0x02ed }
            int r5 = r2.restoreStatus     // Catch:{ all -> 0x02ed }
            r5 = r5 | 4
            r2.restoreStatus = r5     // Catch:{ all -> 0x02ed }
            r5 = r23
            r5.add(r2)     // Catch:{ all -> 0x02ed }
            com.android.launcher3.model.ModelWriter r7 = r27.getModelWriter()     // Catch:{ all -> 0x02ed }
            r7.updateItemInDatabase(r2)     // Catch:{ all -> 0x02ed }
            goto L_0x0226
        L_0x0219:
            r5 = r23
            goto L_0x0226
        L_0x021c:
            r5 = r23
            r6 = r24
            goto L_0x0226
        L_0x0221:
            r5 = r23
            r6 = r24
            r4 = 2
        L_0x0226:
            r23 = r5
            r24 = r6
            goto L_0x01d3
        L_0x022b:
            r4 = 2
            r3 = r27
            r5 = r23
            monitor-exit(r29)     // Catch:{ all -> 0x02ed }
            r1 = r20
            r3.bindUpdatedWorkspaceItems(r1)
            boolean r1 = r21.isEmpty()
            if (r1 != 0) goto L_0x0245
            java.util.function.Predicate r1 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r21)
            java.lang.String r2 = "removed because the target component is invalid"
            r3.deleteAndBindComponentsRemoved(r1, r2)
        L_0x0245:
            boolean r1 = r5.isEmpty()
            if (r1 != 0) goto L_0x0253
            com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$EOjf9d9GXSQJa14OpORjl_i5GnE r1 = new com.android.launcher3.model.-$$Lambda$PackageUpdatedTask$EOjf9d9GXSQJa14OpORjl_i5GnE
            r1.<init>(r5)
            r3.scheduleCallbackTask(r1)
        L_0x0253:
            java.util.HashSet r1 = new java.util.HashSet
            r1.<init>()
            int r2 = r3.mOp
            r5 = 3
            if (r2 != r5) goto L_0x0263
            r5 = r26
            java.util.Collections.addAll(r1, r5)
            goto L_0x0287
        L_0x0263:
            r5 = r26
            if (r2 != r4) goto L_0x0287
            java.lang.Class<android.content.pm.LauncherApps> r2 = android.content.pm.LauncherApps.class
            java.lang.Object r2 = r0.getSystemService(r2)
            android.content.pm.LauncherApps r2 = (android.content.pm.LauncherApps) r2
            r6 = r16
            r4 = r25
        L_0x0273:
            if (r6 >= r4) goto L_0x0289
            r7 = r5[r6]
            android.os.UserHandle r8 = r3.mUser
            boolean r7 = r2.isPackageEnabled(r7, r8)
            if (r7 != 0) goto L_0x0284
            r7 = r5[r6]
            r1.add(r7)
        L_0x0284:
            int r6 = r6 + 1
            goto L_0x0273
        L_0x0287:
            r4 = r25
        L_0x0289:
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L_0x0295
            boolean r2 = r17.isEmpty()
            if (r2 != 0) goto L_0x02c5
        L_0x0295:
            android.os.UserHandle r2 = r3.mUser
            java.util.function.Predicate r2 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r1, r2)
            android.os.UserHandle r6 = r3.mUser
            r7 = r17
            java.util.function.Predicate r6 = com.android.launcher3.util.ItemInfoMatcher.ofComponents(r7, r6)
            java.util.function.Predicate r2 = r2.or(r6)
            java.util.function.Predicate r6 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r22)
            java.util.function.Predicate r6 = r6.negate()
            java.util.function.Predicate r2 = r2.and(r6)
            java.lang.String r6 = "removed because the corresponding package or component is removed"
            r3.deleteAndBindComponentsRemoved(r2, r6)
            com.android.launcher3.util.MainThreadInitializedObject<com.android.launcher3.model.ItemInstallQueue> r2 = com.android.launcher3.model.ItemInstallQueue.INSTANCE
            java.lang.Object r0 = r2.lambda$get$1$MainThreadInitializedObject(r0)
            com.android.launcher3.model.ItemInstallQueue r0 = (com.android.launcher3.model.ItemInstallQueue) r0
            android.os.UserHandle r2 = r3.mUser
            r0.removeFromInstallQueue(r1, r2)
        L_0x02c5:
            int r0 = r3.mOp
            r1 = 1
            if (r0 != r1) goto L_0x02e4
            r0 = r16
        L_0x02cc:
            if (r0 >= r4) goto L_0x02e1
            com.android.launcher3.model.WidgetsModel r1 = r15.widgetsModel
            com.android.launcher3.util.PackageUserKey r2 = new com.android.launcher3.util.PackageUserKey
            r6 = r5[r0]
            android.os.UserHandle r7 = r3.mUser
            r2.<init>((java.lang.String) r6, (android.os.UserHandle) r7)
            r6 = r28
            r1.update(r6, r2)
            int r0 = r0 + 1
            goto L_0x02cc
        L_0x02e1:
            r3.bindUpdatedWidgets(r15)
        L_0x02e4:
            return
        L_0x02e5:
            r0 = move-exception
            r3 = r27
            goto L_0x02eb
        L_0x02e9:
            r0 = move-exception
            r3 = r14
        L_0x02eb:
            monitor-exit(r29)     // Catch:{ all -> 0x02ed }
            throw r0
        L_0x02ed:
            r0 = move-exception
            goto L_0x02eb
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.PackageUpdatedTask.execute(com.android.launcher3.LauncherAppState, com.android.launcher3.model.BgDataModel, com.android.launcher3.model.AllAppsList):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00fb, code lost:
        if (updateWorkspaceItemIntent(r1, r3, r8) != false) goto L_0x00fd;
     */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0048  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x014e  */
    /* JADX WARNING: Removed duplicated region for block: B:69:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$execute$1$PackageUpdatedTask(java.util.HashSet r16, android.content.Context r17, java.util.function.Predicate r18, com.android.launcher3.util.IntSet r19, boolean r20, com.android.launcher3.util.IntSet r21, java.util.HashSet r22, java.util.HashMap r23, com.android.launcher3.icons.IconCache r24, com.android.launcher3.util.FlagOp r25, java.util.ArrayList r26, com.android.launcher3.model.data.WorkspaceItemInfo r27) {
        /*
            r15 = this;
            r0 = r15
            r1 = r17
            r2 = r21
            r3 = r27
            android.content.Intent$ShortcutIconResource r4 = r3.iconResource
            r5 = 0
            r6 = 1
            if (r4 == 0) goto L_0x002c
            android.content.Intent$ShortcutIconResource r4 = r3.iconResource
            java.lang.String r4 = r4.packageName
            r7 = r16
            boolean r4 = r7.contains(r4)
            if (r4 == 0) goto L_0x002c
            com.android.launcher3.icons.LauncherIcons r4 = com.android.launcher3.icons.LauncherIcons.obtain(r17)
            android.content.Intent$ShortcutIconResource r7 = r3.iconResource
            com.android.launcher3.icons.BitmapInfo r7 = r4.createIconBitmap((android.content.Intent.ShortcutIconResource) r7)
            r4.recycle()
            if (r7 == 0) goto L_0x002c
            r3.bitmap = r7
            r4 = r6
            goto L_0x002d
        L_0x002c:
            r4 = r5
        L_0x002d:
            android.content.ComponentName r7 = r27.getTargetComponent()
            if (r7 == 0) goto L_0x0140
            r8 = r18
            boolean r8 = r8.test(r3)
            if (r8 == 0) goto L_0x0140
            java.lang.String r8 = r7.getPackageName()
            r9 = 8
            boolean r9 = r3.hasStatusFlag(r9)
            r10 = 3
            if (r9 == 0) goto L_0x0054
            int r9 = r3.id
            r11 = r19
            r11.add(r9)
            int r9 = r0.mOp
            if (r9 != r10) goto L_0x0054
            return
        L_0x0054:
            boolean r9 = r27.isPromise()
            r11 = 2
            if (r9 == 0) goto L_0x00ed
            if (r20 == 0) goto L_0x00ed
            java.lang.String r9 = r7.getClassName()
            java.lang.String r12 = "."
            boolean r9 = r9.equals(r12)
            r9 = r9 ^ r6
            int r12 = r3.itemType
            r13 = 6
            if (r12 != r13) goto L_0x009b
            com.android.launcher3.shortcuts.ShortcutRequest r12 = new com.android.launcher3.shortcuts.ShortcutRequest
            android.os.UserHandle r13 = r0.mUser
            r12.<init>(r1, r13)
            java.lang.String r7 = r7.getPackageName()
            java.lang.String[] r13 = new java.lang.String[r6]
            java.lang.String r14 = r27.getDeepShortcutId()
            r13[r5] = r14
            com.android.launcher3.shortcuts.ShortcutRequest r7 = r12.forPackage((java.lang.String) r7, (java.lang.String[]) r13)
            com.android.launcher3.shortcuts.ShortcutRequest$QueryResult r7 = r7.query(r11)
            boolean r12 = r7.isEmpty()
            if (r12 == 0) goto L_0x0090
            r9 = r5
            goto L_0x00ab
        L_0x0090:
            java.lang.Object r4 = r7.get(r5)
            android.content.pm.ShortcutInfo r4 = (android.content.pm.ShortcutInfo) r4
            r3.updateFromDeepShortcutInfo(r4, r1)
            r4 = r6
            goto L_0x00ab
        L_0x009b:
            if (r9 == 0) goto L_0x00ab
            java.lang.Class<android.content.pm.LauncherApps> r9 = android.content.pm.LauncherApps.class
            java.lang.Object r9 = r1.getSystemService(r9)
            android.content.pm.LauncherApps r9 = (android.content.pm.LauncherApps) r9
            android.os.UserHandle r12 = r0.mUser
            boolean r9 = r9.isActivityEnabled(r7, r12)
        L_0x00ab:
            if (r9 != 0) goto L_0x00c6
            boolean r7 = r3.hasStatusFlag(r10)
            if (r7 == 0) goto L_0x00c6
            boolean r1 = r15.updateWorkspaceItemIntent(r1, r3, r8)
            if (r1 == 0) goto L_0x00ba
            goto L_0x00fd
        L_0x00ba:
            boolean r1 = r27.hasPromiseIconUi()
            if (r1 == 0) goto L_0x00fe
            int r1 = r3.id
            r2.add(r1)
            return
        L_0x00c6:
            if (r9 != 0) goto L_0x00ea
            int r1 = r3.id
            r2.add(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Restored shortcut no longer valid "
            java.lang.StringBuilder r1 = r1.append(r2)
            android.content.Intent r2 = r27.getIntent()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "PackageUpdatedTask"
            com.android.launcher3.logging.FileLog.e(r2, r1)
            return
        L_0x00ea:
            r3.status = r5
            goto L_0x00fd
        L_0x00ed:
            if (r20 == 0) goto L_0x00fe
            r2 = r22
            boolean r2 = r2.contains(r7)
            if (r2 == 0) goto L_0x00fe
            boolean r1 = r15.updateWorkspaceItemIntent(r1, r3, r8)
            if (r1 == 0) goto L_0x00fe
        L_0x00fd:
            r4 = r6
        L_0x00fe:
            if (r20 == 0) goto L_0x012f
            r1 = r23
            java.lang.Object r1 = r1.get(r8)
            java.util.List r1 = (java.util.List) r1
            if (r1 == 0) goto L_0x011c
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L_0x0111
            goto L_0x011c
        L_0x0111:
            java.lang.Object r1 = r1.get(r5)
            android.content.pm.LauncherActivityInfo r1 = (android.content.pm.LauncherActivityInfo) r1
            int r1 = com.android.launcher3.util.PackageManagerHelper.getLoadingProgress(r1)
            goto L_0x011e
        L_0x011c:
            r1 = 100
        L_0x011e:
            r3.setProgressLevel(r1, r11)
            int r1 = r3.itemType
            if (r1 != 0) goto L_0x012f
            boolean r1 = r27.usingLowResIcon()
            r2 = r24
            r2.getTitleAndIcon(r3, r1)
            r4 = r6
        L_0x012f:
            int r1 = r3.runtimeStatusFlags
            int r2 = r3.runtimeStatusFlags
            r7 = r25
            int r2 = r7.apply(r2)
            r3.runtimeStatusFlags = r2
            int r2 = r3.runtimeStatusFlags
            if (r2 == r1) goto L_0x0140
            r5 = r6
        L_0x0140:
            if (r4 != 0) goto L_0x0144
            if (r5 == 0) goto L_0x0147
        L_0x0144:
            r26.add(r27)
        L_0x0147:
            if (r4 == 0) goto L_0x0155
            int r1 = r3.id
            r2 = -1
            if (r1 == r2) goto L_0x0155
            com.android.launcher3.model.ModelWriter r1 = r15.getModelWriter()
            r1.updateItemInDatabase(r3)
        L_0x0155:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.PackageUpdatedTask.lambda$execute$1$PackageUpdatedTask(java.util.HashSet, android.content.Context, java.util.function.Predicate, com.android.launcher3.util.IntSet, boolean, com.android.launcher3.util.IntSet, java.util.HashSet, java.util.HashMap, com.android.launcher3.icons.IconCache, com.android.launcher3.util.FlagOp, java.util.ArrayList, com.android.launcher3.model.data.WorkspaceItemInfo):void");
    }

    private boolean updateWorkspaceItemIntent(Context context, WorkspaceItemInfo workspaceItemInfo, String str) {
        Intent appLaunchIntent;
        if (workspaceItemInfo.itemType == 6 || (appLaunchIntent = new PackageManagerHelper(context).getAppLaunchIntent(str, this.mUser)) == null) {
            return false;
        }
        workspaceItemInfo.intent = appLaunchIntent;
        workspaceItemInfo.status = 0;
        return true;
    }
}
