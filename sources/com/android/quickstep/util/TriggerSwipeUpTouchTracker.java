package com.android.quickstep.util;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

public class TriggerSwipeUpTouchTracker {
    private final boolean mDisableHorizontalSwipe;
    private final PointF mDownPos = new PointF();
    private boolean mInterceptedTouch;
    private final float mMinFlingVelocity;
    private final NavBarPosition mNavBarPosition;
    private final Runnable mOnInterceptTouch;
    private final OnSwipeUpListener mOnSwipeUp;
    private final float mSquaredTouchSlop;
    private VelocityTracker mVelocityTracker;

    public interface OnSwipeUpListener {
        void onSwipeUp(boolean z, PointF pointF);

        void onSwipeUpCancelled();
    }

    public TriggerSwipeUpTouchTracker(Context context, boolean z, NavBarPosition navBarPosition, Runnable runnable, OnSwipeUpListener onSwipeUpListener) {
        this.mSquaredTouchSlop = Utilities.squaredTouchSlop(context);
        this.mMinFlingVelocity = context.getResources().getDimension(R.dimen.quickstep_fling_threshold_speed);
        this.mNavBarPosition = navBarPosition;
        this.mDisableHorizontalSwipe = z;
        this.mOnInterceptTouch = runnable;
        this.mOnSwipeUp = onSwipeUpListener;
        init();
    }

    public void init() {
        this.mInterceptedTouch = false;
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    public boolean interceptedTouch() {
        return this.mInterceptedTouch;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mDownPos.set(motionEvent.getX(), motionEvent.getY());
            } else if (actionMasked == 1) {
                onGestureEnd(motionEvent);
                endTouchTracking();
            } else if (actionMasked != 2) {
                if (actionMasked == 3) {
                    endTouchTracking();
                }
            } else if (!this.mInterceptedTouch) {
                float x = motionEvent.getX() - this.mDownPos.x;
                float y = motionEvent.getY() - this.mDownPos.y;
                if (Utilities.squaredHypot(x, y) < this.mSquaredTouchSlop) {
                    return;
                }
                if (!this.mDisableHorizontalSwipe || Math.abs(x) <= Math.abs(y)) {
                    this.mInterceptedTouch = true;
                    Runnable runnable = this.mOnInterceptTouch;
                    if (runnable != null) {
                        runnable.run();
                        return;
                    }
                    return;
                }
                endTouchTracking();
            }
        }
    }

    private void endTouchTracking() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003a, code lost:
        if (r3 > 0.0f) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005f, code lost:
        if (com.android.launcher3.Utilities.squaredHypot(r6, r8.getY() - r7.mDownPos.y) >= r7.mSquaredTouchSlop) goto L_0x0061;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onGestureEnd(android.view.MotionEvent r8) {
        /*
            r7 = this;
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            r1 = 1
            r0.computeCurrentVelocity(r1)
            android.view.VelocityTracker r0 = r7.mVelocityTracker
            float r0 = r0.getXVelocity()
            android.view.VelocityTracker r2 = r7.mVelocityTracker
            float r2 = r2.getYVelocity()
            com.android.quickstep.util.NavBarPosition r3 = r7.mNavBarPosition
            boolean r3 = r3.isRightEdge()
            if (r3 == 0) goto L_0x001c
            float r3 = -r0
            goto L_0x0027
        L_0x001c:
            com.android.quickstep.util.NavBarPosition r3 = r7.mNavBarPosition
            boolean r3 = r3.isLeftEdge()
            if (r3 == 0) goto L_0x0026
            r3 = r0
            goto L_0x0027
        L_0x0026:
            float r3 = -r2
        L_0x0027:
            float r4 = java.lang.Math.abs(r3)
            float r5 = r7.mMinFlingVelocity
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            r5 = 0
            if (r4 < 0) goto L_0x0034
            r4 = r1
            goto L_0x0035
        L_0x0034:
            r4 = r5
        L_0x0035:
            r6 = 0
            if (r4 == 0) goto L_0x003f
            int r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x003d
            goto L_0x0061
        L_0x003d:
            r1 = r5
            goto L_0x0061
        L_0x003f:
            boolean r3 = r7.mDisableHorizontalSwipe
            if (r3 == 0) goto L_0x0044
            goto L_0x004e
        L_0x0044:
            float r3 = r8.getX()
            android.graphics.PointF r6 = r7.mDownPos
            float r6 = r6.x
            float r6 = r3 - r6
        L_0x004e:
            float r8 = r8.getY()
            android.graphics.PointF r3 = r7.mDownPos
            float r3 = r3.y
            float r8 = r8 - r3
            float r8 = com.android.launcher3.Utilities.squaredHypot(r6, r8)
            float r3 = r7.mSquaredTouchSlop
            int r8 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r8 < 0) goto L_0x003d
        L_0x0061:
            com.android.quickstep.util.TriggerSwipeUpTouchTracker$OnSwipeUpListener r8 = r7.mOnSwipeUp
            if (r8 == 0) goto L_0x0073
            if (r1 == 0) goto L_0x0070
            android.graphics.PointF r1 = new android.graphics.PointF
            r1.<init>(r0, r2)
            r8.onSwipeUp(r4, r1)
            goto L_0x0073
        L_0x0070:
            r8.onSwipeUpCancelled()
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.TriggerSwipeUpTouchTracker.onGestureEnd(android.view.MotionEvent):void");
    }
}
