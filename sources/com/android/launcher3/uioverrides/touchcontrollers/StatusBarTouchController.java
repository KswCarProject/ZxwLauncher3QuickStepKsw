package com.android.launcher3.uioverrides.touchcontrollers;

import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.util.TouchController;
import com.android.quickstep.SystemUiProxy;
import java.io.PrintWriter;

public class StatusBarTouchController implements TouchController {
    private static final String TAG = "StatusBarController";
    private boolean mCanIntercept;
    private final SparseArray<PointF> mDownEvents = new SparseArray<>();
    private int mLastAction;
    private final Launcher mLauncher;
    private final SystemUiProxy mSystemUiProxy;
    private final float mTouchSlop;

    public StatusBarTouchController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(launcher);
        this.mTouchSlop = (float) (ViewConfiguration.get(launcher).getScaledTouchSlop() * 2);
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "mCanIntercept:" + this.mCanIntercept);
        printWriter.println(str + "mLastAction:" + MotionEvent.actionToString(this.mLastAction));
        printWriter.println(str + "mSysUiProxy available:" + SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).isActive());
    }

    private void dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mSystemUiProxy.isActive()) {
            this.mLastAction = motionEvent.getActionMasked();
            this.mSystemUiProxy.onStatusBarMotionEvent(motionEvent);
        }
    }

    public final boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(actionIndex);
        if (actionMasked == 0) {
            boolean canInterceptTouch = canInterceptTouch(motionEvent);
            this.mCanIntercept = canInterceptTouch;
            if (!canInterceptTouch) {
                return false;
            }
            this.mDownEvents.put(pointerId, new PointF(motionEvent.getX(), motionEvent.getY()));
        } else if (motionEvent.getActionMasked() == 5) {
            this.mDownEvents.put(pointerId, new PointF(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex)));
        }
        if (this.mCanIntercept && actionMasked == 2) {
            float y = motionEvent.getY(actionIndex) - this.mDownEvents.get(pointerId).y;
            float x = motionEvent.getX(actionIndex) - this.mDownEvents.get(pointerId).x;
            if (y > this.mTouchSlop && y > Math.abs(x) && motionEvent.getPointerCount() == 1) {
                motionEvent.setAction(0);
                dispatchTouchEvent(motionEvent);
                setWindowSlippery(true);
                return true;
            } else if (Math.abs(x) > this.mTouchSlop) {
                this.mCanIntercept = false;
            }
        }
        return false;
    }

    public final boolean onControllerTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 1 && action != 3) {
            return true;
        }
        dispatchTouchEvent(motionEvent);
        this.mLauncher.getStatsLogManager().logger().log(StatsLogManager.LauncherEvent.LAUNCHER_SWIPE_DOWN_WORKSPACE_NOTISHADE_OPEN);
        setWindowSlippery(false);
        return true;
    }

    private void setWindowSlippery(boolean z) {
        Window window = this.mLauncher.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (z) {
            attributes.flags |= 536870912;
        } else {
            attributes.flags &= -536870913;
        }
        window.setAttributes(attributes);
    }

    private boolean canInterceptTouch(MotionEvent motionEvent) {
        if (!this.mLauncher.isInState(LauncherState.NORMAL) || AbstractFloatingView.getTopOpenViewWithType(this.mLauncher, AbstractFloatingView.TYPE_STATUS_BAR_SWIPE_DOWN_DISALLOW) != null) {
            return false;
        }
        if (motionEvent.getY() > ((float) (this.mLauncher.getDragLayer().getHeight() - this.mLauncher.getDeviceProfile().getInsets().bottom))) {
            return false;
        }
        return SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).isActive();
    }
}
