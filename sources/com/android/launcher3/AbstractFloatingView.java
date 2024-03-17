package com.android.launcher3;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AbstractFloatingView extends LinearLayout implements TouchController {
    public static final int TYPE_ACCESSIBLE = 523455;
    public static final int TYPE_ACTION_POPUP = 2;
    public static final int TYPE_ADD_TO_HOME_CONFIRMATION = 262144;
    public static final int TYPE_ALL = 524287;
    public static final int TYPE_ALL_APPS_EDU = 512;
    public static final int TYPE_DISCOVERY_BOUNCE = 64;
    public static final int TYPE_DRAG_DROP_POPUP = 1024;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_HIDE_BACK_BUTTON = 488;
    public static final int TYPE_ICON_SURFACE = 8192;
    public static final int TYPE_LISTENER = 256;
    public static final int TYPE_ON_BOARD_POPUP = 32;
    public static final int TYPE_OPTIONS_POPUP = 4096;
    public static final int TYPE_OPTIONS_POPUP_DIALOG = 262144;
    public static final int TYPE_PIN_WIDGET_FROM_EXTERNAL_POPUP = 16384;
    public static final int TYPE_REBIND_SAFE = 500340;
    public static final int TYPE_SNACKBAR = 128;
    public static final int TYPE_STATUS_BAR_SWIPE_DOWN_DISALLOW = 3196;
    public static final int TYPE_TASKBAR_ALL_APPS = 131072;
    public static final int TYPE_TASKBAR_EDUCATION_DIALOG = 65536;
    public static final int TYPE_TASK_MENU = 2048;
    public static final int TYPE_WIDGETS_BOTTOM_SHEET = 4;
    public static final int TYPE_WIDGETS_EDUCATION_DIALOG = 32768;
    public static final int TYPE_WIDGETS_FULL_SHEET = 16;
    public static final int TYPE_WIDGET_RESIZE_FRAME = 8;
    protected boolean mIsOpen;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FloatingViewType {
    }

    public void addHintCloseAnim(float f, Interpolator interpolator, PendingAnimation pendingAnimation) {
    }

    public boolean canInterceptEventsInSystemGestureRegion() {
        return false;
    }

    /* access modifiers changed from: protected */
    public View getAccessibilityInitialFocusView() {
        return this;
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return null;
    }

    /* access modifiers changed from: protected */
    public abstract void handleClose(boolean z);

    /* access modifiers changed from: protected */
    public abstract boolean isOfType(int i);

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return super.generateDefaultLayoutParams();
    }

    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return super.generateLayoutParams(attributeSet);
    }

    /* access modifiers changed from: protected */
    public /* bridge */ /* synthetic */ ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return super.generateLayoutParams(layoutParams);
    }

    public /* bridge */ /* synthetic */ ViewOverlay getOverlay() {
        return super.getOverlay();
    }

    public AbstractFloatingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AbstractFloatingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public final void close(boolean z) {
        handleClose(z & ValueAnimator.areAnimatorsEnabled());
        this.mIsOpen = false;
    }

    public final boolean isOpen() {
        return this.mIsOpen;
    }

    public boolean onBackPressed() {
        close(true);
        return true;
    }

    /* access modifiers changed from: protected */
    public void announceAccessibilityChanges() {
        Pair<View, String> accessibilityTarget = getAccessibilityTarget();
        if (accessibilityTarget != null && AccessibilityManagerCompat.isAccessibilityEnabled(getContext())) {
            AccessibilityManagerCompat.sendCustomAccessibilityEvent((View) accessibilityTarget.first, 32, (String) accessibilityTarget.second);
            if (this.mIsOpen) {
                getAccessibilityInitialFocusView().performAccessibilityAction(64, (Bundle) null);
            }
            ((ActivityContext) ActivityContext.lookupContext(getContext())).getDragLayer().sendAccessibilityEvent(2048);
        }
    }

    public static <T extends AbstractFloatingView> T getOpenView(ActivityContext activityContext, int i) {
        return getView(activityContext, i, true);
    }

    public static boolean hasOpenView(ActivityContext activityContext, int i) {
        return getOpenView(activityContext, i) != null;
    }

    public static <T extends AbstractFloatingView> T getAnyView(ActivityContext activityContext, int i) {
        return getView(activityContext, i, false);
    }

    private static <T extends AbstractFloatingView> T getView(ActivityContext activityContext, int i, boolean z) {
        BaseDragLayer dragLayer = activityContext.getDragLayer();
        if (dragLayer == null) {
            return null;
        }
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            T childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof AbstractFloatingView) {
                T t = (AbstractFloatingView) childAt;
                if (t.isOfType(i) && (!z || t.isOpen())) {
                    return t;
                }
            }
        }
        return null;
    }

    public static void closeOpenContainer(ActivityContext activityContext, int i) {
        AbstractFloatingView openView = getOpenView(activityContext, i);
        if (openView != null) {
            openView.close(true);
        }
    }

    public static void closeOpenViews(ActivityContext activityContext, boolean z, int i) {
        BaseDragLayer dragLayer = activityContext.getDragLayer();
        for (int childCount = dragLayer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = dragLayer.getChildAt(childCount);
            if (childAt instanceof AbstractFloatingView) {
                AbstractFloatingView abstractFloatingView = (AbstractFloatingView) childAt;
                if (abstractFloatingView.isOfType(i)) {
                    abstractFloatingView.close(z);
                }
            }
        }
    }

    public static void closeAllOpenViews(ActivityContext activityContext, boolean z) {
        closeOpenViews(activityContext, z, TYPE_ALL);
        activityContext.finishAutoCancelActionMode();
    }

    public static void closeAllOpenViews(ActivityContext activityContext) {
        closeAllOpenViews(activityContext, true);
    }

    public static void closeAllOpenViewsExcept(ActivityContext activityContext, boolean z, int i) {
        closeOpenViews(activityContext, z, (~i) & TYPE_ALL);
        activityContext.finishAutoCancelActionMode();
    }

    public static void closeAllOpenViewsExcept(ActivityContext activityContext, int i) {
        closeAllOpenViewsExcept(activityContext, true, i);
    }

    public static AbstractFloatingView getTopOpenView(ActivityContext activityContext) {
        return getTopOpenViewWithType(activityContext, TYPE_ALL);
    }

    public static AbstractFloatingView getTopOpenViewWithType(ActivityContext activityContext, int i) {
        return getOpenView(activityContext, i);
    }
}
