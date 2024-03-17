package com.android.launcher3.taskbar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.taskbar.TaskbarDragLayerController;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.views.BaseDragLayer;
import com.android.systemui.shared.system.ViewTreeObserverWrapper;

public class TaskbarDragLayer extends BaseDragLayer<TaskbarActivityContext> {
    private final TaskbarBackgroundRenderer mBackgroundRenderer;
    private TaskbarDragLayerController.TaskbarDragLayerCallbacks mControllerCallbacks;
    private float mTaskbarBackgroundOffset;
    private final ViewTreeObserverWrapper.OnComputeInsetsListener mTaskbarInsetsComputer;

    /* access modifiers changed from: protected */
    public boolean canFindActiveController() {
        return true;
    }

    public TaskbarDragLayer(Context context) {
        this(context, (AttributeSet) null);
    }

    public TaskbarDragLayer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TaskbarDragLayer(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public TaskbarDragLayer(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, 1);
        this.mTaskbarInsetsComputer = new ViewTreeObserverWrapper.OnComputeInsetsListener() {
            public final void onComputeInsets(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
                TaskbarDragLayer.this.onComputeTaskbarInsets(insetsInfo);
            }
        };
        TaskbarBackgroundRenderer taskbarBackgroundRenderer = new TaskbarBackgroundRenderer((TaskbarActivityContext) this.mActivity);
        this.mBackgroundRenderer = taskbarBackgroundRenderer;
        taskbarBackgroundRenderer.getPaint().setAlpha(0);
    }

    public void init(TaskbarDragLayerController.TaskbarDragLayerCallbacks taskbarDragLayerCallbacks) {
        this.mControllerCallbacks = taskbarDragLayerCallbacks;
        recreateControllers();
    }

    public void recreateControllers() {
        this.mControllers = this.mControllerCallbacks.getTouchControllers();
    }

    /* access modifiers changed from: private */
    public void onComputeTaskbarInsets(ViewTreeObserverWrapper.InsetsInfo insetsInfo) {
        TaskbarDragLayerController.TaskbarDragLayerCallbacks taskbarDragLayerCallbacks = this.mControllerCallbacks;
        if (taskbarDragLayerCallbacks != null) {
            taskbarDragLayerCallbacks.updateInsetsTouchability(insetsInfo);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        ViewTreeObserverWrapper.removeOnComputeInsetsListener(this.mTaskbarInsetsComputer);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserverWrapper.addOnComputeInsetsListener(getViewTreeObserver(), this.mTaskbarInsetsComputer);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        TaskbarDragLayerController.TaskbarDragLayerCallbacks taskbarDragLayerCallbacks = this.mControllerCallbacks;
        if (taskbarDragLayerCallbacks != null) {
            taskbarDragLayerCallbacks.onDragLayerViewRemoved();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mBackgroundRenderer.setBackgroundHeight(((float) this.mControllerCallbacks.getTaskbarBackgroundHeight()) * (1.0f - this.mTaskbarBackgroundOffset));
        this.mBackgroundRenderer.draw(canvas);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void setTaskbarBackgroundAlpha(float f) {
        this.mBackgroundRenderer.getPaint().setAlpha((int) (f * 255.0f));
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void setTaskbarBackgroundOffset(float f) {
        this.mTaskbarBackgroundOffset = f;
        invalidate();
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        TestLogging.recordMotionEvent(TestProtocol.SEQUENCE_MAIN, "Touch event", motionEvent);
        return super.dispatchTouchEvent(motionEvent);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        AbstractFloatingView topOpenView;
        if (keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 4 || (topOpenView = AbstractFloatingView.getTopOpenView((ActivityContext) this.mActivity)) == null || !topOpenView.onBackPressed()) {
            return super.dispatchKeyEvent(keyEvent);
        }
        return true;
    }
}
