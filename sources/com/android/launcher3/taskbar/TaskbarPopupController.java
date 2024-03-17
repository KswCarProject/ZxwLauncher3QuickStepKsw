package com.android.launcher3.taskbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.dot.FolderDotInfo;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.popup.PopupLiveUpdateHandler;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.taskbar.TaskbarControllers;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LauncherBindableItemsContainer;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.views.ActivityContext;
import com.android.quickstep.SystemUiProxy;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskbarPopupController implements TaskbarControllers.LoggableTaskbarController {
    private static final SystemShortcut.Factory<BaseTaskbarContext> APP_INFO = $$Lambda$UMmm9ybUCGlyBXYz3N8wJIh0Mw.INSTANCE;
    /* access modifiers changed from: private */
    public final TaskbarActivityContext mContext;
    private TaskbarControllers mControllers;
    private final PopupDataProvider mPopupDataProvider = new PopupDataProvider(new Consumer() {
        public final void accept(Object obj) {
            TaskbarPopupController.this.updateNotificationDots((Predicate) obj);
        }
    });

    public TaskbarPopupController(TaskbarActivityContext taskbarActivityContext) {
        this.mContext = taskbarActivityContext;
    }

    public void init(TaskbarControllers taskbarControllers) {
        this.mControllers = taskbarControllers;
        NotificationListener.addNotificationsChangedListener(this.mPopupDataProvider);
    }

    public void onDestroy() {
        NotificationListener.removeNotificationsChangedListener(this.mPopupDataProvider);
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mPopupDataProvider;
    }

    public void setDeepShortcutMap(HashMap<ComponentKey, Integer> hashMap) {
        this.mPopupDataProvider.setDeepShortcutMap(hashMap);
    }

    /* access modifiers changed from: private */
    public void updateNotificationDots(Predicate<PackageUserKey> predicate) {
        $$Lambda$TaskbarPopupController$xN657bYWCg9ZtlGSUwwDsmsp1No r3 = new LauncherBindableItemsContainer.ItemOperator(new Predicate(predicate) {
            public final /* synthetic */ Predicate f$1;

            {
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return TaskbarPopupController.lambda$updateNotificationDots$0(PackageUserKey.this, this.f$1, (ItemInfo) obj);
            }
        }) {
            public final /* synthetic */ Predicate f$1;

            {
                this.f$1 = r2;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return TaskbarPopupController.this.lambda$updateNotificationDots$1$TaskbarPopupController(this.f$1, itemInfo, view);
            }
        };
        this.mControllers.taskbarViewController.mapOverItems(r3);
        Folder open = Folder.getOpen(this.mContext);
        if (open != null) {
            open.iterateOverItems(r3);
        }
    }

    static /* synthetic */ boolean lambda$updateNotificationDots$0(PackageUserKey packageUserKey, Predicate predicate, ItemInfo itemInfo) {
        return !packageUserKey.updateFromItemInfo(itemInfo) || predicate.test(packageUserKey);
    }

    public /* synthetic */ boolean lambda$updateNotificationDots$1$TaskbarPopupController(Predicate predicate, ItemInfo itemInfo, View view) {
        if (!(itemInfo instanceof WorkspaceItemInfo) || !(view instanceof BubbleTextView)) {
            if (!(itemInfo instanceof FolderInfo) || !(view instanceof FolderIcon)) {
                return false;
            }
            FolderInfo folderInfo = (FolderInfo) itemInfo;
            if (!folderInfo.contents.stream().anyMatch(predicate)) {
                return false;
            }
            FolderDotInfo folderDotInfo = new FolderDotInfo();
            Iterator<WorkspaceItemInfo> it = folderInfo.contents.iterator();
            while (it.hasNext()) {
                folderDotInfo.addDotInfo(this.mPopupDataProvider.getDotInfoForItem(it.next()));
            }
            ((FolderIcon) view).setDotInfo(folderDotInfo);
            return false;
        } else if (!predicate.test(itemInfo)) {
            return false;
        } else {
            ((BubbleTextView) view).applyDotState(itemInfo, true);
            return false;
        }
    }

    public PopupContainerWithArrow<BaseTaskbarContext> showForIcon(BubbleTextView bubbleTextView) {
        BaseTaskbarContext baseTaskbarContext = (BaseTaskbarContext) ActivityContext.lookupContext(bubbleTextView.getContext());
        if (PopupContainerWithArrow.getOpen(baseTaskbarContext) != null) {
            bubbleTextView.clearFocus();
            return null;
        }
        ItemInfo itemInfo = (ItemInfo) bubbleTextView.getTag();
        if (!ShortcutUtil.supportsShortcuts(itemInfo)) {
            return null;
        }
        PopupContainerWithArrow<BaseTaskbarContext> popupContainerWithArrow = (PopupContainerWithArrow) baseTaskbarContext.getLayoutInflater().inflate(R.layout.popup_container, baseTaskbarContext.getDragLayer(), false);
        popupContainerWithArrow.addOnAttachStateChangeListener(new PopupLiveUpdateHandler<BaseTaskbarContext>(baseTaskbarContext, popupContainerWithArrow) {
            /* access modifiers changed from: protected */
            public void showPopupContainerForIcon(BubbleTextView bubbleTextView) {
                TaskbarPopupController.this.showForIcon(bubbleTextView);
            }
        });
        popupContainerWithArrow.setPopupItemDragHandler(new TaskbarPopupItemDragHandler());
        this.mControllers.taskbarDragController.addDragListener(popupContainerWithArrow);
        popupContainerWithArrow.populateAndShow(bubbleTextView, this.mPopupDataProvider.getShortcutCountForItem(itemInfo), this.mPopupDataProvider.getNotificationKeysForItem(itemInfo), (List) getSystemShortcuts().map(new Function(itemInfo, bubbleTextView) {
            public final /* synthetic */ ItemInfo f$1;
            public final /* synthetic */ BubbleTextView f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final Object apply(Object obj) {
                return ((SystemShortcut.Factory) obj).getShortcut(BaseTaskbarContext.this, this.f$1, this.f$2);
            }
        }).filter($$Lambda$TaskbarPopupController$5DTpxP45pqMGaPreGfSqTyTDkt0.INSTANCE).collect(Collectors.toList()));
        popupContainerWithArrow.requestFocus();
        baseTaskbarContext.onPopupVisibilityChanged(true);
        popupContainerWithArrow.setOnCloseCallback(new Runnable(popupContainerWithArrow) {
            public final /* synthetic */ PopupContainerWithArrow f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TaskbarPopupController.lambda$showForIcon$4(BaseTaskbarContext.this, this.f$1);
            }
        });
        return popupContainerWithArrow;
    }

    static /* synthetic */ void lambda$showForIcon$4(BaseTaskbarContext baseTaskbarContext, PopupContainerWithArrow popupContainerWithArrow) {
        baseTaskbarContext.getDragLayer().post(new Runnable() {
            public final void run() {
                BaseTaskbarContext.this.onPopupVisibilityChanged(false);
            }
        });
        popupContainerWithArrow.setOnCloseCallback((Runnable) null);
    }

    private Stream<SystemShortcut.Factory> getSystemShortcuts() {
        return Stream.concat(Utilities.getSplitPositionOptions(this.mContext.getDeviceProfile()).stream().map(new Function() {
            public final Object apply(Object obj) {
                return TaskbarPopupController.this.createSplitShortcutFactory((SplitConfigurationOptions.SplitPositionOption) obj);
            }
        }), Stream.of(APP_INFO));
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "TaskbarPopupController:");
        this.mPopupDataProvider.dump(str + "\t", printWriter);
    }

    private class TaskbarPopupItemDragHandler implements PopupContainerWithArrow.PopupItemDragHandler {
        protected final Point mIconLastTouchPos = new Point();

        TaskbarPopupItemDragHandler() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action != 0 && action != 2) {
                return false;
            }
            this.mIconLastTouchPos.set((int) motionEvent.getX(), (int) motionEvent.getY());
            return false;
        }

        public boolean onLongClick(View view) {
            if (!(view.getParent() instanceof DeepShortcutView)) {
                return false;
            }
            DeepShortcutView deepShortcutView = (DeepShortcutView) view.getParent();
            deepShortcutView.setWillDrawIcon(false);
            Point point = new Point();
            point.x = this.mIconLastTouchPos.x - deepShortcutView.getIconCenter().x;
            point.y = this.mIconLastTouchPos.y - TaskbarPopupController.this.mContext.getDeviceProfile().iconSizePx;
            ((TaskbarDragController) ((ActivityContext) ActivityContext.lookupContext(view.getContext())).getDragController()).startDragOnLongClick(deepShortcutView, point);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public SystemShortcut.Factory<BaseTaskbarContext> createSplitShortcutFactory(SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
        return new SystemShortcut.Factory() {
            public final SystemShortcut getShortcut(Context context, ItemInfo itemInfo, View view) {
                return TaskbarPopupController.lambda$createSplitShortcutFactory$5(SplitConfigurationOptions.SplitPositionOption.this, (BaseTaskbarContext) context, itemInfo, view);
            }
        };
    }

    static /* synthetic */ SystemShortcut lambda$createSplitShortcutFactory$5(SplitConfigurationOptions.SplitPositionOption splitPositionOption, BaseTaskbarContext baseTaskbarContext, ItemInfo itemInfo, View view) {
        return new TaskbarSplitShortcut(baseTaskbarContext, itemInfo, view, splitPositionOption);
    }

    private static class TaskbarSplitShortcut extends SystemShortcut<BaseTaskbarContext> {
        private final SplitConfigurationOptions.SplitPositionOption mPosition;

        TaskbarSplitShortcut(BaseTaskbarContext baseTaskbarContext, ItemInfo itemInfo, View view, SplitConfigurationOptions.SplitPositionOption splitPositionOption) {
            super(splitPositionOption.iconResId, splitPositionOption.textResId, baseTaskbarContext, itemInfo, view);
            this.mPosition = splitPositionOption;
        }

        public void onClick(View view) {
            AbstractFloatingView.closeAllOpenViews((ActivityContext) this.mTarget);
            if (this.mItemInfo.itemType == 6) {
                WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) this.mItemInfo;
                SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mTarget).startShortcut(workspaceItemInfo.getIntent().getPackage(), workspaceItemInfo.getDeepShortcutId(), this.mPosition.stagePosition, (Bundle) null, workspaceItemInfo.user);
                return;
            }
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mTarget).startIntent(((LauncherApps) ((BaseTaskbarContext) this.mTarget).getSystemService(LauncherApps.class)).getMainActivityLaunchIntent(this.mItemInfo.getIntent().getComponent(), (Bundle) null, this.mItemInfo.user), new Intent(), this.mPosition.stagePosition, (Bundle) null);
        }
    }
}
