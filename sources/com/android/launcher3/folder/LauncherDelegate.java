package com.android.launcher3.folder;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class LauncherDelegate {
    /* access modifiers changed from: private */
    public final Launcher mLauncher;

    private LauncherDelegate(Launcher launcher) {
        this.mLauncher = launcher;
    }

    /* access modifiers changed from: package-private */
    public void init(Folder folder, FolderIcon folderIcon) {
        folder.setDragController(this.mLauncher.getDragController());
        folderIcon.setOnFocusChangeListener(this.mLauncher.getFocusHandler());
    }

    /* access modifiers changed from: package-private */
    public boolean isDraggingEnabled() {
        return this.mLauncher.isDraggingEnabled();
    }

    /* access modifiers changed from: package-private */
    public void beginDragShared(View view, DragSource dragSource, DragOptions dragOptions) {
        this.mLauncher.getWorkspace().beginDragShared(view, dragSource, dragOptions);
    }

    /* access modifiers changed from: package-private */
    public ModelWriter getModelWriter() {
        return this.mLauncher.getModelWriter();
    }

    /* access modifiers changed from: package-private */
    public void forEachVisibleWorkspacePage(Consumer<View> consumer) {
        this.mLauncher.getWorkspace().forEachVisiblePage(consumer);
    }

    /* access modifiers changed from: package-private */
    public Launcher getLauncher() {
        return this.mLauncher;
    }

    /* access modifiers changed from: package-private */
    public boolean replaceFolderWithFinalItem(final Folder folder) {
        AnonymousClass1 r0 = new Runnable() {
            public void run() {
                WorkspaceItemInfo workspaceItemInfo;
                int itemCount = folder.getItemCount();
                FolderInfo folderInfo = folder.mInfo;
                if (itemCount <= 1) {
                    View view = null;
                    if (itemCount == 1) {
                        CellLayout cellLayout = LauncherDelegate.this.mLauncher.getCellLayout(folderInfo.container, folderInfo.screenId);
                        WorkspaceItemInfo remove = folderInfo.contents.remove(0);
                        View createShortcut = LauncherDelegate.this.mLauncher.createShortcut(cellLayout, remove);
                        LauncherDelegate.this.mLauncher.getModelWriter().addOrMoveItemInDatabase(remove, folderInfo.container, folderInfo.screenId, folderInfo.cellX, folderInfo.cellY);
                        WorkspaceItemInfo workspaceItemInfo2 = remove;
                        view = createShortcut;
                        workspaceItemInfo = workspaceItemInfo2;
                    } else {
                        workspaceItemInfo = null;
                    }
                    LauncherDelegate.this.mLauncher.removeItem(folder.mFolderIcon, folderInfo, true, "folder removed because there's only 1 item in it");
                    if (folder.mFolderIcon instanceof DropTarget) {
                        folder.mDragController.removeDropTarget((DropTarget) folder.mFolderIcon);
                    }
                    if (view != null) {
                        LauncherDelegate.this.mLauncher.getWorkspace().addInScreenFromBind(view, folderInfo);
                        view.requestFocus();
                    }
                    if (workspaceItemInfo != null) {
                        StatsLogManager.StatsLogger withItemInfo = LauncherDelegate.this.mLauncher.getStatsLogManager().logger().withItemInfo(workspaceItemInfo);
                        Optional<InstanceId> logInstanceId = folder.mDragController.getLogInstanceId();
                        Objects.requireNonNull(withItemInfo);
                        ((StatsLogManager.StatsLogger) logInstanceId.map(new Function() {
                            public final Object apply(Object obj) {
                                return StatsLogManager.StatsLogger.this.withInstanceId((InstanceId) obj);
                            }
                        }).orElse(withItemInfo)).log(StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_CONVERTED_TO_ICON);
                    }
                }
            }
        };
        if (folder.mContent.getLastItem() != null) {
            folder.mFolderIcon.performDestroyAnimation(r0);
            return true;
        }
        r0.run();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean interceptOutsideTouch(MotionEvent motionEvent, BaseDragLayer baseDragLayer, Folder folder) {
        if (!this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
            folder.close(true);
            return true;
        } else if (!baseDragLayer.isEventOverView(this.mLauncher.getDropTargetBar(), motionEvent)) {
            return true;
        } else {
            return false;
        }
    }

    private static class FallbackDelegate extends LauncherDelegate {
        private final ActivityContext mContext;
        private ModelWriter mWriter;

        /* access modifiers changed from: package-private */
        public void beginDragShared(View view, DragSource dragSource, DragOptions dragOptions) {
        }

        /* access modifiers changed from: package-private */
        public void forEachVisibleWorkspacePage(Consumer<View> consumer) {
        }

        /* access modifiers changed from: package-private */
        public Launcher getLauncher() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public boolean isDraggingEnabled() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean replaceFolderWithFinalItem(Folder folder) {
            return false;
        }

        FallbackDelegate(ActivityContext activityContext) {
            super((Launcher) null);
            this.mContext = activityContext;
        }

        /* access modifiers changed from: package-private */
        public void init(Folder folder, FolderIcon folderIcon) {
            folder.setDragController(this.mContext.getDragController());
        }

        /* access modifiers changed from: package-private */
        public ModelWriter getModelWriter() {
            if (this.mWriter == null) {
                this.mWriter = LauncherAppState.getInstance((Context) this.mContext).getModel().getWriter(false, false, (BgDataModel.Callbacks) null);
            }
            return this.mWriter;
        }

        /* access modifiers changed from: package-private */
        public boolean interceptOutsideTouch(MotionEvent motionEvent, BaseDragLayer baseDragLayer, Folder folder) {
            folder.close(true);
            return true;
        }
    }

    static LauncherDelegate from(ActivityContext activityContext) {
        if (activityContext instanceof Launcher) {
            return new LauncherDelegate((Launcher) activityContext);
        }
        return new FallbackDelegate(activityContext);
    }
}
