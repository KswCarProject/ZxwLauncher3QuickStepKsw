package com.android.quickstep.interaction;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.launcher3.R;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.util.DisplayController;
import com.android.quickstep.util.MotionPauseDetector;
import com.android.quickstep.util.NavBarPosition;
import com.android.quickstep.util.TriggerSwipeUpTouchTracker;
import com.android.quickstep.util.VibratorWrapper;
import com.android.systemui.shared.system.QuickStepContract;

public class NavBarGestureHandler implements View.OnTouchListener, TriggerSwipeUpTouchTracker.OnSwipeUpListener, MotionPauseDetector.OnMotionPauseListener {
    private static final String LOG_TAG = "NavBarGestureHandler";
    private static final long RETRACT_GESTURE_ANIMATION_DURATION_MS = 300;
    private final int mAssistantAngleThreshold;
    /* access modifiers changed from: private */
    public float mAssistantDistance;
    private final float mAssistantDragDistThreshold;
    private long mAssistantDragStartTime;
    /* access modifiers changed from: private */
    public final float mAssistantFlingDistThreshold;
    private boolean mAssistantGestureActive;
    private final GestureDetector mAssistantGestureDetector;
    /* access modifiers changed from: private */
    public float mAssistantLastProgress;
    private final RectF mAssistantLeftRegion;
    private final RectF mAssistantRightRegion;
    private final float mAssistantSquaredSlop;
    private final PointF mAssistantStartDragPos = new PointF();
    private float mAssistantTimeFraction;
    private final long mAssistantTimeThreshold;
    private final int mBottomGestureHeight;
    private final Context mContext;
    private final Point mDisplaySize;
    private final PointF mDownPos = new PointF();
    /* access modifiers changed from: private */
    public NavBarGestureAttemptCallback mGestureCallback;
    private final PointF mLastPos = new PointF();
    /* access modifiers changed from: private */
    public boolean mLaunchedAssistant;
    private final MotionPauseDetector mMotionPauseDetector;
    private boolean mPassedAssistantSlop;
    private final TriggerSwipeUpTouchTracker mSwipeUpTouchTracker;
    /* access modifiers changed from: private */
    public boolean mTouchCameFromAssistantCorner;
    private boolean mTouchCameFromNavBar;

    interface NavBarGestureAttemptCallback {
        void onMotionPaused(boolean z) {
        }

        void onNavBarGestureAttempted(NavBarGestureResult navBarGestureResult, PointF pointF);

        void setAssistantProgress(float f) {
        }

        void setNavBarGestureProgress(Float f) {
        }
    }

    enum NavBarGestureResult {
        UNKNOWN,
        HOME_GESTURE_COMPLETED,
        OVERVIEW_GESTURE_COMPLETED,
        HOME_NOT_STARTED_TOO_FAR_FROM_EDGE,
        OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE,
        HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION,
        HOME_OR_OVERVIEW_CANCELLED,
        ASSISTANT_COMPLETED,
        ASSISTANT_NOT_STARTED_BAD_ANGLE,
        ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT
    }

    NavBarGestureHandler(Context context) {
        Point point = new Point();
        this.mDisplaySize = point;
        RectF rectF = new RectF();
        this.mAssistantLeftRegion = rectF;
        RectF rectF2 = new RectF();
        this.mAssistantRightRegion = rectF2;
        this.mContext = context;
        context.getDisplay();
        DisplayController.Info info = DisplayController.INSTANCE.lambda$get$1$MainThreadInitializedObject(context).getInfo();
        int i = info.rotation;
        Point point2 = info.currentSize;
        point.set(point2.x, point2.y);
        this.mSwipeUpTouchTracker = new TriggerSwipeUpTouchTracker(context, true, new NavBarPosition(DisplayController.NavigationMode.NO_BUTTON, i), (Runnable) null, this);
        this.mMotionPauseDetector = new MotionPauseDetector(context);
        Resources resources = context.getResources();
        int navbarSize = ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, resources);
        this.mBottomGestureHeight = navbarSize;
        this.mAssistantDragDistThreshold = resources.getDimension(R.dimen.gestures_assistant_drag_threshold);
        this.mAssistantFlingDistThreshold = resources.getDimension(R.dimen.gestures_assistant_fling_threshold);
        this.mAssistantTimeThreshold = (long) resources.getInteger(R.integer.assistant_gesture_min_time_threshold);
        this.mAssistantAngleThreshold = resources.getInteger(R.integer.assistant_gesture_corner_deg_threshold);
        this.mAssistantGestureDetector = new GestureDetector(context, new AssistantGestureListener());
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.gestures_assistant_width);
        float max = Math.max((float) navbarSize, QuickStepContract.getWindowCornerRadius(context));
        float f = (float) point.y;
        rectF2.bottom = f;
        rectF.bottom = f;
        float f2 = ((float) point.y) - max;
        rectF2.top = f2;
        rectF.top = f2;
        rectF.left = 0.0f;
        rectF.right = (float) dimensionPixelSize;
        rectF2.right = (float) point.x;
        rectF2.left = (float) (point.x - dimensionPixelSize);
        float scaledTouchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        this.mAssistantSquaredSlop = scaledTouchSlop * scaledTouchSlop;
    }

    /* access modifiers changed from: package-private */
    public void registerNavBarGestureAttemptCallback(NavBarGestureAttemptCallback navBarGestureAttemptCallback) {
        this.mGestureCallback = navBarGestureAttemptCallback;
    }

    /* access modifiers changed from: package-private */
    public void unregisterNavBarGestureAttemptCallback() {
        this.mGestureCallback = null;
    }

    public void onSwipeUp(boolean z, PointF pointF) {
        NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
        if (navBarGestureAttemptCallback != null && !this.mAssistantGestureActive) {
            if (this.mTouchCameFromNavBar) {
                navBarGestureAttemptCallback.onNavBarGestureAttempted(z ? NavBarGestureResult.HOME_GESTURE_COMPLETED : NavBarGestureResult.OVERVIEW_GESTURE_COMPLETED, pointF);
            } else {
                navBarGestureAttemptCallback.onNavBarGestureAttempted(z ? NavBarGestureResult.HOME_NOT_STARTED_TOO_FAR_FROM_EDGE : NavBarGestureResult.OVERVIEW_NOT_STARTED_TOO_FAR_FROM_EDGE, pointF);
            }
        }
    }

    public void onSwipeUpCancelled() {
        NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
        if (navBarGestureAttemptCallback != null && !this.mAssistantGestureActive) {
            navBarGestureAttemptCallback.onNavBarGestureAttempted(NavBarGestureResult.HOME_OR_OVERVIEW_CANCELLED, new PointF());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0015, code lost:
        if (r9 != 3) goto L_0x0188;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.View r9, android.view.MotionEvent r10) {
        /*
            r8 = this;
            int r9 = r10.getAction()
            com.android.quickstep.util.TriggerSwipeUpTouchTracker r0 = r8.mSwipeUpTouchTracker
            boolean r0 = r0.interceptedTouch()
            r1 = 0
            r2 = 1
            if (r9 == 0) goto L_0x011a
            r3 = 0
            r4 = 2
            if (r9 == r2) goto L_0x00c4
            if (r9 == r4) goto L_0x0019
            r5 = 3
            if (r9 == r5) goto L_0x00c4
            goto L_0x0188
        L_0x0019:
            android.graphics.PointF r9 = r8.mLastPos
            float r4 = r10.getX()
            float r5 = r10.getY()
            r9.set(r4, r5)
            boolean r9 = r8.mAssistantGestureActive
            if (r9 != 0) goto L_0x002c
            goto L_0x0188
        L_0x002c:
            boolean r9 = r8.mPassedAssistantSlop
            if (r9 != 0) goto L_0x008b
            android.graphics.PointF r9 = r8.mLastPos
            float r9 = r9.x
            android.graphics.PointF r3 = r8.mDownPos
            float r3 = r3.x
            float r9 = r9 - r3
            android.graphics.PointF r3 = r8.mLastPos
            float r3 = r3.y
            android.graphics.PointF r4 = r8.mDownPos
            float r4 = r4.y
            float r3 = r3 - r4
            float r9 = com.android.launcher3.Utilities.squaredHypot(r9, r3)
            float r3 = r8.mAssistantSquaredSlop
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 <= 0) goto L_0x0188
            r8.mPassedAssistantSlop = r2
            android.graphics.PointF r9 = r8.mAssistantStartDragPos
            android.graphics.PointF r3 = r8.mLastPos
            float r3 = r3.x
            android.graphics.PointF r4 = r8.mLastPos
            float r4 = r4.y
            r9.set(r3, r4)
            long r3 = android.os.SystemClock.uptimeMillis()
            r8.mAssistantDragStartTime = r3
            android.graphics.PointF r9 = r8.mDownPos
            float r9 = r9.x
            android.graphics.PointF r3 = r8.mLastPos
            float r3 = r3.x
            float r9 = r9 - r3
            android.graphics.PointF r3 = r8.mDownPos
            float r3 = r3.y
            android.graphics.PointF r4 = r8.mLastPos
            float r4 = r4.y
            float r3 = r3 - r4
            boolean r9 = r8.isValidAssistantGestureAngle(r9, r3)
            r8.mAssistantGestureActive = r9
            if (r9 != 0) goto L_0x0188
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r9 = r8.mGestureCallback
            if (r9 == 0) goto L_0x0188
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r3 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE
            android.graphics.PointF r4 = new android.graphics.PointF
            r4.<init>()
            r9.onNavBarGestureAttempted(r3, r4)
            goto L_0x0188
        L_0x008b:
            android.graphics.PointF r9 = r8.mLastPos
            float r9 = r9.x
            android.graphics.PointF r4 = r8.mAssistantStartDragPos
            float r4 = r4.x
            float r9 = r9 - r4
            double r4 = (double) r9
            android.graphics.PointF r9 = r8.mLastPos
            float r9 = r9.y
            android.graphics.PointF r6 = r8.mAssistantStartDragPos
            float r6 = r6.y
            float r9 = r9 - r6
            double r6 = (double) r9
            double r4 = java.lang.Math.hypot(r4, r6)
            float r9 = (float) r4
            r8.mAssistantDistance = r9
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 < 0) goto L_0x0188
            long r3 = android.os.SystemClock.uptimeMillis()
            long r5 = r8.mAssistantDragStartTime
            long r3 = r3 - r5
            float r9 = (float) r3
            r3 = 1065353216(0x3f800000, float:1.0)
            float r9 = r9 * r3
            long r4 = r8.mAssistantTimeThreshold
            float r4 = (float) r4
            float r9 = r9 / r4
            float r9 = java.lang.Math.min(r9, r3)
            r8.mAssistantTimeFraction = r9
            r8.updateAssistantProgress()
            goto L_0x0188
        L_0x00c4:
            com.android.quickstep.util.MotionPauseDetector r9 = r8.mMotionPauseDetector
            r9.clear()
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r9 = r8.mGestureCallback
            if (r9 == 0) goto L_0x00e0
            if (r0 != 0) goto L_0x00e0
            boolean r5 = r8.mTouchCameFromNavBar
            if (r5 == 0) goto L_0x00e0
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r0 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.HOME_OR_OVERVIEW_NOT_STARTED_WRONG_SWIPE_DIRECTION
            android.graphics.PointF r3 = new android.graphics.PointF
            r3.<init>()
            r9.onNavBarGestureAttempted(r0, r3)
            r0 = r2
            goto L_0x0188
        L_0x00e0:
            boolean r5 = r8.mAssistantGestureActive
            if (r5 == 0) goto L_0x0116
            boolean r5 = r8.mLaunchedAssistant
            if (r5 != 0) goto L_0x0116
            if (r9 == 0) goto L_0x0116
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureResult r5 = com.android.quickstep.interaction.NavBarGestureHandler.NavBarGestureResult.ASSISTANT_NOT_STARTED_SWIPE_TOO_SHORT
            android.graphics.PointF r6 = new android.graphics.PointF
            r6.<init>()
            r9.onNavBarGestureAttempted(r5, r6)
            float[] r9 = new float[r4]
            float r4 = r8.mAssistantLastProgress
            r9[r1] = r4
            r9[r2] = r3
            android.animation.ValueAnimator r9 = android.animation.ValueAnimator.ofFloat(r9)
            r3 = 300(0x12c, double:1.48E-321)
            android.animation.ValueAnimator r9 = r9.setDuration(r3)
            com.android.quickstep.interaction.-$$Lambda$NavBarGestureHandler$jsbVIeBpyw9lElyDXHlB6KgEmy8 r3 = new com.android.quickstep.interaction.-$$Lambda$NavBarGestureHandler$jsbVIeBpyw9lElyDXHlB6KgEmy8
            r3.<init>()
            r9.addUpdateListener(r3)
            android.view.animation.Interpolator r3 = com.android.launcher3.anim.Interpolators.DEACCEL_2
            r9.setInterpolator(r3)
            r9.start()
        L_0x0116:
            r8.mPassedAssistantSlop = r1
            goto L_0x0188
        L_0x011a:
            android.graphics.PointF r9 = r8.mDownPos
            float r3 = r10.getX()
            float r4 = r10.getY()
            r9.set(r3, r4)
            android.graphics.PointF r9 = r8.mLastPos
            android.graphics.PointF r3 = r8.mDownPos
            r9.set(r3)
            android.graphics.RectF r9 = r8.mAssistantLeftRegion
            float r3 = r10.getX()
            float r4 = r10.getY()
            boolean r9 = r9.contains(r3, r4)
            if (r9 != 0) goto L_0x0151
            android.graphics.RectF r9 = r8.mAssistantRightRegion
            float r3 = r10.getX()
            float r4 = r10.getY()
            boolean r9 = r9.contains(r3, r4)
            if (r9 == 0) goto L_0x014f
            goto L_0x0151
        L_0x014f:
            r9 = r1
            goto L_0x0152
        L_0x0151:
            r9 = r2
        L_0x0152:
            r8.mTouchCameFromAssistantCorner = r9
            r8.mAssistantGestureActive = r9
            if (r9 != 0) goto L_0x016a
            android.graphics.PointF r9 = r8.mDownPos
            float r9 = r9.y
            android.graphics.Point r3 = r8.mDisplaySize
            int r3 = r3.y
            int r4 = r8.mBottomGestureHeight
            int r3 = r3 - r4
            float r3 = (float) r3
            int r9 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r9 < 0) goto L_0x016a
            r9 = r2
            goto L_0x016b
        L_0x016a:
            r9 = r1
        L_0x016b:
            r8.mTouchCameFromNavBar = r9
            if (r9 != 0) goto L_0x0177
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r9 = r8.mGestureCallback
            if (r9 == 0) goto L_0x0177
            r3 = 0
            r9.setNavBarGestureProgress(r3)
        L_0x0177:
            r8.mLaunchedAssistant = r1
            com.android.quickstep.util.TriggerSwipeUpTouchTracker r9 = r8.mSwipeUpTouchTracker
            r9.init()
            com.android.quickstep.util.MotionPauseDetector r9 = r8.mMotionPauseDetector
            r9.clear()
            com.android.quickstep.util.MotionPauseDetector r9 = r8.mMotionPauseDetector
            r9.setOnMotionPauseListener(r8)
        L_0x0188:
            boolean r9 = r8.mTouchCameFromNavBar
            if (r9 == 0) goto L_0x01a0
            com.android.quickstep.interaction.NavBarGestureHandler$NavBarGestureAttemptCallback r9 = r8.mGestureCallback
            if (r9 == 0) goto L_0x01a0
            float r3 = r10.getY()
            android.graphics.PointF r4 = r8.mDownPos
            float r4 = r4.y
            float r3 = r3 - r4
            java.lang.Float r3 = java.lang.Float.valueOf(r3)
            r9.setNavBarGestureProgress(r3)
        L_0x01a0:
            com.android.quickstep.util.TriggerSwipeUpTouchTracker r9 = r8.mSwipeUpTouchTracker
            r9.onMotionEvent(r10)
            android.view.GestureDetector r9 = r8.mAssistantGestureDetector
            r9.onTouchEvent(r10)
            com.android.quickstep.util.MotionPauseDetector r9 = r8.mMotionPauseDetector
            r9.addPosition(r10)
            com.android.quickstep.util.MotionPauseDetector r9 = r8.mMotionPauseDetector
            android.graphics.PointF r10 = r8.mLastPos
            float r10 = r10.y
            android.graphics.Point r3 = r8.mDisplaySize
            int r3 = r3.y
            int r4 = r8.mBottomGestureHeight
            int r3 = r3 - r4
            float r3 = (float) r3
            int r10 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r10 < 0) goto L_0x01c2
            r1 = r2
        L_0x01c2:
            r9.setDisallowPause(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.interaction.NavBarGestureHandler.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    public /* synthetic */ void lambda$onTouch$0$NavBarGestureHandler(ValueAnimator valueAnimator) {
        this.mGestureCallback.setAssistantProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: package-private */
    public boolean onInterceptTouch(MotionEvent motionEvent) {
        return this.mAssistantLeftRegion.contains(motionEvent.getX(), motionEvent.getY()) || this.mAssistantRightRegion.contains(motionEvent.getX(), motionEvent.getY()) || motionEvent.getY() >= ((float) (this.mDisplaySize.y - this.mBottomGestureHeight));
    }

    public void onMotionPauseChanged(boolean z) {
        this.mGestureCallback.onMotionPaused(z);
    }

    public void onMotionPauseDetected() {
        VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).vibrate(VibratorWrapper.OVERVIEW_HAPTIC);
    }

    /* access modifiers changed from: private */
    public boolean isValidAssistantGestureAngle(float f, float f2) {
        float degrees = (float) Math.toDegrees(Math.atan2((double) f2, (double) f));
        if (degrees > 90.0f) {
            degrees = 180.0f - degrees;
        }
        return degrees > ((float) this.mAssistantAngleThreshold) && degrees < 90.0f;
    }

    private void updateAssistantProgress() {
        if (!this.mLaunchedAssistant) {
            float min = Math.min((this.mAssistantDistance * 1.0f) / this.mAssistantDragDistThreshold, 1.0f);
            float f = this.mAssistantTimeFraction;
            float f2 = min * f;
            this.mAssistantLastProgress = f2;
            if (this.mAssistantDistance < this.mAssistantDragDistThreshold || f < 1.0f) {
                NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
                if (navBarGestureAttemptCallback != null) {
                    navBarGestureAttemptCallback.setAssistantProgress(f2);
                    return;
                }
                return;
            }
            startAssistant(new PointF());
        }
    }

    /* access modifiers changed from: private */
    public void startAssistant(PointF pointF) {
        NavBarGestureAttemptCallback navBarGestureAttemptCallback = this.mGestureCallback;
        if (navBarGestureAttemptCallback != null) {
            navBarGestureAttemptCallback.onNavBarGestureAttempted(NavBarGestureResult.ASSISTANT_COMPLETED, pointF);
        }
        VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mContext).vibrate(VibratorWrapper.EFFECT_CLICK);
        this.mLaunchedAssistant = true;
    }

    private class AssistantGestureListener extends GestureDetector.SimpleOnGestureListener {
        private AssistantGestureListener() {
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (NavBarGestureHandler.this.mLaunchedAssistant || !NavBarGestureHandler.this.mTouchCameFromAssistantCorner) {
                return true;
            }
            PointF pointF = new PointF(f, f2);
            if (!NavBarGestureHandler.this.isValidAssistantGestureAngle(f, -f2)) {
                if (NavBarGestureHandler.this.mGestureCallback == null) {
                    return true;
                }
                NavBarGestureHandler.this.mGestureCallback.onNavBarGestureAttempted(NavBarGestureResult.ASSISTANT_NOT_STARTED_BAD_ANGLE, pointF);
                return true;
            } else if (NavBarGestureHandler.this.mAssistantDistance < NavBarGestureHandler.this.mAssistantFlingDistThreshold) {
                return true;
            } else {
                float unused = NavBarGestureHandler.this.mAssistantLastProgress = 1.0f;
                NavBarGestureHandler.this.startAssistant(pointF);
                return true;
            }
        }
    }
}
