package com.android.quickstep.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.android.launcher3.Alarm;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.R;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.testing.TestProtocol;

public class MotionPauseDetector {
    private static final long FORCE_PAUSE_TIMEOUT = 300;
    private static final long HARDER_TRIGGER_TIMEOUT = 400;
    private static final float RAPID_DECELERATION_FACTOR = 0.6f;
    private final Context mContext;
    private boolean mDisallowPause;
    private final Alarm mForcePauseTimeout;
    private boolean mHasEverBeenPaused;
    private boolean mIsPaused;
    private final boolean mMakePauseHarderToTrigger;
    private OnMotionPauseListener mOnMotionPauseListener;
    private Float mPreviousVelocity;
    private long mSlowStartTime;
    private final float mSpeedFast;
    private final float mSpeedSlow;
    private final float mSpeedSomewhatFast;
    private final float mSpeedVerySlow;
    private final SystemVelocityProvider mVelocityProvider;

    public interface OnMotionPauseListener {
        void onMotionPauseChanged(boolean z) {
        }

        void onMotionPauseDetected();
    }

    public MotionPauseDetector(Context context) {
        this(context, false);
    }

    public MotionPauseDetector(Context context, boolean z) {
        this(context, z, 1);
    }

    public MotionPauseDetector(Context context, boolean z, int i) {
        this.mPreviousVelocity = null;
        this.mContext = context;
        Resources resources = context.getResources();
        this.mSpeedVerySlow = resources.getDimension(R.dimen.motion_pause_detector_speed_very_slow);
        this.mSpeedSlow = resources.getDimension(R.dimen.motion_pause_detector_speed_slow);
        this.mSpeedSomewhatFast = resources.getDimension(R.dimen.motion_pause_detector_speed_somewhat_fast);
        this.mSpeedFast = resources.getDimension(R.dimen.motion_pause_detector_speed_fast);
        Alarm alarm = new Alarm();
        this.mForcePauseTimeout = alarm;
        alarm.setOnAlarmListener(new OnAlarmListener() {
            public final void onAlarm(Alarm alarm) {
                MotionPauseDetector.this.lambda$new$0$MotionPauseDetector(alarm);
            }
        });
        this.mMakePauseHarderToTrigger = z;
        this.mVelocityProvider = new SystemVelocityProvider(i);
    }

    public /* synthetic */ void lambda$new$0$MotionPauseDetector(Alarm alarm) {
        updatePaused(true);
    }

    public void setOnMotionPauseListener(OnMotionPauseListener onMotionPauseListener) {
        this.mOnMotionPauseListener = onMotionPauseListener;
    }

    public void setDisallowPause(boolean z) {
        this.mDisallowPause = z;
        updatePaused(this.mIsPaused);
    }

    public void addPosition(MotionEvent motionEvent) {
        addPosition(motionEvent, 0);
    }

    public void addPosition(MotionEvent motionEvent, int i) {
        long j;
        if (TestProtocol.sForcePauseTimeout != null) {
            j = TestProtocol.sForcePauseTimeout.longValue();
        } else {
            j = this.mMakePauseHarderToTrigger ? HARDER_TRIGGER_TIMEOUT : 300;
        }
        this.mForcePauseTimeout.setAlarm(j);
        float addMotionEvent = this.mVelocityProvider.addMotionEvent(motionEvent, motionEvent.getPointerId(i));
        Float f = this.mPreviousVelocity;
        if (f != null) {
            checkMotionPaused(addMotionEvent, f.floatValue(), motionEvent.getEventTime());
        }
        this.mPreviousVelocity = Float.valueOf(addMotionEvent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0073, code lost:
        if ((r8 - r5.mSlowStartTime) >= HARDER_TRIGGER_TIMEOUT) goto L_0x001a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0016, code lost:
        if (r1 >= r6) goto L_0x0019;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkMotionPaused(float r6, float r7, long r8) {
        /*
            r5 = this;
            float r0 = java.lang.Math.abs(r6)
            float r1 = java.lang.Math.abs(r7)
            boolean r2 = r5.mIsPaused
            r3 = 1
            r4 = 0
            if (r2 == 0) goto L_0x001d
            float r6 = r5.mSpeedFast
            int r7 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r7 < 0) goto L_0x001a
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x0019
            goto L_0x001a
        L_0x0019:
            r3 = r4
        L_0x001a:
            r4 = r3
            goto L_0x007a
        L_0x001d:
            r2 = 0
            int r6 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0024
            r6 = r3
            goto L_0x0025
        L_0x0024:
            r6 = r4
        L_0x0025:
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 >= 0) goto L_0x002b
            r7 = r3
            goto L_0x002c
        L_0x002b:
            r7 = r4
        L_0x002c:
            if (r6 == r7) goto L_0x002f
            goto L_0x007a
        L_0x002f:
            float r6 = r5.mSpeedVerySlow
            int r7 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r7 >= 0) goto L_0x003b
            int r6 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x003b
            r6 = r3
            goto L_0x003c
        L_0x003b:
            r6 = r4
        L_0x003c:
            if (r6 != 0) goto L_0x0058
            boolean r7 = r5.mHasEverBeenPaused
            if (r7 != 0) goto L_0x0058
            r6 = 1058642330(0x3f19999a, float:0.6)
            float r1 = r1 * r6
            int r6 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r6 >= 0) goto L_0x004c
            r6 = r3
            goto L_0x004d
        L_0x004c:
            r6 = r4
        L_0x004d:
            if (r6 == 0) goto L_0x0057
            float r6 = r5.mSpeedSomewhatFast
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 >= 0) goto L_0x0057
            r6 = r3
            goto L_0x0058
        L_0x0057:
            r6 = r4
        L_0x0058:
            boolean r7 = r5.mMakePauseHarderToTrigger
            if (r7 == 0) goto L_0x0079
            float r6 = r5.mSpeedSlow
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            r0 = 0
            if (r6 >= 0) goto L_0x0076
            long r6 = r5.mSlowStartTime
            int r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r6 != 0) goto L_0x006c
            r5.mSlowStartTime = r8
        L_0x006c:
            long r6 = r5.mSlowStartTime
            long r8 = r8 - r6
            r6 = 400(0x190, double:1.976E-321)
            int r6 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r6 < 0) goto L_0x0019
            goto L_0x001a
        L_0x0076:
            r5.mSlowStartTime = r0
            goto L_0x007a
        L_0x0079:
            r4 = r6
        L_0x007a:
            r5.updatePaused(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.quickstep.util.MotionPauseDetector.checkMotionPaused(float, float, long):void");
    }

    private void updatePaused(boolean z) {
        boolean z2 = false;
        if (this.mDisallowPause) {
            z = false;
        }
        if (this.mIsPaused != z) {
            this.mIsPaused = z;
            if (!this.mHasEverBeenPaused && z) {
                z2 = true;
            }
            if (z) {
                AccessibilityManagerCompat.sendPauseDetectedEventToTest(this.mContext);
                this.mHasEverBeenPaused = true;
            }
            OnMotionPauseListener onMotionPauseListener = this.mOnMotionPauseListener;
            if (onMotionPauseListener != null) {
                if (z2) {
                    onMotionPauseListener.onMotionPauseDetected();
                }
                OnMotionPauseListener onMotionPauseListener2 = this.mOnMotionPauseListener;
                if (onMotionPauseListener2 != null) {
                    onMotionPauseListener2.onMotionPauseChanged(this.mIsPaused);
                }
            }
        }
    }

    public void clear() {
        this.mVelocityProvider.clear();
        this.mPreviousVelocity = null;
        setOnMotionPauseListener((OnMotionPauseListener) null);
        this.mHasEverBeenPaused = false;
        this.mIsPaused = false;
        this.mSlowStartTime = 0;
        this.mForcePauseTimeout.cancelAlarm();
    }

    public boolean isPaused() {
        return this.mIsPaused;
    }

    private static class SystemVelocityProvider {
        private final int mAxis;
        private final VelocityTracker mVelocityTracker = VelocityTracker.obtain();

        SystemVelocityProvider(int i) {
            this.mAxis = i;
        }

        public float addMotionEvent(MotionEvent motionEvent, int i) {
            this.mVelocityTracker.addMovement(motionEvent);
            this.mVelocityTracker.computeCurrentVelocity(1);
            if (this.mAxis == 0) {
                return this.mVelocityTracker.getXVelocity(i);
            }
            return this.mVelocityTracker.getYVelocity(i);
        }

        public void clear() {
            this.mVelocityTracker.clear();
        }
    }
}
