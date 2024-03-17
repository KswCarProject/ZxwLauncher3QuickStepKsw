package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.NavigableAppWidgetHostView;

public class ShortcutAndWidgetContainer extends ViewGroup implements FolderIcon.FolderIconParent {
    static final String TAG = "ShortcutAndWidgetContainer";
    private final ActivityContext mActivity;
    private Point mBorderSpace;
    private int mCellHeight;
    private int mCellWidth;
    private final int mContainerType;
    private int mCountX;
    private int mCountY;
    private boolean mInvertIfRtl = false;
    private final Rect mTempRect = new Rect();
    private final int[] mTmpCellXY = new int[2];
    private final WallpaperManager mWallpaperManager;

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public ShortcutAndWidgetContainer(Context context, int i) {
        super(context);
        this.mActivity = (ActivityContext) ActivityContext.lookupContext(context);
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mContainerType = i;
    }

    public void setCellDimensions(int i, int i2, int i3, int i4, Point point) {
        this.mCellWidth = i;
        this.mCellHeight = i2;
        this.mCountX = i3;
        this.mCountY = i4;
        this.mBorderSpace = point;
    }

    public View getChildAt(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
            if (layoutParams.cellX <= i && i < layoutParams.cellX + layoutParams.cellHSpan && layoutParams.cellY <= i2 && i2 < layoutParams.cellY + layoutParams.cellVSpan) {
                return childAt;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                measureChild(childAt);
            }
        }
    }

    public void setupLp(View view) {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        if (view instanceof NavigableAppWidgetHostView) {
            DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
            ((NavigableAppWidgetHostView) view).getWidgetInset(deviceProfile, this.mTempRect);
            layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, this.mCountY, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y, this.mBorderSpace, this.mTempRect);
            return;
        }
        layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, this.mCountY, this.mBorderSpace, (Rect) null);
    }

    public void setInvertIfRtl(boolean z) {
        this.mInvertIfRtl = z;
    }

    public int getCellContentHeight() {
        return Math.min(getMeasuredHeight(), this.mActivity.getDeviceProfile().getCellContentHeight(this.mContainerType));
    }

    public void measureChild(View view) {
        int i;
        int i2;
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (view instanceof NavigableAppWidgetHostView) {
            ((NavigableAppWidgetHostView) view).getWidgetInset(deviceProfile, this.mTempRect);
            layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, this.mCountY, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y, this.mBorderSpace, this.mTempRect);
        } else {
            layoutParams.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, this.mCountY, this.mBorderSpace, (Rect) null);
            int cellContentHeight = getCellContentHeight();
            if (!deviceProfile.isScalableGrid || this.mContainerType != 0) {
                i = (int) Math.max(0.0f, ((float) (layoutParams.height - cellContentHeight)) / 2.0f);
            } else {
                i = deviceProfile.cellYPaddingPx;
            }
            boolean z = true;
            if ((deviceProfile.cellLayoutBorderSpacePx.x <= 0 || this.mContainerType != 0) && ((deviceProfile.folderCellLayoutBorderSpacePx.x <= 0 || this.mContainerType != 2) && (deviceProfile.hotseatBorderSpace <= 0 || this.mContainerType != 1))) {
                z = false;
            }
            if (z) {
                i2 = 0;
            } else if (this.mContainerType == 0) {
                i2 = deviceProfile.workspaceCellPaddingXPx;
            } else {
                i2 = (int) (((float) deviceProfile.edgeMarginPx) / 2.0f);
            }
            view.setPadding(i2, i, i2, 0);
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(layoutParams.height, BasicMeasure.EXACTLY));
    }

    public boolean invertLayoutHorizontally() {
        return this.mInvertIfRtl && Utilities.isRtl(getResources());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) childAt.getLayoutParams();
                layoutChild(childAt);
            }
        }
    }

    public void layoutChild(View view) {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        if (view instanceof NavigableAppWidgetHostView) {
            NavigableAppWidgetHostView navigableAppWidgetHostView = (NavigableAppWidgetHostView) view;
            DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
            float f = deviceProfile.appWidgetScale.x;
            float f2 = deviceProfile.appWidgetScale.y;
            navigableAppWidgetHostView.setScaleToFit(Math.min(f, f2));
            navigableAppWidgetHostView.setTranslationForCentering((-(((float) layoutParams.width) - (((float) layoutParams.width) * f))) / 2.0f, (-(((float) layoutParams.height) - (((float) layoutParams.height) * f2))) / 2.0f);
        }
        int i = layoutParams.x;
        int i2 = layoutParams.y;
        view.layout(i, i2, layoutParams.width + i, layoutParams.height + i2);
        if (layoutParams.dropped) {
            layoutParams.dropped = false;
            int[] iArr = this.mTmpCellXY;
            getLocationOnScreen(iArr);
            this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.home.drop", iArr[0] + i + (layoutParams.width / 2), iArr[1] + i2 + (layoutParams.height / 2), 0, (Bundle) null);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && getAlpha() == 0.0f) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        if (view != null) {
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            requestRectangleOnScreen(rect);
        }
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    public void drawFolderLeaveBehindForIcon(FolderIcon folderIcon) {
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) folderIcon.getLayoutParams();
        layoutParams.canReorder = false;
        if (this.mContainerType == 1) {
            ((CellLayout) getParent()).setFolderLeaveBehindCell(layoutParams.cellX, layoutParams.cellY);
        }
    }

    public void clearFolderLeaveBehind(FolderIcon folderIcon) {
        ((CellLayout.LayoutParams) folderIcon.getLayoutParams()).canReorder = true;
        if (this.mContainerType == 1) {
            ((CellLayout) getParent()).clearFolderLeaveBehind();
        }
    }
}
