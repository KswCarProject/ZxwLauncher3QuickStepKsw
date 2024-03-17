package com.android.quickstep.inputconsumers;

import android.view.MotionEvent;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.TaskAnimationManager;

public class ResetGestureInputConsumer implements InputConsumer {
    private final TaskAnimationManager mTaskAnimationManager;

    public int getType() {
        return 256;
    }

    public ResetGestureInputConsumer(TaskAnimationManager taskAnimationManager) {
        this.mTaskAnimationManager = taskAnimationManager;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            this.mTaskAnimationManager.finishRunningRecentsAnimation(false);
        }
    }
}
