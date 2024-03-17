package com.android.launcher3.touch;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.SplitConfigurationOptions;
import java.util.List;

public interface PagedOrientationHandler {
    public static final Float2DAction<Canvas> CANVAS_TRANSLATE = $$Lambda$PagedOrientationHandler$HT0D7S8EJdDOP2efJkAQgNIVjW8.INSTANCE;
    public static final PagedOrientationHandler LANDSCAPE = new LandscapePagedViewHandler();
    public static final Float2DAction<Matrix> MATRIX_POST_TRANSLATE = $$Lambda$PagedOrientationHandler$nlcrEZjfXzHAociO2IEsX0xVlxg.INSTANCE;
    public static final PagedOrientationHandler PORTRAIT = new PortraitPagedViewHandler();
    public static final PagedOrientationHandler SEASCAPE = new SeascapePagedViewHandler();
    public static final Int2DAction<View> VIEW_SCROLL_BY = $$Lambda$PagedOrientationHandler$k1Q3MNHnKzExQ9fAcIyonKafcM.INSTANCE;
    public static final Int2DAction<View> VIEW_SCROLL_TO = $$Lambda$PagedOrientationHandler$RSyS5_SLzg8RAnh9nfGVICQSBI.INSTANCE;

    public interface Float2DAction<T> {
        void call(T t, float f, float f2);
    }

    public interface Int2DAction<T> {
        void call(T t, int i, int i2);
    }

    void adjustFloatingIconStartVelocity(PointF pointF);

    void fixBoundsForHomeAnimStartRect(RectF rectF, DeviceProfile deviceProfile);

    PointF getAdditionalInsetForTaskMenu(float f);

    int getCenterForPage(View view, Rect rect);

    ChildBounds getChildBounds(View view, int i, int i2, boolean z);

    int getChildStart(View view);

    int getClearAllSidePadding(View view, boolean z);

    int getDefaultSplitPosition(DeviceProfile deviceProfile);

    float getDegreesRotated();

    int getDistanceToBottomOfRect(DeviceProfile deviceProfile, Rect rect);

    Pair<Float, Float> getDwbLayoutTranslations(int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, View[] viewArr, int i3, View view);

    float getEnd(RectF rectF);

    void getFinalSplitPlaceholderBounds(int i, DeviceProfile deviceProfile, int i2, Rect rect, Rect rect2);

    void getInitialSplitPlaceholderBounds(int i, int i2, DeviceProfile deviceProfile, int i3, Rect rect);

    int getMeasuredSize(View view);

    float getPrimaryDirection(MotionEvent motionEvent, int i);

    float getPrimaryScale(View view);

    int getPrimaryScroll(View view);

    float getPrimarySize(RectF rectF);

    int getPrimarySize(View view);

    float getPrimaryValue(float f, float f2);

    int getPrimaryValue(int i, int i2);

    <T> T getPrimaryValue(T t, T t2);

    float getPrimaryVelocity(VelocityTracker velocityTracker, int i);

    FloatProperty<View> getPrimaryViewTranslate();

    boolean getRecentsRtlSetting(Resources resources);

    int getRotation();

    int getScrollOffsetEnd(View view, Rect rect);

    int getScrollOffsetStart(View view, Rect rect);

    int getSecondaryDimension(View view);

    int getSecondaryTranslationDirectionFactor();

    float getSecondaryValue(float f, float f2);

    int getSecondaryValue(int i, int i2);

    <T> T getSecondaryValue(T t, T t2);

    FloatProperty<View> getSecondaryViewTranslate();

    List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile deviceProfile);

    Pair<FloatProperty, FloatProperty> getSplitSelectTaskOffset(FloatProperty floatProperty, FloatProperty floatProperty2, DeviceProfile deviceProfile);

    int getSplitTranslationDirectionFactor(int i, DeviceProfile deviceProfile);

    float getStart(RectF rectF);

    int getTaskDragDisplacementFactor(boolean z);

    int getTaskMenuWidth(View view, DeviceProfile deviceProfile);

    float getTaskMenuX(float f, View view, int i, DeviceProfile deviceProfile);

    float getTaskMenuY(float f, View view, int i);

    int getUpDirection(boolean z);

    SingleAxisSwipeDetector.Direction getUpDownSwipeDirection();

    boolean isGoingUp(float f, boolean z);

    boolean isLayoutNaturalToLauncher();

    void measureGroupedTaskViewThumbnailBounds(View view, View view2, int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, boolean z);

    <T> void set(T t, Int2DAction<T> int2DAction, int i, int i2);

    void setLayoutParamsForTaskMenuOptionItem(LinearLayout.LayoutParams layoutParams, LinearLayout linearLayout, DeviceProfile deviceProfile);

    void setMaxScroll(AccessibilityEvent accessibilityEvent, int i);

    <T> void setPrimary(T t, Float2DAction<T> float2DAction, float f);

    <T> void setPrimary(T t, Int2DAction<T> int2DAction, int i);

    void setPrimaryScale(View view, float f);

    <T> void setSecondary(T t, Float2DAction<T> float2DAction, float f);

    void setSecondaryScale(View view, float f);

    void setSplitIconParams(View view, View view2, int i, int i2, int i3, int i4, int i5, boolean z, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds);

    void setSplitTaskSwipeRect(DeviceProfile deviceProfile, Rect rect, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, int i);

    void setTaskIconParams(FrameLayout.LayoutParams layoutParams, int i, int i2, int i3, boolean z);

    void setTaskMenuAroundTaskView(LinearLayout linearLayout, float f);

    void setTaskOptionsMenuLayoutOrientation(DeviceProfile deviceProfile, LinearLayout linearLayout, int i, ShapeDrawable shapeDrawable);

    void updateStagedSplitIconParams(View view, float f, float f2, float f3, float f4, int i, int i2, DeviceProfile deviceProfile, int i3);

    public static class ChildBounds {
        public final int childPrimaryEnd;
        public final int childSecondaryEnd;
        public final int primaryDimension;
        public final int secondaryDimension;

        ChildBounds(int i, int i2, int i3, int i4) {
            this.primaryDimension = i;
            this.secondaryDimension = i2;
            this.childPrimaryEnd = i3;
            this.childSecondaryEnd = i4;
        }
    }
}
