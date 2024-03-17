package com.android.launcher3.dragndrop;

import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.widget.PendingItemDragHelper;
import java.util.UUID;

public abstract class BaseItemDragListener implements View.OnDragListener, DragSource, DragOptions.PreDragCondition, ActivityTracker.SchedulerCallback<Launcher> {
    public static final String EXTRA_PIN_ITEM_DRAG_LISTENER = "pin_item_drag_listener";
    private static final String MIME_TYPE_PREFIX = "com.android.launcher3.drag_and_drop/";
    private static final String TAG = "BaseItemDragListener";
    private DragController mDragController;
    private final String mId = UUID.randomUUID().toString();
    protected Launcher mLauncher;
    private final int mPreviewBitmapWidth;
    private final Rect mPreviewRect;
    private final int mPreviewViewWidth;

    /* access modifiers changed from: protected */
    public abstract PendingItemDragHelper createDragHelper();

    public BaseItemDragListener(Rect rect, int i, int i2) {
        this.mPreviewRect = rect;
        this.mPreviewBitmapWidth = i;
        this.mPreviewViewWidth = i2;
    }

    public String getMimeType() {
        return MIME_TYPE_PREFIX + this.mId;
    }

    public boolean init(Launcher launcher, boolean z) {
        AbstractFloatingView.closeAllOpenViews(launcher, z);
        launcher.getStateManager().goToState(LauncherState.NORMAL, z);
        launcher.getDragLayer().setOnDragListener(this);
        launcher.getRotationHelper().setStateHandlerRequest(2);
        this.mLauncher = launcher;
        this.mDragController = launcher.getDragController();
        return false;
    }

    public boolean onDrag(View view, DragEvent dragEvent) {
        if (this.mLauncher == null || this.mDragController == null) {
            postCleanup();
            return false;
        } else if (dragEvent.getAction() != 1 && this.mDragController.isDragging()) {
            return this.mDragController.onDragEvent(dragEvent);
        } else {
            if (onDragStart(dragEvent)) {
                return true;
            }
            postCleanup();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent dragEvent) {
        return onDragStart(dragEvent, this);
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent dragEvent, DragOptions.PreDragCondition preDragCondition) {
        ClipDescription clipDescription = dragEvent.getClipDescription();
        if (clipDescription == null || !clipDescription.hasMimeType(getMimeType())) {
            Log.e(TAG, "Someone started a dragAndDrop before us.");
            return false;
        }
        Point point = new Point((int) dragEvent.getX(), (int) dragEvent.getY());
        DragOptions dragOptions = new DragOptions();
        dragOptions.simulatedDndStartPoint = point;
        dragOptions.preDragCondition = preDragCondition;
        createDragHelper().startDrag(new Rect(this.mPreviewRect), this.mPreviewBitmapWidth, this.mPreviewViewWidth, point, this, dragOptions);
        return true;
    }

    public boolean shouldStartDrag(double d) {
        return !this.mLauncher.isWorkspaceLocked();
    }

    public void onPreDragStart(DropTarget.DragObject dragObject) {
        this.mLauncher.getDragLayer().setAlpha(1.0f);
        dragObject.dragView.setAlpha(0.5f);
    }

    public void onPreDragEnd(DropTarget.DragObject dragObject, boolean z) {
        if (z) {
            dragObject.dragView.setAlpha(1.0f);
        }
    }

    public void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
        postCleanup();
    }

    /* access modifiers changed from: protected */
    public void postCleanup() {
        if (this.mLauncher != null) {
            Intent intent = new Intent(this.mLauncher.getIntent());
            intent.removeExtra(EXTRA_PIN_ITEM_DRAG_LISTENER);
            this.mLauncher.setIntent(intent);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public final void run() {
                BaseItemDragListener.this.removeListener();
            }
        });
    }

    public void removeListener() {
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            launcher.getRotationHelper().setStateHandlerRequest(0);
            this.mLauncher.getDragLayer().setOnDragListener((View.OnDragListener) null);
        }
    }
}
