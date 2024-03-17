package com.android.launcher3.hybridhotseat;

import android.content.Intent;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Hotseat;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Workspace;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.views.Snackbar;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class HotseatEduController {
    public static final String SETTINGS_ACTION = "android.settings.ACTION_CONTENT_SUGGESTIONS_SETTINGS";
    private static final String TAG = "HotseatEduController";
    private HotseatEduDialog mActiveDialog;
    private final Hotseat mHotseat;
    private final Launcher mLauncher;
    private ArrayList<ItemInfo> mNewItems = new ArrayList<>();
    private IntArray mNewScreens = null;
    private List<WorkspaceItemInfo> mPredictedApps;

    HotseatEduController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mHotseat = launcher.getHotseat();
    }

    /* access modifiers changed from: package-private */
    public void migrate() {
        HotseatRestoreHelper.createBackup(this.mLauncher);
        if (FeatureFlags.HOTSEAT_MIGRATE_TO_FOLDER.get()) {
            migrateToFolder();
        } else {
            migrateHotseatWhole();
        }
        Snackbar.show(this.mLauncher, R.string.hotsaet_tip_prediction_enabled, R.string.hotseat_prediction_settings, (Runnable) null, new Runnable() {
            public final void run() {
                HotseatEduController.this.lambda$migrate$0$HotseatEduController();
            }
        });
    }

    public /* synthetic */ void lambda$migrate$0$HotseatEduController() {
        this.mLauncher.startActivity(getSettingsIntent());
    }

    private int migrateToFolder() {
        ArrayDeque arrayDeque = new ArrayDeque();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mLauncher.getDeviceProfile().numShownHotseatIcons; i++) {
            View childAt = this.mHotseat.getChildAt(i, 0);
            if (childAt != null) {
                ItemInfo itemInfo = (ItemInfo) childAt.getTag();
                if (itemInfo.itemType == 2) {
                    arrayDeque.add((FolderInfo) itemInfo);
                } else if ((itemInfo instanceof WorkspaceItemInfo) && itemInfo.container == -101) {
                    arrayList.add((WorkspaceItemInfo) itemInfo);
                }
            }
        }
        if (!arrayList.isEmpty()) {
            ItemInfo itemInfo2 = (ItemInfo) arrayList.get(0);
            FolderInfo folderInfo = new FolderInfo();
            this.mLauncher.getModelWriter().addItemToDatabase(folderInfo, itemInfo2.container, itemInfo2.screenId, itemInfo2.cellX, itemInfo2.cellY);
            folderInfo.setTitle("", this.mLauncher.getModelWriter());
            folderInfo.contents.addAll(arrayList);
            for (int i2 = 0; i2 < folderInfo.contents.size(); i2++) {
                ItemInfo itemInfo3 = folderInfo.contents.get(i2);
                itemInfo3.rank = i2;
                this.mLauncher.getModelWriter().moveItemInDatabase(itemInfo3, folderInfo.id, 0, itemInfo3.cellX, itemInfo3.cellY);
            }
            arrayDeque.add(folderInfo);
        }
        this.mNewItems.addAll(arrayDeque);
        return placeFoldersInWorkspace(arrayDeque);
    }

    private int placeFoldersInWorkspace(ArrayDeque<FolderInfo> arrayDeque) {
        if (arrayDeque.isEmpty()) {
            return 0;
        }
        Workspace<?> workspace = this.mLauncher.getWorkspace();
        InvariantDeviceProfile invariantDeviceProfile = this.mLauncher.getDeviceProfile().inv;
        int childCount = workspace.getChildCount();
        GridOccupancy[] gridOccupancyArr = new GridOccupancy[childCount];
        for (int i = 0; i < childCount; i++) {
            gridOccupancyArr[i] = ((CellLayout) workspace.getChildAt(i)).cloneGridOccupancy();
        }
        int[] iArr = new int[2];
        int i2 = 0;
        while (i2 < childCount && !arrayDeque.isEmpty()) {
            GridOccupancy gridOccupancy = gridOccupancyArr[i2];
            if (gridOccupancy.findVacantCell(iArr, 1, 1)) {
                FolderInfo poll = arrayDeque.poll();
                this.mLauncher.getModelWriter().moveItemInDatabase(poll, -100, workspace.getScreenIdForPageIndex(i2), iArr[0], iArr[1]);
                gridOccupancy.markCells((ItemInfo) poll, true);
            } else {
                i2++;
            }
        }
        if (arrayDeque.isEmpty()) {
            return workspace.getScreenIdForPageIndex(i2);
        }
        int i3 = LauncherSettings.Settings.call(this.mLauncher.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getInt("value");
        int i4 = 0;
        while (true) {
            FolderInfo poll2 = arrayDeque.poll();
            if (poll2 != null) {
                this.mLauncher.getModelWriter().moveItemInDatabase(poll2, -100, i3, i4, invariantDeviceProfile.numRows - 1);
                i4++;
            } else {
                this.mNewScreens = IntArray.wrap(i3);
                return workspace.getPageCount();
            }
        }
    }

    private int migrateHotseatWhole() {
        int i;
        Workspace<?> workspace = this.mLauncher.getWorkspace();
        int i2 = 0;
        while (true) {
            if (i2 >= workspace.getPageCount()) {
                i = 0;
                i2 = -1;
                break;
            } else if (workspace.getScreenWithId(workspace.getScreenIdForPageIndex(i2)).makeSpaceForHotseatMigration(true)) {
                i = this.mLauncher.getDeviceProfile().inv.numRows - 1;
                break;
            } else {
                i2++;
            }
        }
        if (i2 == -1) {
            i2 = LauncherSettings.Settings.call(this.mLauncher.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getInt("value");
            this.mNewScreens = IntArray.wrap(i2);
        }
        boolean z = !this.mLauncher.getDeviceProfile().isVerticalBarLayout();
        int i3 = this.mLauncher.getDeviceProfile().numShownHotseatIcons;
        for (int i4 = 0; i4 < i3; i4++) {
            View childAt = this.mHotseat.getChildAt(z ? i4 : 0, z ? 0 : (i3 - i4) - 1);
            if (!(childAt == null || childAt.getTag() == null)) {
                ItemInfo itemInfo = (ItemInfo) childAt.getTag();
                if (itemInfo.container != -103) {
                    this.mLauncher.getModelWriter().moveItemInDatabase(itemInfo, -100, i2, i4, i);
                    this.mNewItems.add(itemInfo);
                }
            }
        }
        return i2;
    }

    /* access modifiers changed from: package-private */
    public void moveHotseatItems() {
        this.mHotseat.removeAllViewsInLayout();
        if (!this.mNewItems.isEmpty()) {
            ArrayList<ItemInfo> arrayList = this.mNewItems;
            int i = arrayList.get(arrayList.size() - 1).screenId;
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            Iterator<ItemInfo> it = this.mNewItems.iterator();
            while (it.hasNext()) {
                ItemInfo next = it.next();
                if (next.screenId == i) {
                    arrayList2.add(next);
                } else {
                    arrayList3.add(next);
                }
            }
            this.mLauncher.bindAppsAdded(this.mNewScreens, arrayList3, arrayList2);
        }
    }

    /* access modifiers changed from: package-private */
    public void finishOnboarding() {
        this.mLauncher.getModel().onWorkspaceUiChanged();
    }

    /* access modifiers changed from: package-private */
    public void showDimissTip() {
        if (this.mHotseat.getShortcutsAndWidgets().getChildCount() < this.mLauncher.getDeviceProfile().numShownHotseatIcons) {
            Snackbar.show(this.mLauncher, R.string.hotseat_tip_gaps_filled, R.string.hotseat_prediction_settings, (Runnable) null, new Runnable() {
                public final void run() {
                    HotseatEduController.this.lambda$showDimissTip$1$HotseatEduController();
                }
            });
        } else {
            showHotseatArrowTip(true, this.mLauncher.getString(R.string.hotseat_tip_no_empty_slots));
        }
    }

    public /* synthetic */ void lambda$showDimissTip$1$HotseatEduController() {
        this.mLauncher.startActivity(getSettingsIntent());
    }

    /* access modifiers changed from: package-private */
    public void setPredictedApps(List<WorkspaceItemInfo> list) {
        this.mPredictedApps = list;
    }

    /* access modifiers changed from: package-private */
    public void showEdu() {
        int childCount = this.mHotseat.getShortcutsAndWidgets().getChildCount();
        CellLayout screenWithId = this.mLauncher.getWorkspace().getScreenWithId(0);
        boolean anyMatch = IntStream.range(0, childCount).anyMatch(new IntPredicate() {
            public final boolean test(int i) {
                return HotseatEduController.this.lambda$showEdu$2$HotseatEduController(i);
            }
        });
        boolean makeSpaceForHotseatMigration = screenWithId.makeSpaceForHotseatMigration(false);
        if (!anyMatch || !makeSpaceForHotseatMigration) {
            if (showHotseatArrowTip(anyMatch, this.mLauncher.getString(anyMatch ? R.string.hotseat_tip_no_empty_slots : R.string.hotseat_auto_enrolled))) {
                this.mLauncher.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_HOTSEAT_EDU_ONLY_TIP);
            }
            finishOnboarding();
            return;
        }
        showDialog();
    }

    public /* synthetic */ boolean lambda$showEdu$2$HotseatEduController(int i) {
        View childAt = this.mHotseat.getShortcutsAndWidgets().getChildAt(i);
        return (childAt == null || childAt.getTag() == null || ((ItemInfo) childAt.getTag()).container == -103) ? false : true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: android.view.View} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: com.android.launcher3.BubbleTextView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean showHotseatArrowTip(boolean r9, java.lang.String r10) {
        /*
            r8 = this;
            com.android.launcher3.Hotseat r0 = r8.mHotseat
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r0.getShortcutsAndWidgets()
            int r0 = r0.getChildCount()
            com.android.launcher3.Launcher r1 = r8.mLauncher
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            boolean r1 = r1.isVerticalBarLayout()
            r2 = 1
            r1 = r1 ^ r2
            r3 = 0
            int r0 = r0 - r2
        L_0x0018:
            r4 = -1
            r5 = 0
            if (r0 <= r4) goto L_0x0054
            if (r1 == 0) goto L_0x0020
            r4 = r0
            goto L_0x0021
        L_0x0020:
            r4 = r5
        L_0x0021:
            if (r1 == 0) goto L_0x0025
            r6 = r5
            goto L_0x0026
        L_0x0025:
            r6 = r0
        L_0x0026:
            com.android.launcher3.Hotseat r7 = r8.mHotseat
            com.android.launcher3.ShortcutAndWidgetContainer r7 = r7.getShortcutsAndWidgets()
            android.view.View r4 = r7.getChildAt(r4, r6)
            boolean r6 = r4 instanceof com.android.launcher3.BubbleTextView
            if (r6 == 0) goto L_0x0051
            java.lang.Object r6 = r4.getTag()
            boolean r6 = r6 instanceof com.android.launcher3.model.data.WorkspaceItemInfo
            if (r6 == 0) goto L_0x0051
            java.lang.Object r6 = r4.getTag()
            com.android.launcher3.model.data.ItemInfo r6 = (com.android.launcher3.model.data.ItemInfo) r6
            int r6 = r6.container
            r7 = -101(0xffffffffffffff9b, float:NaN)
            if (r6 != r7) goto L_0x004a
            r6 = r2
            goto L_0x004b
        L_0x004a:
            r6 = r5
        L_0x004b:
            if (r6 != r9) goto L_0x0051
            r3 = r4
            com.android.launcher3.BubbleTextView r3 = (com.android.launcher3.BubbleTextView) r3
            goto L_0x0054
        L_0x0051:
            int r0 = r0 + -1
            goto L_0x0018
        L_0x0054:
            if (r3 != 0) goto L_0x005e
            java.lang.String r9 = "HotseatEduController"
            java.lang.String r10 = "Unable to find suitable view for ArrowTip"
            android.util.Log.e(r9, r10)
            return r5
        L_0x005e:
            android.graphics.Rect r9 = com.android.launcher3.Utilities.getViewBounds(r3)
            com.android.launcher3.views.ArrowTipView r0 = new com.android.launcher3.views.ArrowTipView
            com.android.launcher3.Launcher r1 = r8.mLauncher
            r0.<init>(r1)
            r1 = 8388613(0x800005, float:1.175495E-38)
            int r3 = r9.centerX()
            int r9 = r9.top
            r0.show(r10, r1, r3, r9)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.hybridhotseat.HotseatEduController.showHotseatArrowTip(boolean, java.lang.String):boolean");
    }

    /* access modifiers changed from: package-private */
    public void showDialog() {
        List<WorkspaceItemInfo> list = this.mPredictedApps;
        if (list != null && !list.isEmpty()) {
            HotseatEduDialog hotseatEduDialog = this.mActiveDialog;
            if (hotseatEduDialog != null) {
                hotseatEduDialog.handleClose(false);
            }
            HotseatEduDialog dialog = HotseatEduDialog.getDialog(this.mLauncher);
            this.mActiveDialog = dialog;
            dialog.setHotseatEduController(this);
            this.mActiveDialog.show(this.mPredictedApps);
        }
    }

    static Intent getSettingsIntent() {
        return new Intent(SETTINGS_ACTION).addFlags(268435456);
    }
}
