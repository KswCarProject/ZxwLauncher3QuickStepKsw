package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTargetBar;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.anim.SpringProperty;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.graphics.Scrim;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import java.util.ArrayList;

public class DragLayer extends BaseDragLayer<Launcher> {
    private static final int ALPHA_CHANNEL_COUNT = 1;
    public static final int ALPHA_INDEX_OVERLAY = 0;
    public static final int ANIMATION_END_DISAPPEAR = 0;
    public static final int ANIMATION_END_REMAIN_VISIBLE = 2;
    private int mChildCountOnLastUpdate = -1;
    private DragController mDragController;
    private Animator mDropAnim = null;
    private DragView mDropView = null;
    private final ViewGroupFocusHelper mFocusIndicatorHelper;
    private boolean mHoverPointClosesFolder = false;
    private int mTopViewIndex;
    private Scrim mWorkspaceDragScrim;

    public boolean onHoverEvent(MotionEvent motionEvent) {
        return false;
    }

    public DragLayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 1);
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        this.mFocusIndicatorHelper = new ViewGroupFocusHelper(this);
    }

    public void setup(DragController dragController, Workspace<?> workspace) {
        this.mDragController = dragController;
        recreateControllers();
        this.mWorkspaceDragScrim = new Scrim(this);
    }

    public void recreateControllers() {
        this.mControllers = ((Launcher) this.mActivity).createTouchControllers();
    }

    public ViewGroupFocusHelper getFocusIndicatorHelper() {
        return this.mFocusIndicatorHelper;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mDragController.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
    }

    private boolean isEventOverAccessibleDropTargetBar(MotionEvent motionEvent) {
        return isInAccessibleDrag() && isEventOverView(((Launcher) this.mActivity).getDropTargetBar(), motionEvent);
    }

    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        if (!(this.mActivity == null || ((Launcher) this.mActivity).getWorkspace() == null)) {
            AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView((ActivityContext) this.mActivity);
            if ((topOpenView instanceof Folder) && ((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
                Folder folder = (Folder) topOpenView;
                int action = motionEvent.getAction();
                if (action == 7) {
                    boolean z = isEventOverView(topOpenView, motionEvent) || isEventOverAccessibleDropTargetBar(motionEvent);
                    if (!z && !this.mHoverPointClosesFolder) {
                        sendTapOutsideFolderAccessibilityEvent(folder.isEditingName());
                        this.mHoverPointClosesFolder = true;
                        return true;
                    } else if (!z) {
                        return true;
                    } else {
                        this.mHoverPointClosesFolder = false;
                    }
                } else if (action == 9) {
                    if (!(isEventOverView(topOpenView, motionEvent) || isEventOverAccessibleDropTargetBar(motionEvent))) {
                        sendTapOutsideFolderAccessibilityEvent(folder.isEditingName());
                        this.mHoverPointClosesFolder = true;
                        return true;
                    }
                    this.mHoverPointClosesFolder = false;
                }
            }
        }
        return false;
    }

    private void sendTapOutsideFolderAccessibilityEvent(boolean z) {
        AccessibilityManagerCompat.sendCustomAccessibilityEvent(this, 8, getContext().getString(z ? R.string.folder_tap_to_rename : R.string.folder_tap_to_close));
    }

    private boolean isInAccessibleDrag() {
        return ((Launcher) this.mActivity).getAccessibilityDelegate().isInAccessibleDrag();
    }

    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        if (!isInAccessibleDrag() || !(view instanceof DropTargetBar)) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        return true;
    }

    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
        AbstractFloatingView topOpenViewWithType = AbstractFloatingView.getTopOpenViewWithType((ActivityContext) this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE);
        if (topOpenViewWithType != null) {
            addAccessibleChildToList(topOpenViewWithType, arrayList);
            if (isInAccessibleDrag()) {
                addAccessibleChildToList(((Launcher) this.mActivity).getDropTargetBar(), arrayList);
                return;
            }
            return;
        }
        super.addChildrenForAccessibility(arrayList);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        motionEvent.offsetLocation(getTranslationX(), 0.0f);
        try {
            return super.dispatchTouchEvent(motionEvent);
        } finally {
            motionEvent.offsetLocation(-getTranslationX(), 0.0f);
        }
    }

    public void animateViewIntoPosition(DragView dragView, int[] iArr, float f, float f2, float f3, int i, Runnable runnable, int i2) {
        animateViewIntoPosition(dragView, iArr[0], iArr[1], f, f2, f3, runnable, i, i2, (View) null);
    }

    public void animateViewIntoPosition(DragView dragView, View view, View view2) {
        animateViewIntoPosition(dragView, view, -1, view2);
    }

    public void animateViewIntoPosition(DragView dragView, View view, int i, View view2) {
        float f;
        int i2;
        int i3;
        View view3 = view;
        ShortcutAndWidgetContainer shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) view.getParent();
        CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) view.getLayoutParams();
        shortcutAndWidgetContainer.measureChild(view3);
        shortcutAndWidgetContainer.layoutChild(view3);
        float scaleX = view.getScaleX();
        float f2 = 1.0f - scaleX;
        float[] fArr = {((float) layoutParams.x) + ((((float) view.getMeasuredWidth()) * f2) / 2.0f), ((float) layoutParams.y) + ((((float) view.getMeasuredHeight()) * f2) / 2.0f)};
        float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf((View) view.getParent(), fArr) * scaleX;
        int round = Math.round(fArr[0]);
        int round2 = Math.round(fArr[1]);
        if (view3 instanceof DraggableView) {
            Rect rect = new Rect();
            ((DraggableView) view3).getWorkspaceVisualDragBounds(rect);
            float width = ((((float) rect.width()) * 1.0f) / ((float) (dragView.getMeasuredWidth() - dragView.getBlurSizeOutline()))) * descendantCoordRelativeToSelf;
            float f3 = 1.0f - width;
            float blurSizeOutline = (((float) rect.left) * descendantCoordRelativeToSelf) - ((((float) dragView.getBlurSizeOutline()) * width) / 2.0f);
            i2 = (int) (((float) round2) + (((descendantCoordRelativeToSelf * ((float) rect.top)) - ((((float) dragView.getBlurSizeOutline()) * width) / 2.0f)) - ((((float) dragView.getMeasuredHeight()) * f3) / 2.0f)));
            i3 = (int) (((float) round) + (blurSizeOutline - ((((float) dragView.getMeasuredWidth()) * f3) / 2.0f)));
            f = width;
        } else {
            i2 = round2;
            f = descendantCoordRelativeToSelf;
            i3 = round;
        }
        view3.setVisibility(4);
        animateViewIntoPosition(dragView, i3, i2, 1.0f, f, f, new Runnable(view3) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.setVisibility(0);
            }
        }, 0, i, view2);
    }

    public void animateViewIntoPosition(DragView dragView, int i, int i2, float f, float f2, float f3, Runnable runnable, int i3, int i4, View view) {
        DragView dragView2 = dragView;
        animateView(dragView2, new Rect(i, i2, dragView.getMeasuredWidth() + i, dragView.getMeasuredHeight() + i2), f, f2, f3, i4, (Interpolator) null, runnable, i3, view);
    }

    public void animateView(DragView dragView, Rect rect, float f, float f2, float f3, int i, Interpolator interpolator, Runnable runnable, int i2, View view) {
        int i3;
        DragView dragView2 = dragView;
        Rect rect2 = rect;
        View view2 = view;
        dragView.cancelAnimation();
        dragView.requestLayout();
        int[] viewLocationRelativeToSelf = getViewLocationRelativeToSelf(dragView);
        float hypot = (float) Math.hypot((double) (rect2.left - viewLocationRelativeToSelf[0]), (double) (rect2.top - viewLocationRelativeToSelf[1]));
        Resources resources = getResources();
        float integer = (float) resources.getInteger(R.integer.config_dropAnimMaxDist);
        if (i < 0) {
            int integer2 = resources.getInteger(R.integer.config_dropAnimMaxDuration);
            if (hypot < integer) {
                integer2 = (int) (((float) integer2) * Interpolators.DEACCEL_1_5.getInterpolation(hypot / integer));
            }
            i3 = Math.max(integer2, resources.getInteger(R.integer.config_dropAnimMinDuration));
        } else {
            i3 = i;
        }
        Interpolator interpolator2 = interpolator == null ? Interpolators.DEACCEL_1_5 : interpolator;
        PendingAnimation pendingAnimation = new PendingAnimation((long) i3);
        pendingAnimation.add(ObjectAnimator.ofFloat(dragView, View.SCALE_X, new float[]{f2}), interpolator2, SpringProperty.DEFAULT);
        pendingAnimation.add(ObjectAnimator.ofFloat(dragView, View.SCALE_Y, new float[]{f3}), interpolator2, SpringProperty.DEFAULT);
        float f4 = f;
        pendingAnimation.setViewAlpha(dragView, f, interpolator2);
        pendingAnimation.setFloat(dragView, LauncherAnimUtils.VIEW_TRANSLATE_Y, (float) rect2.top, interpolator2);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(dragView, LauncherAnimUtils.VIEW_TRANSLATE_X, new float[]{(float) rect2.left});
        if (view2 != null) {
            ofFloat.setEvaluator(new TypeEvaluator(view2, view.getScrollX()) {
                public final /* synthetic */ View f$0;
                public final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final Object evaluate(float f, Object obj, Object obj2) {
                    return Float.valueOf(Utilities.mapRange(f, ((Float) obj).floatValue(), ((Float) obj2).floatValue()) + (this.f$0.getScaleX() * ((float) (this.f$1 - this.f$0.getScrollX()))));
                }
            });
        }
        pendingAnimation.add(ofFloat, interpolator2, SpringProperty.DEFAULT);
        if (runnable != null) {
            pendingAnimation.addListener(AnimatorListeners.forEndCallback(runnable));
        }
        playDropAnimation(dragView, pendingAnimation.buildAnim(), i2);
    }

    public void playDropAnimation(DragView dragView, Animator animator, int i) {
        Animator animator2 = this.mDropAnim;
        if (animator2 != null) {
            animator2.cancel();
        }
        this.mDropView = dragView;
        this.mDropAnim = animator;
        animator.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
            public final void run() {
                DragLayer.this.lambda$playDropAnimation$2$DragLayer();
            }
        }));
        if (i == 0) {
            this.mDropAnim.addListener(AnimatorListeners.forEndCallback((Runnable) new Runnable() {
                public final void run() {
                    DragLayer.this.clearAnimatedView();
                }
            }));
        }
        this.mDropAnim.start();
    }

    public /* synthetic */ void lambda$playDropAnimation$2$DragLayer() {
        this.mDropAnim = null;
    }

    public void clearAnimatedView() {
        Animator animator = this.mDropAnim;
        if (animator != null) {
            animator.cancel();
        }
        this.mDropAnim = null;
        DragView dragView = this.mDropView;
        if (dragView != null) {
            this.mDragController.onDeferredEndDrag(dragView);
        }
        this.mDropView = null;
        invalidate();
    }

    public View getAnimatedView() {
        return this.mDropView;
    }

    public void onViewAdded(View view) {
        super.onViewAdded(view);
        updateChildIndices();
        ((Launcher) this.mActivity).onDragLayerHierarchyChanged();
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        updateChildIndices();
        ((Launcher) this.mActivity).onDragLayerHierarchyChanged();
    }

    public void bringChildToFront(View view) {
        super.bringChildToFront(view);
        updateChildIndices();
    }

    private void updateChildIndices() {
        this.mTopViewIndex = -1;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof DragView) {
                this.mTopViewIndex = i;
            }
        }
        this.mChildCountOnLastUpdate = childCount;
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int i, int i2) {
        if (this.mChildCountOnLastUpdate != i) {
            updateChildIndices();
        }
        int i3 = this.mTopViewIndex;
        if (i3 == -1) {
            return i2;
        }
        if (i2 == i - 1) {
            return i3;
        }
        return i2 < i3 ? i2 : i2 + 1;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mWorkspaceDragScrim.draw(canvas);
        this.mFocusIndicatorHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public Scrim getWorkspaceDragScrim() {
        return this.mWorkspaceDragScrim;
    }

    public void onOneHandedModeStateChanged(boolean z) {
        for (TouchController onOneHandedModeStateChanged : this.mControllers) {
            onOneHandedModeStateChanged.onOneHandedModeStateChanged(z);
        }
    }
}
