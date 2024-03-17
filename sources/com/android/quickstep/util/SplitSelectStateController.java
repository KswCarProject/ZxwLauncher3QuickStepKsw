package com.android.quickstep.util;

import android.app.ActivityOptions;
import android.app.ActivityThread;
import android.app.IApplicationThread;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
import android.window.TransitionInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.statehandlers.DepthController;
import com.android.launcher3.statemanager.StateManager;
import com.android.launcher3.util.Executors;
import com.android.quickstep.SystemUiProxy;
import com.android.quickstep.TaskAnimationManager;
import com.android.quickstep.TaskViewUtils;
import com.android.quickstep.util.SplitSelectStateController;
import com.android.quickstep.views.GroupedTaskView;
import com.android.quickstep.views.TaskView;
import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.RemoteAnimationAdapterCompat;
import com.android.systemui.shared.system.RemoteAnimationRunnerCompat;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.RemoteTransitionRunner;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class SplitSelectStateController {
    private final Context mContext;
    /* access modifiers changed from: private */
    public final DepthController mDepthController;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    private int mInitialTaskId = -1;
    private Intent mInitialTaskIntent;
    /* access modifiers changed from: private */
    public GroupedTaskView mLaunchingTaskView;
    /* access modifiers changed from: private */
    public boolean mRecentsAnimationRunning;
    private int mSecondTaskId = -1;
    private String mSecondTaskPackageName;
    private int mStagePosition;
    /* access modifiers changed from: private */
    public final StateManager mStateManager;
    private final SystemUiProxy mSystemUiProxy;

    public SplitSelectStateController(Context context, Handler handler, StateManager stateManager, DepthController depthController) {
        this.mContext = context;
        this.mHandler = handler;
        this.mSystemUiProxy = SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(context);
        this.mStateManager = stateManager;
        this.mDepthController = depthController;
    }

    public void setInitialTaskSelect(int i, int i2) {
        this.mInitialTaskId = i;
        this.mStagePosition = i2;
        this.mInitialTaskIntent = null;
    }

    public void setInitialTaskSelect(Intent intent, int i) {
        this.mInitialTaskIntent = intent;
        this.mStagePosition = i;
        this.mInitialTaskId = -1;
    }

    public void launchSplitTasks(Consumer<Boolean> consumer) {
        Intent intent;
        PendingIntent pendingIntent = null;
        if (this.mInitialTaskIntent != null) {
            Intent intent2 = new Intent();
            if (TextUtils.equals(this.mInitialTaskIntent.getComponent().getPackageName(), this.mSecondTaskPackageName)) {
                intent2.addFlags(134217728);
            }
            intent = intent2;
        } else {
            intent = null;
        }
        Intent intent3 = this.mInitialTaskIntent;
        if (intent3 != null) {
            pendingIntent = PendingIntent.getActivity(this.mContext, 0, intent3, QuickStepContract.SYSUI_STATE_VOICE_INTERACTION_WINDOW_SHOWING);
        }
        launchTasks(this.mInitialTaskId, pendingIntent, intent, this.mSecondTaskId, this.mStagePosition, consumer, false, 0.5f);
    }

    public void setSecondTask(Task task) {
        this.mSecondTaskId = task.key.id;
        if (this.mInitialTaskIntent != null) {
            this.mSecondTaskPackageName = task.getTopComponent().getPackageName();
        }
    }

    public void launchTasks(GroupedTaskView groupedTaskView, Consumer<Boolean> consumer, boolean z) {
        this.mLaunchingTaskView = groupedTaskView;
        TaskView.TaskIdAttributeContainer[] taskIdAttributeContainers = groupedTaskView.getTaskIdAttributeContainers();
        launchTasks(taskIdAttributeContainers[0].getTask().key.id, taskIdAttributeContainers[1].getTask().key.id, taskIdAttributeContainers[0].getStagePosition(), consumer, z, groupedTaskView.getSplitRatio());
    }

    public void launchTasks(int i, int i2, int i3, Consumer<Boolean> consumer, boolean z, float f) {
        launchTasks(i, (PendingIntent) null, (Intent) null, i2, i3, consumer, z, f);
    }

    public void launchTasks(int i, PendingIntent pendingIntent, Intent intent, int i2, int i3, Consumer<Boolean> consumer, boolean z, float f) {
        int[] iArr = i3 == 0 ? new int[]{i, i2} : new int[]{i2, i};
        if (TaskAnimationManager.ENABLE_SHELL_TRANSITIONS) {
            this.mSystemUiProxy.startTasks(iArr[0], (Bundle) null, iArr[1], (Bundle) null, 1, f, new RemoteTransitionCompat((RemoteTransitionRunner) new RemoteSplitLaunchTransitionRunner(i, pendingIntent, i2, consumer), (Executor) Executors.MAIN_EXECUTOR, (IApplicationThread) ActivityThread.currentActivityThread().getApplicationThread()));
            return;
        }
        RemoteAnimationAdapter remoteAnimationAdapter = new RemoteAnimationAdapter(RemoteAnimationAdapterCompat.wrapRemoteAnimationRunner(new RemoteSplitLaunchAnimationRunner(i, pendingIntent, i2, consumer)), 300, 150, ActivityThread.currentActivityThread().getApplicationThread());
        ActivityOptions makeBasic = ActivityOptions.makeBasic();
        if (z) {
            makeBasic.setFreezeRecentTasksReordering();
        }
        if (pendingIntent == null) {
            this.mSystemUiProxy.startTasksWithLegacyTransition(iArr[0], makeBasic.toBundle(), iArr[1], (Bundle) null, 1, f, remoteAnimationAdapter);
        } else {
            this.mSystemUiProxy.startIntentAndTaskWithLegacyTransition(pendingIntent, intent, i2, makeBasic.toBundle(), (Bundle) null, i3, f, remoteAnimationAdapter);
        }
    }

    public int getActiveSplitStagePosition() {
        return this.mStagePosition;
    }

    public void setRecentsAnimationRunning(boolean z) {
        this.mRecentsAnimationRunning = z;
    }

    private class RemoteSplitLaunchTransitionRunner implements RemoteTransitionRunner {
        private final int mInitialTaskId;
        private final PendingIntent mInitialTaskPendingIntent;
        private final int mSecondTaskId;
        private final Consumer<Boolean> mSuccessCallback;

        RemoteSplitLaunchTransitionRunner(int i, PendingIntent pendingIntent, int i2, Consumer<Boolean> consumer) {
            this.mInitialTaskId = i;
            this.mInitialTaskPendingIntent = pendingIntent;
            this.mSecondTaskId = i2;
            this.mSuccessCallback = consumer;
        }

        public void startAnimation(IBinder iBinder, TransitionInfo transitionInfo, SurfaceControl.Transaction transaction, Runnable runnable) {
            TaskViewUtils.composeRecentsSplitLaunchAnimator(this.mInitialTaskId, this.mInitialTaskPendingIntent, this.mSecondTaskId, transitionInfo, transaction, new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SplitSelectStateController.RemoteSplitLaunchTransitionRunner.this.lambda$startAnimation$0$SplitSelectStateController$RemoteSplitLaunchTransitionRunner(this.f$1);
                }
            });
            SplitSelectStateController.this.resetState();
        }

        public /* synthetic */ void lambda$startAnimation$0$SplitSelectStateController$RemoteSplitLaunchTransitionRunner(Runnable runnable) {
            runnable.run();
            Consumer<Boolean> consumer = this.mSuccessCallback;
            if (consumer != null) {
                consumer.accept(true);
            }
        }
    }

    private class RemoteSplitLaunchAnimationRunner implements RemoteAnimationRunnerCompat {
        private final int mInitialTaskId;
        private final PendingIntent mInitialTaskPendingIntent;
        private final int mSecondTaskId;
        private final Consumer<Boolean> mSuccessCallback;

        RemoteSplitLaunchAnimationRunner(int i, PendingIntent pendingIntent, int i2, Consumer<Boolean> consumer) {
            this.mInitialTaskId = i;
            this.mInitialTaskPendingIntent = pendingIntent;
            this.mSecondTaskId = i2;
            this.mSuccessCallback = consumer;
        }

        public void onAnimationStart(int i, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Runnable runnable) {
            Utilities.postAsyncCallback(SplitSelectStateController.this.mHandler, new Runnable(remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, runnable) {
                public final /* synthetic */ RemoteAnimationTargetCompat[] f$1;
                public final /* synthetic */ RemoteAnimationTargetCompat[] f$2;
                public final /* synthetic */ RemoteAnimationTargetCompat[] f$3;
                public final /* synthetic */ Runnable f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    SplitSelectStateController.RemoteSplitLaunchAnimationRunner.this.lambda$onAnimationStart$1$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationStart$1$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr3, Runnable runnable) {
            TaskViewUtils.composeRecentsSplitLaunchAnimatorLegacy(SplitSelectStateController.this.mLaunchingTaskView, this.mInitialTaskId, this.mInitialTaskPendingIntent, this.mSecondTaskId, remoteAnimationTargetCompatArr, remoteAnimationTargetCompatArr2, remoteAnimationTargetCompatArr3, SplitSelectStateController.this.mStateManager, SplitSelectStateController.this.mDepthController, new Runnable(runnable) {
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SplitSelectStateController.RemoteSplitLaunchAnimationRunner.this.lambda$onAnimationStart$0$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationStart$0$SplitSelectStateController$RemoteSplitLaunchAnimationRunner(Runnable runnable) {
            runnable.run();
            Consumer<Boolean> consumer = this.mSuccessCallback;
            if (consumer != null) {
                consumer.accept(true);
            }
            SplitSelectStateController.this.resetState();
        }

        public void onAnimationCancelled() {
            Utilities.postAsyncCallback(SplitSelectStateController.this.mHandler, new Runnable() {
                public final void run() {
                    SplitSelectStateController.RemoteSplitLaunchAnimationRunner.this.lambda$onAnimationCancelled$2$SplitSelectStateController$RemoteSplitLaunchAnimationRunner();
                }
            });
        }

        public /* synthetic */ void lambda$onAnimationCancelled$2$SplitSelectStateController$RemoteSplitLaunchAnimationRunner() {
            Consumer<Boolean> consumer = this.mSuccessCallback;
            if (consumer != null) {
                consumer.accept(Boolean.valueOf(SplitSelectStateController.this.mRecentsAnimationRunning));
            }
            SplitSelectStateController.this.resetState();
        }
    }

    public void resetState() {
        this.mInitialTaskId = -1;
        this.mInitialTaskIntent = null;
        this.mSecondTaskId = -1;
        this.mStagePosition = -1;
        this.mRecentsAnimationRunning = false;
        this.mLaunchingTaskView = null;
    }

    public boolean isSplitSelectActive() {
        return isInitialTaskIntentSet() && this.mSecondTaskId == -1;
    }

    public boolean isBothSplitAppsConfirmed() {
        return isInitialTaskIntentSet() && this.mSecondTaskId != -1;
    }

    private boolean isInitialTaskIntentSet() {
        return (this.mInitialTaskId == -1 && this.mInitialTaskIntent == null) ? false : true;
    }
}
