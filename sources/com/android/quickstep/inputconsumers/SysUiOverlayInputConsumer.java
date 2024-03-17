package com.android.quickstep.inputconsumers;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;
import com.android.systemui.shared.system.InputMonitorCompat;

public class SysUiOverlayInputConsumer implements InputConsumer, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private static final String SYSTEM_DIALOG_REASON_GESTURE_NAV = "gestureNav";
    private static final String TAG = "SysUiOverlayInputConsumer";
    private final Context mContext;
    private final InputMonitorCompat mInputMonitor;
    private final TriggerSwipeUpTouchTracker mTriggerSwipeUpTracker;

    public int getType() {
        return 1024;
    }

    public void onSwipeUpCancelled() {
    }

    public SysUiOverlayInputConsumer(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, InputMonitorCompat inputMonitorCompat) {
        this.mContext = context;
        this.mInputMonitor = inputMonitorCompat;
        this.mTriggerSwipeUpTracker = new TriggerSwipeUpTouchTracker(context, true, recentsAnimationDeviceState.getNavBarPosition(), new Runnable() {
            public final void run() {
                SysUiOverlayInputConsumer.this.onInterceptTouch();
            }
        }, this);
    }

    public boolean allowInterceptByParent() {
        return !this.mTriggerSwipeUpTracker.interceptedTouch();
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        this.mTriggerSwipeUpTracker.onMotionEvent(motionEvent);
    }

    /* access modifiers changed from: private */
    public void onInterceptTouch() {
        if (this.mInputMonitor != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
            this.mInputMonitor.pilferPointers();
        }
    }

    public void onSwipeUp(boolean z, PointF pointF) {
        try {
            ActivityManager.getService().closeSystemDialogs(SYSTEM_DIALOG_REASON_GESTURE_NAV);
        } catch (RemoteException e) {
            Log.e(TAG, "Exception calling closeSystemDialogs " + e.getMessage());
        }
    }
}
