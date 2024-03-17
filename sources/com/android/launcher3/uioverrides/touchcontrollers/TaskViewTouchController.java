package com.android.launcher3.uioverrides.touchcontrollers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.touch.BaseSwipeDetector;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.touch.SingleAxisSwipeDetector;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.FlingBlockCheck;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.util.VibratorWrapper;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskThumbnailView;
import com.android.quickstep.views.TaskView;

public abstract class TaskViewTouchController<T extends BaseDraggingActivity> extends AnimatorListenerAdapter implements TouchController, SingleAxisSwipeDetector.Listener {
    private static final float ANIMATION_PROGRESS_FRACTION_MIDPOINT = 0.5f;
    private static final long MAX_TASK_DISMISS_ANIMATION_DURATION = 600;
    private static final long MIN_TASK_DISMISS_ANIMATION_DURATION = 300;
    public static final VibrationEffect TASK_DISMISS_VIBRATION_FALLBACK = VibratorWrapper.EFFECT_TEXTURE_TICK;
    public static final int TASK_DISMISS_VIBRATION_PRIMITIVE = (Utilities.ATLEAST_R ? 7 : -1);
    public static final float TASK_DISMISS_VIBRATION_PRIMITIVE_SCALE = 1.0f;
    protected final T mActivity;
    private boolean mAllowGoingDown;
    private boolean mAllowGoingUp;
    private AnimatorPlaybackController mCurrentAnimation;
    private boolean mCurrentAnimationIsGoingUp;
    private final SingleAxisSwipeDetector mDetector;
    private float mDisplacementShift;
    private float mEndDisplacement;
    private FlingBlockCheck mFlingBlockCheck = new FlingBlockCheck();
    private boolean mIsDismissHapticRunning = false;
    private final boolean mIsRtl;
    private boolean mNoIntercept;
    private Float mOverrideVelocity = null;
    private float mProgressMultiplier;
    private final RecentsView mRecentsView;
    private TaskView mTaskBeingDragged;
    private final int[] mTempCords = new int[2];

    /* access modifiers changed from: protected */
    public abstract boolean isRecentsInteractive();

    /* access modifiers changed from: protected */
    public abstract boolean isRecentsModal();

    /* access modifiers changed from: protected */
    public void onUserControlledAnimationCreated(AnimatorPlaybackController animatorPlaybackController) {
    }

    public TaskViewTouchController(T t) {
        this.mActivity = t;
        RecentsView recentsView = (RecentsView) t.getOverviewPanel();
        this.mRecentsView = recentsView;
        this.mIsRtl = Utilities.isRtl(t.getResources());
        this.mDetector = new SingleAxisSwipeDetector(t, this, recentsView.getPagedOrientationHandler().getUpDownSwipeDirection());
    }

    private boolean canInterceptTouch(MotionEvent motionEvent) {
        if ((motionEvent.getEdgeFlags() & 256) != 0) {
            AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
            if (animatorPlaybackController != null) {
                animatorPlaybackController.getAnimationPlayer().end();
            }
            return false;
        }
        AnimatorPlaybackController animatorPlaybackController2 = this.mCurrentAnimation;
        if (animatorPlaybackController2 != null) {
            animatorPlaybackController2.forceFinishIfCloseToEnd();
        }
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenViewWithType(this.mActivity, AbstractFloatingView.TYPE_ACCESSIBLE) != null) {
            return false;
        }
        return isRecentsInteractive();
    }

    public void onAnimationCancel(Animator animator) {
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null && animator == animatorPlaybackController.getTarget()) {
            clearState();
        }
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        int i = 3;
        boolean z = true;
        if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.mCurrentAnimation == null) {
            clearState();
        }
        if (motionEvent.getAction() == 0) {
            boolean z2 = !canInterceptTouch(motionEvent);
            this.mNoIntercept = z2;
            if (z2) {
                return false;
            }
            if (this.mCurrentAnimation == null) {
                this.mTaskBeingDragged = null;
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mRecentsView.getTaskViewCount()) {
                        break;
                    }
                    TaskView taskViewAt = this.mRecentsView.getTaskViewAt(i2);
                    if (!this.mRecentsView.isTaskViewVisible(taskViewAt) || !this.mActivity.getDragLayer().isEventOverView(taskViewAt, motionEvent)) {
                        i2++;
                    } else if (isRecentsModal()) {
                        this.mTaskBeingDragged = null;
                    } else {
                        this.mTaskBeingDragged = taskViewAt;
                        int upDirection = this.mRecentsView.getPagedOrientationHandler().getUpDirection(this.mIsRtl);
                        this.mAllowGoingUp = true;
                        boolean z3 = i2 == this.mRecentsView.getCurrentPage() && DisplayController.getNavigationMode(this.mActivity).hasGestures && (!this.mRecentsView.showAsGrid() || this.mTaskBeingDragged.isFocusedTask()) && this.mRecentsView.isTaskInExpectedScrollPosition(i2);
                        this.mAllowGoingDown = z3;
                        if (!z3) {
                            i = upDirection;
                        }
                    }
                }
                i = 0;
                if (this.mTaskBeingDragged == null) {
                    this.mNoIntercept = true;
                    return false;
                }
                z = false;
            }
            this.mDetector.setDetectableScrollConditions(i, z);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(motionEvent);
        return this.mDetector.isDraggingOrSettling();
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        return this.mDetector.onTouchEvent(motionEvent);
    }

    private void reInitAnimationController(boolean z) {
        Interpolator interpolator;
        PendingAnimation pendingAnimation;
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController != null && this.mCurrentAnimationIsGoingUp == z) {
            return;
        }
        if (z && !this.mAllowGoingUp) {
            return;
        }
        if (z || this.mAllowGoingDown) {
            if (animatorPlaybackController != null) {
                animatorPlaybackController.setPlayFraction(0.0f);
                this.mCurrentAnimation.getTarget().removeListener(this);
                this.mCurrentAnimation.dispatchOnCancel();
            }
            PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
            this.mCurrentAnimationIsGoingUp = z;
            BaseDragLayer dragLayer = this.mActivity.getDragLayer();
            int secondaryDimension = pagedOrientationHandler.getSecondaryDimension(dragLayer);
            long j = (long) (secondaryDimension * 2);
            int taskDragDisplacementFactor = pagedOrientationHandler.getTaskDragDisplacementFactor(this.mIsRtl);
            int secondaryDimension2 = pagedOrientationHandler.getSecondaryDimension(this.mTaskBeingDragged);
            if (z) {
                interpolator = Interpolators.LINEAR;
                pendingAnimation = this.mRecentsView.createTaskDismissAnimation(this.mTaskBeingDragged, true, true, j, false);
                this.mEndDisplacement = (float) (-secondaryDimension2);
            } else {
                interpolator = Interpolators.ZOOM_IN;
                PendingAnimation createTaskLaunchAnimation = this.mRecentsView.createTaskLaunchAnimation(this.mTaskBeingDragged, j, interpolator);
                TaskThumbnailView thumbnail = this.mTaskBeingDragged.getThumbnail();
                this.mTempCords[1] = pagedOrientationHandler.getSecondaryDimension(thumbnail);
                dragLayer.getDescendantCoordRelativeToSelf((View) thumbnail, this.mTempCords);
                this.mEndDisplacement = (float) (secondaryDimension - this.mTempCords[1]);
                pendingAnimation = createTaskLaunchAnimation;
            }
            this.mEndDisplacement *= (float) taskDragDisplacementFactor;
            AnimatorPlaybackController createPlaybackController = pendingAnimation.createPlaybackController();
            this.mCurrentAnimation = createPlaybackController;
            createPlaybackController.getTarget().setInterpolator(interpolator);
            onUserControlledAnimationCreated(this.mCurrentAnimation);
            this.mCurrentAnimation.getTarget().addListener(this);
            this.mCurrentAnimation.dispatchOnStart();
            this.mProgressMultiplier = 1.0f / this.mEndDisplacement;
        }
    }

    public void onDragStart(boolean z, float f) {
        PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
        AnimatorPlaybackController animatorPlaybackController = this.mCurrentAnimation;
        if (animatorPlaybackController == null) {
            reInitAnimationController(pagedOrientationHandler.isGoingUp(f, this.mIsRtl));
            this.mDisplacementShift = 0.0f;
        } else {
            this.mDisplacementShift = animatorPlaybackController.getProgressFraction() / this.mProgressMultiplier;
            this.mCurrentAnimation.pause();
        }
        this.mFlingBlockCheck.unblockFling();
        this.mOverrideVelocity = null;
    }

    public boolean onDrag(float f) {
        boolean z;
        PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
        float f2 = f + this.mDisplacementShift;
        if (f2 == 0.0f) {
            z = this.mCurrentAnimationIsGoingUp;
        } else {
            z = pagedOrientationHandler.isGoingUp(f2, this.mIsRtl);
        }
        if (z != this.mCurrentAnimationIsGoingUp) {
            reInitAnimationController(z);
            this.mFlingBlockCheck.blockFling();
        } else {
            this.mFlingBlockCheck.onEvent();
        }
        if (!z) {
            this.mCurrentAnimation.setPlayFraction(Utilities.boundToRange(f2 * this.mProgressMultiplier, 0.0f, 1.0f));
            return true;
        } else if (this.mCurrentAnimation.getProgressFraction() < 0.5f) {
            this.mCurrentAnimation.setPlayFraction(Utilities.boundToRange((f2 * this.mProgressMultiplier) / 2.0f, 0.0f, 1.0f));
            return true;
        } else {
            int i = R.dimen.default_task_dismiss_drag_velocity;
            if (this.mRecentsView.showAsGrid()) {
                i = this.mTaskBeingDragged.isFocusedTask() ? R.dimen.default_task_dismiss_drag_velocity_grid_focus_task : R.dimen.default_task_dismiss_drag_velocity_grid;
            }
            this.mOverrideVelocity = Float.valueOf(-this.mTaskBeingDragged.getResources().getDimension(i));
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 1, 0.0f, 0.0f, 0);
            this.mDetector.onTouchEvent(obtain);
            obtain.recycle();
            return true;
        }
    }

    public void onDragEnd(float f) {
        Float f2 = this.mOverrideVelocity;
        if (f2 != null) {
            f = f2.floatValue();
            this.mOverrideVelocity = null;
        }
        float dimension = this.mTaskBeingDragged.getResources().getDimension(R.dimen.max_task_dismiss_drag_velocity);
        float boundToRange = Utilities.boundToRange(f, -dimension, dimension);
        boolean isFling = this.mDetector.isFling(boundToRange);
        boolean z = false;
        boolean z2 = isFling && this.mFlingBlockCheck.isBlocked();
        if (z2) {
            isFling = false;
        }
        PagedOrientationHandler pagedOrientationHandler = this.mRecentsView.getPagedOrientationHandler();
        boolean isGoingUp = pagedOrientationHandler.isGoingUp(boundToRange, this.mIsRtl);
        float progressFraction = this.mCurrentAnimation.getProgressFraction();
        float interpolatedProgress = this.mCurrentAnimation.getInterpolatedProgress();
        if (!isFling ? interpolatedProgress > 0.5f : isGoingUp == this.mCurrentAnimationIsGoingUp) {
            z = true;
        }
        if (z) {
            progressFraction = 1.0f - progressFraction;
        }
        long calculateDuration = BaseSwipeDetector.calculateDuration(boundToRange, progressFraction);
        if (z2 && !z) {
            calculateDuration *= (long) LauncherAnimUtils.blockedFlingDurationFactor(boundToRange);
        }
        long boundToRange2 = Utilities.boundToRange(calculateDuration, 300, (long) MAX_TASK_DISMISS_ANIMATION_DURATION);
        this.mCurrentAnimation.setEndAction(new Runnable() {
            public final void run() {
                TaskViewTouchController.this.clearState();
            }
        });
        this.mCurrentAnimation.startWithVelocity(this.mActivity, z, boundToRange * ((float) pagedOrientationHandler.getSecondaryTranslationDirectionFactor()), this.mEndDisplacement, boundToRange2);
        if (isGoingUp && z && !this.mIsDismissHapticRunning) {
            VibratorWrapper.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mActivity).vibrate(TASK_DISMISS_VIBRATION_PRIMITIVE, 1.0f, TASK_DISMISS_VIBRATION_FALLBACK);
            this.mIsDismissHapticRunning = true;
        }
    }

    /* access modifiers changed from: private */
    public void clearState() {
        this.mDetector.finishedScrolling();
        this.mDetector.setDetectableScrollConditions(0, false);
        this.mTaskBeingDragged = null;
        this.mCurrentAnimation = null;
        this.mIsDismissHapticRunning = false;
    }
}
