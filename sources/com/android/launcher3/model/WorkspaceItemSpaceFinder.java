package com.android.launcher3.model;

import android.util.LongSparseArray;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.IntArray;
import com.android.launcher3.util.IntSet;
import java.util.ArrayList;
import java.util.Iterator;

public class WorkspaceItemSpaceFinder {
    public int[] findSpaceForItem(LauncherAppState launcherAppState, BgDataModel bgDataModel, IntArray intArray, IntArray intArray2, int i, int i2) {
        boolean z;
        BgDataModel bgDataModel2 = bgDataModel;
        IntArray intArray3 = intArray;
        LongSparseArray longSparseArray = new LongSparseArray();
        synchronized (bgDataModel) {
            Iterator<ItemInfo> it = bgDataModel2.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo next = it.next();
                if (next.container == -100) {
                    ArrayList arrayList = (ArrayList) longSparseArray.get((long) next.screenId);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        longSparseArray.put((long) next.screenId, arrayList);
                    }
                    arrayList.add(next);
                }
            }
        }
        int[] iArr = new int[2];
        int size = intArray.size();
        IntSet intSet = new IntSet();
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if (i4 >= size) {
                z = false;
                break;
            }
            int i5 = intArray3.get(i4);
            if (!intSet.contains(i5)) {
                if (findNextAvailableIconSpaceInScreen(launcherAppState, (ArrayList) longSparseArray.get((long) i5), iArr, i, i2)) {
                    z = true;
                    i3 = i5;
                    break;
                }
            }
            i4++;
            i3 = i5;
        }
        if (!z) {
            int i6 = LauncherSettings.Settings.call(launcherAppState.getContext().getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getInt("value");
            intArray3.add(i6);
            intArray2.add(i6);
            if (findNextAvailableIconSpaceInScreen(launcherAppState, (ArrayList) longSparseArray.get((long) i6), iArr, i, i2)) {
                i3 = i6;
            } else {
                throw new RuntimeException("Can't find space to add the item");
            }
        }
        return new int[]{i3, iArr[0], iArr[1]};
    }

    private boolean findNextAvailableIconSpaceInScreen(LauncherAppState launcherAppState, ArrayList<ItemInfo> arrayList, int[] iArr, int i, int i2) {
        InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
        GridOccupancy gridOccupancy = new GridOccupancy(invariantDeviceProfile.numColumns, invariantDeviceProfile.numRows);
        if (arrayList != null) {
            Iterator<ItemInfo> it = arrayList.iterator();
            while (it.hasNext()) {
                gridOccupancy.markCells(it.next(), true);
            }
        }
        return gridOccupancy.findVacantCell(iArr, i, i2);
    }
}
