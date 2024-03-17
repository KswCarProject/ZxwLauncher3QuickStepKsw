package com.android.launcher3.dragndrop;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragDriver;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.ActivityContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class DragController<T extends ActivityContext> implements DragDriver.EventListener, TouchController {
    private static final int DEEP_PRESS_DISTANCE_FACTOR = 3;
    protected final T mActivity;
    private final int[] mCoordinatesTemp = new int[2];
    protected int mDistanceSinceScroll = 0;
    protected DragDriver mDragDriver = null;
    protected DropTarget.DragObject mDragObject;
    private final ArrayList<DropTarget> mDropTargets = new ArrayList<>();
    protected boolean mIsInPreDrag;
    protected DropTarget mLastDropTarget;
    protected final Point mLastTouch = new Point();
    private int mLastTouchClassification;
    private final ArrayList<DragListener> mListeners = new ArrayList<>();
    protected final Point mMotionDown = new Point();
    protected DragOptions mOptions;
    private final Rect mRectTemp = new Rect();
    protected final Point mTmpPoint = new Point();

    public interface DragListener {
        void onDragEnd();

        void onDragStart(DropTarget.DragObject dragObject, DragOptions dragOptions);
    }

    /* access modifiers changed from: protected */
    public boolean endWithFlingAnimation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract void exitDrag();

    /* access modifiers changed from: protected */
    public abstract DropTarget getDefaultDropTarget(int[] iArr);

    /* access modifiers changed from: protected */
    public abstract DragView startDrag(Drawable drawable, View view, DraggableView draggableView, int i, int i2, DragSource dragSource, ItemInfo itemInfo, Point point, Rect rect, float f, float f2, DragOptions dragOptions);

    public DragController(T t) {
        this.mActivity = t;
    }

    public DragView startDrag(Drawable drawable, DraggableView draggableView, int i, int i2, DragSource dragSource, ItemInfo itemInfo, Point point, Rect rect, float f, float f2, DragOptions dragOptions) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "4");
        }
        return startDrag(drawable, (View) null, draggableView, i, i2, dragSource, itemInfo, point, rect, f, f2, dragOptions);
    }

    public DragView startDrag(View view, DraggableView draggableView, int i, int i2, DragSource dragSource, ItemInfo itemInfo, Point point, Rect rect, float f, float f2, DragOptions dragOptions) {
        return startDrag((Drawable) null, view, draggableView, i, i2, dragSource, itemInfo, point, rect, f, f2, dragOptions);
    }

    /* access modifiers changed from: protected */
    public void callOnDragStart() {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.NO_DROP_TARGET, "6");
        }
        if (this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, true);
        }
        this.mIsInPreDrag = false;
        this.mDragObject.dragView.onDragStart();
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragStart(this.mDragObject, this.mOptions);
        }
    }

    public Optional<InstanceId> getLogInstanceId() {
        return Optional.ofNullable(this.mDragObject).map($$Lambda$DragController$BtnND8wC6yNPIKz3qPKzHAkZx1Q.INSTANCE);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mDragDriver != null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.mOptions;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isDragging() {
        /*
            r1 = this;
            com.android.launcher3.dragndrop.DragDriver r0 = r1.mDragDriver
            if (r0 != 0) goto L_0x000f
            com.android.launcher3.dragndrop.DragOptions r0 = r1.mOptions
            if (r0 == 0) goto L_0x000d
            boolean r0 = r0.isAccessibleDrag
            if (r0 == 0) goto L_0x000d
            goto L_0x000f
        L_0x000d:
            r0 = 0
            goto L_0x0010
        L_0x000f:
            r0 = 1
        L_0x0010:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dragndrop.DragController.isDragging():boolean");
    }

    public void cancelDrag() {
        if (isDragging()) {
            DropTarget dropTarget = this.mLastDropTarget;
            if (dropTarget != null) {
                dropTarget.onDragExit(this.mDragObject);
            }
            this.mDragObject.deferDragViewCleanupPostAnimation = false;
            this.mDragObject.cancelled = true;
            this.mDragObject.dragComplete = true;
            if (!this.mIsInPreDrag) {
                dispatchDropComplete((View) null, false);
            }
        }
        endDrag();
    }

    private void dispatchDropComplete(View view, boolean z) {
        if (!z) {
            exitDrag();
            this.mDragObject.deferDragViewCleanupPostAnimation = false;
        }
        this.mDragObject.dragSource.onDropCompleted(view, this.mDragObject, z);
    }

    public void onAppsRemoved(Predicate<ItemInfo> predicate) {
        DropTarget.DragObject dragObject = this.mDragObject;
        if (dragObject != null) {
            ItemInfo itemInfo = dragObject.dragInfo;
            if ((itemInfo instanceof WorkspaceItemInfo) && predicate.test(itemInfo)) {
                cancelDrag();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void endDrag() {
        if (isDragging()) {
            this.mDragDriver = null;
            boolean z = false;
            if (this.mDragObject.dragView != null) {
                z = this.mDragObject.deferDragViewCleanupPostAnimation;
                if (!z) {
                    this.mDragObject.dragView.remove();
                } else if (this.mIsInPreDrag) {
                    animateDragViewToOriginalPosition((Runnable) null, (View) null, -1);
                }
                this.mDragObject.dragView = null;
            }
            if (!z) {
                callOnDragEnd();
            }
        }
    }

    public void animateDragViewToOriginalPosition(final Runnable runnable, final View view, int i) {
        this.mDragObject.dragView.animateTo(this.mMotionDown.x, this.mMotionDown.y, new Runnable() {
            public void run() {
                View view = view;
                if (view != null) {
                    view.setVisibility(0);
                }
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }, i);
    }

    /* access modifiers changed from: protected */
    public void callOnDragEnd() {
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null) {
            this.mOptions.preDragCondition.onPreDragEnd(this.mDragObject, false);
        }
        this.mIsInPreDrag = false;
        this.mOptions = null;
        Iterator it = new ArrayList(this.mListeners).iterator();
        while (it.hasNext()) {
            ((DragListener) it.next()).onDragEnd();
        }
    }

    /* access modifiers changed from: package-private */
    public void onDeferredEndDrag(DragView dragView) {
        dragView.remove();
        if (this.mDragObject.deferDragViewCleanupPostAnimation) {
            callOnDragEnd();
        }
    }

    /* access modifiers changed from: protected */
    public Point getClampedDragLayerPos(float f, float f2) {
        this.mActivity.getDragLayer().getLocalVisibleRect(this.mRectTemp);
        this.mTmpPoint.x = (int) Math.max((float) this.mRectTemp.left, Math.min(f, (float) (this.mRectTemp.right - 1)));
        this.mTmpPoint.y = (int) Math.max((float) this.mRectTemp.top, Math.min(f2, (float) (this.mRectTemp.bottom - 1)));
        return this.mTmpPoint;
    }

    public void onDriverDragMove(float f, float f2) {
        Point clampedDragLayerPos = getClampedDragLayerPos(f, f2);
        handleMoveEvent(clampedDragLayerPos.x, clampedDragLayerPos.y);
    }

    public void onDriverDragExitWindow() {
        DropTarget dropTarget = this.mLastDropTarget;
        if (dropTarget != null) {
            dropTarget.onDragExit(this.mDragObject);
            this.mLastDropTarget = null;
        }
    }

    public void onDriverDragEnd(float f, float f2) {
        if (!endWithFlingAnimation()) {
            drop(findDropTarget((int) f, (int) f2, this.mCoordinatesTemp), (Runnable) null);
        }
        endDrag();
    }

    public void onDriverDragCancel() {
        cancelDrag();
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        DragOptions dragOptions = this.mOptions;
        if (dragOptions != null && dragOptions.isAccessibleDrag) {
            return false;
        }
        Point clampedDragLayerPos = getClampedDragLayerPos(getX(motionEvent), getY(motionEvent));
        this.mLastTouch.set(clampedDragLayerPos.x, clampedDragLayerPos.y);
        if (motionEvent.getAction() == 0) {
            this.mMotionDown.set(clampedDragLayerPos.x, clampedDragLayerPos.y);
        }
        if (Utilities.ATLEAST_Q) {
            this.mLastTouchClassification = motionEvent.getClassification();
        }
        DragDriver dragDriver = this.mDragDriver;
        if (dragDriver == null || !dragDriver.onInterceptTouchEvent(motionEvent)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public float getX(MotionEvent motionEvent) {
        return motionEvent.getX();
    }

    /* access modifiers changed from: protected */
    public float getY(MotionEvent motionEvent) {
        return motionEvent.getY();
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        DragDriver dragDriver = this.mDragDriver;
        return dragDriver != null && dragDriver.onTouchEvent(motionEvent);
    }

    public boolean onDragEvent(DragEvent dragEvent) {
        DragDriver dragDriver = this.mDragDriver;
        return dragDriver != null && dragDriver.onDragEvent(dragEvent);
    }

    /* access modifiers changed from: protected */
    public void handleMoveEvent(int i, int i2) {
        this.mDragObject.dragView.move(i, i2);
        int[] iArr = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget(i, i2, iArr);
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        checkTouchMove(findDropTarget);
        this.mDistanceSinceScroll = (int) (((double) this.mDistanceSinceScroll) + Math.hypot((double) (this.mLastTouch.x - i), (double) (this.mLastTouch.y - i2)));
        this.mLastTouch.set(i, i2);
        int i3 = this.mDistanceSinceScroll;
        if (Utilities.ATLEAST_Q && this.mLastTouchClassification == 2) {
            i3 /= 3;
        }
        if (this.mIsInPreDrag && this.mOptions.preDragCondition != null && this.mOptions.preDragCondition.shouldStartDrag((double) i3)) {
            callOnDragStart();
        }
    }

    public float getDistanceDragged() {
        return (float) this.mDistanceSinceScroll;
    }

    public void forceTouchMove() {
        int[] iArr = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget(this.mLastTouch.x, this.mLastTouch.y, iArr);
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        checkTouchMove(findDropTarget);
    }

    private void checkTouchMove(DropTarget dropTarget) {
        if (dropTarget != null) {
            DropTarget dropTarget2 = this.mLastDropTarget;
            if (dropTarget2 != dropTarget) {
                if (dropTarget2 != null) {
                    dropTarget2.onDragExit(this.mDragObject);
                }
                dropTarget.onDragEnter(this.mDragObject);
            }
            dropTarget.onDragOver(this.mDragObject);
        } else {
            DropTarget dropTarget3 = this.mLastDropTarget;
            if (dropTarget3 != null) {
                dropTarget3.onDragExit(this.mDragObject);
            }
        }
        this.mLastDropTarget = dropTarget;
    }

    public void completeAccessibleDrag(int[] iArr) {
        int[] iArr2 = this.mCoordinatesTemp;
        DropTarget findDropTarget = findDropTarget(iArr[0], iArr[1], iArr2);
        this.mDragObject.x = iArr2[0];
        this.mDragObject.y = iArr2[1];
        checkTouchMove(findDropTarget);
        findDropTarget.prepareAccessibilityDrop();
        drop(findDropTarget, (Runnable) null);
        endDrag();
    }

    /* access modifiers changed from: protected */
    public void drop(DropTarget dropTarget, Runnable runnable) {
        int[] iArr = this.mCoordinatesTemp;
        boolean z = false;
        this.mDragObject.x = iArr[0];
        this.mDragObject.y = iArr[1];
        DropTarget dropTarget2 = this.mLastDropTarget;
        if (dropTarget != dropTarget2) {
            if (dropTarget2 != null) {
                dropTarget2.onDragExit(this.mDragObject);
            }
            this.mLastDropTarget = dropTarget;
            if (dropTarget != null) {
                dropTarget.onDragEnter(this.mDragObject);
            }
        }
        this.mDragObject.dragComplete = true;
        if (!this.mIsInPreDrag) {
            if (dropTarget != null) {
                dropTarget.onDragExit(this.mDragObject);
                if (dropTarget.acceptDrop(this.mDragObject)) {
                    if (runnable != null) {
                        runnable.run();
                    } else {
                        dropTarget.onDrop(this.mDragObject, this.mOptions);
                    }
                    z = true;
                }
            }
            dispatchDropComplete(dropTarget instanceof View ? (View) dropTarget : null, z);
        } else if (dropTarget != null) {
            dropTarget.onDragExit(this.mDragObject);
        }
    }

    private DropTarget findDropTarget(int i, int i2, int[] iArr) {
        this.mDragObject.x = i;
        this.mDragObject.y = i2;
        Rect rect = this.mRectTemp;
        ArrayList<DropTarget> arrayList = this.mDropTargets;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            DropTarget dropTarget = arrayList.get(size);
            if (dropTarget.isDropEnabled()) {
                dropTarget.getHitRectRelativeToDragLayer(rect);
                if (rect.contains(i, i2)) {
                    iArr[0] = i;
                    iArr[1] = i2;
                    this.mActivity.getDragLayer().mapCoordInSelfToDescendant((View) dropTarget, iArr);
                    return dropTarget;
                }
            }
        }
        iArr[0] = i;
        iArr[1] = i2;
        return getDefaultDropTarget(iArr);
    }

    public void addDragListener(DragListener dragListener) {
        this.mListeners.add(dragListener);
    }

    public void removeDragListener(DragListener dragListener) {
        this.mListeners.remove(dragListener);
    }

    public void addDropTarget(DropTarget dropTarget) {
        this.mDropTargets.add(dropTarget);
    }

    public void removeDropTarget(DropTarget dropTarget) {
        this.mDropTargets.remove(dropTarget);
    }
}
