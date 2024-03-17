package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.Utilities;

public class SingleAxisSwipeDetector extends BaseSwipeDetector {
    public static final int DIRECTION_BOTH = 3;
    public static final int DIRECTION_NEGATIVE = 2;
    public static final int DIRECTION_POSITIVE = 1;
    public static final Direction HORIZONTAL = new Direction() {
        /* access modifiers changed from: package-private */
        public boolean isNegative(float f) {
            return f < 0.0f;
        }

        /* access modifiers changed from: package-private */
        public boolean isPositive(float f) {
            return f > 0.0f;
        }

        public String toString() {
            return "HORIZONTAL";
        }

        /* access modifiers changed from: package-private */
        public float extractDirection(PointF pointF) {
            return pointF.x;
        }

        /* access modifiers changed from: package-private */
        public float extractOrthogonalDirection(PointF pointF) {
            return pointF.y;
        }
    };
    public static final Direction VERTICAL = new Direction() {
        /* access modifiers changed from: package-private */
        public boolean isNegative(float f) {
            return f > 0.0f;
        }

        /* access modifiers changed from: package-private */
        public boolean isPositive(float f) {
            return f < 0.0f;
        }

        public String toString() {
            return "VERTICAL";
        }

        /* access modifiers changed from: package-private */
        public float extractDirection(PointF pointF) {
            return pointF.y;
        }

        /* access modifiers changed from: package-private */
        public float extractOrthogonalDirection(PointF pointF) {
            return pointF.x;
        }
    };
    private final Direction mDir;
    private final Listener mListener;
    private int mScrollDirections;
    private float mTouchSlopMultiplier = 1.0f;

    public static abstract class Direction {
        /* access modifiers changed from: package-private */
        public abstract float extractDirection(PointF pointF);

        /* access modifiers changed from: package-private */
        public abstract float extractOrthogonalDirection(PointF pointF);

        /* access modifiers changed from: package-private */
        public abstract boolean isNegative(float f);

        /* access modifiers changed from: package-private */
        public abstract boolean isPositive(float f);
    }

    public SingleAxisSwipeDetector(Context context, Listener listener, Direction direction) {
        super(context, ViewConfiguration.get(context), Utilities.isRtl(context.getResources()));
        this.mListener = listener;
        this.mDir = direction;
    }

    protected SingleAxisSwipeDetector(Context context, ViewConfiguration viewConfiguration, Listener listener, Direction direction, boolean z) {
        super(context, viewConfiguration, z);
        this.mListener = listener;
        this.mDir = direction;
    }

    public void setTouchSlopMultiplier(float f) {
        this.mTouchSlopMultiplier = f;
    }

    public void setDetectableScrollConditions(int i, boolean z) {
        this.mScrollDirections = i;
        this.mIgnoreSlopWhenSettling = z;
    }

    public boolean wasInitialTouchPositive() {
        Direction direction = this.mDir;
        return direction.isPositive(direction.extractDirection(this.mSubtractDisplacement));
    }

    /* access modifiers changed from: protected */
    public boolean shouldScrollStart(PointF pointF) {
        if (Math.abs(this.mDir.extractDirection(pointF)) < Math.max(this.mTouchSlop * this.mTouchSlopMultiplier, Math.abs(this.mDir.extractOrthogonalDirection(pointF)))) {
            return false;
        }
        float extractDirection = this.mDir.extractDirection(pointF);
        if (canScrollNegative(extractDirection) || canScrollPositive(extractDirection)) {
            return true;
        }
        return false;
    }

    private boolean canScrollNegative(float f) {
        return (this.mScrollDirections & 2) > 0 && this.mDir.isNegative(f);
    }

    private boolean canScrollPositive(float f) {
        return (this.mScrollDirections & 1) > 0 && this.mDir.isPositive(f);
    }

    /* access modifiers changed from: protected */
    public void reportDragStartInternal(boolean z) {
        this.mListener.onDragStart(!z, this.mDir.extractDirection(this.mSubtractDisplacement));
    }

    /* access modifiers changed from: protected */
    public void reportDraggingInternal(PointF pointF, MotionEvent motionEvent) {
        this.mListener.onDrag(this.mDir.extractDirection(pointF), this.mDir.extractOrthogonalDirection(pointF), motionEvent);
    }

    /* access modifiers changed from: protected */
    public void reportDragEndInternal(PointF pointF) {
        this.mListener.onDragEnd(this.mDir.extractDirection(pointF));
    }

    public interface Listener {
        boolean onDrag(float f);

        void onDragEnd(float f);

        void onDragStart(boolean z, float f);

        boolean onDrag(float f, MotionEvent motionEvent) {
            return onDrag(f);
        }

        boolean onDrag(float f, float f2, MotionEvent motionEvent) {
            return onDrag(f, motionEvent);
        }
    }
}
