package com.android.launcher3;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.DropTarget;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.util.IntSet;

public class DeleteDropTarget extends ButtonDropTarget {
    private StatsLogManager.LauncherEvent mLauncherEvent;
    private final StatsLogManager mStatsLogManager;

    public int getAccessibilityAction() {
        return R.id.action_remove;
    }

    /* access modifiers changed from: protected */
    public void setupItemInfo(ItemInfo itemInfo) {
    }

    /* access modifiers changed from: protected */
    public boolean supportsDrop(ItemInfo itemInfo) {
        return true;
    }

    public DeleteDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DeleteDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStatsLogManager = StatsLogManager.newInstance(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setDrawable(R.drawable.ic_remove_no_shadow);
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        super.onDragStart(dragObject, dragOptions);
        setTextBasedOnDragSource(dragObject.dragInfo);
        setControlTypeBasedOnDragSource(dragObject.dragInfo);
    }

    public boolean supportsAccessibilityDrop(ItemInfo itemInfo, View view) {
        if (itemInfo instanceof WorkspaceItemInfo) {
            return canRemove(itemInfo);
        }
        return (itemInfo instanceof LauncherAppWidgetInfo) || (itemInfo instanceof FolderInfo);
    }

    private void setTextBasedOnDragSource(ItemInfo itemInfo) {
        if (!TextUtils.isEmpty(this.mText)) {
            this.mText = getResources().getString(canRemove(itemInfo) ? R.string.remove_drop_target_label : 17039360);
            setContentDescription(this.mText);
            requestLayout();
        }
    }

    private boolean canRemove(ItemInfo itemInfo) {
        return itemInfo.id != -1;
    }

    private void setControlTypeBasedOnDragSource(ItemInfo itemInfo) {
        StatsLogManager.LauncherEvent launcherEvent;
        if (itemInfo.id != -1) {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROPPED_ON_REMOVE;
        } else {
            launcherEvent = StatsLogManager.LauncherEvent.LAUNCHER_ITEM_DROPPED_ON_CANCEL;
        }
        this.mLauncherEvent = launcherEvent;
    }

    public void onDrop(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        if (canRemove(dragObject.dragInfo)) {
            this.mLauncher.getModelWriter().prepareToUndoDelete();
            dragObject.dragInfo.container = -1;
        }
        super.onDrop(dragObject, dragOptions);
        this.mStatsLogManager.logger().withInstanceId(dragObject.logInstanceId).log(this.mLauncherEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
        r0 = r5.mLauncher.getWorkspace().getHomescreenIconByItemId(r6.container);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void completeDrop(com.android.launcher3.DropTarget.DragObject r6) {
        /*
            r5 = this;
            com.android.launcher3.model.data.ItemInfo r6 = r6.dragInfo
            boolean r0 = r5.canRemove(r6)
            if (r0 == 0) goto L_0x0061
            int r0 = r6.container
            if (r0 > 0) goto L_0x0021
            com.android.launcher3.Launcher r0 = r5.mLauncher
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            int r1 = r6.container
            android.view.View r0 = r0.getHomescreenIconByItemId(r1)
            if (r0 == 0) goto L_0x0021
            java.lang.Object r0 = r0.getTag()
            com.android.launcher3.model.data.ItemInfo r0 = (com.android.launcher3.model.data.ItemInfo) r0
            goto L_0x0022
        L_0x0021:
            r0 = r6
        L_0x0022:
            int r1 = r0.container
            r2 = -100
            if (r1 != r2) goto L_0x0035
            r1 = 1
            int[] r1 = new int[r1]
            r2 = 0
            int r0 = r0.screenId
            r1[r2] = r0
            com.android.launcher3.util.IntSet r0 = com.android.launcher3.util.IntSet.wrap((int[]) r1)
            goto L_0x003f
        L_0x0035:
            com.android.launcher3.Launcher r0 = r5.mLauncher
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            com.android.launcher3.util.IntSet r0 = r0.getCurrentPageScreenIds()
        L_0x003f:
            r1 = 0
            r5.onAccessibilityDrop(r1, r6)
            com.android.launcher3.Launcher r6 = r5.mLauncher
            com.android.launcher3.model.ModelWriter r6 = r6.getModelWriter()
            com.android.launcher3.-$$Lambda$DeleteDropTarget$ho13jM9KF_Ffc74iITTGR_elCTY r1 = new com.android.launcher3.-$$Lambda$DeleteDropTarget$ho13jM9KF_Ffc74iITTGR_elCTY
            r1.<init>(r0, r6)
            com.android.launcher3.Launcher r0 = r5.mLauncher
            r2 = 2131755198(0x7f1000be, float:1.9141269E38)
            r3 = 2131755370(0x7f10016a, float:1.9141617E38)
            java.util.Objects.requireNonNull(r6)
            com.android.launcher3.-$$Lambda$BAg_e45R3M9tQSdEPNB0D9sEJL4 r4 = new com.android.launcher3.-$$Lambda$BAg_e45R3M9tQSdEPNB0D9sEJL4
            r4.<init>()
            com.android.launcher3.views.Snackbar.show(r0, r2, r3, r4, r1)
        L_0x0061:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.DeleteDropTarget.completeDrop(com.android.launcher3.DropTarget$DragObject):void");
    }

    public /* synthetic */ void lambda$completeDrop$0$DeleteDropTarget(IntSet intSet, ModelWriter modelWriter) {
        this.mLauncher.setPagesToBindSynchronously(intSet);
        modelWriter.abortDelete();
        this.mLauncher.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_UNDO);
    }

    public void onAccessibilityDrop(View view, ItemInfo itemInfo) {
        this.mLauncher.removeItem(view, itemInfo, true, "removed by accessibility drop");
        this.mLauncher.getWorkspace().stripEmptyScreens();
        this.mLauncher.getDragLayer().announceForAccessibility(getContext().getString(R.string.item_removed));
    }
}
