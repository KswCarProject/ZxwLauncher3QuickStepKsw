package com.android.launcher3.uioverrides.touchcontrollers;

import android.view.MotionEvent;
import android.view.animation.Interpolator;
import com.android.launcher3.Launcher;
import com.android.launcher3.anim.PendingAnimation;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;

public final class PortraitOverviewStateTouchHelper {
    Launcher mLauncher;
    RecentsView mRecentsView;

    public PortraitOverviewStateTouchHelper(Launcher launcher) {
        this.mLauncher = launcher;
        this.mRecentsView = (RecentsView) launcher.getOverviewPanel();
    }

    /* access modifiers changed from: package-private */
    public boolean canInterceptTouch(MotionEvent motionEvent) {
        if (this.mRecentsView.getTaskViewCount() > 0) {
            return motionEvent.getY() >= ((float) this.mRecentsView.getTaskViewAt(0).getBottom());
        }
        return PortraitStatesTouchController.isTouchOverHotseat(this.mLauncher, motionEvent);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldSwipeDownReturnToApp() {
        return this.mRecentsView.getNextPageTaskView() != null && this.mRecentsView.shouldSwipeDownLaunchApp();
    }

    /* access modifiers changed from: package-private */
    public PendingAnimation createSwipeDownToTaskAppAnimation(long j, Interpolator interpolator) {
        RecentsView recentsView = this.mRecentsView;
        recentsView.setCurrentPage(recentsView.getDestinationPage());
        TaskView currentPageTaskView = this.mRecentsView.getCurrentPageTaskView();
        if (currentPageTaskView != null) {
            return this.mRecentsView.createTaskLaunchAnimation(currentPageTaskView, j, interpolator);
        }
        throw new IllegalStateException("There is no task view to animate to.");
    }
}
