package com.android.quickstep.inputconsumers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.PagedView;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.tracing.InputConsumerProto;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.TraceHelper;
import com.android.quickstep.AbsSwipeUpHandler;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.RotationTouchHelper;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.CachedEventDispatcher;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.NavBarPosition;
import com.android.systemui.shared.system.InputChannelCompat;
import com.android.systemui.shared.system.InputMonitorCompat;
import java.util.function.Consumer;

public class OtherActivityInputConsumer extends ContextWrapper implements InputConsumer {
    public static final String DOWN_EVT = "OtherActivityInputConsumer.DOWN";
    public static final int OVERVIEW_MIN_DEGREES = 15;
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO_GESTURAL = 2.0f;
    public static final float QUICKSTEP_TOUCH_SLOP_RATIO_TWO_BUTTON = 9.0f;
    private static final String UP_EVT = "OtherActivityInputConsumer.UP";
    private RecentsAnimationCallbacks mActiveCallbacks;
    private int mActivePointerId = -1;
    private final BaseActivityInterface mActivityInterface;
    private Runnable mCancelRecentsAnimationRunnable = $$Lambda$OtherActivityInputConsumer$EjYFk6GXX3Ti0a0CqE4Q7WLJNI.INSTANCE;
    private final RecentsAnimationDeviceState mDeviceState;
    private final boolean mDisableHorizontalSwipe;
    private final PointF mDownPos = new PointF();
    private final GestureState mGestureState;
    private final AbsSwipeUpHandler.Factory mHandlerFactory;
    private final InputChannelCompat.InputEventReceiver mInputEventReceiver;
    private final InputMonitorCompat mInputMonitorCompat;
    private AbsSwipeUpHandler mInteractionHandler;
    private final boolean mIsDeferredDownTarget;
    private final PointF mLastPos = new PointF();
    private Handler mMainThreadHandler;
    private final MotionPauseDetector mMotionPauseDetector;
    private final float mMotionPauseMinDisplacement;
    private final NavBarPosition mNavBarPosition;
    private final Consumer<OtherActivityInputConsumer> mOnCompleteCallback;
    private boolean mPassedPilferInputSlop;
    private boolean mPassedSlopOnThisGesture;
    private boolean mPassedWindowMoveSlop;
    private final CachedEventDispatcher mRecentsViewDispatcher = new CachedEventDispatcher();
    private final RotationTouchHelper mRotationTouchHelper;
    private final float mSquaredTouchSlop;
    private float mStartDisplacement;
    private final TaskAnimationManager mTaskAnimationManager;
    private final float mTouchSlop;
    private VelocityTracker mVelocityTracker;

    public int getType() {
        return 4;
    }

    public boolean isConsumerDetachedFromGesture() {
        return true;
    }

    public OtherActivityInputConsumer(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, boolean z, Consumer<OtherActivityInputConsumer> consumer, InputMonitorCompat inputMonitorCompat, InputChannelCompat.InputEventReceiver inputEventReceiver, boolean z2, AbsSwipeUpHandler.Factory factory) {
        super(context);
        this.mDeviceState = recentsAnimationDeviceState;
        NavBarPosition navBarPosition = recentsAnimationDeviceState.getNavBarPosition();
        this.mNavBarPosition = navBarPosition;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mMainThreadHandler = new Handler(Looper.getMainLooper());
        this.mHandlerFactory = factory;
        this.mActivityInterface = gestureState.getActivityInterface();
        boolean z3 = true;
        this.mMotionPauseDetector = new MotionPauseDetector(context, false, (navBarPosition.isLeftEdge() || navBarPosition.isRightEdge()) ? 0 : 1);
        this.mMotionPauseMinDisplacement = context.getResources().getDimension(R.dimen.motion_pause_detector_min_displacement_from_app);
        this.mOnCompleteCallback = consumer;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mInputMonitorCompat = inputMonitorCompat;
        this.mInputEventReceiver = inputEventReceiver;
        boolean isRecentsAnimationRunning = taskAnimationManager.isRecentsAnimationRunning();
        this.mIsDeferredDownTarget = !isRecentsAnimationRunning && z;
        float f = recentsAnimationDeviceState.isFullyGesturalNavMode() ? 2.0f : 9.0f;
        float scaledTouchSlop = (float) ViewConfiguration.get(this).getScaledTouchSlop();
        this.mTouchSlop = scaledTouchSlop;
        this.mSquaredTouchSlop = f * scaledTouchSlop * scaledTouchSlop;
        this.mPassedWindowMoveSlop = isRecentsAnimationRunning;
        this.mPassedPilferInputSlop = isRecentsAnimationRunning;
        this.mDisableHorizontalSwipe = (isRecentsAnimationRunning || !z2) ? false : z3;
        this.mRotationTouchHelper = recentsAnimationDeviceState.getRotationTouchHelper();
    }

    private void forceCancelGesture(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        motionEvent.setAction(3);
        finishTouchTracking(motionEvent);
        motionEvent.setAction(action);
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker != null) {
            if (this.mPassedWindowMoveSlop && this.mInteractionHandler != null && !this.mRecentsViewDispatcher.hasConsumer()) {
                this.mRecentsViewDispatcher.setConsumer(this.mInteractionHandler.getRecentsViewDispatcher(this.mNavBarPosition.getRotation()));
                int action = motionEvent.getAction();
                motionEvent.setAction(PagedView.ACTION_MOVE_ALLOW_EASY_FLING);
                this.mRecentsViewDispatcher.dispatchEvent(motionEvent);
                motionEvent.setAction(action);
            }
            int edgeFlags = motionEvent.getEdgeFlags();
            motionEvent.setEdgeFlags(edgeFlags | 256);
            this.mRecentsViewDispatcher.dispatchEvent(motionEvent);
            motionEvent.setEdgeFlags(edgeFlags);
            this.mVelocityTracker.addMovement(motionEvent);
            if (motionEvent.getActionMasked() == 6) {
                this.mVelocityTracker.clear();
                this.mMotionPauseDetector.clear();
            }
            int actionMasked = motionEvent.getActionMasked();
            boolean z = false;
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (findPointerIndex != -1) {
                            this.mLastPos.set(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
                            float displacement = getDisplacement(motionEvent);
                            float f = this.mLastPos.x - this.mDownPos.x;
                            float f2 = this.mLastPos.y - this.mDownPos.y;
                            if (!this.mPassedWindowMoveSlop && !this.mIsDeferredDownTarget) {
                                float abs = Math.abs(displacement);
                                float f3 = this.mTouchSlop;
                                if (abs > f3) {
                                    this.mPassedWindowMoveSlop = true;
                                    this.mStartDisplacement = Math.min(displacement, -f3);
                                }
                            }
                            float abs2 = Math.abs(f);
                            float f4 = -displacement;
                            boolean z2 = Utilities.squaredHypot(f, f2) >= this.mSquaredTouchSlop;
                            if (!this.mPassedSlopOnThisGesture && z2) {
                                this.mPassedSlopOnThisGesture = true;
                            }
                            boolean z3 = (!this.mPassedSlopOnThisGesture && this.mPassedPilferInputSlop) || Math.toDegrees(Math.atan((double) (f4 / abs2))) <= 15.0d;
                            if (!this.mPassedPilferInputSlop && z2) {
                                if (!this.mDisableHorizontalSwipe || Math.abs(f) <= Math.abs(f2)) {
                                    this.mPassedPilferInputSlop = true;
                                    if (this.mIsDeferredDownTarget) {
                                        startTouchTrackingForWindowAnimation(motionEvent.getEventTime());
                                    }
                                    if (!this.mPassedWindowMoveSlop) {
                                        this.mPassedWindowMoveSlop = true;
                                        this.mStartDisplacement = Math.min(displacement, -this.mTouchSlop);
                                    }
                                    notifyGestureStarted(z3);
                                } else {
                                    forceCancelGesture(motionEvent);
                                    return;
                                }
                            }
                            AbsSwipeUpHandler absSwipeUpHandler = this.mInteractionHandler;
                            if (absSwipeUpHandler != null) {
                                if (this.mPassedWindowMoveSlop) {
                                    absSwipeUpHandler.updateDisplacement(displacement - this.mStartDisplacement);
                                }
                                if (this.mDeviceState.isFullyGesturalNavMode()) {
                                    MotionPauseDetector motionPauseDetector = this.mMotionPauseDetector;
                                    if (f4 < this.mMotionPauseMinDisplacement || z3) {
                                        z = true;
                                    }
                                    motionPauseDetector.setDisallowPause(z);
                                    this.mMotionPauseDetector.addPosition(motionEvent);
                                    this.mInteractionHandler.setIsLikelyToStartNewTask(z3);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    } else if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                int actionIndex = motionEvent.getActionIndex();
                                if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
                                    if (actionIndex == 0) {
                                        z = true;
                                    }
                                    this.mDownPos.set(motionEvent.getX(z ? 1 : 0) - (this.mLastPos.x - this.mDownPos.x), motionEvent.getY(z) - (this.mLastPos.y - this.mDownPos.y));
                                    this.mLastPos.set(motionEvent.getX(z), motionEvent.getY(z));
                                    this.mActivePointerId = motionEvent.getPointerId(z);
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if (!this.mPassedPilferInputSlop) {
                            if (!this.mRotationTouchHelper.isInSwipeUpTouchRegion(motionEvent, motionEvent.getActionIndex())) {
                                forceCancelGesture(motionEvent);
                                return;
                            }
                            return;
                        } else {
                            return;
                        }
                    }
                }
                finishTouchTracking(motionEvent);
                return;
            }
            this.mInputEventReceiver.setBatchingEnabled(false);
            Object beginSection = TraceHelper.INSTANCE.beginSection(DOWN_EVT, 4);
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mDownPos.set(motionEvent.getX(), motionEvent.getY());
            this.mLastPos.set(this.mDownPos);
            if (!this.mIsDeferredDownTarget) {
                startTouchTrackingForWindowAnimation(motionEvent.getEventTime());
            }
            TraceHelper.INSTANCE.endSection(beginSection);
        }
    }

    private void notifyGestureStarted(boolean z) {
        ActiveGestureLog.INSTANCE.addLog("startQuickstep");
        if (this.mInteractionHandler != null) {
            TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
            this.mInputMonitorCompat.pilferPointers();
            this.mInputEventReceiver.setBatchingEnabled(true);
            this.mInteractionHandler.onGestureStarted(z);
        }
    }

    private void startTouchTrackingForWindowAnimation(long j) {
        ActiveGestureLog.INSTANCE.addLog("startRecentsAnimation");
        AbsSwipeUpHandler newHandler = this.mHandlerFactory.newHandler(this.mGestureState, j);
        this.mInteractionHandler = newHandler;
        newHandler.setGestureEndCallback(new Runnable() {
            public final void run() {
                OtherActivityInputConsumer.this.onInteractionGestureFinished();
            }
        });
        this.mMotionPauseDetector.setOnMotionPauseListener(this.mInteractionHandler.getMotionPauseListener());
        this.mInteractionHandler.initWhenReady();
        if (this.mTaskAnimationManager.isRecentsAnimationRunning()) {
            RecentsAnimationCallbacks continueRecentsAnimation = this.mTaskAnimationManager.continueRecentsAnimation(this.mGestureState);
            this.mActiveCallbacks = continueRecentsAnimation;
            continueRecentsAnimation.addListener(this.mInteractionHandler);
            this.mTaskAnimationManager.notifyRecentsAnimationState(this.mInteractionHandler);
            notifyGestureStarted(true);
            return;
        }
        Intent intent = new Intent(this.mInteractionHandler.getLaunchIntent());
        intent.putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, this.mGestureState.getGestureId());
        this.mActiveCallbacks = this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, intent, this.mInteractionHandler);
    }

    private void finishTouchTracking(MotionEvent motionEvent) {
        float f;
        Object beginSection = TraceHelper.INSTANCE.beginSection(UP_EVT, 4);
        if (!this.mPassedWindowMoveSlop || this.mInteractionHandler == null) {
            onConsumerAboutToBeSwitched();
            onInteractionGestureFinished();
            this.mMainThreadHandler.removeCallbacks(this.mCancelRecentsAnimationRunnable);
            this.mMainThreadHandler.postDelayed(this.mCancelRecentsAnimationRunnable, 100);
        } else if (motionEvent.getActionMasked() == 3) {
            this.mInteractionHandler.onGestureCancelled();
        } else {
            this.mVelocityTracker.computeCurrentVelocity(1);
            float xVelocity = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
            float yVelocity = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
            if (this.mNavBarPosition.isRightEdge()) {
                f = xVelocity;
            } else {
                f = this.mNavBarPosition.isLeftEdge() ? -xVelocity : yVelocity;
            }
            this.mInteractionHandler.updateDisplacement(getDisplacement(motionEvent) - this.mStartDisplacement);
            this.mInteractionHandler.onGestureEnded(f, new PointF(xVelocity, yVelocity), this.mDownPos);
        }
        cleanupAfterGesture();
        TraceHelper.INSTANCE.endSection(beginSection);
    }

    private void cleanupAfterGesture() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.mMotionPauseDetector.clear();
    }

    public void notifyOrientationSetup() {
        this.mRotationTouchHelper.onStartGesture();
    }

    public void onConsumerAboutToBeSwitched() {
        Preconditions.assertUIThread();
        this.mMainThreadHandler.removeCallbacks(this.mCancelRecentsAnimationRunnable);
        if (this.mInteractionHandler != null) {
            removeListener();
            this.mInteractionHandler.onConsumerAboutToBeSwitched();
        }
    }

    /* access modifiers changed from: private */
    public void onInteractionGestureFinished() {
        Preconditions.assertUIThread();
        removeListener();
        this.mInteractionHandler = null;
        cleanupAfterGesture();
        this.mOnCompleteCallback.accept(this);
    }

    private void removeListener() {
        RecentsAnimationCallbacks recentsAnimationCallbacks = this.mActiveCallbacks;
        if (recentsAnimationCallbacks != null) {
            recentsAnimationCallbacks.removeListener(this.mInteractionHandler);
        }
    }

    private float getDisplacement(MotionEvent motionEvent) {
        float y;
        float f;
        if (this.mNavBarPosition.isRightEdge()) {
            y = motionEvent.getX();
            f = this.mDownPos.x;
        } else if (this.mNavBarPosition.isLeftEdge()) {
            return this.mDownPos.x - motionEvent.getX();
        } else {
            y = motionEvent.getY();
            f = this.mDownPos.y;
        }
        return y - f;
    }

    public boolean allowInterceptByParent() {
        return !this.mPassedPilferInputSlop;
    }

    public void writeToProtoInternal(InputConsumerProto.Builder builder) {
        AbsSwipeUpHandler absSwipeUpHandler = this.mInteractionHandler;
        if (absSwipeUpHandler != null) {
            absSwipeUpHandler.writeToProto(builder);
        }
    }
}
