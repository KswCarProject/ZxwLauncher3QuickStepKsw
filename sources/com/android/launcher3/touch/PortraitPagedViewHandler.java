package com.android.launcher3.touch;

import android.content.res.Resources;
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
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import androidx.core.view.GravityCompat;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.views.BaseDragLayer;
import java.util.List;

public class PortraitPagedViewHandler implements PagedOrientationHandler {
    private final Matrix mTmpMatrix = new Matrix();
    private final RectF mTmpRectF = new RectF();

    public void adjustFloatingIconStartVelocity(PointF pointF) {
    }

    public float getDegreesRotated() {
        return 0.0f;
    }

    public float getPrimaryValue(float f, float f2) {
        return f;
    }

    public int getPrimaryValue(int i, int i2) {
        return i;
    }

    public <T> T getPrimaryValue(T t, T t2) {
        return t;
    }

    public int getRotation() {
        return 0;
    }

    public int getSecondaryTranslationDirectionFactor() {
        return -1;
    }

    public float getSecondaryValue(float f, float f2) {
        return f2;
    }

    public int getSecondaryValue(int i, int i2) {
        return i2;
    }

    public <T> T getSecondaryValue(T t, T t2) {
        return t2;
    }

    public int getTaskDragDisplacementFactor(boolean z) {
        return 1;
    }

    public float getTaskMenuY(float f, View view, int i) {
        return f;
    }

    public int getUpDirection(boolean z) {
        return 1;
    }

    public boolean isGoingUp(float f, boolean z) {
        return f < 0.0f;
    }

    public boolean isLayoutNaturalToLauncher() {
        return true;
    }

    public void fixBoundsForHomeAnimStartRect(RectF rectF, DeviceProfile deviceProfile) {
        if (rectF.left > ((float) deviceProfile.widthPx)) {
            rectF.offsetTo(0.0f, rectF.top);
        } else if (rectF.left < ((float) (-deviceProfile.widthPx))) {
            rectF.offsetTo(0.0f, rectF.top);
        }
    }

    public <T> void setPrimary(T t, PagedOrientationHandler.Int2DAction<T> int2DAction, int i) {
        int2DAction.call(t, i, 0);
    }

    public <T> void setPrimary(T t, PagedOrientationHandler.Float2DAction<T> float2DAction, float f) {
        float2DAction.call(t, f, 0.0f);
    }

    public <T> void setSecondary(T t, PagedOrientationHandler.Float2DAction<T> float2DAction, float f) {
        float2DAction.call(t, 0.0f, f);
    }

    public <T> void set(T t, PagedOrientationHandler.Int2DAction<T> int2DAction, int i, int i2) {
        int2DAction.call(t, i, i2);
    }

    public float getPrimaryDirection(MotionEvent motionEvent, int i) {
        return motionEvent.getX(i);
    }

    public float getPrimaryVelocity(VelocityTracker velocityTracker, int i) {
        return velocityTracker.getXVelocity(i);
    }

    public int getMeasuredSize(View view) {
        return view.getMeasuredWidth();
    }

    public int getPrimarySize(View view) {
        return view.getWidth();
    }

    public float getPrimarySize(RectF rectF) {
        return rectF.width();
    }

    public float getStart(RectF rectF) {
        return rectF.left;
    }

    public float getEnd(RectF rectF) {
        return rectF.right;
    }

    public int getClearAllSidePadding(View view, boolean z) {
        return (z ? view.getPaddingRight() : -view.getPaddingLeft()) / 2;
    }

    public int getSecondaryDimension(View view) {
        return view.getHeight();
    }

    public FloatProperty<View> getPrimaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_X;
    }

    public FloatProperty<View> getSecondaryViewTranslate() {
        return LauncherAnimUtils.VIEW_TRANSLATE_Y;
    }

    public int getPrimaryScroll(View view) {
        return view.getScrollX();
    }

    public float getPrimaryScale(View view) {
        return view.getScaleX();
    }

    public void setMaxScroll(AccessibilityEvent accessibilityEvent, int i) {
        accessibilityEvent.setMaxScrollX(i);
    }

    public boolean getRecentsRtlSetting(Resources resources) {
        return !Utilities.isRtl(resources);
    }

    public void setPrimaryScale(View view, float f) {
        view.setScaleX(f);
    }

    public void setSecondaryScale(View view, float f) {
        view.setScaleY(f);
    }

    public int getChildStart(View view) {
        return view.getLeft();
    }

    public int getCenterForPage(View view, Rect rect) {
        return ((((view.getPaddingTop() + view.getMeasuredHeight()) + rect.top) - rect.bottom) - view.getPaddingBottom()) / 2;
    }

    public int getScrollOffsetStart(View view, Rect rect) {
        return rect.left + view.getPaddingLeft();
    }

    public int getScrollOffsetEnd(View view, Rect rect) {
        return (view.getWidth() - view.getPaddingRight()) - rect.right;
    }

    public int getSplitTranslationDirectionFactor(int i, DeviceProfile deviceProfile) {
        return (!deviceProfile.isLandscape || i != 1) ? 1 : -1;
    }

    public float getTaskMenuX(float f, View view, int i, DeviceProfile deviceProfile) {
        float f2;
        if (deviceProfile.isLandscape) {
            f += (float) i;
            f2 = ((float) (view.getMeasuredWidth() - view.getMeasuredHeight())) / 2.0f;
        } else {
            f2 = (float) i;
        }
        return f + f2;
    }

    public int getTaskMenuWidth(View view, DeviceProfile deviceProfile) {
        if (!deviceProfile.isLandscape || deviceProfile.isTablet) {
            return view.getMeasuredWidth();
        }
        return view.getMeasuredHeight();
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
        layoutParams.leftMargin = (int) (((float) layoutParams.leftMargin) + f);
    }

    public PointF getAdditionalInsetForTaskMenu(float f) {
        return new PointF(0.0f, 0.0f);
    }

    public Pair<Float, Float> getDwbLayoutTranslations(int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, View[] viewArr, int i3, View view) {
        float f;
        float f2;
        float f3;
        float f4;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        float f5 = 0.0f;
        Float valueOf = Float.valueOf(0.0f);
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setRotation(getDegreesRotated());
        if (stagedSplitBounds == null) {
            layoutParams.width = -1;
            layoutParams.gravity = 81;
            return new Pair<>(valueOf, valueOf);
        }
        layoutParams.gravity = (deviceProfile.isLandscape ? GravityCompat.START : 1) | 80;
        if (i3 == stagedSplitBounds.leftTopTaskId) {
            layoutParams.width = viewArr[0].getMeasuredWidth();
        } else {
            layoutParams.width = viewArr[1].getMeasuredWidth();
        }
        if (deviceProfile.isLandscape) {
            if (i3 == stagedSplitBounds.rightBottomTaskId) {
                if (stagedSplitBounds.appsStackedVertically) {
                    f3 = stagedSplitBounds.topTaskPercent;
                } else {
                    f3 = stagedSplitBounds.leftTaskPercent;
                }
                if (stagedSplitBounds.appsStackedVertically) {
                    f4 = stagedSplitBounds.dividerHeightPercent;
                } else {
                    f4 = stagedSplitBounds.dividerWidthPercent;
                }
                float f6 = (float) i;
                float f7 = (f3 * f6) + (f6 * f4);
                f = 0.0f;
                f5 = f7;
                return new Pair<>(Float.valueOf(f5), Float.valueOf(f));
            }
        } else if (i3 == stagedSplitBounds.leftTopTaskId) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewArr[0].getLayoutParams();
            if (stagedSplitBounds.appsStackedVertically) {
                f2 = stagedSplitBounds.topTaskPercent;
            } else {
                f2 = stagedSplitBounds.leftTaskPercent;
            }
            f = -(((float) (i2 - layoutParams2.topMargin)) * (1.0f - f2));
            return new Pair<>(Float.valueOf(f5), Float.valueOf(f));
        }
        f = 0.0f;
        return new Pair<>(Float.valueOf(f5), Float.valueOf(f));
    }

    public SingleAxisSwipeDetector.Direction getUpDownSwipeDirection() {
        return SingleAxisSwipeDetector.VERTICAL;
    }

    public PagedOrientationHandler.ChildBounds getChildBounds(View view, int i, int i2, boolean z) {
        int measuredWidth = view.getMeasuredWidth();
        int i3 = i + measuredWidth;
        int measuredHeight = view.getMeasuredHeight();
        int i4 = i2 - (measuredHeight / 2);
        if (z) {
            view.layout(i, i4, i3, i4 + measuredHeight);
        }
        return new PagedOrientationHandler.ChildBounds(measuredWidth, measuredHeight, i3, i4);
    }

    public int getDistanceToBottomOfRect(DeviceProfile deviceProfile, Rect rect) {
        return deviceProfile.heightPx - rect.bottom;
    }

    public List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile deviceProfile) {
        return Utilities.getSplitPositionOptions(deviceProfile);
    }

    public void getInitialSplitPlaceholderBounds(int i, int i2, DeviceProfile deviceProfile, int i3, Rect rect) {
        int i4;
        int i5 = deviceProfile.widthPx;
        int i6 = deviceProfile.heightPx;
        boolean z = true;
        if (i3 != 1) {
            z = false;
        }
        if (!deviceProfile.isLandscape) {
            i4 = deviceProfile.getInsets().top;
        } else {
            Rect insets = deviceProfile.getInsets();
            i4 = z ? insets.right : insets.left;
        }
        rect.set(0, 0, i5, i4 + i);
        if (!deviceProfile.isLandscape) {
            rect.inset(i2, 0);
            rect.top -= ((int) ((((((float) i6) * 1.0f) / 2.0f) * ((float) (i5 - (i2 * 2)))) / ((float) i5))) - i;
            return;
        }
        float f = (float) i6;
        float f2 = (float) i5;
        float f3 = f / f2;
        this.mTmpMatrix.reset();
        this.mTmpMatrix.postRotate(z ? 90.0f : 270.0f);
        this.mTmpMatrix.postTranslate(z ? f2 : 0.0f, z ? 0.0f : f2);
        this.mTmpMatrix.postScale(1.0f, f3);
        this.mTmpRectF.set(rect);
        this.mTmpMatrix.mapRect(this.mTmpRectF);
        this.mTmpRectF.inset(0.0f, (float) i2);
        this.mTmpRectF.roundOut(rect);
        int i7 = (int) ((((f2 * 1.0f) / 2.0f) * ((float) (i6 - (i2 * 2)))) / f);
        int width = rect.width();
        if (z) {
            rect.right += i7 - width;
        } else {
            rect.left -= i7 - width;
        }
    }

    public void updateStagedSplitIconParams(View view, float f, float f2, float f3, float f4, int i, int i2, DeviceProfile deviceProfile, int i3) {
        boolean z = true;
        if (i3 != 1) {
            z = false;
        }
        if (!deviceProfile.isLandscape) {
            view.setX((float) Math.round((f / f3) - ((((float) i) * 1.0f) / 2.0f)));
            view.setY((float) Math.round(((f2 + (((float) deviceProfile.getInsets().top) / 2.0f)) / f4) - ((((float) i2) * 1.0f) / 2.0f)));
            return;
        }
        if (z) {
            view.setX((float) Math.round(((f - (((float) deviceProfile.getInsets().right) / 2.0f)) / f3) - ((((float) i) * 1.0f) / 2.0f)));
        } else {
            view.setX((float) Math.round(((f + (((float) deviceProfile.getInsets().left) / 2.0f)) / f3) - ((((float) i) * 1.0f) / 2.0f)));
        }
        view.setY((float) Math.round((f2 / f4) - ((((float) i2) * 1.0f) / 2.0f)));
    }

    public void getFinalSplitPlaceholderBounds(int i, DeviceProfile deviceProfile, int i2, Rect rect, Rect rect2) {
        int i3 = deviceProfile.heightPx;
        int i4 = deviceProfile.widthPx;
        int i5 = i3 / 2;
        boolean z = false;
        rect.set(0, 0, i4, i5 - i);
        rect2.set(0, i5 + i, i4, i3);
        if (deviceProfile.isLandscape) {
            if (i2 == 1) {
                z = true;
            }
            float f = (float) i3;
            float f2 = (float) i4;
            float f3 = f / f2;
            this.mTmpMatrix.reset();
            this.mTmpMatrix.postRotate(z ? 90.0f : 270.0f);
            Matrix matrix = this.mTmpMatrix;
            if (!z) {
                f = 0.0f;
            }
            if (z) {
                f2 = 0.0f;
            }
            matrix.postTranslate(f, f2);
            this.mTmpMatrix.postScale(1.0f / f3, f3);
            this.mTmpRectF.set(rect);
            this.mTmpMatrix.mapRect(this.mTmpRectF);
            this.mTmpRectF.roundOut(rect);
            this.mTmpRectF.set(rect2);
            this.mTmpMatrix.mapRect(this.mTmpRectF);
            this.mTmpRectF.roundOut(rect2);
        }
    }

    public void setSplitTaskSwipeRect(DeviceProfile deviceProfile, Rect rect, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, int i) {
        float f;
        float f2;
        boolean z = deviceProfile.isLandscape;
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
            if (z) {
                rect.right = rect.left + ((int) (((float) rect.width()) * f));
            } else {
                rect.bottom = rect.top + ((int) (((float) rect.height()) * f));
            }
        } else if (z) {
            rect.left += (int) (((float) rect.width()) * (f + f2));
        } else {
            rect.top += (int) (((float) rect.height()) * (f + f2));
        }
    }

    public void measureGroupedTaskViewThumbnailBounds(View view, View view2, int i, int i2, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds, DeviceProfile deviceProfile, boolean z) {
        int i3;
        int i4;
        int i5;
        int i6 = deviceProfile.overviewTaskThumbnailTopMarginPx;
        int i7 = i2 - i6;
        if (stagedSplitBounds.appsStackedVertically) {
            i3 = (int) (stagedSplitBounds.dividerHeightPercent * ((float) i2));
        } else {
            i3 = (int) (stagedSplitBounds.dividerWidthPercent * ((float) i));
        }
        float f = stagedSplitBounds.appsStackedVertically ? stagedSplitBounds.topTaskPercent : stagedSplitBounds.leftTaskPercent;
        if (deviceProfile.isLandscape) {
            int i8 = (int) (((float) i) * f);
            int i9 = (i - i8) - i3;
            int i10 = i3 + i8;
            if (z) {
                view.setTranslationX((float) (-i10));
                view2.setTranslationX(0.0f);
            } else {
                view2.setTranslationX((float) i10);
                view.setTranslationX(0.0f);
            }
            view2.setTranslationY((float) i6);
            i5 = i9;
            i = i8;
            i4 = i7;
        } else {
            int i11 = (int) (((float) i7) * f);
            int i12 = (i7 - i11) - i3;
            view2.setTranslationY((float) (i6 + i11 + i3));
            view2.setTranslationX(0.0f);
            view.setTranslationX(0.0f);
            i5 = i;
            int i13 = i12;
            i7 = i11;
            i4 = i13;
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(i, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i7, BasicMeasure.EXACTLY));
        view2.measure(View.MeasureSpec.makeMeasureSpec(i5, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(i4, BasicMeasure.EXACTLY));
    }

    public void setTaskIconParams(FrameLayout.LayoutParams layoutParams, int i, int i2, int i3, boolean z) {
        layoutParams.gravity = 49;
        layoutParams.rightMargin = 0;
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = i;
    }

    public void setSplitIconParams(View view, View view2, int i, int i2, int i3, int i4, int i5, boolean z, DeviceProfile deviceProfile, SplitConfigurationOptions.StagedSplitBounds stagedSplitBounds) {
        int i6;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(layoutParams);
        if (deviceProfile.isLandscape) {
            if (deviceProfile.isSeascape()) {
                i6 = deviceProfile.getInsets().right;
            } else {
                i6 = deviceProfile.getInsets().left;
            }
            float f = (float) (i5 + 0);
            int i7 = (int) ((((float) ((deviceProfile.widthPx - i6) / 2)) / ((float) deviceProfile.widthPx)) * f);
            int i8 = (int) (f * (((float) i6) / ((float) deviceProfile.widthPx)));
            boolean isSeascape = deviceProfile.isSeascape();
            int i9 = GravityCompat.START;
            if (isSeascape) {
                layoutParams.gravity = (z ? 8388613 : 8388611) | 48;
                if (z) {
                    i9 = 8388613;
                }
                layoutParams2.gravity = i9 | 48;
                if (stagedSplitBounds.initiatedFromSeascape) {
                    view.setTranslationX((float) (i7 - i));
                    view2.setTranslationX((float) i7);
                } else {
                    int i10 = i7 + i8;
                    view.setTranslationX((float) (i10 - i));
                    view2.setTranslationX((float) i10);
                }
            } else {
                layoutParams.gravity = (z ? 8388611 : 8388613) | 48;
                if (!z) {
                    i9 = 8388613;
                }
                layoutParams2.gravity = i9 | 48;
                if (!stagedSplitBounds.initiatedFromSeascape) {
                    int i11 = -i7;
                    view.setTranslationX((float) i11);
                    view2.setTranslationX((float) (i11 + i));
                } else {
                    int i12 = (-i7) - i8;
                    view.setTranslationX((float) i12);
                    view2.setTranslationX((float) (i12 + i));
                }
            }
        } else {
            layoutParams.gravity = 49;
            float f2 = ((float) i) / 2.0f;
            view.setTranslationX(-f2);
            layoutParams2.gravity = 49;
            view2.setTranslationX(f2);
        }
        view.setTranslationY(0.0f);
        view2.setTranslationY(0.0f);
        view.setLayoutParams(layoutParams);
        view2.setLayoutParams(layoutParams2);
    }

    public int getDefaultSplitPosition(DeviceProfile deviceProfile) {
        if (deviceProfile.isTablet) {
            return deviceProfile.isLandscape ? 1 : 0;
        }
        throw new IllegalStateException("Default position available only for large screens");
    }

    public Pair<FloatProperty, FloatProperty> getSplitSelectTaskOffset(FloatProperty floatProperty, FloatProperty floatProperty2, DeviceProfile deviceProfile) {
        if (deviceProfile.isLandscape) {
            return new Pair<>(floatProperty, floatProperty2);
        }
        return new Pair<>(floatProperty2, floatProperty);
    }
}
