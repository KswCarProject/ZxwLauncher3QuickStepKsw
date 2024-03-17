package com.android.quickstep.fallback;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.PendingAnimation;
import com.android.launcher3.popup.QuickstepSystemShortcut;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.quickstep.FallbackActivityInterface;
import com.android.quickstep.GestureState;
import com.android.quickstep.RecentsActivity;
import com.android.quickstep.RotationTouchHelper;
import com.android.quickstep.util.GroupTask;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.util.TaskViewSimulator;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.RecentsView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class FallbackRecentsView extends RecentsView<RecentsActivity, RecentsState> implements StateManager.StateListener<RecentsState> {
    private Task mHomeTask;

    public FallbackRecentsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FallbackRecentsView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, FallbackActivityInterface.INSTANCE);
        ((RecentsActivity) this.mActivity).getStateManager().addStateListener(this);
    }

    public void init(OverviewActionsView overviewActionsView, SplitSelectStateController splitSelectStateController, View view) {
        super.init(overviewActionsView, splitSelectStateController, view);
        setOverviewStateEnabled(true);
        setOverlayEnabled(true);
    }

    public void startHome() {
        ((RecentsActivity) this.mActivity).startHome();
        AbstractFloatingView.closeAllOpenViews(this.mActivity, ((RecentsActivity) this.mActivity).isStarted());
    }

    public void onGestureAnimationStartOnHome(Task[] taskArr, RotationTouchHelper rotationTouchHelper) {
        this.mHomeTask = taskArr.length > 0 ? taskArr[0] : null;
        onGestureAnimationStart(taskArr, rotationTouchHelper);
    }

    public void onPrepareGestureEndAnimation(AnimatorSet animatorSet, GestureState.GestureEndTarget gestureEndTarget, TaskViewSimulator[] taskViewSimulatorArr) {
        TaskView taskViewByTaskId;
        super.onPrepareGestureEndAnimation(animatorSet, gestureEndTarget, taskViewSimulatorArr);
        if (this.mHomeTask != null && gestureEndTarget == GestureState.GestureEndTarget.RECENTS && animatorSet != null && (taskViewByTaskId = getTaskViewByTaskId(this.mHomeTask.key.id)) != null) {
            PendingAnimation createTaskDismissAnimation = createTaskDismissAnimation(taskViewByTaskId, true, false, 150, false);
            createTaskDismissAnimation.addEndListener(new Consumer() {
                public final void accept(Object obj) {
                    FallbackRecentsView.this.lambda$onPrepareGestureEndAnimation$0$FallbackRecentsView((Boolean) obj);
                }
            });
            AnimatorPlaybackController createPlaybackController = createTaskDismissAnimation.createPlaybackController();
            createPlaybackController.dispatchOnStart();
            animatorSet.play(createPlaybackController.getAnimationPlayer());
        }
    }

    public /* synthetic */ void lambda$onPrepareGestureEndAnimation$0$FallbackRecentsView(Boolean bool) {
        setCurrentTask(-1);
    }

    public void onGestureAnimationEnd() {
        if (this.mCurrentGestureEndTarget == GestureState.GestureEndTarget.HOME) {
            reset();
        }
        super.onGestureAnimationEnd();
    }

    public void setCurrentTask(int i) {
        super.setCurrentTask(i);
        int i2 = getTaskIdsForRunningTaskView()[0];
        Task task = this.mHomeTask;
        if (task != null && task.key.id != i2) {
            this.mHomeTask = null;
            setRunningTaskHidden(false);
        }
    }

    /* access modifiers changed from: protected */
    public TaskView getHomeTaskView() {
        Task task = this.mHomeTask;
        if (task != null) {
            return getTaskViewByTaskId(task.key.id);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean shouldAddStubTaskView(Task[] taskArr) {
        if (taskArr.length > 1) {
            return super.shouldAddStubTaskView(taskArr);
        }
        Task task = taskArr[0];
        Task task2 = this.mHomeTask;
        if (task2 == null || task == null || task2.key.id != task.key.id || getTaskViewCount() != 0 || !this.mLoadPlanEverApplied) {
            return super.shouldAddStubTaskView(taskArr);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void applyLoadPlan(ArrayList<GroupTask> arrayList) {
        boolean z = false;
        int i = getTaskIdsForRunningTaskView()[0];
        Task task = this.mHomeTask;
        if (task != null && task.key.id == i && !arrayList.isEmpty()) {
            Iterator<GroupTask> it = arrayList.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().containsTask(i)) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!z) {
                ArrayList<GroupTask> arrayList2 = new ArrayList<>(arrayList.size() + 1);
                arrayList2.addAll(arrayList);
                arrayList2.add(new GroupTask(this.mHomeTask, (Task) null, (SplitConfigurationOptions.StagedSplitBounds) null));
                arrayList = arrayList2;
            }
        }
        super.applyLoadPlan(arrayList);
    }

    public void setRunningTaskHidden(boolean z) {
        if (this.mHomeTask != null) {
            z = true;
        }
        super.setRunningTaskHidden(z);
    }

    public void setModalStateEnabled(boolean z) {
        super.setModalStateEnabled(z);
        if (z) {
            ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.MODAL_TASK);
        } else if (((RecentsActivity) this.mActivity).isInState(RecentsState.MODAL_TASK)) {
            ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.DEFAULT);
            resetModalVisuals();
        }
    }

    public void initiateSplitSelect(TaskView taskView, int i) {
        super.initiateSplitSelect(taskView, i);
        ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.OVERVIEW_SPLIT_SELECT);
    }

    public void onStateTransitionStart(RecentsState recentsState) {
        setOverviewStateEnabled(true);
        setOverviewGridEnabled(recentsState.displayOverviewTasksAsGrid(((RecentsActivity) this.mActivity).getDeviceProfile()));
        setOverviewFullscreenEnabled(recentsState.isFullScreen());
        if (recentsState == RecentsState.MODAL_TASK) {
            setOverviewSelectEnabled(true);
        }
        Log.d(TestProtocol.BAD_STATE, "FRV onStateTransitionStart setFreezeVisibility=true, toState=" + recentsState);
        setFreezeViewVisibility(true);
    }

    public void onStateTransitionComplete(RecentsState recentsState) {
        if (recentsState == RecentsState.HOME) {
            reset();
        }
        boolean z = recentsState == RecentsState.DEFAULT || recentsState == RecentsState.MODAL_TASK;
        setOverlayEnabled(z);
        Log.d(TestProtocol.BAD_STATE, "FRV onStateTransitionComplete setFreezeVisibility=false, finalState=" + recentsState);
        setFreezeViewVisibility(false);
        if (recentsState != RecentsState.MODAL_TASK) {
            setOverviewSelectEnabled(false);
        }
        if (z) {
            runActionOnRemoteHandles($$Lambda$FallbackRecentsView$VOoB2oGpg3uCicJ9nhLqPLjqb8Y.INSTANCE);
        }
    }

    public void setOverviewStateEnabled(boolean z) {
        super.setOverviewStateEnabled(z);
        if (z) {
            setDisallowScrollToClearAll(!((RecentsActivity) this.mActivity).getStateManager().getState().hasClearAllButton());
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return super.onTouchEvent(motionEvent) || ((RecentsActivity) this.mActivity).getStateManager().getState().overviewUi();
    }

    public void initiateSplitSelect(QuickstepSystemShortcut.SplitSelectSource splitSelectSource) {
        super.initiateSplitSelect(splitSelectSource);
        ((RecentsActivity) this.mActivity).getStateManager().goToState(RecentsState.OVERVIEW_SPLIT_SELECT);
    }

    /* access modifiers changed from: protected */
    public boolean canLaunchFullscreenTask() {
        return !((RecentsActivity) this.mActivity).isInState(RecentsState.OVERVIEW_SPLIT_SELECT);
    }
}
