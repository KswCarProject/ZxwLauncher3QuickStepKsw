package com.android.launcher3;

import android.graphics.PointF;
import android.view.View;

public interface Reorderable {
    void getReorderBounceOffset(PointF pointF);

    float getReorderBounceScale();

    void getReorderPreviewOffset(PointF pointF);

    View getView();

    void setReorderBounceOffset(float f, float f2);

    void setReorderBounceScale(float f);

    void setReorderPreviewOffset(float f, float f2);
}
