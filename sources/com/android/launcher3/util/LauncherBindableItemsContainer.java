package com.android.launcher3.util;

import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.icons.FastBitmapDrawable;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public interface LauncherBindableItemsContainer {

    public interface ItemOperator {
        boolean evaluate(ItemInfo itemInfo, View view);
    }

    void mapOverItems(ItemOperator itemOperator);

    void updateWorkspaceItems(List<WorkspaceItemInfo> list, ActivityContext activityContext) {
        $$Lambda$LauncherBindableItemsContainer$z1wzKturG9ax5vSI24CnFm82Fxo r2 = new ItemOperator(new HashSet(list)) {
            public final /* synthetic */ HashSet f$0;

            {
                this.f$0 = r1;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return LauncherBindableItemsContainer.lambda$updateWorkspaceItems$0(this.f$0, itemInfo, view);
            }
        };
        mapOverItems(r2);
        Folder open = Folder.getOpen(activityContext);
        if (open != null) {
            open.iterateOverItems(r2);
        }
    }

    static /* synthetic */ boolean lambda$updateWorkspaceItems$0(HashSet hashSet, ItemInfo itemInfo, View view) {
        if ((view instanceof BubbleTextView) && hashSet.contains(itemInfo)) {
            WorkspaceItemInfo workspaceItemInfo = (WorkspaceItemInfo) itemInfo;
            BubbleTextView bubbleTextView = (BubbleTextView) view;
            FastBitmapDrawable icon = bubbleTextView.getIcon();
            boolean z = true;
            if (workspaceItemInfo.isPromise() == ((icon instanceof PreloadIconDrawable) && ((PreloadIconDrawable) icon).hasNotCompleted())) {
                z = false;
            }
            bubbleTextView.applyFromWorkspaceItem(workspaceItemInfo, z);
        } else if ((itemInfo instanceof FolderInfo) && (view instanceof FolderIcon)) {
            Objects.requireNonNull(hashSet);
            ((FolderIcon) view).updatePreviewItems((Predicate<WorkspaceItemInfo>) new Predicate(hashSet) {
                public final /* synthetic */ HashSet f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean test(Object obj) {
                    return this.f$0.contains((WorkspaceItemInfo) obj);
                }
            });
        }
        return false;
    }

    void updateRestoreItems(HashSet<ItemInfo> hashSet, ActivityContext activityContext) {
        $$Lambda$LauncherBindableItemsContainer$HhMiviAmwHt0IBQSsnEnROvxfo4 r0 = new ItemOperator(hashSet) {
            public final /* synthetic */ HashSet f$0;

            {
                this.f$0 = r1;
            }

            public final boolean evaluate(ItemInfo itemInfo, View view) {
                return LauncherBindableItemsContainer.lambda$updateRestoreItems$1(this.f$0, itemInfo, view);
            }
        };
        mapOverItems(r0);
        Folder open = Folder.getOpen(activityContext);
        if (open != null) {
            open.iterateOverItems(r0);
        }
    }

    static /* synthetic */ boolean lambda$updateRestoreItems$1(HashSet hashSet, ItemInfo itemInfo, View view) {
        if ((itemInfo instanceof WorkspaceItemInfo) && (view instanceof BubbleTextView) && hashSet.contains(itemInfo)) {
            ((BubbleTextView) view).applyLoadingState(false);
        } else if ((view instanceof PendingAppWidgetHostView) && (itemInfo instanceof LauncherAppWidgetInfo) && hashSet.contains(itemInfo)) {
            ((PendingAppWidgetHostView) view).applyState();
        } else if ((view instanceof FolderIcon) && (itemInfo instanceof FolderInfo)) {
            Objects.requireNonNull(hashSet);
            ((FolderIcon) view).updatePreviewItems((Predicate<WorkspaceItemInfo>) new Predicate(hashSet) {
                public final /* synthetic */ HashSet f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean test(Object obj) {
                    return this.f$0.contains((WorkspaceItemInfo) obj);
                }
            });
        }
        return false;
    }
}
