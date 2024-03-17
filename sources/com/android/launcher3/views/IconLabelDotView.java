package com.android.launcher3.views;

import android.view.View;

public interface IconLabelDotView {
    void setForceHideDot(boolean z);

    void setIconVisible(boolean z);

    static void setIconAndDotVisible(View view, boolean z) {
        if (view instanceof IconLabelDotView) {
            IconLabelDotView iconLabelDotView = (IconLabelDotView) view;
            iconLabelDotView.setIconVisible(z);
            iconLabelDotView.setForceHideDot(!z);
            return;
        }
        view.setVisibility(z ? 0 : 4);
    }
}
