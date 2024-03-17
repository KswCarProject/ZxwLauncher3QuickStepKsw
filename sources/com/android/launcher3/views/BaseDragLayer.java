package com.android.launcher3.views;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.util.window.RefreshRateTracker;
import com.android.launcher3.views.ActivityContext;
import java.io.PrintWriter;
import java.util.ArrayList;

public abstract class BaseDragLayer<T extends Context & ActivityContext> extends InsettableFrameLayout {
    public static final Property<LayoutParams, Integer> LAYOUT_X = new Property<LayoutParams, Integer>(Integer.TYPE, "x") {
        public Integer get(LayoutParams layoutParams) {
            return Integer.valueOf(layoutParams.x);
        }

        public void set(LayoutParams layoutParams, Integer num) {
            layoutParams.x = num.intValue();
        }
    };
    public static final Property<LayoutParams, Integer> LAYOUT_Y = new Property<LayoutParams, Integer>(Integer.TYPE, "y") {
        public Integer get(LayoutParams layoutParams) {
            return Integer.valueOf(layoutParams.y);
        }

        public void set(LayoutParams layoutParams, Integer num) {
            layoutParams.y = num.intValue();
        }
    };
    private static final int TOUCH_DISPATCHING_FROM_PROXY = 4;
    private static final int TOUCH_DISPATCHING_FROM_VIEW = 1;
    private static final int TOUCH_DISPATCHING_FROM_VIEW_GESTURE_REGION = 2;
    private static final int TOUCH_DISPATCHING_TO_VIEW_IN_PROGRESS = 8;
    protected TouchController mActiveController;
    /* access modifiers changed from: protected */
    public final T mActivity;
    protected TouchController[] mControllers;
    protected final Rect mHitRect = new Rect();
    private boolean mIsBMWID8 = false;
    private final MultiValueAlpha mMultiValueAlpha;
    protected TouchController mProxyTouchController;
    @ViewDebug.ExportedProperty(category = "launcher")
    private final RectF mSystemGestureRegion = new RectF();
    protected final float[] mTmpRectPoints = new float[4];
    protected final float[] mTmpXY = new float[2];
    private TouchCompleteListener mTouchCompleteListener;
    private int mTouchDispatchState = 0;
    private final WallpaperManager mWallpaperManager;

    public interface TouchCompleteListener {
        void onTouchComplete();
    }

    public abstract void recreateControllers();

    public void setmIsBMWID8(boolean z) {
        this.mIsBMWID8 = z;
    }

    public BaseDragLayer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mActivity = ActivityContext.lookupContext(context);
        this.mMultiValueAlpha = new MultiValueAlpha(this, i);
        this.mWallpaperManager = (WallpaperManager) context.getSystemService(WallpaperManager.class);
    }

    public boolean isEventOverView(View view, MotionEvent motionEvent) {
        getDescendantRectRelativeToSelf(view, this.mHitRect);
        return this.mHitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    public boolean isEventOverView(View view, MotionEvent motionEvent, View view2) {
        int[] iArr = {(int) motionEvent.getX(), (int) motionEvent.getY()};
        getDescendantCoordRelativeToSelf(view2, iArr);
        getDescendantRectRelativeToSelf(view, this.mHitRect);
        return this.mHitRect.contains(iArr[0], iArr[1]);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 1 || action == 3) {
            TouchCompleteListener touchCompleteListener = this.mTouchCompleteListener;
            if (touchCompleteListener != null) {
                touchCompleteListener.onTouchComplete();
            }
            this.mTouchCompleteListener = null;
        } else if (action == 0) {
            ((ActivityContext) this.mActivity).finishAutoCancelActionMode();
        }
        return findActiveController(motionEvent);
    }

    private boolean isEventInLauncher(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if ((!this.mIsBMWID8 || !"0".equals(System.getProperty("CheckInRecent"))) && x >= this.mSystemGestureRegion.left && x < ((float) getWidth()) - this.mSystemGestureRegion.right && y >= this.mSystemGestureRegion.top && y < ((float) getHeight()) - this.mSystemGestureRegion.bottom) {
            return true;
        }
        return false;
    }

    private TouchController findControllerToHandleTouch(MotionEvent motionEvent) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView((ActivityContext) this.mActivity);
        if (topOpenView != null && ((isEventInLauncher(motionEvent) || topOpenView.canInterceptEventsInSystemGestureRegion()) && topOpenView.onControllerInterceptTouchEvent(motionEvent))) {
            return topOpenView;
        }
        for (TouchController touchController : this.mControllers) {
            if (touchController.onControllerInterceptTouchEvent(motionEvent)) {
                return touchController;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean findActiveController(MotionEvent motionEvent) {
        this.mActiveController = null;
        if (canFindActiveController()) {
            this.mActiveController = findControllerToHandleTouch(motionEvent);
        }
        return this.mActiveController != null;
    }

    /* access modifiers changed from: protected */
    public boolean canFindActiveController() {
        return (this.mTouchDispatchState & 6) == 0;
    }

    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        AbstractFloatingView topOpenViewWithType = AbstractFloatingView.getTopOpenViewWithType((ActivityContext) this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE);
        if (topOpenViewWithType == null) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        if (view == topOpenViewWithType) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        return false;
    }

    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
        AbstractFloatingView topOpenViewWithType = AbstractFloatingView.getTopOpenViewWithType((ActivityContext) this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE);
        if (topOpenViewWithType != null) {
            addAccessibleChildToList(topOpenViewWithType, arrayList);
        } else {
            super.addChildrenForAccessibility(arrayList);
        }
    }

    /* access modifiers changed from: protected */
    public void addAccessibleChildToList(View view, ArrayList<View> arrayList) {
        if (view.isImportantForAccessibility()) {
            arrayList.add(view);
        } else {
            view.addChildrenForAccessibility(arrayList);
        }
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (view instanceof AbstractFloatingView) {
            AbstractFloatingView abstractFloatingView = (AbstractFloatingView) view;
            if (abstractFloatingView.isOpen()) {
                postDelayed(new Runnable() {
                    public final void run() {
                        AbstractFloatingView.this.close(false);
                    }
                }, (long) RefreshRateTracker.getSingleFrameMs(getContext()));
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 1 || action == 3) {
            TouchCompleteListener touchCompleteListener = this.mTouchCompleteListener;
            if (touchCompleteListener != null) {
                touchCompleteListener.onTouchComplete();
            }
            this.mTouchCompleteListener = null;
        }
        TouchController touchController = this.mActiveController;
        if (touchController != null) {
            return touchController.onControllerTouchEvent(motionEvent);
        }
        return findActiveController(motionEvent);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            if ((this.mTouchDispatchState & 8) != 0) {
                int action2 = motionEvent.getAction();
                motionEvent.setAction(3);
                super.dispatchTouchEvent(motionEvent);
                motionEvent.setAction(action2);
            }
            this.mTouchDispatchState |= 9;
            if (isEventInLauncher(motionEvent)) {
                this.mTouchDispatchState &= -3;
            } else {
                this.mTouchDispatchState |= 2;
            }
        } else if (action == 1 || action == 3) {
            int i = this.mTouchDispatchState & -3;
            this.mTouchDispatchState = i;
            int i2 = i & -2;
            this.mTouchDispatchState = i2;
            this.mTouchDispatchState = i2 & -9;
        }
        super.dispatchTouchEvent(motionEvent);
        return true;
    }

    public boolean proxyTouchEvent(MotionEvent motionEvent, boolean z) {
        boolean z2;
        int actionMasked = motionEvent.getActionMasked();
        int i = this.mTouchDispatchState;
        boolean z3 = false;
        boolean z4 = (i & 1) != 0;
        if (z && !z4 && (actionMasked == 0 || (i & 8) != 0)) {
            this.mTouchDispatchState = i | 8;
            super.dispatchTouchEvent(motionEvent);
            if (actionMasked == 1 || actionMasked == 3) {
                int i2 = this.mTouchDispatchState & -9;
                this.mTouchDispatchState = i2;
                this.mTouchDispatchState = i2 & -5;
            }
            return true;
        }
        TouchController touchController = this.mProxyTouchController;
        if (touchController != null) {
            z2 = touchController.onControllerTouchEvent(motionEvent);
        } else {
            if (actionMasked == 0) {
                if (!z4 || this.mActiveController == null) {
                    this.mTouchDispatchState = i | 4;
                } else {
                    this.mTouchDispatchState = i & -5;
                }
            }
            if ((this.mTouchDispatchState & 4) != 0) {
                this.mProxyTouchController = findControllerToHandleTouch(motionEvent);
            }
            if (this.mProxyTouchController != null) {
                z3 = true;
            }
            z2 = z3;
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.mProxyTouchController = null;
            this.mTouchDispatchState &= -5;
        }
        return z2;
    }

    public float getDescendantRectRelativeToSelf(View view, Rect rect) {
        float[] fArr = this.mTmpRectPoints;
        fArr[0] = 0.0f;
        fArr[1] = 0.0f;
        fArr[2] = (float) view.getWidth();
        this.mTmpRectPoints[3] = (float) view.getHeight();
        float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf(view, this.mTmpRectPoints);
        float[] fArr2 = this.mTmpRectPoints;
        rect.left = Math.round(Math.min(fArr2[0], fArr2[2]));
        float[] fArr3 = this.mTmpRectPoints;
        rect.top = Math.round(Math.min(fArr3[1], fArr3[3]));
        float[] fArr4 = this.mTmpRectPoints;
        rect.right = Math.round(Math.max(fArr4[0], fArr4[2]));
        float[] fArr5 = this.mTmpRectPoints;
        rect.bottom = Math.round(Math.max(fArr5[1], fArr5[3]));
        return descendantCoordRelativeToSelf;
    }

    public float getLocationInDragLayer(View view, int[] iArr) {
        iArr[0] = 0;
        iArr[1] = 0;
        return getDescendantCoordRelativeToSelf(view, iArr);
    }

    public float getDescendantCoordRelativeToSelf(View view, int[] iArr) {
        float[] fArr = this.mTmpXY;
        fArr[0] = (float) iArr[0];
        fArr[1] = (float) iArr[1];
        float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf(view, fArr);
        Utilities.roundArray(this.mTmpXY, iArr);
        return descendantCoordRelativeToSelf;
    }

    public float getDescendantCoordRelativeToSelf(View view, float[] fArr) {
        return getDescendantCoordRelativeToSelf(view, fArr, false);
    }

    public float getDescendantCoordRelativeToSelf(View view, float[] fArr, boolean z) {
        return Utilities.getDescendantCoordRelativeToAncestor(view, this, fArr, z);
    }

    public void mapRectInSelfToDescendant(View view, Rect rect) {
        Utilities.mapRectInSelfToDescendant(view, this, rect);
    }

    public void mapCoordInSelfToDescendant(View view, float[] fArr) {
        Utilities.mapCoordInSelfToDescendant(view, this, fArr);
    }

    public void mapCoordInSelfToDescendant(View view, int[] iArr) {
        float[] fArr = this.mTmpXY;
        fArr[0] = (float) iArr[0];
        fArr[1] = (float) iArr[1];
        Utilities.mapCoordInSelfToDescendant(view, this, fArr);
        Utilities.roundArray(this.mTmpXY, iArr);
    }

    public void getViewRectRelativeToSelf(View view, Rect rect) {
        int[] viewLocationRelativeToSelf = getViewLocationRelativeToSelf(view);
        rect.set(viewLocationRelativeToSelf[0], viewLocationRelativeToSelf[1], viewLocationRelativeToSelf[0] + view.getMeasuredWidth(), viewLocationRelativeToSelf[1] + view.getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public int[] getViewLocationRelativeToSelf(View view) {
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        view.getLocationInWindow(iArr);
        iArr[0] = iArr[0] - i;
        iArr[1] = iArr[1] - i2;
        return iArr;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView((ActivityContext) this.mActivity);
        if (topOpenView != null) {
            return topOpenView.requestFocus(i, rect);
        }
        return super.onRequestFocusInDescendants(i, rect);
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView((ActivityContext) this.mActivity);
        if (topOpenView != null) {
            topOpenView.addFocusables(arrayList, i);
        } else {
            super.addFocusables(arrayList, i, i2);
        }
    }

    public void setTouchCompleteListener(TouchCompleteListener touchCompleteListener) {
        this.mTouchCompleteListener = touchCompleteListener;
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    public MultiValueAlpha.AlphaProperty getAlphaProperty(int i) {
        return this.mMultiValueAlpha.getProperty(i);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "DragLayer:");
        if (this.mActiveController != null) {
            printWriter.println(str + "\tactiveController: " + this.mActiveController);
            this.mActiveController.dump(str + "\t", printWriter);
        }
        printWriter.println(str + "\tdragLayerAlpha : " + this.mMultiValueAlpha);
    }

    public static class LayoutParams extends InsettableFrameLayout.LayoutParams {
        public boolean customPosition = false;
        public int x;
        public int y;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                LayoutParams layoutParams2 = (LayoutParams) layoutParams;
                if (layoutParams2.customPosition) {
                    childAt.layout(layoutParams2.x, layoutParams2.y, layoutParams2.x + layoutParams2.width, layoutParams2.y + layoutParams2.height);
                }
            }
        }
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets windowInsets) {
        if (Utilities.ATLEAST_Q) {
            Insets mandatorySystemGestureInsets = windowInsets.getMandatorySystemGestureInsets();
            int i = mandatorySystemGestureInsets.bottom;
            DeviceProfile deviceProfile = ((ActivityContext) this.mActivity).getDeviceProfile();
            if (deviceProfile.isTaskbarPresent) {
                i = Math.max(0, i - deviceProfile.taskbarSize);
            }
            this.mSystemGestureRegion.set((float) mandatorySystemGestureInsets.left, (float) mandatorySystemGestureInsets.top, (float) mandatorySystemGestureInsets.right, (float) i);
        }
        return super.dispatchApplyWindowInsets(windowInsets);
    }
}
