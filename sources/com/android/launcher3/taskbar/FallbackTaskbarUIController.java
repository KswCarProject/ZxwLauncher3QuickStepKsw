package com.android.launcher3.taskbar;

import android.animation.Animator;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.taskbar.FallbackTaskbarUIController;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.fallback.RecentsState;
import com.android.quickstep.views.RecentsView;

public class FallbackTaskbarUIController extends TaskbarUIController {
    /* access modifiers changed from: private */
    public final RecentsActivity mRecentsActivity;
    private final StateManager.StateListener<RecentsState> mStateListener = new StateManager.StateListener<RecentsState>() {
        public void onStateTransitionStart(RecentsState recentsState) {
            FallbackTaskbarUIController.this.animateToRecentsState(recentsState);
            ((RecentsView) FallbackTaskbarUIController.this.mRecentsActivity.getOverviewPanel()).setTaskLaunchListener(recentsState == RecentsState.DEFAULT ? new RecentsView.TaskLaunchListener() {
                public final void onTaskLaunched() {
                    FallbackTaskbarUIController.AnonymousClass1.this.lambda$onStateTransitionStart$0$FallbackTaskbarUIController$1();
                }
            } : null);
        }

        public /* synthetic */ void lambda$onStateTransitionStart$0$FallbackTaskbarUIController$1() {
            FallbackTaskbarUIController.this.animateToRecentsState(RecentsState.BACKGROUND_APP);
        }
    };

    public FallbackTaskbarUIController(RecentsActivity recentsActivity) {
        this.mRecentsActivity = recentsActivity;
    }

    /* access modifiers changed from: protected */
    public void init(TaskbarControllers taskbarControllers) {
        super.init(taskbarControllers);
        this.mRecentsActivity.setTaskbarUIController(this);
        this.mRecentsActivity.getStateManager().addStateListener(this.mStateListener);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mRecentsActivity.setTaskbarUIController((FallbackTaskbarUIController) null);
        this.mRecentsActivity.getStateManager().removeStateListener(this.mStateListener);
    }

    public Animator createAnimToRecentsState(RecentsState recentsState, long j) {
        boolean hasOverviewActions = recentsState.hasOverviewActions();
        TaskbarStashController taskbarStashController = this.mControllers.taskbarStashController;
        taskbarStashController.updateStateForFlag(64, hasOverviewActions);
        taskbarStashController.updateStateForFlag(1, !hasOverviewActions);
        return taskbarStashController.applyStateWithoutStart(j);
    }

    /* access modifiers changed from: private */
    public void animateToRecentsState(RecentsState recentsState) {
        Animator createAnimToRecentsState = createAnimToRecentsState(recentsState, 300);
        if (createAnimToRecentsState != null) {
            createAnimToRecentsState.start();
        }
    }
}
