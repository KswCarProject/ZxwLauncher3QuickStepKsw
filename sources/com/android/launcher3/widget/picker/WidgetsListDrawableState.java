package com.android.launcher3.widget.picker;

enum WidgetsListDrawableState {
    FIRST(new int[]{16842916}),
    FIRST_EXPANDED(new int[]{16842916, 16842920}),
    MIDDLE(new int[]{16842917}),
    MIDDLE_EXPANDED(new int[]{16842917, 16842920}),
    LAST(new int[]{16842918}),
    SINGLE(new int[]{16842915});
    
    final int[] mStateSet;

    private WidgetsListDrawableState(int[] iArr) {
        this.mStateSet = iArr;
    }

    static WidgetsListDrawableState obtain(boolean z, boolean z2, boolean z3) {
        if (z && z2) {
            return SINGLE;
        }
        if (z && z3) {
            return FIRST_EXPANDED;
        }
        if (z) {
            return FIRST;
        }
        if (z2) {
            return LAST;
        }
        if (z3) {
            return MIDDLE_EXPANDED;
        }
        return MIDDLE;
    }
}
