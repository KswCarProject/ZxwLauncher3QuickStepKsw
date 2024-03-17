package com.android.quickstep.inputconsumers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;
import com.android.launcher3.anim.AnimatorListeners;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.MultiStateCallback;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.RecentsAnimationTargets;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.InputMonitorCompat;
import java.util.HashMap;

public class ProgressDelegateInputConsumer implements InputConsumer, RecentsAnimationCallbacks.RecentsAnimationListener, SingleAxisSwipeDetector.Listener {
    private static final int STATE_FLING_FINISHED = getFlagForIndex(2, "STATE_FLING_FINISHED");
    private static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(1, "STATE_HANDLER_INVALIDATED");
    private static final String[] STATE_NAMES = null;
    private static final int STATE_TARGET_RECEIVED = getFlagForIndex(0, "STATE_TARGET_RECEIVED");
    private static final float SWIPE_DISTANCE_THRESHOLD = 0.2f;
    private final Context mContext;
    private final Point mDisplaySize;
    private boolean mDragStarted = false;
    private Boolean mFlingEndsOnHome;
    private final GestureState mGestureState;
    private final InputMonitorCompat mInputMonitorCompat;
    private final AnimatedFloat mProgress;
    private RecentsAnimationController mRecentsAnimationController;
    private final MultiStateCallback mStateCallback;
    private final SingleAxisSwipeDetector mSwipeDetector;
    private final TaskAnimationManager mTaskAnimationManager;

    private static int getFlagForIndex(int i, String str) {
        return 1 << i;
    }

    public int getType() {
        return 512;
    }

    public ProgressDelegateInputConsumer(Context context, TaskAnimationManager taskAnimationManager, GestureState gestureState, InputMonitorCompat inputMonitorCompat, AnimatedFloat animatedFloat) {
        this.mContext = context;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mInputMonitorCompat = inputMonitorCompat;
        this.mProgress = animatedFloat;
        this.mDisplaySize = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo().currentSize;
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        int i = STATE_TARGET_RECEIVED;
        multiStateCallback.runOnceAtState(STATE_HANDLER_INVALIDATED | i, new Runnable() {
            public final void run() {
                ProgressDelegateInputConsumer.this.endRemoteAnimation();
            }
        });
        multiStateCallback.runOnceAtState(i | STATE_FLING_FINISHED, new Runnable() {
            public final void run() {
                ProgressDelegateInputConsumer.this.onFlingFinished();
            }
        });
        SingleAxisSwipeDetector singleAxisSwipeDetector = new SingleAxisSwipeDetector(context, this, SingleAxisSwipeDetector.VERTICAL);
        this.mSwipeDetector = singleAxisSwipeDetector;
        singleAxisSwipeDetector.setDetectableScrollConditions(1, false);
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        if (this.mFlingEndsOnHome == null) {
            this.mSwipeDetector.onTouchEvent(motionEvent);
        }
    }

    public void onDragStart(boolean z, float f) {
        this.mDragStarted = true;
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitorCompat.pilferPointers();
        this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, this.mGestureState.getHomeIntent().putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, this.mGestureState.getGestureId()), this);
    }

    public boolean onDrag(float f) {
        if (this.mDisplaySize.y <= 0) {
            return true;
        }
        this.mProgress.updateValue(f / ((float) (-this.mDisplaySize.y)));
        return true;
    }

    public void onDragEnd(float f) {
        boolean z = true;
        float f2 = 0.0f;
        if (!this.mSwipeDetector.isFling(f) ? this.mProgress.value <= 0.2f : f >= 0.0f) {
            z = false;
        }
        if (z) {
            f2 = 1.0f;
        }
        long calculateDuration = BaseSwipeDetector.calculateDuration(f, f2 - this.mProgress.value);
        this.mFlingEndsOnHome = Boolean.valueOf(z);
        ObjectAnimator animateToValue = this.mProgress.animateToValue(f2);
        animateToValue.setDuration(calculateDuration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(f));
        animateToValue.addListener(AnimatorListeners.forSuccessCallback(new Runnable() {
            public final void run() {
                ProgressDelegateInputConsumer.this.lambda$onDragEnd$0$ProgressDelegateInputConsumer();
            }
        }));
        animateToValue.start();
    }

    public /* synthetic */ void lambda$onDragEnd$0$ProgressDelegateInputConsumer() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_FLING_FINISHED);
    }

    /* access modifiers changed from: private */
    public void onFlingFinished() {
        if (this.mRecentsAnimationController != null) {
            Boolean bool = this.mFlingEndsOnHome;
            this.mRecentsAnimationController.finishController(bool == null ? true : bool.booleanValue(), (Runnable) null, false);
        }
    }

    public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        this.mRecentsAnimationController = recentsAnimationController;
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_TARGET_RECEIVED);
    }

    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
        this.mRecentsAnimationController = null;
    }

    /* access modifiers changed from: private */
    public void endRemoteAnimation() {
        onDragEnd(Float.MIN_VALUE);
    }

    public void onConsumerAboutToBeSwitched() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_HANDLER_INVALIDATED);
    }

    public boolean allowInterceptByParent() {
        return !this.mDragStarted;
    }
}
