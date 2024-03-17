package com.android.quickstep.fallback;

import android.graphics.PointF;
import android.view.MotionEvent;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.util.NavBarPosition;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;

public class FallbackNavBarTouchController implements TouchController, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private final RecentsActivity mActivity;
    private final TriggerSwipeUpTouchTracker mTriggerSwipeUpTracker;

    public void onSwipeUpCancelled() {
    }

    public FallbackNavBarTouchController(RecentsActivity recentsActivity) {
        this.mActivity = recentsActivity;
        DisplayController.NavigationMode navigationMode = DisplayController.getNavigationMode(recentsActivity);
        if (navigationMode == DisplayController.NavigationMode.NO_BUTTON) {
            this.mTriggerSwipeUpTracker = new TriggerSwipeUpTouchTracker(recentsActivity, true, new NavBarPosition(navigationMode, DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(recentsActivity).getInfo()), (Runnable) null, this);
            return;
        }
        this.mTriggerSwipeUpTracker = null;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        if (!((motionEvent.getEdgeFlags() & 256) != 0) || this.mTriggerSwipeUpTracker == null) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            this.mTriggerSwipeUpTracker.init();
        }
        onControllerTouchEvent(motionEvent);
        return this.mTriggerSwipeUpTracker.interceptedTouch();
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        TriggerSwipeUpTouchTracker triggerSwipeUpTouchTracker = this.mTriggerSwipeUpTracker;
        if (triggerSwipeUpTouchTracker == null) {
            return false;
        }
        triggerSwipeUpTouchTracker.onMotionEvent(motionEvent);
        return true;
    }

    public void onSwipeUp(boolean z, PointF pointF) {
        ((FallbackRecentsView) this.mActivity.getOverviewPanel()).startHome();
    }
}
