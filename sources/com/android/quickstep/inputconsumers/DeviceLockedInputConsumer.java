package com.android.quickstep.inputconsumers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.testing.TestLogging;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.AnimatedFloat;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.MultiStateCallback;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.RecentsAnimationController;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.RecentsAnimationTargets;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.util.ActiveGestureLog;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.InputMonitorCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import java.util.HashMap;

public class DeviceLockedInputConsumer implements InputConsumer, RecentsAnimationCallbacks.RecentsAnimationListener, TransformParams.BuilderProxy {
    /* access modifiers changed from: private */
    public static final int STATE_HANDLER_INVALIDATED = getFlagForIndex(1, "STATE_HANDLER_INVALIDATED");
    private static final String[] STATE_NAMES = null;
    private static final int STATE_TARGET_RECEIVED = getFlagForIndex(0, "STATE_TARGET_RECEIVED");
    /* access modifiers changed from: private */
    public final Context mContext;
    private final RecentsAnimationDeviceState mDeviceState;
    private final Point mDisplaySize;
    private final GestureState mGestureState;
    /* access modifiers changed from: private */
    public boolean mHomeLaunched = false;
    private final InputMonitorCompat mInputMonitorCompat;
    private final Matrix mMatrix = new Matrix();
    private final float mMaxTranslationY;
    private final AnimatedFloat mProgress = new AnimatedFloat(new Runnable() {
        public final void run() {
            DeviceLockedInputConsumer.this.applyTransform();
        }
    });
    private RecentsAnimationController mRecentsAnimationController;
    /* access modifiers changed from: private */
    public final MultiStateCallback mStateCallback;
    private final TaskAnimationManager mTaskAnimationManager;
    private boolean mThresholdCrossed = false;
    private final PointF mTouchDown = new PointF();
    private final float mTouchSlopSquared;
    private final TransformParams mTransformParams;
    private VelocityTracker mVelocityTracker;

    private static int getFlagForIndex(int i, String str) {
        return 1 << i;
    }

    public int getType() {
        return 16;
    }

    public DeviceLockedInputConsumer(Context context, RecentsAnimationDeviceState recentsAnimationDeviceState, TaskAnimationManager taskAnimationManager, GestureState gestureState, InputMonitorCompat inputMonitorCompat) {
        this.mContext = context;
        this.mDeviceState = recentsAnimationDeviceState;
        this.mTaskAnimationManager = taskAnimationManager;
        this.mGestureState = gestureState;
        this.mTouchSlopSquared = Utilities.squaredTouchSlop(context);
        this.mTransformParams = new TransformParams();
        this.mInputMonitorCompat = inputMonitorCompat;
        this.mMaxTranslationY = (float) context.getResources().getDimensionPixelSize(R.dimen.device_locked_y_offset);
        this.mDisplaySize = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo().currentSize;
        MultiStateCallback multiStateCallback = new MultiStateCallback(STATE_NAMES);
        this.mStateCallback = multiStateCallback;
        multiStateCallback.runOnceAtState(STATE_TARGET_RECEIVED | STATE_HANDLER_INVALIDATED, new Runnable() {
            public final void run() {
                DeviceLockedInputConsumer.this.endRemoteAnimation();
            }
        });
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action != 1) {
                    if (action != 2) {
                        if (action != 3) {
                            if (action == 5 && !this.mThresholdCrossed) {
                                if (!this.mDeviceState.getRotationTouchHelper().isInSwipeUpTouchRegion(motionEvent, motionEvent.getActionIndex())) {
                                    int action2 = motionEvent.getAction();
                                    motionEvent.setAction(3);
                                    finishTouchTracking(motionEvent);
                                    motionEvent.setAction(action2);
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                    } else if (this.mThresholdCrossed) {
                        this.mProgress.updateValue(Math.max(this.mTouchDown.y - y, 0.0f) / ((float) this.mDisplaySize.y));
                        return;
                    } else if (Utilities.squaredHypot(x - this.mTouchDown.x, y - this.mTouchDown.y) > this.mTouchSlopSquared) {
                        startRecentsTransition();
                        return;
                    } else {
                        return;
                    }
                }
                finishTouchTracking(motionEvent);
                return;
            }
            this.mTouchDown.set(x, y);
        }
    }

    private void finishTouchTracking(MotionEvent motionEvent) {
        if (this.mThresholdCrossed) {
            final boolean z = true;
            if (motionEvent.getAction() == 1) {
                this.mVelocityTracker.computeCurrentVelocity(1);
                float yVelocity = this.mVelocityTracker.getYVelocity();
                if (Math.abs(yVelocity) <= this.mContext.getResources().getDimension(R.dimen.quickstep_fling_threshold_speed) ? this.mProgress.value < 0.3f : yVelocity >= 0.0f) {
                    z = false;
                }
                AnimatedFloat animatedFloat = this.mProgress;
                ObjectAnimator animateToValue = animatedFloat.animateToValue(animatedFloat.value, 0.0f);
                animateToValue.setDuration(100);
                animateToValue.setInterpolator(Interpolators.ACCEL);
                animateToValue.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (z) {
                            DeviceLockedInputConsumer.this.mContext.startActivity(Utilities.createHomeIntent());
                            boolean unused = DeviceLockedInputConsumer.this.mHomeLaunched = true;
                        }
                        DeviceLockedInputConsumer.this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(DeviceLockedInputConsumer.STATE_HANDLER_INVALIDATED);
                    }
                });
                animateToValue.start();
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_HANDLER_INVALIDATED);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
    }

    private void startRecentsTransition() {
        this.mThresholdCrossed = true;
        this.mHomeLaunched = false;
        TestLogging.recordEvent(TestProtocol.SEQUENCE_PILFER, "pilferPointers");
        this.mInputMonitorCompat.pilferPointers();
        this.mTaskAnimationManager.startRecentsAnimation(this.mGestureState, this.mGestureState.getHomeIntent().putExtra(ActiveGestureLog.INTENT_EXTRA_LOG_TRACE_ID, this.mGestureState.getGestureId()), this);
    }

    public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
        this.mRecentsAnimationController = recentsAnimationController;
        this.mTransformParams.setTargetSet(recentsAnimationTargets);
        applyTransform();
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_TARGET_RECEIVED);
    }

    public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
        this.mRecentsAnimationController = null;
        this.mTransformParams.setTargetSet((RemoteAnimationTargets) null);
    }

    /* access modifiers changed from: private */
    public void endRemoteAnimation() {
        if (this.mHomeLaunched) {
            ActivityManagerWrapper.getInstance().cancelRecentsAnimation(false);
            return;
        }
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.finishController(false, (Runnable) null, false);
        }
    }

    /* access modifiers changed from: private */
    public void applyTransform() {
        this.mTransformParams.setProgress(this.mProgress.value);
        if (this.mTransformParams.getTargetSet() != null) {
            TransformParams transformParams = this.mTransformParams;
            transformParams.applySurfaceParams(transformParams.createSurfaceParams(this));
        }
    }

    public void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
        this.mMatrix.setTranslate(0.0f, this.mProgress.value * this.mMaxTranslationY);
        builder.withMatrix(this.mMatrix);
    }

    public void onConsumerAboutToBeSwitched() {
        this.mStateCallback.lambda$setStateOnUiThread$0$MultiStateCallback(STATE_HANDLER_INVALIDATED);
    }

    public boolean allowInterceptByParent() {
        return !this.mThresholdCrossed;
    }
}
