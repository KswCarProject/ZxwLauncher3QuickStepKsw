package com.android.launcher3.widget;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.widget.ScrollView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.AddItemActivity;
import com.android.launcher3.views.AbstractSlideInView;

public class AddItemWidgetsBottomSheet extends AbstractSlideInView<AddItemActivity> implements View.OnApplyWindowInsetsListener {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    private int mContentHorizontalMarginInPx;
    private final Rect mInsets;
    private ScrollView mWidgetPreviewScrollView;

    /* access modifiers changed from: protected */
    public boolean isOfType(int i) {
        return (i & 16384) != 0;
    }

    public AddItemWidgetsBottomSheet(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AddItemWidgetsBottomSheet(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mInsets = new Rect();
        this.mContentHorizontalMarginInPx = getResources().getDimensionPixelSize(R.dimen.widget_list_horizontal_margin);
    }

    public void show() {
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this);
        }
        attachToContainer();
        setOnApplyWindowInsetsListener(this);
        animateOpen();
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mNoIntercept = false;
            if (getPopupContainer().isEventOverView(this.mWidgetPreviewScrollView, motionEvent) && this.mWidgetPreviewScrollView.getScrollY() > 0) {
                this.mNoIntercept = true;
            }
        }
        return super.onControllerInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i4 - i2;
        int measuredWidth = this.mContent.getMeasuredWidth();
        int i6 = (((((i3 - i) - measuredWidth) - this.mInsets.left) - this.mInsets.right) / 2) + this.mInsets.left;
        this.mContent.layout(i6, i5 - this.mContent.getMeasuredHeight(), measuredWidth + i6, i5);
        setTranslationShift(this.mTranslationShift);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int max;
        DeviceProfile deviceProfile = ((AddItemActivity) this.mActivityContext).getDeviceProfile();
        if (deviceProfile.isTablet) {
            max = Math.max(deviceProfile.allAppsLeftRightMargin * 2, (this.mInsets.left + this.mInsets.right) * 2);
        } else if (this.mInsets.bottom > 0) {
            max = this.mInsets.left + this.mInsets.right;
        } else {
            Rect rect = deviceProfile.workspacePadding;
            max = Math.max(rect.left + rect.right, (this.mInsets.left + this.mInsets.right) * 2);
        }
        measureChildWithMargins(this.mContent, i, max, i2, deviceProfile.bottomSheetTopPadding);
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (ViewGroup) findViewById(R.id.add_item_bottom_sheet_content);
        this.mWidgetPreviewScrollView = (ScrollView) findViewById(R.id.widget_preview_scroll_view);
    }

    private void animateOpen() {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean z) {
        handleClose(z, 200);
    }

    /* access modifiers changed from: protected */
    public int getScrimColor(Context context) {
        return context.getResources().getColor(R.color.widgets_picker_scrim);
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        if (Utilities.ATLEAST_R) {
            Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
            this.mInsets.set(insets.left, insets.top, insets.right, insets.bottom);
        } else {
            this.mInsets.set(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        this.mContent.setPadding(this.mContent.getPaddingStart(), this.mContent.getPaddingTop(), this.mContent.getPaddingEnd(), this.mInsets.bottom);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.widget_list_horizontal_margin);
        if (dimensionPixelSize != this.mContentHorizontalMarginInPx) {
            setContentHorizontalMargin(findViewById(R.id.widget_appName), dimensionPixelSize);
            setContentHorizontalMargin(findViewById(R.id.widget_drag_instruction), dimensionPixelSize);
            setContentHorizontalMargin(findViewById(R.id.widget_cell), dimensionPixelSize);
            setContentHorizontalMargin(findViewById(R.id.actions_container), dimensionPixelSize);
            this.mContentHorizontalMarginInPx = dimensionPixelSize;
        }
        return windowInsets;
    }

    private static void setContentHorizontalMargin(View view, int i) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.setMarginStart(i);
        marginLayoutParams.setMarginEnd(i);
    }
}
