package com.android.quickstep;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.RemoteAnimationTarget;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.Executors;
import com.android.quickstep.RecentsAnimationCallbacks;
import com.android.quickstep.TopTaskTracker;
import com.android.quickstep.views.RecentsView;
import com.android.systemui.shared.recents.model.ThumbnailData;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.ActivityOptionsCompat;
import com.android.systemui.shared.system.RecentsAnimationListener;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.RemoteTransitionCompat;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class TaskAnimationManager implements RecentsAnimationCallbacks.RecentsAnimationListener {
    public static final boolean ENABLE_SHELL_TRANSITIONS;
    public static final boolean SHELL_TRANSITIONS_ROTATION;
    /* access modifiers changed from: private */
    public RecentsAnimationCallbacks mCallbacks;
    /* access modifiers changed from: private */
    public RecentsAnimationController mController;
    private Context mCtx;
    /* access modifiers changed from: private */
    public RemoteAnimationTargetCompat mLastAppearedTaskTarget;
    /* access modifiers changed from: private */
    public GestureState mLastGestureState;
    private Runnable mLiveTileCleanUpHandler;
    /* access modifiers changed from: private */
    public final TaskStackChangeListener mLiveTileRestartListener = new TaskStackChangeListener() {
        public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo runningTaskInfo, boolean z, boolean z2, boolean z3) {
            RecentsView recentsView;
            if (TaskAnimationManager.this.mLastGestureState == null) {
                TaskStackChangeListeners.getInstance().unregisterTaskStackListener(TaskAnimationManager.this.mLiveTileRestartListener);
                return;
            }
            BaseActivityInterface activityInterface = TaskAnimationManager.this.mLastGestureState.getActivityInterface();
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && activityInterface.isInLiveTileMode() && activityInterface.getCreatedActivity() != null && (recentsView = (RecentsView) activityInterface.getCreatedActivity().getOverviewPanel()) != null) {
                recentsView.launchSideTaskInLiveTileModeForRestartedApp(runningTaskInfo.taskId);
                TaskStackChangeListeners.getInstance().unregisterTaskStackListener(TaskAnimationManager.this.mLiveTileRestartListener);
            }
        }
    };
    /* access modifiers changed from: private */
    public RecentsAnimationTargets mTargets;

    public void dump() {
    }

    static {
        boolean z = false;
        boolean z2 = SystemProperties.getBoolean("persist.wm.debug.shell_transit", false);
        ENABLE_SHELL_TRANSITIONS = z2;
        if (z2 && SystemProperties.getBoolean("persist.wm.debug.shell_transit_rotate", false)) {
            z = true;
        }
        SHELL_TRANSITIONS_ROTATION = z;
    }

    TaskAnimationManager(Context context) {
        this.mCtx = context;
    }

    public void preloadRecentsAnimation(Intent intent) {
        Executors.UI_HELPER_EXECUTOR.execute(new Runnable(intent) {
            public final /* synthetic */ Intent f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                ActivityManagerWrapper.getInstance().startRecentsActivity(this.f$0, 0, (RecentsAnimationListener) null, (Consumer<Boolean>) null, (Handler) null);
            }
        });
    }

    public RecentsAnimationCallbacks startRecentsAnimation(GestureState gestureState, Intent intent, RecentsAnimationCallbacks.RecentsAnimationListener recentsAnimationListener) {
        if (this.mController != null) {
            Log.e("TaskAnimationManager", "New recents animation started before old animation completed", new Exception());
        }
        boolean z = false;
        finishRunningRecentsAnimation(false);
        if (this.mCallbacks != null) {
            cleanUpRecentsAnimation();
        }
        final BaseActivityInterface activityInterface = gestureState.getActivityInterface();
        this.mLastGestureState = gestureState;
        RecentsAnimationCallbacks recentsAnimationCallbacks = new RecentsAnimationCallbacks(SystemUiProxy.INSTANCE.lambda$get$1$MainThreadInitializedObject(this.mCtx), activityInterface.allowMinimizeSplitScreen());
        this.mCallbacks = recentsAnimationCallbacks;
        recentsAnimationCallbacks.addListener(new RecentsAnimationCallbacks.RecentsAnimationListener() {
            public void onRecentsAnimationStart(RecentsAnimationController recentsAnimationController, RecentsAnimationTargets recentsAnimationTargets) {
                if (TaskAnimationManager.this.mCallbacks != null) {
                    RecentsAnimationController unused = TaskAnimationManager.this.mController = recentsAnimationController;
                    RecentsAnimationTargets unused2 = TaskAnimationManager.this.mTargets = recentsAnimationTargets;
                    TaskAnimationManager taskAnimationManager = TaskAnimationManager.this;
                    RemoteAnimationTargetCompat unused3 = taskAnimationManager.mLastAppearedTaskTarget = taskAnimationManager.mTargets.findTask(TaskAnimationManager.this.mLastGestureState.getRunningTaskId());
                    TaskAnimationManager.this.mLastGestureState.updateLastAppearedTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                }
            }

            public void onRecentsAnimationCanceled(HashMap<Integer, ThumbnailData> hashMap) {
                TaskAnimationManager.this.cleanUpRecentsAnimation();
            }

            public void onRecentsAnimationFinished(RecentsAnimationController recentsAnimationController) {
                TaskAnimationManager.this.cleanUpRecentsAnimation();
            }

            public void onTasksAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr) {
                RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargetCompatArr[0];
                BaseActivityInterface activityInterface = TaskAnimationManager.this.mLastGestureState.getActivityInterface();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (RemoteAnimationTargetCompat remoteAnimationTargetCompat2 : remoteAnimationTargetCompatArr) {
                    if (remoteAnimationTargetCompat2.activityType != 2) {
                        arrayList.add(remoteAnimationTargetCompat2);
                    } else {
                        arrayList2.add(remoteAnimationTargetCompat2);
                    }
                }
                RemoteAnimationTarget[] remoteAnimationTargetArr = (RemoteAnimationTarget[]) arrayList.stream().map($$Lambda$TaskAnimationManager$2$tAyCrZ2lAkHHAGjbVm8XNYk3trc.INSTANCE).toArray($$Lambda$TaskAnimationManager$2$sGYHdOrKRreTwhv63kiu4r6jabU.INSTANCE);
                if (((RemoteAnimationTarget[]) arrayList2.stream().map($$Lambda$TaskAnimationManager$2$tAyCrZ2lAkHHAGjbVm8XNYk3trc.INSTANCE).toArray($$Lambda$TaskAnimationManager$2$9wlN3mClTwfTe3cJw4YATWwgv1o.INSTANCE)).length <= 0 || !(activityInterface.getCreatedActivity() instanceof RecentsActivity)) {
                    RemoteAnimationTarget[] onStartingSplitLegacy = SystemUiProxy.INSTANCE.getNoCreate().onStartingSplitLegacy(remoteAnimationTargetArr);
                    if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && activityInterface.isInLiveTileMode() && activityInterface.getCreatedActivity() != null) {
                        RecentsView recentsView = (RecentsView) activityInterface.getCreatedActivity().getOverviewPanel();
                        if (recentsView != null) {
                            recentsView.launchSideTaskInLiveTileMode(remoteAnimationTargetCompat.taskId, remoteAnimationTargetCompatArr, new RemoteAnimationTargetCompat[0], RemoteAnimationTargetCompat.wrap(onStartingSplitLegacy));
                            return;
                        }
                    } else if (onStartingSplitLegacy != null && onStartingSplitLegacy.length > 0) {
                        TaskViewUtils.createSplitAuxiliarySurfacesAnimator(RemoteAnimationTargetCompat.wrap(onStartingSplitLegacy), true, $$Lambda$TaskAnimationManager$2$rWJAHar3i6Jd2w_Wr_FgB_k3zt0.INSTANCE);
                    }
                    if (TaskAnimationManager.this.mController == null) {
                        return;
                    }
                    if (TaskAnimationManager.this.mLastAppearedTaskTarget == null || remoteAnimationTargetCompat.taskId != TaskAnimationManager.this.mLastAppearedTaskTarget.taskId) {
                        if (TaskAnimationManager.this.mLastAppearedTaskTarget != null) {
                            TaskAnimationManager.this.mController.removeTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                        }
                        RemoteAnimationTargetCompat unused = TaskAnimationManager.this.mLastAppearedTaskTarget = remoteAnimationTargetCompat;
                        TaskAnimationManager.this.mLastGestureState.updateLastAppearedTaskTarget(TaskAnimationManager.this.mLastAppearedTaskTarget);
                        return;
                    }
                    return;
                }
                ((RecentsActivity) activityInterface.getCreatedActivity()).startHome();
            }

            static /* synthetic */ RemoteAnimationTarget[] lambda$onTasksAppeared$0(int i) {
                return new RemoteAnimationTarget[i];
            }

            static /* synthetic */ RemoteAnimationTarget[] lambda$onTasksAppeared$1(int i) {
                return new RemoteAnimationTarget[i];
            }

            public boolean onSwitchToScreenshot(Runnable runnable) {
                if (!FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() || !activityInterface.isInLiveTileMode() || activityInterface.getCreatedActivity() == null) {
                    runnable.run();
                    return true;
                }
                RecentsView recentsView = (RecentsView) activityInterface.getCreatedActivity().getOverviewPanel();
                if (recentsView != null) {
                    recentsView.switchToScreenshot(runnable);
                    return true;
                }
                runnable.run();
                return true;
            }
        });
        long swipeUpStartTimeMs = gestureState.getSwipeUpStartTimeMs();
        this.mCallbacks.addListener(gestureState);
        this.mCallbacks.addListener(recentsAnimationListener);
        if (ENABLE_SHELL_TRANSITIONS) {
            RecentsAnimationCallbacks recentsAnimationCallbacks2 = this.mCallbacks;
            RecentsAnimationController recentsAnimationController = this.mController;
            ActivityOptions makeRemoteTransition = ActivityOptionsCompat.makeRemoteTransition(new RemoteTransitionCompat((RecentsAnimationListener) recentsAnimationCallbacks2, recentsAnimationController != null ? recentsAnimationController.getController() : null, this.mCtx.getIApplicationThread()));
            TopTaskTracker.CachedTaskInfo runningTask = gestureState.getRunningTask();
            if (runningTask != null && runningTask.isHomeTask()) {
                z = true;
            }
            if (!z) {
                makeRemoteTransition.setTransientLaunch();
            }
            makeRemoteTransition.setSourceInfo(4, swipeUpStartTimeMs);
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(intent, makeRemoteTransition) {
                public final /* synthetic */ Intent f$1;
                public final /* synthetic */ ActivityOptions f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TaskAnimationManager.this.lambda$startRecentsAnimation$1$TaskAnimationManager(this.f$1, this.f$2);
                }
            });
        } else {
            Executors.UI_HELPER_EXECUTOR.execute(new Runnable(intent, swipeUpStartTimeMs) {
                public final /* synthetic */ Intent f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    TaskAnimationManager.this.lambda$startRecentsAnimation$2$TaskAnimationManager(this.f$1, this.f$2);
                }
            });
        }
        gestureState.setState(GestureState.STATE_RECENTS_ANIMATION_INITIALIZED);
        return this.mCallbacks;
    }

    public /* synthetic */ void lambda$startRecentsAnimation$1$TaskAnimationManager(Intent intent, ActivityOptions activityOptions) {
        this.mCtx.startActivity(intent, activityOptions.toBundle());
    }

    public /* synthetic */ void lambda$startRecentsAnimation$2$TaskAnimationManager(Intent intent, long j) {
        ActivityManagerWrapper.getInstance().startRecentsActivity(intent, j, this.mCallbacks, (Consumer<Boolean>) null, (Handler) null);
    }

    public RecentsAnimationCallbacks continueRecentsAnimation(GestureState gestureState) {
        this.mCallbacks.removeListener(this.mLastGestureState);
        this.mLastGestureState = gestureState;
        this.mCallbacks.addListener(gestureState);
        gestureState.setState(GestureState.STATE_RECENTS_ANIMATION_INITIALIZED | GestureState.STATE_RECENTS_ANIMATION_STARTED);
        gestureState.updateLastAppearedTaskTarget(this.mLastAppearedTaskTarget);
        return this.mCallbacks;
    }

    public void endLiveTile() {
        RecentsView recentsView;
        GestureState gestureState = this.mLastGestureState;
        if (gestureState != null) {
            BaseActivityInterface activityInterface = gestureState.getActivityInterface();
            if (FeatureFlags.ENABLE_QUICKSTEP_LIVE_TILE.get() && activityInterface.isInLiveTileMode() && activityInterface.getCreatedActivity() != null && (recentsView = (RecentsView) activityInterface.getCreatedActivity().getOverviewPanel()) != null) {
                recentsView.switchToScreenshot((HashMap<Integer, ThumbnailData>) null, new Runnable() {
                    public final void run() {
                        RecentsView.this.finishRecentsAnimation(true, false, (Runnable) null);
                    }
                });
            }
        }
    }

    public void setLiveTileCleanUpHandler(Runnable runnable) {
        this.mLiveTileCleanUpHandler = runnable;
    }

    public void enableLiveTileRestartListener() {
        TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mLiveTileRestartListener);
    }

    public void finishRunningRecentsAnimation(boolean z) {
        Runnable runnable;
        if (this.mController != null) {
            this.mCallbacks.notifyAnimationCanceled();
            Handler handler = Executors.MAIN_EXECUTOR.getHandler();
            if (z) {
                RecentsAnimationController recentsAnimationController = this.mController;
                Objects.requireNonNull(recentsAnimationController);
                runnable = new Runnable() {
                    public final void run() {
                        RecentsAnimationController.this.finishAnimationToHome();
                    }
                };
            } else {
                RecentsAnimationController recentsAnimationController2 = this.mController;
                Objects.requireNonNull(recentsAnimationController2);
                runnable = new Runnable() {
                    public final void run() {
                        RecentsAnimationController.this.finishAnimationToApp();
                    }
                };
            }
            Utilities.postAsyncCallback(handler, runnable);
            cleanUpRecentsAnimation();
        }
    }

    public void notifyRecentsAnimationState(RecentsAnimationCallbacks.RecentsAnimationListener recentsAnimationListener) {
        if (isRecentsAnimationRunning()) {
            recentsAnimationListener.onRecentsAnimationStart(this.mController, this.mTargets);
        }
    }

    public boolean isRecentsAnimationRunning() {
        return this.mController != null;
    }

    /* access modifiers changed from: private */
    public void cleanUpRecentsAnimation() {
        Runnable runnable = this.mLiveTileCleanUpHandler;
        if (runnable != null) {
            runnable.run();
            this.mLiveTileCleanUpHandler = null;
        }
        TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mLiveTileRestartListener);
        RecentsAnimationTargets recentsAnimationTargets = this.mTargets;
        if (recentsAnimationTargets != null) {
            recentsAnimationTargets.release();
        }
        RecentsAnimationCallbacks recentsAnimationCallbacks = this.mCallbacks;
        if (recentsAnimationCallbacks != null) {
            recentsAnimationCallbacks.removeAllListeners();
        }
        this.mController = null;
        this.mCallbacks = null;
        this.mTargets = null;
        this.mLastGestureState = null;
        this.mLastAppearedTaskTarget = null;
    }

    public RecentsAnimationCallbacks getCurrentCallbacks() {
        return this.mCallbacks;
    }
}
