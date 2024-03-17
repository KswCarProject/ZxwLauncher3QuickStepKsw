package com.android.launcher3;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.IntSet;

public interface WorkspaceLayoutManager {
    public static final int EXTRA_EMPTY_SCREEN_ID = -201;
    public static final IntSet EXTRA_EMPTY_SCREEN_IDS = IntSet.wrap(EXTRA_EMPTY_SCREEN_ID, -200);
    public static final int EXTRA_EMPTY_SCREEN_SECOND_ID = -200;
    public static final int FIRST_SCREEN_ID = 0;
    public static final int SECOND_SCREEN_ID = 1;
    public static final String TAG = "Launcher.Workspace";

    Hotseat getHotseat();

    CellLayout getScreenWithId(int i);

    void onAddDropTarget(DropTarget dropTarget) {
    }

    void addInScreenFromBind(View view, ItemInfo itemInfo) {
        int i;
        int i2;
        int i3 = itemInfo.cellX;
        int i4 = itemInfo.cellY;
        if (itemInfo.container == -101 || itemInfo.container == -103) {
            int i5 = itemInfo.screenId;
            int cellXFromOrder = getHotseat().getCellXFromOrder(i5);
            i = getHotseat().getCellYFromOrder(i5);
            i2 = cellXFromOrder;
        } else {
            i2 = i3;
            i = i4;
        }
        addInScreen(view, itemInfo.container, itemInfo.screenId, i2, i, itemInfo.spanX, itemInfo.spanY);
    }

    void addInScreen(View view, ItemInfo itemInfo) {
        addInScreen(view, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY);
    }

    void addInScreen(View view, int i, int i2, int i3, int i4, int i5, int i6) {
        CellLayout cellLayout;
        CellLayout.LayoutParams layoutParams;
        View view2 = view;
        int i7 = i;
        int i8 = i2;
        int i9 = i3;
        int i10 = i4;
        int i11 = i5;
        int i12 = i6;
        if (i7 == -100 && getScreenWithId(i2) == null) {
            Log.e(TAG, "Skipping child, screenId " + i2 + " not found");
            new Throwable().printStackTrace();
        } else if (!EXTRA_EMPTY_SCREEN_IDS.contains(i2)) {
            if (i7 == -101 || i7 == -103) {
                cellLayout = getHotseat();
                if (view2 instanceof FolderIcon) {
                    ((FolderIcon) view2).setTextVisible(false);
                }
            } else {
                if (view2 instanceof FolderIcon) {
                    ((FolderIcon) view2).setTextVisible(true);
                }
                cellLayout = getScreenWithId(i2);
            }
            ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
            if (layoutParams2 == null || !(layoutParams2 instanceof CellLayout.LayoutParams)) {
                layoutParams = new CellLayout.LayoutParams(i9, i10, i11, i12);
            } else {
                layoutParams = (CellLayout.LayoutParams) layoutParams2;
                layoutParams.cellX = i9;
                layoutParams.cellY = i10;
                layoutParams.cellHSpan = i11;
                layoutParams.cellVSpan = i12;
            }
            if (i11 < 0 && i12 < 0) {
                layoutParams.isLockedToGrid = false;
            }
            if (!cellLayout.addViewToCellLayout(view, -1, ((ItemInfo) view.getTag()).getViewId(), layoutParams, !(view2 instanceof Folder))) {
                Log.e(TAG, "Failed to add to item at (" + layoutParams.cellX + "," + layoutParams.cellY + ") to CellLayout");
            }
            view.setHapticFeedbackEnabled(false);
            view.setOnLongClickListener(getWorkspaceChildOnLongClickListener());
            if (view2 instanceof DropTarget) {
                onAddDropTarget((DropTarget) view2);
            }
        } else {
            throw new RuntimeException("Screen id should not be extra empty screen: " + i2);
        }
    }

    View.OnLongClickListener getWorkspaceChildOnLongClickListener() {
        return ItemLongClickListener.INSTANCE_WORKSPACE;
    }
}
