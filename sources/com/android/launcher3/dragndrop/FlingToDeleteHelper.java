package com.android.launcher3.dragndrop;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.util.FlingAnimation;

public class FlingToDeleteHelper {
    private static final float MAX_FLING_DEGREES = 35.0f;
    private ButtonDropTarget mDropTarget;
    private final Launcher mLauncher;
    private VelocityTracker mVelocityTracker;

    public FlingToDeleteHelper(Launcher launcher) {
        this.mLauncher = launcher;
    }

    public void recordMotionEvent(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
    }

    public void releaseVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public DropTarget getDropTarget() {
        return this.mDropTarget;
    }

    public Runnable getFlingAnimation(DropTarget.DragObject dragObject, DragOptions dragOptions) {
        if (dragOptions == null) {
            return null;
        }
        PointF isFlingingToDelete = isFlingingToDelete();
        dragOptions.isFlingToDelete = isFlingingToDelete != null;
        if (!dragOptions.isFlingToDelete) {
            return null;
        }
        return new FlingAnimation(dragObject, isFlingingToDelete, this.mDropTarget, this.mLauncher, dragOptions);
    }

    private PointF isFlingingToDelete() {
        if (this.mVelocityTracker == null) {
            return null;
        }
        if (this.mDropTarget == null) {
            this.mDropTarget = (ButtonDropTarget) this.mLauncher.findViewById(R.id.delete_target_text);
        }
        ButtonDropTarget buttonDropTarget = this.mDropTarget;
        if (buttonDropTarget != null && buttonDropTarget.isDropEnabled()) {
            this.mVelocityTracker.computeCurrentVelocity(1000, (float) ViewConfiguration.get(this.mLauncher).getScaledMaximumFlingVelocity());
            PointF pointF = new PointF(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
            float f = 36.0f;
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            if (this.mVelocityTracker.getYVelocity() < ((float) deviceProfile.flingToDeleteThresholdVelocity)) {
                f = getAngleBetweenVectors(pointF, new PointF(0.0f, -1.0f));
            } else if (this.mLauncher.getDeviceProfile().isVerticalBarLayout() && this.mVelocityTracker.getXVelocity() < ((float) deviceProfile.flingToDeleteThresholdVelocity)) {
                f = getAngleBetweenVectors(pointF, new PointF(-1.0f, 0.0f));
            }
            if (((double) f) <= Math.toRadians(35.0d)) {
                return pointF;
            }
        }
        return null;
    }

    private float getAngleBetweenVectors(PointF pointF, PointF pointF2) {
        return (float) Math.acos((double) (((pointF.x * pointF2.x) + (pointF.y * pointF2.y)) / (pointF.length() * pointF2.length())));
    }
}
