package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.R;
import com.android.launcher3.touch.BaseSwipeDetector;
import java.util.LinkedList;
import java.util.Queue;

public abstract class BaseSwipeDetector {
    private static final float ANIMATION_DURATION = 1200.0f;
    private static final boolean DBG = false;
    private static final String TAG = "BaseSwipeDetector";
    private static final PointF sTempPoint = new PointF();
    private int mActivePointerId = -1;
    protected Context mContext;
    private PointF mDisplacement = new PointF();
    private final PointF mDownPos = new PointF();
    protected boolean mIgnoreSlopWhenSettling;
    protected final boolean mIsRtl;
    private boolean mIsSettingState;
    private PointF mLastDisplacement = new PointF();
    private final PointF mLastPos = new PointF();
    protected final float mMaxVelocity;
    private final float mReleaseVelocity;
    private final Queue<Runnable> mSetStateQueue = new LinkedList();
    ScrollState mState = ScrollState.IDLE;
    protected PointF mSubtractDisplacement = new PointF();
    protected final float mTouchSlop;
    private VelocityTracker mVelocityTracker;

    private enum ScrollState {
        IDLE,
        DRAGGING,
        SETTLING
    }

    /* access modifiers changed from: protected */
    public abstract void reportDragEndInternal(PointF pointF);

    /* access modifiers changed from: protected */
    public abstract void reportDragStartInternal(boolean z);

    /* access modifiers changed from: protected */
    public abstract void reportDraggingInternal(PointF pointF, MotionEvent motionEvent);

    /* access modifiers changed from: protected */
    public abstract boolean shouldScrollStart(PointF pointF);

    protected BaseSwipeDetector(Context context, ViewConfiguration viewConfiguration, boolean z) {
        this.mTouchSlop = (float) viewConfiguration.getScaledTouchSlop();
        this.mMaxVelocity = (float) viewConfiguration.getScaledMaximumFlingVelocity();
        this.mIsRtl = z;
        this.mContext = context;
        this.mReleaseVelocity = (float) context.getResources().getDimensionPixelSize(R.dimen.base_swift_detector_fling_release_velocity);
    }

    public static long calculateDuration(float f, float f2) {
        return (long) Math.max(100.0f, (1200.0f / Math.max(2.0f, Math.abs(f * 0.5f))) * Math.max(0.2f, f2));
    }

    public int getDownX() {
        return (int) this.mDownPos.x;
    }

    public int getDownY() {
        return (int) this.mDownPos.y;
    }

    public boolean isIdleState() {
        return this.mState == ScrollState.IDLE;
    }

    public boolean isSettlingState() {
        return this.mState == ScrollState.SETTLING;
    }

    public boolean isDraggingState() {
        return this.mState == ScrollState.DRAGGING;
    }

    public boolean isDraggingOrSettling() {
        return this.mState == ScrollState.DRAGGING || this.mState == ScrollState.SETTLING;
    }

    public void finishedScrolling() {
        lambda$setState$0$BaseSwipeDetector(ScrollState.IDLE);
    }

    public boolean isFling(float f) {
        return Math.abs(f) > this.mReleaseVelocity;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        VelocityTracker velocityTracker;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0 && (velocityTracker = this.mVelocityTracker) != null) {
            velocityTracker.clear();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        int i = 0;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (findPointerIndex != -1) {
                        this.mDisplacement.set(motionEvent.getX(findPointerIndex) - this.mDownPos.x, motionEvent.getY(findPointerIndex) - this.mDownPos.y);
                        if (this.mIsRtl) {
                            PointF pointF = this.mDisplacement;
                            pointF.x = -pointF.x;
                        }
                        if (this.mState != ScrollState.DRAGGING && shouldScrollStart(this.mDisplacement)) {
                            lambda$setState$0$BaseSwipeDetector(ScrollState.DRAGGING);
                        }
                        if (this.mState == ScrollState.DRAGGING) {
                            reportDragging(motionEvent);
                        }
                        this.mLastPos.set(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 6) {
                        int actionIndex = motionEvent.getActionIndex();
                        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                            if (actionIndex == 0) {
                                i = 1;
                            }
                            this.mDownPos.set(motionEvent.getX(i) - (this.mLastPos.x - this.mDownPos.x), motionEvent.getY(i) - (this.mLastPos.y - this.mDownPos.y));
                            this.mLastPos.set(motionEvent.getX(i), motionEvent.getY(i));
                            this.mActivePointerId = motionEvent.getPointerId(i);
                        }
                    }
                }
            }
            if (this.mState == ScrollState.DRAGGING) {
                lambda$setState$0$BaseSwipeDetector(ScrollState.SETTLING);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        } else {
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mDownPos.set(motionEvent.getX(), motionEvent.getY());
            this.mLastPos.set(this.mDownPos);
            this.mLastDisplacement.set(0.0f, 0.0f);
            this.mDisplacement.set(0.0f, 0.0f);
            if (this.mState == ScrollState.SETTLING && this.mIgnoreSlopWhenSettling) {
                lambda$setState$0$BaseSwipeDetector(ScrollState.DRAGGING);
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: setState */
    public void lambda$setState$0$BaseSwipeDetector(ScrollState scrollState) {
        if (this.mIsSettingState) {
            this.mSetStateQueue.add(new Runnable(scrollState) {
                public final /* synthetic */ BaseSwipeDetector.ScrollState f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BaseSwipeDetector.this.lambda$setState$0$BaseSwipeDetector(this.f$1);
                }
            });
            return;
        }
        this.mIsSettingState = true;
        if (scrollState == ScrollState.DRAGGING) {
            initializeDragging();
            if (this.mState == ScrollState.IDLE) {
                reportDragStart(false);
            } else if (this.mState == ScrollState.SETTLING) {
                reportDragStart(true);
            }
        }
        if (scrollState == ScrollState.SETTLING) {
            reportDragEnd();
        }
        this.mState = scrollState;
        this.mIsSettingState = false;
        if (!this.mSetStateQueue.isEmpty()) {
            this.mSetStateQueue.remove().run();
        }
    }

    private void initializeDragging() {
        if (this.mState != ScrollState.SETTLING || !this.mIgnoreSlopWhenSettling) {
            this.mSubtractDisplacement.x = this.mDisplacement.x > 0.0f ? this.mTouchSlop : -this.mTouchSlop;
            this.mSubtractDisplacement.y = this.mDisplacement.y > 0.0f ? this.mTouchSlop : -this.mTouchSlop;
            return;
        }
        this.mSubtractDisplacement.set(0.0f, 0.0f);
    }

    private void reportDragStart(boolean z) {
        reportDragStartInternal(z);
    }

    private void reportDragging(MotionEvent motionEvent) {
        PointF pointF = this.mDisplacement;
        PointF pointF2 = this.mLastDisplacement;
        if (pointF != pointF2) {
            pointF2.set(pointF);
            PointF pointF3 = sTempPoint;
            pointF3.set(this.mDisplacement.x - this.mSubtractDisplacement.x, this.mDisplacement.y - this.mSubtractDisplacement.y);
            reportDraggingInternal(pointF3, motionEvent);
        }
    }

    private void reportDragEnd() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        PointF pointF = new PointF(this.mVelocityTracker.getXVelocity() / 1000.0f, this.mVelocityTracker.getYVelocity() / 1000.0f);
        if (this.mIsRtl) {
            pointF.x = -pointF.x;
        }
        reportDragEndInternal(pointF);
    }
}
