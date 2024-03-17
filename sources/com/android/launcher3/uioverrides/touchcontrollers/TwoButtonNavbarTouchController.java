package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.ValueAnimator;
import android.os.SystemClock;
import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.util.LayoutUtils;
import com.android.quickstep.views.AllAppsEduView;

public class TwoButtonNavbarTouchController extends AbstractStateChangeTouchController {
    private static final int MAX_NUM_SWIPES_TO_TRIGGER_EDU = 3;
    private static final String TAG = "2BtnNavbarTouchCtrl";
    private int mContinuousTouchCount;
    private boolean mFinishFastOnSecondTouch;
    private final boolean mIsTransposed;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TwoButtonNavbarTouchController(Launcher launcher) {
        super(launcher, launcher.getDeviceProfile().isVerticalBarLayout() ? SingleAxisSwipeDetector.HORIZONTAL : SingleAxisSwipeDetector.VERTICAL);
        this.mContinuousTouchCount = 0;
        this.mIsTransposed = launcher.getDeviceProfile().isVerticalBarLayout();
    }

    /* access modifiers changed from: protected */
    public boolean canInterceptTouch(MotionEvent motionEvent) {
        boolean canInterceptTouchInternal = canInterceptTouchInternal(motionEvent);
        if (!canInterceptTouchInternal) {
            this.mContinuousTouchCount = 0;
        }
        return canInterceptTouchInternal;
    }

    private boolean canInterceptTouchInternal(MotionEvent motionEvent) {
        if (this.mCurrentAnimation != null) {
            if (this.mFinishFastOnSecondTouch) {
                this.mCurrentAnimation.getAnimationPlayer().end();
            }
            return true;
        } else if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null || (motionEvent.getEdgeFlags() & 256) == 0) {
            return false;
        } else {
            if (this.mIsTransposed || !this.mLauncher.isInState(LauncherState.OVERVIEW)) {
                return this.mLauncher.isInState(LauncherState.NORMAL);
            }
            return true;
        }
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onControllerInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState launcherState, boolean z) {
        boolean z2 = true;
        if (this.mIsTransposed) {
            if (this.mLauncher.getDeviceProfile().isSeascape() != z) {
                z2 = false;
            }
            return z2 ? LauncherState.HINT_STATE_TWO_BUTTON : LauncherState.NORMAL;
        }
        if (this.mStartState != null) {
            launcherState = this.mStartState;
        }
        if (launcherState != LauncherState.OVERVIEW) {
            z2 = false;
        }
        return z ^ z2 ? LauncherState.HINT_STATE_TWO_BUTTON : LauncherState.NORMAL;
    }

    /* access modifiers changed from: protected */
    public void onReinitToState(LauncherState launcherState) {
        super.onReinitToState(launcherState);
    }

    /* access modifiers changed from: protected */
    public void updateSwipeCompleteAnimation(ValueAnimator valueAnimator, long j, LauncherState launcherState, float f, boolean z) {
        super.updateSwipeCompleteAnimation(valueAnimator, j, launcherState, f, z);
        this.mFinishFastOnSecondTouch = !this.mIsTransposed && this.mFromState == LauncherState.NORMAL;
        if (launcherState == LauncherState.HINT_STATE_TWO_BUTTON) {
            valueAnimator.setDuration(0);
        }
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return LayoutUtils.getDefaultSwipeHeight(this.mLauncher, this.mLauncher.getDeviceProfile());
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation() {
        float shiftRange = getShiftRange();
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, (long) (2.0f * shiftRange));
        return ((float) (this.mLauncher.getDeviceProfile().isSeascape() ? 1 : -1)) / shiftRange;
    }

    /* access modifiers changed from: protected */
    public void updateProgress(float f) {
        super.updateProgress(f);
        if (f >= 1.0f && this.mToState == LauncherState.HINT_STATE_TWO_BUTTON) {
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 1, 0.0f, 0.0f, 0);
            this.mDetector.onTouchEvent(obtain);
            obtain.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void onSwipeInteractionCompleted(LauncherState launcherState) {
        super.lambda$onDragEnd$0$AbstractStateChangeTouchController(launcherState);
        if (!this.mIsTransposed) {
            this.mContinuousTouchCount++;
        }
        if (this.mStartState == LauncherState.NORMAL && launcherState == LauncherState.HINT_STATE_TWO_BUTTON) {
            SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mLauncher).onOverviewShown(true, TAG);
        } else if (launcherState == LauncherState.NORMAL && this.mContinuousTouchCount >= 3) {
            this.mContinuousTouchCount = 0;
            if (AbstractFloatingView.getOpenView(this.mLauncher, 512) == null) {
                AllAppsEduView.show(this.mLauncher);
            }
        }
        this.mStartState = null;
    }
}
