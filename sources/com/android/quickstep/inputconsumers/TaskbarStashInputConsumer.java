package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.taskbar.TaskbarActivityContext;
import com.android.quickstep.InputConsumer;
import com.android.systemui.shared.system.InputMonitorCompat;

public class TaskbarStashInputConsumer extends DelegateInputConsumer {
    private boolean mCanceledUnstashHint;
    private float mDownX;
    private float mDownY;
    private final GestureDetector mLongPressDetector;
    private final float mScreenWidth;
    private final float mSquaredTouchSlop;
    private final TaskbarActivityContext mTaskbarActivityContext;
    private final float mUnstashArea;

    public TaskbarStashInputConsumer(Context context, InputConsumer inputConsumer, InputMonitorCompat inputMonitorCompat, TaskbarActivityContext taskbarActivityContext) {
        super(inputConsumer, inputMonitorCompat);
        this.mTaskbarActivityContext = taskbarActivityContext;
        this.mSquaredTouchSlop = Utilities.squaredTouchSlop(context);
        this.mScreenWidth = (float) taskbarActivityContext.getDeviceProfile().widthPx;
        this.mUnstashArea = (float) context.getResources().getDimensionPixelSize(R.dimen.taskbar_unstash_input_area);
        this.mLongPressDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent motionEvent) {
                TaskbarStashInputConsumer.this.onLongPressDetected(motionEvent);
            }
        });
    }

    public int getType() {
        return this.mDelegate.getType() | 4096;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        this.mLongPressDetector.onTouchEvent(motionEvent);
        if (this.mState != 1) {
            this.mDelegate.onMotionEvent(motionEvent);
            if (this.mTaskbarActivityContext != null) {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                int action = motionEvent.getAction();
                if (action != 0) {
                    if (action != 1) {
                        if (action != 2) {
                            if (action != 3) {
                                return;
                            }
                        } else if (!this.mCanceledUnstashHint && Utilities.squaredHypot(this.mDownX - rawX, this.mDownY - rawY) > this.mSquaredTouchSlop) {
                            this.mTaskbarActivityContext.startTaskbarUnstashHint(false);
                            this.mCanceledUnstashHint = true;
                            return;
                        } else {
                            return;
                        }
                    }
                    if (!this.mCanceledUnstashHint) {
                        this.mTaskbarActivityContext.startTaskbarUnstashHint(false);
                    }
                } else if (isInArea(rawX)) {
                    this.mDownX = rawX;
                    this.mDownY = rawY;
                    this.mTaskbarActivityContext.startTaskbarUnstashHint(true);
                    this.mCanceledUnstashHint = false;
                }
            }
        }
    }

    private boolean isInArea(float f) {
        return Math.abs((this.mScreenWidth / 2.0f) - f) < this.mUnstashArea / 2.0f;
    }

    /* access modifiers changed from: private */
    public void onLongPressDetected(MotionEvent motionEvent) {
        if (this.mTaskbarActivityContext != null && isInArea(motionEvent.getRawX()) && this.mTaskbarActivityContext.onLongPressToUnstashTaskbar()) {
            setActive(motionEvent);
        }
    }
}
