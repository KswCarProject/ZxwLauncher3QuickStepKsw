package com.android.launcher3.util;

public abstract class AbsGridOccupancy {
    /* access modifiers changed from: protected */
    public boolean findVacantCell(int[] iArr, boolean[][] zArr, int i, int i2, int i3, int i4) {
        int i5 = 0;
        while (true) {
            int i6 = i5 + i4;
            if (i6 <= i2) {
                int i7 = 0;
                while (true) {
                    int i8 = i7 + i3;
                    if (i8 > i) {
                        break;
                    }
                    boolean z = !zArr[i7][i5];
                    for (int i9 = i7; i9 < i8; i9++) {
                        for (int i10 = i5; i10 < i6; i10++) {
                            z = z && !zArr[i9][i10];
                            if (!z) {
                                break;
                            }
                        }
                    }
                    if (z) {
                        iArr[0] = i7;
                        iArr[1] = i5;
                        return true;
                    }
                    i7++;
                }
            } else {
                return false;
            }
            i5++;
        }
    }
}
