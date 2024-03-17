package com.android.launcher3.folder;

import android.graphics.Point;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import java.util.ArrayList;
import java.util.List;

public class FolderGridOrganizer {
    private int mCountX;
    private int mCountY;
    private boolean mDisplayingUpperLeftQuadrant = false;
    private final int mMaxCountX;
    private final int mMaxCountY;
    private final int mMaxItemsPerPage;
    private int mNumItemsInFolder;
    private final Point mPoint = new Point();

    public FolderGridOrganizer(InvariantDeviceProfile invariantDeviceProfile) {
        int i = invariantDeviceProfile.numFolderColumns;
        this.mMaxCountX = i;
        int i2 = invariantDeviceProfile.numFolderRows;
        this.mMaxCountY = i2;
        this.mMaxItemsPerPage = i * i2;
    }

    public FolderGridOrganizer setFolderInfo(FolderInfo folderInfo) {
        return setContentSize(folderInfo.contents.size());
    }

    public FolderGridOrganizer setContentSize(int i) {
        if (i != this.mNumItemsInFolder) {
            calculateGridSize(i);
            this.mDisplayingUpperLeftQuadrant = i > 4;
            this.mNumItemsInFolder = i;
        }
        return this;
    }

    public int getCountX() {
        return this.mCountX;
    }

    public int getCountY() {
        return this.mCountY;
    }

    public int getMaxItemsPerPage() {
        return this.mMaxItemsPerPage;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0031  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void calculateGridSize(int r7) {
        /*
            r6 = this;
            int r0 = r6.mCountX
            int r1 = r6.mCountY
            int r2 = r6.mMaxItemsPerPage
            r3 = 0
            r4 = 1
            if (r7 < r2) goto L_0x0010
            int r0 = r6.mMaxCountX
            int r1 = r6.mMaxCountY
            r2 = r4
            goto L_0x0011
        L_0x0010:
            r2 = r3
        L_0x0011:
            if (r2 != 0) goto L_0x005b
            int r2 = r0 * r1
            if (r2 >= r7) goto L_0x0034
            if (r0 <= r1) goto L_0x001d
            int r2 = r6.mMaxCountY
            if (r1 != r2) goto L_0x0025
        L_0x001d:
            int r2 = r6.mMaxCountX
            if (r0 >= r2) goto L_0x0025
            int r2 = r0 + 1
            r5 = r2
            goto L_0x002e
        L_0x0025:
            int r2 = r6.mMaxCountY
            if (r1 >= r2) goto L_0x002d
            int r2 = r1 + 1
            r5 = r0
            goto L_0x002f
        L_0x002d:
            r5 = r0
        L_0x002e:
            r2 = r1
        L_0x002f:
            if (r2 != 0) goto L_0x0050
            int r2 = r2 + 1
            goto L_0x0050
        L_0x0034:
            int r2 = r1 + -1
            int r5 = r2 * r0
            if (r5 < r7) goto L_0x0042
            if (r1 < r0) goto L_0x0042
            int r2 = java.lang.Math.max(r3, r2)
            r5 = r0
            goto L_0x0050
        L_0x0042:
            int r2 = r0 + -1
            int r5 = r2 * r1
            if (r5 < r7) goto L_0x004e
            int r2 = java.lang.Math.max(r3, r2)
            r5 = r2
            goto L_0x004f
        L_0x004e:
            r5 = r0
        L_0x004f:
            r2 = r1
        L_0x0050:
            if (r5 != r0) goto L_0x0056
            if (r2 != r1) goto L_0x0056
            r0 = r4
            goto L_0x0057
        L_0x0056:
            r0 = r3
        L_0x0057:
            r1 = r2
            r2 = r0
            r0 = r5
            goto L_0x0011
        L_0x005b:
            r6.mCountX = r0
            r6.mCountY = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderGridOrganizer.calculateGridSize(int):void");
    }

    public boolean updateRankAndPos(ItemInfo itemInfo, int i) {
        Point posForRank = getPosForRank(i);
        if (posForRank.equals(itemInfo.cellX, itemInfo.cellY) && i == itemInfo.rank) {
            return false;
        }
        itemInfo.rank = i;
        itemInfo.cellX = posForRank.x;
        itemInfo.cellY = posForRank.y;
        return true;
    }

    public Point getPosForRank(int i) {
        int i2 = i % this.mMaxItemsPerPage;
        this.mPoint.x = i2 % this.mCountX;
        this.mPoint.y = i2 / this.mCountX;
        return this.mPoint;
    }

    public <T, R extends T> ArrayList<R> previewItemsForPage(int i, List<T> list) {
        ArrayList<R> arrayList = new ArrayList<>();
        int i2 = this.mCountX * this.mCountY;
        int i3 = i2 * i;
        int min = Math.min(i2 + i3, list.size());
        int i4 = 0;
        while (i3 < min) {
            if (isItemInPreview(i, i4)) {
                arrayList.add(list.get(i3));
            }
            if (arrayList.size() == 4) {
                break;
            }
            i3++;
            i4++;
        }
        return arrayList;
    }

    public boolean isItemInPreview(int i) {
        return isItemInPreview(0, i);
    }

    public boolean isItemInPreview(int i, int i2) {
        if (i <= 0 && !this.mDisplayingUpperLeftQuadrant) {
            return i2 < 4;
        }
        int i3 = this.mCountX;
        int i4 = i2 % i3;
        int i5 = i2 / i3;
        if (i4 >= 2 || i5 >= 2) {
            return false;
        }
        return true;
    }
}
