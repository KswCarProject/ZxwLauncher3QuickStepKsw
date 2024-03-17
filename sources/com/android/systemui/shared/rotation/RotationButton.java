package com.android.systemui.shared.rotation;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface RotationButton {

    public interface RotationButtonUpdatesCallback {
        void onPositionChanged() {
        }

        void onVisibilityChanged(boolean z) {
        }
    }

    View getCurrentView() {
        return null;
    }

    Drawable getImageDrawable() {
        return null;
    }

    boolean hide() {
        return false;
    }

    boolean isVisible() {
        return false;
    }

    void onTaskbarStateChanged(boolean z, boolean z2) {
    }

    void setCanShowRotationButton(boolean z) {
    }

    void setDarkIntensity(float f) {
    }

    void setOnClickListener(View.OnClickListener onClickListener) {
    }

    void setOnHoverListener(View.OnHoverListener onHoverListener) {
    }

    void setRotationButtonController(RotationButtonController rotationButtonController) {
    }

    void setUpdatesCallback(RotationButtonUpdatesCallback rotationButtonUpdatesCallback) {
    }

    boolean show() {
        return false;
    }

    void updateIcon(int i, int i2) {
    }

    boolean acceptRotationProposal() {
        return getCurrentView() != null;
    }
}
