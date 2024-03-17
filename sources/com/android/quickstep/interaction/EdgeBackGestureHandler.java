package com.android.quickstep.interaction;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemProperties;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.interaction.EdgeBackGesturePanel;

public class EdgeBackGestureHandler implements View.OnTouchListener {
    private static final int MAX_LONG_PRESS_TIMEOUT = SystemProperties.getInt("gestures.back_timeout", 250);
    private static final String TAG = "EdgeBackGestureHandler";
    private boolean mAllowGesture = false;
    private final EdgeBackGesturePanel.BackCallback mBackCallback = new EdgeBackGesturePanel.BackCallback() {
        public void triggerBack() {
            BackGestureResult backGestureResult;
            if (EdgeBackGestureHandler.this.mGestureCallback != null) {
                BackGestureAttemptCallback access$000 = EdgeBackGestureHandler.this.mGestureCallback;
                if (EdgeBackGestureHandler.this.mEdgeBackPanel.getIsLeftPanel()) {
                    backGestureResult = BackGestureResult.BACK_COMPLETED_FROM_LEFT;
                } else {
                    backGestureResult = BackGestureResult.BACK_COMPLETED_FROM_RIGHT;
                }
                access$000.onBackGestureAttempted(backGestureResult);
            }
        }

        public void cancelBack() {
            BackGestureResult backGestureResult;
            if (EdgeBackGestureHandler.this.mGestureCallback != null) {
                BackGestureAttemptCallback access$000 = EdgeBackGestureHandler.this.mGestureCallback;
                if (EdgeBackGestureHandler.this.mEdgeBackPanel.getIsLeftPanel()) {
                    backGestureResult = BackGestureResult.BACK_CANCELLED_FROM_LEFT;
                } else {
                    backGestureResult = BackGestureResult.BACK_CANCELLED_FROM_RIGHT;
                }
                access$000.onBackGestureAttempted(backGestureResult);
            }
        }
    };
    private final int mBottomGestureHeight;
    private final Context mContext;
    private BackGestureResult mDisallowedGestureReason;
    private final Point mDisplaySize = new Point();
    private final PointF mDownPoint = new PointF();
    /* access modifiers changed from: private */
    public EdgeBackGesturePanel mEdgeBackPanel;
    private final int mEdgeWidth;
    /* access modifiers changed from: private */
    public BackGestureAttemptCallback mGestureCallback;
    private boolean mIsEnabled;
    private int mLeftInset;
    private final int mLongPressTimeout;
    private int mRightInset;
    private boolean mThresholdCrossed = false;
    private final float mTouchSlop;

    interface BackGestureAttemptCallback {
        void onBackGestureAttempted(BackGestureResult backGestureResult);
    }

    enum BackGestureResult {
        UNKNOWN,
        BACK_COMPLETED_FROM_LEFT,
        BACK_COMPLETED_FROM_RIGHT,
        BACK_CANCELLED_FROM_LEFT,
        BACK_CANCELLED_FROM_RIGHT,
        BACK_NOT_STARTED_TOO_FAR_FROM_EDGE,
        BACK_NOT_STARTED_IN_NAV_BAR_REGION
    }

    EdgeBackGestureHandler(Context context) {
        Resources resources = context.getResources();
        this.mContext = context;
        this.mTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        this.mLongPressTimeout = Math.min(MAX_LONG_PRESS_TIMEOUT, ViewConfiguration.getLongPressTimeout());
        this.mBottomGestureHeight = ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, resources);
        int navbarSize = ResourceUtils.getNavbarSize("config_backGestureInset", resources);
        this.mEdgeWidth = navbarSize == 0 ? Utilities.dpToPx(18.0f) : navbarSize;
    }

    /* access modifiers changed from: package-private */
    public void setViewGroupParent(ViewGroup viewGroup) {
        this.mIsEnabled = viewGroup != null;
        EdgeBackGesturePanel edgeBackGesturePanel = this.mEdgeBackPanel;
        if (edgeBackGesturePanel != null) {
            edgeBackGesturePanel.onDestroy();
            this.mEdgeBackPanel = null;
        }
        if (this.mIsEnabled) {
            EdgeBackGesturePanel edgeBackGesturePanel2 = new EdgeBackGesturePanel(this.mContext, viewGroup, createLayoutParams());
            this.mEdgeBackPanel = edgeBackGesturePanel2;
            edgeBackGesturePanel2.setBackCallback(this.mBackCallback);
            Point point = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).getInfo().currentSize;
            this.mDisplaySize.set(point.x, point.y);
            this.mEdgeBackPanel.setDisplaySize(this.mDisplaySize);
        }
    }

    /* access modifiers changed from: package-private */
    public void registerBackGestureAttemptCallback(BackGestureAttemptCallback backGestureAttemptCallback) {
        this.mGestureCallback = backGestureAttemptCallback;
    }

    /* access modifiers changed from: package-private */
    public void unregisterBackGestureAttemptCallback() {
        this.mGestureCallback = null;
    }

    private ViewGroup.LayoutParams createLayoutParams() {
        Resources resources = this.mContext.getResources();
        return new ViewGroup.LayoutParams(ResourceUtils.getNavbarSize("navigation_edge_panel_width", resources), ResourceUtils.getNavbarSize("navigation_edge_panel_height", resources));
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.mIsEnabled) {
            return false;
        }
        onMotionEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean onInterceptTouch(MotionEvent motionEvent) {
        return isWithinTouchRegion((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    private boolean isWithinTouchRegion(int i, int i2) {
        if (i > this.mEdgeWidth + this.mLeftInset && i < (this.mDisplaySize.x - this.mEdgeWidth) - this.mRightInset) {
            this.mDisallowedGestureReason = BackGestureResult.BACK_NOT_STARTED_TOO_FAR_FROM_EDGE;
            return false;
        } else if (i2 < this.mDisplaySize.y - this.mBottomGestureHeight) {
            return true;
        } else {
            this.mDisallowedGestureReason = BackGestureResult.BACK_NOT_STARTED_IN_NAV_BAR_REGION;
            return false;
        }
    }

    private void cancelGesture(MotionEvent motionEvent) {
        this.mAllowGesture = false;
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setAction(3);
        this.mEdgeBackPanel.onMotionEvent(obtain);
        obtain.recycle();
    }

    private void onMotionEvent(MotionEvent motionEvent) {
        BackGestureAttemptCallback backGestureAttemptCallback;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            boolean z = motionEvent.getX() <= ((float) (this.mEdgeWidth + this.mLeftInset));
            this.mDisallowedGestureReason = BackGestureResult.UNKNOWN;
            this.mAllowGesture = isWithinTouchRegion((int) motionEvent.getX(), (int) motionEvent.getY());
            this.mDownPoint.set(motionEvent.getX(), motionEvent.getY());
            if (this.mAllowGesture) {
                this.mEdgeBackPanel.setIsLeftPanel(z);
                this.mEdgeBackPanel.onMotionEvent(motionEvent);
                this.mThresholdCrossed = false;
            }
        } else if (this.mAllowGesture) {
            if (!this.mThresholdCrossed) {
                if (actionMasked == 5) {
                    cancelGesture(motionEvent);
                    return;
                } else if (actionMasked == 2) {
                    if (motionEvent.getEventTime() - motionEvent.getDownTime() > ((long) this.mLongPressTimeout)) {
                        cancelGesture(motionEvent);
                        return;
                    }
                    float abs = Math.abs(motionEvent.getX() - this.mDownPoint.x);
                    float abs2 = Math.abs(motionEvent.getY() - this.mDownPoint.y);
                    if (abs2 > abs && abs2 > this.mTouchSlop) {
                        cancelGesture(motionEvent);
                        return;
                    } else if (abs > abs2 && abs > this.mTouchSlop) {
                        this.mThresholdCrossed = true;
                    }
                }
            }
            this.mEdgeBackPanel.onMotionEvent(motionEvent);
        }
        if (actionMasked == 1 || actionMasked == 3) {
            float abs3 = Math.abs(motionEvent.getX() - this.mDownPoint.x);
            if (abs3 > Math.abs(motionEvent.getY() - this.mDownPoint.y) && abs3 > this.mTouchSlop && !this.mAllowGesture && (backGestureAttemptCallback = this.mGestureCallback) != null) {
                backGestureAttemptCallback.onBackGestureAttempted(this.mDisallowedGestureReason);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setInsets(int i, int i2) {
        this.mLeftInset = i;
        this.mRightInset = i2;
    }
}
