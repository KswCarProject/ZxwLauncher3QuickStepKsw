package com.android.quickstep.inputconsumers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.quickstep.BaseActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.InputConsumer;
import com.android.quickstep.RecentsAnimationDeviceState;
import com.android.quickstep.SystemUiProxy;
import com.android.systemui.shared.system.InputMonitorCompat;
import java.util.function.Consumer;

public class AssistantInputConsumer extends DelegateInputConsumer {
    private static final String OPA_BUNDLE_TRIGGER = "triggered_by";
    private static final int OPA_BUNDLE_TRIGGER_DIAG_SWIPE_GESTURE = 83;
    private static final long RETRACT_ANIMATION_DURATION_MS = 300;
    private static final String TAG = "AssistantInputConsumer";
    private int mActivePointerId = -1;
    private BaseActivityInterface mActivityInterface;
    private final int mAngleThreshold;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public float mDistance;
    private final PointF mDownPos = new PointF();
    private final float mDragDistThreshold;
    private long mDragTime;
    /* access modifiers changed from: private */
    public final float mFlingDistThreshold;
    private final Consumer<MotionEvent> mGestureDetector;
    private final PointF mLastPos = new PointF();
    /* access modifiers changed from: private */
    public float mLastProgress;
    /* access modifiers changed from: private */
    public boolean mLaunchedAssistant;
    private boolean mPassedSlop;
    private final float mSquaredSlop;
    private final PointF mStartDragPos = new PointF();
    private float mTimeFraction;
    private final long mTimeThreshold;

    static /* synthetic */ void lambda$new$0(MotionEvent motionEvent) {
    }

    public AssistantInputConsumer(Context context, GestureState gestureState, InputConsumer inputConsumer, InputMonitorCompat inputMonitorCompat, RecentsAnimationDeviceState recentsAnimationDeviceState, MotionEvent motionEvent) {
        super(inputConsumer, inputMonitorCompat);
        Consumer<MotionEvent> consumer;
        Resources resources = context.getResources();
        this.mContext = context;
        this.mDragDistThreshold = resources.getDimension(R.dimen.gestures_assistant_drag_threshold);
        this.mFlingDistThreshold = resources.getDimension(R.dimen.gestures_assistant_fling_threshold);
        this.mTimeThreshold = (long) resources.getInteger(R.integer.assistant_gesture_min_time_threshold);
        this.mAngleThreshold = resources.getInteger(R.integer.assistant_gesture_corner_deg_threshold);
        float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        this.mSquaredSlop = scaledTouchSlop * scaledTouchSlop;
        this.mActivityInterface = gestureState.getActivityInterface();
        if (recentsAnimationDeviceState.isAssistantGestureIsConstrained() || recentsAnimationDeviceState.isInDeferredGestureRegion(motionEvent)) {
            consumer = $$Lambda$AssistantInputConsumer$08Y__pD2w2JXyQdqnYwIuUanlBQ.INSTANCE;
        } else {
            consumer = new Consumer(new GestureDetector(context, new AssistantGestureListener())) {
                public final /* synthetic */ GestureDetector f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    boolean unused = this.f$0.onTouchEvent((MotionEvent) obj);
                }
            };
        }
        this.mGestureDetector = consumer;
    }

    public int getType() {
        return this.mDelegate.getType() | 8;
    }

    public void onMotionEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        int i = 0;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
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
                        } else if (this.mState != 1) {
                            this.mState = 2;
                        }
                    }
                } else if (this.mState != 2) {
                    if (!this.mDelegate.allowInterceptByParent()) {
                        this.mState = 2;
                    } else {
                        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (findPointerIndex != -1) {
                            this.mLastPos.set(motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex));
                            if (this.mPassedSlop) {
                                float hypot = (float) Math.hypot((double) (this.mLastPos.x - this.mStartDragPos.x), (double) (this.mLastPos.y - this.mStartDragPos.y));
                                this.mDistance = hypot;
                                if (hypot >= 0.0f) {
                                    this.mTimeFraction = Math.min((((float) (SystemClock.uptimeMillis() - this.mDragTime)) * 1.0f) / ((float) this.mTimeThreshold), 1.0f);
                                    updateAssistantProgress();
                                }
                            } else if (Utilities.squaredHypot(this.mLastPos.x - this.mDownPos.x, this.mLastPos.y - this.mDownPos.y) > this.mSquaredSlop) {
                                this.mPassedSlop = true;
                                this.mStartDragPos.set(this.mLastPos.x, this.mLastPos.y);
                                this.mDragTime = SystemClock.uptimeMillis();
                                if (isValidAssistantGestureAngle(this.mDownPos.x - this.mLastPos.x, this.mDownPos.y - this.mLastPos.y)) {
                                    setActive(motionEvent);
                                } else {
                                    this.mState = 2;
                                }
                            }
                        }
                    }
                }
            }
            if (this.mState != 2 && !this.mLaunchedAssistant) {
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{this.mLastProgress, 0.0f}).setDuration(300);
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        AssistantInputConsumer.this.lambda$onMotionEvent$1$AssistantInputConsumer(valueAnimator);
                    }
                });
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(AssistantInputConsumer.this.mContext).onAssistantProgress(0.0f);
                    }
                });
                duration.setInterpolator(Interpolators.DEACCEL_2);
                duration.start();
            }
            this.mPassedSlop = false;
            this.mState = 0;
        } else {
            this.mActivePointerId = motionEvent.getPointerId(0);
            this.mDownPos.set(motionEvent.getX(), motionEvent.getY());
            this.mLastPos.set(this.mDownPos);
            this.mTimeFraction = 0.0f;
        }
        this.mGestureDetector.accept(motionEvent);
        if (this.mState != 1) {
            this.mDelegate.onMotionEvent(motionEvent);
        }
    }

    public /* synthetic */ void lambda$onMotionEvent$1$AssistantInputConsumer(ValueAnimator valueAnimator) {
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).onAssistantProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void updateAssistantProgress() {
        if (!this.mLaunchedAssistant) {
            float min = Math.min((this.mDistance * 1.0f) / this.mDragDistThreshold, 1.0f);
            float f = this.mTimeFraction;
            this.mLastProgress = min * f;
            if (this.mDistance < this.mDragDistThreshold || f < 1.0f) {
                SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).onAssistantProgress(this.mLastProgress);
                return;
            }
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).onAssistantGestureCompletion(0.0f);
            startAssistantInternal();
        }
    }

    /* access modifiers changed from: private */
    public void startAssistantInternal() {
        StatefulActivity createdActivity = this.mActivityInterface.getCreatedActivity();
        if (createdActivity != null) {
            createdActivity.getRootView().performHapticFeedback(13, 1);
        }
        Bundle bundle = new Bundle();
        bundle.putInt(OPA_BUNDLE_TRIGGER, 83);
        bundle.putInt("invocation_type", 1);
        SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).startAssistant(bundle);
        this.mLaunchedAssistant = true;
    }

    /* access modifiers changed from: private */
    public boolean isValidAssistantGestureAngle(float f, float f2) {
        float degrees = (float) Math.toDegrees(Math.atan2((double) f2, (double) f));
        if (degrees > 90.0f) {
            degrees = 180.0f - degrees;
        }
        return degrees > ((float) this.mAngleThreshold) && degrees < 90.0f;
    }

    private class AssistantGestureListener extends GestureDetector.SimpleOnGestureListener {
        private AssistantGestureListener() {
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (!AssistantInputConsumer.this.isValidAssistantGestureAngle(f, -f2) || AssistantInputConsumer.this.mDistance < AssistantInputConsumer.this.mFlingDistThreshold || AssistantInputConsumer.this.mLaunchedAssistant || AssistantInputConsumer.this.mState == 2) {
                return true;
            }
            float unused = AssistantInputConsumer.this.mLastProgress = 1.0f;
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(AssistantInputConsumer.this.mContext).onAssistantGestureCompletion((float) Math.sqrt((double) ((f * f) + (f2 * f2))));
            AssistantInputConsumer.this.startAssistantInternal();
            return true;
        }
    }
}
