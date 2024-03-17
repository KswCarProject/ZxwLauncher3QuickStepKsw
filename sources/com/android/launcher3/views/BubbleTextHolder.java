package com.android.launcher3.views;

import com.android.launcher3.BubbleTextView;

public interface BubbleTextHolder extends IconLabelDotView {
    BubbleTextView getBubbleText();

    void setIconVisible(boolean z) {
        getBubbleText().setIconVisible(z);
    }

    void setForceHideDot(boolean z) {
        getBubbleText().setForceHideDot(z);
    }
}
