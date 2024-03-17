package com.android.launcher3.touch;

import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.view.GravityCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Collections;
import java.util.List;

public class SeascapePagedViewHandler extends LandscapePagedViewHandler {
    public float getDegreesRotated() {
        return 270.0f;
    }

    public int getRotation() {
        return 3;
    }

    public int getSecondaryTranslationDirectionFactor() {
        return -1;
    }

    public int getSplitTranslationDirectionFactor(int i, DeviceProfile deviceProfile) {
        return i == 1 ? -1 : 1;
    }

    public int getTaskDragDisplacementFactor(boolean z) {
        return z ? -1 : 1;
    }

    public float getTaskMenuX(float f, View view, int i, DeviceProfile deviceProfile) {
        return f;
    }

    public int getUpDirection(boolean z) {
        return z ? 1 : 2;
    }

    public boolean isGoingUp(float f, boolean z) {
        if (z) {
            if (f > 0.0f) {
                return true;
            }
        } else if (f < 0.0f) {
            return true;
        }
        return false;
    }

    public boolean getRecentsRtlSetting(Resources resources) {
        return Utilities.isRtl(resources);
    }

    public void adjustFloatingIconStartVelocity(PointF pointF) {
        pointF.set(pointF.y, -pointF.x);
    }

    public float getTaskMenuY(float f, View view, int i) {
        return f + ((float) i) + (((float) (view.getMeasuredHeight() + view.getMeasuredWidth())) / 2.0f);
    }

    public void setTaskMenuAroundTaskView(LinearLayout linearLayout, float f) {
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.bottomMargin = (int) (((float) layoutParams.bottomMargin) + f);
    }

    public PointF getAdditionalInsetForTaskMenu(float f) {
        return new PointF(-f, f);
    }

    public Pair<Float, Float> getDwbLayoutTranslations(int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, View[] viewArr, int i3, View view) {
        float f;
        boolean z = view.getLayoutDirection() == 1;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        float f2 = 0.0f;
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setRotation(getDegreesRotated());
        float height = (float) (i - view.getHeight());
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewArr[0].getLayoutParams();
        layoutParams.gravity = (z ? GravityCompat.END : GravityCompat.START) | 80;
        if (stagedSplitBounds == null) {
            layoutParams.width = i2 - layoutParams2.topMargin;
            return new Pair<>(Float.valueOf(height), Float.valueOf((float) view.getHeight()));
        }
        if (i3 == stagedSplitBounds.leftTopTaskId) {
            layoutParams.width = viewArr[0].getMeasuredHeight();
        } else {
            layoutParams.width = viewArr[1].getMeasuredHeight();
        }
        if (i3 == stagedSplitBounds.rightBottomTaskId) {
            f2 = (float) view.getHeight();
        }
        if (i3 == stagedSplitBounds.leftTopTaskId) {
            if (stagedSplitBounds.appsStackedVertically) {
                f = stagedSplitBounds.topTaskPercent;
            } else {
                f = stagedSplitBounds.leftTaskPercent;
            }
            f2 = ((float) view.getHeight()) - (((float) (i2 - layoutParams2.topMargin)) * (1.0f - f));
        }
        return new Pair<>(Float.valueOf(height), Float.valueOf(f2));
    }

    public int getDistanceToBottomOfRect(DeviceProfile deviceProfile, Rect rect) {
        return deviceProfile.widthPx - rect.right;
    }

    public List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile deviceProfile) {
        return Collections.singletonList(new SplitConfigurationOptions.SplitPositionOption(R.drawable.ic_split_right, R.string.split_screen_position_right, 1, 0));
    }

    public void setTaskIconParams(FrameLayout.LayoutParams layoutParams, int i, int i2, int i3, boolean z) {
        layoutParams.gravity = (z ? GravityCompat.END : GravityCompat.START) | 16;
        layoutParams.leftMargin = (-i2) - (i / 2);
        layoutParams.rightMargin = 0;
        layoutParams.topMargin = i3 / 2;
    }

    public void setSplitIconParams(View view, View view2, int i, int i2, int i3, int i4, int i5, boolean z, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        super.setSplitIconParams(view, view2, i, i2, i3, i4, i5, z, deviceProfile, stagedSplitBounds);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) view2.getLayoutParams();
        int i6 = deviceProfile.getInsets().top;
        float f = ((float) ((deviceProfile.heightPx - i6) / 2)) / ((float) deviceProfile.heightPx);
        float f2 = (float) (i4 - deviceProfile.overviewTaskThumbnailTopMarginPx);
        int i7 = (int) (f * f2);
        int i8 = (int) (f2 * (((float) i6) / ((float) deviceProfile.heightPx)));
        int i9 = GravityCompat.END;
        layoutParams.gravity = (z ? 8388613 : 8388611) | 80;
        if (!z) {
            i9 = 8388611;
        }
        layoutParams2.gravity = i9 | 80;
        view.setTranslationX(0.0f);
        view2.setTranslationX(0.0f);
        if (stagedSplitBounds.initiatedFromSeascape) {
            int i10 = (-i7) - i8;
            view.setTranslationY((float) i10);
            view2.setTranslationY((float) (i10 + i));
        } else {
            int i11 = -i7;
            view.setTranslationY((float) i11);
            view2.setTranslationY((float) (i11 + i));
        }
        view.setLayoutParams(layoutParams);
        view2.setLayoutParams(layoutParams2);
    }

    public SingleAxisSwipeDetector.Direction getUpDownSwipeDirection() {
        return SingleAxisSwipeDetector.HORIZONTAL;
    }
}
