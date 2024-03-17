package com.android.launcher3.model;

import android.util.Pair;
import com.android.launcher3.model.data.ItemInfo;
import java.util.List;

public class AddWorkspaceItemsTask extends BaseModelUpdateTask {
    private static final String LOG = "AddWorkspaceItemsTask";
    private final List<Pair<ItemInfo, Object>> mItemList;
    private final WorkspaceItemSpaceFinder mItemSpaceFinder;

    public AddWorkspaceItemsTask(List<Pair<ItemInfo, Object>> list) {
        this(list, new WorkspaceItemSpaceFinder());
    }

    public AddWorkspaceItemsTask(List<Pair<ItemInfo, Object>> list, WorkspaceItemSpaceFinder workspaceItemSpaceFinder) {
        this.mItemList = list;
        this.mItemSpaceFinder = workspaceItemSpaceFinder;
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0112  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0122  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(com.android.launcher3.LauncherAppState r23, com.android.launcher3.model.BgDataModel r24, com.android.launcher3.model.AllAppsList r25) {
        /*
            r22 = this;
            r1 = r22
            r9 = r24
            java.util.List<android.util.Pair<com.android.launcher3.model.data.ItemInfo, java.lang.Object>> r0 = r1.mItemList
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x000d
            return
        L_0x000d:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            com.android.launcher3.util.IntArray r10 = new com.android.launcher3.util.IntArray
            r10.<init>()
            monitor-enter(r24)
            com.android.launcher3.util.IntArray r11 = r24.collectWorkspaceScreens()     // Catch:{ all -> 0x0215 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x0215 }
            r2.<init>()     // Catch:{ all -> 0x0215 }
            java.util.List<android.util.Pair<com.android.launcher3.model.data.ItemInfo, java.lang.Object>> r3 = r1.mItemList     // Catch:{ all -> 0x0215 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0215 }
        L_0x0027:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0215 }
            r12 = 1
            if (r4 == 0) goto L_0x008a
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0215 }
            android.util.Pair r4 = (android.util.Pair) r4     // Catch:{ all -> 0x0215 }
            java.lang.Object r4 = r4.first     // Catch:{ all -> 0x0215 }
            com.android.launcher3.model.data.ItemInfo r4 = (com.android.launcher3.model.data.ItemInfo) r4     // Catch:{ all -> 0x0215 }
            int r5 = r4.itemType     // Catch:{ all -> 0x0215 }
            if (r5 == 0) goto L_0x0040
            int r5 = r4.itemType     // Catch:{ all -> 0x0215 }
            if (r5 != r12) goto L_0x0072
        L_0x0040:
            android.content.Intent r5 = r4.getIntent()     // Catch:{ all -> 0x0215 }
            android.os.UserHandle r6 = r4.user     // Catch:{ all -> 0x0215 }
            boolean r5 = r1.shortcutExists(r9, r5, r6)     // Catch:{ all -> 0x0215 }
            if (r5 == 0) goto L_0x0058
            boolean r4 = com.android.launcher3.testing.TestProtocol.sDebugTracing     // Catch:{ all -> 0x0215 }
            if (r4 == 0) goto L_0x0027
            java.lang.String r4 = "b/202985412"
            java.lang.String r5 = "AddWorkspaceItemsTask Item already on workspace."
            android.util.Log.d(r4, r5)     // Catch:{ all -> 0x0215 }
            goto L_0x0027
        L_0x0058:
            android.content.Context r5 = r23.getContext()     // Catch:{ all -> 0x0215 }
            android.content.Intent r6 = r4.getIntent()     // Catch:{ all -> 0x0215 }
            boolean r5 = com.android.launcher3.util.PackageManagerHelper.isSystemApp(r5, r6)     // Catch:{ all -> 0x0215 }
            if (r5 == 0) goto L_0x0072
            boolean r4 = com.android.launcher3.testing.TestProtocol.sDebugTracing     // Catch:{ all -> 0x0215 }
            if (r4 == 0) goto L_0x0027
            java.lang.String r4 = "b/202985412"
            java.lang.String r5 = "AddWorkspaceItemsTask Item is a system app."
            android.util.Log.d(r4, r5)     // Catch:{ all -> 0x0215 }
            goto L_0x0027
        L_0x0072:
            int r5 = r4.itemType     // Catch:{ all -> 0x0215 }
            if (r5 != 0) goto L_0x0084
            boolean r5 = r4 instanceof com.android.launcher3.model.data.WorkspaceItemFactory     // Catch:{ all -> 0x0215 }
            if (r5 == 0) goto L_0x0084
            com.android.launcher3.model.data.WorkspaceItemFactory r4 = (com.android.launcher3.model.data.WorkspaceItemFactory) r4     // Catch:{ all -> 0x0215 }
            android.content.Context r5 = r23.getContext()     // Catch:{ all -> 0x0215 }
            com.android.launcher3.model.data.WorkspaceItemInfo r4 = r4.makeWorkspaceItem(r5)     // Catch:{ all -> 0x0215 }
        L_0x0084:
            if (r4 == 0) goto L_0x0027
            r2.add(r4)     // Catch:{ all -> 0x0215 }
            goto L_0x0027
        L_0x008a:
            com.android.launcher3.util.MainThreadInitializedObject<com.android.launcher3.pm.InstallSessionHelper> r3 = com.android.launcher3.pm.InstallSessionHelper.INSTANCE     // Catch:{ all -> 0x0215 }
            android.content.Context r4 = r23.getContext()     // Catch:{ all -> 0x0215 }
            java.lang.Object r3 = r3.lambda$get$1$MainThreadInitializedObject(r4)     // Catch:{ all -> 0x0215 }
            r13 = r3
            com.android.launcher3.pm.InstallSessionHelper r13 = (com.android.launcher3.pm.InstallSessionHelper) r13     // Catch:{ all -> 0x0215 }
            android.content.Context r3 = r23.getContext()     // Catch:{ all -> 0x0215 }
            java.lang.Class<android.content.pm.LauncherApps> r4 = android.content.pm.LauncherApps.class
            java.lang.Object r3 = r3.getSystemService(r4)     // Catch:{ all -> 0x0215 }
            r14 = r3
            android.content.pm.LauncherApps r14 = (android.content.pm.LauncherApps) r14     // Catch:{ all -> 0x0215 }
            java.util.Iterator r15 = r2.iterator()     // Catch:{ all -> 0x0215 }
        L_0x00a8:
            boolean r2 = r15.hasNext()     // Catch:{ all -> 0x0215 }
            if (r2 == 0) goto L_0x0205
            java.lang.Object r2 = r15.next()     // Catch:{ all -> 0x0215 }
            r8 = r2
            com.android.launcher3.model.data.ItemInfo r8 = (com.android.launcher3.model.data.ItemInfo) r8     // Catch:{ all -> 0x0215 }
            com.android.launcher3.model.WorkspaceItemSpaceFinder r2 = r1.mItemSpaceFinder     // Catch:{ all -> 0x0215 }
            int r7 = r8.spanX     // Catch:{ all -> 0x0215 }
            int r6 = r8.spanY     // Catch:{ all -> 0x0215 }
            r3 = r23
            r4 = r24
            r5 = r11
            r16 = r6
            r6 = r10
            r12 = r8
            r8 = r16
            int[] r2 = r2.findSpaceForItem(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0215 }
            r3 = 0
            r19 = r2[r3]     // Catch:{ all -> 0x0215 }
            boolean r4 = r12 instanceof com.android.launcher3.model.data.WorkspaceItemInfo     // Catch:{ all -> 0x0215 }
            if (r4 != 0) goto L_0x00f2
            boolean r4 = r12 instanceof com.android.launcher3.model.data.FolderInfo     // Catch:{ all -> 0x0215 }
            if (r4 != 0) goto L_0x00f2
            boolean r4 = r12 instanceof com.android.launcher3.model.data.LauncherAppWidgetInfo     // Catch:{ all -> 0x0215 }
            if (r4 == 0) goto L_0x00da
            goto L_0x00f2
        L_0x00da:
            boolean r4 = r12 instanceof com.android.launcher3.model.data.WorkspaceItemFactory     // Catch:{ all -> 0x0215 }
            if (r4 == 0) goto L_0x00ea
            r8 = r12
            com.android.launcher3.model.data.WorkspaceItemFactory r8 = (com.android.launcher3.model.data.WorkspaceItemFactory) r8     // Catch:{ all -> 0x0215 }
            android.content.Context r4 = r23.getContext()     // Catch:{ all -> 0x0215 }
            com.android.launcher3.model.data.WorkspaceItemInfo r8 = r8.makeWorkspaceItem(r4)     // Catch:{ all -> 0x0215 }
            goto L_0x00f3
        L_0x00ea:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ all -> 0x0215 }
            java.lang.String r2 = "Unexpected info type"
            r0.<init>(r2)     // Catch:{ all -> 0x0215 }
            throw r0     // Catch:{ all -> 0x0215 }
        L_0x00f2:
            r8 = r12
        L_0x00f3:
            boolean r4 = r12 instanceof com.android.launcher3.model.data.WorkspaceItemInfo     // Catch:{ all -> 0x0215 }
            if (r4 == 0) goto L_0x01d6
            r4 = r12
            com.android.launcher3.model.data.WorkspaceItemInfo r4 = (com.android.launcher3.model.data.WorkspaceItemInfo) r4     // Catch:{ all -> 0x0215 }
            boolean r4 = r4.isPromise()     // Catch:{ all -> 0x0215 }
            if (r4 == 0) goto L_0x01d6
            r4 = r12
            com.android.launcher3.model.data.WorkspaceItemInfo r4 = (com.android.launcher3.model.data.WorkspaceItemInfo) r4     // Catch:{ all -> 0x0215 }
            android.content.ComponentName r5 = r12.getTargetComponent()     // Catch:{ all -> 0x0215 }
            if (r5 == 0) goto L_0x0112
            android.content.ComponentName r5 = r12.getTargetComponent()     // Catch:{ all -> 0x0215 }
            java.lang.String r5 = r5.getPackageName()     // Catch:{ all -> 0x0215 }
            goto L_0x0113
        L_0x0112:
            r5 = 0
        L_0x0113:
            if (r5 != 0) goto L_0x0122
            boolean r2 = com.android.launcher3.testing.TestProtocol.sDebugTracing     // Catch:{ all -> 0x0215 }
            if (r2 == 0) goto L_0x0120
            java.lang.String r2 = "b/202985412"
            java.lang.String r3 = "AddWorkspaceItemsTask Null packageName."
            android.util.Log.d(r2, r3)     // Catch:{ all -> 0x0215 }
        L_0x0120:
            r12 = 1
            goto L_0x00a8
        L_0x0122:
            android.os.UserHandle r6 = r12.user     // Catch:{ all -> 0x0215 }
            android.content.pm.PackageInstaller$SessionInfo r6 = r13.getActiveSessionInfo(r6, r5)     // Catch:{ all -> 0x0215 }
            boolean r7 = r13.verifySessionInfo(r6)     // Catch:{ all -> 0x0215 }
            if (r7 != 0) goto L_0x0152
            java.lang.String r2 = "AddWorkspaceItemsTask"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0215 }
            r3.<init>()     // Catch:{ all -> 0x0215 }
            java.lang.String r5 = "Item info failed session info verification. Skipping : "
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch:{ all -> 0x0215 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ all -> 0x0215 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0215 }
            com.android.launcher3.logging.FileLog.d(r2, r3)     // Catch:{ all -> 0x0215 }
            boolean r2 = com.android.launcher3.testing.TestProtocol.sDebugTracing     // Catch:{ all -> 0x0215 }
            if (r2 == 0) goto L_0x0120
            java.lang.String r2 = "b/202985412"
            java.lang.String r3 = "AddWorkspaceItemsTaskFailed verification."
            android.util.Log.d(r2, r3)     // Catch:{ all -> 0x0215 }
            goto L_0x0120
        L_0x0152:
            android.os.UserHandle r7 = r12.user     // Catch:{ all -> 0x0215 }
            java.util.List r5 = r14.getActivityList(r5, r7)     // Catch:{ all -> 0x0215 }
            if (r5 == 0) goto L_0x0162
            boolean r7 = r5.isEmpty()     // Catch:{ all -> 0x0215 }
            if (r7 != 0) goto L_0x0162
            r7 = 1
            goto L_0x0163
        L_0x0162:
            r7 = r3
        L_0x0163:
            if (r6 != 0) goto L_0x0173
            if (r7 != 0) goto L_0x0180
            boolean r2 = com.android.launcher3.testing.TestProtocol.sDebugTracing     // Catch:{ all -> 0x0215 }
            if (r2 == 0) goto L_0x0120
            java.lang.String r2 = "b/202985412"
            java.lang.String r3 = "AddWorkspaceItemsTaskSession cancelled"
            android.util.Log.d(r2, r3)     // Catch:{ all -> 0x0215 }
            goto L_0x0120
        L_0x0173:
            float r6 = r6.getProgress()     // Catch:{ all -> 0x0215 }
            r16 = 1120403456(0x42c80000, float:100.0)
            float r6 = r6 * r16
            int r6 = (int) r6     // Catch:{ all -> 0x0215 }
            r3 = 1
            r4.setProgressLevel(r6, r3)     // Catch:{ all -> 0x0215 }
        L_0x0180:
            if (r7 == 0) goto L_0x01d6
            com.android.launcher3.model.data.AppInfo r3 = new com.android.launcher3.model.data.AppInfo     // Catch:{ all -> 0x0215 }
            android.content.Context r4 = r23.getContext()     // Catch:{ all -> 0x0215 }
            r6 = 0
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x0215 }
            android.content.pm.LauncherActivityInfo r5 = (android.content.pm.LauncherActivityInfo) r5     // Catch:{ all -> 0x0215 }
            android.os.UserHandle r6 = r12.user     // Catch:{ all -> 0x0215 }
            r3.<init>((android.content.Context) r4, (android.content.pm.LauncherActivityInfo) r5, (android.os.UserHandle) r6)     // Catch:{ all -> 0x0215 }
            android.content.Context r4 = r23.getContext()     // Catch:{ all -> 0x0215 }
            com.android.launcher3.model.data.WorkspaceItemInfo r8 = r3.makeWorkspaceItem(r4)     // Catch:{ all -> 0x0215 }
            android.content.Intent r3 = r8.getIntent()     // Catch:{ all -> 0x0215 }
            android.os.UserHandle r4 = r8.user     // Catch:{ all -> 0x0215 }
            boolean r3 = r1.shortcutExists(r9, r3, r4)     // Catch:{ all -> 0x0215 }
            if (r3 == 0) goto L_0x01b5
            boolean r2 = com.android.launcher3.testing.TestProtocol.sDebugTracing     // Catch:{ all -> 0x0215 }
            if (r2 == 0) goto L_0x0120
            java.lang.String r2 = "b/202985412"
            java.lang.String r3 = "AddWorkspaceItemsTaskshortcutExists"
            android.util.Log.d(r2, r3)     // Catch:{ all -> 0x0215 }
            goto L_0x0120
        L_0x01b5:
            r3 = r8
            com.android.launcher3.model.data.WorkspaceItemInfo r3 = (com.android.launcher3.model.data.WorkspaceItemInfo) r3     // Catch:{ all -> 0x0215 }
            java.lang.String r4 = ""
            r3.title = r4     // Catch:{ all -> 0x0215 }
            com.android.launcher3.icons.IconCache r4 = r23.getIconCache()     // Catch:{ all -> 0x0215 }
            android.os.UserHandle r5 = r12.user     // Catch:{ all -> 0x0215 }
            com.android.launcher3.icons.BitmapInfo r4 = r4.getDefaultIcon(r5)     // Catch:{ all -> 0x0215 }
            r3.bitmap = r4     // Catch:{ all -> 0x0215 }
            com.android.launcher3.icons.IconCache r4 = r23.getIconCache()     // Catch:{ all -> 0x0215 }
            r5 = r8
            com.android.launcher3.model.data.WorkspaceItemInfo r5 = (com.android.launcher3.model.data.WorkspaceItemInfo) r5     // Catch:{ all -> 0x0215 }
            boolean r5 = r5.usingLowResIcon()     // Catch:{ all -> 0x0215 }
            r4.getTitleAndIcon(r3, r5)     // Catch:{ all -> 0x0215 }
        L_0x01d6:
            com.android.launcher3.model.ModelWriter r16 = r22.getModelWriter()     // Catch:{ all -> 0x0215 }
            r18 = -100
            r3 = 1
            r20 = r2[r3]     // Catch:{ all -> 0x0215 }
            r4 = 2
            r21 = r2[r4]     // Catch:{ all -> 0x0215 }
            r17 = r8
            r16.addItemToDatabase(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x0215 }
            r0.add(r8)     // Catch:{ all -> 0x0215 }
            java.lang.String r2 = "AddWorkspaceItemsTask"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0215 }
            r4.<init>()     // Catch:{ all -> 0x0215 }
            java.lang.String r5 = "Adding item info to workspace: "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch:{ all -> 0x0215 }
            java.lang.StringBuilder r4 = r4.append(r8)     // Catch:{ all -> 0x0215 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0215 }
            com.android.launcher3.logging.FileLog.d(r2, r4)     // Catch:{ all -> 0x0215 }
            r12 = r3
            goto L_0x00a8
        L_0x0205:
            monitor-exit(r24)     // Catch:{ all -> 0x0215 }
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0214
            com.android.launcher3.model.AddWorkspaceItemsTask$1 r2 = new com.android.launcher3.model.AddWorkspaceItemsTask$1
            r2.<init>(r0, r10)
            r1.scheduleCallbackTask(r2)
        L_0x0214:
            return
        L_0x0215:
            r0 = move-exception
            monitor-exit(r24)     // Catch:{ all -> 0x0215 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AddWorkspaceItemsTask.execute(com.android.launcher3.LauncherAppState, com.android.launcher3.model.BgDataModel, com.android.launcher3.model.AllAppsList):void");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00be, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shortcutExists(com.android.launcher3.model.BgDataModel r12, android.content.Intent r13, android.os.UserHandle r14) {
        /*
            r11 = this;
            r0 = 1
            if (r13 != 0) goto L_0x0004
            return r0
        L_0x0004:
            android.content.ComponentName r1 = r13.getComponent()
            r2 = 0
            r3 = 0
            if (r1 == 0) goto L_0x003e
            android.content.ComponentName r1 = r13.getComponent()
            java.lang.String r1 = r1.getPackageName()
            java.lang.String r4 = r13.getPackage()
            if (r4 == 0) goto L_0x002c
            java.lang.String r4 = r13.toUri(r3)
            android.content.Intent r5 = new android.content.Intent
            r5.<init>(r13)
            android.content.Intent r2 = r5.setPackage(r2)
            java.lang.String r2 = r2.toUri(r3)
            goto L_0x0049
        L_0x002c:
            android.content.Intent r2 = new android.content.Intent
            r2.<init>(r13)
            android.content.Intent r2 = r2.setPackage(r1)
            java.lang.String r4 = r2.toUri(r3)
            java.lang.String r2 = r13.toUri(r3)
            goto L_0x0049
        L_0x003e:
            java.lang.String r4 = r13.toUri(r3)
            java.lang.String r1 = r13.toUri(r3)
            r10 = r2
            r2 = r1
            r1 = r10
        L_0x0049:
            boolean r5 = com.android.launcher3.util.PackageManagerHelper.isLauncherAppTarget(r13)
            monitor-enter(r12)
            com.android.launcher3.util.IntSparseArrayMap<com.android.launcher3.model.data.ItemInfo> r6 = r12.itemsIdMap     // Catch:{ all -> 0x00c1 }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x00c1 }
        L_0x0054:
            boolean r7 = r6.hasNext()     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x00bf
            java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x00c1 }
            com.android.launcher3.model.data.ItemInfo r7 = (com.android.launcher3.model.data.ItemInfo) r7     // Catch:{ all -> 0x00c1 }
            boolean r8 = r7 instanceof com.android.launcher3.model.data.WorkspaceItemInfo     // Catch:{ all -> 0x00c1 }
            if (r8 == 0) goto L_0x0054
            r8 = r7
            com.android.launcher3.model.data.WorkspaceItemInfo r8 = (com.android.launcher3.model.data.WorkspaceItemInfo) r8     // Catch:{ all -> 0x00c1 }
            android.content.Intent r9 = r7.getIntent()     // Catch:{ all -> 0x00c1 }
            if (r9 == 0) goto L_0x0054
            android.os.UserHandle r9 = r8.user     // Catch:{ all -> 0x00c1 }
            boolean r9 = r9.equals(r14)     // Catch:{ all -> 0x00c1 }
            if (r9 == 0) goto L_0x0054
            android.content.Intent r9 = new android.content.Intent     // Catch:{ all -> 0x00c1 }
            android.content.Intent r7 = r7.getIntent()     // Catch:{ all -> 0x00c1 }
            r9.<init>(r7)     // Catch:{ all -> 0x00c1 }
            android.graphics.Rect r7 = r13.getSourceBounds()     // Catch:{ all -> 0x00c1 }
            r9.setSourceBounds(r7)     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r9.toUri(r3)     // Catch:{ all -> 0x00c1 }
            boolean r9 = r4.equals(r7)     // Catch:{ all -> 0x00c1 }
            if (r9 != 0) goto L_0x00bd
            boolean r7 = r2.equals(r7)     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0096
            goto L_0x00bd
        L_0x0096:
            if (r5 == 0) goto L_0x0054
            boolean r7 = r8.isPromise()     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            r7 = 2
            boolean r7 = r8.hasStatusFlag(r7)     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            android.content.ComponentName r7 = r8.getTargetComponent()     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            if (r1 == 0) goto L_0x0054
            android.content.ComponentName r7 = r8.getTargetComponent()     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r7.getPackageName()     // Catch:{ all -> 0x00c1 }
            boolean r7 = r1.equals(r7)     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            return r0
        L_0x00bd:
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            return r0
        L_0x00bf:
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            return r3
        L_0x00c1:
            r13 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AddWorkspaceItemsTask.shortcutExists(com.android.launcher3.model.BgDataModel, android.content.Intent, android.os.UserHandle):boolean");
    }
}
