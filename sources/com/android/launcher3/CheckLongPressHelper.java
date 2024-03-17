package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class CheckLongPressHelper {
    public static final float DEFAULT_LONG_PRESS_TIMEOUT_FACTOR = 0.75f;
    private boolean mHasPerformedLongPress;
    private final View.OnLongClickListener mListener;
    private float mLongPressTimeoutFactor;
    private Runnable mPendingCheckForLongPress;
    private final float mSlop;
    private final View mView;

    public CheckLongPressHelper(View view) {
        this(view, (View.OnLongClickListener) null);
    }

    public CheckLongPressHelper(View view, View.OnLongClickListener onLongClickListener) {
        this.mLongPressTimeoutFactor = 0.75f;
        this.mView = view;
        this.mListener = onLongClickListener;
        this.mSlop = (float) ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action != 2) {
                    if (action != 3) {
                        return;
                    }
                } else if (!Utilities.pointInView(this.mView, motionEvent.getX(), motionEvent.getY(), this.mSlop)) {
                    cancelLongPress();
                    return;
                } else if (this.mPendingCheckForLongPress != null && isStylusButtonPressed(motionEvent)) {
                    triggerLongPress();
                    return;
                } else {
                    return;
                }
            }
            cancelLongPress();
            return;
        }
        cancelLongPress();
        postCheckForLongPress();
        if (isStylusButtonPressed(motionEvent)) {
            triggerLongPress();
        }
    }

    public void setLongPressTimeoutFactor(float f) {
        this.mLongPressTimeoutFactor = f;
    }

    private void postCheckForLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress == null) {
            this.mPendingCheckForLongPress = new Runnable() {
                public final void run() {
                    CheckLongPressHelper.this.triggerLongPress();
                }
            };
        }
        this.mView.postDelayed(this.mPendingCheckForLongPress, (long) (((float) ViewConfiguration.getLongPressTimeout()) * this.mLongPressTimeoutFactor));
    }

    public void cancelLongPress() {
        this.mHasPerformedLongPress = false;
        clearCallbacks();
    }

    public boolean hasPerformedLongPress() {
        return this.mHasPerformedLongPress;
    }

    /* access modifiers changed from: private */
    public void triggerLongPress() {
        boolean z;
        if (this.mView.getParent() != null && this.mView.hasWindowFocus()) {
            if ((!this.mView.isPressed() || this.mListener != null) && !this.mHasPerformedLongPress) {
                View.OnLongClickListener onLongClickListener = this.mListener;
                if (onLongClickListener != null) {
                    z = onLongClickListener.onLongClick(this.mView);
                } else {
                    z = this.mView.performLongClick();
                }
                if (z) {
                    this.mView.setPressed(false);
                    this.mHasPerformedLongPress = true;
                }
                clearCallbacks();
            }
        }
    }

    private void clearCallbacks() {
        Runnable runnable = this.mPendingCheckForLongPress;
        if (runnable != null) {
            this.mView.removeCallbacks(runnable);
            this.mPendingCheckForLongPress = null;
        }
    }

    private static boolean isStylusButtonPressed(MotionEvent motionEvent) {
        if (motionEvent.getToolType(0) != 2 || !motionEvent.isButtonPressed(2)) {
            return false;
        }
        return true;
    }
}
