package com.android.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.FrameLayout;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import java.util.function.Consumer;

public class Hotseat extends CellLayout implements Insettable {
    public static final float QSB_CENTER_FACTOR = 0.325f;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mHasVerticalHotseat;
    private Consumer<Boolean> mOnVisibilityAggregatedCallback;
    private final View mQsb;
    private final int mQsbHeight;
    private boolean mSendTouchToWorkspace;
    private Workspace<?> mWorkspace;

    public Hotseat(Context context) {
        this(context, (AttributeSet) null);
    }

    public Hotseat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Hotseat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        View inflate = LayoutInflater.from(context).inflate(R.layout.search_container_hotseat, this, false);
        this.mQsb = inflate;
        addView(inflate);
        this.mQsbHeight = getResources().getDimensionPixelSize(R.dimen.qsb_widget_height);
    }

    public int getCellXFromOrder(int i) {
        if (this.mHasVerticalHotseat) {
            return 0;
        }
        return i;
    }

    public int getCellYFromOrder(int i) {
        if (this.mHasVerticalHotseat) {
            return getCountY() - (i + 1);
        }
        return 0;
    }

    public void resetLayout(boolean z) {
        removeAllViewsInLayout();
        this.mHasVerticalHotseat = z;
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        resetCellSize(deviceProfile);
        if (z) {
            setGridSize(1, deviceProfile.numShownHotseatIcons);
        } else {
            setGridSize(deviceProfile.numShownHotseatIcons, 1);
        }
    }

    public void setInsets(Rect rect) {
        int i;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        if (deviceProfile.isVerticalBarLayout()) {
            this.mQsb.setVisibility(8);
            layoutParams.height = -1;
            if (deviceProfile.isSeascape()) {
                layoutParams.gravity = 3;
                layoutParams.width = deviceProfile.hotseatBarSizePx + rect.left;
            } else {
                layoutParams.gravity = 5;
                layoutParams.width = deviceProfile.hotseatBarSizePx + rect.right;
            }
        } else {
            this.mQsb.setVisibility(0);
            layoutParams.gravity = 80;
            layoutParams.width = -1;
            if (deviceProfile.isTaskbarPresent) {
                i = deviceProfile.workspacePadding.bottom;
            } else {
                i = deviceProfile.hotseatBarSizePx + rect.bottom;
            }
            layoutParams.height = i;
        }
        Rect hotseatLayoutPadding = deviceProfile.getHotseatLayoutPadding(getContext());
        setPadding(hotseatLayoutPadding.left, hotseatLayoutPadding.top, hotseatLayoutPadding.right, hotseatLayoutPadding.bottom);
        setLayoutParams(layoutParams);
        InsettableFrameLayout.dispatchInsets(this, rect);
    }

    public void setWorkspace(Workspace<?> workspace) {
        this.mWorkspace = workspace;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int measuredHeight = getMeasuredHeight() - getPaddingBottom();
        if (this.mWorkspace == null || motionEvent.getY() > ((float) measuredHeight)) {
            return false;
        }
        boolean onInterceptTouchEvent = this.mWorkspace.onInterceptTouchEvent(motionEvent);
        this.mSendTouchToWorkspace = onInterceptTouchEvent;
        return onInterceptTouchEvent;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mSendTouchToWorkspace) {
            return false;
        }
        int action = motionEvent.getAction() & 255;
        if (action == 1 || action == 3) {
            this.mSendTouchToWorkspace = false;
        }
        return this.mWorkspace.onTouchEvent(motionEvent);
    }

    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        Consumer<Boolean> consumer = this.mOnVisibilityAggregatedCallback;
        if (consumer != null) {
            consumer.accept(Boolean.valueOf(z));
        }
    }

    public void setOnVisibilityAggregatedCallback(Consumer<Boolean> consumer) {
        this.mOnVisibilityAggregatedCallback = consumer;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mQsb.measure(View.MeasureSpec.makeMeasureSpec(this.mActivity.getDeviceProfile().qsbWidth, BasicMeasure.EXACTLY), View.MeasureSpec.makeMeasureSpec(this.mQsbHeight, BasicMeasure.EXACTLY));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        super.onLayout(z, i, i2, i3, i4);
        int measuredWidth = this.mQsb.getMeasuredWidth();
        if (this.mActivity.getDeviceProfile().isQsbInline) {
            int i6 = this.mActivity.getDeviceProfile().hotseatBorderSpace;
            if (Utilities.isRtl(getResources())) {
                i5 = (i3 - getPaddingRight()) + i6;
            } else {
                i5 = ((i + getPaddingLeft()) - measuredWidth) - i6;
            }
        } else {
            i5 = ((i3 - i) - measuredWidth) / 2;
        }
        int qsbOffsetY = (i4 - i2) - this.mActivity.getDeviceProfile().getQsbOffsetY();
        int i7 = qsbOffsetY - this.mQsbHeight;
        this.mQsb.layout(i5, i7, measuredWidth + i5, qsbOffsetY);
    }

    public void setIconsAlpha(float f) {
        getShortcutsAndWidgets().setAlpha(f);
    }

    public float getIconsAlpha() {
        return getShortcutsAndWidgets().getAlpha();
    }

    public View getQsb() {
        return this.mQsb;
    }
}
