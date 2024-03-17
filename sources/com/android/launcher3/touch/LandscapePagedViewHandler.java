package com.android.launcher3.touch;

import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.util.FloatProperty;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.view.GravityCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Collections;
import java.util.List;

public class LandscapePagedViewHandler implements PagedOrientationHandler {
    public float getDegreesRotated() {
        return 90.0f;
    }

    public float getPrimaryValue(float f, float f2) {
        return f2;
    }

    public int getPrimaryValue(int i, int i2) {
        return i2;
    }

    public <T> T getPrimaryValue(T t, T t2) {
        return t2;
    }

    public int getRotation() {
        return 1;
    }

    public int getSecondaryTranslationDirectionFactor() {
        return 1;
    }

    public float getSecondaryValue(float f, float f2) {
        return f;
    }

    public int getSecondaryValue(int i, int i2) {
        return i;
    }

    public <T> T getSecondaryValue(T t, T t2) {
        return t;
    }

    public int getSplitTranslationDirectionFactor(int i, DeviceProfile deviceProfile) {
        return i == 1 ? -1 : 1;
    }

    public int getTaskDragDisplacementFactor(boolean z) {
        return z ? 1 : -1;
    }

    public int getUpDirection(boolean z) {
        return z ? 2 : 1;
    }

    public boolean isGoingUp(float f, boolean z) {
        if (z) {
            if (f < 0.0f) {
                return true;
            }
        } else if (f > 0.0f) {
            return true;
        }
        return false;
    }

    public boolean isLayoutNaturalToLauncher() {
        return false;
    }

    public void adjustFloatingIconStartVelocity(PointF pointF) {
        pointF.set(-pointF.y, pointF.x);
    }

    public void fixBoundsForHomeAnimStartRect(RectF rectF, DeviceProfile deviceProfile) {
        if (rectF.left > ((float) deviceProfile.heightPx)) {
            rectF.offsetTo(0.0f, rectF.top);
        } else if (rectF.left < ((float) (-deviceProfile.heightPx))) {
            rectF.offsetTo(0.0f, rectF.top);
        }
    }

    public <T> void setPrimary(T t, PagedOrientationHandler.Int2DAction<T> int2DAction, int i) {
        int2DAction.call(t, 0, i);
    }

    public <T> void setPrimary(T t, PagedOrientationHandler.Float2DAction<T> float2DAction, float f) {
        float2DAction.call(t, 0.0f, f);
    }

    public <T> void setSecondary(T t, PagedOrientationHandler.Float2DAction<T> float2DAction, float f) {
        float2DAction.call(t, f, 0.0f);
    }

    public <T> void set(T t, PagedOrientationHandler.Int2DAction<T> int2DAction, int i, int i2) {
        int2DAction.call(t, i2, i);
    }

    public float getPrimaryDirection(MotionEvent motionEvent, int i) {
        return motionEvent.getY(i);
    }

    public float getPrimaryVelocity(VelocityTracker velocityTracker, int i) {
        return velocityTracker.getYVelocity(i);
    }

    public int getMeasuredSize(View view) {
        return view.getMeasuredHeight();
    }

    public int getPrimarySize(View view) {
        return view.getHeight();
    }

    public float getPrimarySize(RectF rectF) {
        return rectF.height();
    }

    public float getStart(RectF rectF) {
        return rectF.top;
    }

    public float getEnd(RectF rectF) {
        return rectF.bottom;
    }

    public int getClearAllSidePadding(View view, boolean z) {
        return (z ? view.getPaddingBottom() : -view.getPaddingTop()) / 2;
    }

    public int getSecondaryDimension(View view) {
        return view.getWidth();
    }

    public FloatProperty<View> getPrimaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_Y;
    }

    public FloatProperty<View> getSecondaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_X;
    }

    public int getPrimaryScroll(View view) {
        return view.getScrollY();
    }

    public float getPrimaryScale(View view) {
        return view.getScaleY();
    }

    public void setMaxScroll(AccessibilityEvent accessibilityEvent, int i) {
        accessibilityEvent.setMaxScrollY(i);
    }

    public boolean getRecentsRtlSetting(Resources resources) {
        return !Utilities.isRtl(resources);
    }

    public void setPrimaryScale(View view, float f) {
        view.setScaleY(f);
    }

    public void setSecondaryScale(View view, float f) {
        view.setScaleX(f);
    }

    public int getChildStart(View view) {
        return view.getTop();
    }

    public int getCenterForPage(View view, Rect rect) {
        return ((((view.getPaddingLeft() + view.getMeasuredWidth()) + rect.left) - rect.right) - view.getPaddingRight()) / 2;
    }

    public int getScrollOffsetStart(View view, Rect rect) {
        return rect.top + view.getPaddingTop();
    }

    public int getScrollOffsetEnd(View view, Rect rect) {
        return (view.getHeight() - view.getPaddingBottom()) - rect.bottom;
    }

    public float getTaskMenuX(float f, View view, int i, DeviceProfile deviceProfile) {
        return ((float) view.getMeasuredWidth()) + f;
    }

    public float getTaskMenuY(float f, View view, int i) {
        return f + ((float) i) + (((float) (view.getMeasuredHeight() - view.getMeasuredWidth())) / 2.0f);
    }

    public int getTaskMenuWidth(View view, DeviceProfile deviceProfile) {
        return view.getMeasuredWidth();
    }

    public void setTaskOptionsMenuLayoutOrientation(DeviceProfile deviceProfile, LinearLayout linearLayout, int i, ShapeDrawable shapeDrawable) {
        linearLayout.setOrientation(1);
        shapeDrawable.setIntrinsicHeight(i);
        linearLayout.setDividerDrawable(shapeDrawable);
    }

    public void setLayoutParamsForTaskMenuOptionItem(LinearLayout.LayoutParams layoutParams, LinearLayout linearLayout, DeviceProfile deviceProfile) {
        linearLayout.setOrientation(0);
        layoutParams.width = -1;
        layoutParams.height = -2;
    }

    public void setTaskMenuAroundTaskView(LinearLayout linearLayout, float f) {
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.topMargin = (int) (((float) layoutParams.topMargin) + f);
    }

    public PointF getAdditionalInsetForTaskMenu(float f) {
        return new PointF(f, 0.0f);
    }

    public Pair<Float, Float> getDwbLayoutTranslations(int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, View[] viewArr, int i3, View view) {
        float f;
        float f2;
        boolean z = view.getLayoutDirection() == 1;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        float f3 = 0.0f;
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setRotation(getDegreesRotated());
        float height = (float) view.getHeight();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewArr[0].getLayoutParams();
        layoutParams.gravity = (z ? GravityCompat.END : GravityCompat.START) | 48;
        if (stagedSplitBounds == null) {
            layoutParams.width = i2 - layoutParams2.topMargin;
            return new Pair<>(Float.valueOf(height), Float.valueOf(Integer.valueOf(layoutParams2.topMargin).floatValue()));
        }
        if (i3 == stagedSplitBounds.leftTopTaskId) {
            layoutParams.width = viewArr[0].getMeasuredHeight();
        } else {
            layoutParams.width = viewArr[1].getMeasuredHeight();
        }
        if (i3 == stagedSplitBounds.rightBottomTaskId) {
            if (stagedSplitBounds.appsStackedVertically) {
                f2 = stagedSplitBounds.topTaskPercent;
                f = stagedSplitBounds.dividerHeightPercent;
            } else {
                f2 = stagedSplitBounds.leftTaskPercent;
                f = stagedSplitBounds.dividerWidthPercent;
            }
            f3 = ((float) layoutParams2.topMargin) + (((float) (i2 - layoutParams2.topMargin)) * (f2 + f));
        }
        if (i3 == stagedSplitBounds.leftTopTaskId) {
            f3 = (float) layoutParams2.topMargin;
        }
        return new Pair<>(Float.valueOf(height), Float.valueOf(f3));
    }

    public SingleAxisSwipeDetector.Direction getUpDownSwipeDirection() {
        return SingleAxisSwipeDetector.HORIZONTAL;
    }

    public PagedOrientationHandler.ChildBounds getChildBounds(View view, int i, int i2, boolean z) {
        int measuredHeight = view.getMeasuredHeight();
        int i3 = i + measuredHeight;
        int measuredWidth = view.getMeasuredWidth();
        int i4 = i2 - (measuredWidth / 2);
        if (z) {
            view.layout(i4, i, i4 + measuredWidth, i3);
        }
        return new PagedOrientationHandler.ChildBounds(measuredHeight, measuredWidth, i3, i4);
    }

    public int getDistanceToBottomOfRect(DeviceProfile deviceProfile, Rect rect) {
        return rect.left;
    }

    public List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile deviceProfile) {
        return Collections.singletonList(new SplitConfigurationOptions.SplitPositionOption(R.drawable.ic_split_left, R.string.split_screen_position_left, 0, 0));
    }

    public void getInitialSplitPlaceholderBounds(int i, int i2, DeviceProfile deviceProfile, int i3, Rect rect) {
        rect.set(0, 0, deviceProfile.widthPx, deviceProfile.getInsets().top + i);
        rect.inset(i2, 0);
        int i4 = deviceProfile.widthPx;
        rect.top -= ((int) ((((((float) deviceProfile.heightPx) * 1.0f) / 2.0f) * ((float) (i4 - (i2 * 2)))) / ((float) i4))) - i;
    }

    public void updateStagedSplitIconParams(View view, float f, float f2, float f3, float f4, int i, int i2, DeviceProfile deviceProfile, int i3) {
        view.setX((float) Math.round((f / f3) - ((((float) i) * 1.0f) / 2.0f)));
        view.setY((float) Math.round(((f2 + (((float) deviceProfile.getInsets().top) / 2.0f)) / f4) - ((((float) i2) * 1.0f) / 2.0f)));
    }

    public void getFinalSplitPlaceholderBounds(int i, DeviceProfile deviceProfile, int i2, Rect rect, Rect rect2) {
        int i3 = deviceProfile.heightPx;
        int i4 = deviceProfile.widthPx;
        int i5 = i3 / 2;
        rect.set(0, 0, i4, i5 - i);
        rect2.set(0, i5 + i, i4, i3);
    }

    public void setSplitTaskSwipeRect(DeviceProfile deviceProfile, Rect rect, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, int i) {
        float f;
        float f2;
        if (stagedSplitBounds.appsStackedVertically) {
            f = stagedSplitBounds.topTaskPercent;
        } else {
            f = stagedSplitBounds.leftTaskPercent;
        }
        if (stagedSplitBounds.appsStackedVertically) {
            f2 = stagedSplitBounds.dividerHeightPercent;
        } else {
            f2 = stagedSplitBounds.dividerWidthPercent;
        }
        if (i == 0) {
            rect.bottom = rect.top + ((int) (((float) rect.height()) * f));
        } else {
            rect.top += (int) (((float) rect.height()) * (f + f2));
        }
    }

    public void measureGroupedTaskViewThumbnailBounds(View view, View view2, int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, boolean z) {
        int i3;
        int i4 = deviceProfile.overviewTaskThumbnailTopMarginPx;
        int i5 = i2 - i4;
        if (stagedSplitBounds.appsStackedVertically) {
            i3 = stagedSplitBounds.visualDividerBounds.height();
        } else {
            i3 = stagedSplitBounds.visualDividerBounds.width();
        }
        int i6 = (int) (((float) i5) * (stagedSplitBounds.appsStackedVertically ? stagedSplitBounds.topTaskPercent : stagedSplitBounds.leftTaskPercent));
        view2.setTranslationY((float) (i4 + i6 + i3));
        view.measure(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i6, BasicMeasure.EXACTLY));
        view2.measure(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec((i5 - i6) - i3, BasicMeasure.EXACTLY));
    }

    public void setTaskIconParams(FrameLayout.LayoutParams layoutParams, int i, int i2, int i3, boolean z) {
        layoutParams.gravity = (z ? GravityCompat.START : GravityCompat.END) | 16;
        layoutParams.rightMargin = (-i2) - (i / 2);
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = i3 / 2;
    }

    public void setSplitIconParams(View view, View view2, int i, int i2, int i3, int i4, int i5, boolean z, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(layoutParams);
        int i6 = deviceProfile.getInsets().top;
        float f = ((float) ((deviceProfile.heightPx - i6) / 2)) / ((float) deviceProfile.heightPx);
        float f2 = (float) (i4 - deviceProfile.overviewTaskThumbnailTopMarginPx);
        int i7 = (int) (f * f2);
        int i8 = (int) (f2 * (((float) i6) / ((float) deviceProfile.heightPx)));
        int i9 = GravityCompat.START;
        layoutParams.gravity = (z ? 8388611 : 8388613) | 80;
        if (!z) {
            i9 = 8388613;
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

    public int getDefaultSplitPosition(DeviceProfile deviceProfile) {
        throw new IllegalStateException("Default position not available in fake landscape");
    }

    public Pair<FloatProperty, FloatProperty> getSplitSelectTaskOffset(FloatProperty floatProperty, FloatProperty floatProperty2, DeviceProfile deviceProfile) {
        return new Pair<>(floatProperty, floatProperty2);
    }
}
