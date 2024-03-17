package com.android.quickstep.inputconsumers;

import android.media.session.MediaSessionManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.BaseState;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.TaskUtils;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputMonitorCompat;

public class OverviewInputConsumer<S extends BaseState<S>, T extends StatefulActivity<S>> implements InputConsumer {
    private final T mActivity;
    private final BaseActivityInterface<?, T> mActivityInterface;
    private final InputMonitorCompat mInputMonitor;
    private final int[] mLocationOnScreen;
    private final boolean mStartingInActivityBounds;
    private final BaseDragLayer mTarget;
    private boolean mTargetHandledTouch;

    public int getType() {
        return 2;
    }

    public OverviewInputConsumer(GestureState gestureState, T t, InputMonitorCompat inputMonitorCompat, boolean z) {
        int[] iArr = new int[2];
        this.mLocationOnScreen = iArr;
        this.mActivity = t;
        this.mInputMonitor = inputMonitorCompat;
        this.mStartingInActivityBounds = z;
        this.mActivityInterface = gestureState.getActivityInterface();
        BaseDragLayer dragLayer = t.getDragLayer();
        this.mTarget = dragLayer;
        dragLayer.getLocationOnScreen(iArr);
    }

    public boolean allowInterceptByParent() {
        return !this.mTargetHandledTouch;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        int edgeFlags = motionEvent.getEdgeFlags();
        if (!this.mStartingInActivityBounds) {
            motionEvent.setEdgeFlags(edgeFlags | 256);
        }
        int[] iArr = this.mLocationOnScreen;
        motionEvent.offsetLocation((float) (-iArr[0]), (float) (-iArr[1]));
        boolean proxyTouchEvent = this.mTarget.proxyTouchEvent(motionEvent, this.mStartingInActivityBounds);
        int[] iArr2 = this.mLocationOnScreen;
        motionEvent.offsetLocation((float) iArr2[0], (float) iArr2[1]);
        motionEvent.setEdgeFlags(edgeFlags);
        if (!this.mTargetHandledTouch && proxyTouchEvent) {
            this.mTargetHandledTouch = true;
            if (!this.mStartingInActivityBounds) {
                this.mActivityInterface.closeOverlay();
                TaskUtils.closeSystemWindowsAsync(ActivityManagerWrapper.CLOSE_SYSTEM_WINDOWS_REASON_RECENTS);
                ActiveGestureLog.INSTANCE.addLog("startQuickstep");
            }
            if (this.mInputMonitor != null) {
                TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
                this.mInputMonitor.pilferPointers();
            }
        }
    }

    public void onHoverEvent(MotionEvent motionEvent) {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            this.mActivity.dispatchGenericMotionEvent(motionEvent);
        }
    }

    public void onKeyEvent(KeyEvent keyEvent) {
        if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get()) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 24 || keyCode == 25 || keyCode == 164) {
                ((MediaSessionManager) this.mActivity.getSystemService(MediaSessionManager.class)).dispatchVolumeKeyEventAsSystemService(keyEvent, Integer.MIN_VALUE);
            }
            this.mActivity.dispatchKeyEvent(keyEvent);
        }
    }
}
