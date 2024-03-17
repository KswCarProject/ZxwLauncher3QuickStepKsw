package com.android.quickstep.inputconsumers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Utilities;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;
import com.android.systemui.shared.system.InputMonitorCompat;

public class OverviewWithoutFocusInputConsumer implements InputConsumer, TriggerSwipeUpTouchTracker.OnSwipeUpListener {
    private final Context mContext;
    private final GestureState mGestureState;
    private final InputMonitorCompat mInputMonitor;
    private final TriggerSwipeUpTouchTracker mTriggerSwipeUpTracker;

    public int getType() {
        return 128;
    }

    public void onSwipeUpCancelled() {
    }

    public OverviewWithoutFocusInputConsumer(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, GestureState gestureState, InputMonitorCompat inputMonitorCompat, boolean z) {
        this.mContext = context;
        this.mGestureState = gestureState;
        this.mInputMonitor = inputMonitorCompat;
        this.mTriggerSwipeUpTracker = new TriggerSwipeUpTouchTracker(context, z, recentsAnimationDeviceState.getNavBarPosition(), new Runnable() {
            public final void run() {
                OverviewWithoutFocusInputConsumer.this.onInterceptTouch();
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
            this.mContext.startActivity(this.mGestureState.getHomeIntent());
        } catch (ActivityNotFoundException | NullPointerException | SecurityException unused) {
            this.mContext.startActivity(Utilities.createHomeIntent());
        }
        ActiveGestureLog.INSTANCE.addLog("startQuickstep");
        BaseActivity fromContext = BaseDraggingActivity.fromContext(this.mContext);
        GestureState gestureState = this.mGestureState;
        fromContext.getStatsLogManager().logger().withSrcState(1).withDstState((gestureState == null || gestureState.getEndTarget() == null) ? 2 : this.mGestureState.getEndTarget().containerType).withContainerInfo((LauncherAtom.ContainerInfo) LauncherAtom.ContainerInfo.newBuilder().setWorkspace(LauncherAtom.WorkspaceContainer.newBuilder().setPageIndex(-1)).build()).log(StatsLogManager.LauncherEvent.LAUNCHER_HOME_GESTURE);
    }
}
