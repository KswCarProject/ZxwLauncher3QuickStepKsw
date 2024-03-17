package com.android.launcher3.taskbar.allapps;

import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.LauncherState;
import com.android.launcher3.appprediction.AppsDividerView;
import com.android.launcher3.appprediction.PredictionRowView;
import com.android.launcher3.taskbar.TaskbarDragController;
import com.android.launcher3.taskbar.TaskbarStashController;
import com.android.launcher3.util.OnboardingPrefs;
import com.android.launcher3.views.AbstractSlideInView;
import java.util.Objects;

final class TaskbarAllAppsViewController {
    private final TaskbarAllAppsContainerView mAppsView;
    private final TaskbarAllAppsContext mContext;
    private final TaskbarAllAppsSlideInView mSlideInView;
    private final TaskbarStashController mTaskbarStashController;

    TaskbarAllAppsViewController(TaskbarAllAppsContext taskbarAllAppsContext, TaskbarAllAppsSlideInView taskbarAllAppsSlideInView, TaskbarAllAppsController taskbarAllAppsController, TaskbarStashController taskbarStashController) {
        this.mContext = taskbarAllAppsContext;
        this.mSlideInView = taskbarAllAppsSlideInView;
        this.mAppsView = taskbarAllAppsSlideInView.getAppsView();
        this.mTaskbarStashController = taskbarStashController;
        setUpIconLongClick();
        setUpAppDivider();
        setUpTaskbarStashing();
        Objects.requireNonNull(taskbarAllAppsController);
        taskbarAllAppsSlideInView.addOnCloseListener(new AbstractSlideInView.OnCloseListener() {
            public final void onSlideInViewClosed() {
                TaskbarAllAppsController.this.maybeCloseWindow();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void show(boolean z) {
        this.mSlideInView.show(z);
    }

    /* access modifiers changed from: package-private */
    public void close(boolean z) {
        this.mSlideInView.close(z);
    }

    private void setUpIconLongClick() {
        TaskbarAllAppsContainerView taskbarAllAppsContainerView = this.mAppsView;
        TaskbarDragController dragController = this.mContext.getDragController();
        Objects.requireNonNull(dragController);
        taskbarAllAppsContainerView.setOnIconLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return TaskbarDragController.this.startDragOnLongClick(view);
            }
        });
        TaskbarDragController dragController2 = this.mContext.getDragController();
        Objects.requireNonNull(dragController2);
        ((PredictionRowView) this.mAppsView.getFloatingHeaderView().findFixedRowByType(PredictionRowView.class)).setOnIconLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return TaskbarDragController.this.startDragOnLongClick(view);
            }
        });
    }

    private void setUpAppDivider() {
        ((AppsDividerView) this.mAppsView.getFloatingHeaderView().findFixedRowByType(AppsDividerView.class)).setShowAllAppsLabel(!this.mContext.getOnboardingPrefs().hasReachedMaxCount(OnboardingPrefs.ALL_APPS_VISITED_COUNT));
        this.mContext.getOnboardingPrefs().incrementEventCount(OnboardingPrefs.ALL_APPS_VISITED_COUNT);
    }

    private void setUpTaskbarStashing() {
        this.mTaskbarStashController.updateStateForFlag(128, true);
        this.mTaskbarStashController.applyState((long) LauncherState.ALL_APPS.getTransitionDuration(this.mContext, true));
        this.mSlideInView.setOnCloseBeginListener(new AbstractSlideInView.OnCloseListener() {
            public final void onSlideInViewClosed() {
                TaskbarAllAppsViewController.this.lambda$setUpTaskbarStashing$0$TaskbarAllAppsViewController();
            }
        });
    }

    public /* synthetic */ void lambda$setUpTaskbarStashing$0$TaskbarAllAppsViewController() {
        AbstractFloatingView.closeOpenContainer(this.mContext, 2);
        TaskbarAllAppsSlideInView taskbarAllAppsSlideInView = this.mSlideInView;
        TaskbarStashController taskbarStashController = this.mTaskbarStashController;
        Objects.requireNonNull(taskbarStashController);
        taskbarAllAppsSlideInView.post(new Runnable() {
            public final void run() {
                TaskbarStashController.this.maybeResetStashedInAppAllApps();
            }
        });
    }
}
