package com.android.launcher3.pageindicators;

public interface PageIndicator {
    void pauseAnimations() {
    }

    void setActiveMarker(int i);

    void setMarkersCount(int i);

    void setScroll(int i, int i2);

    void setShouldAutoHide(boolean z) {
    }

    void skipAnimationsToEnd() {
    }
}
