package com.android.launcher3.touch;

import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.views.BubbleTextHolder;

public class ItemLongClickListener {
    public static final View.OnLongClickListener INSTANCE_ALL_APPS = $$Lambda$ItemLongClickListener$w0E77iw3NhDMXITrEZo4RYOwnrg.INSTANCE;
    public static final View.OnLongClickListener INSTANCE_WORKSPACE = $$Lambda$ItemLongClickListener$nz9MSaglTImbNXjBQmvpOY7s8M.INSTANCE;

    /* access modifiers changed from: private */
    public static boolean onWorkspaceItemLongClick(View view) {
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "onWorkspaceItemLongClick");
        Launcher launcher = Launcher.getLauncher(view.getContext());
        if (!canStartDrag(launcher)) {
            return false;
        }
        if ((!launcher.isInState(LauncherState.NORMAL) && !launcher.isInState(LauncherState.OVERVIEW)) || !(view.getTag() instanceof ItemInfo)) {
            return false;
        }
        launcher.setWaitingForResult((PendingRequestArgs) null);
        beginDrag(view, launcher, (ItemInfo) view.getTag(), launcher.getDefaultWorkspaceDragOptions());
        return true;
    }

    public static void beginDrag(View view, Launcher launcher, ItemInfo itemInfo, DragOptions dragOptions) {
        Folder open;
        if (itemInfo.container >= 0 && (open = Folder.getOpen(launcher)) != null) {
            if (!open.getIconsInReadingOrder().contains(view)) {
                open.close(true);
            } else {
                open.startDrag(view, dragOptions);
                return;
            }
        }
        launcher.getWorkspace().startDrag(new CellLayout.CellInfo(view, itemInfo), dragOptions);
    }

    /* access modifiers changed from: private */
    public static boolean onAllAppsItemLongClick(final View view) {
        TestLogging.recordEvent(TestProtocol.SEQUENCE_MAIN, "onAllAppsItemLongClick");
        view.cancelLongPress();
        if (view instanceof BubbleTextHolder) {
            view = ((BubbleTextHolder) view).getBubbleText();
        }
        Launcher launcher = Launcher.getLauncher(view.getContext());
        if (!canStartDrag(launcher)) {
            return false;
        }
        if ((!launcher.isInState(LauncherState.ALL_APPS) && !launcher.isInState(LauncherState.OVERVIEW)) || launcher.getWorkspace().isSwitchingState()) {
            return false;
        }
        StatsLogManager.StatsLogger logger = launcher.getStatsLogManager().logger();
        if (view.getTag() instanceof ItemInfo) {
            logger.withItemInfo((ItemInfo) view.getTag());
        }
        logger.log(StatsLogManager.LauncherEvent.LAUNCHER_ALLAPPS_ITEM_LONG_PRESSED);
        final DragController dragController = launcher.getDragController();
        dragController.addDragListener(new DragController.DragListener() {
            public void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions) {
                view.setVisibility(4);
            }

            public void onDragEnd() {
                view.setVisibility(0);
                dragController.removeDragListener(this);
            }
        });
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        DragOptions dragOptions = new DragOptions();
        dragOptions.intrinsicIconScaleFactor = ((float) deviceProfile.allAppsIconSizePx) / ((float) deviceProfile.iconSizePx);
        launcher.getWorkspace().beginDragShared(view, launcher.getAppsView(), dragOptions);
        return false;
    }

    public static boolean canStartDrag(Launcher launcher) {
        if (launcher != null && !launcher.isWorkspaceLocked() && !launcher.getDragController().isDragging()) {
            return true;
        }
        return false;
    }
}
