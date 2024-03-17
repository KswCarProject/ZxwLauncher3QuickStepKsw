package com.android.launcher3.dragndrop;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.Executors;
import java.util.Objects;
import java.util.function.Consumer;

public class LauncherDragController extends DragController<Launcher> {
    private static final boolean PROFILE_DRAWING_DURING_DRAG = false;
    private final FlingToDeleteHelper mFlingToDeleteHelper;

    public LauncherDragController(Launcher launcher) {
        super(launcher);
        this.mFlingToDeleteHelper = new FlingToDeleteHelper(launcher);
    }

    /* access modifiers changed from: protected */
    public DragView startDrag(Drawable drawable, View view, DraggableView draggableView, int i, int i2, DragSource dragSource, ItemInfo itemInfo, Point point, Rect rect, float f, float f2, DragOptions dragOptions) {
        int i3;
        int i4;
        LauncherDragView launcherDragView;
        DropTarget.DragObject dragObject;
        ItemInfo itemInfo2 = itemInfo;
        Point point2 = point;
        Rect rect2 = rect;
        DragOptions dragOptions2 = dragOptions;
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "5");
        }
        ((Launcher) this.mActivity).hideKeyboard();
        AbstractFloatingView.closeOpenViews(this.mActivity, false, 64);
        this.mOptions = dragOptions2;
        if (this.mOptions.simulatedDndStartPoint != null) {
            Point point3 = this.mLastTouch;
            Point point4 = this.mMotionDown;
            int i5 = this.mOptions.simulatedDndStartPoint.x;
            point4.x = i5;
            point3.x = i5;
            Point point5 = this.mLastTouch;
            Point point6 = this.mMotionDown;
            int i6 = this.mOptions.simulatedDndStartPoint.y;
            point6.y = i6;
            point5.y = i6;
        }
        int i7 = this.mMotionDown.x - i;
        int i8 = this.mMotionDown.y - i2;
        if (rect2 == null) {
            i3 = 0;
        } else {
            i3 = rect2.left;
        }
        if (rect2 == null) {
            i4 = 0;
        } else {
            i4 = rect2.top;
        }
        this.mLastDropTarget = null;
        this.mDragObject = new DropTarget.DragObject(((Launcher) this.mActivity).getApplicationContext());
        this.mDragObject.originalView = draggableView;
        this.mIsInPreDrag = this.mOptions.preDragCondition != null && !this.mOptions.preDragCondition.shouldStartDrag(0.0d);
        float dimensionPixelSize = this.mIsInPreDrag ? (float) ((Launcher) this.mActivity).getResources().getDimensionPixelSize(R.dimen.pre_drag_view_scale) : 0.0f;
        DropTarget.DragObject dragObject2 = this.mDragObject;
        if (drawable != null) {
            dragObject = dragObject2;
            launcherDragView = new LauncherDragView((Launcher) this.mActivity, drawable, i7, i8, f, f2, dimensionPixelSize);
        } else {
            dragObject = dragObject2;
            launcherDragView = new LauncherDragView((Launcher) this.mActivity, view, view.getMeasuredWidth(), view.getMeasuredHeight(), i7, i8, f, f2, dimensionPixelSize);
        }
        dragObject.dragView = launcherDragView;
        launcherDragView.setItemInfo(itemInfo2);
        this.mDragObject.dragComplete = false;
        this.mDragObject.xOffset = this.mMotionDown.x - (i + i3);
        this.mDragObject.yOffset = this.mMotionDown.y - (i2 + i4);
        DragOptions dragOptions3 = this.mOptions;
        FlingToDeleteHelper flingToDeleteHelper = this.mFlingToDeleteHelper;
        Objects.requireNonNull(flingToDeleteHelper);
        this.mDragDriver = DragDriver.create(this, dragOptions3, new Consumer() {
            public final void accept(Object obj) {
                FlingToDeleteHelper.this.recordMotionEvent((MotionEvent) obj);
            }
        });
        if (!this.mOptions.isAccessibleDrag) {
            this.mDragObject.stateAnnouncer = DragViewStateAnnouncer.createFor(launcherDragView);
        }
        this.mDragObject.dragSource = dragSource;
        this.mDragObject.dragInfo = itemInfo2;
        this.mDragObject.originalDragInfo = this.mDragObject.dragInfo.makeShallowCopy();
        if (point2 != null) {
            launcherDragView.setDragVisualizeOffset(new Point(point2));
        }
        if (rect2 != null) {
            launcherDragView.setDragRegion(new Rect(rect2));
        }
        ((Launcher) this.mActivity).getDragLayer().performHapticFeedback(0);
        launcherDragView.show(this.mLastTouch.x, this.mLastTouch.y);
        this.mDistanceSinceScroll = 0;
        if (!this.mIsInPreDrag) {
            callOnDragStart();
        } else if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragStart(this.mDragObject);
        }
        handleMoveEvent(this.mLastTouch.x, this.mLastTouch.y);
        if (!((Launcher) this.mActivity).isTouchInProgress() && dragOptions2.simulatedDndStartPoint == null) {
            Executors.MAIN_EXECUTOR.submit(new Runnable() {
                public final void run() {
                    LauncherDragController.this.cancelDrag();
                }
            });
        }
        return launcherDragView;
    }

    /* access modifiers changed from: protected */
    public void exitDrag() {
        ((Launcher) this.mActivity).getStateManager().goToState(LauncherState.NORMAL, 500);
    }

    /* access modifiers changed from: protected */
    public boolean endWithFlingAnimation() {
        Runnable flingAnimation = this.mFlingToDeleteHelper.getFlingAnimation(this.mDragObject, this.mOptions);
        if (flingAnimation == null) {
            return super.endWithFlingAnimation();
        }
        drop(this.mFlingToDeleteHelper.getDropTarget(), flingAnimation);
        return true;
    }

    /* access modifiers changed from: protected */
    public void endDrag() {
        super.endDrag();
        this.mFlingToDeleteHelper.releaseVelocityTracker();
    }

    /* access modifiers changed from: protected */
    public DropTarget getDefaultDropTarget(int[] iArr) {
        ((Launcher) this.mActivity).getDragLayer().mapCoordInSelfToDescendant((View) ((Launcher) this.mActivity).getWorkspace(), iArr);
        return ((Launcher) this.mActivity).getWorkspace();
    }
}
