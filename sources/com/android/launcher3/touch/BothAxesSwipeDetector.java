package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.Utilities;

public class BothAxesSwipeDetector extends BaseSwipeDetector {
    public static final int DIRECTION_DOWN = 4;
    public static final int DIRECTION_LEFT = 8;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_UP = 1;
    private final Listener mListener;
    private int mScrollDirections;

    public interface Listener {
        boolean onDrag(PointF pointF, MotionEvent motionEvent);

        void onDragEnd(PointF pointF);

        void onDragStart(boolean z);
    }

    public BothAxesSwipeDetector(Context context, Listener listener) {
        super(context, ViewConfiguration.get(context), Utilities.isRtl(context.getResources()));
        this.mListener = listener;
    }

    public void setDetectableScrollConditions(int i, boolean z) {
        this.mScrollDirections = i;
        this.mIgnoreSlopWhenSettling = z;
    }

    /* access modifiers changed from: protected */
    public boolean shouldScrollStart(PointF pointF) {
        boolean z = (this.mScrollDirections & 1) > 0 && pointF.y <= (-this.mTouchSlop);
        boolean z2 = (this.mScrollDirections & 2) > 0 && pointF.x >= this.mTouchSlop;
        boolean z3 = (this.mScrollDirections & 4) > 0 && pointF.y >= this.mTouchSlop;
        boolean z4 = (this.mScrollDirections & 8) > 0 && pointF.x <= (-this.mTouchSlop);
        if (z || z2 || z3 || z4) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void reportDragStartInternal(boolean z) {
        this.mListener.onDragStart(!z);
    }

    /* access modifiers changed from: protected */
    public void reportDraggingInternal(PointF pointF, MotionEvent motionEvent) {
        this.mListener.onDrag(pointF, motionEvent);
    }

    /* access modifiers changed from: protected */
    public void reportDragEndInternal(PointF pointF) {
        this.mListener.onDragEnd(pointF);
    }
}
