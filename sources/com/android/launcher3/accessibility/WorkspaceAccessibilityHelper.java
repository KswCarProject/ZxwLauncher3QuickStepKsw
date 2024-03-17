package com.android.launcher3.accessibility;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.BaseAccessibilityDelegate;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import java.util.Iterator;

public class WorkspaceAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    public WorkspaceAccessibilityHelper(CellLayout cellLayout) {
        super(cellLayout);
    }

    /* access modifiers changed from: protected */
    public int intersectsValidDropTarget(int i) {
        int countX = this.mView.getCountX();
        int countY = this.mView.getCountY();
        int i2 = i % countX;
        int i3 = i / countX;
        BaseAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        if (dragInfo.dragType == BaseAccessibilityDelegate.DragType.WIDGET && !this.mView.acceptsWidget()) {
            return -1;
        }
        if (dragInfo.dragType == BaseAccessibilityDelegate.DragType.WIDGET) {
            int i4 = dragInfo.info.spanX;
            int i5 = dragInfo.info.spanY;
            for (int i6 = 0; i6 < i4; i6++) {
                for (int i7 = 0; i7 < i5; i7++) {
                    int i8 = i2 - i6;
                    int i9 = i3 - i7;
                    if (i8 >= 0 && i9 >= 0) {
                        boolean z = true;
                        for (int i10 = i8; i10 < i8 + i4 && z; i10++) {
                            int i11 = i9;
                            while (true) {
                                if (i11 >= i9 + i5) {
                                    break;
                                } else if (i10 >= countX || i11 >= countY || this.mView.isOccupied(i10, i11)) {
                                    z = false;
                                } else {
                                    i11++;
                                }
                            }
                            z = false;
                        }
                        if (z) {
                            return i8 + (countX * i9);
                        }
                    }
                }
            }
            return -1;
        }
        View childAt = this.mView.getChildAt(i2, i3);
        if (childAt == null || childAt == dragInfo.item) {
            return i;
        }
        if (dragInfo.dragType != BaseAccessibilityDelegate.DragType.FOLDER) {
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if ((itemInfo instanceof AppInfo) || (itemInfo instanceof FolderInfo) || (itemInfo instanceof WorkspaceItemInfo)) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public String getConfirmationForIconDrop(int i) {
        int countX = i % this.mView.getCountX();
        int countX2 = i / this.mView.getCountX();
        BaseAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        View childAt = this.mView.getChildAt(countX, countX2);
        if (childAt == null || childAt == dragInfo.item) {
            return this.mContext.getString(R.string.item_moved);
        }
        ItemInfo itemInfo = (ItemInfo) childAt.getTag();
        if ((itemInfo instanceof AppInfo) || (itemInfo instanceof WorkspaceItemInfo)) {
            return this.mContext.getString(R.string.folder_created);
        }
        return itemInfo instanceof FolderInfo ? this.mContext.getString(R.string.added_to_folder) : "";
    }

    /* access modifiers changed from: protected */
    public String getLocationDescriptionForIconDrop(int i) {
        int countX = i % this.mView.getCountX();
        int countX2 = i / this.mView.getCountX();
        BaseAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        View childAt = this.mView.getChildAt(countX, countX2);
        if (childAt == null || childAt == dragInfo.item) {
            return this.mView.getItemMoveDescription(countX, countX2);
        }
        return getDescriptionForDropOver(childAt, this.mContext);
    }

    public static String getDescriptionForDropOver(View view, Context context) {
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (itemInfo instanceof WorkspaceItemInfo) {
            return context.getString(R.string.create_folder_with, new Object[]{itemInfo.title});
        } else if (!(itemInfo instanceof FolderInfo)) {
            return "";
        } else {
            if (TextUtils.isEmpty(itemInfo.title)) {
                WorkspaceItemInfo workspaceItemInfo = null;
                Iterator<WorkspaceItemInfo> it = ((FolderInfo) itemInfo).contents.iterator();
                while (it.hasNext()) {
                    WorkspaceItemInfo next = it.next();
                    if (workspaceItemInfo == null || workspaceItemInfo.rank > next.rank) {
                        workspaceItemInfo = next;
                    }
                }
                if (workspaceItemInfo != null) {
                    return context.getString(R.string.add_to_folder_with_app, new Object[]{workspaceItemInfo.title});
                }
            }
            return context.getString(R.string.add_to_folder, new Object[]{itemInfo.title});
        }
    }
}
